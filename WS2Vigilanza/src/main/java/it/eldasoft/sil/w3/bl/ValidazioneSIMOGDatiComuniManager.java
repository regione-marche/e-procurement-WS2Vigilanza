package it.eldasoft.sil.w3.bl;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import javax.servlet.jsp.JspException;
import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.utils.metadata.cache.DizionarioCampi;
import it.eldasoft.utils.metadata.domain.Campo;

import org.apache.log4j.Logger;

public class ValidazioneSIMOGDatiComuniManager {

  static Logger      logger = Logger.getLogger(ValidazioneSIMOGDatiComuniManager.class);

  private SqlManager sqlManager;

  /**
   * 
   * @return
   */
  public SqlManager getSqlManager() {
    return this.sqlManager;
  }

  /**
   * @param sqlManager
   *        sqlManager da settare internamente alla classe.
   */
  public void setSqlManager(SqlManager sqlManager) {
    this.sqlManager = sqlManager;
  }

  public HashMap validate(Object[] params) throws JspException {
    HashMap infoValidazione = new HashMap();
    Long scheda_id = new Long((String) params[1]);
    infoValidazione = this.validate(scheda_id);
    return infoValidazione;
  }

  public HashMap validate(Long scheda_id) throws JspException {

    HashMap infoValidazione = new HashMap();
    List listaControlliDatiComuni = new Vector();
    List listaControlliAggiudicatari = new Vector();
    List listaControlliResponsabili = new Vector();

    try {
      // Controllo dati comuni
      this.validazioneW3DACO(sqlManager, scheda_id, listaControlliDatiComuni);

      // Controllo anagrafica stazione appaltante
      List listaControlliStazioneAppaltante = new Vector();
      validazioneW3STAP(sqlManager, scheda_id, listaControlliStazioneAppaltante);
      if (!listaControlliStazioneAppaltante.isEmpty()) {
        this.setTitolo(listaControlliDatiComuni,
            "Anagrafica della stazione appaltante");
        for (int i = 0; i < listaControlliStazioneAppaltante.size(); i++) {
          listaControlliDatiComuni.add(listaControlliStazioneAppaltante.get(i));
        }
      }

      // Anagrafiche degli aggiudicatari
      this.validazioneAggiudicatari(sqlManager, scheda_id,
          listaControlliAggiudicatari);

      // Anagrafiche dei responsabili
      this.validazioneResponsabili(sqlManager, scheda_id,
          listaControlliResponsabili);

    }

    catch (GestoreException e) {
      throw new JspException("Errore nella funzione di controllo dei dati", e);
    }

    infoValidazione.put("listaControlliDatiComuni", listaControlliDatiComuni);
    infoValidazione.put("listaControlliAggiudicatari",
        listaControlliAggiudicatari);
    infoValidazione.put("listaControlliResponsabili",
        listaControlliResponsabili);

    int numeroErroriDatiComuni = 0;
    int numeroWarningDatiComuni = 0;
    int numeroErroriAggiudicatari = 0;
    int numeroWarningAggiudicatari = 0;
    int numeroErroriResponsabili = 0;
    int numeroWarningResponsabili = 0;

    if (!listaControlliDatiComuni.isEmpty()) {
      for (int i = 0; i < listaControlliDatiComuni.size(); i++) {
        Object[] controllo = (Object[]) listaControlliDatiComuni.get(i);
        String tipo = (String) controllo[0];
        if ("E".equals(tipo)) numeroErroriDatiComuni++;
        if ("W".equals(tipo)) numeroWarningDatiComuni++;
      }
    }

    if (!listaControlliAggiudicatari.isEmpty()) {
      for (int i = 0; i < listaControlliAggiudicatari.size(); i++) {
        Object[] controllo = (Object[]) listaControlliAggiudicatari.get(i);
        String tipo = (String) controllo[0];
        if ("E".equals(tipo)) numeroErroriAggiudicatari++;
        if ("W".equals(tipo)) numeroWarningAggiudicatari++;
      }
    }

    if (!listaControlliResponsabili.isEmpty()) {
      for (int i = 0; i < listaControlliResponsabili.size(); i++) {
        Object[] controllo = (Object[]) listaControlliResponsabili.get(i);
        String tipo = (String) controllo[0];
        if ("E".equals(tipo)) numeroErroriResponsabili++;
        if ("W".equals(tipo)) numeroWarningResponsabili++;
      }
    }

    infoValidazione.put("numeroErroriDatiComuni", new Long(
        numeroErroriDatiComuni));
    infoValidazione.put("numeroWarningDatiComuni", new Long(
        numeroWarningDatiComuni));
    infoValidazione.put("numeroErroriAggiudicatari", new Long(
        numeroErroriAggiudicatari));
    infoValidazione.put("numeroWarningAggiudicatari", new Long(
        numeroWarningAggiudicatari));
    infoValidazione.put("numeroErroriResponsabili", new Long(
        numeroErroriResponsabili));
    infoValidazione.put("numeroWarningResponsabili", new Long(
        numeroWarningResponsabili));

    return infoValidazione;
  }

