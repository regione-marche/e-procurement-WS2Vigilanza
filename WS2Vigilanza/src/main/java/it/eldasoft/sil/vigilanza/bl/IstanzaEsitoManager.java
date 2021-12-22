package it.eldasoft.sil.vigilanza.bl;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlOptions;
import org.apache.xmlbeans.XmlValidationError;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.db.datautils.DataColumn;
import it.eldasoft.gene.db.datautils.DataColumnContainer;
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.sil.vigilanza.beans.EsitoType;
import it.eldasoft.sil.vigilanza.beans.LottoEsitoType;
import it.eldasoft.sil.vigilanza.beans.RichiestaSincronaIstanzaEsitoDocument;
import it.eldasoft.sil.vigilanza.commons.CostantiWSW9;
import it.eldasoft.sil.vigilanza.commons.WSVigilanzaException;
import it.eldasoft.sil.vigilanza.utils.UtilitySITAT;
import it.eldasoft.sil.vigilanza.ws.beans.IstanzaOggettoType;
import it.eldasoft.sil.vigilanza.ws.beans.LoginType;
import it.eldasoft.sil.vigilanza.ws.beans.ResponseLottoType;
import it.eldasoft.sil.vigilanza.ws.beans.ResponseType;
import it.eldasoft.utils.properties.ConfigManager;

/**
 * Classe per l'import dei dati dell'esito dei lotti della gara.
 * 
 * @author Luca.Giacomazzo.
 */
public class IstanzaEsitoManager {

