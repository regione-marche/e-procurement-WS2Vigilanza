package it.eldasoft.sil.vigilanza.bl;
//"IDGARA 578653: modificata 'Pubblicazione documenti' con codice W9PBCOD_PUBB01"
import it.eldasoft.gene.bl.GenChiaviManager;
import it.eldasoft.gene.bl.GeneManager;
import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.db.datautils.DataColumn;
import it.eldasoft.gene.db.datautils.DataColumnContainer;
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.sil.vigilanza.beans.DocumentoType;
import it.eldasoft.sil.vigilanza.beans.PubblicazioneType;
import it.eldasoft.sil.vigilanza.beans.RichiestaSincronaIstanzaPubblicazioneDocumentiDocument;
import it.eldasoft.sil.vigilanza.commons.CostantiWSW9;
import it.eldasoft.sil.vigilanza.commons.WSVigilanzaException;
import it.eldasoft.sil.vigilanza.utils.UtilitySITAT;
import it.eldasoft.sil.vigilanza.ws.beans.IstanzaOggettoType;
import it.eldasoft.sil.vigilanza.ws.beans.LoginType;
import it.eldasoft.sil.vigilanza.ws.beans.ResponsePubblicazioneType;
import it.eldasoft.sil.vigilanza.ws.beans.ResponseType;
import it.eldasoft.utils.properties.ConfigManager;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlOptions;
import org.apache.xmlbeans.XmlValidationError;

/**
 * Manager per l'importazione dei dati delle pubblicazioni di documenti.
 * 
 * @author Luca.Giacomazzo
 */
public class IstanzaPubblicazioneDocumentiManager {

	private static Logger logger = Logger.getLogger(IstanzaPubblicazioneDocumentiManager.class);

	private CredenzialiManager credenzialiManager;

	private SqlManager sqlManager;

	private GeneManager geneManager;
	
	private GenChiaviManager genChiaviManager;

	/**
	 * @param credenzialiManager
	 *            the credenzialiManager to set
	 */
	public void setCredenzialiManager(CredenzialiManager credenzialiManager) {
		this.credenzialiManager = credenzialiManager;
	}

	/**
	 * @param sqlManager
	 *            the sqlManager to set
	 */
	public void setSqlManager(SqlManager sqlManager) {
		this.sqlManager = sqlManager;
	}

	/**
	 * @param geneManager
	 *            the geneManager to set
	 */
	public void setGeneManager(GeneManager geneManager) {
		this.geneManager = geneManager;
	}
	
	/**
	 * @param genChiaviManager
	 * 						the genChiaviManager to set
	 */
	public void setGenChiaviManager(GenChiaviManager genChiaviManager) {
		this.genChiaviManager = genChiaviManager;
	}