  /**
   * Validazione dei dati dei responsabili
   * 
   * @param sqlManager
   * @param scheda_id
   * @param listaControlli
   * @throws GestoreException
   */
  private void validazioneResponsabili(SqlManager sqlManager, Long scheda_id,
      List listaControlli) throws GestoreException {

    try {
      String selectResponsabili = "select cftec, cogtei, nometei, "
          + " teltec, ematec, faxtec, "
          + " loctec, indtec, ncitec, "
          + " captec,cittec, codtec "
          + " from tecni where codtec in (select tecni.codtec "
          + " from w3inca, w3fasi, tecni "
          + " where w3inca.schedacompleta_id = w3fasi.schedacompleta_id "
          + " and w3fasi.scheda_id = ? and w3fasi.stato_fase = 2 "
          + " and tecni.codtec = w3inca.codtec and tecni.codtec is not null)";

      List datiResponsabili = sqlManager.getListVector(selectResponsabili,
          new Object[] { scheda_id });

      String nomeTabella = "TECNI";
      String pagina = "";

      if (datiResponsabili != null && datiResponsabili.size() > 0) {

        for (int i = 0; i < datiResponsabili.size(); i++) {

          String codtec = (String) SqlManager.getValueFromVectorParam(
              datiResponsabili.get(i), 11).getValue();
          pagina = "Tecnico con codice anagrafico " + codtec + " - Archivio";

          // Controlli aggiunti perché nella definizione XSD sono sempre
          // obbligatori
          if (SqlManager.getValueFromVectorParam(datiResponsabili.get(i), 0).getValue() == null) {
            this.addCampoObbligatorio(listaControlli, nomeTabella, "CFTEC",
                pagina);
          }
          if (SqlManager.getValueFromVectorParam(datiResponsabili.get(i), 1).getValue() == null) {
            this.addCampoObbligatorio(listaControlli, nomeTabella, "COGTEI",
                pagina);
          }
          if (SqlManager.getValueFromVectorParam(datiResponsabili.get(i), 2).getValue() == null) {
            this.addCampoObbligatorio(listaControlli, nomeTabella, "NOMETEI",
                pagina);
          }
          if (SqlManager.getValueFromVectorParam(datiResponsabili.get(i), 3).getValue() == null) {
            this.addCampoObbligatorio(listaControlli, nomeTabella, "TELTEC",
                pagina);
          }
          if (SqlManager.getValueFromVectorParam(datiResponsabili.get(i), 4).getValue() == null) {
            this.addCampoObbligatorio(listaControlli, nomeTabella, "EMATEC",
                pagina);
          }
          if (SqlManager.getValueFromVectorParam(datiResponsabili.get(i), 5).getValue() == null) {
            this.addCampoObbligatorio(listaControlli, nomeTabella, "FAXTEC",
                pagina);
          }
          if (SqlManager.getValueFromVectorParam(datiResponsabili.get(i), 6).getValue() == null) {
            this.addCampoObbligatorio(listaControlli, nomeTabella, "LOCTEC",
                pagina);
          }
          if (SqlManager.getValueFromVectorParam(datiResponsabili.get(i), 7).getValue() == null) {
            this.addCampoObbligatorio(listaControlli, nomeTabella, "INDTEC",
                pagina);
          }

          if (SqlManager.getValueFromVectorParam(datiResponsabili.get(i), 8).getValue() == null) {
            this.addCampoObbligatorio(listaControlli, nomeTabella, "NCITEC",
                pagina);
          }

          if (SqlManager.getValueFromVectorParam(datiResponsabili.get(i), 9).getValue() == null) {
            this.addCampoObbligatorio(listaControlli, nomeTabella, "CAPTEC",
                pagina);
          }

          if (SqlManager.getValueFromVectorParam(datiResponsabili.get(i), 10).getValue() == null) {
            this.addCampoObbligatorio(listaControlli, nomeTabella, "CITTEC",
                pagina);
          }

        }

      }

    } catch (SQLException e) {
      throw new GestoreException(
          "Errore nella lettura delle informazioni relative agli aggiudicatari",
          "validazioneAggiudicatari", e);
    }

  }

