package it.eldasoft.sil.vigilanza.bl;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.db.datautils.DataColumn;
import it.eldasoft.gene.db.datautils.DataColumnContainer;
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.sil.vigilanza.beans.ContrattoType;
import it.eldasoft.sil.vigilanza.beans.LottoContrattoType;
import it.eldasoft.sil.vigilanza.beans.RichiestaSincronaIstanzaContrattoDocument;
import it.eldasoft.sil.vigilanza.commons.CostantiWSW9;
import it.eldasoft.sil.vigilanza.commons.WSVigilanzaException;
import it.eldasoft.sil.vigilanza.utils.UtilitySITAT;
import it.eldasoft.sil.vigilanza.ws.beans.IstanzaOggettoType;
import it.eldasoft.sil.vigilanza.ws.beans.LoginType;
import it.eldasoft.sil.vigilanza.ws.beans.ResponseLottoType;
import it.eldasoft.sil.vigilanza.ws.beans.ResponseType;
import it.eldasoft.utils.properties.ConfigManager;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlOptions;
import org.apache.xmlbeans.XmlValidationError;

/**
 * Classe per l'import dei dati del contratto per i diversi lotti della gara.
 * 
 * @author Luca.Giacomazzo
 */
public class IstanzaContrattoManager {
  
private static Logger logger = Logger.getLogger(IstanzaContrattoManager.class);
  
  private CredenzialiManager credenzialiManager;
  
  private SqlManager sqlManager;
 
  /**
   * @param credenzialiManager the credenzialiManager to set
   */
  public void setCredenzialiManager(CredenzialiManager credenzialiManager) {
    this.credenzialiManager = credenzialiManager;
  }
  
  /**
   * @param sqlManager the sqlManager to set
   */
  public void setSqlManager(SqlManager sqlManager) {
    this.sqlManager = sqlManager;
  }
  
