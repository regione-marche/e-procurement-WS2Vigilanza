package it.eldasoft.sil.vigilanza.bl;

import it.eldasoft.gene.bl.GenChiaviManager;
import it.eldasoft.gene.bl.GeneManager;
import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.db.datautils.DataColumn;
import it.eldasoft.gene.db.datautils.DataColumnContainer;
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.sil.vigilanza.beans.ElencoImpreseInvitateType;
import it.eldasoft.sil.vigilanza.beans.ImpresaInvitataType;
import it.eldasoft.sil.vigilanza.beans.RichiestaSincronaIstanzaElencoImpreseInvitateDocument;
import it.eldasoft.sil.vigilanza.commons.CostantiWSW9;
import it.eldasoft.sil.vigilanza.commons.WSVigilanzaException;
import it.eldasoft.sil.vigilanza.utils.UtilitySITAT;
import it.eldasoft.sil.vigilanza.ws.beans.IstanzaOggettoType;
import it.eldasoft.sil.vigilanza.ws.beans.LoginType;
import it.eldasoft.sil.vigilanza.ws.beans.ResponseLottoType;
import it.eldasoft.sil.vigilanza.ws.beans.ResponseType;
import it.eldasoft.utils.properties.ConfigManager;
import it.toscana.rete.rfc.sitat.types.FaseEstesaType;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlOptions;
import org.apache.xmlbeans.XmlValidationError;

/**
 * Classe per l'importazione delle imprese invitate/partecipanti al lotto di gara
 * 
 * @author luca.giacomazzo
 */
public class IstanzaElencoImpreseInvitateManager {

private static Logger logger = Logger.getLogger(IstanzaElencoImpreseInvitateManager.class);
  
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
  
  public void setGenChiaviManager(GenChiaviManager genChiaviManager) {
    this.genChiaviManager = genChiaviManager;
  }

