package it.eldasoft.sil.w3.bl;

import it.eldasoft.gene.bl.GenChiaviManager;
import it.eldasoft.gene.bl.GeneManager;
import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.bl.admin.UffintManager;
import it.eldasoft.gene.db.datautils.DataColumn;
import it.eldasoft.gene.db.datautils.DataColumnContainer;
import it.eldasoft.gene.db.domain.UfficioIntestatario;
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.gene.tags.utils.UtilityTags;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.simog.ws.EsitoVerificaCIG;
import it.eldasoft.simog.ws.xmlbeans.CategorieMerceologicheType;
import it.eldasoft.simog.ws.xmlbeans.GaraDocument;
import it.eldasoft.simog.ws.xmlbeans.GaraType;
import it.eldasoft.simog.ws.xmlbeans.LottoType;
import it.eldasoft.simog.ws.xmlbeans.SmartCIGDocument;
import it.eldasoft.simog.ws.xmlbeans.SmartCIGType;
import it.eldasoft.simog.ws.xmlbeans.TecnicoType;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlOptions;
import org.springframework.transaction.TransactionStatus;

public class EldasoftSIMOGWSManager {

  static Logger               logger                  = Logger.getLogger(EldasoftSIMOGWSManager.class);

  //private static final String PROP_PROTEZIONE_ARCHIVI = "it.eldasoft.protezionearchivi";

  private GeneManager         geneManager;

  private SqlManager          sqlManager;

  protected GenChiaviManager  genChiaviManager;

  private W3Manager           w3Manager;
  
  private UffintManager 		uffintManager;

  public void setGeneManager(GeneManager geneManager) {
    this.geneManager = geneManager;
  }

  public void setSqlManager(SqlManager sqlManager) {
    this.sqlManager = sqlManager;
  }

  public void setGenChiaviManager(GenChiaviManager genChiaviManager) {
    this.genChiaviManager = genChiaviManager;
  }

  public void setW3Manager(W3Manager w3Manager) {
    this.w3Manager = w3Manager;
  }

  /**
   * @param uffintManager the uffintManager to set
   */
  public void setUffintManager(UffintManager uffintManager) {
    this.uffintManager = uffintManager;
  }
  
  /**
   * Inserimento dei dati della gara e dei lotti
   * 
   * @param login
   * @param password
   * @param datiGaraLottoXML
   * @throws XmlException
   * @throws GestoreException
   * @throws SQLException
   * @throws Exception
   */
  public List<String[]> inserisciGaraLotto(String login, String password, String datiGaraLottoXML) throws Exception {

    if (logger.isDebugEnabled()) logger.debug("EldasoftSIMOGWSManager.inserisciGaraLotto: inizio metodo");

    List<String[]> messaggiDML = new Vector<String[]>();

    try {

      // Ricavo l'identificativo account dell'utente che ha
      // inviato richiesta via WS
      Long idAccountRemoto = new Long(this.w3Manager.getIdAccount(login, password));

      // Documento principale ricavato da parse XML
      XmlOptions options = new XmlOptions();
      options.setCharacterEncoding("UTF-8");
      GaraDocument garaDocument = GaraDocument.Factory.parse(datiGaraLottoXML, options);
      garaDocument.documentProperties().setEncoding("UTF-8");
      // Controllo congruenza struttura via XMLBEANS
      ArrayList<Object> validationErrors = new ArrayList<Object>();
      XmlOptions validationOptions = new XmlOptions();
      validationOptions.setErrorListener(validationErrors);
      boolean isValid = garaDocument.validate(validationOptions);

      if (isValid) {
    	  String codein = null;
    	  //verifico se la stazione appaltante esiste
    	  List< ? > listaUffint = this.uffintManager.getUfficiIntestatariAccount(idAccountRemoto.intValue());
    	  if (listaUffint != null && listaUffint.size() > 0) {
    		  for (int i = 0; i < listaUffint.size(); i++) {
    	          UfficioIntestatario ufficioIntestatario = (UfficioIntestatario) listaUffint.get(i);
    	          //ricavo da uffint il CFANAC
    	          String cfanac = (String) sqlManager.getObject("select CFANAC from UFFINT where CODEIN = ?", new Object[] { ufficioIntestatario.getCodice() });
    	          
    	          if ((StringUtils.isNotEmpty(ufficioIntestatario.getCodFiscale())
    	                && (!garaDocument.getGara().isSetCFSTAZIONEAPPALTANTE() || ufficioIntestatario.getCodFiscale().equalsIgnoreCase(garaDocument.getGara().getCFSTAZIONEAPPALTANTE()))) ||
    	                (StringUtils.isNotEmpty(cfanac)
    	    	                && (!garaDocument.getGara().isSetCFSTAZIONEAPPALTANTE() || cfanac.equalsIgnoreCase(garaDocument.getGara().getCFSTAZIONEAPPALTANTE()))))
    	    	  {
    	        	  if (codein != null) {
    	        		  logger.error("Esistono piu' stazioni appaltanti con il codice fiscale indicato associate all'utente.");
    	        		  throw new Exception("Esistono piu' stazioni appaltanti con il codice fiscale indicato associate all'utente.");
    	        	  } else {
    	        		  codein = ufficioIntestatario.getCodice();
    	        	  }
    	          }
    	      }
    	  }
    	  if (codein != null) {
    		  // Inserimento della gara
    	      this.gestioneW3GARA(idAccountRemoto, garaDocument, messaggiDML, codein);
    	  } else {
    		  //genero errore
    		  logger.error("Non esiste alcuna stazione appaltante con il codice fiscale indicato associata all'utente.");
    		  throw new Exception("Non esiste alcuna stazione appaltante con il codice fiscale indicato associata all'utente.");
    	  }
      } else {
        String listaErroriValidazione = "";
        Iterator<?> iter = validationErrors.iterator();
        while (iter.hasNext()) {
          listaErroriValidazione += iter.next() + " ";
        }
        logger.error("I dati inviati per la gare ed i lotti non rispettano il formato previsto: "
            + datiGaraLottoXML
            + "\n"
            + listaErroriValidazione);
        throw new Exception(UtilityTags.getResource("errors.inserisciGaraLotto.validate", null, false));
      }

    } catch (XmlException e) {
      logger.error("Errore nel lettura dei dati inviati: " + datiGaraLottoXML);
      throw new Exception(UtilityTags.getResource("errors.inserisciGaraLotto.xmlexception", null, false));
    } catch (SQLException e) {
      logger.error("Errore di database nella gestione dell'inserimento dei dati della gara e dei lotti: " + datiGaraLottoXML);
      throw new Exception(UtilityTags.getResource("errors.database.dataAccessException", null, false));
    }

    if (logger.isDebugEnabled()) logger.debug("EldasoftSIMOGWSManager.inserisciGaraLotto: fine metodo");

    return messaggiDML;

  }

  /**
   * Consultazione IDGARA sulla base dell'identificativo UUID
   * 
   * @param uuid
   * @return
   * @throws Exception
   */
  public String consultaIDGARA(String uuid) throws Exception {

    if (logger.isDebugEnabled()) logger.debug("EldasoftSIMOGWSManager.consultaIDGARA: inizio metodo");

    String idgara = null;

    try {

      // Verifico se esiste una gara con codice UUID
      Long conteggio = (Long) sqlManager.getObject("select count(*) from w3gara where gara_uuid = ?", new Object[] { uuid });

      if (conteggio != null && conteggio.longValue() > 0) {
        // Se esiste una gara procedo alla lettura dell'identificativo IDGARA
        idgara = (String) sqlManager.getObject("select id_gara from w3gara where gara_uuid = ?", new Object[] { uuid });
        if (idgara == null) {
          // Messaggio IDGARA non presente
          logger.error("Per la gara indicata (UUID: " + uuid + ") non e' ancora stato assegnato l'identificativo IDGARA");
          throw new Exception(UtilityTags.getResource("errors.consultaIDGARA.IDGARAnonAssegnato", null, false));
        }
      } else {
        // Messaggio (UUID) gara non presente
        logger.error("La gara indicata (UUID: " + uuid + ") non e' presente in base dati");
        throw new Exception(UtilityTags.getResource("errors.consultaIDGARA.UUIDnonPresente", null, false));
      }
    } catch (SQLException e) {
      logger.error("Errore di database nella gestione della richiesta di consultazione IDGARA", e);
      throw new Exception(UtilityTags.getResource(" errors.database.dataAccessException", null, false));
    }

    if (logger.isDebugEnabled()) logger.debug("EldasoftSIMOGWSManager.consultaIDGARA: fine metodo");

    return idgara;

  }

  /**
   * Consultazione CIG sulla base dell'identificativo UUID
   * 
   * @param uuid
   * @return
   * @throws Exception
   */
  public String consultaCIG(String uuid) throws Exception {

    if (logger.isDebugEnabled()) logger.debug("EldasoftSIMOGWSManager.consultaCIG: inizio metodo");

    String cig = null;

    try {

      // Verifico se esiste una gara con codice UUID
      Long conteggio = (Long) sqlManager.getObject("select count(*) from w3lott where lotto_uuid = ?", new Object[] { uuid });
      Long conteggioSmartCig = (Long) sqlManager.getObject("select count(*) from w3smartcig where gara_uuid = ?", new Object[] { uuid });

      if (conteggio != null && conteggio.longValue() > 0) {
        // Se esiste un lotto procedo alla lettura dell'identificativo CIG
        cig = (String) sqlManager.getObject("select cig from w3lott where lotto_uuid = ?", new Object[] { uuid });
        if (cig == null) {
          // Messaggio CIG non presente
          logger.error("Per il lotto indicato (UUID: " + uuid + ") non e' ancora stato assegnato l'identificativo CIG");
          throw new Exception(UtilityTags.getResource("errors.consultaCIG.CIGnonAssegnato", null, false));
        }
      } else if (conteggioSmartCig != null && conteggioSmartCig.longValue() > 0) {
          // Se esiste uno smartcig procedo alla lettura dell'identificativo CIG
          cig = (String) sqlManager.getObject("select cig from w3smartcig where gara_uuid = ?", new Object[] { uuid });
          if (cig == null) {
            // Messaggio CIG non presente
            logger.error("Per lo smartcig indicato (UUID: " + uuid + ") non e' ancora stato assegnato l'identificativo CIG");
            throw new Exception(UtilityTags.getResource("errors.consultaCIG.CIGnonAssegnato", null, false));
          }
      } else {
        // Messaggio (UUID) lotto non presente
        logger.error("Il lotto o smartcig indicato (UUID: " + uuid + ") non e' presente in base dati");
        throw new Exception(UtilityTags.getResource("errors.consultaCIG.UUIDnonPresente", null, false));
      }
    } catch (SQLException e) {
      logger.error("Errore di database nella gestione della richiesta di consultazione CIG", e);
      throw new Exception(UtilityTags.getResource(" errors.database.dataAccessException", null, false));
    }

    if (logger.isDebugEnabled()) logger.debug("EldasoftSIMOGWSManager.consultaCIG: fine metodo");

    return cig;

  }

