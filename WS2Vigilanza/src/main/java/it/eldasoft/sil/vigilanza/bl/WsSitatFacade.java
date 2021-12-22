package it.eldasoft.sil.vigilanza.bl;

import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.sil.vigilanza.commons.WSVigilanzaException;
import it.eldasoft.sil.vigilanza.ws.beans.IstanzaOggettoType;
import it.eldasoft.sil.vigilanza.ws.beans.LoginType;
import it.eldasoft.sil.vigilanza.ws.beans.ResponseAvvisoType;
import it.eldasoft.sil.vigilanza.ws.beans.ResponsePubblicazioneType;
import it.eldasoft.sil.vigilanza.ws.beans.ResponseType;


import java.sql.SQLException;

import org.apache.log4j.Logger;
import org.apache.xmlbeans.XmlException;

public class WsSitatFacade {

	private static Logger logger = Logger.getLogger(WsSitatFacade.class);
	
  private IstanzaGaraManager istanzaGaraManager;
  
  private IstanzaAggiudicazioneManager istanzaAggiudicazioneManager;
  
  private IstanzaEsitoManager istanzaEsitoManager;
  
  private IstanzaContrattoManager istanzaContrattoManager;
  
  private IstanzaInizioManager istanzaInizioManager;

  private IstanzaAvanzamentoManager istanzaAvanzamentoManager;

  private IstanzaSospensioneManager istanzaSospensioneManager;

  private IstanzaVarianteManager istanzaVarianteManager;

  private IstanzaSubappaltoManager istanzaSubappaltoManager;

  private IstanzaConclusioneManager istanzaConclusioneManager;

  private IstanzaCollaudoManager istanzaCollaudoManager;
  
  private IstanzaElencoImpreseInvitateManager istanzaElencoImpreseInvitateManager;
  
  private IstanzaAvvisoManager istanzaAvvisoManager;

  private IstanzaPubblicazioneDocumentiManager istanzaPubblicazioneDocumentiManager;

  /**
   * @param istanzaGaraManager the istanzaGaraManager to set
   */
  public void setIstanzaGaraManager(IstanzaGaraManager istanzaGaraManager) {
    this.istanzaGaraManager = istanzaGaraManager;
  }

  /**
   * @param istanzaAggiudicazioneManager the istanzaAggiudicazioneManager to set
   */
  public void setIstanzaAggiudicazioneManager(
      IstanzaAggiudicazioneManager istanzaAggiudicazioneManager) {
    this.istanzaAggiudicazioneManager = istanzaAggiudicazioneManager;
  }

  /**
   * @param istanzaEsitoManager the istanzaEsitoManager to set
   */
  public void setIstanzaEsitoManager(IstanzaEsitoManager istanzaEsitoManager) {
    this.istanzaEsitoManager = istanzaEsitoManager;
  }

  /**
   * @param istanzaContrattoManager the istanzaContrattoManager to set
   */
  public void setIstanzaContrattoManager(
      IstanzaContrattoManager istanzaContrattoManager) {
    this.istanzaContrattoManager = istanzaContrattoManager;
  }
  
  /**
   * @param istanzaInizioManager the istanzaInizioManager to set
   */
  public void setIstanzaInizioManager(IstanzaInizioManager istanzaInizioManager) {
    this.istanzaInizioManager = istanzaInizioManager;
  }

  /**
   * @param istanzaAvanzamentoManager the istanzaAvanzamentoManager to set
   */
  public void setIstanzaAvanzamentoManager(IstanzaAvanzamentoManager istanzaAvanzamentoManager) {
    this.istanzaAvanzamentoManager = istanzaAvanzamentoManager;
  }

  /**
   * @param istanzaSospensioneManager the istanzaSospensioneManager to set
   */
  public void setIstanzaSospensioneManager(IstanzaSospensioneManager istanzaSospensioneManager) {
    this.istanzaSospensioneManager = istanzaSospensioneManager;
  }

