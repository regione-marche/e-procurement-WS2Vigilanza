package it.eldasoft.sil.vigilanza.bl;

import it.eldasoft.gene.bl.LoginManager;
import it.eldasoft.gene.bl.admin.UffintManager;
import it.eldasoft.gene.db.domain.UfficioIntestatario;
import it.eldasoft.gene.db.domain.admin.Account;
import it.eldasoft.sil.vigilanza.ws.beans.LoginType;
import it.eldasoft.sil.vigilanza.ws.beans.ResponseType;
import it.eldasoft.utils.sicurezza.CriptazioneException;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

/**
 * Classe per la verifica della login e password per accesso ai metodi del WS.
 * 
 * @author Luca.Giacomazzo
 */
public class CredenzialiManager {

  private static Logger logger = Logger.getLogger(CredenzialiManager.class);

  private LoginManager loginManager;

  private UffintManager uffintManager;
 
  /**
   * @param loginManager the loginManager to set
   */
  public void setLoginManager(it.eldasoft.gene.bl.LoginManager loginManager) {
    this.loginManager = loginManager;
  }

  /**
   * @param uffintManager the uffintManager to set
   */
  public void setUffintManager(UffintManager uffintManager) {
    this.uffintManager = uffintManager;
  }
  
  /**
   * Verifica di login, password e il codice fiscale della S.A.
   * 
   * @param login LoginType
   * @param codFiscStazioneAppaltante Codice fiscale della Stazione appaltante
   * @return Ritorna una HashMap con gli oggetti Credenziale e ResponseType, individuati rispettivamente
   *         con le chiavi "USER" e "RESULT"
   */
  public HashMap<String, Object> verificaCredenziali(LoginType login, String codFiscaleStazAppaltante) {
    
    ResponseType response = new ResponseType();
    response.setSuccess(true);
    
    if (logger.isDebugEnabled()) {
      logger.debug("verificaCredenziali: inizio metodo");
    }
        
    Credenziale credenzialiUtente = new Credenziale();
    
    // HashMap con oggetti utili nell'elaborazione del messaggio XML
    HashMap<String, Object> hm = new HashMap<String, Object>();

    // Verifica di login e password
    try {
      Account account = this.loginManager.getAccountByLoginEPassword(
          login.getLogin(), login.getPassword());
      
      if (account != null) {
        if (account.isNotAbilitato()) {

          // L'utente esiste nella USRSYS, ma e' disabilitato/disattivo.
          StringBuilder strBuilder = new StringBuilder(
              "L'utente indicato esiste nella base dati, ma non e' attivo (Login: ");
          strBuilder.append(login.getLogin());
          strBuilder.append(" - ");
          strBuilder.append("S.A. con C.F.: ");
          strBuilder.append(codFiscaleStazAppaltante);
          strBuilder.append(").");
          logger.error(strBuilder);
          
          response.setError("L'utente con login e password indicate non e' attivo.");
          response.setSuccess(false);
          
          account = null;
        } else {
          credenzialiUtente.setAccount(account);
        }
      } else {
        // L'utente indicato non esiste nella base dati.
        StringBuilder strBuilder = new StringBuilder(
            "L'utente indicato non esiste nella base dati (Login: ");
        strBuilder.append(login.getLogin());
        strBuilder.append(" - ");
        strBuilder.append("S.A. con C.F.: ");
        strBuilder.append(codFiscaleStazAppaltante);
        strBuilder.append(").");
        logger.error(strBuilder);
        
        response.setError("L'utente con login e password indicate non esiste nella base dati.");
        response.setSuccess(false);
      }
    } catch (CriptazioneException e) {
      logger.error("Errore nella criptazione di login e/o password nell'accedere al WS.", e);
      
      response.setError("Errore inatteso nella verifica di login e password di accesso. Si prega di riprovare.");
      response.setSuccess(false);
    } catch (Throwable t) {
      logger.error("Errore inatteso nel controllo di login e/o password per accedere al WS", t);
      
      response.setError("Errore inatteso nella verifica di login e password di accesso. Si prega di riprovare.");
      response.setSuccess(false);
    }

    if (response.isSuccess()) {
      
      // Stazione appaltante associata all'utente e con CF uguale a quello 
      // indicato nella testata del messaggio XML. 
      UfficioIntestatario stazioneAppaltante = null; 
      
      List<UfficioIntestatario> listaStazioniAppaltanti = this.getStazioneAppaltante(
          credenzialiUtente.getAccount().getIdAccount(), codFiscaleStazAppaltante);

      if (listaStazioniAppaltanti == null || (listaStazioniAppaltanti != null && listaStazioniAppaltanti.size() == 0)) {
        StringBuilder strBuilder = new StringBuilder("L'utente con SYSCON=");
        strBuilder.append(credenzialiUtente.getAccount().getIdAccount());
        strBuilder.append(" non e' associato alla stazione appaltante con CFEIN='");
        strBuilder.append(codFiscaleStazAppaltante);
        strBuilder.append("', oppure non esiste nessuna stazione appaltante con tale codice fiscale.");
        logger.error(strBuilder);
        
        response.setSuccess(false);
        response.setError("Non esiste alcuna stazione appaltante con il codice fiscale indicato associata all'utente.");
      } else if (listaStazioniAppaltanti != null && listaStazioniAppaltanti.size() > 1) {
        StringBuilder strBuilder = new StringBuilder("L'utente con SYSCON=");
        strBuilder.append(credenzialiUtente.getAccount().getIdAccount());
        strBuilder.append(" e' associato a piu' stazioni appaltanti con CFEIN='");
        strBuilder.append(codFiscaleStazAppaltante);
        strBuilder.append("'.");
        logger.error(strBuilder);
        
        response.setSuccess(false);
        response.setError("Esistono piu' stazioni appaltanti con il codice fiscale indicato associate all'utente.");
      } else {
        stazioneAppaltante = listaStazioniAppaltanti.get(0);
        credenzialiUtente.setStazioneAppaltante(stazioneAppaltante);
      }
      
      // Login superata con successo.
      
    }
    
    hm.put("RESULT", response);
    hm.put("USER", credenzialiUtente);
    
    if (logger.isDebugEnabled()) {
      logger.debug("verificaCredenziali: fine metodo");
    }
    
    return hm;
  }
  