  /**
   * Verifica esistenza CIG. Restituisce anche altri dati inerenti la gara cui
   * appartiene il lotto identificato dal CIG richiesto.
   * 
   * @param cig
   * @return
   * @throws Exception
   */
  public it.eldasoft.simog.ws.EsitoVerificaCIG verificaCIG(String cig) throws Exception {

    if (logger.isDebugEnabled()) logger.debug("EldasoftSIMOGWSManager.verificaCIG: inizio metodo");

    EsitoVerificaCIG esitoVerificaCIG = new EsitoVerificaCIG();

    try {
      // Verifica esistenza CIG
      Long conteggio = (Long) sqlManager.getObject("select count(*) from w3lott where cig = ?", new Object[] { cig });
      Long conteggioSmartCig = (Long) sqlManager.getObject("select count(*) from w3smartcig where cig = ?", new Object[] { cig });
      if (conteggio != null && conteggio.longValue() > 0) {
        esitoVerificaCIG.setEsito(true);

        String selectW3GARA = "select w3gara.oggetto, "
            + "w3gara.id_gara, "
            + "w3gara.importo_gara, "
            + "w3gara.rup_codtec, "
            + "w3gara.numgara, "
            + "w3lott.importo_lotto "
            + "from w3gara, w3lott "
            + "where w3gara.numgara = w3lott.numgara "
            + "and w3lott.cig = ?";

        List<?> datiW3GARA = this.sqlManager.getVector(selectW3GARA, new Object[] { cig });
        if (datiW3GARA != null && datiW3GARA.size() > 0) {
          String oggettogara = (String) SqlManager.getValueFromVectorParam(datiW3GARA, 0).getValue();
          String numerogara = (String) SqlManager.getValueFromVectorParam(datiW3GARA, 1).getValue();
          Double importogara = (Double) SqlManager.getValueFromVectorParam(datiW3GARA, 2).getValue();
          Long numgara = (Long) SqlManager.getValueFromVectorParam(datiW3GARA, 4).getValue();
          //Double importolotto = (Double) SqlManager.getValueFromVectorParam(datiW3GARA, 5).getValue();
          
          esitoVerificaCIG.setOggettogara(oggettogara);
          esitoVerificaCIG.setNumerogara(numerogara);
          /*if (importolotto != null) {
        	  esitoVerificaCIG.setImportogara(new BigDecimal(importolotto.toString()));
          }*/
          if (importogara != null && importogara>0) {
        	  esitoVerificaCIG.setImportogara(new BigDecimal(importogara.toString()));
          } else {
        	  
        	  Object importoObj = sqlManager.getObject("select SUM(" + sqlManager.getDBFunction("isnull", new String[] {"IMPORTO_LOTTO", "0.00" }) + ") from W3LOTT where NUMGARA=? and STATO_SIMOG in (1,2,3,4,7,99)", 
      	            new Object[] { numgara });
        	  Double importo = new Double(0);
  	          if (importoObj != null) {
  	              if (!(importoObj instanceof Double)) {
  	                importo = new Double(importoObj.toString());
  	              } else {
  	                importo = (Double) importoObj;
  	              }
  	          } 
  	          if (importoObj != null) {
  	        	  esitoVerificaCIG.setImportogara(new BigDecimal(importo.toString()));
  	          } else {
  	        	  esitoVerificaCIG.setImportogara(new BigDecimal("-1"));
  	          }
          }

          String rup_codtec = (String) SqlManager.getValueFromVectorParam(datiW3GARA, 3).getValue();

          if (rup_codtec != null) {
            String selectTECNI = "select nomtec, cftec from tecni where codtec = ?";
            List<?> datiTECNI = this.sqlManager.getVector(selectTECNI, new Object[] { rup_codtec });
            if (datiTECNI != null && datiTECNI.size() > 0) {
              String denominazionerup = (String) SqlManager.getValueFromVectorParam(datiTECNI, 0).getValue();
              String codicefiscalerup = (String) SqlManager.getValueFromVectorParam(datiTECNI, 1).getValue();

              esitoVerificaCIG.setDenominazionerup(denominazionerup);
              esitoVerificaCIG.setCodicefiscalerup(codicefiscalerup);

            }
          }
        }
      } else if (conteggioSmartCig != null && conteggioSmartCig.longValue() > 0) {
          esitoVerificaCIG.setEsito(true);

          String selectW3SMARTCIG = "select oggetto, "
              + "cig, importo, rup "
              + "from w3smartcig where cig = ?";

          List<?> datiW3SMARTCIG = this.sqlManager.getVector(selectW3SMARTCIG, new Object[] { cig });
          if (datiW3SMARTCIG != null && datiW3SMARTCIG.size() > 0) {
            String oggettogara = (String) SqlManager.getValueFromVectorParam(datiW3SMARTCIG, 0).getValue();
            String numerogara = (String) SqlManager.getValueFromVectorParam(datiW3SMARTCIG, 1).getValue();
            Double importogara = (Double) SqlManager.getValueFromVectorParam(datiW3SMARTCIG, 2).getValue();

            esitoVerificaCIG.setOggettogara(oggettogara);
            esitoVerificaCIG.setNumerogara(numerogara);

            if (importogara != null) {
              esitoVerificaCIG.setImportogara(new BigDecimal(importogara.toString()));
            }

            String rup_codtec = (String) SqlManager.getValueFromVectorParam(datiW3SMARTCIG, 3).getValue();

            if (rup_codtec != null) {
              String selectTECNI = "select nomtec, cftec from tecni where codtec = ?";
              List<?> datiTECNI = this.sqlManager.getVector(selectTECNI, new Object[] { rup_codtec });
              if (datiTECNI != null && datiTECNI.size() > 0) {
                String denominazionerup = (String) SqlManager.getValueFromVectorParam(datiTECNI, 0).getValue();
                String codicefiscalerup = (String) SqlManager.getValueFromVectorParam(datiTECNI, 1).getValue();

                esitoVerificaCIG.setDenominazionerup(denominazionerup);
                esitoVerificaCIG.setCodicefiscalerup(codicefiscalerup);

              }
            }
          }
        } 
      else {
        esitoVerificaCIG.setEsito(false);
      }

    } catch (SQLException e) {
      logger.error("Errore di database nella gestione della richiesta di verifica CIG", e);
      throw new Exception(UtilityTags.getResource(" errors.database.dataAccessException", null, false));
    }
    if (logger.isDebugEnabled()) logger.debug("EldasoftSIMOGWSManager.verificaCIG: fine metodo");

    return esitoVerificaCIG;

  }

  /**
   * Inserimento dei dati dello smartCIG
   * 
   * @param login
   * @param password
   * @param datiSmartCIGXML
   * @throws XmlException
   * @throws GestoreException
   * @throws SQLException
   * @throws Exception
   */
  public List<String[]> inserisciSmartCIG(String login, String password, String datiSmartCIGXML) throws Exception {

    if (logger.isDebugEnabled()) logger.debug("EldasoftSIMOGWSManager.inserisciSmartCIG: inizio metodo");

    List<String[]> messaggiDML = new Vector<String[]>();

    try {

      // Ricavo l'identificativo account dell'utente che ha
      // inviato richiesta via WS
      Long idAccountRemoto = new Long(this.w3Manager.getIdAccount(login, password));

      // Documento principale ricavato da parse XML
      SmartCIGDocument smartCIGDocument = SmartCIGDocument.Factory.parse(datiSmartCIGXML);

      // Controllo congruenza struttura via XMLBEANS
      ArrayList<Object> validationErrors = new ArrayList<Object>();
      XmlOptions validationOptions = new XmlOptions();
      validationOptions.setErrorListener(validationErrors);
      boolean isValid = smartCIGDocument.validate(validationOptions);

      if (isValid) {
    	  String codein = null;
    	  //verifico se la stazione appaltante esiste
    	  List< ? > listaUffint = this.uffintManager.getUfficiIntestatariAccount(idAccountRemoto.intValue());
    	  if (listaUffint != null && listaUffint.size() > 0) {
    		  for (int i = 0; i < listaUffint.size(); i++) {
    	          UfficioIntestatario ufficioIntestatario = (UfficioIntestatario) listaUffint.get(i);
    	          //ricavo da uffint il CFANAC
    	          String cfanac = (String) sqlManager.getObject("select CFANAC from UFFINT where CODEIN = ?", new Object[] { ufficioIntestatario.getCodice() });
    	          
    	          if ((StringUtils.isNotEmpty(ufficioIntestatario.getCodFiscale())
    	                && (!smartCIGDocument.getSmartCIG().isSetCFSTAZIONEAPPALTANTE() || ufficioIntestatario.getCodFiscale().equalsIgnoreCase(smartCIGDocument.getSmartCIG().getCFSTAZIONEAPPALTANTE()))) ||
    	                (StringUtils.isNotEmpty(cfanac)
    	    	                && (!smartCIGDocument.getSmartCIG().isSetCFSTAZIONEAPPALTANTE() || cfanac.equalsIgnoreCase(smartCIGDocument.getSmartCIG().getCFSTAZIONEAPPALTANTE())))) {
    	        	  if (codein != null) {
    	        		  logger.error("Esistono piu' stazioni appaltanti con il codice fiscale indicato associate all'utente.");
    	        		  throw new Exception("Esistono piu' stazioni appaltanti con il codice fiscale indicato associate all'utente.");
    	        	  } else {
    	        		  codein = ufficioIntestatario.getCodice();
    	        	  }
    	          }
    	      }
    	  }
    	  if (codein != null) {
    		  // Inserimento smart CIG
    	      this.gestioneW3SMARTCIG(idAccountRemoto, smartCIGDocument, messaggiDML, codein);
    	  } else {
    		  //genero errore
    		  logger.error("Non esiste alcuna stazione appaltante con il codice fiscale indicato associata all'utente.");
    		  throw new Exception("Non esiste alcuna stazione appaltante con il codice fiscale indicato associata all'utente.");
    	  }
        
      } else {
        String listaErroriValidazione = "";
        Iterator<?> iter = validationErrors.iterator();
        while (iter.hasNext()) {
          listaErroriValidazione += iter.next() + " ";
        }
        logger.error("I dati inviati per lo smartCIG non rispettano il formato previsto: "
            + datiSmartCIGXML
            + "\n"
            + listaErroriValidazione);
        throw new Exception(UtilityTags.getResource("errors.inserisciSmartCIG.validate", null, false));
      }

    } catch (XmlException e) {
      logger.error("Errore nel lettura dei dati inviati: " + datiSmartCIGXML);
      throw new Exception(UtilityTags.getResource("errors.inserisciGaraLotto.xmlexception", null, false));
    } catch (SQLException e) {
      logger.error("Errore di database nella gestione dell'inserimento dei dati dello smartCIG: " + datiSmartCIGXML);
      throw new Exception(UtilityTags.getResource("errors.database.dataAccessException", null, false));
    }

    if (logger.isDebugEnabled()) logger.debug("EldasoftSIMOGWSManager.inserisciSmartCIG: fine metodo");

    return messaggiDML;

  }
  