  /**
   * Validazione dei dati degli aggiudicatari
   * 
   * @param sqlManager
   * @param scheda_id
   * @param listaControlli
   * @throws GestoreException
   */
  private void validazioneAggiudicatari(SqlManager sqlManager, Long scheda_id,
      List listaControlli) throws GestoreException {

    try {
      String filtroAggiudicatari = "select w3aggi.codimp from w3aggi, w3fasi "
          + " where w3aggi.schedacompleta_id = ? and w3fasi.schedacompleta_id = w3aggi.schedacompleta_id "
          + " and w3fasi.stato_fase = 2 and w3fasi.fase_esecuzione = 1 and w3aggi.codimp is not null "
          + " union "
          + " select w3aggi.codimp_ausiliaria from w3aggi, w3fasi "
          + " where w3aggi.schedacompleta_id = ? and w3fasi.schedacompleta_id = w3aggi.schedacompleta_id "
          + " and w3fasi.stato_fase = 2 and w3fasi.fase_esecuzione = 1 and w3aggi.codimp_ausiliaria is not null "
          + " union "
          + " select w3suba.codimp from w3suba, w3fasi "
          + " where w3suba.schedacompleta_id = ? and w3fasi.schedacompleta_id = w3suba.schedacompleta_id "
          + " and w3fasi.stato_fase = 2 and w3fasi.fase_esecuzione = 9 and w3suba.codimp is not null ";

      String selectAggiudicatari = "select cfimp, nomest, ncciaa, pivimp, "
          + " indimp, nciimp, capimp, locimp, proimp, codimp from impr where codimp in ("
          + filtroAggiudicatari
          + ")";

      String nomeTabella = "IMPR";
      String pagina = "";

      List datiAggiudicatari = sqlManager.getListVector(selectAggiudicatari,
          new Object[] { scheda_id, scheda_id, scheda_id });

      if (datiAggiudicatari != null && datiAggiudicatari.size() > 0) {
        for (int i = 0; i < datiAggiudicatari.size(); i++) {

          String codimp = (String) SqlManager.getValueFromVectorParam(
              datiAggiudicatari.get(i), 9).getValue();

          if (codimp != null) {

            pagina = "Impresa con codice anagrafico " + codimp + " - Archivio";

            if (SqlManager.getValueFromVectorParam(datiAggiudicatari.get(i), 1).getValue() == null) {
              this.addCampoObbligatorio(listaControlli, nomeTabella, "NOMEST",
                  pagina);
            }

            if (SqlManager.getValueFromVectorParam(datiAggiudicatari.get(i), 0).getValue() == null) {
              this.addCampoObbligatorio(listaControlli, nomeTabella, "CFIMP",
                  pagina);
            }

            if (SqlManager.getValueFromVectorParam(datiAggiudicatari.get(i), 3).getValue() == null) {
              this.addCampoObbligatorio(listaControlli, nomeTabella, "PIVIMP",
                  pagina);
            }

            if (SqlManager.getValueFromVectorParam(datiAggiudicatari.get(i), 4).getValue() == null) {
              this.addCampoObbligatorio(listaControlli, nomeTabella, "INDIMP",
                  pagina);
            }

            if (SqlManager.getValueFromVectorParam(datiAggiudicatari.get(i), 5).getValue() == null) {
              this.addCampoObbligatorio(listaControlli, nomeTabella, "NCIIMP",
                  pagina);
            }

            if (SqlManager.getValueFromVectorParam(datiAggiudicatari.get(i), 8).getValue() == null) {
              this.addCampoObbligatorio(listaControlli, nomeTabella, "PROIMP",
                  pagina);
            }

            if (SqlManager.getValueFromVectorParam(datiAggiudicatari.get(i), 6).getValue() == null) {
              this.addCampoObbligatorio(listaControlli, nomeTabella, "CAPIMP",
                  pagina);
            }
            if (SqlManager.getValueFromVectorParam(datiAggiudicatari.get(i), 7).getValue() == null) {
              this.addCampoObbligatorio(listaControlli, nomeTabella, "LOCIMP",
                  pagina);
            }

            // Controllo legale rappresentate
            Long conteggioImpleg = (Long) sqlManager.getObject(
                "select count(*) from impleg where codimp2 = ?",
                new Object[] { codimp });
            if (conteggioImpleg == null
                || (conteggioImpleg != null && conteggioImpleg.longValue() == 0)) {
              listaControlli.add(((Object) (new Object[] { "E", pagina,
                  "Legale rappresentante",
                  "E' necessario indicare il legale rappresentante" })));
            } else {
              String cftim = (String) sqlManager.getObject(
                  "select teim.cftim from teim, impleg where teim.codtim = impleg.codleg and impleg.codimp2 = ?",
                  new Object[] { codimp });
              if (cftim == null)
                listaControlli.add(((Object) (new Object[] { "E", pagina,
                    "Legale rappresentante",
                    "Il legale rappresentate indicato è privo di codice fiscale" })));

            }

            if (SqlManager.getValueFromVectorParam(datiAggiudicatari.get(i), 2).getValue() == null) {
              this.addCampoObbligatorio(listaControlli, nomeTabella, "NCCIAA",
                  pagina);
            }

          }
        }

      }
    } catch (SQLException e) {
      throw new GestoreException(
          "Errore nella lettura delle informazioni relative agli aggiudicatari",
          "validazioneAggiudicatari", e);
    }

  }

