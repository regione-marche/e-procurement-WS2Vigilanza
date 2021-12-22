package it.eldasoft.sil.vigilanza.bl;

import it.eldasoft.gene.bl.GenChiaviManager;
import it.eldasoft.gene.bl.GeneManager;
import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.db.datautils.DataColumn;
import it.eldasoft.gene.db.datautils.DataColumnContainer;
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.sil.vigilanza.beans.ImpresaType;
import it.eldasoft.sil.vigilanza.beans.LottoSubappaltoType;
import it.eldasoft.sil.vigilanza.beans.RichiestaSincronaIstanzaSubappaltoDocument;
import it.eldasoft.sil.vigilanza.beans.SubappaltoType;
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
 * Classe per l'import dei dati dei subappalti dei lotti della gara.
 * 
 * @author Luca.Giacomazzo
 */
public class IstanzaSubappaltoManager {

  private static Logger logger = Logger.getLogger(IstanzaSubappaltoManager.class);
  
  private CredenzialiManager credenzialiManager;
  
  private SqlManager sqlManager;

  private GeneManager geneManager;
  
  private GenChiaviManager genChiaviManager;
  
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
   * @param genChiaviManager the genChiaviManager to set
   */
  public void setGenChiaviManager(GenChiaviManager genChiaviManager) {
    this.genChiaviManager = genChiaviManager;
  }
  
