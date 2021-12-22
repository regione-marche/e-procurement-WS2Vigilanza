package it.eldasoft.sil.vigilanza.bl;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.db.datautils.DataColumn;
import it.eldasoft.gene.db.datautils.DataColumnContainer;
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.sil.vigilanza.beans.ConclusioneType;
import it.eldasoft.sil.vigilanza.beans.LottoConclusioneType;
import it.eldasoft.sil.vigilanza.beans.RichiestaSincronaIstanzaConclusioneDocument;
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
 * Classe per l'import dei dati di conclusione dei lotti della gara.
 * 
 * @author Luca.Giacomazzo
 */
public class IstanzaConclusioneManager {

  private static Logger logger = Logger.getLogger(IstanzaConclusioneManager.class);
  
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
   * Metodo per la gestione della conclusione dei contratti.
   * 
   * @param login LoginType
   * @param conclusione IstanzaOggettoType
   * @return Ritorna l'oggetto ResponseType con l'esito dell'operazione
   * @throws XmlException 
   * @throws GestoreException 
   * @throws SQLException 
   * @throws Throwable
   */
  public ResponseType istanziaConclusione(LoginType login, IstanzaOggettoType conclusione)
      throws XmlException, GestoreException, SQLException, Throwable {
    ResponseType result = null;
    
    if (logger.isDebugEnabled()) {
      logger.debug("istanziaConclusione: inizio metodo");
      
      logger.debug("XML : " + conclusione.getOggettoXML());
    }
  
    // Codice fiscale della Stazione appaltante
    String codFiscaleStazAppaltante = conclusione.getTestata().getCFEIN();
    
    boolean sovrascrivereDatiEsistenti = false;
    if (conclusione.getTestata().getSOVRASCR() != null) {
      sovrascrivereDatiEsistenti = conclusione.getTestata().getSOVRASCR().booleanValue(); 
    }
    
    // Verifica di login, password e determinazione della S.A.
    HashMap<String, Object> hm = this.credenzialiManager.verificaCredenziali(login, codFiscaleStazAppaltante);
    result = (ResponseType) hm.get("RESULT");
    
    if (result.isSuccess()) {
      Credenziale credenzialiUtente = (Credenziale) hm.get("USER");
      String xmlConclusione = conclusione.getOggettoXML();
      
      try {
        RichiestaSincronaIstanzaConclusioneDocument istanzaConclusioneDocument =
          RichiestaSincronaIstanzaConclusioneDocument.Factory.parse(xmlConclusione);

        // si esegue il controllo sintattico del messaggio
        XmlOptions validationOptions = new XmlOptions();
        ArrayList<XmlValidationError> validationErrors = new ArrayList<XmlValidationError>();
        validationOptions.setErrorListener(validationErrors);
        boolean isSintassiXmlOK = istanzaConclusioneDocument.validate(validationOptions);

        boolean isMessaggioDiTest = 
          istanzaConclusioneDocument.getRichiestaSincronaIstanzaConclusione().isSetTest()
            && istanzaConclusioneDocument.getRichiestaSincronaIstanzaConclusione().getTest();
        
        if (! isMessaggioDiTest) {
        
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
            LottoConclusioneType[] arrayLottoConclusione =
              istanzaConclusioneDocument.getRichiestaSincronaIstanzaConclusione().getListaLottiConclusioniArray();
            
            if (arrayLottoConclusione != null && arrayLottoConclusione.length > 0) {
              // HashMap per caricare gli oggetti ResponseLottoType per ciascun lotto, con CIG come chiave della hashMap
              HashMap<String, ResponseLottoType> hmResponseLotti = new HashMap<String, ResponseLottoType>();
              
              StringBuilder strQueryCig = new StringBuilder("'");
              for (int concl = 0; concl < arrayLottoConclusione.length && result.isSuccess(); concl++) {
                LottoConclusioneType oggettoLottoConclusione = arrayLottoConclusione[concl];
                String cigLotto = oggettoLottoConclusione.getW3CIG();

                strQueryCig.append(cigLotto);
                if (concl + 1 < arrayLottoConclusione.length)
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
              	if (listaCodGara.size() !=  arrayLottoConclusione.length) {
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
              
              for (int conclus = 0; conclus < arrayLottoConclusione.length && result.isSuccess(); conclus++) {
                LottoConclusioneType oggettoLottoConclusione = arrayLottoConclusione[conclus];
                String cigLotto = oggettoLottoConclusione.getW3CIG();
                
                HashMap<String, Long> hashM = UtilitySITAT.getCodGaraCodLottByCIG(cigLotto, this.sqlManager);
                Long codiceGara = hashM.get("CODGARA");
                Long codiceLotto = hashM.get("CODLOTT");
                //verifica monitoraggio multilotto - verifico se il campo CIG_MASTER_ML è valorizzato
                String cigMaster = (String) this.sqlManager.getObject("select CIG_MASTER_ML from W9LOTT where CODGARA=? and CODLOTT=?", new Object[] { codiceGara, codiceLotto });
                if (! UtilitySITAT.isLottoRiaggiudicato(codiceGara, codiceLotto, this.sqlManager) && 
                		(cigMaster == null || cigMaster.trim().length() == 0)) {
                	
	            		if (!UtilitySITAT.isUtenteAmministratore(credenzialiUtente)) {
	            			if (!UtilitySITAT.isUtenteRupDelLotto(cigLotto, credenzialiUtente, this.sqlManager)) {
	            				logger.error(credenzialiUtente.getPrefissoLogger() + "L'utente non e' RUP del lotto con CIG=" + cigLotto
	            						+ ", oppure nella gara e/o nel lotto non e' stato indicato il RUP");
	
	                		throw new WSVigilanzaException("Le credenziali fornite non coincidono con quelle del RUP indicato");
	            			}
	            		}
	
	                // Controlli preliminari su gara e su lotto.
	                boolean isS2 = UtilitySITAT.isS2(codiceGara, codiceLotto, this.sqlManager);
	                boolean isE1 = UtilitySITAT.isE1(codiceGara, codiceLotto, this.sqlManager);
	                boolean isORD = UtilitySITAT.isOrd(codiceGara, this.sqlManager);
	                
	                Long faseEsecuz = null;
	                if (isS2 && !isE1 && isORD) {
	                  faseEsecuz = new Long(CostantiWSW9.CONCLUSIONE_CONTRATTO_SOPRA_SOGLIA);
	                } else {
	                  faseEsecuz = new Long(CostantiWSW9.FASE_SEMPLIFICATA_CONCLUSIONE_CONTRATTO);
	                }
	                
	                if (UtilitySITAT.isFaseAttiva(faseEsecuz, this.sqlManager)) {
		                boolean esisteFaseConclusione = UtilitySITAT.existsFase(codiceGara, codiceLotto, new Long(1),
		                    faseEsecuz.intValue(), this.sqlManager);
		                if (esisteFaseConclusione || UtilitySITAT.isFaseVisualizzabile(codiceGara, codiceLotto, faseEsecuz.intValue(), this.sqlManager)) {
		                  if (esisteFaseConclusione || UtilitySITAT.isFaseAbilitata(codiceGara, codiceLotto,new Long(1),  faseEsecuz.intValue(), this.sqlManager)) {
		                    ConclusioneType lottoConclusione = oggettoLottoConclusione.getConclusione();
		                      
		                    // Record in W9FASI.
		                    UtilitySITAT.istanziaFase(this.sqlManager, codiceGara, codiceLotto, faseEsecuz, new Long(1));
		                  	
		                    DataColumn codGaraConclusione = new DataColumn("W9CONC.CODGARA",
		                        new JdbcParametro(JdbcParametro.TIPO_NUMERICO, codiceGara));
		                    DataColumn codLottConclusione = new DataColumn("W9CONC.CODLOTT",
		                        new JdbcParametro(JdbcParametro.TIPO_NUMERICO, codiceLotto));
		                    DataColumn numConclusione = new DataColumn("W9CONC.NUM_CONC", 
		                        new JdbcParametro(JdbcParametro.TIPO_NUMERICO, new Long(1)));
		                    DataColumn numAppa = new DataColumn("W9CONC.NUM_APPA", 
		                        new JdbcParametro(JdbcParametro.TIPO_NUMERICO, new Long(1)));
		                    
		                    DataColumnContainer dccConclusione = new DataColumnContainer(new DataColumn[] { 
		                        codGaraConclusione, codLottConclusione, numConclusione, numAppa });
		
		                    if (lottoConclusione.isSetW3CSINTANTIC()) {
		                      dccConclusione.addColumn("W9CONC.INTANTIC", 
		                          new JdbcParametro(JdbcParametro.TIPO_TESTO,
		                              lottoConclusione.getW3CSINTANTIC() ? "1" : "2"));
		                    }
		                    if (lottoConclusione.isSetW3IDMOTI1()) {
		                      dccConclusione.addColumn("W9CONC.ID_MOTIVO_INTERR", 
		                          new JdbcParametro(JdbcParametro.TIPO_NUMERICO,
		                              new Long(lottoConclusione.getW3IDMOTI1().toString())));
		                    }
		                    if (lottoConclusione.isSetW3IDMOTI2()) {
		                      dccConclusione.addColumn("W9CONC.ID_MOTIVO_RISOL", 
		                          new JdbcParametro(JdbcParametro.TIPO_NUMERICO,
		                              new Long(lottoConclusione.getW3IDMOTI2().toString())));
		                    }
		                    if (lottoConclusione.isSetW3DATARIS()) {
		                      dccConclusione.addColumn("W9CONC.DATA_RISOLUZIONE", 
		                          new JdbcParametro(JdbcParametro.TIPO_DATA,
		                              lottoConclusione.getW3DATARIS().getTime()));
		                    }
		                    if (lottoConclusione.isSetW3FLAGONE()) {
		                      dccConclusione.addColumn("W9CONC.FLAG_ONERI", 
		                        new JdbcParametro(JdbcParametro.TIPO_NUMERICO,
		                            new Long(lottoConclusione.getW3FLAGONE().toString())));
		                    }
		                    if (lottoConclusione.isSetW3ONERIRI()) {
		                      dccConclusione.addColumn("W9CONC.ONERI_RISOLUZIONE", 
		                        new JdbcParametro(JdbcParametro.TIPO_DECIMALE,
		                          lottoConclusione.getW3ONERIRI()));
		                    }
		                    if (lottoConclusione.isSetW3FLAGPOL()) {
		                      dccConclusione.addColumn("W9CONC.FLAG_POLIZZA", 
		                        new JdbcParametro(JdbcParametro.TIPO_TESTO,
		                          lottoConclusione.getW3FLAGPOL() ? "1" : "2"));
		                    }
		                    if (lottoConclusione.isSetW3DATAULT()) {
		                      dccConclusione.addColumn("W9CONC.DATA_ULTIMAZIONE", 
		                        new JdbcParametro(JdbcParametro.TIPO_DATA,
		                          lottoConclusione.getW3DATAULT().getTime()));
		                    }
		                    if (lottoConclusione.isSetW3NUMINFO()) {
		                      dccConclusione.addColumn("W9CONC.NUM_INFORTUNI",
		                        new JdbcParametro(JdbcParametro.TIPO_NUMERICO,
		                          new Long(lottoConclusione.getW3NUMINFO())));
		                    }
		                    if (lottoConclusione.isSetW3NUMPERM()) {
		                      dccConclusione.addColumn("W9CONC.NUM_INF_PERM",
		                        new JdbcParametro(JdbcParametro.TIPO_NUMERICO,
		                          new Long(lottoConclusione.getW3NUMPERM())));
		                    }
		                    if (lottoConclusione.isSetW3NUMMORT()) {
		                      dccConclusione.addColumn("W9CONC.NUM_INF_MORT",
		                        new JdbcParametro(JdbcParametro.TIPO_NUMERICO,
		                          new Long(lottoConclusione.getW3NUMMORT())));
		                    }
		                    if (lottoConclusione.isSetW9COGGPROR()) {
		                      dccConclusione.addColumn("W9CONC.NUM_GIORNI_PROROGA",
		                        new JdbcParametro(JdbcParametro.TIPO_NUMERICO,
		                          new Long(lottoConclusione.getW9COGGPROR())));
		                    }
		                    if (lottoConclusione.isSetW9COTERCONT()) {
		                    	dccConclusione.addColumn("W9CONC.TERMINE_CONTRATTO_ULT", 
		                    			new JdbcParametro(JdbcParametro.TIPO_DATA,
		                    					lottoConclusione.getW9COTERCONT().getTime()));
		                    }
		                    if (lottoConclusione.isSetW9CODVERCON()) {
		                    	dccConclusione.addColumn("W9CONC.DATA_VERBALE_CONSEGNA", 
		                    			new JdbcParametro(JdbcParametro.TIPO_DATA,
		                    					lottoConclusione.getW9CODVERCON().getTime()));
		                    }
		                    if (lottoConclusione.isSetW9COORELAVO()) {
		                    	dccConclusione.addColumn("W9CONC.ORE_LAVORATE", 
		                    			new JdbcParametro(JdbcParametro.TIPO_NUMERICO,
		                    					new Long(lottoConclusione.getW9COORELAVO())));
		                    }
		
		                    if (!esisteFaseConclusione) {
		                      dccConclusione.insert("W9CONC", this.sqlManager);
		                      
		                      if (UtilitySITAT.isScritturaNoteAvvisiAttiva()) {
		                      	String codiceCIG = UtilitySITAT.getCIGLotto(codiceGara, codiceLotto, sqlManager);
		                     		UtilitySITAT.insertNoteAvvisi("W9LOTT", codiceGara.toString(), codiceLotto.toString(), null, null, null,
		                   				"CIG " + codiceCIG + ": inserita fase 'Conclusione contratto'", null, this.sqlManager); 
		                      }
		                      
		                      numeroLottiImportati++;
		                      UtilitySITAT.aggiungiMsgSchedaA(hmResponseLotti, cigLotto, true, null);
		
		                    } else if (esisteFaseConclusione && sovrascrivereDatiEsistenti) {
		
		                    	DataColumnContainer dccConclusioneDB = new DataColumnContainer(this.sqlManager, "W9CONC", 
		                    			"select INTANTIC, ID_MOTIVO_INTERR, ID_MOTIVO_RISOL, DATA_RISOLUZIONE, FLAG_ONERI, ONERI_RISOLUZIONE, "
		                    			 + "FLAG_POLIZZA, DATA_ULTIMAZIONE, NUM_INFORTUNI, NUM_INF_PERM, NUM_INF_MORT, NUM_GIORNI_PROROGA, "
		                    			 + "DATA_VERBALE_CONSEGNA, TERMINE_CONTRATTO_ULT, ORE_LAVORATE, "
		              						 + "CODGARA, CODLOTT, NUM_CONC, NUM_APPA "
		                    		 + " from W9CONC where CODGARA=? and CODLOTT=? and NUM_CONC=1 and NUM_APPA=1", new Object[] { codiceGara, codiceLotto });
		                    	
		                     	Iterator<Entry<String, DataColumn>> iterInizioDB = dccConclusioneDB.getColonne().entrySet().iterator();
		                      while (iterInizioDB.hasNext()) {
		                       	Entry<String, DataColumn> entry = iterInizioDB.next(); 
		                        String nomeCampo = entry.getKey();
		                        if (dccConclusione.isColumn(nomeCampo)) {
		                        	dccConclusioneDB.setValue(nomeCampo, dccConclusione.getColumn(nomeCampo).getValue());
		                        }
		                      }
		                    	
		                      /*if (lottoConclusione.isSetW3CSINTANTIC()) {
		                        dccConclusioneDB.setValue("W9CONC.INTANTIC", 
		                            new JdbcParametro(JdbcParametro.TIPO_TESTO,
		                                lottoConclusione.getW3CSINTANTIC() ? "1" : "2"));
		                      }
		                      if (lottoConclusione.isSetW3IDMOTI1()) {
		                        dccConclusioneDB.setValue("W9CONC.ID_MOTIVO_INTERR", 
		                            new JdbcParametro(JdbcParametro.TIPO_NUMERICO,
		                                new Long(lottoConclusione.getW3IDMOTI1().toString())));
		                      }
		                      if (lottoConclusione.isSetW3IDMOTI2()) {
		                        dccConclusioneDB.setValue("W9CONC.ID_MOTIVO_RISOL", 
		                            new JdbcParametro(JdbcParametro.TIPO_NUMERICO,
		                                new Long(lottoConclusione.getW3IDMOTI2().toString())));
		                      }
		                      if (lottoConclusione.isSetW3DATARIS()) {
		                        dccConclusioneDB.setValue("W9CONC.DATA_RISOLUZIONE", 
		                            new JdbcParametro(JdbcParametro.TIPO_DATA,
		                                lottoConclusione.getW3DATARIS().getTime()));
		                      }
		                      if (lottoConclusione.isSetW3FLAGONE()) {
		                        dccConclusioneDB.setValue("W9CONC.FLAG_ONERI", 
		                          new JdbcParametro(JdbcParametro.TIPO_NUMERICO,
		                              new Long(lottoConclusione.getW3FLAGONE().toString())));
		                      }
		                      if (lottoConclusione.isSetW3ONERIRI()) {
		                        dccConclusioneDB.setValue("W9CONC.ONERI_RISOLUZIONE", 
		                          new JdbcParametro(JdbcParametro.TIPO_DECIMALE,
		                            lottoConclusione.getW3ONERIRI()));
		                      }
		                      if (lottoConclusione.isSetW3FLAGPOL()) {
		                        dccConclusioneDB.setValue("W9CONC.FLAG_POLIZZA", 
		                          new JdbcParametro(JdbcParametro.TIPO_TESTO,
		                            lottoConclusione.getW3FLAGPOL() ? "1" : "2"));
		                      }
		                      if (lottoConclusione.isSetW3DATAULT()) {
		                        dccConclusioneDB.setValue("W9CONC.DATA_ULTIMAZIONE", 
		                          new JdbcParametro(JdbcParametro.TIPO_DATA,
		                            lottoConclusione.getW3DATAULT().getTime()));
		                      }
		                      if (lottoConclusione.isSetW3NUMINFO()) {
		                        dccConclusioneDB.setValue("W9CONC.NUM_INFORTUNI",
		                          new JdbcParametro(JdbcParametro.TIPO_NUMERICO,
		                            new Long(lottoConclusione.getW3NUMINFO())));
		                      }
		                      if (lottoConclusione.isSetW3NUMPERM()) {
		                        dccConclusioneDB.setValue("W9CONC.NUM_INF_PERM",
		                          new JdbcParametro(JdbcParametro.TIPO_NUMERICO,
		                            new Long(lottoConclusione.getW3NUMPERM())));
		                      }
		                      if (lottoConclusione.isSetW3NUMMORT()) {
		                        dccConclusioneDB.setValue("W9CONC.NUM_INF_MORT",
		                          new JdbcParametro(JdbcParametro.TIPO_NUMERICO,
		                            new Long(lottoConclusione.getW3NUMMORT())));
		                      }
		                      if (lottoConclusione.isSetW9COGGPROR()) {
		                        dccConclusioneDB.setValue("W9CONC.NUM_GIORNI_PROROGA",
		                          new JdbcParametro(JdbcParametro.TIPO_NUMERICO,
		                            new Long(lottoConclusione.getW9COGGPROR())));
		                      }
		                      if (lottoConclusione.isSetW9COTERCONT()) {
		                      	dccConclusioneDB.setValue("W9CONC.DATA_VERBALE_CONSEGNA", 
		                      			new JdbcParametro(JdbcParametro.TIPO_DATA,
		                      					lottoConclusione.getW9COTERCONT().getTime()));
		                      }
		                      if (lottoConclusione.isSetW9CODVERCON()) {
		                      	dccConclusioneDB.setValue("W9CONC.TERMINE_CONTRATTO_ULT", 
		                      			new JdbcParametro(JdbcParametro.TIPO_DATA,
		                      					lottoConclusione.getW9COTERCONT().getTime()));
		                      }*/
		
		                    	if (dccConclusioneDB.isModifiedTable("W9CONC")) {
		                    		dccConclusioneDB.getColumn("W9CONC.CODGARA").setChiave(true);
		                    		dccConclusioneDB.getColumn("W9CONC.CODLOTT").setChiave(true);
		                    		dccConclusioneDB.getColumn("W9CONC.NUM_CONC").setChiave(true);
		                    		dccConclusioneDB.update("W9CONC", this.sqlManager);
		                    	}
		                    	
		                      if (UtilitySITAT.isScritturaNoteAvvisiAttiva()) {
		                      	String codiceCIG = UtilitySITAT.getCIGLotto(codiceGara, codiceLotto, sqlManager);
		                    		UtilitySITAT.insertNoteAvvisi("W9LOTT", codiceGara.toString(), codiceLotto.toString(), null, null, null,
		                    				"CIG " + codiceCIG + ": inserita fase 'Conclusione contratto'", null, this.sqlManager);
		                      }
		                    	
		                    	numeroLottiAggiornati++;
		                      UtilitySITAT.aggiungiMsgSchedaA(hmResponseLotti, cigLotto, true, null);
		                    	
		                    } else {
		                      // Caso in cui in base dati esiste gia' la fase contratto per il lotto
		                      // ed il flag di sovrascrittura dei dati e' valorizzato a false.
		                      
		                      StringBuilder strLog = new StringBuilder(credenzialiUtente.getPrefissoLogger());
		                      strLog.append(" La conclusione del contratto del lotto con CIG=");
		                      strLog.append(cigLotto);
		                      strLog.append(" non e' stato importato perche' la fase gia' esiste nella base dati " +
		                          "e non la si vuole sovrascrivere.");
		                      logger.info(strLog.toString());
		                      
		                      numeroLottiNonImportati++;
		                      
		                      UtilitySITAT.aggiungiMsgSchedaA(hmResponseLotti, cigLotto, false,
		                      		StringUtils.replace(ConfigManager.getValore("error.conclusione.esistente"),
		                      				"{0}", cigLotto));	                        
		                    }
		                  } else {
		                    StringBuilder strLog =
		                      new StringBuilder(credenzialiUtente.getPrefissoLogger());
		                    strLog.append(" I controlli preliminari non sono stati superati. La conclusione del lotto con CIG='");
		                    strLog.append(cigLotto);
		                    strLog.append(" non e' stato importato perche' non ha superato i controlli preliminari" +
		                    		" della fase del contratto. Nel caso specifico la condizione (isEA) non risulta verificata.");
		                    logger.info(strLog.toString());
		                    
		                    numeroLottiNonImportati++;
		                    
		                    UtilitySITAT.aggiungiMsgSchedaA(hmResponseLotti, cigLotto, false,
		
		                    		StringUtils.replace(ConfigManager.getValore("error.conclusione.noVisibile"),
		                    				"{0}", cigLotto));
		                  }
		                } else {
		                  // Controlli preliminari non superati
		                  StringBuilder strLog =
		                    new StringBuilder(credenzialiUtente.getPrefissoLogger());
		                  strLog.append(" Il lotto con CIG='");
		                  strLog.append(cigLotto);
		                  strLog.append("' non ha la fase di inizio contratto. Impossibile importare la conclusione del contratto." );
		                  logger.error(strLog);
		
		                  numeroLottiNonImportati++;
		                  
		                  UtilitySITAT.aggiungiMsgSchedaA(hmResponseLotti, cigLotto, false,
		                  		StringUtils.replace(ConfigManager.getValore("error.conclusione.noFlussiPrecedenti"),
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
              } // Chiusura ciclo sui lotti presenti nel XML.
              
							UtilitySITAT.preparaRisultatoMessaggio(result, sovrascrivereDatiEsistenti, hmResponseLotti,
									numeroLottiImportati, numeroLottiNonImportati, numeroLottiAggiornati);
            }
          }
        } else {
          // E' stato inviato un messaggio di test.
          StringBuilder strLog = new StringBuilder(credenzialiUtente.getPrefissoLogger());
          strLog.append(" E' stato inviato un messaggio di test. Tale messaggio non e' stato elaborato.'\n");
          strLog.append("Messaggio: ");
          strLog.append(conclusione.toString());
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
      logger.debug("istanziaConclusione: fine metodo");
      
      logger.debug("Response : " + UtilitySITAT.messaggioLogEsteso(result));
    }
    
    // MODIFICA TEMPORANEA -- DA CANCELLARE ---------------
    result.setError(UtilitySITAT.messaggioEsteso(result));
    // ----------------------------------------------------
    
    return result;
  }

}