  /**
   * @param istanzaVarianteManager the istanzaVarianteManager to set
   */
  public void setIstanzaVarianteManager(IstanzaVarianteManager istanzaVarianteManager) {
    this.istanzaVarianteManager = istanzaVarianteManager;
  }

  /**
   * @param istanzaSubappaltoManager the istanzaSubappaltoManager to set
   */
  public void setIstanzaSubappaltoManager(IstanzaSubappaltoManager istanzaSubappaltoManager) {
    this.istanzaSubappaltoManager = istanzaSubappaltoManager;
  }

  /**
   * @param istanzaConclusioneManager the istanzaConclusioneManager to set
   */
  public void setIstanzaConclusioneManager(IstanzaConclusioneManager istanzaConclusioneManager) {
    this.istanzaConclusioneManager = istanzaConclusioneManager;
  }

  /**
   * @param istanzaCollaudoManager the istanzaCollaudoManager to set
   */
  public void setIstanzaCollaudoManager(IstanzaCollaudoManager istanzaCollaudoManager) {
    this.istanzaCollaudoManager = istanzaCollaudoManager;
  }

  public void setIstanzaElencoImpreseInvitateManager(IstanzaElencoImpreseInvitateManager istanzaElencoImpreseInvitateManager) {
  	this.istanzaElencoImpreseInvitateManager = istanzaElencoImpreseInvitateManager;
  }
  
  /**
   * @param istanzaAvvisoManager the istanzaAvvisoManager to set
   */
  public void setIstanzaAvvisoManager(IstanzaAvvisoManager istanzaAvvisoManager) {
    this.istanzaAvvisoManager = istanzaAvvisoManager;
  }
  
  public void setIstanzaPubblicazioneDocumentiManager(IstanzaPubblicazioneDocumentiManager istanzaPubblicazioneDocumentiManager) {
    this.istanzaPubblicazioneDocumentiManager = istanzaPubblicazioneDocumentiManager;
  }
  
  public ResponseType istanziaGara(LoginType login, IstanzaOggettoType garaLotti) {
    try {

      return this.istanzaGaraManager.istanziaGaraLotti(login, garaLotti);
    
    } catch (XmlException e) {
    	logger.error("Impossibile interpretare i dati inviati (Parsing XML)", e);
    	
    	ResponseType result = new ResponseType();
      result.setSuccess(false);
      result.setError("Impossibile interpretare i dati inviati (Parsing XML)");
      return result;
      
    } catch (GestoreException g) {
    	logger.error("Errore inatteso nella preparazione dei dati della gara e dei lotti", g);
    	
      ResponseType result = new ResponseType();
      result.setSuccess(false);
      result.setError("Errore inatteso nella preparazione dei dati della gara e dei lotti");
      return result;

    } catch (SQLException sql) {
    	logger.error("Errore inatteso nel salvataggio dei dati della gara e dei lotti", sql);
    	
    	ResponseType result = new ResponseType();
      
      result.setSuccess(false);
      result.setError("Errore inatteso nel salvataggio dei dati della gara e dei lotti");
      return result;

    } catch (WSVigilanzaException wsexp) {
    	logger.error("Errore inaspettato nell'esecuzione della procedura", wsexp);
    	
    	ResponseType result = new ResponseType();
      result.setSuccess(false);
      result.setError(wsexp.getMessage());
      return result;
      
    } catch (Throwable t) {
    	logger.error("Errore inaspettato nell'esecuzione della procedura", t);
    	
      ResponseType result = new ResponseType();
      result.setSuccess(false);
      result.setError("Errore inaspettato nell'esecuzione della procedura");
      return result;
    }
  }
  