  /**
   * Inserimento/aggiornamento della gara in W3GARA.
   * 
   * @param idAccountRemoto
   * @param garaDocument
   * @param codein
   * @throws GestoreException
   * @throws SQLException
   * @throws Exception
   */
  private void gestioneW3GARA(Long idAccountRemoto, GaraDocument garaDocument, List<String[]> messaggiDML, String codein) throws GestoreException {

    if (logger.isDebugEnabled()) logger.debug("EldasoftSIMOGWSManager.gestioneW3GARA: inizio metodo");

    try {

      GaraType datiGara = garaDocument.getGara();

      // Controllo di esistenza della gara sulla base del
      // codice univoco universale UUID
      boolean esisteW3GARA = this.w3Manager.esisteW3GARA_UUID(datiGara.getUUID());

      Long numgara = null;

      if (!esisteW3GARA) {
        // LA GARA NON ESISTE: SI PROCEDE A CREARNE UNA NUOVA

        // Calcolo della chiave
        numgara = this.getNextValNUMGARA();

        DataColumnContainer dccW3GARA = new DataColumnContainer(new DataColumn[] { new DataColumn("W3GARA.NUMGARA", new JdbcParametro(
            JdbcParametro.TIPO_NUMERICO, numgara)) });

        // Utente proprietario
        dccW3GARA.addColumn("W3GARA.SYSCON", JdbcParametro.TIPO_NUMERICO, idAccountRemoto);

        // Identificativo Univoco Universale
        dccW3GARA.addColumn("W3GARA.GARA_UUID", datiGara.getUUID());

        // Stato della gara: nuova gara in attesa di richiesta, a SIMOG,
        // del numero gara IDGARA
        dccW3GARA.addColumn("W3GARA.STATO_SIMOG", JdbcParametro.TIPO_NUMERICO, new Long(1));

        // Aggiungo la lista delle colonne, per poterle utilizzare
        // nel metodo successivo
        dccW3GARA.addColumn("W3GARA.OGGETTO", JdbcParametro.TIPO_TESTO, null);
        dccW3GARA.addColumn("W3GARA.TIPO_SCHEDA", JdbcParametro.TIPO_TESTO, null);
        dccW3GARA.addColumn("W3GARA.MODO_INDIZIONE", JdbcParametro.TIPO_NUMERICO, null);
        dccW3GARA.addColumn("W3GARA.MODO_REALIZZAZIONE", JdbcParametro.TIPO_NUMERICO, null);
        dccW3GARA.addColumn("W3GARA.IMPORTO_GARA", JdbcParametro.TIPO_DECIMALE, null);
        dccW3GARA.addColumn("W3GARA.RUP_CODTEC", JdbcParametro.TIPO_TESTO, null);
        dccW3GARA.addColumn("W3GARA.COLLABORAZIONE", JdbcParametro.TIPO_DECIMALE, null);
        dccW3GARA.addColumn("W3GARA.CIG_ACC_QUADRO", JdbcParametro.TIPO_TESTO, null);
        dccW3GARA.addColumn("W3GARA.CODEIN", JdbcParametro.TIPO_TESTO, codein);
        
        dccW3GARA.addColumn("W3GARA.M_RICH_CIG", JdbcParametro.TIPO_NUMERICO, null);
        dccW3GARA.addColumn("W3GARA.ALLEGATO_IX", JdbcParametro.TIPO_NUMERICO, null);
        dccW3GARA.addColumn("W3GARA.STRUMENTO_SVOLGIMENTO", JdbcParametro.TIPO_NUMERICO, null);
        dccW3GARA.addColumn("W3GARA.URGENZA_DL133", JdbcParametro.TIPO_TESTO, null);
        dccW3GARA.addColumn("W3GARA.ESTREMA_URGENZA", JdbcParametro.TIPO_NUMERICO, null);
        
        dccW3GARA.addColumn("W3GARA.ESCLUSO_AVCPASS", JdbcParametro.TIPO_TESTO, null);
        dccW3GARA.addColumn("W3GARA.DURATA_ACCQUADRO", JdbcParametro.TIPO_NUMERICO, null);
        dccW3GARA.addColumn("W3GARA.VER_SIMOG", JdbcParametro.TIPO_NUMERICO, new Long(4));
        // Setto nel DataColumnContainer anche gli altri dati
        this.dccW3GARA_AltriDatiGara(dccW3GARA, datiGara, numgara, idAccountRemoto, codein);

        dccW3GARA.insert("W3GARA", this.sqlManager);

        messaggiDML.add(new String[] { "GARA", "INS", datiGara.getUUID() });

      } else {
        // LA GARA ESISTE GIA': SI PROCEDE AD AGGIORNARLA PREVIO
        // SUPERAMENTO DI ALCUNI CONTROLLI

        // Ricavo la chiave
        numgara = this.w3Manager.getNUMGARA_UUID(datiGara.getUUID());

        // Controllo n. 1: i nuovi dati devono appartenere all'utente
        // che originariamente li ha inviati
        Long syscon_originale = (Long) sqlManager.getObject("select syscon from w3gara where numgara = ?", new Object[] { numgara });
        if (syscon_originale.longValue() != idAccountRemoto) {
          logger.error("La gara (NUMGARA: "
              + numgara.toString()
              + ") che si sta tentando di aggiornare e' di proprieta' di un altro utente: non e' possibile procedere nell'aggiornamento");
          throw new GestoreException(
              "La gara che si sta tentando di aggiornare e' di proprieta' di un altro utente: non e' possibile procedere nell'aggiornamento",
              "errors.inserisciGaraLotto.syscon.differente", null);
        }

        // Controllo n. 2: i nuovi dati devono essere in carico
        // allo stesso RUP che li aveva in carico originariamente
        String rup_originale = (String) sqlManager.getObject(
            "select tecni.cftec from tecni, w3gara where w3gara.rup_codtec = tecni.codtec and w3gara.numgara = ?", new Object[] { numgara });
        if (rup_originale != null) {
          if (!rup_originale.equals(datiGara.getRUP().getCFTEC())) {
            logger.error("La gara (NUMGARA: "
                + numgara.toString()
                + ")che si sta tentando di aggiornare e' in carico ad un RUP differente: non e' possibile procedere nell'aggiornamento");
            throw new GestoreException(
                "La gara che si sta tentando di aggiornare e' in carico ad un RUP differente: non e' possibile procedere nell'aggiornamento",
                "errors.inserisciGaraLotto.rup.differente", null);
          }
        }

        // Controllo n. 3: la gara deve essere in uno stato modificabile
        if (!this.w3Manager.isW3GARA_UUIDModificabile(datiGara.getUUID())) {
          logger.error("La gara (NUMGARA: "
              + numgara.toString()
              + ") che si sta tentando di aggiornare e' in uno stato non modificabile: non e' possibile procedere nell'aggiornamento");
          throw new GestoreException(
              "La gara che si sta tentando di aggiornare e' in uno stato non modificabile: non e' possibile procedere nell'aggiornamento",
              "errors.inserisciGaraLotto.nonmodificabile", null);
        }

        DataColumnContainer dccW3GARA = new DataColumnContainer(this.sqlManager, "W3GARA", "select * from w3gara where numgara = ?",
            new Object[] { numgara });

        // Aggiungo al DataColumnContainer anche gli altri dati comuni
        this.dccW3GARA_AltriDatiGara(dccW3GARA, datiGara, numgara, idAccountRemoto, codein);

        if (dccW3GARA.isModifiedTable("W3GARA")) {

          Long stato_simog = dccW3GARA.getLong("W3GARA.STATO_SIMOG");
          if (!new Long(1).equals(stato_simog)) {
            dccW3GARA.setValue("W3GARA.STATO_SIMOG", new Long(3));
          }

          dccW3GARA.getColumn("W3GARA.NUMGARA").setChiave(true);
          dccW3GARA.setValue("W3GARA.NUMGARA", numgara);
          dccW3GARA.setOriginalValue("W3GARA.NUMGARA", new JdbcParametro(JdbcParametro.TIPO_NUMERICO, numgara));
          dccW3GARA.update("W3GARA", this.sqlManager);

          messaggiDML.add(new String[] { "GARA", "UPD", datiGara.getUUID() });

        }

      }
      this.gestioneRequisiti(numgara);
      // Gestione delle richieste di cancellazione dei lotti gia' inseriti
      // ma non piu' presenti nell'invio remoto
      this.gestioneRichiesteCancellazioneW3LOTT(datiGara, numgara, messaggiDML);

      // Gestione dell'inserimento/aggiornamento dei lotti associata alla gara
      this.gestioneW3LOTT(datiGara, numgara, messaggiDML);

    } catch (SQLException e) {
      throw new GestoreException("Errore di database nella gestione dell'inserimento della gare e dei lotti",
          "errors.inserisciGaraLotto.sqlerror", e);
    }

    if (logger.isDebugEnabled()) logger.debug("EldasoftSIMOGWSManager.gestioneW3GARA: fine metodo");

  }

  /**
   * Gestione dei requisiti per la gara
   *
   * @param numgara
   * @throws GestoreException
   */
  private void gestioneRequisiti(Long numgara) throws GestoreException {

    if (logger.isDebugEnabled())
      logger.debug("gestioneRequisiti: inizio metodo");

    try {
    	// Ricavo l'importo totale dei lotti con stato 2 
    	Double importo = new Double(0);

    	//Controllo AVCPASS
    	String avcpass = (String)sqlManager.getObject("select ESCLUSO_AVCPASS from W3GARA where NUMGARA = ?", new Object[] { numgara });
    	if (avcpass != null && !avcpass.equals("1")) {
    		//Verifico se i requisiti sono già stati creati
        	Long numeroRequisiti = (Long)sqlManager.getObject(
    	              "select COUNT(*) from W3GARAREQ where NUMGARA = ?",
    	              new Object[] { numgara });
        	if (numeroRequisiti.equals(new Long(0))) {
        		//se i requisiti non sono stati ancora inseriti allora li inserisco
        		List<?> datiW3TABREQ = this.sqlManager.getListVector(
        		          "select CODICE_DETTAGLIO, DESCRIZIONE, DOCUMENTI_DEFAULT from W3TABREQ where INIZIALE = '1'",
        		          new Object[] { });
        		if (datiW3TABREQ != null && datiW3TABREQ.size() > 0) {
        		    for (int i = 0; i < datiW3TABREQ.size(); i++) {
        		          String codice = (String) SqlManager.getValueFromVectorParam(
        		        		  datiW3TABREQ.get(i), 0).getValue();
        		          String descrizione = (String) SqlManager.getValueFromVectorParam(
        		        		  datiW3TABREQ.get(i), 1).getValue();
        		          String documenti = (String) SqlManager.getValueFromVectorParam(
        		        		  datiW3TABREQ.get(i), 2).getValue();
        		          int indexDescrizione = descrizione.indexOf("-");
        		          if (indexDescrizione != -1) {
        		        	  descrizione = descrizione.substring(indexDescrizione + 2);
        		          }
        		          if (descrizione.length() > 80) {
        		        	  descrizione = descrizione.substring(0,80);
        		          }
        		          Long numreq = new Long(i+1);
        		          this.sqlManager.update("insert into W3GARAREQ(NUMGARA, NUMREQ, CODICE_DETTAGLIO, DESCRIZIONE, FLAG_ESCLUSIONE, FLAG_COMPROVA_OFFERTA, FLAG_AVVALIMENTO, FLAG_BANDO_TIPO, FLAG_RISERVATEZZA) VALUES(?,?,?,?,?,?,?,?,?)", new Object[] {
        		        		  numgara, numreq, codice, descrizione, "2", "2", "2", "2", "2" });
        		          if (documenti != null && !documenti.trim().equals("")) {
        		        	  String[] listaDocumenti = documenti.split("-");
        		        	  int j = 1;
        		        	  for(String tipoDocumento:listaDocumenti) {
        		        		  String descrizioneDoc = (String)sqlManager.getObject(
        		        	              "select TAB1DESC from TAB1 where TAB1COD = 'W3029' and TAB1TIP = ?",
        		        	              new Object[] { new Long(tipoDocumento) });
        		        		  if (descrizioneDoc != null) {
        		        			  this.sqlManager.update("insert into W3GARAREQDOC(NUMGARA, NUMREQ, NUMDOC, CODICE_TIPO_DOC, DESCRIZIONE, EMETTITORE, FAX, TELEFONO, MAIL, MAIL_PEC) VALUES(?,?,?,?,?,?,?,?,?,?)", new Object[] {
                    		        		  numgara, numreq, new Long(j), new Long(tipoDocumento), descrizioneDoc, "-", "0", "0", "-", "-" });
            		        		  j++;
        		        		  }
        		        	  }
        		          }
        		     }
        		}
        	}
    	} else {
    		//se importo è inferiore a 40000 elimino i requisiti
    		this.sqlManager.update("delete from W3GARAREQDOC where NUMGARA = ?", new Object[] {numgara});
    		this.sqlManager.update("delete from W3GARAREQ where NUMGARA = ?", new Object[] {numgara});
    	}
    } catch (SQLException e) {
      throw new GestoreException(
          "Si e' verificato un errore durante l'interazione con la base dati",
          "gestioneIDGARACIG.sqlerror", e);
    } 

    if (logger.isDebugEnabled())
      logger.debug("gestioneRequisiti: fine metodo");

  }
  