  /**
   * Metodo per la gestione dell'inizio del contratto dei lotti della gara.
   * 
   * @param login LoginType
   * @param contratto IstanzaOggettoType
   * @return Ritorna l'oggetto ResponseType con l'esito dell'operazione
   * @throws XmlException 
   * @throws GestoreException 
   * @throws SQLException 
   * @throws Throwable
   */
  public ResponseType istanziaContratto(LoginType login, IstanzaOggettoType contratto) 
      throws XmlException, GestoreException, SQLException, Throwable {
    
    ResponseType result = new ResponseType();
    
    if (logger.isDebugEnabled()) {
      logger.debug("istanziaContratto: inizio metodo");
      
      logger.debug("XML : " + contratto.getOggettoXML());
    }
    
    // Codice fiscale della Stazione appaltante
    String codFiscaleStazAppaltante = contratto.getTestata().getCFEIN();
    
    boolean sovrascrivereDatiEsistenti = false;
    if (contratto.getTestata().getSOVRASCR() != null) {
      sovrascrivereDatiEsistenti = contratto.getTestata().getSOVRASCR().booleanValue(); 
    }
    
    // Verifica di login, password e determinazione della S.A.
    HashMap<String, Object> hm = this.credenzialiManager.verificaCredenziali(login, codFiscaleStazAppaltante);
    result = (ResponseType) hm.get("RESULT");
    
    if (result.isSuccess()) {
      Credenziale credenzialiUtente = (Credenziale) hm.get("USER");
      String xmlContratto = contratto.getOggettoXML();
      
      try {
        RichiestaSincronaIstanzaContrattoDocument istanzaContrattoDocument =
          RichiestaSincronaIstanzaContrattoDocument.Factory.parse(xmlContratto);

        boolean isMessaggioDiTest = 
          istanzaContrattoDocument.getRichiestaSincronaIstanzaContratto().isSetTest()
            && istanzaContrattoDocument.getRichiestaSincronaIstanzaContratto().getTest();
        
        if (!isMessaggioDiTest) {

          // si esegue il controllo sintattico del messaggio
          XmlOptions validationOptions = new XmlOptions();
          ArrayList<XmlValidationError> validationErrors = new ArrayList<XmlValidationError>();
          validationOptions.setErrorListener(validationErrors);
          boolean isSintassiXmlOK = istanzaContrattoDocument.validate(validationOptions);

          if (!isSintassiXmlOK) {
            synchronized (validationErrors) {
              // Sincronizzazione dell'oggetto validationErrors per scrivere
              // sul log il dettaglio dell'errore su righe successive.  
              StringBuilder strLog = new StringBuilder(credenzialiUtente.getPrefissoLogger());
              strLog.append(" Errore nella validazione del messaggio ricevuto per la gestione di una " +
                  "istanza di contratto dei lotti.");
              logger.error(strLog);
              
              for (int i = 0; i < validationErrors.size(); i++) {
                logger.error(validationErrors.get(i).getMessage());
              }
            }

            // Costruzione del messaggio da ritornare al client
            StringBuilder strError = new StringBuilder("Errore di validazione del messaggio XML: \n");
            for (int i = 0; i < validationErrors.size(); i++) {
              strError.append(validationErrors.get(i).getMessage() + "\n");
            }
            
            result.setSuccess(false);
            result.setError(strError.toString());
            
          } else {
            LottoContrattoType[] arrayContratto = 
              istanzaContrattoDocument.getRichiestaSincronaIstanzaContratto().getListaLottiContrattiArray();

            if (arrayContratto != null && arrayContratto.length > 0) {
              // HashMap per caricare gli oggetti ResponseLottoType per ciascun lotto, con CIG come chiave della hashMap
              HashMap<String, ResponseLottoType> hmResponseLotti = new HashMap<String, ResponseLottoType>();
              
              StringBuilder strQueryCig = new StringBuilder("'");
              
              for (int esi = 0; esi < arrayContratto.length; esi++) {
              	LottoContrattoType oggettoLottoContratto = arrayContratto[esi];
                String cigLotto = oggettoLottoContratto.getW3CIG();

                strQueryCig.append(cigLotto);
                if (esi+1 < arrayContratto.length)
                	strQueryCig.append("','");
                else
                	strQueryCig.append("'");
							}
              
              List<?> listaCodGara = this.sqlManager.getListVector(
              		"select CODGARA from w9lott where CIG in (" + strQueryCig.toString() + ")", null);
              if (listaCodGara == null || (listaCodGara != null && listaCodGara.size() == 0)) {
              	// Nessuno dei CIG indicati non esistono nella base dati di destinazione
              	logger.error(credenzialiUtente.getPrefissoLogger()
              			+ " nessuno dei lotti indicati sono presenti in archivio");
              	throw new WSVigilanzaException("Attenzione: la scheda non e' inviabile poiche' non esiste la gara nel sistema di destinazione. E' necessario provvedere preventivamente alla sua creazione, importandola da simog o inviandola da Appalti");
              } else {
              	if (listaCodGara.size() !=  arrayContratto.length) {
              		// Non tutti i CIG esistono nella base dati!!
              		logger.error(credenzialiUtente.getPrefissoLogger()
                			+ " uno o piu' lotti indicati non sono presenti in archivio");
             			throw new WSVigilanzaException("Uno o piu' lotti indicati non sono presenti in archivio");
              	} else {
              		// Tutti i CIG esistono in base dati, ma controllo che appartengano tutti alla stessa gara
              		List<?> listaDistinctCodGara = this.sqlManager.getListVector(
                  		"select distinct CODGARA from w9lott where CIG in (" + strQueryCig.toString() + ")", null);
              		if (listaDistinctCodGara.size() >  1) {
                		// Non tutti i CIG appartengono alla stessa gara!!
              			logger.error(credenzialiUtente.getPrefissoLogger()
                  			+ " i CIG appartengono a " + listaDistinctCodGara.size() + " gare diverse");
                  	throw new WSVigilanzaException("I CIG indicati appartengono a "
                  			+ listaDistinctCodGara.size() + " gare diverse");
                	}
              	}
              }
              
              int numeroLottiImportati = 0;
              int numeroLottiNonImportati = 0;
              int numeroLottiAggiornati = 0;
              
              for (int contr = 0; contr < arrayContratto.length && result.isSuccess(); contr++) {
                LottoContrattoType oggettoLottoContratto = arrayContratto[contr];
                String cigLotto = oggettoLottoContratto.getW3CIG();

                HashMap<String, Long> hashM = UtilitySITAT.getCodGaraCodLottByCIG(cigLotto, this.sqlManager);
                Long codiceGara = hashM.get("CODGARA");
                Long codiceLotto = hashM.get("CODLOTT");
                //verifica monitoraggio multilotto - verifico se il campo CIG_MASTER_ML è valorizzato
                String cigMaster = (String) this.sqlManager.getObject("select CIG_MASTER_ML from W9LOTT where CODGARA=? and CODLOTT=?", 
                		new Object[] { codiceGara, codiceLotto });
                if (! UtilitySITAT.isLottoRiaggiudicato(codiceGara, codiceLotto, this.sqlManager) &&
                		(cigMaster == null || cigMaster.trim().length() == 0)) {
	            		if (!UtilitySITAT.isUtenteAmministratore(credenzialiUtente)) {
	            			if (!UtilitySITAT.isUtenteRupDelLotto(cigLotto, credenzialiUtente, this.sqlManager)) {
	            				logger.error(credenzialiUtente.getPrefissoLogger() + "L'utente non e' RUP del lotto con CIG=" + cigLotto
	            						+ ", oppure nella gara e/o nel lotto non e' stato indicato il RUP");
	
	                		throw new WSVigilanzaException("Le credenziali fornite non coincidono con quelle del RUP indicato");
	            			}
	            		}
	
	                if (result.isSuccess()) {
	                  // Controlli preliminari su gara e su lotto.
	                  boolean isS2 = UtilitySITAT.isS2(codiceGara, codiceLotto, this.sqlManager);
	                  boolean isE1 = UtilitySITAT.isE1(codiceGara, codiceLotto, this.sqlManager);
	                  boolean isSAQ = UtilitySITAT.isSAQ(codiceGara, this.sqlManager);
	                  boolean isORD = UtilitySITAT.isOrd(codiceGara, this.sqlManager);
	                  
	                  // Controllo esistenza della fase: se no, si esegue l'insert di tutti dati, 
	                  // se si, si esegue l'update dei record di W9INIZ, W9PUES, ecc...
	                  Long faseEsecuz = null;
	                  if (isSAQ) {
	                  	faseEsecuz = new Long(CostantiWSW9.STIPULA_ACCORDO_QUADRO);
	                  } else if (isS2 && !isE1 && !isSAQ && isORD) {
	                    faseEsecuz = new Long(CostantiWSW9.INIZIO_CONTRATTO_SOPRA_SOGLIA);
	                  } else {
	                    faseEsecuz = new Long(CostantiWSW9.FASE_SEMPLIFICATA_INIZIO_CONTRATTO);
	                  }
	                  
	                  boolean esisteFaseInizio = UtilitySITAT.existsFase(codiceGara, codiceLotto, new Long(1), faseEsecuz.intValue(), this.sqlManager);
	                  
	                  if (UtilitySITAT.isFaseAttiva(faseEsecuz, this.sqlManager)) {
		                  if (esisteFaseInizio || UtilitySITAT.isFaseAbilitata(codiceGara, codiceLotto,new Long(1),  faseEsecuz.intValue(), this.sqlManager)) {
		                  	if (esisteFaseInizio || UtilitySITAT.isFaseVisualizzabile(codiceGara, codiceLotto, faseEsecuz.intValue(), this.sqlManager)) {
		                      // Inizio contratto. 
		                      ContrattoType contrattoType = oggettoLottoContratto.getContratto();
		                    	// Record in W9FASI.
		                    	UtilitySITAT.istanziaFase(this.sqlManager, codiceGara, codiceLotto, faseEsecuz, new Long(1));
		                      
		                      DataColumn codGaraInizio = new DataColumn("W9INIZ.CODGARA", new JdbcParametro(
		                      		JdbcParametro.TIPO_NUMERICO, codiceGara));
		                      DataColumn codLottInizio = new DataColumn("W9INIZ.CODLOTT", new JdbcParametro(
		                      		JdbcParametro.TIPO_NUMERICO, codiceLotto));
		                      DataColumn numInizio = new DataColumn("W9INIZ.NUM_INIZ", new JdbcParametro(
		                      		JdbcParametro.TIPO_NUMERICO, new Long(1)));
		                      DataColumn numAppa = new DataColumn("W9INIZ.NUM_APPA", new JdbcParametro(
			                    		JdbcParametro.TIPO_NUMERICO, new Long(1)));
		                      codGaraInizio.setChiave(true);
		                      codLottInizio.setChiave(true);
		                      numInizio.setChiave(true);
		                      
		                      DataColumnContainer dccInizio = new DataColumnContainer(new DataColumn[] { 
		                          codGaraInizio, codLottInizio, numInizio, numAppa });
		                      // DATA_STIPULA
		                      if (contrattoType.isSetW3DATASTI()) {
		                        dccInizio.addColumn("W9INIZ.DATA_STIPULA",
		                          new JdbcParametro(JdbcParametro.TIPO_DATA, contrattoType.getW3DATASTI().getTime()));
		                      }
		                      // DATA_ESECUTIVITA
		                      if (contrattoType.isSetW3DATAESE()) {
		                        dccInizio.addColumn("W9INIZ.DATA_ESECUTIVITA",
		                          new JdbcParametro(JdbcParametro.TIPO_DATA, contrattoType.getW3DATAESE().getTime()));
		                      }
		                      // DATA_DECORRENZA
		                      if (contrattoType.isSetW9INDECO()) {
		                        dccInizio.addColumn("W9INIZ.DATA_DECORRENZA",
		                          new JdbcParametro(JdbcParametro.TIPO_DATA, contrattoType.getW9INDECO().getTime()));
		                      }
		                      // DATA_SCADENZA
		                      if (contrattoType.isSetW9INSCAD()) {
		                        dccInizio.addColumn("W9INIZ.DATA_SCADENZA",
		                          new JdbcParametro(JdbcParametro.TIPO_DATA, contrattoType.getW9INSCAD().getTime()));
		                      }
		                      // IMPORTO_CAUZIONE
		                      if (contrattoType.isSetW3ICAUZIO()) {
		                        dccInizio.addColumn("W9INIZ.IMPORTO_CAUZIONE",
		                          new JdbcParametro(JdbcParametro.TIPO_DECIMALE, new Double(contrattoType.getW3ICAUZIO())));
		                      }
		                      
		                      // W9SIC
		                      DataColumn codGaraSic = new DataColumn("W9SIC.CODGARA", new JdbcParametro(
		                      		JdbcParametro.TIPO_NUMERICO, codiceGara));
		                      DataColumn codLottSic = new DataColumn("W9SIC.CODLOTT", new JdbcParametro(
		                      		JdbcParametro.TIPO_NUMERICO, codiceLotto));
		                      DataColumn numSic = new DataColumn("W9SIC.NUM_SIC", new JdbcParametro(
		                      		JdbcParametro.TIPO_NUMERICO, new Long(1)));
		                      DataColumn numAppaSic = new DataColumn("W9SIC.NUM_INIZ", new JdbcParametro(
			                    		JdbcParametro.TIPO_NUMERICO, new Long(1)));
		                      codGaraSic.setChiave(true);
		                      codLottSic.setChiave(true);
		                      numSic.setChiave(true);
		
		                      DataColumnContainer dccSic = new DataColumnContainer(new DataColumn[] { 
		                      		codGaraSic, codLottSic, numSic, numAppaSic});
		                      dccSic.addColumn("W9SIC.PIANSCIC", new JdbcParametro(JdbcParametro.TIPO_TESTO, "2"));
		                      dccSic.addColumn("W9SIC.DIROPE", new JdbcParametro(JdbcParametro.TIPO_TESTO, "2"));
		                      dccSic.addColumn("W9SIC.TUTOR", new JdbcParametro(JdbcParametro.TIPO_TESTO, "2"));
		                      
		                      // W9PUES
		                      DataColumn codGaraPues = new DataColumn("W9PUES.CODGARA", new JdbcParametro(
		                      		JdbcParametro.TIPO_NUMERICO, codiceGara));
		                      DataColumn codLottPues = new DataColumn("W9PUES.CODLOTT", new JdbcParametro(
		                      		JdbcParametro.TIPO_NUMERICO, codiceLotto));
		                      DataColumn numInizPues = new DataColumn("W9PUES.NUM_INIZ", new JdbcParametro(
		                      		JdbcParametro.TIPO_NUMERICO, new Long(1)));
		                      DataColumn numPues = new DataColumn("W9PUES.NUM_PUES", new JdbcParametro(
		                      		JdbcParametro.TIPO_NUMERICO, new Long(1)));
		                      codGaraPues.setChiave(true);
		                      codLottPues.setChiave(true);
		                      numInizPues.setChiave(true);
		                      numPues.setChiave(true);
		                      
		                      DataColumnContainer dccPues = new DataColumnContainer(new DataColumn[] { 
		                          codGaraPues, codLottPues, numInizPues, numPues });
		                      if (contrattoType.isSetW3GUCE2()) {
		                        dccPues.addColumn("W9PUES.DATA_GUCE", new JdbcParametro(
		                        		JdbcParametro.TIPO_DATA, contrattoType.getW3GUCE2().getTime()));
		                      }
		                      if (contrattoType.isSetW3GURI2()) {
		                        dccPues.addColumn("W9PUES.DATA_GURI", new JdbcParametro(
		                        		JdbcParametro.TIPO_DATA, contrattoType.getW3GURI2().getTime()));
		                      }
		                      if (contrattoType.isSetW3ALBO2()) {
		                        dccPues.addColumn("W9PUES.DATA_ALBO", new JdbcParametro(
		                        		JdbcParametro.TIPO_DATA, contrattoType.getW3ALBO2().getTime()));
		                      }
		                      if (contrattoType.isSetW3NAZ2()) {
		                        dccPues.addColumn("W9PUES.QUOTIDIANI_NAZ", new JdbcParametro(
		                        		JdbcParametro.TIPO_NUMERICO, new Long(contrattoType.getW3NAZ2()))); 
		                      }
		                      if (contrattoType.isSetW3REG2()) {
		                        dccPues.addColumn("W9PUES.QUOTIDIANI_REG", new JdbcParametro(
		                        		JdbcParametro.TIPO_NUMERICO, new Long(contrattoType.getW3REG2()))); 
		                      }
		                      if (contrattoType.isSetW3PROFILO2()) {
		                        dccPues.addColumn("W9PUES.PROFILO_COMMITTENTE", new JdbcParametro(
		                        		JdbcParametro.TIPO_TESTO, contrattoType.getW3PROFILO2() ? "1" : "2"));
		                      }
		                      if (contrattoType.isSetW3MIN2()) {
		                        dccPues.addColumn("W9PUES.SITO_MINISTERO_INF_TRASP", new JdbcParametro(
		                        		JdbcParametro.TIPO_TESTO, contrattoType.getW3MIN2() ? "1": "2"));
		                      }
		                      if (contrattoType.isSetW3OSS2()) {
		                        dccPues.addColumn("W9PUES.SITO_OSSERVATORIO_CP",new JdbcParametro(
		                        		JdbcParametro.TIPO_TESTO, contrattoType.getW3OSS2() ? "1": "2"));
		                      }
		
		                      if (!esisteFaseInizio) {
		                        dccInizio.insert("W9INIZ", this.sqlManager);
		                        dccSic.insert("W9SIC", this.sqlManager);
		                          
		                        Long count = (Long) this.sqlManager.getObject(
	                        			"select count(*) from W9PUES where CODGARA=? and CODLOTT=? and NUM_INIZ=1 and NUM_PUES=1", 
	                        					new Object[] { codiceGara, codiceLotto });
		                        
		                        if (count != null && count.longValue() == 0) {
		                        	dccPues.insert("W9PUES", this.sqlManager);
		                        } else {
		                        	this.aggiornaW9PUES(codiceGara, codiceLotto, dccPues);
		                        }
		
		                        if (UtilitySITAT.isScritturaNoteAvvisiAttiva()) {
		                        	String codiceCIG = UtilitySITAT.getCIGLotto(codiceGara, codiceLotto, this.sqlManager);
		                       		if (CostantiWSW9.STIPULA_ACCORDO_QUADRO == faseEsecuz.intValue()) {
		                       			UtilitySITAT.insertNoteAvvisi("W9LOTT", codiceGara.toString(), codiceLotto.toString(),
		 	          										null, null, null, "CIG " + codiceCIG + ": inserita fase 'Stipula accordo quadro'", null, 
		 	          										this.sqlManager);
		                       		} else {
		                       			UtilitySITAT.insertNoteAvvisi("W9LOTT", codiceGara.toString(), codiceLotto.toString(),
		 	          										null, null, null, "CIG " + codiceCIG + ": inserita fase 'Inizio contratto'", null, 
		 	          										this.sqlManager);
		                       		}
		                        }
		                        
		                        numeroLottiImportati++;
		                        UtilitySITAT.aggiungiMsgSchedaA(hmResponseLotti, cigLotto, true, "Fase Contratto importata");
		
		                      } else if (esisteFaseInizio && sovrascrivereDatiEsistenti) {
		
		                      	DataColumnContainer dccInizioDB = new DataColumnContainer(this.sqlManager, "W9INIZ",
		                      			"select DATA_STIPULA, DATA_ESECUTIVITA, DATA_DECORRENZA, DATA_SCADENZA, IMPORTO_CAUZIONE, "
		                      					 + "CODGARA, CODLOTT, NUM_INIZ "
		                      			+ "from W9INIZ where CODGARA=? and CODLOTT=? and NUM_INIZ=1 and NUM_APPA=1", new Object[] { codiceGara, codiceLotto});
		                      	
		                      	Iterator<Entry<String, DataColumn>> iterInizioDB = dccInizioDB.getColonne().entrySet().iterator();
		                        while (iterInizioDB.hasNext()) {
		                        	Entry<String, DataColumn> entry = iterInizioDB.next(); 
		                          String nomeCampo = entry.getKey();
		                          if (dccInizio.isColumn(nomeCampo)) {
		                          	dccInizioDB.setValue(nomeCampo, dccInizio.getColumn(nomeCampo).getValue());
		                          }
		                        }
		                      	
		                        /*if (contrattoType.isSetW3DATASTI()) {
		                          dccInizioDB.setValue("W9INIZ.DATA_STIPULA",
		                            new JdbcParametro(JdbcParametro.TIPO_DATA, contrattoType.getW3DATASTI().getTime()));
		                        }
		                        if (contrattoType.isSetW3DATAESE()) {
		                          dccInizioDB.setValue("W9INIZ.DATA_ESECUTIVITA",
		                            new JdbcParametro(JdbcParametro.TIPO_DATA, contrattoType.getW3DATAESE().getTime()));
		                        }
		                        if (contrattoType.isSetW9INDECO()) {
		                          dccInizioDB.setValue("W9INIZ.DATA_DECORRENZA",
		                            new JdbcParametro(JdbcParametro.TIPO_DATA, contrattoType.getW9INDECO().getTime()));
		                        }
		                        if (contrattoType.isSetW9INSCAD()) {
		                          dccInizioDB.setValue("W9INIZ.DATA_SCADENZA",
		                            new JdbcParametro(JdbcParametro.TIPO_DATA, contrattoType.getW9INSCAD().getTime()));
		                        }
		                        if (contrattoType.isSetW3ICAUZIO()) {
		                          dccInizioDB.setValue("W9INIZ.IMPORTO_CAUZIONE",
		                            new JdbcParametro(JdbcParametro.TIPO_DECIMALE, new Double(contrattoType.getW3ICAUZIO())));
		                        }*/
		                      	
		                        if (dccInizioDB.isModifiedTable("W9INIZ")) {
		                        	dccInizioDB.getColumn("W9INIZ.CODGARA").setChiave(true);
		                        	dccInizioDB.getColumn("W9INIZ.CODLOTT").setChiave(true);
		                        	dccInizioDB.getColumn("W9INIZ.NUM_INIZ").setChiave(true);
		                        	dccInizioDB.update("W9INIZ", this.sqlManager);
		                        }
		                        
		                        Long count = (Long) this.sqlManager.getObject(
	                        			"select count(*) from W9PUES where CODGARA=? and CODLOTT=? and NUM_INIZ=1 and NUM_PUES=1", 
	                        					new Object[] { codiceGara, codiceLotto });
		                        
		                        if (count != null && count.longValue() == 0) {
		                        	dccPues.insert("W9PUES", this.sqlManager);
		                        } else {
		                        	this.aggiornaW9PUES(codiceGara, codiceLotto, dccPues);
		                        }
		                        
		                      	if (UtilitySITAT.isScritturaNoteAvvisiAttiva()) {
		                        	String codiceCIG = UtilitySITAT.getCIGLotto(codiceGara, codiceLotto, this.sqlManager);
		                      		if (CostantiWSW9.STIPULA_ACCORDO_QUADRO == faseEsecuz.intValue()) {
		                       			UtilitySITAT.insertNoteAvvisi("W9LOTT", codiceGara.toString(), codiceLotto.toString(),
		 	          										null, null, null, "CIG " + codiceCIG + ": modificata fase 'Stipula accordo quadro'", null, 
		 	          										this.sqlManager);
		                       		} else {
		                       			UtilitySITAT.insertNoteAvvisi("W9LOTT", codiceGara.toString(), codiceLotto.toString(),
		 	          										null, null, null, "CIG " + codiceCIG + ": modificata fase 'Inizio contratto'", null, 
		 	          										this.sqlManager);
		                       		}
		                        }
		                      	
		                        numeroLottiAggiornati++;
		                        UtilitySITAT.aggiungiMsgSchedaA(hmResponseLotti, cigLotto, true, "Fase Contratto aggiornata");
		
		                      } else {
		                        // Caso in cui in base dati esiste gia' la fase contratto per il lotto
		                        // ed il flag di sovrascrittura dei dati e' valorizzato a false.
		                        
		                        StringBuilder strLog = new StringBuilder(credenzialiUtente.getPrefissoLogger());
		                        strLog.append(" L'inizio del contratto del lotto con CIG=");
		                        strLog.append(cigLotto);
		                        strLog.append(" non e' stato importato perche' la fase gia' esiste nella base dati " +
		                            "e non la si vuole sovrascrivere.");
		                        logger.info(strLog.toString());
		                        
		                        numeroLottiNonImportati++;
		                        
		                        UtilitySITAT.aggiungiMsgSchedaA(hmResponseLotti, cigLotto, false,
		                        		StringUtils.replace(ConfigManager.getValore("error.contratto.esistente"),
		                        				"{0}", cigLotto));
		                      }
		                    } else {
		                      StringBuilder strLog =
		                        new StringBuilder(credenzialiUtente.getPrefissoLogger());
		
		                      strLog.append(" Il contratto del lotto con CIG=");
		                      strLog.append(cigLotto);
		                      strLog.append(" non e' stato importato perche' non ha superato i controlli preliminari" +
		                      		" della fase del contratto. Nel caso specifico la condizione (!isSAQ && isEA) non risulta verificata.");
		                      
		                      logger.error(strLog);
		
		                      numeroLottiNonImportati++;
		                      
		                      UtilitySITAT.aggiungiMsgSchedaA(hmResponseLotti, cigLotto, false,
		                      		StringUtils.replace(ConfigManager.getValore("error.contratto.noVisibile"),
		                      				"{0}", cigLotto));
		                    }
		                  } else {
		                    // Controlli preliminari non superati. Il lotto non viene importato.
		                    StringBuilder strLog = new StringBuilder(credenzialiUtente.getPrefissoLogger());
		                    strLog.append("Il lotto con CIG='");
		                    strLog.append(cigLotto);
		                    strLog.append("' non ha le fasi comunicazione esito e aggiudicazione oppure non ha la " +
		                    		"fase stipula accordo quadro. Impossibile importare il contratto." );
		                    logger.info(strLog.toString());
		
		                    numeroLottiNonImportati++;
		
		                 		UtilitySITAT.aggiungiMsgSchedaA(hmResponseLotti, cigLotto, false,
		                    		StringUtils.replace(ConfigManager.getValore("error.contratto.noFlussiPrecedenti"),
		                    				"{0}", cigLotto));
		                  }
		                } else {
		                	
		                	// Scheda non prevista per il contratto
		                	StringBuilder strLog = new StringBuilder(credenzialiUtente.getPrefissoLogger());
		                	strLog.append("Il lotto con CIG='");
		                  strLog.append(cigLotto);
		                  strLog.append("' non prevede la scheda ");
		                  strLog.append(faseEsecuz.toString());
		                  strLog.append(" perche' il TAB1NORD e' minore o uguale a zero");
		                  logger.error(strLog);
		
		                  numeroLottiNonImportati++;
		                  
		                  UtilitySITAT.aggiungiMsgSchedaA(hmResponseLotti, cigLotto, false,
		                  		ConfigManager.getValore("error.schedaNonPrevista"));
		                	
		                }
	                }
                } else {
                	// Lotto riaggiudicato o monitoraggio multilotto
                	String codiceErrore = null;
                	String messaggioLog = null;
                	if (cigMaster != null && cigMaster.trim().length() > 0) {
                		codiceErrore = "error.multilotto.noMonitoraggio";
                		messaggioLog = "Il lotto in questione fa parte di un monitoraggio multilotto. Non è possibile trasmettere le singole schede";
                	} else {
                		codiceErrore = "error.lotto.riaggiudicato";
                		messaggioLog = "Non e' possibile importare dati di un lotto riaggiudicato";
                	}
                	logger.info(messaggioLog + " (CIG: " + cigLotto + ", CFSA: " + codFiscaleStazAppaltante + ")");
	              	numeroLottiNonImportati++;
	              	UtilitySITAT.aggiungiMsgSchedaA(hmResponseLotti, cigLotto, false,
                  		StringUtils.replace(ConfigManager.getValore(codiceErrore), "{0}", cigLotto));
                }
              } // Chiusura ciclo for sui lotti con il contratto.
              
              UtilitySITAT.preparaRisultatoMessaggio(result, sovrascrivereDatiEsistenti, hmResponseLotti,
									numeroLottiImportati, numeroLottiNonImportati, numeroLottiAggiornati);
            }
          }
        } else {
          // E' stato inviato un messaggio di test.
          StringBuilder strLog = new StringBuilder(credenzialiUtente.getPrefissoLogger());
          strLog.append(" E' stato inviato un messaggio di test. Tale messaggio non e' stato elaborato.'\n");
          strLog.append("Messaggio: ");
          strLog.append(contratto.toString());
          logger.info(strLog);
          
          result.setSuccess(true);
          result.setError("E' stato inviato un messaggio di test: messaggio non elaborato.");
        }
      } catch (XmlException e) {
        logger.error(" Errore nel parsing dell'XML ricevuto.", e);

        throw e;
      } catch (GestoreException g) {
        logger.error(" Errore nella preparazione dei dati da salvare.", g);

        throw g;
      } catch (SQLException sql) {
    	logger.error("Errore sql nel salvataggio dei dati ricevuti.", sql);

      	throw sql;
      } catch (Throwable t) {
        logger.error(" Errore generico nell'esecuzione della procedura.", t);

        throw t;
      }
    } else {
      logger.error("La verifica delle credenziali non e' stato superato. Messaggio di errore: "
          + result.getError());
    }

    if (logger.isDebugEnabled()) {
      logger.debug("istanziaContratto: fine metodo");
      
      logger.debug("Response : " + UtilitySITAT.messaggioLogEsteso(result));
    }
    
    // MODIFICA TEMPORANEA -- DA CANCELLARE ---------------
    result.setError(UtilitySITAT.messaggioEsteso(result));
    // ----------------------------------------------------
    
    return result;
  }

