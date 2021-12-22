package it.eldasoft.sil.vigilanza.bl;

import it.eldasoft.gene.bl.GeneManager;
import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.db.datautils.DataColumn;
import it.eldasoft.gene.db.datautils.DataColumnContainer;
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.sil.vigilanza.beans.IncaricoProfessionaleType;
import it.eldasoft.sil.vigilanza.beans.InizioType;
import it.eldasoft.sil.vigilanza.beans.LottoInizioType;
import it.eldasoft.sil.vigilanza.beans.RichiestaSincronaIstanzaInizioDocument;
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
 * Classe per l'import dei dati di Inizio Contratto dei lotti della gara.
 * 
 * @author Luca.Giacomazzo
 */
public class IstanzaInizioManager {

  private static Logger logger = Logger.getLogger(IstanzaInizioManager.class);
  
  private CredenzialiManager credenzialiManager;
  
  private SqlManager sqlManager;

  private GeneManager geneManager;
  
  private RupManager rupManager;
  
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
   * @param geneManager the geneManager to set
   */
  public void setGeneManager(GeneManager geneManager) {
    this.geneManager = geneManager;
  }
  
  /**
   * @param rupManager the rupManager to set
   */
  public void setRupManager(RupManager rupManager) {
    this.rupManager = rupManager;
  }
  