  /**
   * Inserimento/aggiornamento dello smartcig in W3SMARTCIG.
   * 
   * @param idAccountRemoto
   * @param smartCIGDocument
   * @param codein
   * @throws GestoreException
   * @throws SQLException
   * @throws Exception
   */
  private void gestioneW3SMARTCIG(Long idAccountRemoto, SmartCIGDocument smartCIGDocument, List<String[]> messaggiDML, String codein) throws GestoreException {

    if (logger.isDebugEnabled()) logger.debug("EldasoftSIMOGWSManager.gestioneW3SMARTCIG: inizio metodo");

    try {

      SmartCIGType datiSmartCig = smartCIGDocument.getSmartCIG();

      // Controllo di esistenza dello smartcig sulla base del
      // codice univoco universale UUID
      boolean esisteW3SMARTCIG = this.w3Manager.esisteW3SMARTCIG_UUID(datiSmartCig.getUUID());

      Long numgara = null;

      if (!esisteW3SMARTCIG) {
        // LO SMARTCIG NON ESISTE: SI PROCEDE A CREARNE UNO NUOVO

        // Calcolo della chiave
        numgara = this.getNextValCODRICH();

        DataColumnContainer dccW3SMARTCIG = new DataColumnContainer(new DataColumn[] { new DataColumn("W3SMARTCIG.CODRICH", new JdbcParametro(
            JdbcParametro.TIPO_NUMERICO, numgara)) });

        // Utente proprietario
        dccW3SMARTCIG.addColumn("W3SMARTCIG.SYSCON", JdbcParametro.TIPO_NUMERICO, idAccountRemoto);

        // Identificativo Univoco Universale
        dccW3SMARTCIG.addColumn("W3SMARTCIG.GARA_UUID", datiSmartCig.getUUID());

        // Stato della gara: nuova gara in attesa di richiesta, a SIMOG,
        // del numero SMARTCIG
        dccW3SMARTCIG.addColumn("W3SMARTCIG.STATO", JdbcParametro.TIPO_NUMERICO, new Long(1));

        // Aggiungo la lista delle colonne, per poterle utilizzare
        // nel metodo successivo
        dccW3SMARTCIG.addColumn("W3SMARTCIG.OGGETTO", JdbcParametro.TIPO_TESTO, null);
        dccW3SMARTCIG.addColumn("W3SMARTCIG.FATTISPECIE", JdbcParametro.TIPO_TESTO, null);
        dccW3SMARTCIG.addColumn("W3SMARTCIG.IMPORTO", JdbcParametro.TIPO_DECIMALE, null);
        dccW3SMARTCIG.addColumn("W3SMARTCIG.TIPO_CONTRATTO", JdbcParametro.TIPO_TESTO, null);
        dccW3SMARTCIG.addColumn("W3SMARTCIG.CIG_ACC_QUADRO", JdbcParametro.TIPO_TESTO, null);
        dccW3SMARTCIG.addColumn("W3SMARTCIG.CUP", JdbcParametro.TIPO_TESTO, null);
        dccW3SMARTCIG.addColumn("W3SMARTCIG.M_RICH_CIG_COMUNI", JdbcParametro.TIPO_TESTO, null);
        dccW3SMARTCIG.addColumn("W3SMARTCIG.M_RICH_CIG", JdbcParametro.TIPO_TESTO, null);
        dccW3SMARTCIG.addColumn("W3SMARTCIG.RUP", JdbcParametro.TIPO_TESTO, null);
        dccW3SMARTCIG.addColumn("W3SMARTCIG.CODEIN", JdbcParametro.TIPO_TESTO, codein);
        dccW3SMARTCIG.addColumn("W3SMARTCIG.ID_SCELTA_CONTRAENTE", JdbcParametro.TIPO_TESTO, null);
        dccW3SMARTCIG.addColumn("W3SMARTCIG.COLLABORAZIONE", JdbcParametro.TIPO_DECIMALE, null);
        // Setto nel DataColumnContainer anche gli altri dati
        this.dccW3SMARTCIG_AltriDati(dccW3SMARTCIG, datiSmartCig, numgara, idAccountRemoto, codein);

        dccW3SMARTCIG.insert("W3SMARTCIG", this.sqlManager);

        messaggiDML.add(new String[] { "SMARTCIG", "INS", datiSmartCig.getUUID() });

      } else {
        // LO SMARTCIG ESISTE GIA': SI PROCEDE AD AGGIORNARLA PREVIO
        // SUPERAMENTO DI ALCUNI CONTROLLI

        // Ricavo la chiave
        numgara = this.w3Manager.getCODRICH_UUID(datiSmartCig.getUUID());

        // Controllo n. 1: i nuovi dati devono appartenere all'utente
        // che originariamente li ha inviati
        Long syscon_originale = (Long) sqlManager.getObject("select syscon from w3smartcig where codrich = ?", new Object[] { numgara });
        if (syscon_originale.longValue() != idAccountRemoto) {
          logger.error("Lo smartcig (CODRICH: "
              + numgara.toString()
              + ") che si sta tentando di aggiornare e' di proprieta' di un altro utente: non e' possibile procedere nell'aggiornamento");
          throw new GestoreException(
              "Lo smartcig che si sta tentando di aggiornare e' di proprieta' di un altro utente: non e' possibile procedere nell'aggiornamento",
              "errors.inserisciSmartCIG.syscon.differente", null);
        }

        // Controllo n. 2: i nuovi dati devono essere in carico
        // allo stesso RUP che li aveva in carico originariamente
        String rup_originale = (String) sqlManager.getObject(
            "select tecni.cftec from tecni, w3smartcig where w3smartcig.rup = tecni.codtec and w3smartcig.codrich = ?", new Object[] { numgara });
        if (rup_originale != null) {
          if (!rup_originale.equals(datiSmartCig.getRUP().getCFTEC())) {
            logger.error("Lo smartcig (CODRICH: "
                + numgara.toString()
                + ")che si sta tentando di aggiornare e' in carico ad un RUP differente: non e' possibile procedere nell'aggiornamento");
            throw new GestoreException(
                "Lo smartcig che si sta tentando di aggiornare e' in carico ad un RUP differente: non e' possibile procedere nell'aggiornamento",
                "errors.inserisciSmartCIG.rup.differente", null);
          }
        }

        // Controllo n. 3: la gara deve essere in uno stato modificabile
        if (!this.w3Manager.isW3SMARTCIG_UUIDModificabile(datiSmartCig.getUUID())) {
          logger.error("Lo smartcig (CODRICH: "
              + numgara.toString()
              + ") che si sta tentando di aggiornare e' in uno stato non modificabile: non e' possibile procedere nell'aggiornamento");
          throw new GestoreException(
              "Lo smartcig che si sta tentando di aggiornare e' in uno stato non modificabile: non e' possibile procedere nell'aggiornamento",
              "errors.inserisciSmartCIG.nonmodificabile", null);
        }

        DataColumnContainer dccW3SMARTCIG = new DataColumnContainer(this.sqlManager, "W3SMARTCIG", "select * from w3smartcig where codrich = ?",
            new Object[] { numgara });

        // Aggiungo al DataColumnContainer anche gli altri dati comuni
        this.dccW3SMARTCIG_AltriDati(dccW3SMARTCIG, datiSmartCig, numgara, idAccountRemoto, codein);

        if (dccW3SMARTCIG.isModifiedTable("W3SMARTCIG")) {

          Long stato_simog = dccW3SMARTCIG.getLong("W3SMARTCIG.STATO");
          if (!new Long(1).equals(stato_simog)) {
        	  dccW3SMARTCIG.setValue("W3SMARTCIG.STATO", new Long(3));
          }

          dccW3SMARTCIG.getColumn("W3SMARTCIG.CODRICH").setChiave(true);
          dccW3SMARTCIG.setValue("W3SMARTCIG.CODRICH", numgara);
          dccW3SMARTCIG.setOriginalValue("W3SMARTCIG.CODRICH", new JdbcParametro(JdbcParametro.TIPO_NUMERICO, numgara));
          dccW3SMARTCIG.update("W3SMARTCIG", this.sqlManager);

          messaggiDML.add(new String[] { "SMARTCIG", "UPD", datiSmartCig.getUUID() });
        }
      }
      
    } catch (SQLException e) {
      throw new GestoreException("Errore di database nella gestione dell'inserimento della gare e dei lotti",
          "errors.inserisciSmartCIG.sqlerror", e);
    }

    if (logger.isDebugEnabled()) logger.debug("EldasoftSIMOGWSManager.gestioneW3SMARTCIG: fine metodo");

  }
  
  /**
   * Gestione delle richieste di cancellazione dei lotti presenti in base dati
   * ma non presenti nell'invio remoto
   * 
   * @param datiGara
   * @param numgara
   * @throws SQLException
   */
  private void gestioneRichiesteCancellazioneW3LOTT(GaraType datiGara, Long numgara, List<String[]> messaggiDML) throws SQLException {

    if (logger.isDebugEnabled()) logger.debug("EldasoftSIMOGWSManager.gestioneRichiesteCancellazioneW3LOTT: inizio metodo");

    String selectW3LOTT = "select numlott, lotto_uuid, stato_simog from w3lott where numgara = ? and stato_simog in (1,2,3,4)";
    List<?> datiW3LOTT = this.sqlManager.getListVector(selectW3LOTT, new Object[] { numgara });

    LottoType[] datiListaLotti = datiGara.getLOTTIArray();

    if (datiW3LOTT != null && datiW3LOTT.size() > 0 && datiListaLotti != null && datiListaLotti.length > 0) {

      for (int i = 0; i < datiW3LOTT.size(); i++) {

        boolean richiestaDiCancellazione = true;

        Long numlott = (Long) SqlManager.getValueFromVectorParam(datiW3LOTT.get(i), 0).getValue();
        String lotto_uuid = (String) SqlManager.getValueFromVectorParam(datiW3LOTT.get(i), 1).getValue();
        Long stato_simog = (Long) SqlManager.getValueFromVectorParam(datiW3LOTT.get(i), 2).getValue();

        for (int j = 0; j < datiListaLotti.length; j++) {

          LottoType datiLotto = datiListaLotti[j];
          if (lotto_uuid != null && lotto_uuid.equals(datiLotto.getUUID())) {
            richiestaDiCancellazione = false;
          }

        }

        if (richiestaDiCancellazione == true) {
          if (stato_simog != null && !(new Long(1).equals(stato_simog))) {
            this.sqlManager.update("update w3lott set stato_simog = ? where numgara = ? and numlott = ?", new Object[] { new Long(5),
                numgara, numlott });
            messaggiDML.add(new String[] { "LOTTO", "DEL", lotto_uuid });
          } else {
            this.sqlManager.update("delete from w3lottcate where numgara = ? and numlott = ?", new Object[] { numgara, numlott });
            this.sqlManager.update("delete from w3lott where numgara = ? and numlott = ?", new Object[] { numgara, numlott });
            messaggiDML.add(new String[] { "LOTTO", "DEL", lotto_uuid });
          }
        }
      }
    }

    if (logger.isDebugEnabled()) logger.debug("EldasoftSIMOGWSManager.gestioneRichiesteCancellazioneW3LOTT: fine metodo");

  }

