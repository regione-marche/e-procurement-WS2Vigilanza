package it.eldasoft.sil.vigilanza.bl;

import it.eldasoft.gene.bl.GeneManager;
import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.db.datautils.DataColumn;
import it.eldasoft.gene.db.datautils.DataColumnContainer;
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.sil.vigilanza.beans.CollaudoType;
import it.eldasoft.sil.vigilanza.beans.LottoCollaudoType;
import it.eldasoft.sil.vigilanza.beans.RichiestaSincronaIstanzaCollaudoDocument;
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
 * Classe per l'import dei dati di collaudo dei lotti della gara.
 * 
 * @author Luca.Giacomazzo
 */
public class IstanzaCollaudoManager {

  private static Logger logger = Logger.getLogger(IstanzaCollaudoManager.class);
  
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
   * Metodo per la gestione del collaudo dei contratti.
   * 
   * @param login LoginType
   * @param collaudo IstanzaOggettoType
   * @return Ritorna l'oggetto ResponseType con l'esito dell'operazione
   * @throws XmlException 
   * @throws GestoreException 
   * @throws SQLException 
   * @throws Throwable
   */
  public ResponseType istanziaCollaudo(LoginType login, IstanzaOggettoType collaudo)
      throws XmlException, GestoreException, SQLException, Throwable {
    ResponseType result = null;
    
    if (logger.isDebugEnabled()) {
      logger.debug("istanziaCollaudo: inizio metodo");
      
      logger.debug("XML : " + collaudo.getOggettoXML());
    }
  
    // Codice fiscale della Stazione appaltante
    String codFiscaleStazAppaltante = collaudo.getTestata().getCFEIN();
    
    boolean sovrascrivereDatiEsistenti = false;
    if (collaudo.getTestata().getSOVRASCR() != null) {
      sovrascrivereDatiEsistenti = collaudo.getTestata().getSOVRASCR().booleanValue(); 
    }
    
    // Verifica di login, password e determinazione della S.A.
    HashMap<String, Object> hm = this.credenzialiManager.verificaCredenziali(login, codFiscaleStazAppaltante);
    result = (ResponseType) hm.get("RESULT");
    
    if (result.isSuccess()) {
      Credenziale credenzialiUtente = (Credenziale) hm.get("USER");
      String xmlCollaudo = collaudo.getOggettoXML();
      
      try {
        RichiestaSincronaIstanzaCollaudoDocument istanzaCollaudoDocument =
          RichiestaSincronaIstanzaCollaudoDocument.Factory.parse(xmlCollaudo);

        boolean isMessaggioDiTest = 
          istanzaCollaudoDocument.getRichiestaSincronaIstanzaCollaudo().isSetTest()
            && istanzaCollaudoDocument.getRichiestaSincronaIstanzaCollaudo().getTest();
        
        if (! isMessaggioDiTest) {
        
          // si esegue il controllo sintattico del messaggio
          XmlOptions validationOptions = new XmlOptions();
          ArrayList<XmlValidationError> validationErrors = new ArrayList<XmlValidationError>();
          validationOptions.setErrorListener(validationErrors);
          boolean isSintassiXmlOK = istanzaCollaudoDocument.validate(validationOptions);
  
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
            LottoCollaudoType[] arrayLottiCollaudi =
              istanzaCollaudoDocument.getRichiestaSincronaIstanzaCollaudo().getListaLottiCollaudoArray();
                        
            if (arrayLottiCollaudi != null && arrayLottiCollaudi.length > 0) {
              // HashMap per caricare gli oggetti ResponseLottoType per ciascun lotto, con CIG come chiave della hashMap
              HashMap<String, ResponseLottoType> hmResponseLotti = new HashMap<String, ResponseLottoType>();
              
              StringBuilder strQueryCig = new StringBuilder("'");
              for (int concl = 0; concl < arrayLottiCollaudi.length && result.isSuccess(); concl++) {
                LottoCollaudoType oggettoLottoColluado = arrayLottiCollaudi[concl];
                String cigLotto = oggettoLottoColluado.getW3CIG();
                
                strQueryCig.append(cigLotto);
                if (concl + 1 < arrayLottiCollaudi.length)
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
              	if (listaCodGara.size() !=  arrayLottiCollaudi.length) {
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

              for (int iniz = 0; iniz < arrayLottiCollaudi.length && result.isSuccess(); iniz++) {
                LottoCollaudoType oggettoLottoCollaudo = arrayLottiCollaudi[iniz];
                String cigLotto = oggettoLottoCollaudo.getW3CIG();

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
	
	                Long faseEsecuz = new Long(CostantiWSW9.COLLAUDO_CONTRATTO);
	                	
	                boolean esisteFaseCollaudo = UtilitySITAT.existsFase(codiceGara, codiceLotto, new Long(1),
	                    faseEsecuz.intValue(), this.sqlManager);
	                // Controlli preliminari su gara e su lotto.
	                if (esisteFaseCollaudo || UtilitySITAT.isFaseAbilitata(codiceGara, codiceLotto, new Long(1), faseEsecuz.intValue(), this.sqlManager)) {
	                	if (esisteFaseCollaudo || UtilitySITAT.isFaseVisualizzabile(codiceGara, codiceLotto, faseEsecuz.intValue(), this.sqlManager)) {
	
	                    CollaudoType lottoCollaudo = oggettoLottoCollaudo.getCollaudo();
	                      	
	                    // Record in W9FASI.
	                    UtilitySITAT.istanziaFase(this.sqlManager, codiceGara, codiceLotto, faseEsecuz, new Long(1));
	                    	
	                  	double importoFinaleLavori = 0;
	                  	double importoFinaleServizi = 0;
	                  	double importoFinaleForniture = 0;
	                  	double importoSubTotale = 0;
	                  	double importoFinaleSicurezza = 0;
	                  	double importoProgettazione = 0;
	                  	double importoComplessivoAppalto = 0;
	                  	double importoDisposizione = 0;
	                    	
	                    DataColumn codGaraCollaudo = new DataColumn("W9COLL.CODGARA",
	                        new JdbcParametro(JdbcParametro.TIPO_NUMERICO, codiceGara));
	                    DataColumn codLottCollaudo = new DataColumn("W9COLL.CODLOTT",
	                        new JdbcParametro(JdbcParametro.TIPO_NUMERICO, codiceLotto));
	                    DataColumn numCollaudo = new DataColumn("W9COLL.NUM_COLL",
	                        new JdbcParametro(JdbcParametro.TIPO_NUMERICO, new Long(1)));
	                    DataColumn numAppa = new DataColumn("W9COLL.NUM_APPA",
	                        new JdbcParametro(JdbcParametro.TIPO_NUMERICO, new Long(1)));
	                    DataColumnContainer dccCollaudo = new DataColumnContainer(
	                        new DataColumn[] {codGaraCollaudo, codLottCollaudo, numCollaudo, numAppa } );
	                    
	                    if (lottoCollaudo.isSetW3DATAREG()) {
	                      dccCollaudo.addColumn("W9COLL.DATA_REGOLARE_ESEC",
	                          new JdbcParametro(JdbcParametro.TIPO_DATA, 
	                              lottoCollaudo.getW3DATAREG().getTime()));
	                      
	                      //dccCollaudo.addColumn("W9COLL.FLAG_SINGOLO_COMMISSIONE",
	                      //		new JdbcParametro(JdbcParametro.TIPO_TESTO, "S"));
	                      
	                      dccCollaudo.addColumn("W9COLL.DATA_INIZIO_OPER", 
		                        new JdbcParametro(JdbcParametro.TIPO_DATA, null));
	                      
	                      dccCollaudo.addColumn("W9COLL.DATA_CERT_COLLAUDO", 
		                        new JdbcParametro(JdbcParametro.TIPO_DATA, null));
	                      
	                      dccCollaudo.addColumn("W9COLL.DATA_DELIBERA", 
		                        new JdbcParametro(JdbcParametro.TIPO_DATA, null));
	                      
	                      if (lottoCollaudo.isSetW3DATADEL()) {
	                        dccCollaudo.addColumn("W9COLL.DATA_APPROVAZIONE", 
	  	                        new JdbcParametro(JdbcParametro.TIPO_DATA, 
	  	                          lottoCollaudo.getW3DATADEL().getTime()));
	                      }
	                      
	                    } else {
	                    	//dccCollaudo.addColumn("W9COLL.FLAG_SINGOLO_COMMISSIONE",
	                      //		new JdbcParametro(JdbcParametro.TIPO_TESTO, "C"));
	                    	
	                    	if (lottoCollaudo.isSetW3DINIZOP()) {
		                      dccCollaudo.addColumn("W9COLL.DATA_INIZIO_OPER", 
		                        new JdbcParametro(JdbcParametro.TIPO_DATA, 
		                          lottoCollaudo.getW3DINIZOP().getTime()));
		                    }
		                    if (lottoCollaudo.isSetW3DCERTCO()) {
		                      dccCollaudo.addColumn("W9COLL.DATA_CERT_COLLAUDO", 
		                        new JdbcParametro(JdbcParametro.TIPO_DATA, 
		                          lottoCollaudo.getW3DCERTCO().getTime()));
		                    }
		                    
		                    if (lottoCollaudo.isSetW3DATADEL()) {
		                      dccCollaudo.addColumn("W9COLL.DATA_DELIBERA", 
		                        new JdbcParametro(JdbcParametro.TIPO_DATA, 
		                          lottoCollaudo.getW3DATADEL().getTime()));
		                      
		                      dccCollaudo.addColumn("W9COLL.DATA_APPROVAZIONE", 
			                        new JdbcParametro(JdbcParametro.TIPO_DATA, 
			                          lottoCollaudo.getW3DATADEL().getTime()));
		                    }
	                    }
	
	                    if (lottoCollaudo.isSetW3DATACOL()) {
	                      dccCollaudo.addColumn("W9COLL.DATA_COLLAUDO_STAT",
	                          new JdbcParametro(JdbcParametro.TIPO_DATA,
	                              lottoCollaudo.getW3DATACOL().getTime()));
	                    }
	                    if (lottoCollaudo.isSetW3MODOCOL()) {
	                      dccCollaudo.addColumn("W9COLL.MODO_COLLAUDO",
	                          new JdbcParametro(JdbcParametro.TIPO_NUMERICO,
	                              Long.parseLong(lottoCollaudo.getW3MODOCOL().toString())));
	                      dccCollaudo.addColumn("W9COLL.TIPO_COLLAUDO",
	                          new JdbcParametro(JdbcParametro.TIPO_NUMERICO, new Long(1)));
	                    } else {
	                    	dccCollaudo.addColumn("W9COLL.TIPO_COLLAUDO",
	                          new JdbcParametro(JdbcParametro.TIPO_NUMERICO, new Long(2)));
	                    }
	                    if (lottoCollaudo.isSetW3DATANOM()) {
	                      dccCollaudo.addColumn("W9COLL.DATA_NOMINA_COLL",
	                          new JdbcParametro(JdbcParametro.TIPO_DATA,
	                              lottoCollaudo.getW3DATANOM().getTime()));
	                    }
	                    
	                    if (lottoCollaudo.isSetW3ESITOCO()) {
	                      dccCollaudo.addColumn("W9COLL.ESITO_COLLAUDO", 
	                        new JdbcParametro(JdbcParametro.TIPO_TESTO, 
	                          lottoCollaudo.getW3ESITOCO().toString()));
	                    }
	                    if (lottoCollaudo.isSetW3IFLAVOR()) {
	                      dccCollaudo.addColumn("W9COLL.IMP_FINALE_LAVORI",
	                        new JdbcParametro(JdbcParametro.TIPO_DECIMALE,
	                          lottoCollaudo.getW3IFLAVOR()));
	                    	importoFinaleLavori = lottoCollaudo.getW3IFLAVOR();
	                    }
	                    if (lottoCollaudo.isSetW3IFSERVI()) {
	                      dccCollaudo.addColumn("W9COLL.IMP_FINALE_SERVIZI",
	                        new JdbcParametro(JdbcParametro.TIPO_DECIMALE,
	                          lottoCollaudo.getW3IFSERVI()));
	                      importoFinaleServizi = lottoCollaudo.getW3IFSERVI();
	                    }
	                    if (lottoCollaudo.isSetW3IFFORNI()) {
	                      dccCollaudo.addColumn("W9COLL.IMP_FINALE_FORNIT",
	                        new JdbcParametro(JdbcParametro.TIPO_DECIMALE,
	                          lottoCollaudo.getW3IFFORNI()));
	                      importoFinaleForniture = lottoCollaudo.getW3IFFORNI();
	                    }
	                    if (lottoCollaudo.isSetW3IFSECUR()) {
	                      dccCollaudo.addColumn("W9COLL.IMP_FINALE_SECUR",
	                        new JdbcParametro(JdbcParametro.TIPO_DECIMALE,
	                          lottoCollaudo.getW3IFSECUR()));
	                      importoFinaleSicurezza = lottoCollaudo.getW3IFSECUR();
	                    }
	                    if (lottoCollaudo.isSetW3IMPPROG()) {
	                      dccCollaudo.addColumn("W9COLL.IMP_PROGETTAZIONE",
	                        new JdbcParametro(JdbcParametro.TIPO_DECIMALE,
	                          lottoCollaudo.getW3IMPPROG()));
	                      importoProgettazione = lottoCollaudo.getW3IMPPROG();
	                    }
	                    if (lottoCollaudo.isSetW3IMPDIS2()) {
	                      dccCollaudo.addColumn("W9COLL.IMP_DISPOSIZIONE",
	                        new JdbcParametro(JdbcParametro.TIPO_DECIMALE,
	                          lottoCollaudo.getW3IMPDIS2()));
	                      importoDisposizione = lottoCollaudo.getW3IMPDIS2();
	                    }
	                    if (lottoCollaudo.isSetW3AMMDEF()) {
	                      dccCollaudo.addColumn("W9COLL.AMM_NUM_DEFINITE",
	                        new JdbcParametro(JdbcParametro.TIPO_NUMERICO,
	                          new Long(lottoCollaudo.getW3AMMDEF())));
	                    }
	                    if (lottoCollaudo.isSetW3AMMDADE()) {
	                      dccCollaudo.addColumn("W9COLL.AMM_NUM_DADEF",
	                        new JdbcParametro(JdbcParametro.TIPO_NUMERICO,
	                            new Long(lottoCollaudo.getW3AMMDADE())));
	                    }
	                    if (lottoCollaudo.isSetW3AMMIRIC()) {
	                      dccCollaudo.addColumn("W9COLL.AMM_IMPORTO_RICH",
	                        new JdbcParametro(JdbcParametro.TIPO_DECIMALE,
	                          lottoCollaudo.getW3AMMIRIC()));
	                    }
	                    if (lottoCollaudo.isSetW3AMMIDEF()) {
	                      dccCollaudo.addColumn("W9COLL.AMM_IMPORTO_DEF",
	                        new JdbcParametro(JdbcParametro.TIPO_DECIMALE,
	                          lottoCollaudo.getW3AMMIDEF()));
	                    }
	                    if (lottoCollaudo.isSetW3ARBDEF()) {
	                      dccCollaudo.addColumn("W9COLL.ARB_NUM_DEFINITE",
	                        new JdbcParametro(JdbcParametro.TIPO_NUMERICO,
	                          new Long(lottoCollaudo.getW3ARBDEF())));
	                    }
	                    if (lottoCollaudo.isSetW3ARBDADE()) {
	                      dccCollaudo.addColumn("W9COLL.ARB_NUM_DADEF",
	                        new JdbcParametro(JdbcParametro.TIPO_NUMERICO,
	                          new Long(lottoCollaudo.getW3ARBDADE())));
	                    }
	                    if (lottoCollaudo.isSetW3ARBIRIC()) {
	                      dccCollaudo.addColumn("W9COLL.ARB_IMPORTO_RICH",
	                        new JdbcParametro(JdbcParametro.TIPO_DECIMALE,
	                          lottoCollaudo.getW3ARBIRIC()));
	                    }
	                    if (lottoCollaudo.isSetW3ARBIDEF()) {
	                      dccCollaudo.addColumn("W9COLL.ARB_IMPORTO_DEF",
	                        new JdbcParametro(JdbcParametro.TIPO_DECIMALE,
	                          lottoCollaudo.getW3ARBIDEF()));
	                    }
	                    if (lottoCollaudo.isSetW3GIUDEF()) {
	                      dccCollaudo.addColumn("W9COLL.GIU_NUM_DEFINITE",
	                        new JdbcParametro(JdbcParametro.TIPO_NUMERICO,
	                          new Long(lottoCollaudo.getW3GIUDEF())));
	                    }
	                    if (lottoCollaudo.isSetW3GIUDADE()) {
	                      dccCollaudo.addColumn("W9COLL.GIU_NUM_DADEF",
	                        new JdbcParametro(JdbcParametro.TIPO_NUMERICO,
	                          new Long(lottoCollaudo.getW3GIUDADE())));
	                    }
	                    if (lottoCollaudo.isSetW3GIUIRIC()) {
	                      dccCollaudo.addColumn("W9COLL.GIU_IMPORTO_RICH",
	                        new JdbcParametro(JdbcParametro.TIPO_DECIMALE,
	                          lottoCollaudo.getW3GIUIRIC()));
	                    }
	                    if (lottoCollaudo.isSetW3GIUIDEF()) {
	                      dccCollaudo.addColumn("W9COLL.GIU_IMPORTO_DEF",
	                        new JdbcParametro(JdbcParametro.TIPO_DECIMALE,
	                          lottoCollaudo.getW3GIUIDEF()));
	                    }
	                    if (lottoCollaudo.isSetW3TRADEF()) {
	                      dccCollaudo.addColumn("W9COLL.TRA_NUM_DEFINITE",
	                        new JdbcParametro(JdbcParametro.TIPO_DECIMALE,
	                            new Long(lottoCollaudo.getW3TRADEF())));
	                    }
	                    if (lottoCollaudo.isSetW3TRADADE()) {
	                      dccCollaudo.addColumn("W9COLL.TRA_NUM_DADEF",
	                        new JdbcParametro(JdbcParametro.TIPO_DECIMALE,
	                            new Long(lottoCollaudo.getW3TRADADE())));
	                    }
	                    if (lottoCollaudo.isSetW3TRAIRIC()) {
	                      dccCollaudo.addColumn("W9COLL.TRA_IMPORTO_RICH",
	                        new JdbcParametro(JdbcParametro.TIPO_DECIMALE,
	                          lottoCollaudo.getW3TRAIRIC()));
	                    }
	                    if (lottoCollaudo.isSetW3TRAIDEF()) {
	                      dccCollaudo.addColumn("W9COLL.TRA_IMPORTO_DEF",
	                        new JdbcParametro(JdbcParametro.TIPO_DECIMALE,
	                          lottoCollaudo.getW3TRAIDEF()));
	                    }
	                    if (lottoCollaudo.isSetW3IMPSUBT2()) {
	                      dccCollaudo.addColumn("W9COLL.IMP_SUBTOTALE",
	                        new JdbcParametro(JdbcParametro.TIPO_DECIMALE,
	                          lottoCollaudo.getW3IMPSUBT2()));
	                    } else {
	                    	if ((importoFinaleLavori + importoFinaleServizi + importoFinaleForniture) != 0) {
	                    		dccCollaudo.addColumn("W9COLL.IMP_SUBTOTALE",
	                            new JdbcParametro(JdbcParametro.TIPO_DECIMALE,
	                            		importoFinaleLavori + importoFinaleServizi + importoFinaleForniture));
	                    		importoSubTotale = importoFinaleLavori + importoFinaleServizi + importoFinaleForniture;
	                    	}
	                    }
	                    if (lottoCollaudo.isSetW3IMPCOMA2()) {
	                      dccCollaudo.addColumn("W9COLL.IMP_COMPL_APPALTO",
	                        new JdbcParametro(JdbcParametro.TIPO_DECIMALE,
	                          lottoCollaudo.getW3IMPCOMA2()));
	                      importoComplessivoAppalto = lottoCollaudo.getW3IMPCOMA2();
	                    } else {
	                    	if ((importoSubTotale + importoFinaleSicurezza + importoProgettazione) != 0) {
	                    		dccCollaudo.addColumn("W9COLL.IMP_COMPL_APPALTO",
	                            new JdbcParametro(JdbcParametro.TIPO_DECIMALE,
	                            		importoSubTotale + importoFinaleSicurezza + importoProgettazione));
	                    		importoComplessivoAppalto = importoSubTotale + importoFinaleSicurezza + importoProgettazione;
	                    	}
	                    }
	                    if (lottoCollaudo.isSetW3IMPCOMI2()) {
	                      dccCollaudo.addColumn("W9COLL.IMP_COMPL_INTERVENTO",
	                        new JdbcParametro(JdbcParametro.TIPO_DECIMALE,
	                          lottoCollaudo.getW3IMPCOMI2()));
	                    } else {
	                    	if ((importoComplessivoAppalto + importoDisposizione) != 0 ) {
	                    		dccCollaudo.addColumn("W9COLL.IMP_COMPL_INTERVENTO",
	                            new JdbcParametro(JdbcParametro.TIPO_DECIMALE,
	                            		importoComplessivoAppalto + importoDisposizione));
	                    	}
	                    }
	                    if (lottoCollaudo.isSetW3COLLLEST()) {
	                      dccCollaudo.addColumn("W9COLL.LAVORI_ESTESI",
	                        new JdbcParametro(JdbcParametro.TIPO_TESTO,
	                          lottoCollaudo.getW3COLLLEST() ? "1" : "2"));
	                    }
	
	                    Long numSubappalti = (Long) this.sqlManager.getObject(
	                    		"select count(*) from W9SUBA where CODGARA=? and CODLOTT=?", 
	                    		new Object[] { codiceGara, codiceLotto });
	                    
	                   	dccCollaudo.addColumn("W9COLL.FLAG_SUBAPPALTATORI",
	                        new JdbcParametro(JdbcParametro.TIPO_TESTO,
	                        		numSubappalti.longValue() > 0 ? "1" : "2"));
	                    
	                    if (!esisteFaseCollaudo) {
	                    	dccCollaudo.insert("W9COLL", this.sqlManager);
	
	                    	UtilitySITAT.gestioneIncarichiProfessionali(result,
	                    			codFiscaleStazAppaltante, credenzialiUtente, faseEsecuz.intValue(), codiceGara,
	                    			codiceLotto, esisteFaseCollaudo, lottoCollaudo.getListaIncarichiProfessionaliArray(),
	                    			this.sqlManager, this.geneManager, this.rupManager, false, false, false, false);
	                        
	                      if (UtilitySITAT.isScritturaNoteAvvisiAttiva()) {
	                      	String codiceCIG = UtilitySITAT.getCIGLotto(codiceGara, codiceLotto, this.sqlManager);
	                     		UtilitySITAT.insertNoteAvvisi("W9LOTT", codiceGara.toString(), codiceLotto.toString(),
	      										null, null, null, "CIG " + codiceCIG + ": inserita fase 'Collaudo contratto'", null,
	       										this.sqlManager);
	                      }
	                        
	                      numeroLottiImportati++;
	                      UtilitySITAT.aggiungiMsgSchedaA(hmResponseLotti, cigLotto, true, "Fase 'Collaudo' importata");
	
	                    } else if (esisteFaseCollaudo && sovrascrivereDatiEsistenti) {
	                      	
	                    	DataColumnContainer dccCollaudoDB = new DataColumnContainer(this.sqlManager, "W9COLL",
	                     			"select DATA_REGOLARE_ESEC, DATA_COLLAUDO_STAT, MODO_COLLAUDO, DATA_NOMINA_COLL, DATA_INIZIO_OPER, DATA_CERT_COLLAUDO, " 
	                     					 + "DATA_DELIBERA, ESITO_COLLAUDO, IMP_FINALE_LAVORI, IMP_FINALE_SERVIZI, IMP_FINALE_FORNIT, IMP_FINALE_SECUR, "
	                     					 + "IMP_PROGETTAZIONE, IMP_DISPOSIZIONE, AMM_NUM_DEFINITE, AMM_NUM_DADEF, AMM_IMPORTO_RICH, AMM_IMPORTO_DEF, "
	                     					 + "ARB_NUM_DEFINITE, ARB_NUM_DADEF, ARB_IMPORTO_RICH, ARB_IMPORTO_DEF, GIU_NUM_DEFINITE, GIU_NUM_DADEF, "
	                     					 + "GIU_IMPORTO_RICH, GIU_IMPORTO_DEF, TRA_NUM_DEFINITE, TRA_NUM_DADEF, TRA_IMPORTO_RICH, TRA_IMPORTO_DEF, "
	                     					 + "IMP_SUBTOTALE, IMP_COMPL_APPALTO, IMP_COMPL_INTERVENTO, LAVORI_ESTESI, FLAG_SUBAPPALTATORI, "
	                  						 + "FLAG_SINGOLO_COMMISSIONE, DATA_APPROVAZIONE, CODGARA, CODLOTT, NUM_COLL, NUM_APPA "
	                     			+ "from W9COLL where CODGARA=? and CODLOTT=? and NUM_COLL=1 and NUM_APPA=1", 
	                     			new Object[] { codiceGara, codiceLotto } );
	                      	
	                     	Iterator<Entry<String, DataColumn>> iterInizioDB = dccCollaudoDB.getColonne().entrySet().iterator();
	                      while (iterInizioDB.hasNext()) {
	                       	Entry<String, DataColumn> entry = iterInizioDB.next(); 
	                        String nomeCampo = entry.getKey();
	                        if (dccCollaudo.isColumn(nomeCampo)) {
	                        	dccCollaudoDB.setValue(nomeCampo, dccCollaudo.getColumn(nomeCampo).getValue());
	                        }
	                      }
	                      
	                      if (lottoCollaudo.isSetW3DATAREG()) {
	                      	dccCollaudoDB.setValue("W9COLL.DATA_REGOLARE_ESEC",
	                            new JdbcParametro(JdbcParametro.TIPO_DATA, 
	                              lottoCollaudo.getW3DATAREG().getTime()));
	                      	//dccCollaudo.addColumn("W9COLL.DATA_REGOLARE_ESEC",
	                        //    new JdbcParametro(JdbcParametro.TIPO_DATA, 
	                        //        lottoCollaudo.getW3DATAREG().getTime()));
	                      	
	                      	//dccCollaudoDB.setValue("W9COLL.FLAG_SINGOLO_COMMISSIONE","S");
	                        //dccCollaudo.addColumn("W9COLL.FLAG_SINGOLO_COMMISSIONE",
	                        //		new JdbcParametro(JdbcParametro.TIPO_TESTO, "S"));
	                        
	                      	dccCollaudoDB.setValue("W9COLL.DATA_INIZIO_OPER", null);
	                        //dccCollaudo.addColumn("W9COLL.DATA_INIZIO_OPER", 
	  	                    //    new JdbcParametro(JdbcParametro.TIPO_DATA, null));
	                        
	                      	dccCollaudoDB.setValue("W9COLL.DATA_CERT_COLLAUDO", null);
	                        //dccCollaudo.addColumn("W9COLL.DATA_CERT_COLLAUDO", 
	  	                    //    new JdbcParametro(JdbcParametro.TIPO_DATA, null));
	                        
	                      	dccCollaudoDB.setValue("W9COLL.DATA_DELIBERA", null);
	                        //dccCollaudo.addColumn("W9COLL.DATA_DELIBERA", 
	  	                    //    new JdbcParametro(JdbcParametro.TIPO_DATA, null));
	                        
	                      	if (lottoCollaudo.isSetW3DATADEL()) {
	                        	dccCollaudoDB.setValue("W9COLL.DATA_APPROVAZIONE", 
	                        			new JdbcParametro(JdbcParametro.TIPO_DATA, 
	            	                    lottoCollaudo.getW3DATADEL().getTime()));
	                          //dccCollaudo.addColumn("W9COLL.DATA_APPROVAZIONE", 
	    	                    //    new JdbcParametro(JdbcParametro.TIPO_DATA, 
	    	                    //      lottoCollaudo.getW3DATADEL().getTime()));
	                        }
	                        
	                      } else {
	                      	//dccCollaudoDB.setValue("W9COLL.FLAG_SINGOLO_COMMISSIONE","C");
	                      	//dccCollaudo.addColumn("W9COLL.FLAG_SINGOLO_COMMISSIONE",
	                        //		new JdbcParametro(JdbcParametro.TIPO_TESTO, "C"));
	                      	
	                      	if (lottoCollaudo.isSetW3DINIZOP()) {
	                      		dccCollaudoDB.setValue("W9COLL.DATA_INIZIO_OPER",
	                      				new JdbcParametro(JdbcParametro.TIPO_DATA, 
	      	                          lottoCollaudo.getW3DINIZOP().getTime()));
	  	                      //dccCollaudo.addColumn("W9COLL.DATA_INIZIO_OPER", 
	  	                      //  new JdbcParametro(JdbcParametro.TIPO_DATA, 
	  	                      //    lottoCollaudo.getW3DINIZOP().getTime()));
	  	                    }
	  	                    if (lottoCollaudo.isSetW3DCERTCO()) {
	  	                    	dccCollaudoDB.setValue("W9COLL.DATA_CERT_COLLAUDO", 
	    	                        new JdbcParametro(JdbcParametro.TIPO_DATA, 
	      	                          lottoCollaudo.getW3DCERTCO().getTime()));
	  	                      //dccCollaudo.addColumn("W9COLL.DATA_CERT_COLLAUDO", 
	  	                      //  new JdbcParametro(JdbcParametro.TIPO_DATA, 
	  	                      //    lottoCollaudo.getW3DCERTCO().getTime()));
	  	                    }
	  	                    
	  	                    if (lottoCollaudo.isSetW3DATADEL()) {
	  	                    	dccCollaudoDB.setValue("W9COLL.DATA_DELIBERA", 
	    	                        new JdbcParametro(JdbcParametro.TIPO_DATA, 
	      	                          lottoCollaudo.getW3DATADEL().getTime()));
	  	                      //dccCollaudo.addColumn("W9COLL.DATA_DELIBERA", 
	  	                      //  new JdbcParametro(JdbcParametro.TIPO_DATA, 
	  	                      //    lottoCollaudo.getW3DATADEL().getTime()));
	  	                      
	  	                    	dccCollaudoDB.setValue("W9COLL.DATA_APPROVAZIONE", 
	  		                        new JdbcParametro(JdbcParametro.TIPO_DATA, 
	    		                          lottoCollaudo.getW3DATADEL().getTime()));
	  	                      //dccCollaudo.addColumn("W9COLL.DATA_APPROVAZIONE", 
	  		                    //    new JdbcParametro(JdbcParametro.TIPO_DATA, 
	  		                    //      lottoCollaudo.getW3DATADEL().getTime()));
	  	                    }
	                      }
	
	                     	if (dccCollaudoDB.isModifiedTable("W9COLL")) {
	                     		dccCollaudoDB.getColumn("W9COLL.CODGARA").setChiave(true);
	                     		dccCollaudoDB.getColumn("W9COLL.CODLOTT").setChiave(true);
	                     		dccCollaudoDB.getColumn("W9COLL.NUM_COLL").setChiave(true);
	                     		dccCollaudoDB.update("W9COLL", this.sqlManager);
	                     	}
	                     	
	                        /*if (lottoCollaudo.isSetW3DATAREG()) {
	                          dccCollaudoDB.setValue("W9COLL.DATA_REGOLARE_ESEC",
	                              new JdbcParametro(JdbcParametro.TIPO_DATA, 
	                                  lottoCollaudo.getW3DATAREG().getTime()));
	                        }
	                        if (lottoCollaudo.isSetW3DATACOL()) {
	                          dccCollaudoDB.setValue("W9COLL.DATA_COLLAUDO_STAT",
	                              new JdbcParametro(JdbcParametro.TIPO_DATA,
	                                  lottoCollaudo.getW3DATACOL().getTime()));
	                        }
	                        if (lottoCollaudo.isSetW3MODOCOL()) {
	                          dccCollaudoDB.setValue("W9COLL.MODO_COLLAUDO",
	                              new JdbcParametro(JdbcParametro.TIPO_NUMERICO,
	                                  Long.parseLong(lottoCollaudo.getW3MODOCOL().toString())));
	                        }
	                        if (lottoCollaudo.isSetW3DATANOM()) {
	                          dccCollaudoDB.setValue("W9COLL.DATA_NOMINA_COLL",
	                              new JdbcParametro(JdbcParametro.TIPO_DATA,
	                                  lottoCollaudo.getW3DATANOM().getTime()));
	                        }
	                        if (lottoCollaudo.isSetW3DINIZOP()) {
	                          dccCollaudoDB.setValue("W9COLL.DATA_INIZIO_OPER", 
	                            new JdbcParametro(JdbcParametro.TIPO_DATA, 
	                              lottoCollaudo.getW3DINIZOP().getTime()));
	                        }
	                        if (lottoCollaudo.isSetW3DCERTCO()) {
	                          dccCollaudoDB.setValue("W9COLL.DATA_CERT_COLLAUDO", 
	                            new JdbcParametro(JdbcParametro.TIPO_DATA, 
	                              lottoCollaudo.getW3DCERTCO().getTime()));
	                        }
	                        if (lottoCollaudo.isSetW3DATADEL()) {
	                          dccCollaudoDB.setValue("W9COLL.DATA_DELIBERA", 
	                            new JdbcParametro(JdbcParametro.TIPO_DATA, 
	                              lottoCollaudo.getW3DATADEL().getTime()));
	                        }
	                        if (lottoCollaudo.isSetW3ESITOCO()) {
	                          dccCollaudoDB.setValue("W9COLL.ESITO_COLLAUDO",
	                            new JdbcParametro(JdbcParametro.TIPO_TESTO, 
	                              lottoCollaudo.getW3ESITOCO().toString()));
	                        }
	                        if (lottoCollaudo.isSetW3IFLAVOR()) {
	                          dccCollaudoDB.setValue("W9COLL.IMP_FINALE_LAVORI",
	                            new JdbcParametro(JdbcParametro.TIPO_DECIMALE,
	                              lottoCollaudo.getW3IFLAVOR()));
	                        }
	                        if (lottoCollaudo.isSetW3IFSERVI()) {
	                          dccCollaudoDB.setValue("W9COLL.IMP_FINALE_SERVIZI",
	                            new JdbcParametro(JdbcParametro.TIPO_DECIMALE,
	                              lottoCollaudo.getW3IFSERVI()));
	                        }
	                        if (lottoCollaudo.isSetW3IFFORNI()) {
	                          dccCollaudoDB.setValue("W9COLL.IMP_FINALE_FORNIT",
	                            new JdbcParametro(JdbcParametro.TIPO_DECIMALE,
	                              lottoCollaudo.getW3IFFORNI()));
	                        }
	                        if (lottoCollaudo.isSetW3IFSECUR()) {
	                          dccCollaudoDB.setValue("W9COLL.IMP_FINALE_SECUR",
	                            new JdbcParametro(JdbcParametro.TIPO_DECIMALE,
	                              lottoCollaudo.getW3IFSECUR()));
	                        }
	                        if (lottoCollaudo.isSetW3IMPPROG()) {
	                          dccCollaudoDB.setValue("W9COLL.IMP_PROGETTAZIONE",
	                            new JdbcParametro(JdbcParametro.TIPO_DECIMALE,
	                              lottoCollaudo.getW3IMPPROG()));
	                        }
	                        if (lottoCollaudo.isSetW3IMPDIS2()) {
	                          dccCollaudoDB.setValue("W9COLL.IMP_DISPOSIZIONE",
	                            new JdbcParametro(JdbcParametro.TIPO_DECIMALE,
	                              lottoCollaudo.getW3IMPDIS2()));
	                        }
	                        if (lottoCollaudo.isSetW3AMMDEF()) {
	                          dccCollaudoDB.setValue("W9COLL.AMM_NUM_DEFINITE",
	                            new JdbcParametro(JdbcParametro.TIPO_NUMERICO,
	                              new Long(lottoCollaudo.getW3AMMDEF())));
	                        }
	                        if (lottoCollaudo.isSetW3AMMDADE()) {
	                          dccCollaudoDB.setValue("W9COLL.AMM_NUM_DADEF",
	                            new JdbcParametro(JdbcParametro.TIPO_NUMERICO,
	                                new Long(lottoCollaudo.getW3AMMDADE())));
	                        }
	                        if (lottoCollaudo.isSetW3AMMIRIC()) {
	                          dccCollaudoDB.setValue("W9COLL.AMM_IMPORTO_RICH",
	                            new JdbcParametro(JdbcParametro.TIPO_DECIMALE,
	                              lottoCollaudo.getW3AMMIRIC()));
	                        }
	                        if (lottoCollaudo.isSetW3AMMIDEF()) {
	                          dccCollaudoDB.setValue("W9COLL.AMM_IMPORTO_DEF",
	                            new JdbcParametro(JdbcParametro.TIPO_DECIMALE,
	                              lottoCollaudo.getW3AMMIDEF()));
	                        }
	                        if (lottoCollaudo.isSetW3ARBDEF()) {
	                          dccCollaudoDB.setValue("W9COLL.ARB_NUM_DEFINITE",
	                            new JdbcParametro(JdbcParametro.TIPO_NUMERICO,
	                              new Long(lottoCollaudo.getW3ARBDEF())));
	                        }
	                        if (lottoCollaudo.isSetW3ARBDADE()) {
	                          dccCollaudoDB.setValue("W9COLL.ARB_NUM_DADEF",
	                            new JdbcParametro(JdbcParametro.TIPO_NUMERICO,
	                              new Long(lottoCollaudo.getW3ARBDADE())));
	                        }
	                        if (lottoCollaudo.isSetW3ARBIRIC()) {
	                          dccCollaudoDB.setValue("W9COLL.ARB_IMPORTO_RICH",
	                            new JdbcParametro(JdbcParametro.TIPO_DECIMALE,
	                              lottoCollaudo.getW3ARBIRIC()));
	                        }
	                        if (lottoCollaudo.isSetW3ARBIDEF()) {
	                          dccCollaudoDB.setValue("W9COLL.ARB_IMPORTO_DEF",
	                            new JdbcParametro(JdbcParametro.TIPO_DECIMALE,
	                              lottoCollaudo.getW3ARBIDEF()));
	                        }
	                        if (lottoCollaudo.isSetW3GIUDEF()) {
	                          dccCollaudoDB.setValue("W9COLL.GIU_NUM_DEFINITE",
	                            new JdbcParametro(JdbcParametro.TIPO_NUMERICO,
	                              new Long(lottoCollaudo.getW3GIUDEF())));
	                        }
	                        if (lottoCollaudo.isSetW3GIUDADE()) {
	                          dccCollaudoDB.setValue("W9COLL.GIU_NUM_DADEF",
	                            new JdbcParametro(JdbcParametro.TIPO_NUMERICO,
	                              new Long(lottoCollaudo.getW3GIUDADE())));
	                        }
	                        if (lottoCollaudo.isSetW3GIUIRIC()) {
	                          dccCollaudoDB.setValue("W9COLL.GIU_IMPORTO_RICH",
	                            new JdbcParametro(JdbcParametro.TIPO_DECIMALE,
	                              lottoCollaudo.getW3GIUIRIC()));
	                        }
	                        if (lottoCollaudo.isSetW3GIUIDEF()) {
	                          dccCollaudoDB.setValue("W9COLL.GIU_IMPORTO_DEF",
	                            new JdbcParametro(JdbcParametro.TIPO_DECIMALE,
	                              lottoCollaudo.getW3GIUIDEF()));
	                        }
	                        if (lottoCollaudo.isSetW3TRADEF()) {
	                          dccCollaudoDB.setValue("W9COLL.TRA_NUM_DEFINITE",
	                            new JdbcParametro(JdbcParametro.TIPO_DECIMALE,
	                                new Long(lottoCollaudo.getW3TRADEF())));
	                        }
	                        if (lottoCollaudo.isSetW3TRADADE()) {
	                          dccCollaudoDB.setValue("W9COLL.TRA_NUM_DADEF",
	                            new JdbcParametro(JdbcParametro.TIPO_DECIMALE,
	                                new Long(lottoCollaudo.getW3TRADADE())));
	                        }
	                        if (lottoCollaudo.isSetW3TRAIRIC()) {
	                          dccCollaudoDB.setValue("W9COLL.TRA_IMPORTO_RICH",
	                            new JdbcParametro(JdbcParametro.TIPO_DECIMALE,
	                              lottoCollaudo.getW3TRAIRIC()));
	                        }
	                        if (lottoCollaudo.isSetW3TRAIDEF()) {
	                          dccCollaudoDB.setValue("W9COLL.TRA_IMPORTO_DEF",
	                            new JdbcParametro(JdbcParametro.TIPO_DECIMALE,
	                              lottoCollaudo.getW3TRAIDEF()));
	                        }
	                        if (lottoCollaudo.isSetW3IMPSUBT2()) {
	                          dccCollaudoDB.setValue("W9COLL.IMP_SUBTOTALE",
	                            new JdbcParametro(JdbcParametro.TIPO_DECIMALE,
	                              lottoCollaudo.getW3IMPSUBT2()));
	                        }
	                        if (lottoCollaudo.isSetW3IMPCOMA2()) {
	                          dccCollaudoDB.setValue("W9COLL.IMP_COMPL_APPALTO",
	                            new JdbcParametro(JdbcParametro.TIPO_DECIMALE,
	                              lottoCollaudo.getW3IMPCOMA2()));
	                        }
	                        if (lottoCollaudo.isSetW3IMPCOMI2()) {
	                          dccCollaudoDB.setValue("W9COLL.IMP_COMPL_INTERVENTO",
	                            new JdbcParametro(JdbcParametro.TIPO_DECIMALE,
	                              lottoCollaudo.getW3IMPCOMI2()));
	                        }
	                        if (lottoCollaudo.isSetW3COLLLEST()) {
	                          dccCollaudoDB.setValue("W9COLL.LAVORI_ESTESI",
	                            new JdbcParametro(JdbcParametro.TIPO_TESTO,
	                              lottoCollaudo.getW3COLLLEST() ? "1" : "2"));
	                        }
	                      	
	                        // Update di W9COLL
	                        if (dccCollaudoDB.isModifiedTable("W9COLL")) {
	                        	dccCollaudoDB.getColumn("W9COLL.CODGARA").setChiave(true);
	                        	dccCollaudoDB.getColumn("W9COLL.CODLOTT").setChiave(true);
	                        	dccCollaudoDB.getColumn("W9COLL.NUM_COLL").setChiave(true);
	                        	dccCollaudoDB.update("W9COLL", this.sqlManager);
	                        } */
	
	                      // Ricalcolo dei alcuni campi calcolati dopo aver effettuato l'update dei campi gestiti dal WS
	                      HashMap<String, Object> hmImporti = this.sqlManager.getHashMap(
	                       		"select IMP_FINALE_LAVORI, IMP_FINALE_SERVIZI, IMP_FINALE_FORNIT, IMP_FINALE_SECUR, IMP_PROGETTAZIONE, IMP_DISPOSIZIONE "
	                       	 + " from W9COLL where CODGARA=? and CODLOTT=? and NUM_COLL=1", new Object[] { codiceGara, codiceLotto });
	                        
	                      Object objImportoFinaleLavori = ((JdbcParametro) hmImporti.get("IMP_FINALE_LAVORI")).getValue();
	                      Object objImportoFinaleServizi = ((JdbcParametro) hmImporti.get("IMP_FINALE_SERVIZI")).getValue();
	                      Object objImportoFinaleForniture = ((JdbcParametro) hmImporti.get("IMP_FINALE_FORNIT")).getValue();
	                      Object objImportoFinaleSicurezza = ((JdbcParametro) hmImporti.get("IMP_FINALE_SECUR")).getValue();
	                      Object objImportoProgettazione = ((JdbcParametro) hmImporti.get("IMP_PROGETTAZIONE")).getValue();
	                      Object objImportoDisposizione = ((JdbcParametro) hmImporti.get("IMP_DISPOSIZIONE")).getValue();
	                      
	                      importoFinaleLavori = 0;
	                    	importoFinaleServizi = 0;
	                    	importoFinaleForniture = 0;
	                    	importoFinaleSicurezza = 0;
	                    	importoProgettazione = 0;
	                    	importoDisposizione = 0;
	
	                    	if (objImportoFinaleLavori != null) {
	                      	if (objImportoFinaleLavori instanceof Double) {
	                      		importoFinaleLavori = ((Double) objImportoFinaleLavori).doubleValue();
	                      	} else if (objImportoFinaleLavori instanceof Long) {
	                      		importoFinaleLavori = ((Long) objImportoFinaleLavori).doubleValue();
	                      	} 
	                      }
	            					if (objImportoFinaleServizi != null) {
	                      	if (objImportoFinaleServizi instanceof Double) {
	                      		importoFinaleServizi = ((Double) objImportoFinaleServizi).doubleValue();
	                      	} else if (objImportoFinaleServizi instanceof Long) {
	                      		importoFinaleServizi = ((Long) objImportoFinaleServizi).doubleValue();
	                      	}
	                      }
	            					if (objImportoFinaleForniture != null) {
	                      	if (objImportoFinaleForniture instanceof Double) {
	                      		importoFinaleForniture = ((Double) objImportoFinaleForniture).doubleValue();
	                      	} else if (objImportoFinaleForniture instanceof Long) {
	                      		importoFinaleForniture = ((Long) objImportoFinaleForniture).doubleValue();
	                      	}
	                      }
	            					if (objImportoFinaleSicurezza != null) {
	                      	if (objImportoFinaleSicurezza instanceof Double) {
	                      		importoFinaleSicurezza = ((Double) objImportoFinaleSicurezza).doubleValue();
	                      	} else if (objImportoFinaleSicurezza instanceof Long) {
	                      		importoFinaleSicurezza = ((Long) objImportoFinaleSicurezza).doubleValue();
	                      	}
	                      }
	            					if (objImportoProgettazione != null) {
	                      	if (objImportoProgettazione instanceof Double) {
	                      		importoProgettazione = ((Double) objImportoProgettazione).doubleValue();
	                      	} else if (objImportoProgettazione instanceof Long) {
	                      		importoProgettazione = ((Long) objImportoProgettazione).doubleValue();
	                      	}
	                      }
	            					if (objImportoDisposizione != null) {
	                      	if (objImportoDisposizione instanceof Double) {
	                      		importoDisposizione = ((Double) objImportoDisposizione).doubleValue();
	                      	} else if (objImportoDisposizione instanceof Long) {
	                      		importoDisposizione = ((Long) objImportoDisposizione).doubleValue();
	                      	}
	                      }
	                    	
	                    	importoSubTotale = importoFinaleLavori + importoFinaleServizi + importoFinaleForniture;
	                    	importoComplessivoAppalto = importoSubTotale + importoFinaleSicurezza + importoProgettazione;
	                    	double importoComplessivoIntervento = importoComplessivoAppalto + importoDisposizione;
	                    	
	                    	this.sqlManager.update("UPDATE W9COLL set IMP_SUBTOTALE=?, IMP_COMPL_APPALTO=?, IMP_COMPL_INTERVENTO=? "
	                        	+	" where CODGARA=? and CODLOTT=? and NUM_COLL=1", 
	                        	new Object[] { importoSubTotale, importoComplessivoAppalto, importoComplessivoIntervento, codiceGara, codiceLotto });
	                      
	                      UtilitySITAT.gestioneIncarichiProfessionali(result,
	                      		codFiscaleStazAppaltante, credenzialiUtente, faseEsecuz.intValue(), codiceGara,
	                      		codiceLotto, esisteFaseCollaudo, lottoCollaudo.getListaIncarichiProfessionaliArray(),
	                      		this.sqlManager, this.geneManager, this.rupManager, false, false, false, false);
	                    	
	                      // Numero collaudatori tra gli incaricati professionali per il collaudo (w9inca.sezione = 'CO')
	                      Long numeroCollaudatori = (Long) this.sqlManager.getObject(
	                      		"select count(*) from W9INCA where CODGARA=? and CODLOTT=? and NUM=1 and SEZIONE in ('CO') and ID_RUOLO=13", 
	                      		new Object[] { codiceGara, codiceLotto });
	                      // Se numeroCollaudatori = 1, allora FLAG_SINGOLO_COMMISSIONE=S (Singolo), altrimenti FLAG_SINGOLO_COMMISSIONE=C (Commissione)
	                      if (numeroCollaudatori == 1) {
	                      	this.sqlManager.update("update W9COLL set FLAG_SINGOLO_COMMISSIONE='S' where CODGARA=? and CODLOTT=? and NUM_COLL=1",
	                      			new Object[] { codiceGara, codiceLotto });
	                      } else {
	                      	this.sqlManager.update("update W9COLL set FLAG_SINGOLO_COMMISSIONE='C' where CODGARA=? and CODLOTT=? and NUM_COLL=1",
	                      			new Object[] { codiceGara, codiceLotto });
	                      }
	                      
	                    	if (UtilitySITAT.isScritturaNoteAvvisiAttiva()) {
	                      	String codiceCIG = UtilitySITAT.getCIGLotto(codiceGara, codiceLotto, this.sqlManager);
	                    		UtilitySITAT.insertNoteAvvisi("W9LOTT", codiceGara.toString(), codiceLotto.toString(),
	                    				null, null, null, "CIG " + codiceCIG + ": modificata fase 'Collaudo contratto'", null,
	          									this.sqlManager);
	                      }
	
	                      numeroLottiAggiornati++;
	                      UtilitySITAT.aggiungiMsgSchedaA(hmResponseLotti, cigLotto, true, "Fase 'Collaudo' aggiornata");
	                      
	                    } else {
	                      // Caso in cui in base dati esiste gia' la fase di collaudo contratto per il lotto
	                      // ed il flag di sovrascrittura dei dati e' valorizzato a false.
	
	                      StringBuilder strLog = new StringBuilder(credenzialiUtente.getPrefissoLogger());
	                      strLog.append(" Collaudo del lotto con CIG=");
	                      strLog.append(cigLotto);
	                      strLog.append(" non e' stato importato perche' la fase gia' esiste nella base dati " +
	                          "e non la si vuole sovrascrivere.");
	                      logger.info(strLog.toString());
	                      
	                      numeroLottiNonImportati++;
	                      
	                      UtilitySITAT.aggiungiMsgSchedaA(hmResponseLotti, cigLotto, false,
	                      		StringUtils.replace(ConfigManager.getValore("error.collaudo.esistente"),
	                      				"{0}", cigLotto));
	                    }
	                  } else {
	                    // Non esiste la fase di conclusione contratto
	                    StringBuilder strLog = new StringBuilder(credenzialiUtente.getPrefissoLogger());
	                    strLog.append("Il lotto con CIG='");
	                    strLog.append(cigLotto);
	                    strLog.append(" non e' stato importato perche' non ha superato i controlli preliminari della fase del collaudo. "
	                    		+ "Nel caso specifico la condizione (isS2 && !isE1 && !isSAQ && isEA) non risulta verificata.");
	                    logger.error(strLog);
	                      
	                    numeroLottiNonImportati++;
	                      
	                    UtilitySITAT.aggiungiMsgSchedaA(hmResponseLotti, cigLotto, false,
	                    		StringUtils.replace(ConfigManager.getValore("error.collaudo.noVisibile"),
	                    				"{0}", cigLotto));
	                  }
	                } else {
	                  // Condizioni di visualizzazione non verificate
	                  StringBuilder strLog = new StringBuilder(credenzialiUtente.getPrefissoLogger());
	                  strLog.append("Uno o piu' lotti indicati non hanno la fase di conclusione contratto. Il lotto con CIG='");
	                  strLog.append(cigLotto);
	                  strLog.append("' non ha la fase conclusione contratto. Impossibile importare il collaudo del contratto." );
	                  logger.error(strLog);
	                    
	                  UtilitySITAT.aggiungiMsgSchedaA(hmResponseLotti, cigLotto, false,
	                   		ConfigManager.getValore("error.collaudo.noFlussiPrecedenti"));
	
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
          strLog.append(collaudo.toString());
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
      logger.debug("istanziaCollaudo: fine metodo");
      
      logger.debug("Response : " + UtilitySITAT.messaggioLogEsteso(result));
    }
    
    // MODIFICA TEMPORANEA -- DA CANCELLARE ---------------
    result.setError(UtilitySITAT.messaggioEsteso(result));
    // ----------------------------------------------------

    return result;
  }
  
}