  private static Logger logger = Logger.getLogger(IstanzaEsitoManager.class);
  
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
   * Metodo per la gestione dell'esito dei lotti della gara.
   * 
   * @param login LoginType
   * @param esito IstanzaOggettoType
   * @return Ritorna l'oggetto ResponseType con l'esito dell'operazione
   * @throws XmlException 
   * @throws GestoreException 
   * @throws SQLException 
   * @throws Throwable
   */
  public ResponseType istanziaEsito(LoginType login, IstanzaOggettoType esito)
      throws XmlException, GestoreException, SQLException, Throwable {
    ResponseType result = new ResponseType();
    
    if (logger.isDebugEnabled()) {
      logger.debug("istanziaEsito: inizio metodo");
      
      logger.debug("XML : " + esito.getOggettoXML());
    }
    
    // Codice fiscale della Stazione appaltante
    String codFiscaleStazAppaltante = esito.getTestata().getCFEIN();
    
    boolean sovrascrivereDatiEsistenti = false;
    if (esito.getTestata().getSOVRASCR() != null) {
      sovrascrivereDatiEsistenti = esito.getTestata().getSOVRASCR().booleanValue(); 
    }
    
    // Verifica di login, password e determinazione della S.A.
    HashMap<String, Object> hm = this.credenzialiManager.verificaCredenziali(login, codFiscaleStazAppaltante);
    result = (ResponseType) hm.get("RESULT");
    

    if (result.isSuccess()) {
      Credenziale credenzialiUtente = (Credenziale) hm.get("USER");
      String xmlEsito = esito.getOggettoXML();
      
      try {
      		RichiestaSincronaIstanzaEsitoDocument istanzaEsitoDocument =
      				RichiestaSincronaIstanzaEsitoDocument.Factory.parse(xmlEsito);
	
	        boolean isMessaggioDiTest = 
	          istanzaEsitoDocument.getRichiestaSincronaIstanzaEsito().isSetTest()
	            && istanzaEsitoDocument.getRichiestaSincronaIstanzaEsito().getTest();
	        
	        if (!isMessaggioDiTest) {
	          
	          // si esegue il controllo sintattico del messaggio
	          XmlOptions validationOptions = new XmlOptions();
	          ArrayList<XmlValidationError> validationErrors = new ArrayList<XmlValidationError>();
	          validationOptions.setErrorListener(validationErrors);
	          boolean isSintassiXmlOK = istanzaEsitoDocument.validate(validationOptions);
	
	          if (!isSintassiXmlOK) {
	            synchronized (validationErrors) {
	              // Sincronizzazione dell'oggetto validationErrors per scrivere
	              // sul log il dettaglio dell'errore su righe successive.  
	              StringBuilder strLog = new StringBuilder(credenzialiUtente.getPrefissoLogger());
	              strLog.append(" Errore nella validazione del messaggio ricevuto per la gestione di una " +
	                  "istanza di esito dei lotti.");
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
	            LottoEsitoType[] arrayEsito = istanzaEsitoDocument.getRichiestaSincronaIstanzaEsito().getListaLottiEsitoArray();
	
	            if (arrayEsito != null && arrayEsito.length > 0) {
	              // HashMap per caricare gli oggetti ResponseLottoType per ciascun lotto, con CIG come chiave della hashMap
	              HashMap<String, ResponseLottoType> hmResponseLotti = new HashMap<String, ResponseLottoType>();
	
	              StringBuilder strQueryCig = new StringBuilder("'");
	              
	              for (int esi = 0; esi < arrayEsito.length; esi++) {
	                LottoEsitoType oggettoLottoEsito = arrayEsito[esi];
	                String cigLotto = oggettoLottoEsito.getW3CIG();
	                strQueryCig.append(cigLotto);
	                if (esi+1 < arrayEsito.length) {
	                	strQueryCig.append("','");
	                } else {
	                	strQueryCig.append("'");
	                }
								}
	              
	              List<?> listaCodGara = this.sqlManager.getListVector(
	              		"select CODGARA from W9LOTT where CIG in (" + strQueryCig.toString() + ")", null);
	              if (listaCodGara == null || (listaCodGara != null && listaCodGara.size() == 0)) {
	              	// Nessuno dei CIG indicati non esistono nella base dati di destinazione
	              	logger.error(credenzialiUtente.getPrefissoLogger()
	              			+ " nessuno dei lotti indicati sono presenti in archivio");
	              	throw new WSVigilanzaException("Attenzione: la scheda non e' inviabile poiche' non esiste la gara nel sistema di destinazione. E' necessario provvedere preventivamente alla sua creazione, importandola da simog o inviandola da Appalti");
	              } else {
	              	if (listaCodGara.size() !=  arrayEsito.length) {
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
	              
	              for (int esi = 0; esi < arrayEsito.length; esi++) {
	                LottoEsitoType oggettoLottoEsito = arrayEsito[esi];
	                String cigLotto = oggettoLottoEsito.getW3CIG();
	
	                HashMap<String, Long> hashM = UtilitySITAT.getCodGaraCodLottByCIG(cigLotto, this.sqlManager);
	                Long codiceGara = hashM.get("CODGARA");
	                Long codiceLotto = hashM.get("CODLOTT");

	                boolean isCfgVigilanza = UtilitySITAT.isConfigurazioneVigilanza(this.sqlManager);
	                
	             		if (!UtilitySITAT.isUtenteAmministratore(credenzialiUtente)) {
	             			if (!UtilitySITAT.isUtenteRupDelLotto(cigLotto, credenzialiUtente, this.sqlManager)) {
	             				logger.error(credenzialiUtente.getPrefissoLogger() + "L'utente non e' RUP del lotto con CIG=" + cigLotto
	             						+ ", oppure nella gara e/o nel lotto non e' stato indicato il RUP");
	
	                		throw new WSVigilanzaException("Le credenziali fornite non coincidono con quelle del RUP indicato");
	             			}
	             		}
	
	             		boolean esisteFaseEsito = UtilitySITAT.existsFase(codiceGara, codiceLotto, new Long(1), CostantiWSW9.COMUNICAZIONE_ESITO, this.sqlManager);

	             		// Ulteriori controlli preliminari
	             		if (UtilitySITAT.isFaseVisualizzabile(codiceGara, codiceLotto, CostantiWSW9.COMUNICAZIONE_ESITO, this.sqlManager)) {
	             			if (UtilitySITAT.isFaseAbilitata(codiceGara, codiceLotto, new Long(1), CostantiWSW9.COMUNICAZIONE_ESITO, this.sqlManager)) {

	             				// Esito procedura contraente 
	                    EsitoType esitoType = oggettoLottoEsito.getEsito();
		                	Long esitoProcedura = new Long(esitoType.getW9LOESIPROC().toString());
	
		                	if (!isCfgVigilanza || (isCfgVigilanza && esitoProcedura.longValue() != 1)) {
			                	if (!esisteFaseEsito || (esisteFaseEsito && sovrascrivereDatiEsistenti)) {
		                  		// Controlli preliminari superati con successo.
		                  		// Inizio inserimento dei dati degli esiti nella base dati.
			
			                		// Record in W9FASI.
			                		UtilitySITAT.istanziaFase(this.sqlManager, codiceGara, codiceLotto, 
																new Long(CostantiWSW9.COMUNICAZIONE_ESITO), new Long(1));
			                      
			                    DataColumn codGaraEsito = new DataColumn("W9ESITO.CODGARA", new JdbcParametro(
			                    		JdbcParametro.TIPO_NUMERICO, codiceGara));
			                    DataColumn codLottEsito = new DataColumn("W9ESITO.CODLOTT", new JdbcParametro(
			                    		JdbcParametro.TIPO_NUMERICO, codiceLotto));
			                    DataColumn proceduraEsito = new DataColumn("W9ESITO.ESITO_PROCEDURA", 
			                        new JdbcParametro(JdbcParametro.TIPO_NUMERICO, esitoProcedura));
			                    DataColumnContainer dccEsito = new DataColumnContainer(new DataColumn[] { 
			                        codGaraEsito, codLottEsito, proceduraEsito } );
			
			                    if (esisteFaseEsito) {
			                    	this.sqlManager.update("delete from W9ESITO where CODGARA=? and CODLOTT=?", 
			                    			new Object[] { codiceGara, codiceLotto } );
			                    }
			                    dccEsito.insert("W9ESITO", this.sqlManager);
			                    
			                    if (UtilitySITAT.isScritturaNoteAvvisiAttiva()) {
			                    	String codiceCIG = UtilitySITAT.getCIGLotto(codiceGara, codiceLotto, this.sqlManager);
			                    	if (!esisteFaseEsito) {
			                    		UtilitySITAT.insertNoteAvvisi("W9LOTT", codiceGara.toString(), codiceLotto.toString(),
			                    				null, null, null, "CIG " + codiceCIG + ": inserita fase 'Comunicazione esito'", "",
			                    				this.sqlManager);
			                    	} else {
			                    		UtilitySITAT.insertNoteAvvisi("W9LOTT", codiceGara.toString(), codiceLotto.toString(),
			                    				null, null, null, "CIG " + codiceCIG + ": modificata fase 'Comunicazione esito'",
			                    				null, sqlManager);
			                    	}
			                    }
			                      
			                    if (!esisteFaseEsito) {
			                      numeroLottiImportati++;
			                      UtilitySITAT.aggiungiMsgSchedaA(hmResponseLotti, cigLotto, true, "Esito importato");
			                    } else {
			                      if (sovrascrivereDatiEsistenti) {
			                        numeroLottiAggiornati++;
			                        UtilitySITAT.aggiungiMsgSchedaA(hmResponseLotti, cigLotto, true, "Esito modificato");
			                      }
			                    }
			                  } else {
			                    // Caso in cui in base dati esiste gia' la fase di comunicazione esito per il lotto
			                    // ed il flag di sovrascrittura dei dati e' valorizzato a false.
			
			                    StringBuilder strLog = new StringBuilder(credenzialiUtente.getPrefissoLogger());
			                    strLog.append(" L'esito del lotto con CIG=");
			                    strLog.append(cigLotto);
			                    strLog.append(" non e' stato importato perche' la fase gia' esiste nella base dati " +
			                        "e non la si vuole sovrascrivere.");
			                    logger.info(strLog.toString());
			                    
			                    numeroLottiNonImportati++;
			                    
			                    UtilitySITAT.aggiungiMsgSchedaA(hmResponseLotti, cigLotto, false,
			                    		StringUtils.replace(ConfigManager.getValore("error.esito.esistente"),
			                    				"{0}", cigLotto));
			                  }
		                	
		                	} else {
		                		// Caso in cui si sta ricevendo l'esito comunicazione procedura contraente con esito_procedura=1 (aggiudicazione)
		                		// e la base dati di destinazione e' quella di VigilanzaComunicazioni
		                		StringBuilder strLog = new StringBuilder(credenzialiUtente.getPrefissoLogger());
		                    strLog.append(" L'esito del lotto con CIG=");
		                    strLog.append(cigLotto);
		                    strLog.append(" non e' stato importato perche' in VigilanzaComunicazioni si puo' creare la fase "
		                    		+ "Comunicazione esito procedura contraente solo nel caso di annullamento, gara deserta o senza esito");
		                    logger.info(strLog.toString());
		                    
		                    numeroLottiNonImportati++;
		                    
		                    UtilitySITAT.aggiungiMsgSchedaA(hmResponseLotti, cigLotto, false,
		                    		StringUtils.replace(ConfigManager.getValore("error.esito.proceduraNonAmmessa"),
		                    				"{0}", cigLotto));
		                		
		                	}
		                } else {
		                	
		                  StringBuilder strLog =
		                    new StringBuilder(credenzialiUtente.getPrefissoLogger());
		                  strLog.append(" L'esito del lotto con CIG=");
		                  strLog.append(cigLotto);
		                  strLog.append(" non e' stato importato perche' non ha superato i controlli preliminari" +
		                  		" della fase di esito. Nel caso specifico esiste gia' l'aggiudicazione.");
		                  logger.info(strLog.toString());
		
		                  numeroLottiNonImportati++;
		
		                  UtilitySITAT.aggiungiMsgSchedaA(hmResponseLotti, cigLotto, false,
		                  		StringUtils.replace(ConfigManager.getValore("error.esito.nonAbilitata"),
		                				"{0}", cigLotto));
		                }
	                } else {
	                	StringBuilder strLog =
		                    new StringBuilder(credenzialiUtente.getPrefissoLogger());
		                  strLog.append(" L'esito del lotto con CIG=");
		                  strLog.append(cigLotto);
		                  strLog.append(" non e' stato importato perche' non ha superato i controlli preliminari" +
		                  		" della fase di esito. Nel caso specifico la condizione (!isAAQ) non risulta verificata.");
		                  logger.info(strLog.toString());
		
		                  numeroLottiNonImportati++;
		
		                  UtilitySITAT.aggiungiMsgSchedaA(hmResponseLotti, cigLotto, false,
		                  		StringUtils.replace(ConfigManager.getValore("error.esito.noVisibile"),
		                				"{0}", cigLotto));
	                }
	                
	                // qui quo qua
	                
	              } // Chiusura ciclo for
	
	              UtilitySITAT.preparaRisultatoMessaggio(result, sovrascrivereDatiEsistenti,
										hmResponseLotti, numeroLottiImportati,
										numeroLottiNonImportati, numeroLottiAggiornati);
	            } // Chiusura if sul test arrayEsito != null && arrayEsito.length > 0
	          } // Chiusura else sintassiXmlOK
	        } else {
	          // E' stato inviato un messaggio di test.
	          StringBuilder strLog = new StringBuilder(credenzialiUtente.getPrefissoLogger());
	          strLog.append(" E' stato inviato un messaggio di test. Tale messaggio non e' stato elaborato.'\n");
	          strLog.append("Messaggio: ");
	          strLog.append(esito.toString());
	          logger.info(strLog);
	          
	          result.setSuccess(true);
	          result.setError("E' stato inviato un messaggio di test: messaggio non elaborato.");
	        }
      } catch (XmlException e) {
        StringBuilder strLog =
            new StringBuilder(credenzialiUtente.getPrefissoLogger());
        strLog.append(" Errore nel parsing dell'XML ricevuto.\n");
        strLog.append(e);
        logger.error(strLog);

        throw e;
      } catch (SQLException sql) {
        logger.error("Errore sql nel salvataggio dei dati ricevuti.", sql);

        throw sql;
      } catch (Throwable t) {
        logger.error("Errore generico nell'esecuzione della procedura.", t);
        
        throw t;
      }
    } else {
      logger.error("La verifica delle credenziali non e' stato superato. Messaggio di errore: "
          + result.getError());
    }
	  
    
    if (logger.isDebugEnabled()) {
      logger.debug("istanziaEsito: fine metodo");
      
      logger.debug("Response : " + UtilitySITAT.messaggioLogEsteso(result));
    }
    
    // MODIFICA TEMPORANEA -- DA CANCELLARE ---------------
    result.setError(UtilitySITAT.messaggioEsteso(result));
    // ----------------------------------------------------
    
    return result;
  }

}