	public ResponsePubblicazioneType istanziaPubblicazioneDocumenti(LoginType login, IstanzaOggettoType objPubblicazione)
      throws XmlException, GestoreException, SQLException, Throwable {
    ResponseType result = new ResponseType();

		if (logger.isDebugEnabled()) {
			logger.debug("istanziaPubblicazioneDocumenti: inizio metodo");
			
			logger.debug("XML : " + objPubblicazione.getOggettoXML());
		}
		
		// Codice fiscale della Stazione appaltante
    String codFiscaleStazAppaltante = objPubblicazione.getTestata().getCFEIN();

    boolean sovrascrivereDatiEsistenti = false;
    if (objPubblicazione.getTestata().getSOVRASCR() != null) {
      sovrascrivereDatiEsistenti = objPubblicazione.getTestata().getSOVRASCR().booleanValue(); 
    }
    
    // Verifica di login, password e determinazione della S.A.
    HashMap<String, Object> hm = this.credenzialiManager.verificaCredenziali(login, codFiscaleStazAppaltante);
    result = (ResponseType) hm.get("RESULT");
    
    ResponsePubblicazioneType resultPubb = new ResponsePubblicazioneType();
    resultPubb.setSuccess(result.isSuccess());
    resultPubb.setError(result.getError());
    
    if (resultPubb.isSuccess()) {
    	// L'utente ha inserito login epassword corrette, e' abilitato ed e' 
    	// associato alla S.A. con CF = codFiscaleStazAppaltante.
      Credenziale credenzialiUtente = (Credenziale) hm.get("USER");
      String xmlPubblicazione = objPubblicazione.getOggettoXML();
      
      try {
        RichiestaSincronaIstanzaPubblicazioneDocumentiDocument istanzaPubblicazioneDocument =
          RichiestaSincronaIstanzaPubblicazioneDocumentiDocument.Factory.parse(xmlPubblicazione);

        boolean isMessaggioDiTest = 
          istanzaPubblicazioneDocument.getRichiestaSincronaIstanzaPubblicazioneDocumenti().isSetTest()
            && istanzaPubblicazioneDocument.getRichiestaSincronaIstanzaPubblicazioneDocumenti().getTest();
        
        if (!isMessaggioDiTest) {
      
          // si esegue il controllo sintattico del messaggio
          XmlOptions validationOptions = new XmlOptions();
          ArrayList<XmlValidationError> validationErrors = new ArrayList<XmlValidationError>();
          validationOptions.setErrorListener(validationErrors);
          boolean isSintassiXmlOK = istanzaPubblicazioneDocument.validate(validationOptions);

          if (!isSintassiXmlOK) {
            synchronized (validationErrors) {
              // Sincronizzazione dell'oggetto validationErrors per scrivere
              // sul log il dettaglio dell'errore su righe successive.  
              StringBuilder strLog = new StringBuilder(credenzialiUtente.getPrefissoLogger());
              strLog.append(" Errore nella validazione del messaggio ricevuto per la gestione di una " +
                  "istanza di pubblicazione documenti.");
              
              for (int i = 0; i < validationErrors.size(); i++) {
              	strLog.append("\n" + validationErrors.get(i).getMessage());
              }
              
              logger.error(credenzialiUtente.getPrefissoLogger() + strLog);
            }
            resultPubb.setSuccess(false);
            resultPubb.setError("Impossibile interpretare i dati inviati (Parsing XML)");
            
          } else {
          	PubblicazioneType pubblicazione =
          		istanzaPubblicazioneDocument.getRichiestaSincronaIstanzaPubblicazioneDocumenti().getPubblicazione();
          	DocumentoType[] arrayDocumenti = 
          		istanzaPubblicazioneDocument.getRichiestaSincronaIstanzaPubblicazioneDocumenti().getDocumentiArray();
          	String[] arrayCigLotti = 
          		istanzaPubblicazioneDocument.getRichiestaSincronaIstanzaPubblicazioneDocumenti().getCigLottiArray();

          	String idAvGara = pubblicazione.getW3IDGARA();
          	
          	if (!UtilitySITAT.existsGara(idAvGara, this.sqlManager)) {
          		// nella base dati di destinazione non esiste la gara con idavgara indicato
          		resultPubb.setSuccess(false);
          		resultPubb.setError("Gara non definita nell’archivio di destinazione");
          		
          		logger.error(credenzialiUtente.getPrefissoLogger() + "Scheda pubblicazione documenti non creata perche' non esiste la gara con IDAVGARA="
          				+ pubblicazione.getW3IDGARA());
          	} else {
          		// Campo chiave della gara in W9GARA
          		Long codiceGara = UtilitySITAT.getCodGaraByIdAvGara(idAvGara, this.sqlManager); 
          		
          		// Verifica se tutti i CIG sono lotti di gara e se l'utente e' il RUP di tali lotti.
          		StringBuffer strBuffer = new StringBuffer("");
          		if (arrayCigLotti != null && arrayCigLotti.length > 0) {
          			if (arrayCigLotti.length > 1) {
          				for (int y=0; y < arrayCigLotti.length; y++) {
          					strBuffer.append("'");
          					if (StringUtils.isNotEmpty(arrayCigLotti[y].trim())) { 
          						strBuffer.append(arrayCigLotti[y].trim().toUpperCase());
          					}
          					strBuffer.append("'");
          					if (y < (arrayCigLotti.length - 1)) {
          						strBuffer.append(",");	
          					}
          				}
          			} else {
          				strBuffer.append("'");
          				strBuffer.append(arrayCigLotti[0]);
          				strBuffer.append("'");
          			}
          			
          			Long countLottiGara = (Long) this.sqlManager.getObject(
          					"select count(*) from W9LOTT where CODGARA=? and CIG in (" + 
          					strBuffer.toString() + ")", new Object[] { codiceGara });
          			
          			if (countLottiGara == arrayCigLotti.length) {
          				// Tutti i CIG sono lotti della gara con idAvGara indicato nella pubblicazione

	                if (!UtilitySITAT.isUtenteAmministratore(credenzialiUtente)) {
	                	Long conteggio = (Long) sqlManager.getObject(
	                			"select count(*) from TECNI T where T.CGENTEI=? and T.CFTEC=? and " +
	                      " exists (select 1 from W9LOTT L where L.RUP=T.CODTEC and CIG in (" +
	                      strBuffer.toString() + "))", 
	                      new Object[] { credenzialiUtente.getStazioneAppaltante().getCodice(),
	                      		credenzialiUtente.getAccount().getCodfisc() });
	                	if (conteggio == null || (conteggio != null && conteggio.intValue() != 1)) {
		                		logger.error(credenzialiUtente.getPrefissoLogger() + "L'utente non e' RUP dei lotti con CIG=" 
		                				+ strBuffer.toString() + ", oppure non e' stato indicato il RUP della gara/lotto");
		                		
	                		throw new WSVigilanzaException("Le credenziali fornite non coincidono con quelle del RUP indicato");
	                	}
	                } else {
	                	// dalla chiamata this.credenzialiManager.verificaCredenziali si e' gia'
	                	// verificato che l'utente e' associato alla S.A. con CF = codFiscaleStazAppaltante
	                	// Quindi si può continuare.
	                }
	                
	                if (UtilitySITAT.isFaseAttiva(new Long(CostantiWSW9.PUBBLICAZIONE_DOCUMENTI), this.sqlManager)) {
		                if (resultPubb.isSuccess()) {
		                	Long codicePubblicazione = null;
		                	boolean esistePubblicazione = false;
	
		                	if (pubblicazione.isSetW9PBCODPUBB()) {
		                		codicePubblicazione = new Long(pubblicazione.getW9PBCODPUBB());
		                		esistePubblicazione = UtilitySITAT.existsPubblicazioneDocumenti(
		                  			codiceGara, codicePubblicazione, this.sqlManager);
		                	} else {
		                		codicePubblicazione = new Long(this.genChiaviManager.getNextId("W9PUBBLICAZIONI_GEN"));
		                		esistePubblicazione = false;
		                	}
	
		                	if ((pubblicazione.isSetW9PBCODPUBB() && esistePubblicazione && sovrascrivereDatiEsistenti)
		                			  || (!pubblicazione.isSetW9PBCODPUBB())) {
	                  		// Controlli preliminari superati con successo.
	                  		// Inizio inserimento dei dati degli esiti nella base dati.
	                  		DataColumn codGaraPubblicazione = new DataColumn("W9PUBBLICAZIONI.CODGARA", 
	                  				new JdbcParametro(JdbcParametro.TIPO_NUMERICO, codiceGara));
	                  		DataColumn numeroPubblicazione = new DataColumn("W9PUBBLICAZIONI.NUM_PUBB", 
	                  				new JdbcParametro(JdbcParametro.TIPO_NUMERICO, new Long(1)));
	
	                      DataColumn codLottEsito = new DataColumn("W9PUBBLICAZIONI.ID_GENERATO", 
	                      		new JdbcParametro(JdbcParametro.TIPO_NUMERICO, new Long(codicePubblicazione)));
	                      DataColumn tipoPubblicazione = new DataColumn("W9PUBBLICAZIONI.TIPDOC",
	                          new JdbcParametro(JdbcParametro.TIPO_NUMERICO, new Long(pubblicazione.getW9PBTIPDOC())));
	                      DataColumnContainer dccPubblicazione = new DataColumnContainer(new DataColumn[] { 
	                          codGaraPubblicazione, codLottEsito, tipoPubblicazione, numeroPubblicazione });
	                      dccPubblicazione.getColumn("W9PUBBLICAZIONI.CODGARA").setChiave(true);
	                      dccPubblicazione.getColumn("W9PUBBLICAZIONI.NUM_PUBB").setChiave(true);
	                      
	                      if (pubblicazione.isSetW9PBDESCRIZ()) {
	                      	dccPubblicazione.addColumn("W9PUBBLICAZIONI.DESCRIZ", new JdbcParametro(
	                        		JdbcParametro.TIPO_TESTO, pubblicazione.getW9PBDESCRIZ()));
	                      }
	                      if (pubblicazione.isSetW9PBTIPOPR()) {
	                      	dccPubblicazione.addColumn("W9PUBBLICAZIONI.TIPO_PROVVEDIMENTO", new JdbcParametro(
	                        		JdbcParametro.TIPO_TESTO, pubblicazione.getW9PBTIPOPR()));
	                      }
	                      if (pubblicazione.isSetW9PBCAUSEISTR()) {
	                      	dccPubblicazione.addColumn("W9PUBBLICAZIONI.CAUSE_ISTRUTTORIA", new JdbcParametro(
	                        		JdbcParametro.TIPO_TESTO, pubblicazione.getW9PBCAUSEISTR()));
	                      }
	                      if (pubblicazione.isSetW9PBDATAAVV()) {
	                      	dccPubblicazione.addColumn("W9PUBBLICAZIONI.DATA_AVVIO", new JdbcParametro(
	                        		JdbcParametro.TIPO_DATA, pubblicazione.getW9PBDATAAVV().getTime()));
	                      }
	                      if (pubblicazione.isSetW9PBDATADEC()) {
	                      	dccPubblicazione.addColumn("W9PUBBLICAZIONI.DATA_DECRETO", new JdbcParametro(
	                        		JdbcParametro.TIPO_DATA, pubblicazione.getW9PBDATADEC().getTime()));
	                      }
	                      if (pubblicazione.isSetW9PBDATASCAD()) {
	                      	dccPubblicazione.addColumn("W9PUBBLICAZIONI.DATASCAD", new JdbcParametro(
	                        		JdbcParametro.TIPO_DATA, pubblicazione.getW9PBDATASCAD().getTime()));
	                      }
	                      if (pubblicazione.isSetW9PBDATAPUBB()) {
	                      	dccPubblicazione.addColumn("W9PUBBLICAZIONI.DATAPUBB", new JdbcParametro(
	                        		JdbcParametro.TIPO_DATA, pubblicazione.getW9PBDATAPUBB().getTime()));
	                      }
	                      
	                      if (pubblicazione.isSetW9PBDATAPR()) {
	                      	dccPubblicazione.addColumn("W9PUBBLICAZIONI.DATA_PROVVEDIMENTO", new JdbcParametro(
	                        		JdbcParametro.TIPO_DATA, pubblicazione.getW9PBDATAPR().getTime()));
	                      }
	                      
	                      
	                      if (esistePubblicazione) {
	                      	Long numPubb = (Long) this.sqlManager.getObject(
	                  					"select NUM_PUBB from W9PUBBLICAZIONI where CODGARA=? and ID_GENERATO=?",
	                  							new Object[] {codiceGara, codicePubblicazione });
		                  		if (numPubb != null) {
		                  			numeroPubblicazione.setValue(new JdbcParametro(JdbcParametro.TIPO_NUMERICO, numPubb));
		                  			this.sqlManager.update("delete from W9PUBBLICAZIONI where CODGARA=? and NUM_PUBB=?",
		                  					new Object[] { codiceGara, numPubb });
		                  		}
	                      } else {
	                      	Long numPubb = new Long(this.genChiaviManager.getMaxId("W9PUBBLICAZIONI", "NUM_PUBB", 
	                  					" CODGARA=" + codiceGara) + 1);
	                  			numeroPubblicazione.setValue(new JdbcParametro(JdbcParametro.TIPO_NUMERICO, numPubb));
	                      }
	                      
	                      dccPubblicazione.insert("W9PUBBLICAZIONI", this.sqlManager);
	                      
	                      if (UtilitySITAT.isScritturaNoteAvvisiAttiva()) {
		                      if (esistePubblicazione) {
		                      	UtilitySITAT.insertNoteAvvisi("W9PUBBLICAZIONI", codiceGara.toString(),
	                      				numeroPubblicazione.getValue().getStringValue(), null, null, null, 
	                      				"IDGARA " + codiceGara + ": modificata 'Pubblicazione documenti' con codice " + 
	                      				codicePubblicazione, null, this.sqlManager);
		                      } else {
		                      	if (UtilitySITAT.isScritturaNoteAvvisiAttiva()) {
		                      		UtilitySITAT.insertNoteAvvisi("W9PUBBLICAZIONI", codiceGara.toString(),
		                      				numeroPubblicazione.getValue().getStringValue(), null, null, null,
		                      				"IDGARA " + codiceGara + ": inserita 'Pubblicazione documenti' con codice = "
		                      				+ codicePubblicazione, null, this.sqlManager);
		                      	}
		                      }
	                      }
	
	    									// -----------------------------------------------------------------
	    									// --- Inserimento dei documenti delle pubblicazioni (W9DOCGARA) ---
	    									// -----------------------------------------------------------------
	    									this.istanziaDocumentiPubblicazione(arrayDocumenti, codiceGara, 
	    											(Long) numeroPubblicazione.getValue().getValue());
	                      
	    									// ------------------------------------------------------------------------
	    									// --- Inserimento associazione pubblicazioni con i lotti (W9PUB_LOTTO) ---
	    									// ------------------------------------------------------------------------
	    									this.istanziaLottiPubblicazione(arrayCigLotti, codiceGara, 
	    											(Long) numeroPubblicazione.getValue().getValue());
	    									
	    									resultPubb.setSuccess(true);
	    									resultPubb.setError(codicePubblicazione.toString());
	    									//resultPubb.setError("Importazione pubblicazione terminata correttamente");
	    									
		                	} else {
		                		if (pubblicazione.isSetW9PBCODPUBB()) {
		                			if (esistePubblicazione && !sovrascrivereDatiEsistenti) {
		                				resultPubb.setSuccess(false);
				                		resultPubb.setError("Pubblicazione gia' esistente e non sovrascritta");
		                			}
		                			
		                			if (!esistePubblicazione) {
		                				resultPubb.setSuccess(false);
				                		resultPubb.setError("Aggiornamento di una pubblicazione inesistente per la gara indicata");
		                			}
		                		} 
		                	}
		                }
	                } else {
	                	// Scheda non prevista per il contratto
	                	StringBuilder strLog = new StringBuilder(credenzialiUtente.getPrefissoLogger());
	                	strLog.append("La gara con Numero Gara= ");
	                	strLog.append(idAvGara);
	                  strLog.append(" non prevede la scheda Pubblicazione documenti (901) perche' il TAB1NORD e' minore o uguale a zero");
	                  logger.error(strLog);
	                  
	                  resultPubb.setSuccess(false);
	                  resultPubb.setError(ConfigManager.getValore("error.schedaNonPrevista"));
	                }
	              } else {
	              	// Non tutti i CIG indicati nella arrayCigLotti sono lotti della gara oppure non esistono in base dati.
	              	logger.error(credenzialiUtente.getPrefissoLogger() + "I Cig = " + strBuffer.toString()
	              			+ " non sono lotti della gara con W9GARA.IDAVGARA=" + idAvGara );

	              	// Si lancia un eccezione per effetttuare la rollback degli insert/update per i lotti precedenti
	              	throw new WSVigilanzaException("Dati scartati per incoerenza Gara - Lotto");
	              }
          		} else {
          			// errore che non si dovrebbe verificare perche' nel caso in cui l'array dei cig dei lotti e' null
          			// oppure ha dimensione nulla rientra nel caso di non validazione del XML
          		}
          	}
          }
        } else {
          // E' stato inviato un messaggio di test.
          StringBuilder strLog = new StringBuilder(credenzialiUtente.getPrefissoLogger());
          strLog.append(" E' stato inviato un messaggio di test. Tale messaggio non e' stato elaborato.'\n");
          strLog.append("Messaggio: ");
          strLog.append(objPubblicazione.toString());
          logger.info(strLog);
          
          resultPubb.setSuccess(true);
          resultPubb.setError("E' stato inviato un messaggio di test: messaggio non elaborato.");
        }
      } catch (XmlException e) {
        StringBuilder strLog = new StringBuilder(credenzialiUtente.getPrefissoLogger());
        strLog.append(" Errore nel parsing dell'XML ricevuto.\n");
        strLog.append(e);
        logger.error(strLog);

        throw e;
      } catch (GestoreException g) {
      	logger.error(credenzialiUtente.getPrefissoLogger() + " Errore nella preparazione dei dati da salvare.", g);

        throw g;
      } catch (SQLException sql) {
      	logger.error(credenzialiUtente.getPrefissoLogger() + "Errore sql nel salvataggio dei dati ricevuti.", sql);

        throw sql;
      } catch (Throwable t) {
      	logger.error(credenzialiUtente.getPrefissoLogger() + "Errore generico nell'esecuzione della procedura.", t);
        
        throw t;
      }
    }
		if (logger.isDebugEnabled()) {
			logger.debug("istanziaPubblicazioneDocumenti: fine metodo");
			
			logger.debug("Response : " + UtilitySITAT.messaggioLogEsteso(result));
		}
	    
	  // MODIFICA TEMPORANEA -- DA CANCELLARE ---------------
	  //result.setError(UtilitySITAT.messaggioEsteso(result));
	  // ----------------------------------------------------
		
		return resultPubb;
	}