  /**
   * Validazione dei dati relativi alla pubblicazione del bando di gara (W3PUBB)
   * 
   * @param sqlManager
   * @param scheda_id
   * @param listaControlli
   * @throws GestoreException
   */
  private void validazioneW3PUBB(SqlManager sqlManager, Long scheda_id,
      List listaControlli) throws GestoreException {

    try {

      String nomeTabella = "W3PUBB";
      String pagina = "Pubblicit&agrave; dell'appalto";

      String selectW3PUBB = "select profilo_committente, sito_ministero_inf_trasp, sito_osservatorio_cp, "
          + " quotidiani_naz, quotidiani_reg from w3pubb where scheda_id = ?";
      List datiW3PUBB = sqlManager.getVector(selectW3PUBB,
          new Object[] { scheda_id });

      if (datiW3PUBB != null && datiW3PUBB.size() > 0) {
        if (SqlManager.getValueFromVectorParam(datiW3PUBB, 0).getValue() == null) {
          this.addCampoObbligatorio(listaControlli, nomeTabella,
              "PROFILO_COMMITTENTE", pagina);
        }
        if (SqlManager.getValueFromVectorParam(datiW3PUBB, 1).getValue() == null) {
          this.addCampoObbligatorio(listaControlli, nomeTabella,
              "SITO_MINISTERO_INF_TRASP", pagina);
        }
        if (SqlManager.getValueFromVectorParam(datiW3PUBB, 2).getValue() == null) {
          this.addCampoObbligatorio(listaControlli, nomeTabella,
              "SITO_OSSERVATORIO_CP", pagina);
        }
        if (SqlManager.getValueFromVectorParam(datiW3PUBB, 3).getValue() == null) {
          this.addCampoObbligatorio(listaControlli, nomeTabella,
              "QUOTIDIANI_NAZ", pagina);
        }
        if (SqlManager.getValueFromVectorParam(datiW3PUBB, 4).getValue() == null) {
          this.addCampoObbligatorio(listaControlli, nomeTabella,
              "QUOTIDIANI_REG", pagina);
        }

      }
    } catch (SQLException e) {
      throw new GestoreException(
          "Errore nella lettura delle informazioni relative alla pubblicazione del bando di gara",
          "validazioneW3PUBB", e);
    }

  }