  /**
   * Inserimento dei lotti associati alla gara
   * 
   * @param datiGara
   * @param numgara
   * @throws GestoreException
   * @throws SQLException
   */
  private void gestioneW3LOTT(GaraType datiGara, Long numgara, List<String[]> messaggiDML) throws GestoreException, SQLException {

    if (logger.isDebugEnabled()) logger.debug("EldasoftSIMOGWSManager.gestioneW3LOTT: inizio metodo");

    LottoType[] datiListaLotti = datiGara.getLOTTIArray();

    if (datiListaLotti != null && datiListaLotti.length > 0) {
      for (int i = 0; i < datiListaLotti.length; i++) {

        LottoType datiLotto = datiListaLotti[i];

        // Controllo di univocita' del lotto sulla base dell'identificativo
        // universale univoco
        boolean esisteW3LOTT = this.w3Manager.esisteW3LOTT_UUID(datiLotto.getUUID());

        Long numlott = null;

        // Il lotto non esiste di procede all'inserimento di un nuovo lotto
        if (!esisteW3LOTT) {
          numlott = this.getNextValNUMLOTT(numgara);

          DataColumnContainer dccW3LOTT = new DataColumnContainer(new DataColumn[] { new DataColumn("W3LOTT.NUMGARA", new JdbcParametro(
              JdbcParametro.TIPO_NUMERICO, numgara)) });
          dccW3LOTT.addColumn("W3LOTT.NUMLOTT", JdbcParametro.TIPO_NUMERICO, numlott);

          // Identificativo Univoco Universale
          dccW3LOTT.addColumn("W3LOTT.LOTTO_UUID", datiLotto.getUUID());

          // Stato del lotto: nuovo lotto in attesa di richiesta, a SIMOG,
          // del codice identificativo CIG
          dccW3LOTT.addColumn("W3LOTT.STATO_SIMOG", JdbcParametro.TIPO_NUMERICO, new Long(1));

          // Aggiunto gli altri campi in modo da poterli utilizzare
          // nel metodo successivo
          dccW3LOTT.addColumn("W3LOTT.OGGETTO", JdbcParametro.TIPO_TESTO, null);
          dccW3LOTT.addColumn("W3LOTT.SOMMA_URGENZA", JdbcParametro.TIPO_TESTO, null);
          dccW3LOTT.addColumn("W3LOTT.TIPO_CONTRATTO", JdbcParametro.TIPO_TESTO, null);
          dccW3LOTT.addColumn("W3LOTT.FLAG_ESCLUSO", JdbcParametro.TIPO_TESTO, "2");
          dccW3LOTT.addColumn("W3LOTT.CPV", JdbcParametro.TIPO_TESTO, null);
          dccW3LOTT.addColumn("W3LOTT.ID_SCELTA_CONTRAENTE", JdbcParametro.TIPO_NUMERICO, null);
          dccW3LOTT.addColumn("W3LOTT.IMPORTO_LOTTO", JdbcParametro.TIPO_DECIMALE, null);
          dccW3LOTT.addColumn("W3LOTT.IMPORTO_ATTUAZIONE_SICUREZZA", JdbcParametro.TIPO_DECIMALE, null);
          dccW3LOTT.addColumn("W3LOTT.ID_CATEGORIA_PREVALENTE", JdbcParametro.TIPO_TESTO, null);
          dccW3LOTT.addColumn("W3LOTT.LUOGO_ISTAT", JdbcParametro.TIPO_TESTO, null);
          dccW3LOTT.addColumn("W3LOTT.LUOGO_NUTS", JdbcParametro.TIPO_TESTO, null);
          dccW3LOTT.addColumn("W3LOTT.TRIENNIO_ANNO_INIZIO", JdbcParametro.TIPO_NUMERICO, null);
          dccW3LOTT.addColumn("W3LOTT.TRIENNIO_ANNO_FINE", JdbcParametro.TIPO_NUMERICO, null);
          dccW3LOTT.addColumn("W3LOTT.TRIENNIO_PROGRESSIVO", JdbcParametro.TIPO_NUMERICO, null);
          dccW3LOTT.addColumn("W3LOTT.FLAG_CUP", JdbcParametro.TIPO_TESTO, null);
          
          dccW3LOTT.addColumn("W3LOTT.ID_AFF_RISERVATI", JdbcParametro.TIPO_NUMERICO, null);
          dccW3LOTT.addColumn("W3LOTT.ID_ESCLUSIONE", JdbcParametro.TIPO_NUMERICO, null);
          dccW3LOTT.addColumn("W3LOTT.FLAG_REGIME", JdbcParametro.TIPO_TESTO, "2");
          dccW3LOTT.addColumn("W3LOTT.ART_REGIME", JdbcParametro.TIPO_TESTO, null);
          dccW3LOTT.addColumn("W3LOTT.FLAG_DL50", JdbcParametro.TIPO_TESTO, null);
          dccW3LOTT.addColumn("W3LOTT.PRIMA_ANNUALITA", JdbcParametro.TIPO_NUMERICO, null);
          dccW3LOTT.addColumn("W3LOTT.ANNUALE_CUI_MININF", JdbcParametro.TIPO_TESTO, null);
          dccW3LOTT.addColumn("W3LOTT.FLAG_PREVEDE_RIP", JdbcParametro.TIPO_TESTO, "2");
          dccW3LOTT.addColumn("W3LOTT.FLAG_RIPETIZIONE", JdbcParametro.TIPO_TESTO, "2");
          dccW3LOTT.addColumn("W3LOTT.CIG_ORIGINE_RIP", JdbcParametro.TIPO_TESTO, null);
          dccW3LOTT.addColumn("W3LOTT.MOTIVO_COLLEGAMENTO", JdbcParametro.TIPO_NUMERICO, null);
          // Setto nel DataColumnContainer anche gli altri dati
          this.dccW3LOTT_AltriDatiLotto(dccW3LOTT, datiLotto, numgara, numlott);

          dccW3LOTT.insert("W3LOTT", this.sqlManager);

          messaggiDML.add(new String[] { "LOTTO", "INS", datiLotto.getUUID() });

        }

        // Il lotto esiste gia' si procede all'aggiornamento
        if (esisteW3LOTT) {

          HashMap<String, Long> hMap = new HashMap<String, Long>();
          hMap = this.w3Manager.getNUMGARA_NUMLOTT_UUID(datiLotto.getUUID());
          numlott = ((Long) hMap.get("numlott"));

          // Il lotto deve essere in uno stato modificabile
          if (this.w3Manager.isW3LOTT_UUIDModificabile(datiLotto.getUUID())) {

            DataColumnContainer dccW3LOTT = new DataColumnContainer(this.sqlManager, "W3LOTT",
                "select * from w3lott where numgara = ? and numlott = ?", new Object[] { numgara, numlott });

            // Aggiungo al DataColumnContainer anche gli altri dati comuni
            boolean listaUlterioriCategorieModificata = this.dccW3LOTT_AltriDatiLotto(dccW3LOTT, datiLotto, numgara, numlott);

            if (dccW3LOTT.isModifiedTable("W3LOTT") || listaUlterioriCategorieModificata == true) {
              // Stato del lotto: lotto modifica in attesa di
              // allineamento a SIMOG
              Long stato_simog = dccW3LOTT.getLong("W3LOTT.STATO_SIMOG");
              if (!new Long(1).equals(stato_simog)) {
                dccW3LOTT.setValue("W3LOTT.STATO_SIMOG", new Long(3));
              }

              dccW3LOTT.getColumn("W3LOTT.NUMGARA").setChiave(true);
              dccW3LOTT.getColumn("W3LOTT.NUMLOTT").setChiave(true);
              dccW3LOTT.setValue("W3LOTT.NUMGARA", numgara);
              dccW3LOTT.setValue("W3LOTT.NUMLOTT", numlott);

              dccW3LOTT.update("W3LOTT", this.sqlManager);

              messaggiDML.add(new String[] { "LOTTO", "UPD", datiLotto.getUUID() });

            }
          }
        }
      }
    }

    if (logger.isDebugEnabled()) logger.debug("EldasoftSIMOGWSManager.gestioneW3LOTT: fine metodo");

  }

