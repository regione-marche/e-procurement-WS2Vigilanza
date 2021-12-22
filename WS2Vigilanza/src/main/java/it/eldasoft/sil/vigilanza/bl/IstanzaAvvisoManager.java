package it.eldasoft.sil.vigilanza.bl;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.db.datautils.DataColumn;
import it.eldasoft.gene.db.datautils.DataColumnContainer;
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.sil.vigilanza.beans.AvvisoType;
import it.eldasoft.sil.vigilanza.beans.RichiestaSincronaIstanzaAvvisoDocument;
import it.eldasoft.sil.vigilanza.utils.UtilitySITAT;
import it.eldasoft.sil.vigilanza.ws.beans.IstanzaOggettoType;
import it.eldasoft.sil.vigilanza.ws.beans.LoginType;
import it.eldasoft.sil.vigilanza.ws.beans.ResponseAvvisoType;
import it.eldasoft.sil.vigilanza.ws.beans.ResponseType;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlOptions;
import org.apache.xmlbeans.XmlValidationError;

/**
 * Classe per l'import dei dati generali della gara e dei lotti.
 * 
 * @author Luca.Giacomazzo
 */
public class IstanzaAvvisoManager {

  private static Logger logger = Logger.getLogger(IstanzaAvvisoManager.class);
  
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
   * Metodo per la creazione dell'avviso.
   * 
   * @param login LoginType
   * @param avviso IstanzaOggettoType
   * @return Ritorna l'oggetto ResponseType con l'esito dell'operazione
   * @throws XmlException
   * @throws GestoreException
   * @throws SQLException
   * @throws Throwable
   */
  public ResponseAvvisoType istanziaAvviso(LoginType login, IstanzaOggettoType avviso)
      throws XmlException, GestoreException, SQLException, Throwable  {
    ResponseType result = null;
    
    if (logger.isDebugEnabled()) {
      logger.debug("istanziaAvviso: inizio metodo");
      
      logger.debug("XML : " + avviso.getOggettoXML());
    }

    // Codice fiscale della Stazione appaltante
    String codFiscaleStazAppaltante = avviso.getTestata().getCFEIN();
    
    // Verifica di login, password e determinazione della S
    HashMap<String, Object> hm = this.credenzialiManager.verificaCredenziali(login, codFiscaleStazAppaltante);
    result = (ResponseType) hm.get("RESULT");
    
    ResponseAvvisoType resultAvviso = new ResponseAvvisoType();
    resultAvviso.setSuccess(result.isSuccess());
    resultAvviso.setError(result.getError());
    
    if (resultAvviso.isSuccess()) {
      Credenziale credenzialiUtente = (Credenziale) hm.get("USER");
      String xmlAvviso = avviso.getOggettoXML();
      
      try {
        RichiestaSincronaIstanzaAvvisoDocument istanzaAvvisoDocument =
          RichiestaSincronaIstanzaAvvisoDocument.Factory.parse(xmlAvviso);
        
        boolean isMessaggioDiTest = 
        	istanzaAvvisoDocument.getRichiestaSincronaIstanzaAvviso().isSetTest()
            && istanzaAvvisoDocument.getRichiestaSincronaIstanzaAvviso().getTest();
        
        if (!isMessaggioDiTest) {
        
          // si esegue il controllo sintattico del messaggio
          XmlOptions validationOptions = new XmlOptions();
          ArrayList<XmlValidationError> validationErrors = new ArrayList<XmlValidationError>();
          validationOptions.setErrorListener(validationErrors);
          boolean esitoCheckSintassi = istanzaAvvisoDocument.validate(validationOptions);
  
          if (!esitoCheckSintassi) {
            synchronized (validationErrors) {
              // Sincronizzazione dell'oggetto validationErrors per scrivere
              // sul log il dettaglio dell'errore su righe successive.  
              StringBuilder strLog = new StringBuilder(credenzialiUtente.getPrefissoLogger());
              strLog.append(" Errore nella validazione del messaggio ricevuto per la gestione di una istanza di Avviso.");
              for (int i = 0; i < validationErrors.size(); i++) {
                strLog.append("\n" + validationErrors.get(i).getMessage());
              }
              logger.error(strLog);
            }
            resultAvviso.setSuccess(false);
            resultAvviso.setError("Errore nella validazione del messaggio");
            
          } else {
            AvvisoType oggettoAvviso = istanzaAvvisoDocument.getRichiestaSincronaIstanzaAvviso().getAvviso();
            
            // Controllo codice sistema alimentante: deve essere diverso da 1, perche' 
            // tale valore identifica l'applicativo Sitat/Vigilanza.
            if (oggettoAvviso.getW9PACODSIST() != 1) {

              boolean sovrascrivereDatiEsistenti = false;
              if (avviso.getTestata().getSOVRASCR() != null) {
              	sovrascrivereDatiEsistenti = avviso.getTestata().getSOVRASCR().booleanValue();

              	boolean isAvvisoEsistente = UtilitySITAT.existsAvviso(new Long(oggettoAvviso.getW3PAVVISOID()),
              			new Long(oggettoAvviso.getW9PACODSIST()), credenzialiUtente.getStazioneAppaltante().getCodice(),
              			this.sqlManager);
                
              	if (!sovrascrivereDatiEsistenti && isAvvisoEsistente) {
              		StringBuilder strLog = new StringBuilder(credenzialiUtente.getPrefissoLogger());
                  strLog.append(" Avviso con IDAVVISO='");
                  strLog.append(oggettoAvviso.getW3PAVVISOID());
                  strLog.append("' gia' esistente nella base dati");
                  logger.error(strLog);
              		
                	resultAvviso.setSuccess(false);
                	resultAvviso.setError("Avviso non creato perche' gia' esistente nell'archivio di destinazione");
                } else {
                	this.istanziaAvviso(credenzialiUtente, oggettoAvviso, isAvvisoEsistente, resultAvviso);
                }
              }
            	
            } else {
            	logger.error("Errore ricezione di un avviso con Codice del sistema alimentante non consentito (CODSISTEMA <> 1)");
            	
            	resultAvviso.setSuccess(false);
            	resultAvviso.setError("Avviso non importato perche' usato un Codice di sistema alimentante non consentito");
            }
          }
        } else {
          // E' stato inviato un messaggio di test.
          StringBuilder strLog = new StringBuilder(credenzialiUtente.getPrefissoLogger());
          strLog.append(" E' stato inviato un messaggio di test. Tale messaggio non e' stato elaborato.'\n");
          strLog.append("Messaggio: ");
          strLog.append(avviso.toString());
          logger.info(strLog);
          
          resultAvviso.setSuccess(true);
          resultAvviso.setError("E' stato inviato un messaggio di test: messaggio non elaborato.");
          
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
          + resultAvviso.getError());
    }

    if (logger.isDebugEnabled()) {
      logger.debug("istanziaAvviso: fine metodo");
      
      logger.debug("Response : " + UtilitySITAT.messaggioLogEsteso(result));
    }
    
    // MODIFICA TEMPORANEA -- DA CANCELLARE ---------------
    result.setError(UtilitySITAT.messaggioEsteso(result));
    // ----------------------------------------------------
    
    return resultAvviso;
  }

  /**
   * Inserimento / aggiornamento dell'avviso.
   * 
   * @param credenzialiUtente
   * @param oggettoAvviso
   * @param isAvvisoEsistente
   * @return
   * @throws GestoreException
   * @throws SQLException
   */
	private void istanziaAvviso(Credenziale credenzialiUtente, AvvisoType oggettoAvviso,
			boolean isAvvisoEsistente, ResponseAvvisoType resultAvviso) throws GestoreException, SQLException {

		// tabella AVVISO
		DataColumn idAvviso = new DataColumn("AVVISO.IDAVVISO", 
		    new JdbcParametro(JdbcParametro.TIPO_NUMERICO,
		    		new Long(oggettoAvviso.getW3PAVVISOID())));
		DataColumn codeinAvviso = new DataColumn("AVVISO.CODEIN", 
				new JdbcParametro(JdbcParametro.TIPO_TESTO, 
				  credenzialiUtente.getStazioneAppaltante().getCodice()));
		DataColumn codiceSitemaAvviso = new DataColumn("AVVISO.CODSISTEMA", 
				new JdbcParametro(JdbcParametro.TIPO_NUMERICO,
						new Long(oggettoAvviso.getW9PACODSIST())));
		idAvviso.setChiave(true);
		codeinAvviso.setChiave(true);
		codiceSitemaAvviso.setChiave(true);
		
		DataColumnContainer dccAvviso = new DataColumnContainer(
				new DataColumn[]{ idAvviso, codeinAvviso, codiceSitemaAvviso });
		
		// CIG	W3PACIG	CIG	ALFANUMERICO (10)	
		if (oggettoAvviso.isSetW3PACIG()) {
			dccAvviso.addColumn("AVVISO.CIG", JdbcParametro.TIPO_TESTO,
					oggettoAvviso.getW3PACIG().toString());
		}
		
		// TIPOAVV	W3PATAVVI	Tipologia avviso	TABELLATO W3996	
		if (oggettoAvviso.isSetW3PATAVVI()) {
			dccAvviso.addColumn("AVVISO.TIPOAVV", JdbcParametro.TIPO_NUMERICO,
					Long.parseLong(oggettoAvviso.getW3PATAVVI().toString()));
		}
		
		// DATAAVV	W3PADATA	Data avviso	DATA	
		if (oggettoAvviso.isSetW3PADATA()) {
			dccAvviso.addColumn("AVVISO.DATAAVV", JdbcParametro.TIPO_DATA,
					oggettoAvviso.getW3PADATA().getTime());
		}
		
		// DESCRI	W3PADESCRI	Descrizione avviso	ALFANUMERICO (500)
		if (oggettoAvviso.isSetW3PADESCRI()) {
			dccAvviso.addColumn("AVVISO.DESCRI", JdbcParametro.TIPO_TESTO,
					oggettoAvviso.getW3PADESCRI().toString());
		}
		
		// FILEALLEGATO	W9PAFILE	File allegato	BLOB	X
		ByteArrayOutputStream fileAllegato = new ByteArrayOutputStream();
    try {
      fileAllegato.write(oggettoAvviso.getW9PAFILE());
    } catch (IOException e) {
      throw new GestoreException(
          "Errore durante la lettura del file allegato presente nella richiesta XML", null, e);
    }
    dccAvviso.addColumn("AVVISO.FILE_ALLEGATO", JdbcParametro.TIPO_BINARIO, fileAllegato);

		if (!isAvvisoEsistente) {
	  	// Insert in AVVISO
    	dccAvviso.insert("AVVISO", this.sqlManager);
    	
    	if (UtilitySITAT.isScritturaNoteAvvisiAttiva()) {
    		UtilitySITAT.insertNoteAvvisi("AVVISO", credenzialiUtente.getStazioneAppaltante().getCodice(),
    				"" + oggettoAvviso.getW3PAVVISOID(), "" + oggettoAvviso.getW9PACODSIST(), null, null, 
    				"COEDIN " + credenzialiUtente.getStazioneAppaltante().getCodice()
    					+ ": inserito nuovo 'AVVISO' con idavviso = " + oggettoAvviso.getW3PAVVISOID(), null,
    					this.sqlManager);
    		
      }
    	resultAvviso.setSuccess(true);
    } else {
      DataColumnContainer dccAvvisoDB = new DataColumnContainer(this.sqlManager, "AVVISO", 
          "select CODEIN, IDAVVISO, CODSISTEMA, CODGARA, CODLOTT, TIPOAVV, DATAAVV, DESCRI, CIG"
              + " from AVVISO where CODEIN=? and IDAVVISO=? and CODSISTEMA=?",
          new Object[]{ credenzialiUtente.getStazioneAppaltante().getCodice(), 
      		oggettoAvviso.getW3PAVVISOID(), oggettoAvviso.getW9PACODSIST()});
      
      Iterator<Entry<String, DataColumn>> iterInizioDB = dccAvvisoDB.getColonne().entrySet().iterator();
      while (iterInizioDB.hasNext()) {
      	Entry<String, DataColumn> entry = iterInizioDB.next(); 
        String nomeCampo = entry.getKey();
        if (dccAvviso.isColumn(nomeCampo)) {
          dccAvvisoDB.setValue(nomeCampo, dccAvviso.getColumn(nomeCampo).getValue().getValue());
        } else {
          dccAvvisoDB.setValue(nomeCampo, null);
        }
      }
      
      if (dccAvvisoDB.isModifiedTable("AVVISO")) {
      	dccAvvisoDB.getColumn("AVVISO.CODEIN").setChiave(true);
      	dccAvvisoDB.getColumn("AVVISO.IDAVVISO").setChiave(true);
      	dccAvvisoDB.getColumn("AVVISO.CODSISTEMA").setChiave(true);
      	
      	dccAvvisoDB.update("AVVISO", this.sqlManager);
      	
      	if (UtilitySITAT.isScritturaNoteAvvisiAttiva()) {
      		UtilitySITAT.insertNoteAvvisi("AVVISO", credenzialiUtente.getStazioneAppaltante().getCodice(),
      				"" + oggettoAvviso.getW3PAVVISOID(), "" + oggettoAvviso.getW9PACODSIST(), null, null, 
      				"Avviso " + oggettoAvviso.getW3PAVVISOID() + ": modificato 'Avviso'", null, this.sqlManager);
      	}
      	resultAvviso.setSuccess(true);
      }
    }
	}
	
}