  /**
   * Validazione dei dati della stazione appaltante (W3STAP)
   * 
   * @param sqlManager
   * @param scheda_id
   * @param listaControlli
   * @throws GestoreException
   */
  private void validazioneW3STAP(SqlManager sqlManager, Long scheda_id,
      List listaControlli) throws GestoreException {

    try {

      String nomeTabella = "W3STAP";
      String pagina = null;

      String selectW3STAP = "select w3stap.den_sa, w3stap.cf_sa, w3stap.cf_amm, "
          + " w3stap.den_amm, w3stap.id_categ_sa from w3stap, w3daco"
          + " where w3stap.stap_id = w3daco.stap_id and w3daco.scheda_id = ?";

      List datiW3STAP = sqlManager.getVector(selectW3STAP,
          new Object[] { scheda_id });

      if (datiW3STAP != null && datiW3STAP.size() > 0) {

        pagina = "Dati generali della stazione appaltante";
        if (SqlManager.getValueFromVectorParam(datiW3STAP, 0).getValue() == null) {
          this.addCampoObbligatorio(listaControlli, nomeTabella, "DEN_SA",
              pagina);
        }
        if (SqlManager.getValueFromVectorParam(datiW3STAP, 1).getValue() == null) {
          this.addCampoObbligatorio(listaControlli, nomeTabella, "CF_SA",
              pagina);
        }
        if (SqlManager.getValueFromVectorParam(datiW3STAP, 4).getValue() == null) {
          String messaggio = "I campi sono obbligatori";
          String descrizione = this.getDescrizioneCampo("W3STAP", "ID_CATEG_SA");
          descrizione += ", Descrizione categoria";
          listaControlli.add(((Object) (new Object[] { "E", pagina,
              descrizione, messaggio })));
        }

      }

    } catch (SQLException e) {
      throw new GestoreException(
          "Errore nella lettura delle informazioni relative alla stazione appaltante",
          "validazioneW3STAP", e);
    }
  }