  /**
   * Metodo per la gestione dei subappalti dei contratti.
   * 
   * @param login LoginType
   * @param subappalto IstanzaOggettoType
   * @return Ritorna l'oggetto ResponseType con l'esito dell'operazione
   * @throws XmlException 
   * @throws GestoreException 
   * @throws SQLException 
   * @throws Throwable
   */
  public ResponseType istanziaSubappalto(LoginType login, IstanzaOggettoType subappalto)
      throws XmlException, GestoreException, SQLException, Throwable {
    ResponseType result = null;
    
    if (logger.isDebugEnabled()) {
      logger.debug("istanziaSubappalto: inizio metodo");
      
      logger.debug("XML : " + subappalto.getOggettoXML());
    }
  
    // Codice fiscale della Stazione appaltante
    String codFiscaleStazAppaltante = subappalto.getTestata().getCFEIN().toUpperCase();
    
    boolean sovrascrivereDatiEsistenti = false;
    if (subappalto.getTestata().getSOVRASCR() != null) {
      sovrascrivereDatiEsistenti = subappalto.getTestata().getSOVRASCR().booleanValue(); 
    }
    
    // Verifica di login, password e determinazione della S.A.
    HashMap<String, Object> hm = this.credenzialiManager.verificaCredenziali(login, codFiscaleStazAppaltante);
    result = (ResponseType) hm.get("RESULT");
    
    if (result.isSuccess()) {
      Credenziale credenzialiUtente = (Credenziale) hm.get("USER");
      String xmlSubappalto = subappalto.getOggettoXML();
      
      try {
        RichiestaSincronaIstanzaSubappaltoDocument istanzaSubappaltoDocument =
          RichiestaSincronaIstanzaSubappaltoDocument.Factory.parse(xmlSubappalto);

        boolean isMessaggioDiTest = 
          istanzaSubappaltoDocument.getRichiestaSincronaIstanzaSubappalto().isSetTest()
            && istanzaSubappaltoDocument.getRichiestaSincronaIstanzaSubappalto().getTest();
        
        if (! isMessaggioDiTest) {
        
          // si esegue il controllo sintattico del messaggio
          XmlOptions validationOptions = new XmlOptions();
          ArrayList<XmlValidationError> validationErrors = new ArrayList<XmlValidationError>();
          validationOptions.setErrorListener(validationErrors);
          boolean isSintassiXmlOK = istanzaSubappaltoDocument.validate(validationOptions);
  
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
            
            LottoSubappaltoType[] arrayLottoSubAppalto =
              istanzaSubappaltoDocument.getRichiestaSincronaIstanzaSubappalto().getListaLottiSubappaltiArray();
            
            if (arrayLottoSubAppalto != null && arrayLottoSubAppalto.length > 0) {
              // HashMap per caricare gli oggetti ResponseLottoType per ciascun lotto, con CIG come chiave della hashMap
              HashMap<String, ResponseLottoType> hmResponseLotti = new HashMap<String, ResponseLottoType>();
              
              StringBuilder strQueryCig = new StringBuilder("'");
              for (int contr = 0; contr < arrayLottoSubAppalto.length && result.isSuccess(); contr++) {
                LottoSubappaltoType oggettoSubappalto = arrayLottoSubAppalto[contr];
                String cigLotto = oggettoSubappalto.getW3CIG();
                strQueryCig.append(cigLotto);
                if (contr + 1 < arrayLottoSubAppalto.length)
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
              	if (listaCodGara.size() !=  arrayLottoSubAppalto.length) {
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
              
              for (int subapp = 0; subapp < arrayLottoSubAppalto.length && result.isSuccess(); subapp++) {
                LottoSubappaltoType oggettoLottoSubappalto = arrayLottoSubAppalto[subapp];
                String cigLotto = oggettoLottoSubappalto.getW3CIG();

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
	                
	                // Controlli preliminari su gara e su lotto.
	                Long faseEsecuz = new Long(CostantiWSW9.SUBAPPALTO);
	                SubappaltoType[] arraySubappalto = oggettoLottoSubappalto.getListaSubappaltiArray();
	                    
	                if (arraySubappalto != null && arraySubappalto.length > 0) {
	                  int numeroSubappaltiImportati = 0;
	                  int numeroSubappaltiNonImportati = 0;
	                  int numeroSubappaltiAggiornati = 0;
	                      
	                  for (int subap = 0; subap < arraySubappalto.length; subap++) {
	                    SubappaltoType lottoSubappalto = arraySubappalto[subap];
	                        
	                    // Controllo esistenza della fase: se no, si esegue l'insert di tutti dati, 
	                    // se si, si esegue l'update dei record
	                    boolean esisteFaseSubappalto = UtilitySITAT.existsFase(codiceGara, codiceLotto, new Long(1),
	                         faseEsecuz.intValue(), new Long(lottoSubappalto.getW3SUNUMSUBA()), this.sqlManager);
	                    if (esisteFaseSubappalto || UtilitySITAT.isFaseAbilitata(codiceGara, codiceLotto, new Long(1), faseEsecuz.intValue(), this.sqlManager)) {                        
	                      if (esisteFaseSubappalto || UtilitySITAT.isFaseVisualizzabile(codiceGara, codiceLotto, faseEsecuz.intValue(), this.sqlManager)) {
	                        
	                        // Record in W9FASI.
	                      	UtilitySITAT.istanziaFase(this.sqlManager, codiceGara, codiceLotto, faseEsecuz,
	                      				new Long(lottoSubappalto.getW3SUNUMSUBA()));
	                      	                                
	                        DataColumn codGaraSubap = new DataColumn("W9SUBA.CODGARA", 
	                            new JdbcParametro(JdbcParametro.TIPO_NUMERICO, codiceGara));
	                        DataColumn codLottSubap = new DataColumn("W9SUBA.CODLOTT", 
	                            new JdbcParametro(JdbcParametro.TIPO_NUMERICO, codiceLotto));
	                        DataColumn numSubap = new DataColumn("W9SUBA.NUM_SUBA",
	                            new JdbcParametro(JdbcParametro.TIPO_NUMERICO, 
	                                new Long(lottoSubappalto.getW3SUNUMSUBA())));
	                        DataColumn numAppa = new DataColumn("W9SUBA.NUM_APPA",
	                            new JdbcParametro(JdbcParametro.TIPO_NUMERICO, new Long(1)));
	                        DataColumnContainer dccSubappalto = new DataColumnContainer(new DataColumn[] { 
	                            codGaraSubap, codLottSubap, numSubap, numAppa } );
	                        
	                        if (lottoSubappalto.isSetW3DATAAUT()) {
	                          dccSubappalto.addColumn("W9SUBA.DATA_AUTORIZZAZIONE", 
	                              new JdbcParametro(JdbcParametro.TIPO_DATA,
	                                  lottoSubappalto.getW3DATAAUT().getTime()));
	                        }
	                        if (lottoSubappalto.isSetW3OSUBA()) {
	                          dccSubappalto.addColumn("W9SUBA.OGGETTO_SUBAPPALTO", 
	                              new JdbcParametro(JdbcParametro.TIPO_TESTO,
	                                  lottoSubappalto.getW3OSUBA()));
	                        }
	                        if (lottoSubappalto.isSetW3IPRESUN()) {
	                          dccSubappalto.addColumn("W9SUBA.IMPORTO_PRESUNTO", 
	                              new JdbcParametro(JdbcParametro.TIPO_DECIMALE,
	                                  lottoSubappalto.getW3IPRESUN()));
	                        }
	                        if (lottoSubappalto.isSetW3IEFFETT()) {
	                          dccSubappalto.addColumn("W9SUBA.IMPORTO_EFFETTIVO", 
	                              new JdbcParametro(JdbcParametro.TIPO_DECIMALE,
	                                  lottoSubappalto.getW3IEFFETT()));
	                        }
	                        if (lottoSubappalto.isSetW3IDCATE2()) {
	                          dccSubappalto.addColumn("W9SUBA.ID_CATEGORIA", 
	                              new JdbcParametro(JdbcParametro.TIPO_TESTO,
	                                  lottoSubappalto.getW3IDCATE2().toString()));
	                        }
	                        if (lottoSubappalto.isSetW3IDCPV()) {
	                          dccSubappalto.addColumn("W9SUBA.ID_CPV", 
	                              new JdbcParametro(JdbcParametro.TIPO_TESTO,
	                                  lottoSubappalto.getW3IDCPV()));
	                        }
	
	                        if (!esisteFaseSubappalto) {
	                          // Gestione dell'impresa
	                          ImpresaType impresaSubappalto = lottoSubappalto.getImpresa();
	                          
	                          boolean eseguiInsertImpresa = false;
	                          HashMap<String , Object> resultImp = UtilitySITAT.gestioneImpresa(codFiscaleStazAppaltante,
	                          		impresaSubappalto, eseguiInsertImpresa, this.sqlManager, this.geneManager);
	                  		    String codImpresa = (String) resultImp.get("CODIMP");
	
	                          dccSubappalto.addColumn("W9SUBA.CODIMP",
	                              new JdbcParametro(JdbcParametro.TIPO_TESTO, codImpresa));
	                          
	                          UtilitySITAT.gestioneLegaliRappresentanti(codFiscaleStazAppaltante, impresaSubappalto,
	                          		eseguiInsertImpresa, codImpresa, this.sqlManager, this.genChiaviManager);
	                          
	                          dccSubappalto.insert("W9SUBA", this.sqlManager);
	                          
	                          if (UtilitySITAT.isScritturaNoteAvvisiAttiva()) {
	                          	String codiceCIG = UtilitySITAT.getCIGLotto(codiceGara, codiceLotto, sqlManager);
	                         		UtilitySITAT.insertNoteAvvisi("W9LOTT", codiceGara.toString(), codiceLotto.toString(),
	                             		null, null, null, "CIG " + codiceCIG + ": inserita fase 'Subappalto contratto' n."
	                         				+ lottoSubappalto.getW3SUNUMSUBA(), null, this.sqlManager);		                              		
	                          }
	                          
	                        	numeroSubappaltiImportati++;
	                        	UtilitySITAT.aggiungiMsgSchedaB(hmResponseLotti, cigLotto, lottoSubappalto.getW3SUNUMSUBA(),
	                          		true, "Fase 'Subappalto' importata");
	
	                        } else if (esisteFaseSubappalto && sovrascrivereDatiEsistenti) {
	
	                          DataColumnContainer dccSubappaltoDB = new DataColumnContainer(this.sqlManager, "W9SUBA", 
	                          		"select DATA_AUTORIZZAZIONE, OGGETTO_SUBAPPALTO, IMPORTO_PRESUNTO, IMPORTO_EFFETTIVO, ID_CATEGORIA, ID_CPV, CODIMP, " 
	                          				 + "CODGARA, CODLOTT, NUM_SUBA, NUM_APPA "
	                          		+ "from W9SUBA where CODGARA=? and CODLOTT=? and NUM_SUBA=? and NUM_APPA=1",
	                          		new Object[] { codiceGara, codiceLotto, lottoSubappalto.getW3SUNUMSUBA() });
	                          
	                          Iterator<Entry<String, DataColumn>> iterInizioDB = dccSubappaltoDB.getColonne().entrySet().iterator();
	                          while (iterInizioDB.hasNext()) {
	                          	Entry<String, DataColumn> entry = iterInizioDB.next(); 
	                            String nomeCampo = entry.getKey();
	                            if (dccSubappalto.isColumn(nomeCampo)) {
	                            	dccSubappaltoDB.setValue(nomeCampo, dccSubappalto.getColumn(nomeCampo).getValue());
	                            }
	                          }
	                          
	                          /*if (lottoSubappalto.isSetW3DATAAUT()) {
	                            dccSubappaltoDB.setValue("W9SUBA.DATA_AUTORIZZAZIONE", 
	                                new JdbcParametro(JdbcParametro.TIPO_DATA, lottoSubappalto.getW3DATAAUT().getTime()));
	                          }
	
	                          if (lottoSubappalto.isSetW3OSUBA()) {
	                            dccSubappaltoDB.setValue("W9SUBA.OGGETTO_SUBAPPALTO", 
	                                new JdbcParametro(JdbcParametro.TIPO_TESTO, lottoSubappalto.getW3OSUBA()));
	                          }
	
	                          if (lottoSubappalto.isSetW3IPRESUN()) {
	                            dccSubappaltoDB.setValue("W9SUBA.IMPORTO_PRESUNTO", 
	                                new JdbcParametro(JdbcParametro.TIPO_DECIMALE, lottoSubappalto.getW3IPRESUN()));
	                          }
	
	                          if (lottoSubappalto.isSetW3IEFFETT()) {
	                            dccSubappaltoDB.setValue("W9SUBA.IMPORTO_EFFETTIVO", 
	                                new JdbcParametro(JdbcParametro.TIPO_DECIMALE, lottoSubappalto.getW3IEFFETT()));
	                          }
	
	                          if (lottoSubappalto.isSetW3IDCATE2()) {
	                            dccSubappaltoDB.setValue("W9SUBA.ID_CATEGORIA", 
	                                new JdbcParametro(JdbcParametro.TIPO_TESTO, lottoSubappalto.getW3IDCATE2().toString()));
	                          }
	
	                          if (lottoSubappalto.isSetW3IDCPV()) {
	                            dccSubappaltoDB.setValue("W9SUBA.ID_CPV", 
	                                new JdbcParametro(JdbcParametro.TIPO_TESTO, lottoSubappalto.getW3IDCPV()));
	                          }*/
	                        	
	                          // Gestione dell'impresa
	                          ImpresaType impresaSubappalto = lottoSubappalto.getImpresa();
	                          
	                          boolean eseguiInsertImpresa = false;
	                          HashMap<String , Object> resultImp = UtilitySITAT.gestioneImpresa(codFiscaleStazAppaltante,
	                          		impresaSubappalto, eseguiInsertImpresa, this.sqlManager, this.geneManager);
	                  		    String codImpresa = (String) resultImp.get("CODIMP");
	
	                          dccSubappaltoDB.setValue("W9SUBA.CODIMP",
	                              new JdbcParametro(JdbcParametro.TIPO_TESTO, codImpresa));
	                          
	                          UtilitySITAT.gestioneLegaliRappresentanti(codFiscaleStazAppaltante, impresaSubappalto,
	                          		eseguiInsertImpresa, codImpresa, this.sqlManager, this.genChiaviManager);
	                          
	                          if (dccSubappaltoDB.isModifiedTable("W9SUBA")){
	                          	dccSubappaltoDB.getColumn("W9SUBA.CODGARA").setChiave(true);
	                          	dccSubappaltoDB.getColumn("W9SUBA.CODLOTT").setChiave(true);
	                          	dccSubappaltoDB.getColumn("W9SUBA.NUM_SUBA").setChiave(true);
	                          	dccSubappaltoDB.update("W9SUBA", this.sqlManager);
	                          }
	                        	
	                        	if (UtilitySITAT.isScritturaNoteAvvisiAttiva()) {
	                          	String codiceCIG = UtilitySITAT.getCIGLotto(codiceGara, codiceLotto, sqlManager);
	                        		UtilitySITAT.insertNoteAvvisi("W9LOTT", codiceGara.toString(), codiceLotto.toString(),
	                             		null, null, null, "CIG " + codiceCIG + ": modificata fase 'Subappalto contratto' n."
	                         				+ lottoSubappalto.getW3SUNUMSUBA(), null, this.sqlManager);
	                          }
	
	                      		numeroSubappaltiAggiornati++;
	                      		UtilitySITAT.aggiungiMsgSchedaB(hmResponseLotti, cigLotto, lottoSubappalto.getW3SUNUMSUBA(),
	                      				true, "Fase 'Subappalto' aggiornata");
	
	                        } else {
	                          // Caso in cui in base dati esiste gia' la fase di subappalto per il lotto
	                          // ed il flag di sovrascrittura dei dati e' valorizzato a false.
	
	                          StringBuilder strLog = new StringBuilder(credenzialiUtente.getPrefissoLogger());
	                          strLog.append(" Il subappalto numero ");
	                          strLog.append(lottoSubappalto.getW3SUNUMSUBA());
	                          strLog.append(" del lotto con CIG=");
	                          strLog.append(cigLotto);
	                          strLog.append(" non e' stato importato perche' la fase gia' esiste nella base dati " +
	                              "e non la si vuole sovrascrivere.");
	                          logger.info(strLog.toString());
	
	                          UtilitySITAT.aggiungiMsgSchedaB(hmResponseLotti, cigLotto, lottoSubappalto.getW3SUNUMSUBA(), 
	                          		false, StringUtils.replace(ConfigManager.getValore("error.subappalto.esistente"),
		                        				"{0}", cigLotto));
	                        }
	                        } else {
	                        	// Fase subappalto non visibile
	                          StringBuilder strLog = new StringBuilder(credenzialiUtente.getPrefissoLogger());
	                          strLog.append(" Il subappalto del contratto del lotto con CIG='");
	                          strLog.append(cigLotto);
	                          strLog.append(" non e' stato importato perche' non ha superato i controlli preliminari " +
	                          		" della fase del contratto. Nel caso specifico la condizione " +
	                          		"((isS2 || isAII) && !isE1 && isEA) non risulta verificata.");
	                          logger.error(strLog);
	                          
	                          numeroLottiNonImportati++;
	                          
	                          UtilitySITAT.aggiungiMsgSchedaA(hmResponseLotti, cigLotto, false,
	                          		StringUtils.replace(ConfigManager.getValore("error.subappalto.noVisibile"),
	                          				"{0}", cigLotto));
	                        }
	
	                    } else {
	                    	// Fase subappalto non abilitata
	                      StringBuilder strLog = new StringBuilder(credenzialiUtente.getPrefissoLogger());
	                      
	                      Boolean existCollaudo =  UtilitySITAT.existsFaseEsportata(cigLotto, CostantiWSW9.COLLAUDO_CONTRATTO, sqlManager);
	                      
	                      strLog.append("Il lotto con CIG='");
	                      strLog.append(cigLotto);
	                      if (existCollaudo){
	                    	  strLog.append("' ha gia' inviato la fase di collaudo. Impossibile importare il subappalto del contratto." );
	                      } else {
	                    	  strLog.append("' non ha la fase di inizio contratto. Impossibile importare il subappalto del contratto." );
	                      }
	                      logger.error(strLog);
	
	                      numeroLottiNonImportati++;
	                      if (existCollaudo){
	                    	  UtilitySITAT.aggiungiMsgSchedaA(hmResponseLotti, cigLotto, false,
	                    			  StringUtils.replace(ConfigManager.getValore("error.subappalto.flussiSuccessivi"),
	                    					  "{0}", cigLotto));                 	  
	                      } else {
	                    	  UtilitySITAT.aggiungiMsgSchedaA(hmResponseLotti, cigLotto, false,
	                    			  StringUtils.replace(ConfigManager.getValore("error.subappalto.noFlussiPrecedenti"),
	                    					  "{0}", cigLotto)); 
	                      }
	                    }
	                  } // chiusura ciclo for sui subappalti di un lotto
	                  
	                  if (numeroSubappaltiImportati > 0) {
	                    numeroLottiImportati += numeroSubappaltiImportati;
	                  } 
	                 	if (numeroSubappaltiAggiornati > 0) {
	                    numeroLottiAggiornati += numeroSubappaltiAggiornati;
	                  } 
	                 	if (numeroSubappaltiNonImportati > 0) {
	                    numeroLottiNonImportati += numeroSubappaltiNonImportati;
	                  }
	                } else {
	                  // Array dei Subappalti vuoto: non si dovrebbe verificare mai per vincoli del xsd.
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
              } // Chiusura ciclo for sui lotti presenti nel XML.
              
              UtilitySITAT.preparaRisultatoMessaggio(result, sovrascrivereDatiEsistenti, hmResponseLotti,
									numeroLottiImportati, numeroLottiNonImportati, numeroLottiAggiornati);
            }
          }
        } else {
          // E' stato inviato un messaggio di test.
          StringBuilder strLog = new StringBuilder(credenzialiUtente.getPrefissoLogger());
          strLog.append(" E' stato inviato un messaggio di test. Tale messaggio non e' stato elaborato.'\n");
          strLog.append("Messaggio: ");
          strLog.append(subappalto.toString());
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
      logger.debug("istanziaSubappalto: fine metodo");
      
      logger.debug("Response : " + UtilitySITAT.messaggioLogEsteso(result));
    }
    
    // MODIFICA TEMPORANEA -- DA CANCELLARE ---------------
    result.setError(UtilitySITAT.messaggioEsteso(result));
    // ----------------------------------------------------
        
    return result;
  }
  
}
