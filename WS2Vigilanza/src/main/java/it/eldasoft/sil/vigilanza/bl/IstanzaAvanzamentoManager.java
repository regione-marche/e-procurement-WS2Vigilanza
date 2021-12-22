package it.eldasoft.sil.vigilanza.bl;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.db.datautils.DataColumn;
import it.eldasoft.gene.db.datautils.DataColumnContainer;
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.sil.vigilanza.beans.AvanzamentoType;
import it.eldasoft.sil.vigilanza.beans.LottoAvanzamentoType;
import it.eldasoft.sil.vigilanza.beans.RichiestaSincronaIstanzaAvanzamentoDocument;
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
 * Classe per l'import dei dati dell'avanzamento dei lotti della gara.
 * 
 * @author Luca.Giacomazzo
 */
public class IstanzaAvanzamentoManager {
  
  private static Logger logger = Logger.getLogger(IstanzaAvanzamentoManager.class);
  
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
   * Metodo per la gestione dell'avanzamento dei contratti.
   * 
   * @param login LoginType
   * @param avanzamento IstanzaOggettoType
   * @return Ritorna l'oggetto ResponseType con l'esito dell'operazione
   * @throws XmlException 
   * @throws GestoreException 
   * @throws SQLException 
   * @throws Throwable
   */
  public ResponseType istanziaAvanzamento(LoginType login, IstanzaOggettoType avanzamento)
      throws XmlException, GestoreException, SQLException, Throwable {
    ResponseType result = null;
    
    if (logger.isDebugEnabled()) {
      logger.debug("istanziaAvanzamento: inizio metodo");
      
      logger.debug("XML : " + avanzamento.getOggettoXML());
    }
  
    // Codice fiscale della Stazione appaltante
    String codFiscaleStazAppaltante = avanzamento.getTestata().getCFEIN();
    
    boolean sovrascrivereDatiEsistenti = false;
    if (avanzamento.getTestata().getSOVRASCR() != null) {
      sovrascrivereDatiEsistenti = avanzamento.getTestata().getSOVRASCR().booleanValue(); 
    }
    
    // Verifica di login, password e determinazione della S.A.
    HashMap<String, Object> hm = this.credenzialiManager.verificaCredenziali(login, codFiscaleStazAppaltante);
    result = (ResponseType) hm.get("RESULT");
    
    if (result.isSuccess()) {
      Credenziale credenzialiUtente = (Credenziale) hm.get("USER");
      String xmlAvanzamento = avanzamento.getOggettoXML();
      
      try {
        RichiestaSincronaIstanzaAvanzamentoDocument istanzaAvanzamentoDocument =
          RichiestaSincronaIstanzaAvanzamentoDocument.Factory.parse(xmlAvanzamento);

        boolean isMessaggioDiTest = 
          istanzaAvanzamentoDocument.getRichiestaSincronaIstanzaAvanzamento().isSetTest()
            && istanzaAvanzamentoDocument.getRichiestaSincronaIstanzaAvanzamento().getTest();
        
        if (! isMessaggioDiTest) {
          // si esegue il controllo sintattico del messaggio
          XmlOptions validationOptions = new XmlOptions();
          ArrayList<XmlValidationError> validationErrors = new ArrayList<XmlValidationError>();
          validationOptions.setErrorListener(validationErrors);
          boolean isSintassiXmlOK = istanzaAvanzamentoDocument.validate(validationOptions);
  
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
            LottoAvanzamentoType[] arrayLottiAvanzamento =
              istanzaAvanzamentoDocument.getRichiestaSincronaIstanzaAvanzamento().getListaLottiAvanzamentiArray();
            
            if (arrayLottiAvanzamento != null && arrayLottiAvanzamento.length > 0) {
              // HashMap per caricare gli oggetti ResponseLottoType per ciascun lotto, con CIG come chiave della hashMap
              HashMap<String, ResponseLottoType> hmResponseLotti = new HashMap<String, ResponseLottoType>();

              StringBuilder strQueryCig = new StringBuilder("'");
              for (int contr = 0; contr < arrayLottiAvanzamento.length && result.isSuccess(); contr++) {
              	LottoAvanzamentoType oggettoAvanzamento = arrayLottiAvanzamento[contr];
                String cigLotto = oggettoAvanzamento.getW3CIG();

                strQueryCig.append(cigLotto);
                if (contr + 1 < arrayLottiAvanzamento.length)
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
              	if (listaCodGara.size() !=  arrayLottiAvanzamento.length) {
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
	
              for (int avan = 0; avan < arrayLottiAvanzamento.length; avan++) {
                LottoAvanzamentoType oggettoLottoAvanzamento = arrayLottiAvanzamento[avan];
                String cigLotto = oggettoLottoAvanzamento.getW3CIG();
	
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
	
	              	AvanzamentoType[] arrayAvanzamentoType = oggettoLottoAvanzamento.getListaAvanzamentiArray();
	                if (arrayAvanzamentoType != null && arrayAvanzamentoType.length > 0) {
	            	    int numeroAvanzamentiImportati = 0;
	                  int numeroAvanzamentiNonImportati = 0;
	                  int numeroAvanzamentiAggiornati = 0;
	                  
	                  for (int avanzam=0; avanzam < arrayAvanzamentoType.length; avanzam++ ) {

		                  boolean isS3 = UtilitySITAT.isS3(codiceGara, codiceLotto, this.sqlManager);
		                  boolean isAII = UtilitySITAT.isAII(codiceGara, this.sqlManager);
		                  boolean isSAQ = UtilitySITAT.isSAQ(codiceGara, this.sqlManager);
		                  boolean isORD = UtilitySITAT.isOrd(codiceGara, this.sqlManager);
		                  
		                  Long faseEsecuz = null;
		                  if ((isS3 || isAII) && !isSAQ && isORD) {
		                    faseEsecuz = new Long(CostantiWSW9.AVANZAMENTO_CONTRATTO_SOPRA_SOGLIA);
		                  } else {
		                    faseEsecuz = new Long(CostantiWSW9.AVANZAMENTO_CONTRATTO_APPALTO_SEMPLIFICATO);
		                  }
	                  	
		                  if (UtilitySITAT.isFaseAttiva(faseEsecuz, this.sqlManager)) {
		                  	AvanzamentoType lottoAvanzamento = arrayAvanzamentoType[avanzam];
		
		                    // Controllo esistenza della fase: se no, si esegue l'insert di tutti dati, 
		                    // se si, si esegue l'update dei record
		                    boolean esisteFaseAvanzamento = UtilitySITAT.existsFase(codiceGara, codiceLotto, new Long(1),
		                        faseEsecuz.intValue(), new Long(lottoAvanzamento.getW3AVNUMAVAN()), this.sqlManager);
		                    if (esisteFaseAvanzamento || UtilitySITAT.isFaseAbilitata(codiceGara, codiceLotto, new Long(1), faseEsecuz.intValue(), this.sqlManager)) {
		                    	if (esisteFaseAvanzamento || UtilitySITAT.isFaseVisualizzabile(codiceGara, codiceLotto, faseEsecuz.intValue(), this.sqlManager)) {
			
		                        DataColumn codGaraAvanzam = new DataColumn("W9AVAN.CODGARA", new JdbcParametro(
		                        		JdbcParametro.TIPO_NUMERICO, codiceGara));
		                        DataColumn codLottAvanzam = new DataColumn("W9AVAN.CODLOTT", new JdbcParametro(
		                        		JdbcParametro.TIPO_NUMERICO, codiceLotto));
		                        DataColumn numAvanzam = new DataColumn("W9AVAN.NUM_AVAN",new JdbcParametro(
		                        		JdbcParametro.TIPO_NUMERICO, new Long(lottoAvanzamento.getW3AVNUMAVAN())));
		                        DataColumn numAppa = new DataColumn("W9AVAN.NUM_APPA",new JdbcParametro(
		                        		JdbcParametro.TIPO_NUMERICO, new Long(1)));
		                        DataColumnContainer dccAvanzamento = new DataColumnContainer(new DataColumn[] { 
		                            codGaraAvanzam, codLottAvanzam, numAvanzam, numAppa } );
		                        
		                        if (lottoAvanzamento.isSetW3DATACER()) {
		                          dccAvanzamento.addColumn("W9AVAN.DATA_CERTIFICATO", new JdbcParametro(JdbcParametro.TIPO_DATA,
		                              lottoAvanzamento.getW3DATACER().getTime()));
		                        }
		                        if (lottoAvanzamento.isSetW3ICERTIF()) {
		                          dccAvanzamento.addColumn("W9AVAN.IMPORTO_CERTIFICATO", new JdbcParametro(JdbcParametro.TIPO_DECIMALE, 
		                              lottoAvanzamento.getW3ICERTIF()));
		                        }
		                        if (lottoAvanzamento.isSetW3FLAGRIT()) {
		                          dccAvanzamento.addColumn("W9AVAN.FLAG_RITARDO", new JdbcParametro(JdbcParametro.TIPO_TESTO, 
		                              lottoAvanzamento.getW3FLAGRIT().toString()));
		                        }
		                        if (lottoAvanzamento.isSetW3NUMGIO1()) {
		                          dccAvanzamento.addColumn("W9AVAN.NUM_GIORNI_SCOST", new JdbcParametro(JdbcParametro.TIPO_NUMERICO,
		                              new Long(lottoAvanzamento.getW3NUMGIO1())));
		                        }
		                        if (lottoAvanzamento.isSetW3NUMGIO2()) {
		                          dccAvanzamento.addColumn("W9AVAN.NUM_GIORNI_PROROGA", new JdbcParametro(JdbcParametro.TIPO_NUMERICO,
		                              new Long(lottoAvanzamento.getW3NUMGIO2())));
		                        }
		                        if (lottoAvanzamento.isSetW3FLAGPAG()) {
		                          dccAvanzamento.addColumn("W9AVAN.FLAG_PAGAMENTO", new JdbcParametro(JdbcParametro.TIPO_NUMERICO,
		                              new Long(lottoAvanzamento.getW3FLAGPAG().toString())));
		                        }
		                        if (lottoAvanzamento.isSetW3DATAANT()) {
		                          dccAvanzamento.addColumn("W9AVAN.DATA_ANTICIPAZIONE", new JdbcParametro(JdbcParametro.TIPO_DATA,
		                              lottoAvanzamento.getW3DATAANT().getTime()));
		                        }
		                        if (lottoAvanzamento.isSetW3IANTICI()) {
		                          dccAvanzamento.addColumn("W9AVAN.IMPORTO_ANTICIPAZIONE", new JdbcParametro(JdbcParametro.TIPO_DECIMALE,
		                              lottoAvanzamento.getW3IANTICI()));
		                        }
		                        if (lottoAvanzamento.isSetW3DATARAG()) {
		                          dccAvanzamento.addColumn("W9AVAN.DATA_RAGGIUNGIMENTO", new JdbcParametro(JdbcParametro.TIPO_DATA,
		                              lottoAvanzamento.getW3DATARAG().getTime()));
		                        }
		                        if (lottoAvanzamento.isSetW3ISAL()) {
		                          dccAvanzamento.addColumn("W9AVAN.IMPORTO_SAL", new JdbcParametro(JdbcParametro.TIPO_DECIMALE,
		                              lottoAvanzamento.getW3ISAL()));
		                        }
		
		                        if (!esisteFaseAvanzamento) {
		
		                          // Record in W9FASI.
		                          UtilitySITAT.istanziaFase(this.sqlManager, codiceGara, codiceLotto, faseEsecuz,
		                        				new Long(lottoAvanzamento.getW3AVNUMAVAN()));
		
		                          dccAvanzamento.insert("W9AVAN", this.sqlManager);
		                            
		                          if (UtilitySITAT.isScritturaNoteAvvisiAttiva()) {
		                          	String codiceCIG = UtilitySITAT.getCIGLotto(codiceGara, codiceLotto, sqlManager);
		                         		UtilitySITAT.insertNoteAvvisi("W9LOTT", codiceGara.toString(), codiceLotto.toString(), null, null, null,
		                         				"CIG " + codiceCIG + ": inserita fase 'Avanzamento contratto' n. " + lottoAvanzamento.getW3AVNUMAVAN(),
		                         				null, this.sqlManager);
		                          }
		                          
		                          numeroAvanzamentiImportati++;
		                          UtilitySITAT.aggiungiMsgSchedaB(hmResponseLotti, cigLotto, lottoAvanzamento.getW3AVNUMAVAN(),
		                          		true, "Fase 'Avanzamento Contratto' importata");
		
		                        } else if (esisteFaseAvanzamento && sovrascrivereDatiEsistenti) {
		                        		
		                        		DataColumnContainer dccAvanzamentoDB = new DataColumnContainer(this.sqlManager, "W9AVAN", 
		                        				"select DATA_CERTIFICATO, IMPORTO_CERTIFICATO, FLAG_RITARDO, NUM_GIORNI_SCOST, NUM_GIORNI_PROROGA, "
		                        						 + "FLAG_PAGAMENTO, DATA_ANTICIPAZIONE, IMPORTO_ANTICIPAZIONE, DATA_RAGGIUNGIMENTO, IMPORTO_SAL, "
		                        						 + "CODGARA, CODLOTT, NUM_AVAN, NUM_APPA "
		                        				+ "from W9AVAN where CODGARA=? and CODLOTT=? and NUM_AVAN=? and NUM_APPA=1", 
		                          			new Object[]{ codiceGara, codiceLotto, new Long(lottoAvanzamento.getW3AVNUMAVAN()) });
		                        		
		                            Iterator<Entry<String, DataColumn>> iterInizioDB = dccAvanzamentoDB.getColonne().entrySet().iterator();
		                            while (iterInizioDB.hasNext()) {
		                            	Entry<String, DataColumn> entry = iterInizioDB.next(); 
		                              String nomeCampo = entry.getKey();
		                              if (dccAvanzamento.isColumn(nomeCampo)) {
		                              	dccAvanzamentoDB.setValue(nomeCampo, dccAvanzamento.getColumn(nomeCampo).getValue());
		                              }
		                            }
		                        		
		                            /*if (lottoAvanzamento.isSetW3DATACER()) {
		                              dccAvanzamentoDB.setValue("W9AVAN.DATA_CERTIFICATO", new JdbcParametro(JdbcParametro.TIPO_DATA,
		                                  lottoAvanzamento.getW3DATACER().getTime()));
		                            }
		                            if (lottoAvanzamento.isSetW3ICERTIF()) {
		                            dccAvanzamentoDB.setValue("W9AVAN.IMPORTO_CERTIFICATO", new JdbcParametro(JdbcParametro.TIPO_DECIMALE, 
		                                lottoAvanzamento.getW3ICERTIF()));
		                            }
		                            if (lottoAvanzamento.isSetW3FLAGRIT()) {
		                              dccAvanzamentoDB.setValue("W9AVAN.FLAG_RITARDO", new JdbcParametro(JdbcParametro.TIPO_TESTO, 
		                                  lottoAvanzamento.getW3FLAGRIT().toString()));
		                            }
		                            if (lottoAvanzamento.isSetW3NUMGIO1()) {
		                              dccAvanzamentoDB.setValue("W9AVAN.NUM_GIORNI_SCOST", new JdbcParametro(JdbcParametro.TIPO_NUMERICO,
		                                  new Long(lottoAvanzamento.getW3NUMGIO1())));
		                            }
		                            if (lottoAvanzamento.isSetW3NUMGIO2()) {
		                              dccAvanzamentoDB.setValue("W9AVAN.NUM_GIORNI_PROROGA", new JdbcParametro(JdbcParametro.TIPO_NUMERICO,
		                                  new Long(lottoAvanzamento.getW3NUMGIO2())));
		                            }
		                            if (lottoAvanzamento.isSetW3FLAGPAG()) {
		                              dccAvanzamentoDB.setValue("W9AVAN.FLAG_PAGAMENTO", new JdbcParametro(JdbcParametro.TIPO_NUMERICO,
		                                  new Long(lottoAvanzamento.getW3FLAGPAG().toString())));
		                            }
		                            if (lottoAvanzamento.isSetW3DATAANT()) {
		                              dccAvanzamentoDB.setValue("W9AVAN.DATA_ANTICIPAZIONE", new JdbcParametro(JdbcParametro.TIPO_DATA,
		                                  lottoAvanzamento.getW3DATAANT().getTime()));
		                            }
		                            if (lottoAvanzamento.isSetW3IANTICI()) {
		                              dccAvanzamentoDB.setValue("W9AVAN.IMPORTO_ANTICIPAZIONE", new JdbcParametro(JdbcParametro.TIPO_DECIMALE,
		                                  lottoAvanzamento.getW3IANTICI()));
		                            }
		                            if (lottoAvanzamento.isSetW3DATARAG()) {
		                              dccAvanzamentoDB.setValue("W9AVAN.DATA_RAGGIUNGIMENTO", new JdbcParametro(JdbcParametro.TIPO_DATA,
		                                  lottoAvanzamento.getW3DATARAG().getTime()));
		                            }
		                            if (lottoAvanzamento.isSetW3ISAL()) {
		                              dccAvanzamentoDB.setValue("W9AVAN.IMPORTO_SAL", new JdbcParametro(JdbcParametro.TIPO_DECIMALE,
		                                  lottoAvanzamento.getW3ISAL()));
		                            }*/
		                      		
		                            if (dccAvanzamentoDB.isModifiedTable("W9AVAN")) {
		                            	dccAvanzamentoDB.getColumn("W9AVAN.CODGARA").setChiave(true);
		                            	dccAvanzamentoDB.getColumn("W9AVAN.CODLOTT").setChiave(true);
		                            	dccAvanzamentoDB.getColumn("W9AVAN.NUM_AVAN").setChiave(true);
		                            	dccAvanzamentoDB.update("W9AVAN", this.sqlManager);
		                            }
		
		                          	if (UtilitySITAT.isScritturaNoteAvvisiAttiva()) {
		                            	String codiceCIG = UtilitySITAT.getCIGLotto(codiceGara, codiceLotto, sqlManager);
		                           		UtilitySITAT.insertNoteAvvisi("W9LOTT", codiceGara.toString(), codiceLotto.toString(), null, null, null,
		                             				"CIG " + codiceCIG + ": modificata fase 'Avanzamento contratto' n. " + lottoAvanzamento.getW3AVNUMAVAN(),
		                               			null, this.sqlManager);
		                            }
		                          	
		                            numeroAvanzamentiAggiornati++;
		                            UtilitySITAT.aggiungiMsgSchedaB(hmResponseLotti, cigLotto, lottoAvanzamento.getW3AVNUMAVAN(),
		                            		true, "Fase 'Avanzamento Contratto' aggiornata");
		                            
		                          } else {
		                            // Caso in cui in base dati esiste gia' la fase di avanzamento per il lotto
		                            // ed il flag di sovrascrittura dei dati e' valorizzato a false.
		  
		                            StringBuilder strLog = new StringBuilder(credenzialiUtente.getPrefissoLogger());
		                            strLog.append(" L'avanzamento contratto numero ");
		                            strLog.append(lottoAvanzamento.getW3AVNUMAVAN());
		                            strLog.append(" del lotto con CIG=");
		                            strLog.append(cigLotto);
		                            strLog.append(" non e' stato importato perche' la fase esiste gia' nella base dati " +
		                                "e non la si vuole sovrascrivere.");
		                            logger.info(strLog.toString());
		                            
		                            numeroAvanzamentiNonImportati++;
		                            UtilitySITAT.aggiungiMsgSchedaB(hmResponseLotti, cigLotto, lottoAvanzamento.getW3AVNUMAVAN(),
		                            		false, StringUtils.replace(ConfigManager.getValore("error.avanzamento.esistente"),
		                            				"{0}", cigLotto));
		                          }
				                    } else {
				                    	// Fase avanzamento non visibile
				                      StringBuilder strLog =
				                        new StringBuilder(credenzialiUtente.getPrefissoLogger());
					                    strLog.append("L'avanzamento contratto del lotto con CIG='");
					                    strLog.append(cigLotto);
					                    strLog.append(" non e' stato importato perche' non ha superato i controlli preliminari della fase avanzamento contratto. ");
					                    if (faseEsecuz.intValue() == CostantiWSW9.AVANZAMENTO_CONTRATTO_SOPRA_SOGLIA) {
					                    	strLog.append("Nel caso specifico la condizione ((isS3 || isAII) && !isE1 && !isSAQ && isEA) non risulta verificata.");
					                    } else {
					                    	strLog.append("Nel caso specifico la condizione (!isS3 && !isAII && isEA) non risulta verificata.");
					                    }
				                      logger.error(strLog);
				
				                      numeroLottiNonImportati++;
				                      if (faseEsecuz.intValue() == CostantiWSW9.AVANZAMENTO_CONTRATTO_SOPRA_SOGLIA) {
				                      	UtilitySITAT.aggiungiMsgSchedaA(hmResponseLotti, cigLotto, false,
				                      			StringUtils.replace(ConfigManager.getValore("error.avanzamento.noVisibile"),
				                      					"{0}", cigLotto));
				                      } else {
				                      	UtilitySITAT.aggiungiMsgSchedaA(hmResponseLotti, cigLotto, false,
				                      			StringUtils.replace(ConfigManager.getValore("error.avanzamento.noVisibile.faseSemplificata"),
				                      					"{0}", cigLotto));
				                      }
				                    }
				                  } else {
				                    // Fase di avanzamento non abilitata
				                    StringBuilder strLog = new StringBuilder(credenzialiUtente.getPrefissoLogger());
				                    
				                    Boolean existCollaudo =  UtilitySITAT.existsFaseEsportata(cigLotto, CostantiWSW9.COLLAUDO_CONTRATTO, sqlManager);
				                    
				                    strLog.append("Il lotto con CIG='");
			                      strLog.append(cigLotto);
			                      if (faseEsecuz.intValue() == CostantiWSW9.AVANZAMENTO_CONTRATTO_SOPRA_SOGLIA) {
			                      	strLog.append("' non ha la fase di inizio contratto. Impossibile importare l'avanzamento contratto." );
			                      } else {
			                    	  if(existCollaudo){
			                    		  strLog.append("' ha gia' inviato la fase di collaudo. Impossibile importare l'avanzamento contratto." );
			                    	  }
			                    	  else{
			                    		  strLog.append("' non ha la fase di inizio contratto o stipula accordo quadro. Impossibile importare l'avanzamento contratto." );
			                    	  }
			                      }
				                    logger.error(strLog);
				                    
				                    if (existCollaudo) {
					                    UtilitySITAT.aggiungiMsgSchedaA(hmResponseLotti, cigLotto, false,
					                    		StringUtils.replace(ConfigManager.getValore("error.avanzamento.flussiSuccessivi"),
					                    				"{0}", cigLotto));  
				                    } else {
				                	   UtilitySITAT.aggiungiMsgSchedaA(hmResponseLotti, cigLotto, false,
				                    		StringUtils.replace(ConfigManager.getValore("error.avanzamento.noFlussiPrecedenti"),
				                    				"{0}", cigLotto));
				                    }
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
	                    } // Chiusura ciclo sugli avanzamenti del singolo lotto
	                        
	                    if (numeroAvanzamentiImportati > 0) {
	                      numeroLottiImportati += numeroAvanzamentiImportati;
	                    }
	                    if (numeroAvanzamentiAggiornati > 0) {
	                      numeroLottiAggiornati += numeroAvanzamentiAggiornati;
	                    }
	                    if (numeroAvanzamentiNonImportati > 0) {
	                      numeroLottiNonImportati += numeroAvanzamentiNonImportati;
	                    }
	
	                  } else {
	                    // Array degli avanzamenti vuoto: non dovrebbe verificarsi mai, per definizione del xsd.
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
          strLog.append(avanzamento.toString());
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
      logger.debug("istanziaAvanzamento: fine metodo");
      
      logger.debug("Response : " + UtilitySITAT.messaggioLogEsteso(result));
    }
    
    // MODIFICA TEMPORANEA -- DA CANCELLARE ---------------
    result.setError(UtilitySITAT.messaggioEsteso(result));
    // ----------------------------------------------------
    
    return result;
  }
  
}