  public ResponseType istanziaAggiudicazione(LoginType login, IstanzaOggettoType aggiudicazione) {
    try {
      
      return this.istanzaAggiudicazioneManager.istanziaAggiudicazione(login, aggiudicazione);

    } catch (XmlException e) {
      ResponseType result = new ResponseType();
      result.setSuccess(false);
      result.setError("Impossibile interpretare i dati inviati (Parsing XML)");
      return result;
      
    } catch (GestoreException g) {
      ResponseType result = new ResponseType();
      result.setSuccess(false);
      result.setError("Errore inatteso nella preparazione dei dati dell'aggiudicazione");
      return result;

    } catch (SQLException sql) {
      ResponseType result = new ResponseType();
      result.setSuccess(false);
      result.setError("Errore inatteso nel salvataggio dei dati dell'aggiudicazione");
      return result;

    } catch (WSVigilanzaException wsexp) { 
    	ResponseType result = new ResponseType();
      result.setSuccess(false);
      result.setError(wsexp.getMessage());
      return result;
      
    } catch (Throwable t) {
      ResponseType result = new ResponseType();
      result.setSuccess(false);
      result.setError("Errore inaspettato nell'esecuzione della procedura");
      return result;

    }
  }
  
  public ResponseType istanziaEsito(LoginType login, IstanzaOggettoType esito) {
    try {
      
      return this.istanzaEsitoManager.istanziaEsito(login, esito);

    } catch (XmlException e) {
      ResponseType result = new ResponseType();
      result.setSuccess(false);
      result.setError("Impossibile interpretare i dati inviati (Parsing XML)");
  
      return result;
    } catch (GestoreException g) {
      ResponseType result = new ResponseType();
      result.setSuccess(false);
      result.setError("Errore inatteso nella preparazione dei dati dell'esito");
      return result;

    } catch (SQLException sql) {
      ResponseType result = new ResponseType();
      result.setSuccess(false);
      result.setError("Errore inatteso nel salvataggio dei dati dell'esito");
      return result;

    } catch (WSVigilanzaException wsexp) { 
    	ResponseType result = new ResponseType();
      result.setSuccess(false);
      result.setError(wsexp.getMessage());
      return result;
      
    } catch (Throwable t) {
      ResponseType result = new ResponseType();
      result.setSuccess(false);
      result.setError("Errore inaspettato nell'esecuzione della procedura");
      return result;

    }
  }
  
  public ResponseType istanziaContratto(LoginType login, IstanzaOggettoType contratto) {
    try {
      
      return this.istanzaContrattoManager.istanziaContratto(login, contratto);
      
    } catch (XmlException e) {
      ResponseType result = new ResponseType();
      result.setSuccess(false);
      result.setError("Impossibile interpretare i dati inviati (Parsing XML)");
  
      return result;
    } catch (GestoreException g) {
      ResponseType result = new ResponseType();
      result.setSuccess(false);
      result.setError("Errore inatteso nella preparazione dei dati del contratto");
      return result;

    } catch (SQLException sql) {
      ResponseType result = new ResponseType();
      result.setSuccess(false);
      result.setError("Errore inatteso nel salvataggio dei dati del contratto");
      return result;

    } catch (WSVigilanzaException wsexp) { 
    	ResponseType result = new ResponseType();
      result.setSuccess(false);
      result.setError(wsexp.getMessage());
      return result;

    } catch (Throwable t) {
      ResponseType result = new ResponseType();
      result.setSuccess(false);
      result.setError("Errore inaspettato nell'esecuzione della procedura");
      return result;

    }
  }
  
  public ResponseType istanziaInizio(LoginType login, IstanzaOggettoType inizio) {
    try {
      
      return this.istanzaInizioManager.istanziaInizio(login, inizio);
      
    } catch (XmlException e) {
      ResponseType result = new ResponseType();
      result.setSuccess(false);
      result.setError("Impossibile interpretare i dati inviati (Parsing XML)");
  
      return result;
    } catch (GestoreException g) {
      ResponseType result = new ResponseType();
      result.setSuccess(false);
      result.setError("Errore inatteso nella preparazione dei dati dell' inizio contratto");
      return result;

    } catch (SQLException sql) {
      ResponseType result = new ResponseType();
      result.setSuccess(false);
      result.setError("Errore inatteso nel salvataggio dei dati dell'inizio contratto");
      return result;
    
    } catch (WSVigilanzaException wsexp) { 
    	ResponseType result = new ResponseType();
      result.setSuccess(false);
      result.setError(wsexp.getMessage());
      return result;
      
    } catch (Throwable t) {
      ResponseType result = new ResponseType();
      result.setSuccess(false);
      result.setError("Errore inaspettato nell'esecuzione della procedura");
      return result;

    }
  }

