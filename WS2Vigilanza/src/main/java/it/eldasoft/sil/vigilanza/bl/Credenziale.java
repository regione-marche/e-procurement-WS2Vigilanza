package it.eldasoft.sil.vigilanza.bl;

import org.apache.commons.lang.StringUtils;

import it.eldasoft.gene.db.domain.UfficioIntestatario;
import it.eldasoft.gene.db.domain.admin.Account;

/**
 * Bean contenente oggetti valorizzati al momento dell'accesso al WS
 * ed utili alla gestione dell'intero messaggio XML.
 * 
 * @author Luca.Giacomazzo
 */
public class Credenziale {

  /**
   *  Utente che ha fatto accesso al WS (RUP).
   */
  private Account account;
  
  /**
   * Stazione appaltante associata al RUP.
   */
  private UfficioIntestatario stazioneAppaltante;
  
  /**
   * Prefisso da usare nel log per individuare a quella RUP e quale Stazione appaltante
   * si riferisce un messaggio di errore scritto nel log.
   */
  private String prefissoLogger;

  /**
   * @return the account
   */
  public Account getAccount() {
    return account;
  }

  /**
   * @param account the account to set
   */
  public void setAccount(Account account) {
    this.account = account;
    this.setPrefissoLogger();
  }

  /**
   * @return the stazioneAppaltante
   */
  public UfficioIntestatario getStazioneAppaltante() {
    return stazioneAppaltante;
  }

  /**
   * @param stazioneAppaltante the stazioneAppaltante to set
   */
  public void setStazioneAppaltante(UfficioIntestatario stazioneAppaltante) {
    this.stazioneAppaltante = stazioneAppaltante;
    this.setPrefissoLogger();
  }

  /**
   * @return the prefissoLogger
   */
  public String getPrefissoLogger() {
    return prefissoLogger;
  }
  
  /**
   * Set prefissoLogger
   */
  private void setPrefissoLogger() {

    // Prefisso per i messaggi di log, per poter determinare utente e stazione 
    // appaltante che hanno avuto un certo problema.
    
    StringBuffer prefissoLogger = new StringBuffer("");

    if (StringUtils.isEmpty(this.prefissoLogger)) {
      if (this.account != null) {
        prefissoLogger.append("Utente: ");
        prefissoLogger.append(account.getLogin());
        prefissoLogger.append(" (SYSCON=");
        prefissoLogger.append(account.getIdAccount());
        prefissoLogger.append("). ");
      }
      
      if (stazioneAppaltante != null) {
        prefissoLogger.append("S.A.: "); 
        if (StringUtils.isNotEmpty(stazioneAppaltante.getNome())) {
          prefissoLogger.append(stazioneAppaltante.getNome());
        } else {
          prefissoLogger.append(" ");
        }
        prefissoLogger.append(" (CFEIN=");
        prefissoLogger.append(this.stazioneAppaltante.getCodFiscale());
        prefissoLogger.append("). ");
      }
      
    } else {
      if (this.prefissoLogger.indexOf("SYSCON") < 0 && this.account != null) {
        prefissoLogger.append("Utente: ");
        prefissoLogger.append(account.getLogin());
        prefissoLogger.append(" (SYSCON=");
        prefissoLogger.append(account.getIdAccount());
        prefissoLogger.append("). ");
      }
      
      if (this.prefissoLogger.indexOf("S.A.") < 0 && stazioneAppaltante != null) {
        prefissoLogger.append("S.A.: "); 
        if (StringUtils.isNotEmpty(stazioneAppaltante.getNome())) {
          prefissoLogger.append(stazioneAppaltante.getNome());
        } else {
          prefissoLogger.append(" ");
        }
        prefissoLogger.append(" (CFEIN=");
        prefissoLogger.append(this.stazioneAppaltante.getCodFiscale());
        prefissoLogger.append("). ");
      }
    }
    
    if (StringUtils.isNotEmpty(prefissoLogger.toString())) {
      this.prefissoLogger = prefissoLogger.toString();
    }
  }
  
}