  /**
   * Gestione dei dati comuni per l'inserimento e l'aggiornamento in W3LOTT
   * 
   * @param dccW3LOTT
   * @param datiLotto
   * @param numgara
   * @param numlott
   * @throws GestoreException
   * @throws SQLException
   */
  private boolean dccW3LOTT_AltriDatiLotto(DataColumnContainer dccW3LOTT, LottoType datiLotto, Long numgara, Long numlott)
      throws GestoreException, SQLException {

    if (logger.isDebugEnabled()) logger.debug("EldasoftSIMOGWSManager.dccW3LOTT_AltriDatiLotto: inizio metodo");

    boolean isListaUlterioriCategorieModificata = false;
    boolean isListaUlterioriCUPModificata = false;
    boolean isListaUlterioriTipologieLavoroModificata = false;
    boolean isListaUlterioriTipologieFornituraModificata = false;
    boolean isListaUlterioriCondizioniModificata = false;
    // Oggetto
    dccW3LOTT.setValue("W3LOTT.OGGETTO", datiLotto.getOGGETTO());

    // Somma urgenza
    if (datiLotto.isSetSOMMAURGENZA()) {
      dccW3LOTT.setValue("W3LOTT.SOMMA_URGENZA", datiLotto.getSOMMAURGENZA().toString());
    }

    // Tipo contratto
    dccW3LOTT.setValue("W3LOTT.TIPO_CONTRATTO", datiLotto.getTIPOCONTRATTO().toString());

    // Flag escluso
    if (datiLotto.isSetFLAGESCLUSO()) {
      dccW3LOTT.setValue("W3LOTT.FLAG_ESCLUSO", datiLotto.getFLAGESCLUSO().toString());
    }

    // CPV
    if (datiLotto.isSetCPV()) {
      dccW3LOTT.setValue("W3LOTT.CPV", datiLotto.getCPV());
    }

    // Modalita' di scelta del contraente
    if (datiLotto.isSetIDSCELTACONTRAENTE()) {
      dccW3LOTT.setValue("W3LOTT.ID_SCELTA_CONTRAENTE", new Long(datiLotto.getIDSCELTACONTRAENTE().toString()));
    }

    // Importo totale del lotto
    dccW3LOTT.setValue("W3LOTT.IMPORTO_LOTTO", new Double(datiLotto.getIMPORTOLOTTO()));

    // Importo per l'attuazione della sicurezza
    if (datiLotto.isSetIMPORTOATTUAZIONESICUREZZA()) {
      dccW3LOTT.setValue("W3LOTT.IMPORTO_ATTUAZIONE_SICUREZZA", new Double(datiLotto.getIMPORTOATTUAZIONESICUREZZA()));
    }

    // Categoria prevalente
    if (datiLotto.isSetIDCATEGORIAPREVALENTE()) {
      dccW3LOTT.setValue("W3LOTT.ID_CATEGORIA_PREVALENTE", datiLotto.getIDCATEGORIAPREVALENTE());
    }

    // Gestione delle categorie
    if (datiLotto.isSetCATEGORIE()) {

      boolean inserisciCategorie = true;

      String[] datiCategorie = datiLotto.getCATEGORIE().getCATEGORIAArray();

      // Confronto la lista delle categorie gia' memorizzate con quelle inviate
      // via WS
      // Se ci sono differenze provvedo a cancellare le categorie presenti
      // per poi inserirle nuovamente in blocco
      String sqlSelectW3LOTTCATE = "select categoria from w3lottcate where numgara = ? and numlott = ?";
      List<?> datiW3LOTTCATE = this.sqlManager.getListVector(sqlSelectW3LOTTCATE, new Object[] { numgara, numlott });
      if (datiW3LOTTCATE != null && datiW3LOTTCATE.size() > 0) {

        String[] datiCategorieW3LOTTCATE = new String[datiW3LOTTCATE.size()];
        for (int i = 0; i < datiW3LOTTCATE.size(); i++) {
          datiCategorieW3LOTTCATE[i] = (String) SqlManager.getValueFromVectorParam(datiW3LOTTCATE.get(i), 0).getValue();
        }
        java.util.Arrays.sort(datiCategorieW3LOTTCATE);
        java.util.Arrays.sort(datiCategorie);

        if (java.util.Arrays.equals(datiCategorieW3LOTTCATE, datiCategorie)) {
          inserisciCategorie = false;
        } else {
          isListaUlterioriCategorieModificata = true;
          inserisciCategorie = true;
          String sqlDeleteW3LOTTCATE = "delete from w3lottcate where numgara = ? and numlott = ?";
          this.sqlManager.update(sqlDeleteW3LOTTCATE, new Object[] { numgara, numlott });
        }

      }

      // Inserimento di tutte le categorie
      if (inserisciCategorie) {
        if (datiCategorie != null && datiCategorie.length > 0) {
          for (int j = 0; j < datiCategorie.length; j++) {
            DataColumnContainer dccW3LOTTCATE = new DataColumnContainer(new DataColumn[] { new DataColumn("W3LOTTCATE.NUMGARA",
                new JdbcParametro(JdbcParametro.TIPO_NUMERICO, numgara)) });
            dccW3LOTTCATE.addColumn("W3LOTTCATE.NUMLOTT", JdbcParametro.TIPO_NUMERICO, numlott);
            dccW3LOTTCATE.addColumn("W3LOTTCATE.NUMCATE", JdbcParametro.TIPO_NUMERICO, new Long(j + 1));
            dccW3LOTTCATE.addColumn("W3LOTTCATE.CATEGORIA", JdbcParametro.TIPO_TESTO, datiCategorie[j]);
            dccW3LOTTCATE.insert("W3LOTTCATE", this.sqlManager);
          }
        }
      }
    }

    // Luogo ISTAT
    if (datiLotto.isSetLUOGOISTAT()) {
      dccW3LOTT.setValue("W3LOTT.LUOGO_ISTAT", datiLotto.getLUOGOISTAT());
    }

    // Luogo NUTS
    if (datiLotto.isSetLUOGONUTS()) {
      dccW3LOTT.setValue("W3LOTT.LUOGO_NUTS", datiLotto.getLUOGONUTS().toString());
    }

    // Triennio anno inizio
    if (datiLotto.isSetTRIENNIOANNOINIZIO()) {
      dccW3LOTT.setValue("W3LOTT.TRIENNIO_ANNO_INIZIO", new Long(datiLotto.getTRIENNIOANNOINIZIO()));
    }

    // Triennio anno fine
    if (datiLotto.isSetTRIENNIOANNOFINE()) {
      dccW3LOTT.setValue("W3LOTT.TRIENNIO_ANNO_FINE", new Long(datiLotto.getTRIENNIOANNOFINE()));
    }

    // Triennio progressivo
    if (datiLotto.isSetTRIENNIOPROGRESSIVO()) {
      dccW3LOTT.setValue("W3LOTT.TRIENNIO_PROGRESSIVO", new Long(datiLotto.getTRIENNIOPROGRESSIVO()));
    }

    //Flag CUP - cup obbligatorio
    dccW3LOTT.setValue("W3LOTT.FLAG_CUP", datiLotto.getFLAGCUP().toString());
    
    // Gestione del CUP
    if (datiLotto.isSetLISTACUP()) {

      boolean inserisciCUP = true;

      String[] datiCUP = datiLotto.getLISTACUP().getCUPArray();

      // Confronto la lista dei cup gia' memorizzati con quelli inviati
      // via WS
      // Se ci sono differenze provvedo a cancellare i CUP presenti
      // per poi inserirli nuovamente in blocco
      String sqlSelectW3LOTTCUP = "select CUP from W3LOTTCUP where numgara = ? and numlott = ?";
      List<?> datiW3LOTTCUP = this.sqlManager.getListVector(sqlSelectW3LOTTCUP, new Object[] { numgara, numlott });
      if (datiW3LOTTCUP != null && datiW3LOTTCUP.size() > 0) {

        String[] datiCUPW3LOTTCUP = new String[datiW3LOTTCUP.size()];
        for (int i = 0; i < datiW3LOTTCUP.size(); i++) {
        	datiCUPW3LOTTCUP[i] = (String) SqlManager.getValueFromVectorParam(datiW3LOTTCUP.get(i), 0).getValue();
        }
        java.util.Arrays.sort(datiCUPW3LOTTCUP);
        java.util.Arrays.sort(datiCUP);

        if (java.util.Arrays.equals(datiCUPW3LOTTCUP, datiCUP)) {
        	inserisciCUP = false;
        } else {
          isListaUlterioriCUPModificata = true;
          inserisciCUP = true;
          String sqlDeleteW3LOTTCUP = "delete from W3LOTTCUP where numgara = ? and numlott = ?";
          this.sqlManager.update(sqlDeleteW3LOTTCUP, new Object[] { numgara, numlott });
        }

      }

      // Inserimento di tutte le categorie
      if (inserisciCUP) {
        if (datiCUP != null && datiCUP.length > 0) {
          for (int j = 0; j < datiCUP.length; j++) {
            DataColumnContainer dccW3LOTTCUP = new DataColumnContainer(new DataColumn[] { new DataColumn("W3LOTTCUP.NUMGARA",
                new JdbcParametro(JdbcParametro.TIPO_NUMERICO, numgara)) });
            dccW3LOTTCUP.addColumn("W3LOTTCUP.NUMLOTT", JdbcParametro.TIPO_NUMERICO, numlott);
            dccW3LOTTCUP.addColumn("W3LOTTCUP.NUMCUP", JdbcParametro.TIPO_NUMERICO, new Long(j + 1));
            dccW3LOTTCUP.addColumn("W3LOTTCUP.CUP", JdbcParametro.TIPO_TESTO, datiCUP[j]);
            dccW3LOTTCUP.insert("W3LOTTCUP", this.sqlManager);
          }
        }
      }
    }
    
    // Gestione della tipologia lavoro
    if (datiLotto.isSetTIPOLOGIALAVORO()) {

      boolean inserisciTipologiaLavoro = true;

      BigInteger[] datiTipologiaLavoro = datiLotto.getTIPOLOGIALAVORO().getTIPOLOGIALAVOROArray();

      // Confronto la lista delle tipologie gia' memorizzate con quelle inviate
      // via WS
      // Se ci sono differenze provvedo a cancellare le tipologie presenti
      // per poi inserirle nuovamente in blocco
      String sqlSelectW3LOTTTIPI = "select IDAPPALTO from W3LOTTTIPI where numgara = ? and numlott = ?";
      List<?> datiW3LOTTTIPI = this.sqlManager.getListVector(sqlSelectW3LOTTTIPI, new Object[] { numgara, numlott });
      if (datiW3LOTTTIPI != null && datiW3LOTTTIPI.size() > 0) {

    	BigInteger[] datiTipologieW3LOTTTIPI = new BigInteger[datiW3LOTTTIPI.size()];
        for (int i = 0; i < datiW3LOTTTIPI.size(); i++) {
        	if (SqlManager.getValueFromVectorParam(datiW3LOTTTIPI.get(i), 0).getValue() != null) {
        		datiTipologieW3LOTTTIPI[i] = new BigInteger(SqlManager.getValueFromVectorParam(datiW3LOTTTIPI.get(i), 0).getValue().toString());
        	}
        }
        java.util.Arrays.sort(datiTipologieW3LOTTTIPI);
        java.util.Arrays.sort(datiTipologiaLavoro);

        if (java.util.Arrays.equals(datiTipologieW3LOTTTIPI, datiTipologiaLavoro)) {
        	inserisciTipologiaLavoro = false;
        } else {
        	isListaUlterioriTipologieLavoroModificata = true;
        	inserisciTipologiaLavoro = true;
        	String sqlDeleteW3LOTTTIPI = "delete from W3LOTTTIPI where numgara = ? and numlott = ?";
        	this.sqlManager.update(sqlDeleteW3LOTTTIPI, new Object[] { numgara, numlott });
        }

      }

      // Inserimento di tutte le tipologie
      if (inserisciTipologiaLavoro) {
        if (datiTipologiaLavoro != null && datiTipologiaLavoro.length > 0) {
          for (int j = 0; j < datiTipologiaLavoro.length; j++) {
            DataColumnContainer dccW3LOTTTIPI = new DataColumnContainer(new DataColumn[] { new DataColumn("W3LOTTTIPI.NUMGARA",
                new JdbcParametro(JdbcParametro.TIPO_NUMERICO, numgara)) });
            dccW3LOTTTIPI.addColumn("W3LOTTTIPI.NUMLOTT", JdbcParametro.TIPO_NUMERICO, numlott);
            dccW3LOTTTIPI.addColumn("W3LOTTTIPI.NUMTIPI", JdbcParametro.TIPO_NUMERICO, new Long(j + 1));
            dccW3LOTTTIPI.addColumn("W3LOTTTIPI.IDAPPALTO", JdbcParametro.TIPO_NUMERICO, datiTipologiaLavoro[j].longValue());
            dccW3LOTTTIPI.insert("W3LOTTTIPI", this.sqlManager);
          }
        }
      }
    }
    
    // Gestione della tipologia forniture
    if (datiLotto.isSetTIPOLOGIAFORNITURA()) {

      boolean inserisciTipologiaFornitura = true;

      BigInteger[] datiTipologiaFornitura = datiLotto.getTIPOLOGIAFORNITURA().getTIPOLOGIAFORNITURAArray();

      // Confronto la lista delle tipologie gia' memorizzate con quelle inviate
      // via WS
      // Se ci sono differenze provvedo a cancellare le tipologie presenti
      // per poi inserirle nuovamente in blocco
      String sqlSelectW3LOTTTIPI = "select IDAPPALTO from W3LOTTTIPI where numgara = ? and numlott = ?";
      List<?> datiW3LOTTTIPI = this.sqlManager.getListVector(sqlSelectW3LOTTTIPI, new Object[] { numgara, numlott });
      if (datiW3LOTTTIPI != null && datiW3LOTTTIPI.size() > 0) {

    	BigInteger[] datiTipologieW3LOTTTIPI = new BigInteger[datiW3LOTTTIPI.size()];
        for (int i = 0; i < datiW3LOTTTIPI.size(); i++) {
        	if (SqlManager.getValueFromVectorParam(datiW3LOTTTIPI.get(i), 0).getValue() != null) {
        		datiTipologieW3LOTTTIPI[i] = new BigInteger(SqlManager.getValueFromVectorParam(datiW3LOTTTIPI.get(i), 0).getValue().toString());
        	}
        }
        java.util.Arrays.sort(datiTipologieW3LOTTTIPI);
        java.util.Arrays.sort(datiTipologiaFornitura);

        if (java.util.Arrays.equals(datiTipologieW3LOTTTIPI, datiTipologiaFornitura)) {
        	inserisciTipologiaFornitura = false;
        } else {
        	isListaUlterioriTipologieFornituraModificata = true;
        	inserisciTipologiaFornitura = true;
        	String sqlDeleteW3LOTTTIPI = "delete from W3LOTTTIPI where numgara = ? and numlott = ?";
        	this.sqlManager.update(sqlDeleteW3LOTTTIPI, new Object[] { numgara, numlott });
        }
      }

      // Inserimento di tutte le tipologie
      if (inserisciTipologiaFornitura) {
        if (datiTipologiaFornitura != null && datiTipologiaFornitura.length > 0) {
          for (int j = 0; j < datiTipologiaFornitura.length; j++) {
            DataColumnContainer dccW3LOTTTIPI = new DataColumnContainer(new DataColumn[] { new DataColumn("W3LOTTTIPI.NUMGARA",
                new JdbcParametro(JdbcParametro.TIPO_NUMERICO, numgara)) });
            dccW3LOTTTIPI.addColumn("W3LOTTTIPI.NUMLOTT", JdbcParametro.TIPO_NUMERICO, numlott);
            dccW3LOTTTIPI.addColumn("W3LOTTTIPI.NUMTIPI", JdbcParametro.TIPO_NUMERICO, new Long(j + 1));
            dccW3LOTTTIPI.addColumn("W3LOTTTIPI.IDAPPALTO", JdbcParametro.TIPO_NUMERICO, datiTipologiaFornitura[j].longValue());
            dccW3LOTTTIPI.insert("W3LOTTTIPI", this.sqlManager);
          }
        }
      }
    }
    
    // Gestione della condizioni negoziate
    if (datiLotto.isSetCONDIZIONINEGOZIATA()) {

      boolean inserisciCondizione = true;

      BigInteger[] datiCondizione = datiLotto.getCONDIZIONINEGOZIATA().getCONDIZIONEArray();

      // Confronto la lista delle condizioni gia' memorizzate con quelle inviate
      // via WS
      // Se ci sono differenze provvedo a cancellare le condizioni presenti
      // per poi inserirle nuovamente in blocco
      String sqlSelectW3COND = "select ID_CONDIZIONE from W3COND where numgara = ? and numlott = ?";
      List<?> datiW3COND = this.sqlManager.getListVector(sqlSelectW3COND, new Object[] { numgara, numlott });
      if (datiW3COND != null && datiW3COND.size() > 0) {

    	  BigInteger[] datiCondizioniW3COND = new BigInteger[datiW3COND.size()];
    	  for (int i = 0; i < datiW3COND.size(); i++) {
    		  datiCondizioniW3COND[i] = new BigInteger(SqlManager.getValueFromVectorParam(datiW3COND.get(i), 0).getValue().toString());
    	  }
        java.util.Arrays.sort(datiCondizioniW3COND);
        java.util.Arrays.sort(datiCondizione);

        if (java.util.Arrays.equals(datiCondizioniW3COND, datiCondizione)) {
        	inserisciCondizione = false;
        } else {
        	isListaUlterioriCondizioniModificata = true;
        	inserisciCondizione = true;
        	String sqlDeleteW3COND = "delete from W3COND where numgara = ? and numlott = ?";
        	this.sqlManager.update(sqlDeleteW3COND, new Object[] { numgara, numlott });
        }

      }

      // Inserimento di tutte le condizioni
      if (inserisciCondizione) {
        if (datiCondizione != null && datiCondizione.length > 0) {
          for (int j = 0; j < datiCondizione.length; j++) {
            DataColumnContainer dccW3COND = new DataColumnContainer(new DataColumn[] { new DataColumn("W3COND.NUMGARA",
                new JdbcParametro(JdbcParametro.TIPO_NUMERICO, numgara)) });
            dccW3COND.addColumn("W3COND.NUMLOTT", JdbcParametro.TIPO_NUMERICO, numlott);
            dccW3COND.addColumn("W3COND.NUM_COND", JdbcParametro.TIPO_NUMERICO, new Long(j + 1));
            dccW3COND.addColumn("W3COND.ID_CONDIZIONE", JdbcParametro.TIPO_NUMERICO, datiCondizione[j].longValue());
            dccW3COND.insert("W3COND", this.sqlManager);
          }
        }
      }
    }
    
    if (datiLotto.isSetAFFIDAMENTIRISERVATI()) {
    	dccW3LOTT.setValue("W3LOTT.ID_AFF_RISERVATI", new Long(datiLotto.getAFFIDAMENTIRISERVATI().toString()));
    }
    
    if (datiLotto.isSetARTESCLUSIONE()) {
    	dccW3LOTT.setValue("W3LOTT.ID_ESCLUSIONE", new Long(datiLotto.getARTESCLUSIONE().toString()));
    }
    
    if (datiLotto.isSetFLAGREGIME()) {
    	dccW3LOTT.setValue("W3LOTT.FLAG_REGIME", datiLotto.getFLAGREGIME().toString());
    }
    
    if (datiLotto.isSetARTREGIME()) {
    	dccW3LOTT.setValue("W3LOTT.ART_REGIME", datiLotto.getARTREGIME().toString());
    }
    
    if (datiLotto.isSetFLAGDL50()) {
    	dccW3LOTT.setValue("W3LOTT.FLAG_DL50", datiLotto.getFLAGDL50().toString());
    }
    
    if (datiLotto.isSetPRIMAANNUALITA()) {
    	dccW3LOTT.setValue("W3LOTT.PRIMA_ANNUALITA", new Long(datiLotto.getPRIMAANNUALITA()));
    }
    
    if (datiLotto.isSetCUIPROGRAMMA()) {
    	dccW3LOTT.setValue("W3LOTT.ANNUALE_CUI_MININF", datiLotto.getCUIPROGRAMMA());
    }
    
    if (datiLotto.isSetFLAGPREVEDERIP()) {
    	dccW3LOTT.setValue("W3LOTT.FLAG_PREVEDE_RIP", datiLotto.getFLAGPREVEDERIP().toString());
    }
    
    if (datiLotto.isSetFLAGRIPETIZIONE()) {
    	dccW3LOTT.setValue("W3LOTT.FLAG_RIPETIZIONE", datiLotto.getFLAGRIPETIZIONE().toString());
    }
    
    if (datiLotto.isSetCIGORIGINERIP()) {
    	dccW3LOTT.setValue("W3LOTT.CIG_ORIGINE_RIP", datiLotto.getCIGORIGINERIP());
    }
    
    if (datiLotto.isSetMOTIVOCOLLEGAMENTO()) {
    	dccW3LOTT.setValue("W3LOTT.MOTIVO_COLLEGAMENTO", new Long(datiLotto.getMOTIVOCOLLEGAMENTO().toString()));
    }
    
    if (logger.isDebugEnabled()) logger.debug("EldasoftSIMOGWSManager.dccW3LOTT_AltriDatiLotto: fine metodo");

    return isListaUlterioriCategorieModificata || isListaUlterioriCUPModificata ||
    isListaUlterioriTipologieLavoroModificata || isListaUlterioriTipologieFornituraModificata ||
    isListaUlterioriCondizioniModificata;

  }