	/**
	 * Inserimento o aggiornamento dei documenti di una pubblicazione.
	 * 
	 * @param arrayDocumenti
	 * @param codgara
	 * @param numeroPubblicazione
	 * @param codicePubblicazione
	 * @throws GestoreException
	 * @throws SQLException
	 */
	private void istanziaDocumentiPubblicazione(DocumentoType[] arrayDocumenti, Long codgara, 
			Long numeroPubblicazione) throws GestoreException, SQLException {
		
		// Se ci sono occorrenze, vengono prima rimosse
		if (this.geneManager.countOccorrenze("W9DOCGARA", " CODGARA=? and NUM_PUBB=? ",
				new Object[]{codgara, numeroPubblicazione }) > 0) {
			this.sqlManager.update("DELETE FROM W9DOCGARA where CODGARA=? and NUM_PUBB=? ",
					new Object[]{codgara, numeroPubblicazione });
		}
		
		if (arrayDocumenti.length > 0) {
			for (int iu = 0; iu < arrayDocumenti.length; iu++) {
				DocumentoType documento = arrayDocumenti[iu];

				DataColumn codiceGaraW9DocGara = new DataColumn("W9DOCGARA.CODGARA", 
						new JdbcParametro(JdbcParametro.TIPO_NUMERICO, codgara));
				DataColumn numeroPubW9DocGara = new DataColumn("W9DOCGARA.NUM_PUBB", 
						new JdbcParametro(JdbcParametro.TIPO_NUMERICO, numeroPubblicazione));
				DataColumn numDocW9DocGara = new DataColumn("W9DOCGARA.NUMDOC", 
						new JdbcParametro(JdbcParametro.TIPO_NUMERICO, new Long(iu + 1)));
				DataColumn titoloW9DocGara = new DataColumn("W9DOCGARA.TITOLO", 
						new JdbcParametro(JdbcParametro.TIPO_TESTO, documento.getW9DGTITOLO()));

				DataColumnContainer dccDocGara = new DataColumnContainer(new DataColumn[] {
						codiceGaraW9DocGara, numeroPubW9DocGara, numDocW9DocGara, titoloW9DocGara });

				if (documento.isSetFile()) {
					ByteArrayOutputStream fileAllegato = new ByteArrayOutputStream();
					try {
						fileAllegato.write(documento.getFile());
					} catch (IOException e) {
						throw new GestoreException("Errore durante la lettura del file allegato presente nella richiesta XML", null, e);
					}
					dccDocGara.addColumn("W9DOCGARA.FILE_ALLEGATO", JdbcParametro.TIPO_BINARIO, fileAllegato);
				}
				
				if (documento.isSetW9DGURL()) {
					dccDocGara.addColumn("W9DOCGARA.URL", new JdbcParametro(JdbcParametro.TIPO_TESTO, documento.getW9DGURL()));
				}
				
				// Insert in W9DOCGARA
				dccDocGara.insert("W9DOCGARA", this.sqlManager);
			}
		}
	}