  public ResponseType istanziaAvanzamento(LoginType login, IstanzaOggettoType avanzamento) {
    try {
      
      return this.istanzaAvanzamentoManager.istanziaAvanzamento(login, avanzamento);
      
    } catch (XmlException e) {
      ResponseType result = new ResponseType();
      result.setSuccess(false);
      result.setError("Impossibile interpretare i dati inviati (Parsing XML)");
  
      return result;
    } catch (GestoreException g) {
      ResponseType result = new ResponseType();
      result.setSuccess(false);
      result.setError("Errore inatteso nella preparazione dei dati dell'avanzamento contratto");
      return result;

    } catch (SQLException sql) {
      ResponseType result = new ResponseType();
      result.setSuccess(false);
      result.setError("Errore inatteso nel salvataggio dei dati dell'avanzamento contratto");
      return result;

    } catch (WSVigilanzaException wsexp) { 
    	ResponseType result = new ResponseType();
      result.setSuccess(false);
      result.setError(wsexp.getMessage());
      return result;
      
    } catch (Throwable t) {
      ResponseType result = new ResponseType();
      result.setSuccess(false);
      result.setError("Errore inaspettato nell'esecuzione della procedura");

      return result;

    }
  }

  public ResponseType istanziaSospensione(LoginType login, IstanzaOggettoType sospensione) {
    try {
      
      return this.istanzaSospensioneManager.istanziaSospensione(login, sospensione);
      
    } catch (XmlException e) {
      ResponseType result = new ResponseType();
      result.setSuccess(false);
      result.setError("Impossibile interpretare i dati inviati (Parsing XML)");
  
      return result;
    } catch (GestoreException g) {
      ResponseType result = new ResponseType();
      result.setSuccess(false);
      result.setError("Errore inatteso nella preparazione dei dati della sospensione del contratto");
      return result;

    } catch (SQLException sql) {
      ResponseType result = new ResponseType();
      result.setSuccess(false);
      result.setError("Errore inatteso nel salvataggio dei dati della sospensione del contratto");
      return result;

    } catch (WSVigilanzaException wsexp) { 
    	ResponseType result = new ResponseType();
      result.setSuccess(false);
      result.setError(wsexp.getMessage());
      return result;
      
    } catch (Throwable t) {
      ResponseType result = new ResponseType();
      result.setSuccess(false);
      result.setError("Errore inaspettato nell'esecuzione della procedura");
      return result;

    }
  }
  
  public ResponseType istanziaVariante(LoginType login, IstanzaOggettoType variante) {
    try {
      
      return this.istanzaVarianteManager.istanziaVariante(login, variante);
      
    } catch (XmlException e) {
      ResponseType result = new ResponseType();
      result.setSuccess(false);
      result.setError("Impossibile interpretare i dati inviati (Parsing XML)");
  
      return result;
    } catch (GestoreException g) {
      ResponseType result = new ResponseType();
      result.setSuccess(false);
      result.setError("Errore inatteso nella preparazione dei dati della variante del contratto");
      return result;

    } catch (SQLException sql) {
      ResponseType result = new ResponseType();
      result.setSuccess(false);
      result.setError("Errore inatteso nel salvataggio dei dati della variante del contratto");
      return result;

    } catch (WSVigilanzaException wsexp) { 
    	ResponseType result = new ResponseType();
      result.setSuccess(false);
      result.setError(wsexp.getMessage());
      return result;
      
    } catch (Throwable t) {
      ResponseType result = new ResponseType();
      result.setSuccess(false);
      result.setError("Errore inaspettato nell'esecuzione della procedura");
      return result;

    }
  }
  
