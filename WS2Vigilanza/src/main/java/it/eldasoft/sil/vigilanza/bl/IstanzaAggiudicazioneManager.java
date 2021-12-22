package it.eldasoft.sil.vigilanza.bl;

import it.eldasoft.gene.bl.GenChiaviManager;
import it.eldasoft.gene.bl.GeneManager;
import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.db.datautils.DataColumn;
import it.eldasoft.gene.db.datautils.DataColumnContainer;
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.sil.vigilanza.beans.AggiudicazioneType;
import it.eldasoft.sil.vigilanza.beans.FinanziamentiType;
import it.eldasoft.sil.vigilanza.beans.ImpresaType;
import it.eldasoft.sil.vigilanza.beans.IncaricoProfessionaleType;
import it.eldasoft.sil.vigilanza.beans.LottoAggiudicazioneType;
import it.eldasoft.sil.vigilanza.beans.RichiestaSincronaIstanzaAggiudicazioneDocument;
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
 * Classe per l'import dei dati di aggiudicazione dei lotti della gara.
 * 
 * @author Luca.Giacomazzo
 */
public class IstanzaAggiudicazioneManager {

  private static Logger logger = Logger.getLogger(IstanzaAggiudicazioneManager.class);
  
  private CredenzialiManager credenzialiManager;

  private SqlManager sqlManager;

  private GeneManager geneManager;
  
  private RupManager rupManager;
  
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
   * @param rupManager the rupManager to set
   */
  public void setRupManager(RupManager rupManager) {
    this.rupManager = rupManager;
  }

  public void setGenChiaviManager(GenChiaviManager genChiaviManager) {
    this.genChiaviManager = genChiaviManager;
  }
  
