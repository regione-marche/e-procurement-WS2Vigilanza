package it.eldasoft.sil.vigilanza.bl;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.db.datautils.DataColumn;
import it.eldasoft.gene.db.datautils.DataColumnContainer;
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.sil.vigilanza.beans.LottoSospensioneType;
import it.eldasoft.sil.vigilanza.beans.RichiestaSincronaIstanzaSospensioneDocument;
import it.eldasoft.sil.vigilanza.beans.SospensioneType;
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
 * Classe per l'import dei dati della sospensione dei lotti della gara.
 * 
 * @author Luca.Giacomazzo
 */
public class IstanzaSospensioneManager {

  private static Logger logger = Logger.getLogger(IstanzaSospensioneManager.class);
  
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
   * Metodo per la gestione della sospensione dei contratti.
   * 
   * @param login LoginType
   * @param sospensione IstanzaOggettoType
   * @return Ritorna l'oggetto ResponseType con l'esito dell'operazione
   * @throws XmlException 
   * @throws GestoreException 
   * @throws SQLException 
   * @throws Throwable
   */
  public ResponseType istanziaSospensione(LoginType login, IstanzaOggettoType sospensione)
      throws XmlException, GestoreException, SQLException, Throwable {
    ResponseType result = null;
    
    if (logger.isDebugEnabled()) {
      logger.debug("istanziaSospensione: inizio metodo");
      
      logger.debug("XML : " + sospensione.getOggettoXML());
    }
  
    // Codice fiscale della Stazione appaltante
    String codFiscaleStazAppaltante = sospensione.getTestata().getCFEIN();
    
    boolean sovrascrivereDatiEsistenti = false;
    if (sospensione.getTestata().getSOVRASCR() != null) {
      sovrascrivereDatiEsistenti = sospensione.getTestata().getSOVRASCR().booleanValue(); 
    }
    
    // Verifica di login, password e determinazione della S.A.
    HashMap<String, Object> hm = this.credenzialiManager.verificaCredenziali(login, codFiscaleStazAppaltante);
    result = (ResponseType) hm.get("RESULT");
    
    if (result.isSuccess()) {
      Credenziale credenzialiUtente = (Credenziale) hm.get("USER");
      String xmlSospensione = sospensione.getOggettoXML();
      
      try {
        RichiestaSincronaIstanzaSospensioneDocument istanzaSospensioneDocument =
          RichiestaSincronaIstanzaSospensioneDocument.Factory.parse(xmlSospensione);

        boolean isMessaggioDiTest = 
          istanzaSospensioneDocument.getRichiestaSincronaIstanzaSospensione().isSetTest()
            && istanzaSospensioneDocument.getRichiestaSincronaIstanzaSospensione().getTest();
        
        if (! isMessaggioDiTest) {
        
          // si esegue il controllo sintattico del messaggio
          XmlOptions validationOptions = new XmlOptions();
          ArrayList<XmlValidationError> validationErrors = new ArrayList<XmlValidationError>();
          validationOptions.setErrorListener(validationErrors);
          boolean isSintassiXmlOK = istanzaSospensioneDocument.validate(validationOptions);
  
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
            LottoSospensioneType[] arrayLottoSosp = 
              istanzaSospensioneDocument.getRichiestaSincronaIstanzaSospensione().getListaLottiSospensioniArray();
            
            if (arrayLottoSosp != null && arrayLottoSosp.length > 0) {
              // HashMap per caricare gli oggetti ResponseLottoType per ciascun lotto, con CIG come chiave della hashMap
              HashMap<String, ResponseLottoType> hmResponseLotti = new HashMap<String, ResponseLottoType>();
              
              StringBuilder strQueryCig = new StringBuilder("'");
              for (int contr = 0; contr < arrayLottoSosp.length && result.isSuccess(); contr++) {
              	LottoSospensioneType oggettoSospensioneInizio = arrayLottoSosp[contr];
                String cigLotto = oggettoSospensioneInizio.getW3CIG();

                strQueryCig.append(cigLotto);
                if (contr + 1 < arrayLottoSosp.length)
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
              	if (listaCodGara.size() !=  arrayLottoSosp.length) {
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
              
              for (int iniz = 0; iniz < arrayLottoSosp.length && result.isSuccess(); iniz++) {
                LottoSospensioneType oggettoLottoSospensione = arrayLottoSosp[iniz];
                String cigLotto = oggettoLottoSospensione.getW3CIG();
                
                HashMap<String, Long> hashM = UtilitySITAT.getCodGaraCodLottByCIG(cigLotto, this.sqlManager);
                Long codiceGara = hashM.get("CODGARA");
                Long codiceLotto = hashM.get("CODLOTT");
                //verifica monitoraggio multilotto - verifico se il campo CIG_MASTER_ML è valorizzato
                String cigMaster = (String) this.sqlManager.getObject("select CIG_MASTER_ML from W9LOTT where CODGARA=? and CODLOTT=?", new Object[] { codiceGara, codiceLotto });
                
                if (!UtilitySITAT.isLottoRiaggiudicato(codiceGara, codiceLotto, this.sqlManager) &&
                		(cigMaster == null || cigMaster.trim().length() == 0)) {
	            		if (!UtilitySITAT.isUtenteAmministratore(credenzialiUtente)) {
	            			if (!UtilitySITAT.isUtenteRupDelLotto(cigLotto, credenzialiUtente, this.sqlManager)) {
	            				logger.error(credenzialiUtente.getPrefissoLogger() + "L'utente non e' RUP del lotto con CIG=" + cigLotto
	            						+ ", oppure nella gara e/o nel lotto non e' stato indicato il RUP");
	
	                		throw new WSVigilanzaException("Le credenziali fornite non coincidono con quelle del RUP indicato");
	            			}
	            		}
	
	            		//if (result.isSuccess()) {
	              
	                // Controlli preliminari su gara e su lotto.
	                Long faseEsecuz = new Long(CostantiWSW9.SOSPENSIONE_CONTRATTO);
	                SospensioneType[] arraySospensioni = oggettoLottoSospensione.getListaSospensioniArray();
	                
	                if (arraySospensioni != null && arraySospensioni.length > 0) {
	                  int numeroSospensioniImportate = 0;
	                  int numeroSospensioniNonImportate = 0;
	                  int numeroSospensioniAggiornate = 0;
	                  
	                  for (int sosp=0; sosp < arraySospensioni.length; sosp++) {
	                    SospensioneType lottoSospensione = arraySospensioni[sosp];
	                    
	                    // Controllo esistenza della fase: se no, si esegue l'insert di tutti dati, 
	                    // se si, si esegue l'update dei record
	                    boolean esisteFaseSospensione = UtilitySITAT.existsFase(codiceGara, codiceLotto, new Long(1),
	                        faseEsecuz.intValue(), new Long(lottoSospensione.getW3NUM21()), this.sqlManager);
	                    	
	                    // Record in W9FASI.
	                    UtilitySITAT.istanziaFase(this.sqlManager, codiceGara, codiceLotto, faseEsecuz,
	                  				new Long(lottoSospensione.getW3NUM21()));
	                
	                    if (esisteFaseSospensione || UtilitySITAT.isFaseAbilitata(codiceGara, codiceLotto, new Long(1), faseEsecuz.intValue(), this.sqlManager)) {
	                    	if (esisteFaseSospensione || UtilitySITAT.isFaseVisualizzabile(codiceGara, codiceLotto, faseEsecuz.intValue(), this.sqlManager)) {
	                        DataColumn codGaraSospen = new DataColumn("W9SOSP.CODGARA", 
	                            new JdbcParametro(JdbcParametro.TIPO_NUMERICO, codiceGara));
	                        DataColumn codLottSospen = new DataColumn("W9SOSP.CODLOTT", 
	                            new JdbcParametro(JdbcParametro.TIPO_NUMERICO, codiceLotto));
	                        DataColumn numSospen = new DataColumn("W9SOSP.NUM_SOSP",
	                            new JdbcParametro(JdbcParametro.TIPO_NUMERICO, 
	                                new Long(lottoSospensione.getW3NUM21())));
	                        DataColumn numAppa = new DataColumn("W9SOSP.NUM_APPA",
	                            new JdbcParametro(JdbcParametro.TIPO_NUMERICO, new Long(1)));
	                        DataColumnContainer dccSospensione = new DataColumnContainer(new DataColumn[] { 
	                            codGaraSospen, codLottSospen, numSospen, numAppa } );
	                        
	                        if (lottoSospensione.isSetW3DVERBSO()) {
	                          dccSospensione.addColumn("W9SOSP.DATA_VERB_SOSP", new JdbcParametro(
	                          		JdbcParametro.TIPO_DATA, lottoSospensione.getW3DVERBSO().getTime()));
	                        }
	                        if (lottoSospensione.isSetW3DVERBRI()) {
	                        	dccSospensione.addColumn("W9SOSP.DATA_VERB_RIPR", new JdbcParametro(
	                        		JdbcParametro.TIPO_DATA, lottoSospensione.getW3DVERBRI().getTime()));
	                        }
	                        if (lottoSospensione.isSetW3IDMOTI4()) {
	                        dccSospensione.addColumn("W9SOSP.ID_MOTIVO_SOSP", new JdbcParametro(
	                        		JdbcParametro.TIPO_NUMERICO, new Long(lottoSospensione.getW3IDMOTI4().toString())));
	                        }
	                        if (lottoSospensione.isSetW3FLAGSUP()) {
	                          dccSospensione.addColumn("W9SOSP.FLAG_SUPERO_TEMPO", new JdbcParametro(
	                          		JdbcParametro.TIPO_TESTO, lottoSospensione.getW3FLAGSUP() ? "1" : "2"));
	                        }
	                        if (lottoSospensione.isSetW3RISERVE()) {
	                          dccSospensione.addColumn("W9SOSP.FLAG_RISERVE", new JdbcParametro(
	                          		JdbcParametro.TIPO_TESTO, lottoSospensione.getW3RISERVE() ? "1" : "2"));
	                        }
	                        if (lottoSospensione.isSetW3FLAGVER()) {
	                          dccSospensione.addColumn("W9SOSP.FLAG_VERBALE", new JdbcParametro(
	                          		JdbcParametro.TIPO_TESTO, lottoSospensione.getW3FLAGVER() ? "1" : "2"));
	                        }
	
	                        if (!esisteFaseSospensione) {
	                          dccSospensione.insert("W9SOSP", this.sqlManager);
	                          
	                          if (UtilitySITAT.isScritturaNoteAvvisiAttiva()) {
	                          	String codiceCIG = UtilitySITAT.getCIGLotto(codiceGara, codiceLotto, sqlManager);
	                           	UtilitySITAT.insertNoteAvvisi("W9LOTT", codiceGara.toString(), codiceLotto.toString(), null, null, null,
	                           			"CIG " + codiceCIG + ": inserita fase 'Sospensione contratto' n." + lottoSospensione.getW3NUM21(),
	                           			null, this.sqlManager);
	                          }
	                          
	                          numeroSospensioniImportate++;
	                          UtilitySITAT.aggiungiMsgSchedaB(hmResponseLotti, cigLotto, lottoSospensione.getW3NUM21(), 
	                         		 true, "Fase 'Sospensione' importata");
	                          
	                        } else if (esisteFaseSospensione && sovrascrivereDatiEsistenti) {
	                        	
	                        	DataColumnContainer dccSospensioneDB = new DataColumnContainer(this.sqlManager, "W9SOSP",
	                        			"select DATA_VERB_SOSP, DATA_VERB_RIPR, ID_MOTIVO_SOSP, FLAG_SUPERO_TEMPO, FLAG_RISERVE, FLAG_VERBALE, "
	                        					 + "CODGARA, CODLOTT, NUM_SOSP, NUM_APPA "
	                        		 + " from W9SOSP where CODGARA=? and CODLOTT=? and NUM_SOSP=? and NUM_APPA=1",
	                        			new Object[] { codiceGara, codiceLotto, lottoSospensione.getW3NUM21() } );
	                          
	                          Iterator<Entry<String, DataColumn>> iterInizioDB = dccSospensioneDB.getColonne().entrySet().iterator();
	                          while (iterInizioDB.hasNext()) {
	                          	Entry<String, DataColumn> entry = iterInizioDB.next(); 
	                            String nomeCampo = entry.getKey();
	                            if (dccSospensione.isColumn(nomeCampo)) {
	                            	dccSospensioneDB.setValue(nomeCampo, dccSospensione.getColumn(nomeCampo).getValue());
	                            }
	                          }
	                        	
	                          /*if (lottoSospensione.isSetW3DVERBSO()) {
	                            dccSospensioneDB.setValue("W9SOSP.DATA_VERB_SOSP", new JdbcParametro(
	                            		JdbcParametro.TIPO_DATA, lottoSospensione.getW3DVERBSO().getTime()));
	                          }
	                          if (lottoSospensione.isSetW3DVERBRI()) {
	                          	dccSospensioneDB.setValue("W9SOSP.DATA_VERB_RIPR", new JdbcParametro(
	                          		JdbcParametro.TIPO_DATA, lottoSospensione.getW3DVERBRI().getTime()));
	                          }
	                          if (lottoSospensione.isSetW3IDMOTI4()) {
	                          	dccSospensioneDB.setValue("W9SOSP.ID_MOTIVO_SOSP", new JdbcParametro(
	                          		JdbcParametro.TIPO_NUMERICO, new Long(lottoSospensione.getW3IDMOTI4().toString())));
	                          }
	                          if (lottoSospensione.isSetW3FLAGSUP()) {
	                          	dccSospensioneDB.setValue("W9SOSP.FLAG_SUPERO_TEMPO", new JdbcParametro(
	                            		JdbcParametro.TIPO_TESTO, lottoSospensione.getW3FLAGSUP() ? "1" : "2"));
	                          }
	                          if (lottoSospensione.isSetW3RISERVE()) {
	                          	dccSospensioneDB.setValue("W9SOSP.FLAG_RISERVE", new JdbcParametro(
	                            		JdbcParametro.TIPO_TESTO, lottoSospensione.getW3RISERVE() ? "1" : "2"));
	                          }
	                          if (lottoSospensione.isSetW3FLAGVER()) {
	                          	dccSospensioneDB.setValue("W9SOSP.FLAG_VERBALE", new JdbcParametro(
	                            		JdbcParametro.TIPO_TESTO, lottoSospensione.getW3FLAGVER() ? "1" : "2"));
	                          }*/
	                        	
	                          if (dccSospensioneDB.isModifiedTable("W9SOSP")) {
	                          	dccSospensioneDB.getColumn("W9SOSP.CODGARA").setChiave(true);
	                          	dccSospensioneDB.getColumn("W9SOSP.CODLOTT").setChiave(true);
	                          	dccSospensioneDB.getColumn("W9SOSP.NUM_SOSP").setChiave(true);
	                          	dccSospensioneDB.update("W9SOSP", this.sqlManager);
	                          }
	                          
	                        	if (UtilitySITAT.isScritturaNoteAvvisiAttiva()) {
	                          	String codiceCIG = UtilitySITAT.getCIGLotto(codiceGara, codiceLotto, sqlManager);
	                           	UtilitySITAT.insertNoteAvvisi("W9LOTT", codiceGara.toString(), codiceLotto.toString(), null, null, null,
	                          			"CIG " + codiceCIG + ": modificata fase 'Sospensione contratto' n." + lottoSospensione.getW3NUM21(),
	                           			null, this.sqlManager);
	                          }
	                          
	                        	numeroSospensioniAggiornate++;
	                          UtilitySITAT.aggiungiMsgSchedaB(hmResponseLotti, cigLotto, lottoSospensione.getW3NUM21(), 
	                          		 true, "Fase 'Sospensione' aggiornata");
	                        	
	                        } else {
	                          // Caso in cui in base dati esiste gia' la fase di sospensione per il lotto
	                          // ed il flag di sovrascrittura dei dati e' valorizzato a false.
	
	                          StringBuilder strLog = new StringBuilder(credenzialiUtente.getPrefissoLogger());
	                          strLog.append(" La sospensione numero ");
	                          strLog.append(new Long(lottoSospensione.getW3NUM21()));
	                          strLog.append(" del lotto con CIG=");
	                          strLog.append(cigLotto);
	                          strLog.append(" non e' stata importata perche' la fase gia' esiste nella base dati " +
	                              "e non la si vuole sovrascrivere.");
	                          logger.info(strLog.toString());
	                                                      
	                          numeroSospensioniNonImportate++;
	                          UtilitySITAT.aggiungiMsgSchedaB(hmResponseLotti, cigLotto, lottoSospensione.getW3NUM21(), 
	                          		 false, StringUtils.replace(ConfigManager.getValore("error.sospensione.esistente"),
		                        				"{0}", cigLotto));
	                        }
	                      } else {
	                        StringBuilder strLog =
	                          new StringBuilder(credenzialiUtente.getPrefissoLogger());
	                        strLog.append(" La sospensione del contratto del lotto con CIG='");
	                        strLog.append(cigLotto);
	                        strLog.append(" non e' stato importato perche' non ha superato i controlli preliminari" +
	                        		" della fase del contratto. Nel caso specifico la condizione" +
	                        		" ((isS2 || isAII || isR) && !isE1 && !isSAQ && isEA) non risulta verificata.");
	                        logger.error(strLog);
	                        
	                        numeroLottiNonImportati++;
	                        
	                        UtilitySITAT.aggiungiMsgSchedaA(hmResponseLotti, cigLotto, false,
	                         		StringUtils.replace(ConfigManager.getValore("error.sospensione.noVisibile"),
	                         				"{0}", cigLotto));
	                      }
	                    } else {
	                      // Controlli preliminari non superati
	                      StringBuilder strLog =
	                        new StringBuilder(credenzialiUtente.getPrefissoLogger());
	                      
	                      Boolean existConclusione = UtilitySITAT.existsFaseEsportata(cigLotto, CostantiWSW9.CONCLUSIONE_CONTRATTO_SOPRA_SOGLIA, sqlManager);
	                      Boolean existConclusioneSemplificata = UtilitySITAT.existsFaseEsportata(cigLotto, CostantiWSW9.FASE_SEMPLIFICATA_CONCLUSIONE_CONTRATTO, sqlManager);
	                      
	                      
	                      strLog.append("Il lotto con CIG='");
	                      strLog.append(cigLotto);
	                      if(existConclusione || existConclusioneSemplificata){
	                    	  strLog.append("' ha gia' inviato la fase di conclusione del contratto. Impossibile importare la sospensione del contratto." );
	                      } else {
	                    	  strLog.append("' non ha la fase di inizio contratto. Impossibile importare la sospensione del contratto." );
	                      }
	                      logger.error(strLog);
	
	                      numeroLottiNonImportati++;
	                      if(existConclusione || existConclusioneSemplificata){
	                    	  UtilitySITAT.aggiungiMsgSchedaA(hmResponseLotti, cigLotto, false,
	                            		StringUtils.replace(ConfigManager.getValore("error.sospensione.flussiSuccessivi"),
	                            				"{0}", cigLotto));                  	  
	                      } else {
	                    	  UtilitySITAT.aggiungiMsgSchedaA(hmResponseLotti, cigLotto, false,
	                    			  StringUtils.replace(ConfigManager.getValore("error.sospensione.noFlussiPrecedenti"),
	                    					  "{0}", cigLotto));                    
	                      }
	                    }
	                  } // Chiusura ciclo sugli avanzamenti del singolo lotto
	                  
	                  if (numeroSospensioniImportate > 0) {
	                    numeroLottiImportati += numeroSospensioniImportate;
	                  }
	                  if (numeroSospensioniAggiornate > 0) {
	                    numeroLottiAggiornati += numeroSospensioniAggiornate;
	                  }
	                  if (numeroSospensioniNonImportate > 0) {
	                    numeroLottiNonImportati += numeroSospensioniNonImportate;
	                  }
	                } else {
	                  // Array delle Sospensioni vuoto: non si dovrebbe verificare mai per vincoli del xsd.
	                  numeroLottiNonImportati++;
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
                
              } // Chiusura ciclo sui lotti.
              
              UtilitySITAT.preparaRisultatoMessaggio(result, sovrascrivereDatiEsistenti, hmResponseLotti,
									numeroLottiImportati, numeroLottiNonImportati, numeroLottiAggiornati);
            }
          }
        } else { 

          // E' stato inviato un messaggio di test.
          StringBuilder strLog = new StringBuilder(credenzialiUtente.getPrefissoLogger());
          strLog.append(" E' stato inviato un messaggio di test. Tale messaggio non e' stato elaborato.'\n");
          strLog.append("Messaggio: ");
          strLog.append(sospensione.toString());
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
      logger.debug("istanziaSospensione: fine metodo");
      
      logger.debug("Response : " + UtilitySITAT.messaggioLogEsteso(result));
    }
    
    // MODIFICA TEMPORANEA -- DA CANCELLARE ---------------
    result.setError(UtilitySITAT.messaggioEsteso(result));
    // ----------------------------------------------------
    
    return result;
  }
  
}