  public ResponseType istanziaSubappalto(LoginType login, IstanzaOggettoType subappalto) {
    try {
      
      return this.istanzaSubappaltoManager.istanziaSubappalto(login, subappalto);
      
    } catch (XmlException e) {
      ResponseType result = new ResponseType();
      result.setSuccess(false);
      result.setError("Impossibile interpretare i dati inviati (Parsing XML)");
  
      return result;
    } catch (GestoreException g) {
      ResponseType result = new ResponseType();
      result.setSuccess(false);
      result.setError("Errore inatteso nella preparazione dei dati del subappalto");
      return result;

    } catch (SQLException sql) {
      ResponseType result = new ResponseType();
      result.setSuccess(false);
      result.setError("Errore inatteso nel salvataggio dei dati del subappalto");
      return result;

    } catch (WSVigilanzaException wsexp) { 
    	ResponseType result = new ResponseType();
      result.setSuccess(false);
      result.setError(wsexp.getMessage());
      return result;
      
    } catch (Throwable t) {
      ResponseType result = new ResponseType();
      result.setSuccess(false);
      result.setError("Errore inaspettato nell'esecuzione della procedura");
      return result;

    }
  }

  public ResponseType istanziaConclusione(LoginType login, IstanzaOggettoType conclusione) {
    try {
      
      return this.istanzaConclusioneManager.istanziaConclusione(login, conclusione);
      
    } catch (XmlException e) {
      ResponseType result = new ResponseType();
      result.setSuccess(false);
      result.setError("Impossibile interpretare i dati inviati (Parsing XML)");
  
      return result;
    } catch (GestoreException g) {
      ResponseType result = new ResponseType();
      result.setSuccess(false);
      result.setError("Errore inatteso nella preparazione dei dati della conclusione del contratto");
      return result;

    } catch (SQLException sql) {
      ResponseType result = new ResponseType();
      result.setSuccess(false);
      result.setError("Errore inatteso nel salvataggio dei dati della conclusione del contratto");
      return result;

    } catch (WSVigilanzaException wsexp) { 
    	ResponseType result = new ResponseType();
      result.setSuccess(false);
      result.setError(wsexp.getMessage());
      return result;
      
    } catch (Throwable t) {
      ResponseType result = new ResponseType();
      result.setSuccess(false);
      result.setError("Errore inaspettato nell'esecuzione della procedura");
      return result;

    }
  }
  
  public ResponseType istanziaCollaudo(LoginType login, IstanzaOggettoType collaudo) {
    try {
      
      return this.istanzaCollaudoManager.istanziaCollaudo(login, collaudo);
      
    } catch (XmlException e) {
      ResponseType result = new ResponseType();
      result.setSuccess(false);
      result.setError("Impossibile interpretare i dati inviati (Parsing XML)");
  
      return result;
    } catch (GestoreException g) {
      ResponseType result = new ResponseType();
      result.setSuccess(false);
      result.setError("Errore inatteso nella preparazione dei dati del collaudo");
      return result;

    } catch (SQLException sql) {
      ResponseType result = new ResponseType();
      result.setSuccess(false);
      result.setError("Errore inatteso nel salvataggio dei dati del collaudo");
      return result;

    } catch (WSVigilanzaException wsexp) { 
    	ResponseType result = new ResponseType();
      result.setSuccess(false);
      result.setError(wsexp.getMessage());
      return result;
      
    } catch (Throwable t) {
      ResponseType result = new ResponseType();
      result.setSuccess(false);
      result.setError("Errore inaspettato nell'esecuzione della procedura");
      return result;

    }
  }
  