	/**
	 * Inserimento o aggiornamento dell'associazione di una pubblicazione con i lotti della gara.
	 * 
	 * @param arrayCig
	 * @param codgara
	 * @param numeroPubblicazione
	 * @param codicePubblicazione
	 * @throws GestoreException
	 * @throws SQLException
	 */
	private void istanziaLottiPubblicazione(String[] arrayCig, Long codgara, Long numeroPubblicazione) 
		throws GestoreException, SQLException {

		if (this.geneManager.countOccorrenze("W9PUBLOTTO", " CODGARA=? and NUM_PUBB=? ",
				new Object[]{ codgara, numeroPubblicazione }) > 0) {
			this.sqlManager.update("DELETE FROM W9PUBLOTTO where CODGARA=? and NUM_PUBB=? ",
					new Object[]{ codgara, numeroPubblicazione });
		}

		DataColumn codiceGaraW9PubLotto = new DataColumn("W9PUBLOTTO.CODGARA", 
				new JdbcParametro(JdbcParametro.TIPO_NUMERICO, codgara));
		DataColumn numeroPubW9PubLotto = new DataColumn("W9PUBLOTTO.NUM_PUBB", 
				new JdbcParametro(JdbcParametro.TIPO_NUMERICO, numeroPubblicazione));
		
		if (arrayCig.length > 0) {
			HashSet<String> setStr = new HashSet<String>();
			
			// Copio i CIG in un HastSet per eliminare gli eventuali CIG ripetuti
			for (int iu = 0; iu < arrayCig.length; iu++) {
				String cig = arrayCig[iu];
				setStr.add(cig);
			}
			
			if (!setStr.isEmpty()) {
				Iterator<String> iter = setStr.iterator();
				
				while (iter.hasNext()) {
					String cig = iter.next();
					Long numeroLotto = (Long) this.sqlManager.getObject("select CODLOTT from W9LOTT where CODGARA=? and CIG=? ", 
							new Object[] {codgara, cig });
					
					if (numeroLotto != null) {
						DataColumn numLottoW9PubLotto = new DataColumn("W9PUBLOTTO.CODLOTT", 
								new JdbcParametro(JdbcParametro.TIPO_NUMERICO, numeroLotto ));
					
						DataColumnContainer dccPubLotto = new DataColumnContainer(new DataColumn[] {
							codiceGaraW9PubLotto, numeroPubW9PubLotto, numLottoW9PubLotto });
						// Insert in W9PUBLOTTO
						dccPubLotto.insert("W9PUBLOTTO", this.sqlManager);
					} else {
						logger.error("W9PUBLOTTO: Il lotto con CIG='" + cig + "' non e' lotto della gara con CODGARA="
								+ codgara + " o perfino non esiste");
					}
				}
			}
		}
	}

}