	private void aggiornaW9PUES(Long codiceGara, Long codiceLotto, DataColumnContainer dccPues)
			throws GestoreException, SQLException {
		DataColumnContainer dccPuesDB = new DataColumnContainer(this.sqlManager, "W9PUES", 
				"select DATA_GUCE, DATA_GURI, DATA_ALBO, QUOTIDIANI_NAZ, QUOTIDIANI_REG, "
						+ "PROFILO_COMMITTENTE, SITO_MINISTERO_INF_TRASP, SITO_OSSERVATORIO_CP, "
						+ "CODGARA, CODLOTT, NUM_INIZ, NUM_PUES "
			 + " from W9PUES where CODGARA=? and CODLOTT=? and NUM_INIZ=1 and NUM_PUES=1",
			 new Object[] { codiceGara, codiceLotto });
		
		Iterator<Entry<String, DataColumn>> iterPuesDB = dccPuesDB.getColonne().entrySet().iterator();
		while (iterPuesDB.hasNext()) {
			Entry<String, DataColumn> entry = iterPuesDB.next(); 
		  String nomeCampo = entry.getKey();
		  if (dccPues.isColumn(nomeCampo)) {
		  	dccPuesDB.setValue(nomeCampo, dccPues.getColumn(nomeCampo).getValue());
		  }
		}
		
		/*if (contrattoType.isSetW3GUCE2()) {
		  dccPuesDB.setValue("W9PUES.DATA_GUCE", new JdbcParametro(
		  		JdbcParametro.TIPO_DATA, contrattoType.getW3GUCE2().getTime()));
		}
		if (contrattoType.isSetW3GURI2()) {
		  dccPuesDB.setValue("W9PUES.DATA_GURI", new JdbcParametro(
		  		JdbcParametro.TIPO_DATA, contrattoType.getW3GURI2().getTime()));
		}
		if (contrattoType.isSetW3ALBO2()) {
		  dccPuesDB.setValue("W9PUES.DATA_ALBO", new JdbcParametro(
		  		JdbcParametro.TIPO_DATA, contrattoType.getW3ALBO2().getTime()));
		}
		if (contrattoType.isSetW3NAZ2()) {
		  dccPuesDB.setValue("W9PUES.QUOTIDIANI_NAZ", new JdbcParametro(
		  		JdbcParametro.TIPO_NUMERICO, new Long(contrattoType.getW3NAZ2()))); 
		}
		if (contrattoType.isSetW3REG2()) {
		  dccPuesDB.setValue("W9PUES.QUOTIDIANI_REG", new JdbcParametro(
		  		JdbcParametro.TIPO_NUMERICO, new Long(contrattoType.getW3REG2()))); 
		}
		if (contrattoType.isSetW3PROFILO2()) {
		  dccPuesDB.setValue("W9PUES.PROFILO_COMMITTENTE", new JdbcParametro(
		  		JdbcParametro.TIPO_TESTO, contrattoType.getW3PROFILO2() ? "1" : "2"));
		}
		if (contrattoType.isSetW3MIN2()) {
		  dccPuesDB.setValue("W9PUES.SITO_MINISTERO_INF_TRASP", new JdbcParametro(
		  		JdbcParametro.TIPO_TESTO, contrattoType.getW3MIN2() ? "1": "2"));
		}
		if (contrattoType.isSetW3OSS2()) {
		  dccPuesDB.setValue("W9PUES.SITO_OSSERVATORIO_CP",new JdbcParametro(
		  		JdbcParametro.TIPO_TESTO, contrattoType.getW3OSS2() ? "1": "2"));
		}*/
		
		if (dccPuesDB.isModifiedTable("W9PUES")) {
			dccPuesDB.getColumn("W9PUES.CODGARA").setChiave(true);
			dccPuesDB.getColumn("W9PUES.CODLOTT").setChiave(true);
			dccPuesDB.getColumn("W9PUES.NUM_INIZ").setChiave(true);
			dccPuesDB.getColumn("W9PUES.NUM_PUES").setChiave(true);
			dccPuesDB.update("W9PUES", this.sqlManager);
		}
	}

}