  /**
   * Determinazione il nome della stazione appaltante a partire dalle stazioni
   * appaltanti associate all'utente e con codice fiscale uguale a quello specificato nel
   * messaggio XML.
   * 
   * @param idAccount Id Account
   * @param codiceFiscaleSA Codice Fiscale della Stazione appaltante specificato nel messaggio XML
   * @return Ritorna la lista delle stazioni appaltanti associate all'utente con SYSCON = <i>idAccount</i>
   *         il cui codice fiscale coincide con l'argomento <i>codiceFiscaleSA</i>, altrimenti torna null
   */
  private List<UfficioIntestatario> getStazioneAppaltante(int idAccount, String codiceFiscaleSA) {

    if (logger.isDebugEnabled()) {
      logger.debug("getStazioneAppaltante: inizio metodo");
    }
    
    List<UfficioIntestatario> stazioniAppaltanti = new ArrayList< UfficioIntestatario >();

    if (StringUtils.isNotEmpty(codiceFiscaleSA)) {
      List< ? > listaUffint = this.uffintManager.getUfficiIntestatariAccount(idAccount);

      if (listaUffint != null && listaUffint.size() > 0) {
        for (int i = 0; i < listaUffint.size(); i++) {
          UfficioIntestatario ufficioIntestatario = (UfficioIntestatario) listaUffint.get(i);
          
          if (StringUtils.isNotEmpty(ufficioIntestatario.getCodFiscale())
                && ufficioIntestatario.getCodFiscale().equalsIgnoreCase(codiceFiscaleSA)) {
              stazioniAppaltanti.add(ufficioIntestatario);
          }
        }
      }
    }
    
    if (logger.isDebugEnabled()) {
      logger.debug("getStazioneAppaltante: fine metodo");
    }
    
    if (stazioniAppaltanti.size() > 0) {
      return stazioniAppaltanti;
    } else {
      return null;
    }
  }
  
}