  /**
   * Gestione dei dati della gara comuni all'inserimento e all'aggiornamento in
   * W3GARA
   * 
   * @param dccW3GARA
   * @param datiGara
   * @param numgara
   * @param syscon_remoto
   * @param codein
   * @throws GestoreException
   * @throws SQLException
   */
  private void dccW3GARA_AltriDatiGara(DataColumnContainer dccW3GARA, GaraType datiGara, Long numgara, Long syscon_remoto, String codein) throws GestoreException,
      SQLException {

    if (logger.isDebugEnabled()) logger.debug("EldasoftSIMOGWSManager.dccW3GARA_AltriDatiGara: inizio metodo");

    // Oggetto
    dccW3GARA.setValue("W3GARA.OGGETTO", datiGara.getOGGETTO());

    // Tipo scheda
    if (datiGara.isSetTIPOSCHEDA()) {
      dccW3GARA.setValue("W3GARA.TIPO_SCHEDA", datiGara.getTIPOSCHEDA().toString());
    }

    // Modo indizione
    if (datiGara.isSetMODOINDIZIONE()) {
      dccW3GARA.setValue("W3GARA.MODO_INDIZIONE", new Long(datiGara.getMODOINDIZIONE().toString()));
    }

    // Modo realizzazione
    if (datiGara.isSetMODOREALIZZAZIONE()) {
      dccW3GARA.setValue("W3GARA.MODO_REALIZZAZIONE", new Long(datiGara.getMODOREALIZZAZIONE().toString()));
    }

    // Importo gara
    if (datiGara.isSetIMPORTOGARA()) {
      dccW3GARA.setValue("W3GARA.IMPORTO_GARA", new Double(datiGara.getIMPORTOGARA()));
    }

    // RUP
    String rup_codtec = this.gestisciRUP(datiGara.getRUP(), codein, syscon_remoto);
    dccW3GARA.setValue("W3GARA.RUP_CODTEC", rup_codtec);

    // Gestione della collaborazione.
    // Inserisco la collaborazione di default solo se la collaborazione
    // associata al RUP indicato è una sola
    if (rup_codtec != null) {
    	String cfsa = "";
    	if (datiGara.isSetCFSTAZIONEAPPALTANTE()) {
    		cfsa = datiGara.getCFSTAZIONEAPPALTANTE();
    	} else {
    		cfsa = (String)sqlManager.getObject("select cfein from uffint where codein = ?", new Object[] { codein});
    	}
    	List<?> datiW3AZIENDAUFFICIO = sqlManager.getListVector(
                "select w3aziendaufficio.id "
                    + "from w3aziendaufficio, w3usrsyscoll "
                    + "where w3aziendaufficio.id = w3usrsyscoll.w3aziendaufficio_id "
                    + "and w3usrsyscoll.syscon = ? and w3usrsyscoll.rup_codtec = ? and w3aziendaufficio.azienda_cf = ?",
                new Object[] { syscon_remoto, rup_codtec, cfsa });
    	if (datiW3AZIENDAUFFICIO != null && datiW3AZIENDAUFFICIO.size() == 1) {
    		Long collaborazione = (Long) SqlManager.getValueFromVectorParam(datiW3AZIENDAUFFICIO.get(0), 0).getValue();
    		dccW3GARA.setValue("W3GARA.COLLABORAZIONE", collaborazione);
    	}
    }

    // CIG accordo quadro
    if (datiGara.isSetCIGACCQUADRO()) {
      dccW3GARA.setValue("W3GARA.CIG_ACC_QUADRO", datiGara.getCIGACCQUADRO());
    }
    
    if (datiGara.isSetMOTIVAZIONECIG()) {
        dccW3GARA.setValue("W3GARA.M_RICH_CIG", new Long(datiGara.getMOTIVAZIONECIG().toString()));
    }
    
    if (datiGara.isSetMODALITAINDIZIONEALLEGATOIX()) {
        dccW3GARA.setValue("W3GARA.ALLEGATO_IX", new Long(datiGara.getMODALITAINDIZIONEALLEGATOIX().toString()));
    }
    
    if (datiGara.isSetSTRUMENTOSVOLGIMENTOPROCEDURA()) {
        dccW3GARA.setValue("W3GARA.STRUMENTO_SVOLGIMENTO", new Long(datiGara.getSTRUMENTOSVOLGIMENTOPROCEDURA().toString()));
    }
    
    if (datiGara.isSetSOMMAURGENZA()) {
        dccW3GARA.setValue("W3GARA.URGENZA_DL133", datiGara.getSOMMAURGENZA().toString());
    } else {
    	//default NO
    	dccW3GARA.setValue("W3GARA.URGENZA_DL133", "2");
    }
    
    if (datiGara.isSetARTESTREMAURGENZA()) {
        dccW3GARA.setValue("W3GARA.ESTREMA_URGENZA", new Long(datiGara.getARTESTREMAURGENZA().toString()));
    }
    
    dccW3GARA.setValue("W3GARA.ESCLUSO_AVCPASS", "2");
    if (datiGara.isSetMODOREALIZZAZIONE()) {
    	String modalitaRealizzazione = datiGara.getMODOREALIZZAZIONE().toString();
    	// se Accordo quadro/convenzione
    	if(modalitaRealizzazione.equals("9") || modalitaRealizzazione.equals("17") || modalitaRealizzazione.equals("18")) {
    		dccW3GARA.setValue("W3GARA.ESCLUSO_AVCPASS", "1");
    	}
    }

    if (datiGara.isSetDURATAACCQUADRO()) {
        dccW3GARA.setValue("W3GARA.DURATA_ACCQUADRO", new Long(datiGara.getDURATAACCQUADRO()));
    }
    
    //se le categorie merceologiche sono vuote aggiiungo di default la 999
    if (datiGara.getCATEGORIE() == null || datiGara.getCATEGORIE().sizeOfCATEGORIAArray()==0) {
    	CategorieMerceologicheType categorie = datiGara.addNewCATEGORIE();
    	categorie.addCATEGORIA(new BigInteger("999"));
    }
    // Gestione delle categorie
    if (datiGara.getCATEGORIE() != null && datiGara.getCATEGORIE().sizeOfCATEGORIAArray()>0) {
      boolean inserisciCategorie = true;
      BigInteger[] datiCategorie = datiGara.getCATEGORIE().getCATEGORIAArray();
      // Confronto la lista delle categorie gia' memorizzate con quelle inviate
      // via WS
      // Se ci sono differenze provvedo a cancellare le categorie presenti
      // per poi inserirle nuovamente in blocco
      String sqlSelectW3GARAMERC = "select categoria from W3GARAMERC where NUMGARA = ?";
      List<?> datiW3GARAMERC = this.sqlManager.getListVector(sqlSelectW3GARAMERC, new Object[] { numgara });
      if (datiW3GARAMERC != null && datiW3GARAMERC.size() > 0) {
    	BigInteger[] datiCategorieW3GARAMERC = new BigInteger[datiW3GARAMERC.size()];
        for (int i = 0; i < datiW3GARAMERC.size(); i++) {
        	if (SqlManager.getValueFromVectorParam(datiW3GARAMERC.get(i), 0).getValue() != null) {
        		datiCategorieW3GARAMERC[i] = new BigInteger(SqlManager.getValueFromVectorParam(datiW3GARAMERC.get(i), 0).getValue().toString());
        	}
        }
        java.util.Arrays.sort(datiCategorieW3GARAMERC);
        java.util.Arrays.sort(datiCategorie);
        if (java.util.Arrays.equals(datiCategorieW3GARAMERC, datiCategorie)) {
          inserisciCategorie = false;
        } else {
          inserisciCategorie = true;
          String sqlDeleteW3GARAMERC = "delete from W3GARAMERC where NUMGARA = ?";
          this.sqlManager.update(sqlDeleteW3GARAMERC, new Object[] { numgara });
        }
      }
      // Inserimento di tutte le categorie
      if (inserisciCategorie) {
        if (datiCategorie != null && datiCategorie.length > 0) {
          for (int j = 0; j < datiCategorie.length; j++) {
            DataColumnContainer dccW3SMARTCIGMERC = new DataColumnContainer(new DataColumn[] { new DataColumn("W3GARAMERC.NUMGARA",
                new JdbcParametro(JdbcParametro.TIPO_NUMERICO, numgara)) });
            dccW3SMARTCIGMERC.addColumn("W3GARAMERC.NUMMERC", JdbcParametro.TIPO_NUMERICO, new Long(j + 1));
            dccW3SMARTCIGMERC.addColumn("W3GARAMERC.CATEGORIA", JdbcParametro.TIPO_NUMERICO, datiCategorie[j].longValue());
            dccW3SMARTCIGMERC.insert("W3GARAMERC", this.sqlManager);
          }
        }
      }
    }
    
    if (logger.isDebugEnabled()) logger.debug("EldasoftSIMOGWSManager.dccW3GARA_AltriDatiGara: fine metodo");

  }

  
  /**
   * Gestione dei dati dello smartcig all'inserimento e all'aggiornamento in
   * W3SMARTCIG
   * 
   * @param dccW3SMARTCIG
   * @param datiSmartCig
   * @param codrich
   * @param syscon_remoto
   * @param codein
   * @throws GestoreException
   * @throws SQLException
   */
  private void dccW3SMARTCIG_AltriDati(DataColumnContainer dccW3SMARTCIG, SmartCIGType datiSmartCig, Long codrich, Long syscon_remoto, String codein) throws GestoreException,
      SQLException {

    if (logger.isDebugEnabled()) logger.debug("EldasoftSIMOGWSManager.dccW3SMARTCIG_AltriDati: inizio metodo");

    dccW3SMARTCIG.addColumn("W3SMARTCIG.RUP", JdbcParametro.TIPO_TESTO, null);
    
    
    // Oggetto
    dccW3SMARTCIG.setValue("W3SMARTCIG.OGGETTO", datiSmartCig.getOGGETTO());

    // Fattispecie
    if (datiSmartCig.isSetFATTISPECIE()) {
    	dccW3SMARTCIG.setValue("W3SMARTCIG.FATTISPECIE", datiSmartCig.getFATTISPECIE().toString());
    }

    // Importo
    dccW3SMARTCIG.setValue("W3SMARTCIG.IMPORTO", datiSmartCig.getIMPORTO());
    
    // Tipo contratto
    dccW3SMARTCIG.setValue("W3SMARTCIG.TIPO_CONTRATTO", datiSmartCig.getTIPOCONTRATTO().toString());
    
    // cig acc quadro
    if (datiSmartCig.isSetCIGACCQUADRO()) {
    	dccW3SMARTCIG.setValue("W3SMARTCIG.CIG_ACC_QUADRO", datiSmartCig.getCIGACCQUADRO());
    }
    
    // cup
    if (datiSmartCig.isSetCUP()) {
    	dccW3SMARTCIG.setValue("W3SMARTCIG.CUP", datiSmartCig.getCUP());
    }
    
    // M_RICH_CIG_COMUNI
    if (datiSmartCig.isSetMOTIVICOMUNI()) {
    	dccW3SMARTCIG.setValue("W3SMARTCIG.M_RICH_CIG_COMUNI", datiSmartCig.getMOTIVICOMUNI().toString());
    }
    
    // motivi richiesta
    if (datiSmartCig.isSetMOTIVORICHIESTA()) {
    	dccW3SMARTCIG.setValue("W3SMARTCIG.M_RICH_CIG", datiSmartCig.getMOTIVORICHIESTA().toString());
    }
    
    // scelta contraente
   	dccW3SMARTCIG.setValue("W3SMARTCIG.ID_SCELTA_CONTRAENTE", datiSmartCig.getSCELTACONTRAENTE().toString());
    
    // RUP
    String rup_codtec = this.gestisciRUP(datiSmartCig.getRUP(), codein, syscon_remoto);
    dccW3SMARTCIG.setValue("W3SMARTCIG.RUP", rup_codtec);

    // Gestione della collaborazione.
    // Inserisco la collaborazione di default solo se la collaborazione
    // associata al RUP indicato è una sola
    if (rup_codtec != null) {
    	String cfsa = "";
    	if (datiSmartCig.isSetCFSTAZIONEAPPALTANTE()) {
    		cfsa = datiSmartCig.getCFSTAZIONEAPPALTANTE();
    	} else {
    		cfsa = (String)sqlManager.getObject("select cfein from uffint where codein = ?", new Object[] { codein});
    	}
    	List<?> datiW3AZIENDAUFFICIO = sqlManager.getListVector(
                "select w3aziendaufficio.id "
                    + "from w3aziendaufficio, w3usrsyscoll "
                    + "where w3aziendaufficio.id = w3usrsyscoll.w3aziendaufficio_id "
                    + "and w3usrsyscoll.syscon = ? and w3usrsyscoll.rup_codtec = ? and w3aziendaufficio.azienda_cf = ?",
                new Object[] { syscon_remoto, rup_codtec, cfsa });
    	if (datiW3AZIENDAUFFICIO != null && datiW3AZIENDAUFFICIO.size() == 1) {
    		Long collaborazione = (Long) SqlManager.getValueFromVectorParam(datiW3AZIENDAUFFICIO.get(0), 0).getValue();
    		dccW3SMARTCIG.setValue("W3SMARTCIG.COLLABORAZIONE", collaborazione);
    	}
    }

    // Gestione delle categorie
    if (datiSmartCig.getCATEGORIE() != null && datiSmartCig.getCATEGORIE().sizeOfCATEGORIAArray()>0) {
      boolean inserisciCategorie = true;
      BigInteger[] datiCategorie = datiSmartCig.getCATEGORIE().getCATEGORIAArray();
      // Confronto la lista delle categorie gia' memorizzate con quelle inviate
      // via WS
      // Se ci sono differenze provvedo a cancellare le categorie presenti
      // per poi inserirle nuovamente in blocco
      String sqlSelectW3SMARTCIGMERC = "select categoria from w3smartcigmerc where codrich = ?";
      List<?> datiW3SMARTCIGMERC = this.sqlManager.getListVector(sqlSelectW3SMARTCIGMERC, new Object[] { codrich });
      if (datiW3SMARTCIGMERC != null && datiW3SMARTCIGMERC.size() > 0) {
    	BigInteger[] datiCategorieW3SMARTCIGMERC = new BigInteger[datiW3SMARTCIGMERC.size()];
        for (int i = 0; i < datiW3SMARTCIGMERC.size(); i++) {
        	if (SqlManager.getValueFromVectorParam(datiW3SMARTCIGMERC.get(i), 0).getValue() != null) {
        		datiCategorieW3SMARTCIGMERC[i] = new BigInteger(SqlManager.getValueFromVectorParam(datiW3SMARTCIGMERC.get(i), 0).getValue().toString());
        	}
        }
        java.util.Arrays.sort(datiCategorieW3SMARTCIGMERC);
        java.util.Arrays.sort(datiCategorie);
        if (java.util.Arrays.equals(datiCategorieW3SMARTCIGMERC, datiCategorie)) {
          inserisciCategorie = false;
        } else {
          inserisciCategorie = true;
          String sqlDeleteW3SMARTCIGMERC = "delete from w3smartcigmerc where codrich = ?";
          this.sqlManager.update(sqlDeleteW3SMARTCIGMERC, new Object[] { codrich });
        }
      }
      // Inserimento di tutte le categorie
      if (inserisciCategorie) {
        if (datiCategorie != null && datiCategorie.length > 0) {
          for (int j = 0; j < datiCategorie.length; j++) {
            DataColumnContainer dccW3SMARTCIGMERC = new DataColumnContainer(new DataColumn[] { new DataColumn("W3SMARTCIGMERC.CODRICH",
                new JdbcParametro(JdbcParametro.TIPO_NUMERICO, codrich)) });
            dccW3SMARTCIGMERC.addColumn("W3SMARTCIGMERC.NUMMERC", JdbcParametro.TIPO_NUMERICO, new Long(j + 1));
            dccW3SMARTCIGMERC.addColumn("W3SMARTCIGMERC.CATEGORIA", JdbcParametro.TIPO_NUMERICO, datiCategorie[j].longValue());
            dccW3SMARTCIGMERC.insert("W3SMARTCIGMERC", this.sqlManager);
          }
        }
      }
    }
    if (logger.isDebugEnabled()) logger.debug("EldasoftSIMOGWSManager.dccW3SMARTCIG_AltriDati: fine metodo");
  }
  
  
  /**
   * Gestione del RUP incaricato
   * 
   * @param tecnico
   * @param codein
   * @param syscon_remoto
   * @return
   * @throws GestoreException
   * @throws SQLException
   */
  private String gestisciRUP(TecnicoType tecnico, String codein, Long syscon_remoto) throws GestoreException {

    if (logger.isDebugEnabled()) logger.debug("EldasoftSIMOGWSManager.gestisciRUP: inizio metodo");

    String codtec = null;

    try {

      //String protezioneArchivi = ConfigManager.getValore(PROP_PROTEZIONE_ARCHIVI);

      boolean esisteTecnico = false;
      String cftec = null;
      //String pivatec = null;

      cftec = tecnico.getCFTEC();
      //if (tecnico.isSetPIVATEC()) pivatec = tecnico.getPIVATEC();

      if (cftec != null) {
          String selectTECNI = "select codtec from tecni where cftec = ? and cgentei = ?";
          codtec = (String) sqlManager.getObject(selectTECNI, new Object[] { cftec, codein });
          if (codtec != null) esisteTecnico = true;

          if (!esisteTecnico) {
            codtec = this.geneManager.calcolaCodificaAutomatica("TECNI", "CODTEC");

            DataColumn[] dcTECNI = new DataColumn[] { new DataColumn("TECNI.CODTEC", new JdbcParametro(JdbcParametro.TIPO_TESTO, codtec)) };
            DataColumnContainer dccTECNI = new DataColumnContainer(dcTECNI);

            if (tecnico.isSetCOGTEI()) dccTECNI.addColumn("TECNI.COGTEI", JdbcParametro.TIPO_TESTO, tecnico.getCOGTEI());

            if (tecnico.isSetNOMETEI()) dccTECNI.addColumn("TECNI.NOMETEI", JdbcParametro.TIPO_TESTO, tecnico.getNOMETEI());

            dccTECNI.addColumn("TECNI.NOMTEC", JdbcParametro.TIPO_TESTO, tecnico.getNOMTEC());

            dccTECNI.addColumn("TECNI.CFTEC", JdbcParametro.TIPO_TESTO, cftec);

            dccTECNI.addColumn("TECNI.CGENTEI", JdbcParametro.TIPO_TESTO, codein);
            
            dccTECNI.insert("TECNI", this.sqlManager);
          }
      } else {
    	  throw new GestoreException("Errore di database nella gestione del RUP incaricato", "errors.inserisciGaraLotto.gestisciRUP.sqlerror");
      }
    } catch (SQLException e) {
      throw new GestoreException("Errore di database nella gestione del RUP incaricato", "errors.inserisciGaraLotto.gestisciRUP.sqlerror",
          e);
    }

    if (logger.isDebugEnabled()) logger.debug("EldasoftSIMOGWSManager.gestisciRUP: fine metodo");

    return codtec;

  }

