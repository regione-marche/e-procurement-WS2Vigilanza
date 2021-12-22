package it.eldasoft.sil.vigilanza.bl;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.db.datautils.DataColumn;
import it.eldasoft.gene.db.datautils.DataColumnContainer;
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.sil.vigilanza.beans.LottoVarianteType;
import it.eldasoft.sil.vigilanza.beans.MotivazioneType;
import it.eldasoft.sil.vigilanza.beans.RichiestaSincronaIstanzaVarianteDocument;
import it.eldasoft.sil.vigilanza.beans.VarianteType;
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
 * Classe per l'import dei dati delle varianti dei lotti della gara.
 * 
 * @author Luca.Giacomazzo
 */
public class IstanzaVarianteManager {

private static Logger logger = Logger.getLogger(IstanzaVarianteManager.class);
  
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
   * @param variante IstanzaOggettoType
   * @return Ritorna l'oggetto ResponseType con l'esito dell'operazione
   * @throws XmlException 
   * @throws GestoreException 
   * @throws SQLException 
   * @throws Throwable
   */
  public ResponseType istanziaVariante(LoginType login, IstanzaOggettoType variante)
      throws XmlException, GestoreException, SQLException, Throwable {
    ResponseType result = null;
    
    if (logger.isDebugEnabled()) {
      logger.debug("istanziaVariante: inizio metodo");
      
      logger.debug("XML : " + variante.getOggettoXML());
    }
  
    // Codice fiscale della Stazione appaltante
    String codFiscaleStazAppaltante = variante.getTestata().getCFEIN();
    
    boolean sovrascrivereDatiEsistenti = false;
    if (variante.getTestata().getSOVRASCR() != null) {
    	sovrascrivereDatiEsistenti = variante.getTestata().getSOVRASCR().booleanValue(); 
    }
    
    // Verifica di login, password e determinazione della S.A.
    HashMap<String, Object> hm = this.credenzialiManager.verificaCredenziali(login, codFiscaleStazAppaltante);
    result = (ResponseType) hm.get("RESULT");
    
    if (result.isSuccess()) {
      Credenziale credenzialiUtente = (Credenziale) hm.get("USER");
      String xmlVariante = variante.getOggettoXML();
      
      try {
        RichiestaSincronaIstanzaVarianteDocument istanzaVarianteDocument =
          RichiestaSincronaIstanzaVarianteDocument.Factory.parse(xmlVariante);

        boolean isMessaggioDiTest = 
          istanzaVarianteDocument.getRichiestaSincronaIstanzaVariante().isSetTest()
            && istanzaVarianteDocument.getRichiestaSincronaIstanzaVariante().getTest();
        
        if (! isMessaggioDiTest) {
        
          // si esegue il controllo sintattico del messaggio
          XmlOptions validationOptions = new XmlOptions();
          ArrayList<XmlValidationError> validationErrors = new ArrayList<XmlValidationError>();
          validationOptions.setErrorListener(validationErrors);
          boolean isSintassiXmlOK = istanzaVarianteDocument.validate(validationOptions);
  
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
            LottoVarianteType[] arrayLottoVariante =
              istanzaVarianteDocument.getRichiestaSincronaIstanzaVariante().getListaLottiVariantiArray();

            if (arrayLottoVariante != null && arrayLottoVariante.length > 0) {
              // HashMap per caricare gli oggetti ResponseLottoType per ciascun lotto, con CIG come chiave della hashMap
              HashMap<String, ResponseLottoType> hmResponseLotti = new HashMap<String, ResponseLottoType>();
              
              StringBuilder strQueryCig = new StringBuilder("'");
              for (int contr = 0; contr < arrayLottoVariante.length && result.isSuccess(); contr++) {
                LottoVarianteType oggettoVariante = arrayLottoVariante[contr];
                String cigLotto = oggettoVariante.getW3CIG();
                
                strQueryCig.append(cigLotto);
                if (contr + 1 < arrayLottoVariante.length)
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
              	throw new WSVigilanzaException("Attenzione: la scheda non e' inviabile poiche' non esiste la gara nel sistema di destinazione. " +
              			"E' necessario provvedere preventivamente alla sua creazione, importandola da Simog ");
              } else {
              	if (listaCodGara.size() !=  arrayLottoVariante.length) {
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
              
              for (int varian = 0; varian < arrayLottoVariante.length && result.isSuccess(); varian++) {
              	LottoVarianteType oggettoLottoVariante = arrayLottoVariante[varian];
              	String cigLotto = oggettoLottoVariante.getW3CIG();
				
              	HashMap<String, Long> hashM = UtilitySITAT.getCodGaraCodLottByCIG(cigLotto, this.sqlManager);
              	Long codiceGara = hashM.get("CODGARA");
              	Long codiceLotto = hashM.get("CODLOTT");
              	//verifica monitoraggio multilotto - verifico se il campo CIG_MASTER_ML è valorizzato
                String cigMaster = (String) this.sqlManager.getObject("select CIG_MASTER_ML from W9LOTT where CODGARA=? and CODLOTT=?", new Object[] { codiceGara, codiceLotto });
                
              	if (!UtilitySITAT.isLottoRiaggiudicato(codiceGara, codiceLotto, this.sqlManager) && (cigMaster == null || cigMaster.trim().length() == 0)) {
	              	if (!UtilitySITAT.isUtenteAmministratore(credenzialiUtente)) {
	              		if (!UtilitySITAT.isUtenteRupDelLotto(cigLotto, credenzialiUtente, this.sqlManager)) {
	              				logger.error(credenzialiUtente.getPrefissoLogger() + "L'utente non e' RUP del lotto con CIG=" + cigLotto
	              						+ ", oppure nella gara e/o nel lotto non e' stato indicato il RUP");
	
	              				throw new WSVigilanzaException("Le credenziali fornite non coincidono con quelle del RUP indicato");
	              		}
	              	}

	              	VarianteType[] arrayVarianti = oggettoLottoVariante.getListaVariantiArray();
	              	if (arrayVarianti != null && arrayVarianti.length > 0) {

	              		int numeroVariantiImportate = 0;
	              		int numeroVariantiNonImportate = 0;
	              		int numeroVariantiAggiornate = 0;
	
	              		Long faseEsecuz = new Long(CostantiWSW9.VARIANTE_CONTRATTO);
	              		for (int varia=0; varia < arrayVarianti.length; varia++) {
	              			VarianteType lottoVariante = arrayVarianti[varia];

										// Controllo esistenza della fase: se no, si esegue l'insert di tutti dati, 
										// se si, si esegue l'update dei record
										boolean esisteFaseVariante = UtilitySITAT.existsFase(codiceGara, codiceLotto, new Long(1),
												faseEsecuz.intValue(), new Long(lottoVariante.getW3VANUMVARI()), this.sqlManager);
							
										// Controlli preliminari su gara e su lotto.
										if (esisteFaseVariante || UtilitySITAT.isFaseAbilitata(codiceGara, codiceLotto, new Long(1), faseEsecuz.intValue(), this.sqlManager)) {
											if (esisteFaseVariante || UtilitySITAT.isFaseVisualizzabile(codiceGara, codiceLotto, faseEsecuz.intValue(), this.sqlManager)) {

												// Record in W9FASI.
												UtilitySITAT.istanziaFase(this.sqlManager, codiceGara, codiceLotto, 
														faseEsecuz, new Long(lottoVariante.getW3VANUMVARI()));

												DataColumn codGaraVar = new DataColumn("W9VARI.CODGARA", 
														new JdbcParametro(JdbcParametro.TIPO_NUMERICO, codiceGara));
												DataColumn codLottVar = new DataColumn("W9VARI.CODLOTT", 
													new JdbcParametro(JdbcParametro.TIPO_NUMERICO, codiceLotto));
												DataColumn numVar = new DataColumn("W9VARI.NUM_VARI",
													new JdbcParametro(JdbcParametro.TIPO_NUMERICO, 
														new Long(lottoVariante.getW3VANUMVARI())));
												DataColumn numAppa = new DataColumn("W9VARI.NUM_APPA",
														new JdbcParametro(JdbcParametro.TIPO_NUMERICO, new Long(1)));
												DataColumnContainer dccVariante = new DataColumnContainer(new DataColumn[] {
													codGaraVar, codLottVar, numVar, numAppa });
			
												if (lottoVariante.isSetW3DVERBAP()) {
												  dccVariante.addColumn("W9VARI.DATA_VERB_APPR", 
													  new JdbcParametro(JdbcParametro.TIPO_DATA,
														  lottoVariante.getW3DVERBAP().getTime()));
												}
												if (lottoVariante.isSetW3ALTREMO()) {
												  dccVariante.addColumn("W9VARI.ALTRE_MOTIVAZIONI", 
													  new JdbcParametro(JdbcParametro.TIPO_TESTO,
														  lottoVariante.getW3ALTREMO()));
												}
												double importoRideterminatoLavori = 0;
												double importoRideterminatoServizi = 0;
												double importoRideterminatoForniture = 0;
												double importoAttuazioneSicurezza = 0;
												double importoProgettazione = 0;
												double importoSommeADisposizione = 0;
												double importoSommeNonAssoggettareARibasso = 0;
												
												if (lottoVariante.isSetW3IRLAVOR()) {
												  dccVariante.addColumn("W9VARI.IMP_RIDET_LAVORI",
													  new JdbcParametro(JdbcParametro.TIPO_DECIMALE,
														  lottoVariante.getW3IRLAVOR()));
												  importoRideterminatoLavori = lottoVariante.getW3IRLAVOR();
												}
									
												if (lottoVariante.isSetW3IRSERVI()) {
												  dccVariante.addColumn("W9VARI.IMP_RIDET_SERVIZI",
													  new JdbcParametro(JdbcParametro.TIPO_DECIMALE,
														  lottoVariante.getW3IRSERVI()));
												  importoRideterminatoServizi = lottoVariante.getW3IRSERVI();
												}
												
												if (lottoVariante.isSetW3IRFORNI()) {
												  dccVariante.addColumn("W9VARI.IMP_RIDET_FORNIT",
													  new JdbcParametro(JdbcParametro.TIPO_DECIMALE,
														  lottoVariante.getW3IRFORNI()));
												  importoRideterminatoForniture = lottoVariante.getW3IRFORNI();
												}
												if (lottoVariante.isSetW3IMPSICU()) {
												  dccVariante.addColumn("W9VARI.IMP_SICUREZZA", 
													  new JdbcParametro(JdbcParametro.TIPO_DECIMALE,
														  lottoVariante.getW3IMPSICU()));
												  importoAttuazioneSicurezza = lottoVariante.getW3IMPSICU();
												}
												if (lottoVariante.isSetW3IMPPRO1()) {
												  dccVariante.addColumn("W9VARI.IMP_PROGETTAZIONE",
													  new JdbcParametro(JdbcParametro.TIPO_DECIMALE,
														  lottoVariante.getW3IMPPRO1()));
												  importoProgettazione = lottoVariante.getW3IMPPRO1();
												}
												
												if (lottoVariante.isSetW3INONAS1()) {
													dccVariante.addColumn("W9VARI.IMP_NON_ASSOG",
													  new JdbcParametro(JdbcParametro.TIPO_DECIMALE,
														  lottoVariante.getW3INONAS1()));
													importoSommeNonAssoggettareARibasso = lottoVariante.getW3INONAS1();
												}
												if (lottoVariante.isSetW3IMPDIS1()) {
												  dccVariante.addColumn("W9VARI.IMP_DISPOSIZIONE",
													  new JdbcParametro(JdbcParametro.TIPO_DECIMALE,
														  lottoVariante.getW3IMPDIS1()));
												  importoSommeADisposizione = lottoVariante.getW3IMPDIS1();
												}
			
												dccVariante.addColumn("W9VARI.IMP_SUBTOTALE", 
													  new JdbcParametro(JdbcParametro.TIPO_DECIMALE,
														  importoRideterminatoLavori + importoRideterminatoServizi + importoRideterminatoForniture));
												
												dccVariante.addColumn("W9VARI.IMP_COMPL_APPALTO", 
													new JdbcParametro(JdbcParametro.TIPO_DECIMALE,
														importoRideterminatoLavori + importoRideterminatoServizi + importoRideterminatoForniture
														+ importoAttuazioneSicurezza + importoProgettazione + importoSommeNonAssoggettareARibasso));
			
												dccVariante.addColumn("W9VARI.IMP_COMPL_INTERVENTO", 
													new JdbcParametro(JdbcParametro.TIPO_DECIMALE,
														importoRideterminatoLavori + importoRideterminatoServizi + importoRideterminatoForniture
														+ importoAttuazioneSicurezza + importoProgettazione + importoSommeNonAssoggettareARibasso
														+ importoSommeADisposizione));
												
												if (lottoVariante.isSetW3DATAATT()) {
												  dccVariante.addColumn("W9VARI.DATA_ATTO_AGGIUNTIVO", 
													  new JdbcParametro(JdbcParametro.TIPO_DATA,
														  lottoVariante.getW3DATAATT().getTime()));
												}
												if (lottoVariante.isSetW3NUMGIO3()) {
												  dccVariante.addColumn("W9VARI.NUM_GIORNI_PROROGA",
													  new JdbcParametro(JdbcParametro.TIPO_NUMERICO,
														  new Long(lottoVariante.getW3NUMGIO3())));
												}
												if (lottoVariante.isSetW3CIGNPROC()) {
													dccVariante.addColumn("W9VARI.CIG_NUOVA_PROC",
													  new JdbcParametro(JdbcParametro.TIPO_TESTO,
														  lottoVariante.getW3CIGNPROC()));
												}
			
												if (!esisteFaseVariante) {
													dccVariante.insert("W9VARI", this.sqlManager);
												  
													// Insert delle motivazioni.
													if (lottoVariante.getListaMotivazioniArray() != null && lottoVariante.getListaMotivazioniArray().length > 0) {
														for (int motiv = 0; motiv < lottoVariante.getListaMotivazioniArray().length; motiv++) {
															MotivazioneType motivazione = lottoVariante.getListaMotivazioniArray(motiv);
													  
															this.insertMotivazione(codiceGara, codiceLotto, lottoVariante, motiv, motivazione);
														}
													}

													numeroVariantiImportate++;
													UtilitySITAT.aggiungiMsgSchedaB(hmResponseLotti, cigLotto, lottoVariante.getW3VANUMVARI(), true, 
														"Fase 'Variante contratto' importata");

													if (UtilitySITAT.isScritturaNoteAvvisiAttiva()) {
														String codiceCIG = UtilitySITAT.getCIGLotto(codiceGara, codiceLotto, this.sqlManager);
														UtilitySITAT.insertNoteAvvisi("W9LOTT", codiceGara.toString(), codiceLotto.toString(),
																null, null, null, "CIG " + codiceCIG + ": inserita fase 'Variante contratto' n."
																+ lottoVariante.getW3VANUMVARI(), null, this.sqlManager);
													}
												} else if (esisteFaseVariante && sovrascrivereDatiEsistenti) {

													DataColumnContainer dccVarianteDB = new DataColumnContainer(this.sqlManager, "W9VARI",
														"select DATA_VERB_APPR, ALTRE_MOTIVAZIONI, IMP_RIDET_LAVORI, IMP_RIDET_SERVIZI, IMP_RIDET_FORNIT, "
																+ "IMP_SICUREZZA, IMP_PROGETTAZIONE, IMP_NON_ASSOG, IMP_DISPOSIZIONE, DATA_ATTO_AGGIUNTIVO, "
																+ "NUM_GIORNI_PROROGA, CIG_NUOVA_PROC, IMP_SUBTOTALE, IMP_COMPL_APPALTO, IMP_COMPL_INTERVENTO, "
																+ "CODGARA, CODLOTT, NUM_VARI, NUM_APPA "
															+ " from W9VARI where CODGARA=? and CODLOTT=? and NUM_VARI=? and NUM_APPA=1",
														new Object[] { codiceGara, codiceLotto, lottoVariante.getW3VANUMVARI() } );
			
													Iterator<Entry<String, DataColumn>> iterInizioDB = dccVarianteDB.getColonne().entrySet().iterator();
													while (iterInizioDB.hasNext()) {
														Entry<String, DataColumn> entry = iterInizioDB.next(); 
														String nomeCampo = entry.getKey();
														if (dccVariante.isColumn(nomeCampo)) {
															dccVarianteDB.setValue(nomeCampo, dccVariante.getColumn(nomeCampo).getValue());
														}
													}

													/*if (lottoVariante.isSetW3DVERBAP()) {
														dccVarianteDB.setValue("W9VARI.DATA_VERB_APPR", 
															new JdbcParametro(JdbcParametro.TIPO_DATA,
																lottoVariante.getW3DVERBAP().getTime()));
													}
													if (lottoVariante.isSetW3ALTREMO()) {
														dccVarianteDB.setValue("W9VARI.ALTRE_MOTIVAZIONI", 
															new JdbcParametro(JdbcParametro.TIPO_TESTO,
																lottoVariante.getW3ALTREMO()));
													}
													double importoRideterminatoLavori = -1;
													double importoRideterminatoServizi = -1;
													double importoRideterminatoForniture = -1;
													double importoAttuazioneSicurezza = -1;
													double importoProgettazione = -1;
													double importoSommeADisposizione = -1;
													double importoSommeNonAssoggettareARibasso = -1;
													  
													if (lottoVariante.isSetW3IRLAVOR()) {
														dccVarianteDB.setValue("W9VARI.IMP_RIDET_LAVORI",
															new JdbcParametro(JdbcParametro.TIPO_DECIMALE, lottoVariante.getW3IRLAVOR()));
														importoRideterminatoLavori = lottoVariante.getW3IRLAVOR();
													} else if (dccVarianteDB.getColumn("W9VARI.IMP_RIDET_LAVORI").getValue().getValue() != null) {
														importoRideterminatoLavori = dccVarianteDB.getDouble("W9VARI.IMP_RIDET_LAVORI").doubleValue();
													}
													  
													if (lottoVariante.isSetW3IRSERVI()) {
														dccVarianteDB.setValue("W9VARI.IMP_RIDET_SERVIZI",
															new JdbcParametro(JdbcParametro.TIPO_DECIMALE, lottoVariante.getW3IRSERVI()));
														importoRideterminatoServizi = lottoVariante.getW3IRSERVI();
													} else if (dccVarianteDB.getColumn("W9VARI.IMP_RIDET_LAVORI").getValue().getValue() != null) {
														importoRideterminatoServizi = dccVarianteDB.getDouble("W9VARI.IMP_RIDET_LAVORI").doubleValue();
													}
													if (lottoVariante.isSetW3IRFORNI()) {
														dccVarianteDB.setValue("W9VARI.IMP_RIDET_FORNIT",
															new JdbcParametro(JdbcParametro.TIPO_DECIMALE, lottoVariante.getW3IRFORNI()));
														importoRideterminatoForniture = lottoVariante.getW3IRFORNI();
													} else if (dccVarianteDB.getColumn("W9VARI.IMP_RIDET_FORNIT").getValue().getValue() != null) {
														importoRideterminatoForniture = dccVarianteDB.getDouble("W9VARI.IMP_RIDET_FORNIT").doubleValue();
													}
													if (lottoVariante.isSetW3IMPSICU()) {
														dccVarianteDB.setValue("W9VARI.IMP_SICUREZZA", 
															new JdbcParametro(JdbcParametro.TIPO_DECIMALE, lottoVariante.getW3IMPSICU()));
														importoAttuazioneSicurezza = lottoVariante.getW3IMPSICU();
													} else if (dccVarianteDB.getColumn("W9VARI.IMP_SICUREZZA").getValue().getValue() != null) {
														importoAttuazioneSicurezza = dccVarianteDB.getDouble("W9VARI.IMP_SICUREZZA").doubleValue();
													}
													if (lottoVariante.isSetW3IMPPRO1()) {
														dccVarianteDB.setValue("W9VARI.IMP_PROGETTAZIONE",
															new JdbcParametro(JdbcParametro.TIPO_DECIMALE, lottoVariante.getW3IMPPRO1()));
														importoProgettazione = lottoVariante.getW3IMPPRO1();
													} else if (dccVarianteDB.getColumn("W9VARI.IMP_PROGETTAZIONE").getValue().getValue() != null) {
														importoProgettazione = dccVarianteDB.getDouble("W9VARI.IMP_PROGETTAZIONE").doubleValue();
													}
													if (lottoVariante.isSetW3INONAS1()) {
														dccVarianteDB.setValue("W9VARI.IMP_NON_ASSOG",
															new JdbcParametro(JdbcParametro.TIPO_DECIMALE, lottoVariante.getW3INONAS1()));
														importoSommeNonAssoggettareARibasso = lottoVariante.getW3INONAS1();
													} else if (dccVarianteDB.getColumn("W9VARI.IMP_NON_ASSOG").getValue().getValue() != null) {
														importoSommeNonAssoggettareARibasso = dccVarianteDB.getDouble("W9VARI.IMP_NON_ASSOG").doubleValue();
													}
													if (lottoVariante.isSetW3IMPDIS1()) {
														dccVarianteDB.setValue("W9VARI.IMP_DISPOSIZIONE",
															new JdbcParametro(JdbcParametro.TIPO_DECIMALE,
																lottoVariante.getW3IMPDIS1()));
														importoSommeADisposizione = lottoVariante.getW3IMPDIS1();
													} else if (dccVarianteDB.getColumn("W9VARI.IMP_DISPOSIZIONE").getValue().getValue() != null) {
															importoSommeADisposizione = dccVarianteDB.getDouble("W9VARI.IMP_DISPOSIZIONE").doubleValue();
													}
													if (lottoVariante.isSetW3DATAATT()) {
														dccVarianteDB.setValue("W9VARI.DATA_ATTO_AGGIUNTIVO", 
															new JdbcParametro(JdbcParametro.TIPO_DATA,
																lottoVariante.getW3DATAATT().getTime()));
													}
													if (lottoVariante.isSetW3NUMGIO3()) {
														dccVarianteDB.setValue("W9VARI.NUM_GIORNI_PROROGA",
															new JdbcParametro(JdbcParametro.TIPO_NUMERICO,
																new Long(lottoVariante.getW3NUMGIO3())));
													}
													if (lottoVariante.isSetW3CIGNPROC()) {
														dccVarianteDB.setValue("W9VARI.CIG_NUOVA_PROC",
															new JdbcParametro(JdbcParametro.TIPO_TESTO,
																lottoVariante.getW3CIGNPROC()));
													} */                       	
			
													HashMap<String, Object> hmImporti = this.sqlManager.getHashMap(
														"select IMP_RIDET_LAVORI, IMP_RIDET_SERVIZI, IMP_RIDET_FORNIT, IMP_SICUREZZA, "
														+ " IMP_PROGETTAZIONE, IMP_NON_ASSOG, IMP_DISPOSIZIONE "
														+ " from W9VARI where CODGARA=? and CODLOTT=? and NUM_VARI=? and NUM_APPA=1",
														new Object[] { codiceGara, codiceLotto, lottoVariante.getW3VANUMVARI() } );
		
													importoRideterminatoLavori = -1;
													importoRideterminatoServizi = -1;
													importoRideterminatoForniture = -1;
													importoAttuazioneSicurezza = -1;
													importoProgettazione = -1;
													importoSommeADisposizione = -1;
													importoSommeNonAssoggettareARibasso = -1;
			
													Object objImportoLavori = ((JdbcParametro) hmImporti.get("IMP_RIDET_LAVORI")).getValue();
													Object objImportoServizi = ((JdbcParametro) hmImporti.get("IMP_RIDET_SERVIZI")).getValue();
													Object objImportoForniture = ((JdbcParametro) hmImporti.get("IMP_RIDET_FORNIT")).getValue();
													Object objImportoAttuazSic = ((JdbcParametro) hmImporti.get("IMP_SICUREZZA")).getValue();
													Object objImportoProgettaz = ((JdbcParametro) hmImporti.get("IMP_PROGETTAZIONE")).getValue();
													Object objImportoNonAssog = ((JdbcParametro) hmImporti.get("IMP_NON_ASSOG")).getValue();
			
													if (objImportoLavori != null) {
														if (objImportoLavori instanceof Double) {
															importoRideterminatoLavori = ((Double) objImportoLavori).doubleValue();
														} else if (objImportoLavori instanceof Long) {
															importoRideterminatoLavori = ((Long) objImportoLavori).doubleValue();
														} 
													}
													if (objImportoServizi != null) {
														if (objImportoServizi instanceof Double) {
															importoRideterminatoServizi = ((Double) objImportoServizi).doubleValue();
														} else if (objImportoServizi instanceof Long) {
															importoRideterminatoServizi = ((Long) objImportoServizi).doubleValue();
														}
													}
													if (objImportoForniture != null) {
														if (objImportoForniture instanceof Double) {
															importoRideterminatoForniture = ((Double) objImportoForniture).doubleValue();
														} else if (objImportoForniture instanceof Long) {
															importoRideterminatoForniture = ((Long) objImportoForniture).doubleValue();
														}
													}
													if (objImportoAttuazSic != null) {
														if (objImportoAttuazSic instanceof Double) {
															importoAttuazioneSicurezza = ((Double) objImportoAttuazSic).doubleValue();
														} else if (objImportoAttuazSic instanceof Long) {
															importoAttuazioneSicurezza = ((Long) objImportoAttuazSic).doubleValue();
														}
													}
													if (objImportoProgettaz != null) {
														if (objImportoProgettaz instanceof Double) {
															importoProgettazione = ((Double) objImportoProgettaz).doubleValue();
														} else if (objImportoProgettaz instanceof Long) {
															importoProgettazione = ((Long) objImportoProgettaz).doubleValue();
														}
													}
													if (objImportoNonAssog != null) {
														if (objImportoNonAssog instanceof Double) {
															importoSommeNonAssoggettareARibasso = ((Double) objImportoNonAssog).doubleValue();
														} else if (objImportoNonAssog instanceof Long) {
															importoSommeNonAssoggettareARibasso = ((Long) objImportoNonAssog).doubleValue();
														}
													}
			
													// Ricalcolo del campo IMP_SUBTOTALE
													if (importoRideterminatoLavori > -1 || importoRideterminatoServizi > -1 || importoRideterminatoForniture > -1) {
														if (importoRideterminatoLavori < 0)
															importoRideterminatoLavori = 0;
														if (importoRideterminatoForniture < 0)
															importoRideterminatoForniture = 0;
														if (importoRideterminatoServizi < 0) 
															importoRideterminatoServizi = 0;
														dccVarianteDB.setValue("W9VARI.IMP_SUBTOTALE", 
															new JdbcParametro(JdbcParametro.TIPO_DECIMALE,
																importoRideterminatoLavori + importoRideterminatoServizi + importoRideterminatoForniture));
													}
			
													// Ricalcolo del campo IMP_COMPL_APPALTO
													if (importoRideterminatoLavori > -1 || importoRideterminatoServizi > -1 || importoRideterminatoForniture > -1
														|| importoAttuazioneSicurezza > -1 || importoProgettazione > -1 || importoSommeNonAssoggettareARibasso > -1) {
														if (importoRideterminatoLavori < 0)
															importoRideterminatoLavori = 0;
														if (importoRideterminatoForniture < 0)
															importoRideterminatoForniture = 0;
														if (importoRideterminatoServizi < 0) 
															importoRideterminatoServizi = 0;
														if (importoAttuazioneSicurezza < 0)
															importoAttuazioneSicurezza = 0;
														if (importoProgettazione < 0)
															importoProgettazione = 0;
														if (importoSommeNonAssoggettareARibasso < 0)
															importoSommeNonAssoggettareARibasso = 0;
			
														dccVarianteDB.setValue("W9VARI.IMP_COMPL_APPALTO", 
															new JdbcParametro(JdbcParametro.TIPO_DECIMALE,
																	importoRideterminatoLavori + importoRideterminatoServizi + importoRideterminatoForniture
																	+ importoAttuazioneSicurezza + importoProgettazione + importoSommeNonAssoggettareARibasso));
													}
		
													// Ricalcolo del campo IMP_COMPL_INTERVENTO
													if (importoRideterminatoLavori > -1 || importoRideterminatoServizi > -1 || importoRideterminatoForniture > -1
														|| importoAttuazioneSicurezza > -1 || importoProgettazione > -1 || importoSommeNonAssoggettareARibasso > -1
															|| importoSommeADisposizione > -1) {
			
														if (importoRideterminatoLavori < 0)
															importoRideterminatoLavori = 0;
														if (importoRideterminatoForniture < 0)
															importoRideterminatoForniture = 0;
														if (importoRideterminatoServizi < 0) 
															importoRideterminatoServizi = 0;
														if (importoAttuazioneSicurezza < 0)
															importoAttuazioneSicurezza = 0;
														if (importoProgettazione < 0)
															importoProgettazione = 0;
														if (importoSommeNonAssoggettareARibasso < 0)
															importoSommeNonAssoggettareARibasso = 0;
														if (importoSommeADisposizione < 0)
															importoSommeADisposizione = 0;
														
															dccVarianteDB.setValue("W9VARI.IMP_COMPL_INTERVENTO", 
																new JdbcParametro(JdbcParametro.TIPO_DECIMALE,
																		importoRideterminatoLavori + importoRideterminatoServizi + importoRideterminatoForniture
																		+ importoAttuazioneSicurezza + importoProgettazione + importoSommeNonAssoggettareARibasso
																		+ importoSommeADisposizione));
														}
												  
														if (dccVarianteDB.isModifiedTable("W9VARI")) {
															dccVarianteDB.getColumn("W9VARI.CODGARA").setChiave(true);
															dccVarianteDB.getColumn("W9VARI.CODLOTT").setChiave(true);
															dccVarianteDB.getColumn("W9VARI.NUM_VARI").setChiave(true);
															dccVarianteDB.update("W9VARI", this.sqlManager);
														}
			
														// Delete & insert delle motivazioni.
														if (lottoVariante.getListaMotivazioniArray() != null && lottoVariante.getListaMotivazioniArray().length > 0) {
															this.sqlManager.update("delete from W9MOTI where CODGARA=? and CODLOTT=?", new Object[] { codiceGara, codiceLotto });
															for (int motiv = 0; motiv < lottoVariante.getListaMotivazioniArray().length; motiv++) {
																MotivazioneType motivazione = lottoVariante.getListaMotivazioniArray(motiv);
																this.insertMotivazione(codiceGara, codiceLotto, lottoVariante, motiv, motivazione);
															}
														}
				
														numeroVariantiAggiornate++;
														UtilitySITAT.aggiungiMsgSchedaB(hmResponseLotti, cigLotto, lottoVariante.getW3VANUMVARI(), true, 
																"Fase 'Variante contratto' aggiornata");
				
														if (UtilitySITAT.isScritturaNoteAvvisiAttiva()) {
															String codiceCIG = UtilitySITAT.getCIGLotto(codiceGara, codiceLotto, this.sqlManager);
															UtilitySITAT.insertNoteAvvisi("W9LOTT", codiceGara.toString(), codiceLotto.toString(),
																null, null, null, "CIG " + codiceCIG + ": modificata fase 'Variante contratto' n."
																+ lottoVariante.getW3VANUMVARI(), null, this.sqlManager);
														}
												
													} else {
														// Caso in cui in base dati esiste gia' la fase di variante per il lotto
														// ed il flag di sovrascrittura dei dati e' valorizzato a false.
												
														StringBuilder strLog = new StringBuilder(credenzialiUtente.getPrefissoLogger());
														strLog.append(" La variante numero ");
														strLog.append(new Long(lottoVariante.getW3VANUMVARI()));
														strLog.append(" del lotto con CIG=");
														strLog.append(cigLotto);
														strLog.append(" non e' stata importata perche' la fase gia' esiste nella base dati " +
															"e non la si vuole sovrascrivere.");
														logger.info(strLog.toString());
														
														numeroVariantiNonImportate++;
														
														UtilitySITAT.aggiungiMsgSchedaB(hmResponseLotti, cigLotto, lottoVariante.getW3VANUMVARI(), 
																false, StringUtils.replace(ConfigManager.getValore("error.variante.esistente"),
																		"{0}", cigLotto));
													}
												} else {
													StringBuilder strLog =
														new StringBuilder(credenzialiUtente.getPrefissoLogger());
													strLog.append(" I controlli preliminari non sono stati superati. Il contratto del lotto con CIG='");
													strLog.append(cigLotto);
													strLog.append(" non e' stato importato perche' non ha superato i controlli preliminari. " +
														"Nel caso specifico la condizione ((isS2 || isAII) && !isE1 && !isSAQ && isEA) non risulta verificata.");
													logger.error(strLog);
				
													numeroLottiNonImportati++;
				
													UtilitySITAT.aggiungiMsgSchedaA(hmResponseLotti, cigLotto, false,
														StringUtils.replace(ConfigManager.getValore("error.variante.noVisibile"), "{0}", cigLotto));
												}
											} else {
												// Controlli preliminari non superati
												StringBuilder strLog = new StringBuilder(credenzialiUtente.getPrefissoLogger());
					
												Boolean existCollaudo =  UtilitySITAT.existsFaseEsportata(cigLotto, CostantiWSW9.COLLAUDO_CONTRATTO, sqlManager);
					
												strLog.append("Uno o piu' lotti indicati non hanno la fase di inizio contratto oppure esiste gia' la fase di collaudo contratto. Il lotto con CIG='");
												strLog.append(cigLotto);
												if (existCollaudo) {
													strLog.append("' ha gia' inviato la fase di collaudo. Impossibile importare le varianti del contratto." );
												} else {                  
													strLog.append("' non ha la fase di inizio contratto o stipula accordo quadro. Impossibile importare le varianti del contratto." );
												}
												logger.error(strLog);
					
												numeroLottiNonImportati++;
													
												if (existCollaudo) {
													UtilitySITAT.aggiungiMsgSchedaA(hmResponseLotti, cigLotto, false,
														StringUtils.replace(ConfigManager.getValore("error.variante.flussiSuccessivi"), "{0}", cigLotto));                	  
												} else {
													UtilitySITAT.aggiungiMsgSchedaA(hmResponseLotti, cigLotto, false,
														StringUtils.replace(ConfigManager.getValore("error.variante.noFlussiPrecedenti"), "{0}", cigLotto)); 
												}
											}
										} // Chiusura ciclo sulle varianti del singolo lotto
			
										if (numeroVariantiImportate > 0) {
											numeroLottiImportati += numeroVariantiImportate;
											numeroVariantiImportate = 0;
										}
										if (numeroVariantiAggiornate > 0) {
											numeroLottiAggiornati += numeroVariantiAggiornate;
											numeroVariantiAggiornate = 0;
										}
										if (numeroVariantiNonImportate > 0) {
											numeroLottiNonImportati += numeroVariantiNonImportate;
											numeroVariantiNonImportate = 0;
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
              }	// Chiusura ciclo for sui lotti presenti nel XML.
			
							UtilitySITAT.preparaRisultatoMessaggio(result, sovrascrivereDatiEsistenti, hmResponseLotti,
								numeroLottiImportati, numeroLottiNonImportati, numeroLottiAggiornati);
						}
					}
				} else {
          // E' stato inviato un messaggio di test.
          StringBuilder strLog = new StringBuilder(credenzialiUtente.getPrefissoLogger());
          strLog.append(" E' stato inviato un messaggio di test. Tale messaggio non e' stato elaborato.'\n");
          strLog.append("Messaggio: ");
          strLog.append(variante.toString());
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
			logger.debug("istanziaVariante: fine metodo");
	
			logger.debug("Response : " + UtilitySITAT.messaggioLogEsteso(result));
		}
	
		// MODIFICA TEMPORANEA -- DA CANCELLARE ---------------
		result.setError(UtilitySITAT.messaggioEsteso(result));
		// ----------------------------------------------------
	
		return result;
  }

	private void insertMotivazione(Long codiceGara, Long codiceLotto,
			VarianteType lottoVariante, int motiv, MotivazioneType motivazione)
			throws SQLException {
		DataColumn codiceGaraMoti = new DataColumn("W9MOTI.CODGARA",
		    new JdbcParametro(JdbcParametro.TIPO_NUMERICO, codiceGara));
		DataColumn codiceLottoMoti = new DataColumn("W9MOTI.CODLOTT",
		    new JdbcParametro(JdbcParametro.TIPO_NUMERICO, codiceLotto));
		DataColumn numVarMoti = new DataColumn("W9MOTI.NUM_VARI",
		    new JdbcParametro(JdbcParametro.TIPO_NUMERICO,
			    new Long(lottoVariante.getW3VANUMVARI())));
		DataColumn numMoti = new DataColumn("W9MOTI.NUM_MOTI",
		      new JdbcParametro(JdbcParametro.TIPO_NUMERICO,
		  		new Long(motiv+1)));
		DataColumn motivaz = new DataColumn("W9MOTI.ID_MOTIVO_VAR",
		    new JdbcParametro(JdbcParametro.TIPO_NUMERICO,
				Long.parseLong(motivazione.getW3IDMOTI3().toString())));
		
		DataColumnContainer dccMotivazione = new DataColumnContainer(new DataColumn[] { 
		    codiceGaraMoti, codiceLottoMoti, numVarMoti, numMoti, motivaz} );
		
		dccMotivazione.insert("W9MOTI", this.sqlManager);
	}
  
}