  public ResponseType istanziaElencoImpreseInvitate(LoginType login, IstanzaOggettoType elencoImpreseInvitate)
  		throws XmlException, GestoreException, SQLException, Throwable {
  	ResponseType result = null;

  	if (logger.isDebugEnabled()) {
  		logger.debug("istanziaElencoImpreseInvitate: inizio metodo");
    		logger.debug("XML : " + elencoImpreseInvitate.getOggettoXML());
  	}
  
  	// Codice fiscale della Stazione appaltante
    String codFiscaleStazAppaltante = elencoImpreseInvitate.getTestata().getCFEIN();
    
    boolean sovrascrivereDatiEsistenti = false;
    if (elencoImpreseInvitate.getTestata().getSOVRASCR() != null) {
      sovrascrivereDatiEsistenti = elencoImpreseInvitate.getTestata().getSOVRASCR().booleanValue();
    }
    
    // Verifica di login, password e determinazione della S.A.
    HashMap<String, Object> hm = this.credenzialiManager.verificaCredenziali(
    		login, codFiscaleStazAppaltante);
    result = (ResponseType) hm.get("RESULT");
    
    if (result.isSuccess()) {
      Credenziale credenzialiUtente = (Credenziale) hm.get("USER");
      String xmlAggiudicazione = elencoImpreseInvitate.getOggettoXML();
      
      try {
        RichiestaSincronaIstanzaElencoImpreseInvitateDocument istanzaElencoImpreseInvitateDocument =
          RichiestaSincronaIstanzaElencoImpreseInvitateDocument.Factory.parse(xmlAggiudicazione);

        boolean isMessaggioDiTest = 
          istanzaElencoImpreseInvitateDocument.getRichiestaSincronaIstanzaElencoImpreseInvitate().isSetTest()
            && istanzaElencoImpreseInvitateDocument.getRichiestaSincronaIstanzaElencoImpreseInvitate().getTest();
        
        if (! isMessaggioDiTest) {
        
          // si esegue il controllo sintattico del messaggio
          XmlOptions validationOptions = new XmlOptions();
          ArrayList<XmlValidationError> validationErrors = new ArrayList<XmlValidationError>();
          validationOptions.setErrorListener(validationErrors);
          boolean isSintassiXmlOK = istanzaElencoImpreseInvitateDocument.validate(validationOptions);
  
          if (!isSintassiXmlOK) {
            synchronized (validationErrors) {
              // Sincronizzazione dell'oggetto validationErrors per scrivere
              // sul log il dettaglio dell'errore su righe successive.  
              StringBuilder strLog = new StringBuilder(credenzialiUtente.getPrefissoLogger());
              strLog.append(" Errore nella validazione del messaggio ricevuto per la gestione di una " +
              		"istanza di elenco imprese invitate.");
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
            ElencoImpreseInvitateType[] arrayElencoImpreseInvitate =
              istanzaElencoImpreseInvitateDocument.getRichiestaSincronaIstanzaElencoImpreseInvitate().getListaElencoImpreseInvitateArray();
            
            if (arrayElencoImpreseInvitate != null && arrayElencoImpreseInvitate.length > 0) {
              // HashMap per caricare gli oggetti ResponseLottoType per ciascun lotto, con CIG come chiave della hashMap
              HashMap<String, ResponseLottoType> hmResponseLotti = new HashMap<String, ResponseLottoType>();

              StringBuilder strQueryCig = new StringBuilder("'");
              
              for (int esi = 0; esi < arrayElencoImpreseInvitate.length; esi++) {
              	ElencoImpreseInvitateType oggettoElencoImpreseInvitate = arrayElencoImpreseInvitate[esi];
                String cigLotto = oggettoElencoImpreseInvitate.getW3CIG();

                strQueryCig.append(cigLotto);
                if (esi+1 < arrayElencoImpreseInvitate.length)
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
              	if (listaCodGara.size() !=  arrayElencoImpreseInvitate.length) {
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
              
              int numeroLottiImportati = 0;
              int numeroLottiNonImportati = 0;
              int numeroLottiAggiornati = 0;

              // Controlli preliminari superati con successo.
              for (int agg = 0; agg < arrayElencoImpreseInvitate.length; agg++) {
                ElencoImpreseInvitateType impresaInvitataPartecipante = arrayElencoImpreseInvitate[agg];
                String cigLotto = impresaInvitataPartecipante.getW3CIG();
                
                HashMap<String, Long> hashM = UtilitySITAT.getCodGaraCodLottByCIG(cigLotto, this.sqlManager);
                Long codiceGara = hashM.get("CODGARA");
                Long codiceLotto = hashM.get("CODLOTT");

                if (!UtilitySITAT.isUtenteAmministratore(credenzialiUtente)) {
                	if (!UtilitySITAT.isUtenteRupDelLotto(cigLotto, credenzialiUtente, this.sqlManager)) {
                		logger.error(credenzialiUtente.getPrefissoLogger() + "L'utente non e' RUP del lotto con CIG=" + cigLotto
                				+ ", oppure nella gara e/o nel lotto non e' stato indicato il RUP");

                		throw new WSVigilanzaException("Le credenziali fornite non coincidono con quelle del RUP indicato");
                	}
                }
                
                Long faseEsecuz = new Long(CostantiWSW9.ELENCO_IMPRESE_INVITATE_PARTECIPANTI);
                
                if (UtilitySITAT.isFaseAttiva(faseEsecuz, this.sqlManager)) {
                boolean esisteFaseElencoImpreseInviate = UtilitySITAT.existsFase(codiceGara, codiceLotto, new Long(1),
                    faseEsecuz.intValue(), this.sqlManager);
                
	              	if (esisteFaseElencoImpreseInviate || (UtilitySITAT.isFaseAbilitata(codiceGara, codiceLotto, null, faseEsecuz.intValue(), sqlManager)
	              			&& UtilitySITAT.isFaseVisualizzabile(codiceGara, codiceLotto, faseEsecuz.intValue(), sqlManager))) {
	
	              		if (!esisteFaseElencoImpreseInviate || (esisteFaseElencoImpreseInviate && sovrascrivereDatiEsistenti)) {
	                  	// Cancellazione dei record in caso di sovrascrittura dei dati esistenti
	                  	if (esisteFaseElencoImpreseInviate) {
	                    	this.sqlManager.update("delete from W9IMPRESE where CODGARA=? and CODLOTT=?", 
	                    		new Object[] { codiceGara, codiceLotto });
	                    }
	                  	
	                  	// Record in W9FASI.
	                  	UtilitySITAT.istanziaFase(this.sqlManager, codiceGara, codiceLotto, faseEsecuz, new Long(1));
	                    
	                  	for (int imprese = 0; imprese < impresaInvitataPartecipante.getImpresaInvitataArray().length; imprese++) {
	                  		ImpresaInvitataType impresaInvitata = impresaInvitataPartecipante.getImpresaInvitataArray(imprese);
	                  		
	                  		DataColumn codGara = new DataColumn("W9IMPRESE.CODGARA",
	                      	  new JdbcParametro(JdbcParametro.TIPO_NUMERICO, codiceGara));
	                      DataColumn codLott = new DataColumn("W9IMPRESE.CODLOTT",
	                      	  new JdbcParametro(JdbcParametro.TIPO_NUMERICO, codiceLotto));
	                      DataColumn numImpr = new DataColumn("W9IMPRESE.NUM_IMPR",
	                      	  new JdbcParametro(JdbcParametro.TIPO_NUMERICO, new Long(imprese+1)));
	
	                      DataColumnContainer dccImpresa = new DataColumnContainer(
	                        		new DataColumn[] { codGara, codLott, numImpr } );
	
	                      dccImpresa.addColumn("W9IMPRESE.PARTECIP", new JdbcParametro(JdbcParametro.TIPO_NUMERICO,
	                        		new Long(impresaInvitata.getW9IMPARTEC().toString())));
	                      
	                      if (impresaInvitata.isSetW3IDTIPOA()) {
	            			    	dccImpresa.addColumn("W9IMPRESE.TIPOAGG", new JdbcParametro(JdbcParametro.TIPO_NUMERICO,
	                        		new Long(impresaInvitata.getW3IDTIPOA().toString())));
	            			    } else {
	            			    	dccImpresa.addColumn("W9IMPRESE.TIPOAGG", new JdbcParametro(JdbcParametro.TIPO_NUMERICO,
	                        		new Long(3)));
	            			    }
	                      
	                      if (impresaInvitata.isSetW3RUOLO()) {
	                      	dccImpresa.addColumn("W9IMPRESE.RUOLO", new JdbcParametro(JdbcParametro.TIPO_NUMERICO,
	                        		new Long(impresaInvitata.getW3RUOLO().toString())));
	                      }
	                      
	            			    if (impresaInvitata.isSetW3AGIDGRP()) {
	            			    	if (!StringUtils.equals("3", impresaInvitata.getW3IDTIPOA().toString())) {
	            			    		dccImpresa.addColumn("W9IMPRESE.NUM_RAGG", JdbcParametro.TIPO_NUMERICO, 
	            			    				new Long(impresaInvitata.getW3AGIDGRP()));
	            			    	}
	            			    } else {
	            			    	if (!StringUtils.equals("3", impresaInvitata.getW3IDTIPOA().toString())) {
	            			    		dccImpresa.addColumn("W9IMPRESE.NUM_RAGG", JdbcParametro.TIPO_NUMERICO, new Long(1));
	            			    	}
	            			    }
	                      
	                      // Gestione dell'impresa invitata/partecipante
	              			  boolean eseguiInsertImpresa = false;
	              			    
	              			  HashMap<String, Object> hmImpresa = UtilitySITAT.gestioneImpresa(codFiscaleStazAppaltante,
	              			  		impresaInvitata, eseguiInsertImpresa, this.sqlManager, this.geneManager);
	              	
	              			  String codImpresa = (String) hmImpresa.get("CODIMP");
	              			  dccImpresa.addColumn("W9IMPRESE.CODIMP", JdbcParametro.TIPO_TESTO, codImpresa);
	
	                      // Inserimento dell'impresa invitata/partecipante
	                      dccImpresa.insert("W9IMPRESE", this.sqlManager);
	                      
	                      UtilitySITAT.gestioneLegaliRappresentanti(codFiscaleStazAppaltante,
	                      		impresaInvitata, eseguiInsertImpresa, codImpresa,
	            			        this.sqlManager, this.genChiaviManager);
	                  	}
	                    
	                    if (esisteFaseElencoImpreseInviate) {
	                    	numeroLottiAggiornati++;
	                    	UtilitySITAT.aggiungiMsgSchedaA(hmResponseLotti, cigLotto, true, "Scheda aggiornata");
	                    } else {
	                    	numeroLottiImportati++;
	                    	UtilitySITAT.aggiungiMsgSchedaA(hmResponseLotti, cigLotto, true, "Scheda importata");
	                    }
	                    
	                    if (UtilitySITAT.isScritturaNoteAvvisiAttiva()) {
	                    	if (esisteFaseElencoImpreseInviate) {
	                    		UtilitySITAT.insertNoteAvvisi("W9IMPRESE", codiceGara.toString(), codiceLotto.toString(),
	                      				null, null, null, "CIG " + cigLotto + ": aggiornata fase 'Elenco imprese invitate/partecipanti'",
	                      				null, this.sqlManager);
	                    	} else {
	                  			UtilitySITAT.insertNoteAvvisi("W9IMPRESE", codiceGara.toString(), codiceLotto.toString(),
	                      				null, null, null, "CIG " + cigLotto + ": inserita fase 'Elenco imprese invitate/partecipanti'",
	                      				null, this.sqlManager);
	                    	}
	                    }
	                    
	                  } else {
	                    // Caso in cui in base dati esiste gia' la fase elenco imprese invitate/partecipanti
	                    // per il lotto ed il flag di sovrascrittura dei dati e' valorizzato a false.
	                    
	                    StringBuilder strLog = new StringBuilder(credenzialiUtente.getPrefissoLogger());
	                    strLog.append(" L'elenco imprese invitate del lotto con CIG=");
	                    strLog.append(cigLotto);
	                    strLog.append(" non e' stato importato perche' la fase gia' esiste nella base dati " +
	                        "e non la si vuole sovrascrivere.");
	                    logger.info(strLog.toString());
	                    
	                    numeroLottiNonImportati++;
	                    
	                    UtilitySITAT.aggiungiMsgSchedaA(hmResponseLotti, cigLotto, false,
	                    		ConfigManager.getValore("error.elencoImprese.esistente"));
	                  }
	                } else {
	                  // Fase non abilitata e non visibile. Il lotto non viene importato.
	                  StringBuilder strLog = new StringBuilder(credenzialiUtente.getPrefissoLogger());
	                  strLog.append(" L'aggiudicazione del lotto con CIG=");
	                  strLog.append(cigLotto);
	                  strLog.append(" non e' stato importato perche' non ha superato i controlli preliminari" +
	                  		" specifici della fase di aggiudicazione.");
	                  logger.info(strLog.toString());
	
	                  numeroLottiNonImportati++;
	                  
	                  UtilitySITAT.aggiungiMsgSchedaA(hmResponseLotti, cigLotto, false,
	                  		ConfigManager.getValore("error.elencoImprese.noVisibile"));
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
              }

              UtilitySITAT.preparaRisultatoMessaggio(result, sovrascrivereDatiEsistenti,
									hmResponseLotti, numeroLottiImportati,
									numeroLottiNonImportati, numeroLottiAggiornati);
            }
              
          } // Chiusura else del test validazione XML
        } else {
          // E' stato inviato un messaggio di test.
          StringBuilder strLog = new StringBuilder(credenzialiUtente.getPrefissoLogger());
          strLog.append(" E' stato inviato un messaggio di test. Tale messaggio non e' stato elaborato.'\n");
          strLog.append("Messaggio: ");
          strLog.append(elencoImpreseInvitate.getOggettoXML().toString());
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
  		logger.debug("istanziaElencoImpreseInvitate: fine metodo");
  		
  		logger.debug("Response : " + UtilitySITAT.messaggioLogEsteso(result));
  	}

    // MODIFICA TEMPORANEA -- DA CANCELLARE ---------------
    result.setError(UtilitySITAT.messaggioEsteso(result));
    // ----------------------------------------------------
  	
  	return result;
  }
}