  /**
   * Metodo per la gestione dell'inizio dei contratti.
   * 
   * @param login LoginType
   * @param inizio IstanzaOggettoType
   * @return Ritorna l'oggetto ResponseType con l'esito dell'operazione
   * @throws XmlException 
   * @throws GestoreException 
   * @throws SQLException 
   * @throws Throwable
   */
  public ResponseType istanziaInizio(LoginType login, IstanzaOggettoType inizio)
      throws XmlException, GestoreException, SQLException, Throwable {
    ResponseType result = null;

    if (logger.isDebugEnabled()) {
      logger.debug("istanziaInizio: inizio metodo");
      
      logger.debug("XML : " + inizio.getOggettoXML());
    }

    // Codice fiscale della Stazione appaltante
    String codFiscaleStazAppaltante = inizio.getTestata().getCFEIN();

    boolean sovrascrivereDatiEsistenti = false;
    if (inizio.getTestata().getSOVRASCR() != null) {
      sovrascrivereDatiEsistenti = inizio.getTestata().getSOVRASCR().booleanValue(); 
    }

    // Verifica di login, password e determinazione della S.A.
    HashMap<String, Object> hm = this.credenzialiManager.verificaCredenziali(login, codFiscaleStazAppaltante);
    result = (ResponseType) hm.get("RESULT");

    if (result.isSuccess()) {
      Credenziale credenzialiUtente = (Credenziale) hm.get("USER");
      String xmlInizioContratto = inizio.getOggettoXML();

      try {
        RichiestaSincronaIstanzaInizioDocument istanzaInizioDocument =
          RichiestaSincronaIstanzaInizioDocument.Factory.parse(xmlInizioContratto);

        boolean isMessaggioDiTest = 
          istanzaInizioDocument.getRichiestaSincronaIstanzaInizio().isSetTest()
            && istanzaInizioDocument.getRichiestaSincronaIstanzaInizio().getTest();

        if (! isMessaggioDiTest) {

          // si esegue il controllo sintattico del messaggio
          XmlOptions validationOptions = new XmlOptions();
          ArrayList<XmlValidationError> validationErrors = new ArrayList<XmlValidationError>();
          validationOptions.setErrorListener(validationErrors);
          boolean isSintassiXmlOK = istanzaInizioDocument.validate(validationOptions);

          if (!isSintassiXmlOK) {
            synchronized (validationErrors) {
              // Sincronizzazione dell'oggetto validationErrors per scrivere
              // sul log il dettaglio dell'errore su righe successive.  
              StringBuilder strLog = new StringBuilder(credenzialiUtente.getPrefissoLogger());
              strLog.append(" Errore nella validazione del messaggio ricevuto per la gestione di una " +
                  "istanza di inizio contratto.");
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
            LottoInizioType[] arrayLottiInizio = 
              istanzaInizioDocument.getRichiestaSincronaIstanzaInizio().getListaLottiIniziArray();

            if (arrayLottiInizio != null && arrayLottiInizio.length > 0) {
              // HashMap per caricare gli oggetti ResponseLottoType per ciascun lotto, con CIG come chiave della hashMap
              HashMap<String, ResponseLottoType> hmResponseLotti = new HashMap<String, ResponseLottoType>();

              StringBuilder strQueryCig = new StringBuilder("'");
              
              for (int contr = 0; contr < arrayLottiInizio.length && result.isSuccess(); contr++) {
                LottoInizioType oggettoLottoInizio = arrayLottiInizio[contr];
                String cigLotto = oggettoLottoInizio.getW3CIG();
                strQueryCig.append(cigLotto);
                if (contr + 1 < arrayLottiInizio.length)
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
              	if (listaCodGara.size() !=  arrayLottiInizio.length) {
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

              for (int iniz = 0; iniz < arrayLottiInizio.length && result.isSuccess(); iniz++) {
                LottoInizioType oggettoLottoInizio = arrayLottiInizio[iniz];
                String cigLotto = oggettoLottoInizio.getW3CIG();
	                
                HashMap<String, Long> hashM = UtilitySITAT.getCodGaraCodLottByCIG(cigLotto, this.sqlManager);
                Long codiceGara = hashM.get("CODGARA");
                Long codiceLotto = hashM.get("CODLOTT");

            		if (!UtilitySITAT.isUtenteAmministratore(credenzialiUtente)) {
            			if (!UtilitySITAT.isUtenteRupDelLotto(cigLotto, credenzialiUtente, this.sqlManager)) {
            				logger.error(credenzialiUtente.getPrefissoLogger() + "L'utente non e' RUP del lotto con CIG=" + cigLotto
            						+ ", oppure nella gara e/o nel lotto non e' stato indicato il RUP");

                		throw new WSVigilanzaException("Le credenziali fornite non coincidono con quelle del RUP indicato");
            			}
            		}

            		//verifica monitoraggio multilotto - verifico se il campo CIG_MASTER_ML è valorizzato
            		String cigMaster = (String) this.sqlManager.getObject("select CIG_MASTER_ML from W9LOTT where CODGARA=? and CODLOTT=?", new Object[] { codiceGara, codiceLotto });
            		if (! UtilitySITAT.isLottoRiaggiudicato(codiceGara, codiceLotto, this.sqlManager) &&
            				(cigMaster == null || cigMaster.trim().length() == 0)) {
	                // Controlli preliminari su gara e su lotto.
	                boolean isSAQ = UtilitySITAT.isSAQ(codiceGara, this.sqlManager);
	                boolean isAAQ = UtilitySITAT.isAAQ(codiceGara, this.sqlManager);
	                boolean isS2 = UtilitySITAT.isS2(codiceGara, codiceLotto, this.sqlManager);
	                boolean isE1 = UtilitySITAT.isE1(codiceGara, codiceLotto, this.sqlManager);
	                boolean isExSottosoglia = UtilitySITAT.isExSottosoglia(codiceGara, codiceLotto, this.sqlManager);
	                boolean isORD = UtilitySITAT.isOrd(codiceGara, this.sqlManager);
	                boolean isD1 = UtilitySITAT.isD1(codiceGara, this.sqlManager);
	
	                // Controllo esistenza della fase: se no, si esegue l'insert di tutti dati, 
	                // se si, si esegue l'update dei record di W9APPA, W9AGGI, W9INCA, ecc...
	                Long faseEsecuz = null;
	                
	                if (isSAQ && !isD1) {
	                	faseEsecuz = new Long(CostantiWSW9.STIPULA_ACCORDO_QUADRO);
	                } else if (isS2 && !isSAQ && isORD && !isD1) {
	                  faseEsecuz = new Long(CostantiWSW9.INIZIO_CONTRATTO_SOPRA_SOGLIA);
	                } else {
	                  faseEsecuz = new Long(CostantiWSW9.FASE_SEMPLIFICATA_INIZIO_CONTRATTO);
	                }

	                if (UtilitySITAT.isFaseAttiva(faseEsecuz, this.sqlManager)) {
		                boolean esisteFaseInizioContratto = UtilitySITAT.existsFase(codiceGara, codiceLotto, new Long(1), faseEsecuz.intValue(), this.sqlManager);
		                if (esisteFaseInizioContratto || UtilitySITAT.isFaseAbilitata(codiceGara, codiceLotto, new Long(1), faseEsecuz.intValue(), this.sqlManager)) {
		                	if (esisteFaseInizioContratto || UtilitySITAT.isFaseVisualizzabile(codiceGara, codiceLotto, faseEsecuz.intValue(), this.sqlManager)) {
		
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
		                      		codGaraSic, codLottSic, numSic, numAppaSic });
		                        
		                    dccSic.addColumn("W9SIC.PIANSCIC", new JdbcParametro(JdbcParametro.TIPO_TESTO, "2"));
		                    dccSic.addColumn("W9SIC.DIROPE", new JdbcParametro(JdbcParametro.TIPO_TESTO, "2"));
		                    dccSic.addColumn("W9SIC.TUTOR", new JdbcParametro(JdbcParametro.TIPO_TESTO, "2"));
		
		                    InizioType inizioType = oggettoLottoInizio.getInizio();
		
		                    // Record in W9FASI.
		                    UtilitySITAT.istanziaFase(this.sqlManager, codiceGara, codiceLotto, faseEsecuz, new Long(1));
		
		                    DataColumn codGaraInizio = new DataColumn("W9INIZ.CODGARA",new JdbcParametro(
		                    		JdbcParametro.TIPO_NUMERICO, codiceGara));
		                    DataColumn codLottInizio = new DataColumn("W9INIZ.CODLOTT", new JdbcParametro(
		                    		JdbcParametro.TIPO_NUMERICO, codiceLotto));
		                    DataColumn numInizio = new DataColumn("W9INIZ.NUM_INIZ",new JdbcParametro(
		                    		JdbcParametro.TIPO_NUMERICO, new Long(1)));
		                    DataColumn numAppa = new DataColumn("W9INIZ.NUM_APPA", new JdbcParametro(
		                    		JdbcParametro.TIPO_NUMERICO, new Long(1)));
		                    DataColumnContainer dccInizio = new DataColumnContainer(new DataColumn[] { 
		                        codGaraInizio, codLottInizio, numInizio, numAppa } );
		                
		                    if (inizioType.isSetW3DATASTI()) {
		                    	dccInizio.addColumn("W9INIZ.DATA_STIPULA",
		                    			new JdbcParametro(JdbcParametro.TIPO_DATA, inizioType.getW3DATASTI().getTime()));
		                    }
		                    if (inizioType.isSetW3DPROGES()) {
		                      dccInizio.addColumn("W9INIZ.DATA_INI_PROG_ESEC",
		                          new JdbcParametro(JdbcParametro.TIPO_DATA, inizioType.getW3DPROGES().getTime()));
		                    }
		                    if (inizioType.isSetW3DATAAPP()) {
		                      dccInizio.addColumn("W9INIZ.DATA_APP_PROG_ESEC",
		                          new JdbcParametro(JdbcParametro.TIPO_DATA, inizioType.getW3DATAAPP().getTime()));
		                    }
		                    if (inizioType.isSetW3FLAGFRA()) {
		                      dccInizio.addColumn("W9INIZ.FLAG_FRAZIONATA",
		                          new JdbcParametro(JdbcParametro.TIPO_TESTO, inizioType.getW3FLAGFRA() ? "1" : "2"));       
		                    }
		                    if (inizioType.isSetW3DVERBCO()) {                
		                      dccInizio.addColumn("W9INIZ.DATA_VERBALE_CONS",
		                          new JdbcParametro(JdbcParametro.TIPO_DATA, inizioType.getW3DVERBCO().getTime()));
		                    }
		                    if (inizioType.isSetW3DVERBDE()) {
		                      dccInizio.addColumn("W9INIZ.DATA_VERBALE_DEF", 
		                          new JdbcParametro(JdbcParametro.TIPO_DATA, inizioType.getW3DVERBDE().getTime()));
		                    }
		                    if (inizioType.isSetW3RISERVA() ) {
		                      dccInizio.addColumn("W9INIZ.FLAG_RISERVA",
		                          new JdbcParametro(JdbcParametro.TIPO_TESTO, inizioType.getW3RISERVA() ? "1" : "2"));
		                    }
		                    if (inizioType.isSetW3DVERBIN()) {
		                      dccInizio.addColumn("W9INIZ.DATA_VERB_INIZIO",
		                          new JdbcParametro(JdbcParametro.TIPO_DATA, inizioType.getW3DVERBIN().getTime()));
		                    }
		                    if (inizioType.isSetW3DTERM1()  ) {
		                      dccInizio.addColumn("W9INIZ.DATA_TERMINE",
		                          new JdbcParametro(JdbcParametro.TIPO_DATA, inizioType.getW3DTERM1().getTime()));
		                    }
		                    if (inizioType.isSetW3DATAESE() ) {
		                      dccInizio.addColumn("W9INIZ.DATA_ESECUTIVITA",
		                          new JdbcParametro(JdbcParametro.TIPO_DATA, inizioType.getW3DATAESE().getTime()));
		                    }
		
		                    if (!esisteFaseInizioContratto) {
		                      dccInizio.insert("W9INIZ", this.sqlManager);
		                      dccSic.insert("W9SIC", this.sqlManager);
		                      
		                      IncaricoProfessionaleType[] arrayIncarichiProfessionali = 
		                        oggettoLottoInizio.getInizio().getListaIncarichiProfessionaliArray();
		                      
		                      UtilitySITAT.gestioneIncarichiProfessionali(result,
		                      		codFiscaleStazAppaltante, credenzialiUtente, faseEsecuz.intValue(), codiceGara, 
		                      		codiceLotto, esisteFaseInizioContratto, arrayIncarichiProfessionali,
		                          this.sqlManager, this.geneManager, this.rupManager, isAAQ, isE1, isS2, isExSottosoglia);
		                      
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
		                      UtilitySITAT.aggiungiMsgSchedaA(hmResponseLotti, cigLotto, true, "Fase 'Inizio Contratto' inserita");
		                      
		                    } else if (esisteFaseInizioContratto && sovrascrivereDatiEsistenti) {
		
		                      DataColumnContainer dccInizioDB = new DataColumnContainer(this.sqlManager, "W9INIZ",
		                      		"select DATA_STIPULA, DATA_INI_PROG_ESEC, DATA_APP_PROG_ESEC, FLAG_FRAZIONATA, DATA_VERBALE_CONS, " 
		                      				 + "DATA_VERBALE_DEF, FLAG_RISERVA, DATA_VERB_INIZIO, DATA_TERMINE, DATA_ESECUTIVITA, "
		                      				 + "CODGARA, CODLOTT, NUM_INIZ, NUM_APPA "
		                      		+ "from W9INIZ where CODGARA=? and CODLOTT=? and NUM_INIZ=1 and NUM_APPA=1",
		                      				new Object[] { codiceGara, codiceLotto });
		
		                      Iterator<Entry<String, DataColumn>> iterInizioDB = dccInizioDB.getColonne().entrySet().iterator();
		                      while (iterInizioDB.hasNext()) {
		                      	Entry<String, DataColumn> entry = iterInizioDB.next(); 
		                        String nomeCampo = entry.getKey();
		                        if (dccInizio.isColumn(nomeCampo)) {
		                        	dccInizioDB.setValue(nomeCampo, dccInizio.getColumn(nomeCampo).getValue());
		                        }
		                      }
		                      
		                      /*if (inizioType.isSetW3DATASTI()) {
		                      	dccInizioDB.setValue("W9INIZ.DATA_STIPULA",
		                      			new JdbcParametro(JdbcParametro.TIPO_DATA, inizioType.getW3DATASTI().getTime()));
		                      }
		                      if (inizioType.isSetW3DPROGES()) {
		                        dccInizioDB.setValue("W9INIZ.DATA_INI_PROG_ESEC",
		                            new JdbcParametro(JdbcParametro.TIPO_DATA, inizioType.getW3DPROGES().getTime()));
		                      }
		                      if (inizioType.isSetW3DATAAPP()) {
		                        dccInizioDB.setValue("W9INIZ.DATA_APP_PROG_ESEC",
		                            new JdbcParametro(JdbcParametro.TIPO_DATA, inizioType.getW3DATAAPP().getTime()));
		                      }
		                      if (inizioType.isSetW3FLAGFRA()) {
		                        dccInizioDB.setValue("W9INIZ.FLAG_FRAZIONATA",
		                            new JdbcParametro(JdbcParametro.TIPO_TESTO, inizioType.getW3FLAGFRA() ? "1" : "2"));       
		                      }
		                      if (inizioType.isSetW3DVERBCO()) {                
		                        dccInizioDB.setValue("W9INIZ.DATA_VERBALE_CONS",
		                            new JdbcParametro(JdbcParametro.TIPO_DATA, inizioType.getW3DVERBCO().getTime()));
		                      }
		                      if (inizioType.isSetW3DVERBDE()) {
		                        dccInizioDB.setValue("W9INIZ.DATA_VERBALE_DEF", 
		                            new JdbcParametro(JdbcParametro.TIPO_DATA, inizioType.getW3DVERBDE().getTime()));
		                      }
		                      if (inizioType.isSetW3RISERVA() ) {
		                        dccInizioDB.setValue("W9INIZ.FLAG_RISERVA",
		                            new JdbcParametro(JdbcParametro.TIPO_TESTO, inizioType.getW3RISERVA() ? "1" : "2"));
		                      }
		                      if (inizioType.isSetW3DVERBIN()) {
		                        dccInizioDB.setValue("W9INIZ.DATA_VERB_INIZIO",
		                            new JdbcParametro(JdbcParametro.TIPO_DATA, inizioType.getW3DVERBIN().getTime()));
		                      }
		                      if (inizioType.isSetW3DTERM1()  ) {
		                        dccInizioDB.setValue("W9INIZ.DATA_TERMINE",
		                            new JdbcParametro(JdbcParametro.TIPO_DATA, inizioType.getW3DTERM1().getTime()));
		                      }*/
		                      
		                      if (dccInizioDB.isModifiedTable("W9INIZ")) {
		                      	dccInizioDB.getColumn("W9INIZ.CODGARA").setChiave(true);
		                      	dccInizioDB.getColumn("W9INIZ.CODLOTT").setChiave(true);
		                      	dccInizioDB.getColumn("W9INIZ.NUM_INIZ").setChiave(true);
		                      	
		                      	dccInizioDB.update("W9INIZ", this.sqlManager);
		                      }
		                      
		                    	IncaricoProfessionaleType[] arrayIncarichiProfessionali = 
		                        oggettoLottoInizio.getInizio().getListaIncarichiProfessionaliArray();
		                      
		                      UtilitySITAT.gestioneIncarichiProfessionali(result,
		                      		codFiscaleStazAppaltante, credenzialiUtente, faseEsecuz.intValue(), codiceGara, 
		                      		codiceLotto, esisteFaseInizioContratto, arrayIncarichiProfessionali,
		                          this.sqlManager, this.geneManager, this.rupManager, isAAQ, isE1, isS2, isExSottosoglia);
		                    	
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
		                      UtilitySITAT.aggiungiMsgSchedaA(hmResponseLotti, cigLotto, true, "Fase 'Inizio Contratto' aggiornata");
		                    	
		                    } else {
		                      // Caso in cui in base dati esiste gia' la fase di inizio contratto per il lotto
		                      // ed il flag di sovrascrittura dei dati e' valorizzato a false.
		
		                      StringBuilder strLog = new StringBuilder(credenzialiUtente.getPrefissoLogger());
		                      strLog.append(" L'inizio contratto del lotto con CIG=");
		                      strLog.append(cigLotto);
		                      strLog.append(" non e' stato importato perche' la fase gia' esiste nella base dati " +
		                          "e non la si vuole sovrascrivere.");
		                      logger.info(strLog.toString());
		                      
		                      numeroLottiNonImportati++;
		                      
		                      UtilitySITAT.aggiungiMsgSchedaA(hmResponseLotti, cigLotto, false,
		                      		StringUtils.replace(ConfigManager.getValore("error.inizio.esistente"),
		                      				"{0}", cigLotto));
		                    }
		                  } else {
		                    StringBuilder strLog =
		                      new StringBuilder(credenzialiUtente.getPrefissoLogger());
		                    strLog.append(" Il contratto del lotto con CIG=");
		                    strLog.append(cigLotto);
		                    strLog.append(" non e' stato importato perche' non ha superato i controlli preliminari"
		                    		+ " della fase del contratto. Nel caso specifico la condizione (!isSAQ && isEA) "
		                    		+ "non risulta verificata.");
		                    logger.info(strLog.toString());                      
		
		                    numeroLottiNonImportati++;
		                    
		                    UtilitySITAT.aggiungiMsgSchedaA(hmResponseLotti, cigLotto, false,
		                    		StringUtils.replace(ConfigManager.getValore("error.inizio.noVisibile"),
		                    				"{0}", cigLotto));
		                  }
		                } else {
		                  // Controlli preliminari non superati
		                	StringBuilder strLog = new StringBuilder(credenzialiUtente.getPrefissoLogger());
		                	strLog.append("Il lotto con CIG='");
		                  strLog.append(cigLotto);
		                  strLog.append("' non ha la fase comunicazione esito e l'aggiudicazione oppure non ha "
		                  		+ "l'adesione ad un accordo quadro. Impossibile importare l'inizio contratto." );
		                  logger.error(strLog);
		
		                  numeroLottiNonImportati++;
		                  
		                  UtilitySITAT.aggiungiMsgSchedaA(hmResponseLotti, cigLotto, false,
		                  		StringUtils.replace(ConfigManager.getValore("error.inizio.noFlussiPrecedenti"),
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
            		
              } // Chiusura ciclo for sui lotti.

              UtilitySITAT.preparaRisultatoMessaggio(result, sovrascrivereDatiEsistenti, hmResponseLotti,
              		numeroLottiImportati, numeroLottiNonImportati, numeroLottiAggiornati);
            }
          }
        } else {
          // E' stato inviato un messaggio di test.
          StringBuilder strLog = new StringBuilder(credenzialiUtente.getPrefissoLogger());
          strLog.append(" E' stato inviato un messaggio di test. Tale messaggio non e' stato elaborato.'\n");
          strLog.append("Messaggio: ");
          strLog.append(inizio.toString());
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
      logger.debug("istanziaInizio: fine metodo");
      
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