  /**
   * Metodo per la gestione dell'aggiudicazione dei lotti della gara.
   * 
   * @param login LoginType
   * @param aggiudicazione IstanzaOggettoType
   * @return Ritorna l'oggetto ResponseType con l'esito dell'operazione
   * @throws XmlException 
   * @throws GestoreException 
   * @throws SQLException 
   * @throws Throwable
   */
  public ResponseType istanziaAggiudicazione(LoginType login, IstanzaOggettoType aggiudicazione)
      throws XmlException, GestoreException, SQLException, Throwable {
    ResponseType result = null;
    
    if (logger.isDebugEnabled()) {
      logger.debug("istanziaAggiudicazione: inizio metodo");
      
      logger.debug("XML : " + aggiudicazione.getOggettoXML());
    }
  
    // Codice fiscale della Stazione appaltante
    String codFiscaleStazAppaltante = aggiudicazione.getTestata().getCFEIN();
    
    boolean sovrascrivereDatiEsistenti = false;
    if (aggiudicazione.getTestata().getSOVRASCR() != null) {
      sovrascrivereDatiEsistenti = aggiudicazione.getTestata().getSOVRASCR().booleanValue();
    }
    
    // Verifica di login, password e determinazione della S.A.
    HashMap<String, Object> hm = this.credenzialiManager.verificaCredenziali(
    		login, codFiscaleStazAppaltante);
    result = (ResponseType) hm.get("RESULT");
    
    if (result.isSuccess()) {
      Credenziale credenzialiUtente = (Credenziale) hm.get("USER");
      String xmlAggiudicazione = aggiudicazione.getOggettoXML();
      
      try {
        RichiestaSincronaIstanzaAggiudicazioneDocument istanzaAggiudicazioneDocument =
          RichiestaSincronaIstanzaAggiudicazioneDocument.Factory.parse(xmlAggiudicazione);

        boolean isMessaggioDiTest = 
          istanzaAggiudicazioneDocument.getRichiestaSincronaIstanzaAggiudicazione().isSetTest()
            && istanzaAggiudicazioneDocument.getRichiestaSincronaIstanzaAggiudicazione().getTest();
        
        if (! isMessaggioDiTest) {
        
          // si esegue il controllo sintattico del messaggio
          XmlOptions validationOptions = new XmlOptions();
          ArrayList<XmlValidationError> validationErrors = new ArrayList<XmlValidationError>();
          validationOptions.setErrorListener(validationErrors);
          boolean isSintassiXmlOK = istanzaAggiudicazioneDocument.validate(validationOptions);

          int numeroLottiImportati = 0;
          int numeroLottiNonImportati = 0;
          int numeroLottiAggiornati = 0;

          if (!isSintassiXmlOK) {
            synchronized (validationErrors) {
              // Sincronizzazione dell'oggetto validationErrors per scrivere
              // sul log il dettaglio dell'errore su righe successive.  
              StringBuilder strLog = new StringBuilder(credenzialiUtente.getPrefissoLogger());
              strLog.append(" Errore nella validazione del messaggio ricevuto per la gestione di una " +
              		"istanza di aggiudicazione dei lotti.");
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
            LottoAggiudicazioneType[] arrayAggiudicazioni =
              istanzaAggiudicazioneDocument.getRichiestaSincronaIstanzaAggiudicazione().getListaLottiAggiuidicatiArray();
            
            if (arrayAggiudicazioni != null && arrayAggiudicazioni.length > 0) {
              // HashMap per caricare gli oggetti ResponseLottoType per ciascun lotto, con CIG come chiave della hashMap
              HashMap<String, ResponseLottoType> hmResponseLotti = new HashMap<String, ResponseLottoType>();

              StringBuilder strQueryCig = new StringBuilder("'");
              
              for (int esi = 0; esi < arrayAggiudicazioni.length; esi++) {
              	LottoAggiudicazioneType oggettoLottoAggiudicazione = arrayAggiudicazioni[esi];
                String cigLotto = oggettoLottoAggiudicazione.getW3CIG();

                strQueryCig.append(cigLotto);
                if (esi+1 < arrayAggiudicazioni.length)
                	strQueryCig.append("','");
                else
                	strQueryCig.append("'");
							}
              
              List<?> listaCodGara = this.sqlManager.getListVector(
              		"select CODGARA from W9LOTT where CIG in (" + strQueryCig.toString() + ")", null);
              if (listaCodGara == null || (listaCodGara != null && listaCodGara.size() == 0)) {
              	// Nessuno dei CIG indicati non esistono nella base dati di destinazione
              	logger.error(credenzialiUtente.getPrefissoLogger()
              			+ " nessuno dei lotti indicati sono presenti in archivio");
              	throw new WSVigilanzaException("Attenzione: la scheda non e' inviabile poiche' non esiste la gara nel sistema di destinazione. E' necessario provvedere preventivamente alla sua creazione, importandola da simog o inviandola da Appalti");
              } else {
              	if (listaCodGara.size() !=  arrayAggiudicazioni.length) {
              		// Non tutti i CIG esistono nella base dati!!
              		logger.error(credenzialiUtente.getPrefissoLogger()
                			+ " uno o piu' lotti indicati non sono presenti in archivio");
             			throw new WSVigilanzaException("Uno o piu' lotti indicati non sono presenti in archivio");
              	} else {
              		// Tutti i CIG esistono in base dati, ma controllo che appartengano tutti alla stessa gara
              		List<?> listaDistinctCodGara = this.sqlManager.getListVector(
                  		"select distinct(CODGARA) from W9LOTT where CIG in (" + strQueryCig.toString() + ")", null);
              		if (listaDistinctCodGara.size() >  1) {
                		// Non tutti i CIG appartengono alla stessa gara!!
              			logger.error(credenzialiUtente.getPrefissoLogger()
                  			+ " i CIG appartengono a " + listaDistinctCodGara.size() + " gare diverse");
                  	throw new WSVigilanzaException("I CIG indicati appartengono a "
                  			+ listaDistinctCodGara.size() + " gare diverse");
                	}
              	}
              }

              // Controlli preliminari superati con successo.
              // Inizio inserimento dei dati dell'aggiudicazione dei lotti nella base dati.
              for (int agg = 0; agg < arrayAggiudicazioni.length; agg++) {
                LottoAggiudicazioneType oggettoLottoAggiudicazione = arrayAggiudicazioni[agg];
                String codiceCigLotto = oggettoLottoAggiudicazione.getW3CIG();
                
                
                HashMap<String, Long> hashM = UtilitySITAT.getCodGaraCodLottByCIG(codiceCigLotto, this.sqlManager);
                Long codiceGara = hashM.get("CODGARA");
                Long codiceLotto = hashM.get("CODLOTT");
                //verifica monitoraggio multilotto - verifico se il campo CIG_MASTER_ML è valorizzato
                String cigMaster = (String) this.sqlManager.getObject("select CIG_MASTER_ML from W9LOTT where CODGARA=? and CODLOTT=?", new Object[] { codiceGara, codiceLotto });
                
                if (! UtilitySITAT.isLottoRiaggiudicato(codiceGara, codiceLotto, this.sqlManager) &&
                		(cigMaster == null || cigMaster.trim().length() == 0)) {
	                if (!UtilitySITAT.isUtenteAmministratore(credenzialiUtente)) {
	                	if (!UtilitySITAT.isUtenteRupDelLotto(codiceCigLotto, credenzialiUtente, this.sqlManager)) {
	                		logger.error(credenzialiUtente.getPrefissoLogger() + "L'utente non e' RUP del lotto con CIG=" + codiceCigLotto
	                				+ ", oppure nella gara e/o nel lotto non e' stato indicato il RUP");
	
	                		throw new WSVigilanzaException("Le credenziali fornite non coincidono con quelle del RUP indicato");
	                	}
	                }

	                // Ulteriori controlli preliminari su gara e lotto.
	              	
	              	boolean isS2 = UtilitySITAT.isS2(codiceGara, codiceLotto, this.sqlManager);
	                boolean isE1 = UtilitySITAT.isE1(codiceGara, codiceLotto, this.sqlManager);
	                boolean isAAQ = UtilitySITAT.isAAQ(codiceGara, this.sqlManager);
	                boolean isExSottosoglia = UtilitySITAT.isExSottosoglia(codiceGara, codiceLotto, this.sqlManager);
	                
	                Long faseEsecuz = null;
	                if (isAAQ) {
	                	faseEsecuz = new Long(CostantiWSW9.ADESIONE_ACCORDO_QUADRO);
	                } else  if (isS2 && !isE1) {
	                  faseEsecuz = new Long(CostantiWSW9.AGGIUDICAZIONE_SOPRA_SOGLIA);
	                } else {
	                  faseEsecuz = new Long(CostantiWSW9.FASE_SEMPLIFICATA_AGGIUDICAZIONE);
	                }
	              	
	                boolean esisteFaseAggiudicazione = UtilitySITAT.existsFase(codiceGara, codiceLotto, new Long(1),
	                    faseEsecuz.intValue(), this.sqlManager);
	              	if (esisteFaseAggiudicazione || (UtilitySITAT.isFaseAbilitata(codiceGara, codiceLotto, new Long(1), faseEsecuz.intValue(), this.sqlManager)
	              			&& UtilitySITAT.isFaseVisualizzabile(codiceGara, codiceLotto, faseEsecuz.intValue(), this.sqlManager))) {
	                  
	              		String tipoContratto = UtilitySITAT.getTipoContrattoLotto(codiceGara, codiceLotto, this.sqlManager);
	                  Long idSceltaContraente = (Long) this.sqlManager.getObject(
	                  		"select ID_SCELTA_CONTRAENTE from W9LOTT where CODGARA=? and CODLOTT=?",
	                			new Object[] { codiceGara, codiceLotto } );
	
	                	// Record in W9FASI.
	                	UtilitySITAT.istanziaFase(this.sqlManager, codiceGara, codiceLotto, faseEsecuz, new Long(1));
	                  
	                  // Aggiudicazione
	                  AggiudicazioneType aggiudicazioneType = oggettoLottoAggiudicazione.getAggiudicazione();
	                  
	                  DataColumn codGaraAppa = new DataColumn("W9APPA.CODGARA",
	                	  new JdbcParametro(JdbcParametro.TIPO_NUMERICO, codiceGara));
	                  DataColumn codLottAppa = new DataColumn("W9APPA.CODLOTT",
	                	  new JdbcParametro(JdbcParametro.TIPO_NUMERICO, codiceLotto));
	                  DataColumn numAppa = new DataColumn("W9APPA.NUM_APPA",
	                	  new JdbcParametro(JdbcParametro.TIPO_NUMERICO, new Long(1)));
	                  DataColumn flagAccordoQuadro = new DataColumn("W9APPA.FLAG_ACCORDO_QUADRO",
	                      new JdbcParametro(JdbcParametro.TIPO_NUMERICO, new Long(2)));
	                  DataColumnContainer dccAggiudicazione = new DataColumnContainer(new DataColumn[] {
	                      codGaraAppa, codLottAppa, numAppa, flagAccordoQuadro } );
	
	                  if (aggiudicazioneType.isSetW3PROCEDUR()) {
	                    dccAggiudicazione.addColumn("W9APPA.PROCEDURA_ACC", new JdbcParametro(JdbcParametro.TIPO_TESTO,
	                        aggiudicazioneType.getW3PROCEDUR() ? "1" : "2"));
	                  }
	                  if (aggiudicazioneType.isSetW3PREINFOR()) {
	                    dccAggiudicazione.addColumn("W9APPA.PREINFORMAZIONE", new JdbcParametro(JdbcParametro.TIPO_TESTO,
	                        aggiudicazioneType.getW3PREINFOR() ? "1" : "2"));
	                  }
	                  if (aggiudicazioneType.isSetW3TERMINE()) {
	                    dccAggiudicazione.addColumn("W9APPA.TERMINE_RIDOTTO", new JdbcParametro(JdbcParametro.TIPO_TESTO,
	                        aggiudicazioneType.getW3TERMINE() ? "1" : "2"));
	                  }
	                  if (aggiudicazioneType.isSetW3MODIND()) {
	                    dccAggiudicazione.addColumn("W9APPA.ID_MODO_INDIZIONE", new JdbcParametro(JdbcParametro.TIPO_NUMERICO, 
	                        new Long(aggiudicazioneType.getW3MODIND().toString())));
	                  }
	                  if (aggiudicazioneType.isSetW3IMPRINV()) {
	                    dccAggiudicazione.addColumn("W9APPA.NUM_IMPRESE_INVITATE", new JdbcParametro(JdbcParametro.TIPO_NUMERICO,
	                        new Long(aggiudicazioneType.getW3IMPRINV())));
	                  }
	                  if (aggiudicazioneType.isSetW3IMPRAMM()) {
	                    dccAggiudicazione.addColumn("W9APPA.NUM_OFFERTE_AMMESSE", new JdbcParametro(JdbcParametro.TIPO_NUMERICO,
	                        new Long(aggiudicazioneType.getW3IMPRAMM())));
	                  }
	                  if (aggiudicazioneType.isSetW3IMPRRIC()) {
	                  	if (idSceltaContraente != null && (idSceltaContraente.longValue() == 2 || idSceltaContraente.longValue() == 9)) {
	                  		dccAggiudicazione.addColumn("W9APPA.NUM_IMPRESE_RICHIEDENTI", new JdbcParametro(JdbcParametro.TIPO_NUMERICO,
	                          new Long(aggiudicazioneType.getW3IMPRRIC())));
	                  	}
	                  }
	                  if (aggiudicazioneType.isSetW3IMPROFF()) {
	                    dccAggiudicazione.addColumn("W9APPA.NUM_IMPRESE_OFFERENTI", new JdbcParametro(JdbcParametro.TIPO_NUMERICO,
	                        new Long(aggiudicazioneType.getW3IMPROFF())));
	                  }
	                  if (aggiudicazioneType.isSetW3DVERB()) {
	                    dccAggiudicazione.addColumn("W9APPA.DATA_VERB_AGGIUDICAZIONE", new JdbcParametro(JdbcParametro.TIPO_DATA,
	                        aggiudicazioneType.getW3DVERB().getTime()));
	                  }
	                  if (aggiudicazioneType.isSetW3DSCARI()) {
	                  	if (idSceltaContraente != null && (idSceltaContraente.longValue() == 2 || idSceltaContraente.longValue() == 9)) {
	                  		dccAggiudicazione.addColumn("W9APPA.DATA_SCADENZA_RICHIESTA_INVITO", new JdbcParametro(JdbcParametro.TIPO_DATA,
	                  				aggiudicazioneType.getW3DSCARI().getTime()));
	                  	}
	                  }
	                  if (aggiudicazioneType.isSetW3DSCAPO()) {
	                    dccAggiudicazione.addColumn("W9APPA.DATA_SCADENZA_PRES_OFFERTA", new JdbcParametro(JdbcParametro.TIPO_DATA,
	                        aggiudicazioneType.getW3DSCAPO().getTime()));
	                  }
	                  if (aggiudicazioneType.isSetW3IMPAGGI()) {
	                    dccAggiudicazione.addColumn("W9APPA.IMPORTO_AGGIUDICAZIONE", new JdbcParametro(JdbcParametro.TIPO_DECIMALE,
	                        aggiudicazioneType.getW3IMPAGGI()));
	                  }
	                  if (aggiudicazioneType.isSetW3ASTAELE()) {       
	                    dccAggiudicazione.addColumn("W9APPA.ASTA_ELETTRONICA", new JdbcParametro(JdbcParametro.TIPO_TESTO,
	                        aggiudicazioneType.getW3ASTAELE() ? "1" : "2"));
	                  }
	                  if (aggiudicazioneType.isSetW3PERCRIB()) {
	                    dccAggiudicazione.addColumn("W9APPA.PERC_RIBASSO_AGG", new JdbcParametro(JdbcParametro.TIPO_DECIMALE,
	                        new Double(new Float(aggiudicazioneType.getW3PERCRIB()).toString())));
	                  }
	                  if (aggiudicazioneType.isSetW3PERCOFF()) {
	                    dccAggiudicazione.addColumn("W9APPA.PERC_OFF_AUMENTO", new JdbcParametro(JdbcParametro.TIPO_DECIMALE,
	                        new Double(new Float(aggiudicazioneType.getW3PERCOFF()).toString())));
	                  }
	                  if (aggiudicazioneType.isSetW3DATAINV()) {
	                    dccAggiudicazione.addColumn("W9APPA.DATA_INVITO", new JdbcParametro(JdbcParametro.TIPO_DATA, 
	                        aggiudicazioneType.getW3DATAINV().getTime()));
	                  }
	                  if (aggiudicazioneType.isSetW3FLAGRIC()) {
	                    dccAggiudicazione.addColumn("W9APPA.FLAG_RICH_SUBAPPALTO", new JdbcParametro(JdbcParametro.TIPO_TESTO,
	                        aggiudicazioneType.getW3FLAGRIC() ? "1" : "2"));
	                  }
	                  if (aggiudicazioneType.isSetW3OFFEESC()) {
	                    dccAggiudicazione.addColumn("W9APPA.NUM_OFFERTE_ESCLUSE", new JdbcParametro(JdbcParametro.TIPO_NUMERICO,
	                        new Long(aggiudicazioneType.getW3OFFEESC())));
	                  }
	                  if (aggiudicazioneType.isSetW3OFFEMAX()) {
	                    dccAggiudicazione.addColumn("W9APPA.OFFERTA_MASSIMO", new JdbcParametro(JdbcParametro.TIPO_DECIMALE, 
	                        new Double(new Float(aggiudicazioneType.getW3OFFEMAX()).toString())));
	                  }
	                  if (aggiudicazioneType.isSetW3OFFEMIN()) {
	                    dccAggiudicazione.addColumn("W9APPA.OFFERTA_MINIMA", new JdbcParametro(JdbcParametro.TIPO_DECIMALE,
	                        new Double(new Float(aggiudicazioneType.getW3OFFEMIN()).toString())));
	                  }
	                  if (aggiudicazioneType.isSetW3VALSOGL()) {
	                    dccAggiudicazione.addColumn("W9APPA.VAL_SOGLIA_ANOMALIA", new JdbcParametro(JdbcParametro.TIPO_DECIMALE,
	                        new Double(new Float(aggiudicazioneType.getW3VALSOGL()).toString())));
	                  }
	                  if (aggiudicazioneType.isSetW3OFFEFUO()) {
	                    dccAggiudicazione.addColumn("W9APPA.NUM_OFFERTE_FUORI_SOGLIA", new JdbcParametro(JdbcParametro.TIPO_NUMERICO,
	                        new Long(aggiudicazioneType.getW3OFFEFUO())));
	                  }
	                  if (aggiudicazioneType.isSetW3NUMIMP()) {
	                    dccAggiudicazione.addColumn("W9APPA.NUM_IMP_ESCL_INSUF_GIUST", new JdbcParametro(JdbcParametro.TIPO_NUMERICO,
	                        new Long(aggiudicazioneType.getW3NUMIMP())));
	                  }
	                  double importoLavori = 0, importoForniture = 0, importoServizi = 0, importoProgettazione = 0, importoIva = 0;
	                  double importoSubTotale = 0, importoAltreSomme = 0, importoAttuazioneSicurezza = 0,  importoNonSoggettoARibasso = 0;
	                  
	                  if (aggiudicazioneType.isSetW3ILAVORI()) {
	                  	importoLavori = aggiudicazioneType.getW3ILAVORI();
	                  	dccAggiudicazione.addColumn("W9APPA.IMPORTO_LAVORI", new JdbcParametro(JdbcParametro.TIPO_DECIMALE,
	                  			aggiudicazioneType.getW3ILAVORI()));
	                  } 
	                  
	                  if (aggiudicazioneType.isSetW3ISERVIZ()) {
	                  	importoServizi = aggiudicazioneType.getW3ISERVIZ(); 
	                  	dccAggiudicazione.addColumn("W9APPA.IMPORTO_SERVIZI", new JdbcParametro(JdbcParametro.TIPO_DECIMALE,
	                  			aggiudicazioneType.getW3ISERVIZ()));
	                  }
	                  
	                  if (aggiudicazioneType.isSetW3IFORNIT()) {
	                  	importoForniture = aggiudicazioneType.getW3IFORNIT(); 
	                  	dccAggiudicazione.addColumn("W9APPA.IMPORTO_FORNITURE", new JdbcParametro(JdbcParametro.TIPO_DECIMALE,
	                  			aggiudicazioneType.getW3IFORNIT()));
	                  }
	                  
	                  importoSubTotale = importoLavori + importoForniture + importoServizi;
	                  
	                  if (aggiudicazioneType.isSetW3IPROGET()) {
	                  	importoProgettazione = aggiudicazioneType.getW3IPROGET(); 
	                  	dccAggiudicazione.addColumn("W9APPA.IMPORTO_PROGETTAZIONE", new JdbcParametro(JdbcParametro.TIPO_DECIMALE,
	                  			aggiudicazioneType.getW3IPROGET()));
	                  }
	                  
	                  if (aggiudicazioneType.isSetW9APIMPIVA()) {
	                  	importoIva = aggiudicazioneType.getW9APIMPIVA();
	                  	dccAggiudicazione.addColumn("W9APPA.IMPORTO_IVA", new JdbcParametro(JdbcParametro.TIPO_DECIMALE,
	                  			aggiudicazioneType.getW9APIMPIVA()));
	                  }
	                  
	                  if (aggiudicazioneType.isSetW9APIVA()) {
	                  	dccAggiudicazione.addColumn("W9APPA.IVA", new JdbcParametro(JdbcParametro.TIPO_DECIMALE,
	                  			new Double(new Float(aggiudicazioneType.getW9APIVA()).toString())));
	                  }
	                  
	                  if (aggiudicazioneType.isSetW3INONAS()) {
	                  	importoNonSoggettoARibasso = aggiudicazioneType.getW3INONAS();
	                  	dccAggiudicazione.addColumn("W9APPA.IMP_NON_ASSOG", new JdbcParametro(JdbcParametro.TIPO_DECIMALE,
	                  			aggiudicazioneType.getW3INONAS()));
	                  }
	                  
	                  if ((aggiudicazioneType.isSetW3ILAVORI() || aggiudicazioneType.isSetW3ISERVIZ() || 
	                  		aggiudicazioneType.isSetW3IFORNIT()) && importoSubTotale > 0) {
	                  	dccAggiudicazione.addColumn("W9APPA.IMPORTO_SUBTOTALE", new JdbcParametro(JdbcParametro.TIPO_DECIMALE, importoSubTotale));
	                  } else {
	                  	if (aggiudicazioneType.isSetW3ISUBTOT()) {
	                  		dccAggiudicazione.addColumn("W9APPA.IMPORTO_SUBTOTALE", new JdbcParametro(JdbcParametro.TIPO_DECIMALE,
	                        aggiudicazioneType.getW3ISUBTOT()));
	                  		importoSubTotale = aggiudicazioneType.getW3ISUBTOT();
	                  		
	                  		if (StringUtils.isNotEmpty(tipoContratto)) {
	                        if (tipoContratto.indexOf("L") >= 0) {
	                        	dccAggiudicazione.addColumn("W9APPA.IMPORTO_LAVORI", new JdbcParametro(JdbcParametro.TIPO_DECIMALE,
	                        			aggiudicazioneType.getW3ISUBTOT()));
	                        } else if (tipoContratto.indexOf("S") >= 0) {
	                        	dccAggiudicazione.addColumn("W9APPA.IMPORTO_SERVIZI", new JdbcParametro(JdbcParametro.TIPO_DECIMALE,
	                        			aggiudicazioneType.getW3ISUBTOT()));
	                        } else if (tipoContratto.indexOf("F") >= 0) {
	                        	dccAggiudicazione.addColumn("W9APPA.IMPORTO_FORNITURE", new JdbcParametro(JdbcParametro.TIPO_DECIMALE,
	                        			aggiudicazioneType.getW3ISUBTOT()));
	                        }
	                      }
	                  	} else {
	                  		dccAggiudicazione.addColumn("W9APPA.IMPORTO_SUBTOTALE", new JdbcParametro(JdbcParametro.TIPO_DECIMALE, new Double(0)));
	                  		dccAggiudicazione.addColumn("W9APPA.IMPORTO_LAVORI", new JdbcParametro(JdbcParametro.TIPO_DECIMALE, new Double(0)));
	                  		dccAggiudicazione.addColumn("W9APPA.IMPORTO_SERVIZI", new JdbcParametro(JdbcParametro.TIPO_DECIMALE, new Double(0)));
	                  		dccAggiudicazione.addColumn("W9APPA.IMPORTO_FORNITURE", new JdbcParametro(JdbcParametro.TIPO_DECIMALE, new Double(0)));
	                  	}
	                  }
	                  
	                  if (aggiudicazioneType.isSetW3IATTSIC()) {
	                  	importoAttuazioneSicurezza = aggiudicazioneType.getW3IATTSIC();
	                    dccAggiudicazione.addColumn("W9APPA.IMPORTO_ATTUAZIONE_SICUREZZA", 
	                    		new JdbcParametro(JdbcParametro.TIPO_DECIMALE,
	                        aggiudicazioneType.getW3IATTSIC()));
	                  }
	                  
	                  if (aggiudicazioneType.isSetW9APALTSOM()) {
	                  	importoAltreSomme = aggiudicazioneType.getW9APALTSOM();
	                  	dccAggiudicazione.addColumn("W9APPA.ALTRE_SOMME", new JdbcParametro(JdbcParametro.TIPO_DECIMALE,
	                  			aggiudicazioneType.getW9APALTSOM()));
	                  }
                  
	                  /*if (aggiudicazioneType.isSetW9APALTSOM()) {
	                  	importoIva = aggiudicazioneType.getW9APIMPIVA();
	                  	dccAppa.addColumn("W9APPA.IMPORTO_IVA", new JdbcParametro(JdbcParametro.TIPO_DECIMALE,
	                  			aggiudicazioneType.getW9APIMPIVA()));
	                  }*/
                  
	                  if (aggiudicazioneType.isSetW3IDISPOS()) {
	                  	dccAggiudicazione.addColumn("W9APPA.IMPORTO_DISPOSIZIONE", new JdbcParametro(
	                    		JdbcParametro.TIPO_DECIMALE, aggiudicazioneType.getW3IDISPOS()));
	                  } else {
	                  	dccAggiudicazione.addColumn("W9APPA.IMPORTO_DISPOSIZIONE", new JdbcParametro(
	                    		JdbcParametro.TIPO_DECIMALE, importoIva + importoAltreSomme));
	                  }
	
	                  dccAggiudicazione.addColumn("W9APPA.IMPORTO_COMPL_APPALTO", new JdbcParametro(
	                  		JdbcParametro.TIPO_DECIMALE, importoSubTotale + importoAttuazioneSicurezza 
	                  				+ importoProgettazione + importoNonSoggettoARibasso));
	                  
	                 	dccAggiudicazione.addColumn("W9APPA.IMPORTO_COMPL_INTERVENTO", new JdbcParametro(
	                			JdbcParametro.TIPO_DECIMALE, importoSubTotale + importoAttuazioneSicurezza 
	              				+ importoProgettazione + importoNonSoggettoARibasso + importoIva + importoAltreSomme));
	                  
	                  if (aggiudicazioneType.isSetW9APOUSCOMP()) {
	                    dccAggiudicazione.addColumn("W9APPA.OPERE_URBANIZ_SCOMPUTO", new JdbcParametro(
	                    		JdbcParametro.TIPO_TESTO, aggiudicazioneType.getW9APOUSCOMP() ? "1" : "2"));
	                  }
	                  
	                  if (aggiudicazioneType.isSetW9APDATASTI()) {
	                    dccAggiudicazione.addColumn("W9APPA.DATA_STIPULA", new JdbcParametro(
	                    		JdbcParametro.TIPO_DATA, aggiudicazioneType.getW9APDATASTI().getTime()));
	                  }
	                  if (aggiudicazioneType.isSetW9APDURCON()) {
	                    dccAggiudicazione.addColumn("W9APPA.DURATA_CON", new JdbcParametro(
	                    		JdbcParametro.TIPO_NUMERICO, new Long(aggiudicazioneType.getW9APDURCON())));
	                  }
	                  
	                  if (aggiudicazioneType.isSetW3CODSTRU()){
	                  	dccAggiudicazione.addColumn("W9APPA.COD_STRUMENTO", new JdbcParametro(
	                      		JdbcParametro.TIPO_TESTO, aggiudicazioneType.getW3CODSTRU().toString()));
	                  }
	  
	                  if (aggiudicazioneType.isSetW9APTIPAT()) {
	                  	dccAggiudicazione.addColumn("W9APPA.TIPO_ATTO", new JdbcParametro(
	                    		JdbcParametro.TIPO_NUMERICO, new Long(aggiudicazioneType.getW9APTIPAT().toString())));
	                  }
	                  
	                  if (aggiudicazioneType.isSetW9APDATAT()) {
	                  	dccAggiudicazione.addColumn("W9APPA.DATA_ATTO", new JdbcParametro(
	                    		JdbcParametro.TIPO_DATA, aggiudicazioneType.getW9APDATAT().getTime()));
	                  }
	                  
	                  if (aggiudicazioneType.isSetW9APNUMAT()) {
	                  	dccAggiudicazione.addColumn("W9APPA.NUMERO_ATTO", new JdbcParametro(
	                    		JdbcParametro.TIPO_TESTO, aggiudicazioneType.getW9APNUMAT()));
	                  }
	                  
	                  if (aggiudicazioneType.isSetW3DATAMAN()) {
	                  	dccAggiudicazione.addColumn("W9APPA.DATA_MANIF_INTERESSE", new JdbcParametro(
	                    		JdbcParametro.TIPO_DATA, aggiudicazioneType.getW3DATAMAN().getTime()));
	                  }
	                  
	                  if (aggiudicazioneType.isSetW3NUMMANI()) {
	                  	dccAggiudicazione.addColumn("W9APPA.NUM_MANIF_INTERESSE", new JdbcParametro(
	                    		JdbcParametro.TIPO_NUMERICO, new Long(aggiudicazioneType.getW3NUMMANI())));
	                  }
	                  
	                  if (aggiudicazioneType.isSetW3RELAZUNIC()) {
	                  	dccAggiudicazione.addColumn("W9APPA.RELAZIONE_UNICA", new JdbcParametro(JdbcParametro.TIPO_TESTO, 
	                  			aggiudicazioneType.getW3RELAZUNIC() ? "1" : "2"));
	                  }
	                  
	                  if (!esisteFaseAggiudicazione) {
	                    //String codiceCIG = UtilitySITAT.getCIGLotto(codiceGara, codiceLotto, this.sqlManager);
	                    
	                    // Inserimento dell'aggiudicazione in W9APPA
	                    dccAggiudicazione.insert("W9APPA", this.sqlManager);
	
	                   	this.istanziaImpreseAggiudicatarie(codFiscaleStazAppaltante, oggettoLottoAggiudicazione,
	                   			codiceGara, codiceLotto, esisteFaseAggiudicazione);
	
	                  	IncaricoProfessionaleType[] arrayIncarichiProfessionali =
	                      oggettoLottoAggiudicazione.getListaIncarichiProfessionaliArray(); 
	                    
	                    UtilitySITAT.gestioneIncarichiProfessionali(
	                    		result, codFiscaleStazAppaltante, credenzialiUtente, faseEsecuz.intValue(),
	                    		codiceGara, codiceLotto, esisteFaseAggiudicazione, arrayIncarichiProfessionali,
	                    		this.sqlManager, this.geneManager, this.rupManager, isAAQ, isE1, isS2, isExSottosoglia);
	                    
	                    this.istanziaFinanziamenti(codFiscaleStazAppaltante, oggettoLottoAggiudicazione,
	                    		codiceGara, codiceLotto, esisteFaseAggiudicazione);
	
	                    Long numeroFinanziamentiRegionali = (Long) this.sqlManager.getObject(
	                    		"select count(*) from W9FINA where CODGARA=? and CODLOTT=? and NUM_APPA=1 and ID_FINANZIAMENTO='C03'",
	                    		new Object[] { codiceGara, codiceLotto });
	                    if (numeroFinanziamentiRegionali > 0) {
	                    	this.sqlManager.update("update W9APPA set FIN_REGIONALE='1' where CODGARA=? and CODLOTT=? and NUM_APPA=1", new Object[] { codiceGara, codiceLotto });
	                    } else {
	                    	this.sqlManager.update("update W9APPA set FIN_REGIONALE='2' where CODGARA=? and CODLOTT=? and NUM_APPA=1", new Object[] { codiceGara, codiceLotto });
	                    }
	                    
	                   	numeroLottiImportati++;
	                   	UtilitySITAT.aggiungiMsgSchedaA(hmResponseLotti, codiceCigLotto, true, "Scheda aggiornata");
	                  	
	                    if (UtilitySITAT.isScritturaNoteAvvisiAttiva()) {
	                  		if (CostantiWSW9.ADESIONE_ACCORDO_QUADRO == faseEsecuz.longValue()) {
	                  			UtilitySITAT.insertNoteAvvisi("W9LOTT", codiceGara.toString(), codiceLotto.toString(),
	                    				null, null, null, "CIG " + codiceCigLotto + ": aggiornata fase 'Adesione accordo quadro'",
	                    				null, this.sqlManager);
	                  		} else {
	                  			UtilitySITAT.insertNoteAvvisi("W9LOTT", codiceGara.toString(), codiceLotto.toString(),
	                    				null, null, null, "CIG " + codiceCigLotto + ": aggiornata fase 'Aggiudicazione lotto'",
	                    				null, this.sqlManager);
	                  		}
	                    }
                   	
	                  } else if (esisteFaseAggiudicazione && sovrascrivereDatiEsistenti) {
                  	
	                  	DataColumnContainer dccAggiudicazioneDB = new DataColumnContainer(this.sqlManager, "W9APPA",
                  			"select PROCEDURA_ACC, PREINFORMAZIONE, TERMINE_RIDOTTO, ID_MODO_INDIZIONE, NUM_IMPRESE_INVITATE, "
                  				+ "NUM_OFFERTE_AMMESSE, NUM_IMPRESE_RICHIEDENTI, NUM_IMPRESE_OFFERENTI, DATA_VERB_AGGIUDICAZIONE, "
                  				+ "DATA_SCADENZA_RICHIESTA_INVITO, DATA_SCADENZA_PRES_OFFERTA, IMPORTO_AGGIUDICAZIONE, ASTA_ELETTRONICA, "
                  				+ "PERC_RIBASSO_AGG, PERC_OFF_AUMENTO, DATA_INVITO, FLAG_RICH_SUBAPPALTO, NUM_OFFERTE_ESCLUSE, OFFERTA_MASSIMO, "
                  				+ "OFFERTA_MINIMA, VAL_SOGLIA_ANOMALIA, NUM_OFFERTE_FUORI_SOGLIA, NUM_IMP_ESCL_INSUF_GIUST, "
                  				+ "IMPORTO_LAVORI, IMPORTO_SERVIZI, IMPORTO_FORNITURE, IMPORTO_PROGETTAZIONE, IMPORTO_IVA, IVA, "
                  				+ "IMP_NON_ASSOG, IMPORTO_SUBTOTALE, IMPORTO_ATTUAZIONE_SICUREZZA, ALTRE_SOMME, IMPORTO_DISPOSIZIONE, " 
                  				+ "W9APPA.IMPORTO_COMPL_APPALTO, IMPORTO_COMPL_INTERVENTO, OPERE_URBANIZ_SCOMPUTO, DATA_STIPULA, "
                  				+ "DURATA_CON, COD_STRUMENTO, TIPO_ATTO, DATA_ATTO, NUMERO_ATTO, DATA_MANIF_INTERESSE, NUM_MANIF_INTERESSE, "
                  				+ "FIN_REGIONALE, RELAZIONE_UNICA, CODGARA, CODLOTT, NUM_APPA "
                  			+ "from w9APPA where CODGARA=? and CODLOTT=? and NUM_APPA=1 ", 
                  			new Object[] { codiceGara, codiceLotto } );
                    
	                  	Iterator<Entry<String, DataColumn>> iterInizioDB = dccAggiudicazioneDB.getColonne().entrySet().iterator();
	                  	while (iterInizioDB.hasNext()) {
	                  		Entry<String, DataColumn> entry = iterInizioDB.next(); 
	                  		String nomeCampo = entry.getKey();
	                  		if (dccAggiudicazione.isColumn(nomeCampo)) {
	                  			dccAggiudicazioneDB.setValue(nomeCampo, dccAggiudicazione.getColumn(nomeCampo).getValue());
	                  		}
	                  	}
                  	
	                    /*if (aggiudicazioneType.isSetW3PROCEDUR()) {
	                    	dccAggiudicazioneDB.setValue("W9APPA.PROCEDURA_ACC", aggiudicazioneType.getW3PROCEDUR() ? "1" : "2");
	                    }
	                    if (aggiudicazioneType.isSetW3PREINFOR()) {
	                    	dccAggiudicazioneDB.setValue("W9APPA.PREINFORMAZIONE", aggiudicazioneType.getW3PREINFOR() ? "1" : "2");
	                    }
	                    if (aggiudicazioneType.isSetW3TERMINE()) {
	                    	dccAggiudicazioneDB.setValue("W9APPA.TERMINE_RIDOTTO", aggiudicazioneType.getW3TERMINE() ? "1" : "2");
	                    }
	                    if (aggiudicazioneType.isSetW3MODIND()) {
	                    	dccAggiudicazioneDB.setValue("W9APPA.ID_MODO_INDIZIONE", new Long(aggiudicazioneType.getW3MODIND().toString()));
	                    }
	                    if (aggiudicazioneType.isSetW3IMPRINV()) {
	                    	dccAggiudicazioneDB.setValue("W9APPA.NUM_IMPRESE_INVITATE", new Long(aggiudicazioneType.getW3IMPRINV()));
	                    }
	                    if (aggiudicazioneType.isSetW3IMPRAMM()) {
	                    	dccAggiudicazioneDB.setValue("W9APPA.NUM_OFFERTE_AMMESSE", new Long(aggiudicazioneType.getW3IMPRAMM()));
	                    }
	                    if (aggiudicazioneType.isSetW3IMPRRIC()) {
	                    	if (idSceltaContraente != null && (idSceltaContraente.longValue() == 2 || idSceltaContraente.longValue() == 9)) {
	                    		dccAggiudicazioneDB.setValue("W9APPA.NUM_IMPRESE_RICHIEDENTI", new Long(aggiudicazioneType.getW3IMPRRIC()));
	                    	}
	                    }
	                    if (aggiudicazioneType.isSetW3IMPROFF()) {
	                    	dccAggiudicazioneDB.setValue("W9APPA.NUM_IMPRESE_OFFERENTI", new Long(aggiudicazioneType.getW3IMPROFF()));
	                    }
	                    if (aggiudicazioneType.isSetW3DVERB()) {
	                    	dccAggiudicazioneDB.setValue("W9APPA.DATA_VERB_AGGIUDICAZIONE", new JdbcParametro(JdbcParametro.TIPO_DATA,
	                          aggiudicazioneType.getW3DVERB().getTime()));
	                    }
	                    if (aggiudicazioneType.isSetW3DSCARI()) {
	                    	if (idSceltaContraente != null && (idSceltaContraente.longValue() == 2 || idSceltaContraente.longValue() == 9)) {
	                    		dccAggiudicazioneDB.setValue("W9APPA.DATA_SCADENZA_RICHIESTA_INVITO", new JdbcParametro(JdbcParametro.TIPO_DATA,
	                    				aggiudicazioneType.getW3DSCARI().getTime()));
	                    	}
	                    }
	                    if (aggiudicazioneType.isSetW3DSCAPO()) {
	                    	dccAggiudicazioneDB.setValue("W9APPA.DATA_SCADENZA_PRES_OFFERTA", new JdbcParametro(JdbcParametro.TIPO_DATA,
	                          aggiudicazioneType.getW3DSCAPO().getTime()));
	                    }
	                    if (aggiudicazioneType.isSetW3IMPAGGI()) {
	                    	dccAggiudicazioneDB.setValue("W9APPA.IMPORTO_AGGIUDICAZIONE", new JdbcParametro(JdbcParametro.TIPO_DECIMALE,
	                          aggiudicazioneType.getW3IMPAGGI()));
	                    }
	                    if (aggiudicazioneType.isSetW3ASTAELE()) {       
	                    	dccAggiudicazioneDB.setValue("W9APPA.ASTA_ELETTRONICA", new JdbcParametro(JdbcParametro.TIPO_TESTO,
	                          aggiudicazioneType.getW3ASTAELE() ? "1" : "2"));
	                    }
	                    if (aggiudicazioneType.isSetW3PERCRIB()) {
	                      dccAggiudicazioneDB.setValue("W9APPA.PERC_RIBASSO_AGG", new JdbcParametro(JdbcParametro.TIPO_DECIMALE,
	                          new Double(new Float(aggiudicazioneType.getW3PERCRIB()).toString())));
	                    }
	                    if (aggiudicazioneType.isSetW3PERCOFF()) {
	                      dccAggiudicazioneDB.setValue("W9APPA.PERC_OFF_AUMENTO", new JdbcParametro(JdbcParametro.TIPO_DECIMALE,
	                          new Double(new Float(aggiudicazioneType.getW3PERCOFF()).toString())));
	                    }
	                    if (aggiudicazioneType.isSetW3DATAINV()) {
	                      dccAggiudicazioneDB.setValue("W9APPA.DATA_INVITO", new JdbcParametro(JdbcParametro.TIPO_DATA, 
	                          aggiudicazioneType.getW3DATAINV().getTime()));
	                    }
	                    if (aggiudicazioneType.isSetW3FLAGRIC()) {
	                      dccAggiudicazioneDB.setValue("W9APPA.FLAG_RICH_SUBAPPALTO", new JdbcParametro(JdbcParametro.TIPO_TESTO,
	                          aggiudicazioneType.getW3FLAGRIC() ? "1" : "2"));
	                    }
	                    if (aggiudicazioneType.isSetW3OFFEESC()) {
	                      dccAggiudicazioneDB.setValue("W9APPA.NUM_OFFERTE_ESCLUSE", new JdbcParametro(JdbcParametro.TIPO_NUMERICO,
	                          new Long(aggiudicazioneType.getW3OFFEESC())));
	                    }
	                    if (aggiudicazioneType.isSetW3OFFEMAX()) {
	                      dccAggiudicazioneDB.setValue("W9APPA.OFFERTA_MASSIMO", new JdbcParametro(JdbcParametro.TIPO_DECIMALE, 
	                          new Double(new Float(aggiudicazioneType.getW3OFFEMAX()).toString())));
	                    }
	                    if (aggiudicazioneType.isSetW3OFFEMIN()) {
	                      dccAggiudicazioneDB.setValue("W9APPA.OFFERTA_MINIMA", new JdbcParametro(JdbcParametro.TIPO_DECIMALE,
	                          new Double(new Float(aggiudicazioneType.getW3OFFEMIN()).toString())));
	                    }
	                    if (aggiudicazioneType.isSetW3VALSOGL()) {
	                      dccAggiudicazioneDB.setValue("W9APPA.VAL_SOGLIA_ANOMALIA", new JdbcParametro(JdbcParametro.TIPO_DECIMALE,
	                          new Double(new Float(aggiudicazioneType.getW3VALSOGL()).toString())));
	                    }
	                    if (aggiudicazioneType.isSetW3OFFEFUO()) {
	                      dccAggiudicazioneDB.setValue("W9APPA.NUM_OFFERTE_FUORI_SOGLIA", new JdbcParametro(JdbcParametro.TIPO_NUMERICO,
	                          new Long(aggiudicazioneType.getW3OFFEFUO())));
	                    }
	                    if (aggiudicazioneType.isSetW3NUMIMP()) {
	                      dccAggiudicazioneDB.setValue("W9APPA.NUM_IMP_ESCL_INSUF_GIUST", new JdbcParametro(JdbcParametro.TIPO_NUMERICO,
	                          new Long(aggiudicazioneType.getW3NUMIMP())));
	                    }
	                    if (aggiudicazioneType.isSetW3ILAVORI()) {
	                    	dccAggiudicazioneDB.setValue("W9APPA.IMPORTO_LAVORI", new JdbcParametro(JdbcParametro.TIPO_DECIMALE,
	                    			aggiudicazioneType.getW3ILAVORI()));
	                    } 
	                    if (aggiudicazioneType.isSetW3ISERVIZ()) {
	                    	dccAggiudicazioneDB.setValue("W9APPA.IMPORTO_SERVIZI", new JdbcParametro(JdbcParametro.TIPO_DECIMALE,
	                    			aggiudicazioneType.getW3ISERVIZ()));
	                    }
	                    if (aggiudicazioneType.isSetW3IFORNIT()) {
	                    	dccAggiudicazioneDB.setValue("W9APPA.IMPORTO_FORNITURE", new JdbcParametro(JdbcParametro.TIPO_DECIMALE,
	                    			aggiudicazioneType.getW3IFORNIT()));
	                    }
	                    if (aggiudicazioneType.isSetW3IPROGET()) {
	                    	dccAggiudicazioneDB.setValue("W9APPA.IMPORTO_PROGETTAZIONE", new JdbcParametro(JdbcParametro.TIPO_DECIMALE,
	                    			aggiudicazioneType.getW3IPROGET()));
	                    }
	                    if (aggiudicazioneType.isSetW9APIMPIVA()) {
	                    	dccAggiudicazioneDB.setValue("W9APPA.IMPORTO_IVA", new JdbcParametro(JdbcParametro.TIPO_DECIMALE,
	                    			aggiudicazioneType.getW9APIMPIVA()));
	                    }
	                    if (aggiudicazioneType.isSetW9APIVA()) {
	                    	dccAggiudicazioneDB.setValue("W9APPA.IVA", new JdbcParametro(JdbcParametro.TIPO_DECIMALE,
	                    			new Double(new Float(aggiudicazioneType.getW9APIVA()).toString())));
	                    }
	                    if (aggiudicazioneType.isSetW3INONAS()) {
	                    	dccAggiudicazioneDB.setValue("W9APPA.IMP_NON_ASSOG", new JdbcParametro(JdbcParametro.TIPO_DECIMALE,
	                    			aggiudicazioneType.getW3INONAS()));
	                    }
	                    if (aggiudicazioneType.isSetW3IATTSIC()) {
	                      dccAggiudicazioneDB.setValue("W9APPA.IMPORTO_ATTUAZIONE_SICUREZZA", 
	                      		new JdbcParametro(JdbcParametro.TIPO_DECIMALE,
	                          aggiudicazioneType.getW3IATTSIC()));
	                    }
	                    
	                    if (aggiudicazioneType.isSetW9APALTSOM()) {
	                    	dccAggiudicazioneDB.setValue("W9APPA.ALTRE_SOMME", new JdbcParametro(JdbcParametro.TIPO_DECIMALE,
	                    			aggiudicazioneType.getW9APALTSOM()));
	                    }
	                    
	                    if (aggiudicazioneType.isSetW3IDISPOS()) {
	                    	dccAggiudicazioneDB.setValue("W9APPA.IMPORTO_DISPOSIZIONE", new JdbcParametro(
	                      		JdbcParametro.TIPO_DECIMALE, aggiudicazioneType.getW3IDISPOS()));
	                    }
	                    if (aggiudicazioneType.isSetW9APOUSCOMP()) {
	                      dccAggiudicazioneDB.setValue("W9APPA.OPERE_URBANIZ_SCOMPUTO", new JdbcParametro(
	                      		JdbcParametro.TIPO_TESTO, aggiudicazioneType.getW9APOUSCOMP() ? "1" : "2"));
	                    }
	                    if (aggiudicazioneType.isSetW9APDATASTI()) {
	                      dccAggiudicazioneDB.setValue("W9APPA.DATA_STIPULA", new JdbcParametro(
	                      		JdbcParametro.TIPO_DATA, aggiudicazioneType.getW9APDATASTI().getTime()));
	                    }
	                    if (aggiudicazioneType.isSetW9APDURCON()) {
	                      dccAggiudicazioneDB.setValue("W9APPA.DURATA_CON", new JdbcParametro(
	                      		JdbcParametro.TIPO_NUMERICO, new Long(aggiudicazioneType.getW9APDURCON())));
	                    }
	                    if (aggiudicazioneType.isSetW3CODSTRU()){
	                    	dccAggiudicazioneDB.setValue("W9APPA.COD_STRUMENTO", new JdbcParametro(
	                        		JdbcParametro.TIPO_TESTO, aggiudicazioneType.getW3CODSTRU().toString()));
	                    }
	                    if (aggiudicazioneType.isSetW9APTIPAT()) {
	                    	dccAggiudicazioneDB.setValue("W9APPA.TIPO_ATTO", new JdbcParametro(
	                      		JdbcParametro.TIPO_NUMERICO, new Long(aggiudicazioneType.getW9APTIPAT().toString())));
	                    }
	                    if (aggiudicazioneType.isSetW9APDATAT()) {
	                    	dccAggiudicazioneDB.setValue("W9APPA.DATA_ATTO", new JdbcParametro(
	                      		JdbcParametro.TIPO_DATA, aggiudicazioneType.getW9APDATAT().getTime()));
	                    }
	                    if (aggiudicazioneType.isSetW9APNUMAT()) {
	                    	dccAggiudicazioneDB.setValue("W9APPA.NUMERO_ATTO", new JdbcParametro(
	                      		JdbcParametro.TIPO_TESTO, aggiudicazioneType.getW9APNUMAT()));
	                    }
	                    if (aggiudicazioneType.isSetW3DATAMAN()) {
	                    	dccAggiudicazioneDB.setValue("W9APPA.DATA_MANIF_INTERESSE", new JdbcParametro(
	                      		JdbcParametro.TIPO_DATA, aggiudicazioneType.getW3DATAMAN().getTime()));
	                    }
	                    if (aggiudicazioneType.isSetW3NUMMANI()) {
	                    	dccAggiudicazioneDB.setValue("W9APPA.NUM_MANIF_INTERESSE", new JdbcParametro(
	                      		JdbcParametro.TIPO_NUMERICO, new Long(aggiudicazioneType.getW3NUMMANI())));
	                    }*/
	                    
	                    if (dccAggiudicazioneDB.isModifiedTable("W9APPA")) {
	                    	dccAggiudicazioneDB.getColumn("W9APPA.CODGARA").setChiave(true);
	                    	dccAggiudicazioneDB.getColumn("W9APPA.CODLOTT").setChiave(true);
	                    	dccAggiudicazioneDB.getColumn("W9APPA.NUM_APPA").setChiave(true);
	                    	dccAggiudicazioneDB.update("W9APPA", this.sqlManager);
	                    }
                    
	                    // Ricalcolo dei alcuni campi calcolati dopo aver effettuato l'update dei campi gestiti dal WS
	                    HashMap<String, Object> hmImporti = this.sqlManager.getHashMap("select IMPORTO_LAVORI, IMPORTO_SERVIZI, IMPORTO_FORNITURE, "
	                    		+ "IMPORTO_ATTUAZIONE_SICUREZZA, IMPORTO_PROGETTAZIONE, IMP_NON_ASSOG, IMPORTO_IVA, ALTRE_SOMME "
	                    		+ "from W9APPA where CODGARA=? and CODLOTT=? and NUM_APPA=1", new Object[] { codiceGara, codiceLotto } );
	                    
	                    //double importoLavori = 0, importoForniture = 0, importoServizi = 0, importoProgettazione = 0, importoIva = 0;
	                    //double importoSubTotale = 0, importoAltreSomme = 0, importoAttuazioneSicurezza = 0,  importoNonSoggettoARibasso = 0;
	                    
	                    Object objImportoLavori = ((JdbcParametro) hmImporti.get("IMPORTO_LAVORI")).getValue();
	                    Object objImportoServizi = ((JdbcParametro) hmImporti.get("IMPORTO_SERVIZI")).getValue();
	                    Object objImportoForniture = ((JdbcParametro) hmImporti.get("IMPORTO_FORNITURE")).getValue();
	                    Object objImportoAttuazSic = ((JdbcParametro) hmImporti.get("IMPORTO_ATTUAZIONE_SICUREZZA")).getValue();
	                    Object objImportoProgettaz = ((JdbcParametro) hmImporti.get("IMPORTO_PROGETTAZIONE")).getValue();
	                    Object objImportoNonAssog = ((JdbcParametro) hmImporti.get("IMP_NON_ASSOG")).getValue();
	                    Object objImportoIva = ((JdbcParametro) hmImporti.get("IMPORTO_IVA")).getValue();
	                    Object objImportoAltreSomme = ((JdbcParametro) hmImporti.get("ALTRE_SOMME")).getValue();
                    
	                    if (objImportoLavori != null) {
	                    	if (objImportoLavori instanceof Double) {
	                    		importoLavori = ((Double) objImportoLavori).doubleValue();
	                    	} else if (objImportoLavori instanceof Long) {
	                    		importoLavori = ((Long) objImportoLavori).doubleValue();
	                    	} 
	                    }
			          			if (objImportoServizi != null) {
	                    	if (objImportoServizi instanceof Double) {
	                    		importoServizi = ((Double) objImportoServizi).doubleValue();
	                    	} else if (objImportoServizi instanceof Long) {
	                    		importoServizi = ((Long) objImportoServizi).doubleValue();
	                    	}
	                    }
			          			if (objImportoForniture != null) {
	                    	if (objImportoForniture instanceof Double) {
	                    		importoForniture = ((Double) objImportoForniture).doubleValue();
	                    	} else if (objImportoForniture instanceof Long) {
	                    		importoForniture = ((Long) objImportoForniture).doubleValue();
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
	                    		importoNonSoggettoARibasso = ((Double) objImportoNonAssog).doubleValue();
	                    	} else if (objImportoNonAssog instanceof Long) {
	                    		importoNonSoggettoARibasso = ((Long) objImportoNonAssog).doubleValue();
	                    	}
	                    }
			          			if (objImportoIva != null) {
	                    	if (objImportoIva instanceof Double) {
	                    		importoIva = ((Double) objImportoIva).doubleValue();
	                    	} else if (objImportoIva instanceof Long) {
	                    		importoIva = ((Long) objImportoIva).doubleValue();
	                    	}
	                    }
          				  
			          			if (objImportoAltreSomme != null) {
		                  	if (objImportoAltreSomme instanceof Double) {
		                  		importoAltreSomme = ((Double) objImportoAltreSomme).doubleValue();
		                  	} else if (objImportoAltreSomme instanceof Long) {
		                  		importoAltreSomme = ((Long) objImportoAltreSomme).doubleValue();
		                  	}
		                  }
          					
	          					importoSubTotale = importoLavori + importoForniture + importoServizi;
	          					double importoComplessivoInterevento = importoLavori + importoForniture + importoServizi +
	          							importoAttuazioneSicurezza + importoProgettazione + importoNonSoggettoARibasso + importoIva + importoAltreSomme;
	          					double importoComplessivoAppalto = importoLavori + importoForniture + importoServizi +
	          							importoAttuazioneSicurezza + importoProgettazione + importoNonSoggettoARibasso;
	
	                    this.sqlManager.update("update W9APPA set IMPORTO_COMPL_INTERVENTO=?, IMPORTO_COMPL_APPALTO=?, IMPORTO_SUBTOTALE=? "
	                    		+ " where CODGARA=? and CODLOTT=? and NUM_APPA=1", 
	                    		new Object[] { importoComplessivoInterevento, importoComplessivoAppalto, importoSubTotale, codiceGara, codiceLotto});
	                    
	                   	this.istanziaImpreseAggiudicatarie(codFiscaleStazAppaltante, oggettoLottoAggiudicazione,
	                   			codiceGara, codiceLotto, esisteFaseAggiudicazione);
	
	                  	IncaricoProfessionaleType[] arrayIncarichiProfessionali =
	                      oggettoLottoAggiudicazione.getListaIncarichiProfessionaliArray(); 
	                    
	                    UtilitySITAT.gestioneIncarichiProfessionali(
	                    		result, codFiscaleStazAppaltante, credenzialiUtente, faseEsecuz.intValue(),
	                    		codiceGara, codiceLotto, esisteFaseAggiudicazione, arrayIncarichiProfessionali,
	                    		this.sqlManager, this.geneManager, this.rupManager, isAAQ, isE1, isS2, isExSottosoglia);
	                    
	                    this.istanziaFinanziamenti(codFiscaleStazAppaltante, oggettoLottoAggiudicazione,
	                    		codiceGara, codiceLotto, esisteFaseAggiudicazione);
                    
	                    Long numeroFinanziamentiRegionali = (Long) this.sqlManager.getObject(
	                    		"select count(*) from W9FINA where CODGARA=? and CODLOTT=? and NUM_APPA=1 and ID_FINANZIAMENTO='C03'",
	                    		new Object[] { codiceGara, codiceLotto });
	                    if (numeroFinanziamentiRegionali > 0) {
	                    	this.sqlManager.update("update W9APPA set FIN_REGIONALE='1' where CODGARA=? and CODLOTT=? and NUM_APPA=1", new Object[] { codiceGara, codiceLotto });
	                    } else {
	                    	this.sqlManager.update("update W9APPA set FIN_REGIONALE='2' where CODGARA=? and CODLOTT=? and NUM_APPA=1", new Object[] { codiceGara, codiceLotto });
	                    }
	                    
	                    numeroLottiAggiornati++;
	                  	UtilitySITAT.aggiungiMsgSchedaA(hmResponseLotti, codiceCigLotto, true, "Scheda importata");
	                  	
	                  	if (UtilitySITAT.isScritturaNoteAvvisiAttiva()) {
		                		if (CostantiWSW9.ADESIONE_ACCORDO_QUADRO == faseEsecuz.longValue()) {
		                			UtilitySITAT.insertNoteAvvisi("W9LOTT", codiceGara.toString(), codiceLotto.toString(),
		                  				null, null, null, "CIG " + codiceCigLotto + ": inserita fase 'Adesione accordo quadro'",
		                  				null, this.sqlManager);
		                		} else {
		                			UtilitySITAT.insertNoteAvvisi("W9LOTT", codiceGara.toString(), codiceLotto.toString(),
		                  				null, null, null, "CIG " + codiceCigLotto + ": inserita fase 'Aggiudicazione lotto'",
		                  				null, this.sqlManager);
		                		}
	                  	}
	                  } else {
	                    // Caso in cui in base dati esiste gia' la fase di aggiudicazione per il lotto
	                    // ed il flag di sovrascrittura dei dati e' valorizzato a false.
	                    
	                    StringBuilder strLog = new StringBuilder(credenzialiUtente.getPrefissoLogger());
	                    strLog.append(" L'aggiudicazione del lotto con CIG=");
	                    strLog.append(codiceCigLotto);
	                    strLog.append(" non e' stato importato perche' la fase gia' esiste nella base dati " +
	                        "e non la si vuole sovrascrivere.");
	                    logger.info(strLog.toString());
	                    
	                    numeroLottiNonImportati++;
	                    
	                    UtilitySITAT.aggiungiMsgSchedaA(hmResponseLotti, codiceCigLotto, false,
	                    		ConfigManager.getValore("error.aggiudicazione.esistente"));
	                  }
              	
	              	} else {
	                  // Fase non abilitata e non visibile. Il lotto non viene importato.
	                  StringBuilder strLog = new StringBuilder(credenzialiUtente.getPrefissoLogger());
	                  strLog.append(" L'aggiudicazione del lotto con CIG=");
	                  strLog.append(codiceCigLotto);
	                  strLog.append(" non e' stato importato perche' non ha superato i controlli preliminari" +
	                  		" specifici della fase di aggiudicazione.");
	                  logger.info(strLog.toString());
	
	                  numeroLottiNonImportati++;
	                  
	                  UtilitySITAT.aggiungiMsgSchedaA(hmResponseLotti, codiceCigLotto, false,
	                  		ConfigManager.getValore("error.aggiudicazione.noVisibile"));
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
                	logger.info(messaggioLog + " (CIG: " + codiceCigLotto + ", CFSA: " + codFiscaleStazAppaltante + ")");
	              	numeroLottiNonImportati++;
	              	UtilitySITAT.aggiungiMsgSchedaA(hmResponseLotti, codiceCigLotto, false,
                  		StringUtils.replace(ConfigManager.getValore(codiceErrore), "{0}", codiceCigLotto));
	              }
	            
	                UtilitySITAT.preparaRisultatoMessaggio(result, sovrascrivereDatiEsistenti,
	                		hmResponseLotti, numeroLottiImportati, numeroLottiNonImportati, numeroLottiAggiornati);
	            } // Ciclo for sulle aggiudicazioni

            } else {
		          // E' stato inviato un messaggio di test.
		          StringBuilder strLog = new StringBuilder(credenzialiUtente.getPrefissoLogger());
		          strLog.append(" E' stato inviato un messaggio di test. Tale messaggio non e' stato elaborato.'\n");
		          strLog.append("Messaggio: ");
		          strLog.append(aggiudicazione.toString());
		          logger.info(strLog);
		          
		          result.setSuccess(true);
		          result.setError("E' stato inviato un messaggio di test: messaggio non elaborato.");
		        }
          }
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
      logger.debug("istanziaAggiudicazione: fine metodo");
      
      logger.debug("Response : " + UtilitySITAT.messaggioLogEsteso(result));
    }
    
    // MODIFICA TEMPORANEA -- DA CANCELLARE ---------------
    result.setError(UtilitySITAT.messaggioEsteso(result));
    // ----------------------------------------------------
    
    return result;
  }

	private boolean istanziaImpreseAggiudicatarie(String codFiscaleStazAppaltante,
			LottoAggiudicazioneType oggettoLottoAggiudicazione, Long codiceGara,
			Long codiceLotto, boolean esisteFaseAggiudicazione) throws SQLException,
			GestoreException {
		
		boolean impreseAggiudModificate = false;
		
		if (oggettoLottoAggiudicazione.getListaImpreseAggiudicatarieArray() != null
		    && oggettoLottoAggiudicazione.getListaImpreseAggiudicatarieArray().length > 0) {

  		if (esisteFaseAggiudicazione) {
		    sqlManager.update("delete from W9AGGI where CODGARA=? and CODLOTT=? and NUM_APPA=?",
		        new Object[] {codiceGara, codiceLotto, new Long(1)} ) ;
		  }
			
			for (int impAgg = 0; impAgg < oggettoLottoAggiudicazione.getListaImpreseAggiudicatarieArray().length; impAgg++) {
		    ImpresaType impresaAggiudicataria = oggettoLottoAggiudicazione.getListaImpreseAggiudicatarieArray(impAgg);
		    ImpresaType impresaAusiliaria = null;
		    
				// Valore AGGAUS  
				// 1: Aggiudicataria (default)
				// 2: Ausiliaria 
			  // proseguo solo se 1 o non definito
			  if ((impresaAggiudicataria.isSetAGGAUS() && impresaAggiudicataria.getAGGAUS()==1) || !(impresaAggiudicataria.isSetAGGAUS())) {
			    
			    DataColumn codGaraAggi = new DataColumn("W9AGGI.CODGARA", new JdbcParametro(
			    		JdbcParametro.TIPO_NUMERICO, codiceGara));
			    DataColumn codLottAggi = new DataColumn("W9AGGI.CODLOTT", new JdbcParametro(
			    		JdbcParametro.TIPO_NUMERICO, codiceLotto));
			    DataColumn numAppaAggi = new DataColumn("W9AGGI.NUM_APPA", new JdbcParametro(
			    		JdbcParametro.TIPO_NUMERICO, new Long(1)));
			    DataColumn numAggi = new DataColumn("W9AGGI.NUM_AGGI", new JdbcParametro(
			    		JdbcParametro.TIPO_NUMERICO, new Long(impAgg+1)));
	
			    DataColumnContainer dccAggi = new DataColumnContainer(new DataColumn[] {
			        codGaraAggi, codLottAggi, numAppaAggi, numAggi} );
	
			    if (impresaAggiudicataria.isSetW3IDTIPOA()) {
			    	dccAggi.addColumn("W9AGGI.ID_TIPOAGG", JdbcParametro.TIPO_NUMERICO, 
			    			Long.parseLong(impresaAggiudicataria.getW3IDTIPOA().toString()));
			    	
			    	if (!StringUtils.equals("3", impresaAggiudicataria.getW3IDTIPOA().toString())) {
			    		if (impresaAggiudicataria.isSetW3AGIDGRP()) {
				    		dccAggi.addColumn("W9AGGI.ID_GRUPPO", JdbcParametro.TIPO_NUMERICO, 
				    				new Long(impresaAggiudicataria.getW3AGIDGRP()));
				    	} else {
				    		dccAggi.addColumn("W9AGGI.ID_GRUPPO", JdbcParametro.TIPO_NUMERICO, new Long(1));
				    	}
			    	}
			    } else {
			    	dccAggi.addColumn("W9AGGI.ID_TIPOAGG", JdbcParametro.TIPO_NUMERICO, new Long(3));
			    }
			    
			    if (impresaAggiudicataria.isSetW3RUOLO()) {
			      dccAggi.addColumn("W9AGGI.RUOLO", JdbcParametro.TIPO_NUMERICO, 
			          Long.parseLong(impresaAggiudicataria.getW3RUOLO().toString()));  
			    }

			    // Gestione dell'impresa aggiudicataria
			    boolean eseguiInsertImpresa = false;
			    
			    HashMap<String, Object> hmImpresa = UtilitySITAT.gestioneImpresa(codFiscaleStazAppaltante,
			    		impresaAggiudicataria, eseguiInsertImpresa, this.sqlManager, this.geneManager);
	
			    String codImpresa = (String) hmImpresa.get("CODIMP");
			    String codImpresaAusiliaria = null;
			    dccAggi.addColumn("W9AGGI.CODIMP", JdbcParametro.TIPO_TESTO, codImpresa);
	
			    // Gestione impresa Ausiliaria
			    if (impresaAggiudicataria.isSetW3FLAGAVV()) {
			    	dccAggi.addColumn("W9AGGI.FLAG_AVVALIMENTO", JdbcParametro.TIPO_NUMERICO, 
					  Long.parseLong(impresaAggiudicataria.getW3FLAGAVV().toString()));  	
			    }

			    if (impresaAggiudicataria.isSetCFIMPAUSILIARIA()) {
			    	codImpresaAusiliaria = (String) this.sqlManager.getObject("Select CODIMP from IMPR where CFIMP = ?",
			    			new Object[] { impresaAggiudicataria.getCFIMPAUSILIARIA() } );
			    	
			    	if (StringUtils.isNotEmpty(codImpresaAusiliaria)) {
			    		dccAggi.addColumn("W9AGGI.CODIMP_AUSILIARIA", JdbcParametro.TIPO_TESTO, codImpresaAusiliaria);
			    	} else {
			    		for (int impreseAusiliarie=0; impreseAusiliarie < oggettoLottoAggiudicazione.getListaImpreseAggiudicatarieArray().length; impreseAusiliarie++) {
			    			impresaAusiliaria = oggettoLottoAggiudicazione.getListaImpreseAggiudicatarieArray(impreseAusiliarie);
			    			if (impresaAusiliaria.isSetAGGAUS() && impresaAusiliaria.getAGGAUS() == 2 && 
			    					StringUtils.equalsIgnoreCase(impresaAggiudicataria.getCFIMPAUSILIARIA(), impresaAusiliaria.getCFIMP())) {
			    				
			    				HashMap<String, Object> hmImpresaAus = UtilitySITAT.gestioneImpresa(codFiscaleStazAppaltante,
							    		impresaAusiliaria, true, this.sqlManager, this.geneManager);
			    				codImpresaAusiliaria = (String) hmImpresaAus.get("CODIMP");
			    				dccAggi.addColumn("W9AGGI.CODIMP_AUSILIARIA", JdbcParametro.TIPO_TESTO, codImpresaAusiliaria);
			    			}
			    		}
			    	}
			    }

			    if (impresaAggiudicataria.isSetW3AGIMPAGGI()) {
			    	dccAggi.addColumn("W9AGGI.IMPORTO_AGGIUDICAZIONE", JdbcParametro.TIPO_DECIMALE, impresaAggiudicataria.getW3AGIMPAGGI());
			    }
			    
			    if (impresaAggiudicataria.isSetW3AGPERCRIB()) {
			    	dccAggi.addColumn("W9AGGI.PERC_RIBASSO_AGG", JdbcParametro.TIPO_DECIMALE, impresaAggiudicataria.getW3AGPERCRIB());
			    }
			    
			    if (impresaAggiudicataria.isSetW3AGPERCOFF()) {
			    	dccAggi.addColumn("W9AGGI.PERC_OFF_AUMENTO", JdbcParametro.TIPO_DECIMALE, impresaAggiudicataria.getW3AGPERCOFF());
			    }
                  
			    // Inserimento record in W9AGGI
			    dccAggi.insert("W9AGGI", this.sqlManager);
			    
			    UtilitySITAT.gestioneLegaliRappresentanti(codFiscaleStazAppaltante,
			        impresaAggiudicataria, eseguiInsertImpresa, codImpresa,
			        this.sqlManager, this.genChiaviManager);
			    if (impresaAusiliaria != null && StringUtils.isNotEmpty(codImpresaAusiliaria)) {
			    	UtilitySITAT.gestioneLegaliRappresentanti(codFiscaleStazAppaltante,
				        impresaAusiliaria, eseguiInsertImpresa, codImpresaAusiliaria,
				        this.sqlManager, this.genChiaviManager);	
			    }
			  }
			}
		}
		
		return impreseAggiudModificate;
	}

	//Finanziamenti
	private boolean istanziaFinanziamenti(String codFiscaleStazAppaltante,
			LottoAggiudicazioneType oggettoLottoAggiudicazione, Long codiceGara,
			Long codiceLotto, boolean esisteFaseAggiudicazione) throws SQLException,
			GestoreException {
		
		boolean finanziamentiModificati = false;
		
		if (oggettoLottoAggiudicazione.getListaFinanziamentiArray() != null
			    && oggettoLottoAggiudicazione.getListaFinanziamentiArray().length > 0) {
			
	  		if (esisteFaseAggiudicazione) {
			    this.sqlManager.update("delete from W9FINA where CODGARA=? and CODLOTT=? and NUM_APPA=?",
			        new Object[] {codiceGara, codiceLotto, new Long(1)} ) ;
			}
		
			for (int nFinanziamento = 0; nFinanziamento < oggettoLottoAggiudicazione.getListaFinanziamentiArray().length; nFinanziamento++) {
				FinanziamentiType finanziamenti = oggettoLottoAggiudicazione.getListaFinanziamentiArray(nFinanziamento);
				
				// codgara, codlott, numappa
				DataColumn codGaraFina = new DataColumn("W9FINA.CODGARA", new JdbcParametro(
			    		JdbcParametro.TIPO_NUMERICO, codiceGara));
			    DataColumn codLottFina = new DataColumn("W9FINA.CODLOTT", new JdbcParametro(
			    		JdbcParametro.TIPO_NUMERICO, codiceLotto));
			    DataColumn numAppaFina = new DataColumn("W9FINA.NUM_APPA", new JdbcParametro(
			    		JdbcParametro.TIPO_NUMERICO, new Long(1)));
			    
			    //progressivo numero finanziamento 
			    DataColumn numFinanziamenti = new DataColumn("W9FINA.NUM_FINA", new JdbcParametro(
			    		JdbcParametro.TIPO_NUMERICO, new Long(nFinanziamento+1)));
			    
			    DataColumnContainer dccFina = new DataColumnContainer(new DataColumn[] {
				        codGaraFina, codLottFina, numAppaFina, numFinanziamenti} );
			    
			    // id finanziamento - campo obbligatorio
			    dccFina.addColumn("W9FINA.ID_FINANZIAMENTO", new JdbcParametro(
			    		JdbcParametro.TIPO_TESTO, finanziamenti.getW3IDFINAN().toString()));
			    
			    // importo finanziamento
			    if(finanziamenti.isSetW3IFINANZ()){
			    	dccFina.addColumn("W9FINA.IMPORTO_FINANZIAMENTO", new JdbcParametro(
			    		JdbcParametro.TIPO_DECIMALE, finanziamenti.getW3IFINANZ()));
			    }
			  
			    // Inserimento record in W9FINA
			    dccFina.insert("W9FINA", this.sqlManager);
			    
			}
		/*} else {
			// Cancellazione dei finanziamenti precedentemente inseriti.
			if (esisteFaseAggiudicazione) {
		    this.sqlManager.update("delete from W9FINA where CODGARA=? and CODLOTT=? and NUM_APPA=?",
		        new Object[] {codiceGara, codiceLotto, new Long(1)} ) ;
		  }*/
		}
		return finanziamentiModificati;
	}	
	
}