  public ResponseType istanziaElencoImpreseInvitate(LoginType login, IstanzaOggettoType elencoImpreseInvitate) {
    try {
      
      return this.istanzaElencoImpreseInvitateManager.istanziaElencoImpreseInvitate(login, elencoImpreseInvitate);
      
    } catch (XmlException e) {
      ResponseType result = new ResponseType();
      result.setSuccess(false);
      result.setError("Impossibile interpretare i dati inviati (Parsing XML)");
  
      return result;
    } catch (GestoreException g) {
      ResponseType result = new ResponseType();
      result.setSuccess(false);
      result.setError("Errore inatteso nella preparazione dei dati dell'elenco imprese invitate");
      return result;

    } catch (SQLException sql) {
      ResponseType result = new ResponseType();
      result.setSuccess(false);
      result.setError("Errore inatteso nel salvataggio dei dati dell'elenco imprese invitate");
      return result;

    } catch (WSVigilanzaException wsexp) { 
    	ResponseType result = new ResponseType();
      result.setSuccess(false);
      result.setError(wsexp.getMessage());
      return result;
      
    } catch (Throwable t) {
      ResponseType result = new ResponseType();
      result.setSuccess(false);
      result.setError("Errore inaspettato nell'esecuzione della procedura");
      return result;

    }
  }
  
	public ResponseAvvisoType istanziaAvviso(LoginType login, IstanzaOggettoType avviso) {
		try {

			return this.istanzaAvvisoManager.istanziaAvviso(login, avviso);

		} catch (XmlException e) {
			ResponseAvvisoType result = new ResponseAvvisoType();
			result.setSuccess(false);
			result.setError("Impossibile interpretare i dati inviati (Parsing XML)");

			return result;
		} catch (GestoreException g) {
			ResponseAvvisoType result = new ResponseAvvisoType();
			result.setSuccess(false);
			result.setError("Errore inatteso nella preparazione dei dati dell'avviso");
			return result;

		} catch (SQLException sql) {
			ResponseAvvisoType result = new ResponseAvvisoType();
			result.setSuccess(false);
			result.setError("Errore inatteso nel salvataggio dei dati dell'avviso");
			return result;

		} catch (WSVigilanzaException wsexp) {
			ResponseAvvisoType result = new ResponseAvvisoType();
			result.setSuccess(false);
			result.setError(wsexp.getMessage());
			return result;

		} catch (Throwable t) {
			ResponseAvvisoType result = new ResponseAvvisoType();
			result.setSuccess(false);
			result.setError("Errore inaspettato nell'esecuzione della procedura");
			return result;
		}
	}

	public ResponsePubblicazioneType istanziaPubblicazioneDocumenti(LoginType login, IstanzaOggettoType pubblicazioneDocumenti) {
		try {

			return this.istanzaPubblicazioneDocumentiManager.istanziaPubblicazioneDocumenti(login, pubblicazioneDocumenti);

		} catch (XmlException e) {
			ResponsePubblicazioneType result = new ResponsePubblicazioneType();
			result.setSuccess(false);
			result.setError("Impossibile interpretare i dati inviati (Parsing XML)");

			return result;
		} catch (GestoreException g) {
			ResponsePubblicazioneType result = new ResponsePubblicazioneType();
			result.setSuccess(false);
			result
					.setError("Errore inatteso nella preparazione dei dati della pubblicazione documenti");
			return result;

		} catch (SQLException sql) {
			ResponsePubblicazioneType result = new ResponsePubblicazioneType();
			result.setSuccess(false);
			result.setError("Errore inatteso nel salvataggio dei dati della pubblicazione documenti");
			return result;

		} catch (WSVigilanzaException wsexp) {
			ResponsePubblicazioneType result = new ResponsePubblicazioneType();
			result.setSuccess(false);
			result.setError(wsexp.getMessage());
			return result;

		} catch (Throwable t) {
			ResponsePubblicazioneType result = new ResponsePubblicazioneType();
			result.setSuccess(false);
			result.setError("Errore inaspettato nell'esecuzione della procedura");
			return result;
		}
	}
	
}