  /**
   * Validazione dei dati comuni (W3DACO)
   * 
   * @param sqlManager
   * @param scheda_id
   * @param listaControlliDatiComuni
   * @throws JspException
   */
  private void validazioneW3DACO(SqlManager sqlManager, Long scheda_id,
      List listaControlli) throws GestoreException {

    try {

      String nomeTabella = "W3DACO";
      String pagina = null;

      String selectW3DACO = "select cig, oggetto, flag_ente_speciale, "
          + " tipo_contratto, stap_id, flag_sa_agente, "
          + " cf_amm_agente, den_amm_agente, esito_procedura, codice_cc, denom_cc "
          + " from w3daco where scheda_id = ?";

      List datiW3DACO = sqlManager.getVector(selectW3DACO,
          new Object[] { scheda_id });

      if (datiW3DACO != null && datiW3DACO.size() > 0) {

        pagina = "Dati comuni";
        if (SqlManager.getValueFromVectorParam(datiW3DACO, 0).getValue() == null) {
          this.addCampoObbligatorio(listaControlli, nomeTabella, "CIG", pagina);
        }
        if (SqlManager.getValueFromVectorParam(datiW3DACO, 1).getValue() == null) {
          this.addCampoObbligatorio(listaControlli, nomeTabella, "OGGETTO",
              pagina);
        }
        if (SqlManager.getValueFromVectorParam(datiW3DACO, 2).getValue() == null) {
          this.addCampoObbligatorio(listaControlli, nomeTabella,
              "FLAG_ENTE_SPECIALE", pagina);
        }
        if (SqlManager.getValueFromVectorParam(datiW3DACO, 3).getValue() == null) {
          this.addCampoObbligatorio(listaControlli, nomeTabella,
              "TIPO_CONTRATTO", pagina);
        }

        pagina = "Stazione appaltante";
        if (SqlManager.getValueFromVectorParam(datiW3DACO, 4).getValue() == null) {
          String messaggio = "E' obbligatorio indicare la stazione appaltante";
          String descrizione = "Stazione appaltante";
          listaControlli.add(((Object) (new Object[] { "E", pagina,
              descrizione, messaggio })));
        }

        String flag_sa_agente = (String) SqlManager.getValueFromVectorParam(
            datiW3DACO, 5).getValue();
        if (flag_sa_agente == null)
          this.addCampoObbligatorio(listaControlli, nomeTabella,
              "FLAG_SA_AGENTE", pagina);

        if (flag_sa_agente != null && "1".equals(flag_sa_agente)) {
          if (SqlManager.getValueFromVectorParam(datiW3DACO, 6).getValue() == null) {
            this.addCampoObbligatorio(listaControlli, nomeTabella,
                "CF_AMM_AGENTE", pagina);
          }
          if (SqlManager.getValueFromVectorParam(datiW3DACO, 7).getValue() == null) {
            this.addCampoObbligatorio(listaControlli, nomeTabella,
                "DEN_AMM_AGENTE", pagina);
          }
        }

        if (SqlManager.getValueFromVectorParam(datiW3DACO, 9).getValue() == null) {
          this.addCampoObbligatorio(listaControlli, nomeTabella, "CODICE_CC",
              pagina);
        }

        if (SqlManager.getValueFromVectorParam(datiW3DACO, 10).getValue() == null) {
          this.addCampoObbligatorio(listaControlli, nomeTabella, "DENOM_CC",
              pagina);
        }

        // Controllo dati di pubblicazione
        this.validazioneW3PUBB(sqlManager, scheda_id, listaControlli);

        pagina = "Stato attuale";
        if (SqlManager.getValueFromVectorParam(datiW3DACO, 8).getValue() == null) {
          this.addCampoObbligatorio(listaControlli, nomeTabella,
              "ESITO_PROCEDURA", pagina);
        }

      }

    } catch (SQLException e) {
      throw new GestoreException(
          "Errore nella lettura delle informazioni relative ai dati generali",
          "validazioneW3DACO", e);
    }
  }

  /**
   * Aggiunge un messaggio bloccante alla listaControlli
   * 
   * @param listaControlli
   * @param nomeTabella
   * @param nomeCampo
   * @param pagina
   */
  private void addCampoObbligatorio(List listaControlli, String nomeTabella,
      String nomeCampo, String pagina) {
    String descrizione = this.getDescrizioneCampo(nomeTabella, nomeCampo);
    String messaggio = "Il campo è obbligatorio";
    listaControlli.add(((Object) (new Object[] { "E", pagina, descrizione,
        messaggio })));
  }

  /**
   * Restituisce la descrizione WEB del campo
   * 
   * @param nomeTabella
   * @param nomeCampo
   * @return
   */
  private String getDescrizioneCampo(String nomeTabella, String nomeCampo) {

    String descrizione = "";

    try {
      Campo c = DizionarioCampi.getInstance().getCampoByNomeFisico(
          nomeTabella + "." + nomeCampo);
      descrizione = c.getDescrizioneWEB();
    } catch (Throwable t) {

    }

    return descrizione;

  }

  /**
   * Utilizzata per settare il tipo T ossia il titolo all'interno di una tabella
   * 
   * @param listaControlli
   * @param pagina
   */
  private void setTitolo(List listaControlli, String titolo) {
    listaControlli.add(((Object) (new Object[] { "T", titolo, "", "" })));
  }

}