  /**
   * Calcola in NUMGARA di W3GARA
   * 
   * @return
   */
  private Long getNextValNUMGARA() throws GestoreException {

    if (logger.isDebugEnabled()) logger.debug("getNextValNUMGARA: inizio metodo");

    Long numgara = new Long(this.genChiaviManager.getMaxId("W3GARA", "NUMGARA"));
    numgara = new Long(numgara.longValue() + 1);

    if (logger.isDebugEnabled()) logger.debug("getNextValNUMGARA: inizio metodo");

    return numgara;

  }

  /**
   * Calcola in CODRICH di W3SMARTCIG
   * 
   * @return
   */
  private Long getNextValCODRICH() throws GestoreException {

    if (logger.isDebugEnabled()) logger.debug("getNextValCODRICH: inizio metodo");

    Long numgara = new Long(this.genChiaviManager.getMaxId("W3SMARTCIG", "CODRICH"));
    numgara = new Long(numgara.longValue() + 1);

    if (logger.isDebugEnabled()) logger.debug("getNextValCODRICH: inizio metodo");

    return numgara;

  }
  
  /**
   * Calcola NUMLOTT di W3LOTT
   * 
   * @param numgara
   * @return
   * @throws GestoreException
   */
  private Long getNextValNUMLOTT(Long numgara) throws GestoreException {

    if (logger.isDebugEnabled()) logger.debug("getNextValNUMLOTT: inizio metodo");

    Long numlott = null;

    try {
      numlott = (Long) this.sqlManager.getObject("select max(numlott) from w3lott where numgara = ?", new Object[] { numgara });
      if (numlott == null) numlott = new Long(0);
      numlott = new Long(numlott.longValue() + 1);
    } catch (SQLException e) {
      throw new GestoreException("Errore durante il controllo del lotto", "controlloW3LOTT", e);
    }

    if (logger.isDebugEnabled()) logger.debug("getNextValNUMLOTT: inizio metodo");

    return numlott;

  }

}
