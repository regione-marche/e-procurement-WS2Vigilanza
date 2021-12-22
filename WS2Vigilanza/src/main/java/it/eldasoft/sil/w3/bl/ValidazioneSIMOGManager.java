package it.eldasoft.sil.w3.bl;

import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import javax.servlet.jsp.JspException;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.utils.metadata.cache.DizionarioCampi;
import it.eldasoft.utils.metadata.domain.Campo;
import it.eldasoft.utils.utility.UtilityDate;

import org.apache.log4j.Logger;

public class ValidazioneSIMOGManager {

  static Logger      logger = Logger.getLogger(ValidazioneSIMOGManager.class);

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
    Long schedacompleta_id = new Long((String) params[2]);
    Long fase_esecuzione = new Long((String) params[3]);
    Long num = new Long((String) params[4]);
    infoValidazione = this.validate(scheda_id, schedacompleta_id,
        fase_esecuzione, num);
    return infoValidazione;
  }

  public HashMap validate(Long scheda_id, Long schedacompleta_id,
      Long fase_esecuzione, Long num) throws JspException {

    HashMap infoValidazione = new HashMap();
    String titolo = null;
    List listaControlli = new Vector();

    try {
      if ((new Long(1)).equals(fase_esecuzione)) {
        titolo = this.getDescrizioneFase(fase_esecuzione);
        this.validazioneW3APPA(sqlManager, schedacompleta_id, listaControlli);
      }

      if ((new Long(2)).equals(fase_esecuzione)) {
        titolo = this.getDescrizioneFase(fase_esecuzione);
        this.validazioneW3INIZ(sqlManager, schedacompleta_id, listaControlli);
      }

      if ((new Long(3)).equals(fase_esecuzione)) {
        titolo = this.getDescrizioneFase(fase_esecuzione)
            + " n° "
            + num.toString();
        this.validazioneW3AVAN(sqlManager, schedacompleta_id, num,
            listaControlli);
      }

      if ((new Long(4)).equals(fase_esecuzione)) {
        titolo = this.getDescrizioneFase(fase_esecuzione);
        this.validazioneW3CONC(sqlManager, schedacompleta_id, listaControlli);
      }

      if ((new Long(5)).equals(fase_esecuzione)) {
        titolo = this.getDescrizioneFase(fase_esecuzione);
        this.validazioneW3COLL(sqlManager, schedacompleta_id, listaControlli);
      }

      if ((new Long(6)).equals(fase_esecuzione)) {
        titolo = this.getDescrizioneFase(fase_esecuzione)
            + " n° "
            + num.toString();
        this.validazioneW3SOSP(sqlManager, schedacompleta_id, num,
            listaControlli);
      }

      if ((new Long(7)).equals(fase_esecuzione)) {
        titolo = this.getDescrizioneFase(fase_esecuzione)
            + " n° "
            + num.toString();
        this.validazioneW3VARI(sqlManager, schedacompleta_id, num,
            listaControlli);
      }

      if ((new Long(8)).equals(fase_esecuzione)) {
        titolo = this.getDescrizioneFase(fase_esecuzione)
            + " n° "
            + num.toString();
        this.validazioneW3ACCO(sqlManager, schedacompleta_id, num,
            listaControlli);
      }

      if ((new Long(9)).equals(fase_esecuzione)) {
        titolo = this.getDescrizioneFase(fase_esecuzione)
            + " n° "
            + num.toString();
        this.validazioneW3SUBA(sqlManager, schedacompleta_id, num,
            listaControlli);
      }

      if ((new Long(10)).equals(fase_esecuzione)) {
        titolo = this.getDescrizioneFase(fase_esecuzione)
            + " n° "
            + num.toString();
        this.validazioneW3RITA(sqlManager, schedacompleta_id, num,
            listaControlli);
      }

      infoValidazione.put("titolo", titolo);
      infoValidazione.put("listaControlli", listaControlli);

      int numeroErrori = 0;
      int numeroWarning = 0;

      if (!listaControlli.isEmpty()) {
        for (int i = 0; i < listaControlli.size(); i++) {
          Object[] controllo = (Object[]) listaControlli.get(i);
          String tipo = (String) controllo[0];

          if ("E".equals(tipo)) {
            numeroErrori++;
          }
          if ("W".equals(tipo)) {
            numeroWarning++;
          }
        }
      }

      infoValidazione.put("numeroErrori", new Long(numeroErrori));
      infoValidazione.put("numeroWarning", new Long(numeroWarning));

    }

    catch (GestoreException e) {
      throw new JspException("Errore nella funzione di controllo dei dati", e);
    }

    return infoValidazione;
  }

  /**
   * Controllo dati per la fase di aggiudicazione (W3APPA e relative figlie)
   * 
   * @param sqlManager
   * @param schedacompleta_id
   * @param num
   * @param listaControlli
   * @throws GestoreException
   */
  private void validazioneW3APPA(SqlManager sqlManager, Long schedacompleta_id,
      List listaControlli) throws GestoreException {
    if (logger.isDebugEnabled())
      logger.debug("validazioneW3APPA: inizio metodo");

    try {

      // String cui = (String) sqlManager.getObject(
      // "select cui from w3fasi where scheda_id = ? and schedacompleta_id = ?"
      // + " and fase_esecuzione = ? and num = ?", new Object[] {
      // schedacompleta_id, schedacompleta_id, new Long(1), new Long(1) });
      // // Controllo aggiunto perché nella definizione XSD è sempre
      // // obbligatorio
      // if (cui == null) {
      // this.addCampoObbligatorio(listaControlli, "W3FASI", "CUI", "");
      // }

      String nomeTabella = "W3APPA";

      String selectW3APPA = "select luogo_istat, luogo_nuts, cup, "
          + " flag_accordo_quadro, id_tipo_prestazione, cod_strumento, "
          + " importo_lavori, importo_servizi, importo_forniture, "
          + " importo_compl_appalto, importo_disposizione, id_scelta_contraente, "
          + " asta_elettronica, id_modo_gara, procedura_acc, "
          + " preinformazione, termine_ridotto, id_modo_indizione,"
          + " criteri_selezione_stabiliti_sa, sistema_qualificazione, "
          + " opere_urbaniz_scomputo, importo_compl_intervento, importo_attuazione_sicurezza, "
          + " importo_progettazione from w3appa "
          + " where schedacompleta_id = ?  ";
      List datiW3APPA = sqlManager.getVector(selectW3APPA,
          new Object[] { schedacompleta_id });

      if (datiW3APPA != null && datiW3APPA.size() > 0) {

        String flag_ente_speciale = (String) sqlManager.getObject(
            "select flag_ente_speciale from w3daco where scheda_id = ?",
            new Object[] { schedacompleta_id });
        String tipo_contratto = (String) sqlManager.getObject(
            "select tipo_contratto from w3daco where scheda_id = ?",
            new Object[] { schedacompleta_id });

        String pagina_oggetto = "Oggetto dell'appalto";

        // Luogo di esecuzione del contratto LUOGO_ISTAT, LUOGO_NUTS
        String luogo_istat = (String) SqlManager.getValueFromVectorParam(
            datiW3APPA, 0).getValue();
        String luogo_nuts = (String) SqlManager.getValueFromVectorParam(
            datiW3APPA, 1).getValue();

        if (luogo_istat == null && luogo_nuts == null) {
          String descrizione = this.getDescrizioneCampo(nomeTabella,
              "LUOGO_ISTAT");
          descrizione += ", "
              + this.getDescrizioneCampo(nomeTabella, "LUOGO_NUTS");
          String messaggio = "Valorizzare almeno uno dei due campi";
          listaControlli.add(((Object) (new Object[] { "E", pagina_oggetto,
              descrizione, messaggio })));
        }

        // Codice CUP
        String cup = (String) SqlManager.getValueFromVectorParam(datiW3APPA, 2).getValue();

        // Controllo aggiunto perché nella definizione XSD è sempre obbligatorio
        if (cup == null) {
          this.addCampoObbligatorio(listaControlli, nomeTabella, "CUP",
              pagina_oggetto);
        }

        if (cup != null && cup.length() != 15) {
          String messaggio = "Il codice CUP deve essere di 15 caratteri alfanumerici";
          this.addAvviso(listaControlli, nomeTabella, "CUP", "E",
              pagina_oggetto, messaggio);
        }

        // Importi lavori, servizi, forniture
        Double importo_lavori = (Double) SqlManager.getValueFromVectorParam(
            datiW3APPA, 6).getValue();
        Double importo_servizi = (Double) SqlManager.getValueFromVectorParam(
            datiW3APPA, 7).getValue();
        Double importo_forniture = (Double) SqlManager.getValueFromVectorParam(
            datiW3APPA, 8).getValue();

        // Tipologia del lavoro
        Long conteggiow3appalav = (Long) sqlManager.getObject(
            "select count(*) from w3appalav where schedacompleta_id = ?",
            new Object[] { schedacompleta_id });
        if (conteggiow3appalav == null
            || (conteggiow3appalav != null && new Long(0).equals(conteggiow3appalav))) {
          String messaggio = "Indicare \"Si\" in almeno una tipologia fra quelle previste";
          if ("L".equals(tipo_contratto)) {
            listaControlli.add(((Object) (new Object[] { "E",
                "Tipologia lavoro", "Tipologia lavoro", messaggio })));
          } else if ("S".equals(tipo_contratto) || "F".equals(tipo_contratto)) {
            if (importo_lavori != null && importo_lavori.doubleValue() == 0) {
              listaControlli.add(((Object) (new Object[] { "E",
                  "Tipologia lavoro", "Tipologia lavoro", messaggio })));
            }
          }
        }

        // Opere urbanizzazione a scomputo
        if (SqlManager.getValueFromVectorParam(datiW3APPA, 20).getValue() == null) {
          this.addCampoObbligatorio(listaControlli, nomeTabella,
              "OPERE_URBANIZ_SCOMPUTO", pagina_oggetto);
        }

        // Modalità acquisizione fornitura servizi
        Long conteggiow3appaforn = (Long) sqlManager.getObject(
            "select count(*) from w3appaforn where schedacompleta_id = ?",
            new Object[] { schedacompleta_id });
        if (conteggiow3appaforn == null
            || (conteggiow3appaforn != null && new Long(0).equals(conteggiow3appaforn))) {
          String messaggio = "Indicare \"Si\" in almeno una modalit&agrave; fra quelle previste";
          if ("L".equals(tipo_contratto)) {
            listaControlli.add(((Object) (new Object[] { "W",
                "Modalit&agrave; di acquisizione forniture / servizi",
                "Modalità di acquisizione forniture / servizi", messaggio })));
          } else if ("S".equals(tipo_contratto) || "F".equals(tipo_contratto)) {
            listaControlli.add(((Object) (new Object[] { "E",
                "Modalit&agrave; di acquisizione forniture / servizi",
                "Modalità di acquisizione forniture / servizi", messaggio })));
          }
        }

        // Prestazioni comprese nell'appalto
        if (SqlManager.getValueFromVectorParam(datiW3APPA, 4).getValue() == null) {
          this.addCampoObbligatorio(listaControlli, nomeTabella,
              "ID_TIPO_PRESTAZIONE", pagina_oggetto);
        }

        // Controllo numero progettisti - deve essere indicato almeno un
        // progettista
        Long conteggiow3incapa = (Long) sqlManager.getObject(
            "select count(*) from w3inca where schedacompleta_id = ? and sezione='PA'",
            new Object[] { schedacompleta_id });
        if (conteggiow3incapa == null
            || (conteggiow3incapa != null && new Long(0).equals(conteggiow3incapa))) {
          String messaggio = "Deve essere indicato almeno un progettista";
          listaControlli.add(((Object) (new Object[] { "W",
              "Prestazioni progettuali", "Prestazioni progettuali", messaggio })));
        }

        String pagina_dati_economici = "Dati economici dell'appalto";

        // Codice dello strumento di programmazione
        String cod_strumento = (String) SqlManager.getValueFromVectorParam(
            datiW3APPA, 5).getValue();
        if ("O".equals(flag_ente_speciale) && cod_strumento == null) {
          String messaggio = "Il campo è obbligatorio in quanto il CIG appartiene al settore ORDINARIO";
          this.addAvviso(listaControlli, nomeTabella, "COD_STRUMENTO", "E",
              pagina_dati_economici, messaggio);
        }

        // Controlli sugli importi lavori, servizi, forniture
        // Gli importi vengono controllati sempre perché nel file XSD sono
        // sempre obbligatori
        if (importo_lavori == null) {
          this.addCampoObbligatorio(listaControlli, nomeTabella,
              "IMPORTO_LAVORI", pagina_dati_economici);
        }
        if (importo_servizi == null) {
          this.addCampoObbligatorio(listaControlli, nomeTabella,
              "IMPORTO_SERVIZI", pagina_dati_economici);
        }
        if (importo_forniture == null) {
          this.addCampoObbligatorio(listaControlli, nomeTabella,
              "IMPORTO_FORNITURE", pagina_dati_economici);
        }

        // if ("L".equals(tipo_contratto)) {
        // if (importo_lavori == null) {
        // this.addCampoObbligatorio(listaControlli, nomeTabella,
        // "IMPORTO_LAVORI", pagina_dati_economici);
        // }
        //          
        // if (importo_lavori != null && importo_lavori.doubleValue() == 0) {
        // String messaggio = "L\'importo digitato deve essere >0";
        // this.addAvviso(listaControlli, nomeTabella, "IMPORTO_LAVORI", "E",
        // pagina_dati_economici, messaggio);
        // }
        // }
        //          
        // if ("S".equals(tipo_contratto) && importo_servizi == null) {
        // this.addCampoObbligatorio(listaControlli, nomeTabella,
        // "IMPORTO_SERVIZI", pagina_dati_economici);
        // }
        // if ("F".equals(tipo_contratto) && importo_forniture == null) {
        // this.addCampoObbligatorio(listaControlli, nomeTabella,
        // "IMPORTO_FORNITURE", pagina_dati_economici);
        // }

        // Importo complessivo appalto
        Double importo_compl_appalto = (Double) SqlManager.getValueFromVectorParam(
            datiW3APPA, 9).getValue();
        if (importo_compl_appalto == null
            || (importo_compl_appalto != null && importo_compl_appalto.doubleValue() < 150000)) {
          String messaggio = "L\'importo complessivo dell'appalto è inferiore a ";
          messaggio += this.importoToStringa(new Double(150000));
          this.addAvviso(listaControlli, nomeTabella, "IMPORTO_COMPL_APPALTO",
              "W", pagina_dati_economici, messaggio);
        }

        // Controllo aggiunto perché nella definizione XSD è sempre obbligatorio
        Double importo_attuazione_sicurezza = (Double) SqlManager.getValueFromVectorParam(
            datiW3APPA, 22).getValue();
        if (importo_attuazione_sicurezza == null) {
          this.addCampoObbligatorio(listaControlli, nomeTabella,
              "IMPORTO_ATTUAZIONE_SICUREZZA", pagina_dati_economici);
        }

        // Controllo aggiunto perché nella definizione XSD è sempre obbligatorio
        Double importo_progettazione = (Double) SqlManager.getValueFromVectorParam(
            datiW3APPA, 23).getValue();
        if (importo_progettazione == null) {
          this.addCampoObbligatorio(listaControlli, nomeTabella,
              "IMPORTO_PROGETTAZIONE", pagina_dati_economici);
        }

        // Importo somme a disposizione
        Double importo_disposizione = (Double) SqlManager.getValueFromVectorParam(
            datiW3APPA, 10).getValue();
        if (importo_disposizione == null) {
          this.addCampoObbligatorio(listaControlli, nomeTabella,
              "IMPORTO_DISPOSIZIONE", pagina_dati_economici);
        }

        if (importo_disposizione != null
            && importo_disposizione.doubleValue() == 0) {
          String messaggio = "Non sono state indicate le somme a disposizione";
          this.addAvviso(listaControlli, nomeTabella, "IMPORTO_DISPOSIZIONE",
              "W", pagina_dati_economici, messaggio);
        }

        // Importo cumulativo dei finanziamenti e Importo complessivo
        // dell'intervento
        Double importo_finanziamenti = new Double(0);
        List datiW3FINA = sqlManager.getListVector(
            "select importo_finanziamento from w3fina where schedacompleta_id = ?",
            new Object[] { schedacompleta_id });
        if (datiW3FINA != null && datiW3FINA.size() > 0) {
          for (int i = 0; i < datiW3FINA.size(); i++) {
            Double importo_singolo_finanziamento = (Double) SqlManager.getValueFromVectorParam(
                datiW3FINA.get(i), 0).getValue();
            if (importo_singolo_finanziamento != null) {
              importo_finanziamenti = new Double(
                  importo_finanziamenti.doubleValue()
                      + importo_singolo_finanziamento.doubleValue());
            }
          }
        }

        Double importo_compl_intervento = (Double) SqlManager.getValueFromVectorParam(
            datiW3APPA, 21).getValue();

        if (importo_finanziamenti != null
            && importo_finanziamenti.doubleValue() > 0
            && importo_compl_intervento != null) {
          if (importo_compl_intervento.doubleValue() > importo_finanziamenti.doubleValue()) {
            String messaggio = "L\'importo complessivo degli interventi ("
                + this.importoToStringa(importo_compl_intervento)
                + ") non è interamente coperto dall\'importo cumulativo dei finanziamenti ("
                + this.importoToStringa(importo_finanziamenti)
                + ") ";
            this.addAvviso(listaControlli, nomeTabella,
                "IMPORTO_COMPL_INTERVENTO", "W", pagina_dati_economici,
                messaggio);
          }
        }

        // Dati procedurali dell'appalto
        String pagina_dati_procedurali = "Dati procedurali dell'appalto";

        // Scelta contraente
        Long id_scelta_contraente = (Long) SqlManager.getValueFromVectorParam(
            datiW3APPA, 11).getValue();
        if (id_scelta_contraente == null) {
          this.addCampoObbligatorio(listaControlli, nomeTabella,
              "ID_SCELTA_CONTRAENTE", pagina_dati_procedurali);
        }

        // Asta elettronica
        if (SqlManager.getValueFromVectorParam(datiW3APPA, 12).getValue() == null) {
          this.addCampoObbligatorio(listaControlli, nomeTabella,
              "ASTA_ELETTRONICA", pagina_dati_procedurali);
        }

        // Controllo condizioni
        if (id_scelta_contraente != null
            && (id_scelta_contraente.longValue() == 4 || id_scelta_contraente.longValue() == 10)) {
          Long conteggiow3cond = (Long) sqlManager.getObject(
              "select count(*) from w3cond where schedacompleta_id = ?",
              new Object[] { schedacompleta_id });
          if (conteggiow3cond == null
              || (conteggiow3cond != null && new Long(0).equals(conteggiow3cond))) {
            String messaggio = "Indicare \"Si\" in almeno una condizione fra quelle previste";
            listaControlli.add(((Object) (new Object[] { "E", "Condizioni",
                "Condizioni", messaggio })));
          }
        }

        // id_modo_gara
        if (SqlManager.getValueFromVectorParam(datiW3APPA, 13).getValue() == null) {
          this.addCampoObbligatorio(listaControlli, nomeTabella,
              "ID_MODO_GARA", pagina_dati_procedurali);
        }

        // procedura_acc
        if (SqlManager.getValueFromVectorParam(datiW3APPA, 14).getValue() == null) {
          this.addCampoObbligatorio(listaControlli, nomeTabella,
              "PROCEDURA_ACC", pagina_dati_procedurali);
        }

        // preinformazione
        if (SqlManager.getValueFromVectorParam(datiW3APPA, 15).getValue() == null) {
          this.addCampoObbligatorio(listaControlli, nomeTabella,
              "PREINFORMAZIONE", pagina_dati_procedurali);
        }

        // termine_ridotto
        if (SqlManager.getValueFromVectorParam(datiW3APPA, 16).getValue() == null) {
          this.addCampoObbligatorio(listaControlli, nomeTabella,
              "TERMINE_RIDOTTO", pagina_dati_procedurali);
        }

        // id_modo_indizione
        Long id_modo_indizione = (Long) SqlManager.getValueFromVectorParam(
            datiW3APPA, 17).getValue();
        if ("S".equals(flag_ente_speciale) && id_modo_indizione == null) {
          if (id_scelta_contraente != null
              && (id_scelta_contraente.longValue() != 10 || id_scelta_contraente.longValue() != 11)) {
            String messaggio = "Non è stata selezionata la modalit&agrave; di indizione della gara per i settori speciali";
            this.addAvviso(listaControlli, nomeTabella, "ID_MODO_INDIZIONE",
                "E", pagina_dati_procedurali, messaggio);
          }
        }

        // Controllo Categoria Prevalente e Classe d'Importo
        String pagina_requisiti = "Requisiti di partecipazione / qualificazione";

        String selectW3REQU = "select id_categoria, classe_importo from w3requ where schedacompleta_id = ? and prevalente = '1'";
        List datiW3REQU = sqlManager.getVector(selectW3REQU,
            new Object[] { schedacompleta_id });
        if (datiW3REQU != null && datiW3REQU.size() > 0) {

          String id_categoria = (String) SqlManager.getValueFromVectorParam(
              datiW3REQU, 0).getValue();
          String classe_importo = (String) SqlManager.getValueFromVectorParam(
              datiW3REQU, 1).getValue();

          // Controllo aggiunto perché nella definizione XSD è sempre
          // obbligatorio
          if (id_categoria == null)
            this.addCampoObbligatorio(listaControlli, "W3REQU", "ID_CATEGORIA",
                pagina_requisiti);
          // Controllo aggiunto perché nella definizione XSD è sempre
          // obbligatorio
          if (classe_importo == null)
            this.addCampoObbligatorio(listaControlli, "W3REQU",
                "CLASSE_IMPORTO", pagina_requisiti);

          // if ("L".equals(tipo_contratto) && "O".equals(flag_ente_speciale)) {
          // if (id_categoria == null)
          // this.addCampoObbligatorio(listaControlli, "W3REQU",
          // "ID_CATEGORIA", pagina_requisiti);
          // if (classe_importo == null)
          // this.addCampoObbligatorio(listaControlli, "W3REQU",
          // "CLASSE_IMPORTO", pagina_requisiti);
          // }
          //
          // if ("S".equals(tipo_contratto) || "F".equals(tipo_contratto)) {
          // if ("O".equals(flag_ente_speciale)
          // && importo_lavori != null
          // && importo_lavori.doubleValue() > 0) {
          // String messaggio = "Non è stato digitato alcun valore";
          // if (id_categoria == null)
          // this.addAvviso(listaControlli, "W3REQU", "ID_CATEGORIA", "W",
          // pagina_requisiti, messaggio);
          // if (classe_importo == null)
          // this.addAvviso(listaControlli, "W3REQU", "CLASSE_IMPORTO", "W",
          // pagina_requisiti, messaggio);
          // }
          // }

        }

        // Controllo categorie scorporabili / subappaltabili
        if ("O".equals(flag_ente_speciale)) {
          Long conteggio = (Long) sqlManager.getObject(
              "select count(*) from w3requ where schedacompleta_id = ? and scorporabile = '1' and subappaltabile = '1'",
              new Object[] { schedacompleta_id });
          if (conteggio == null
              || (conteggio != null && new Long(0).equals(conteggio))) {
            String messaggio = "Non è stata selezionata alcuna categoria scorporabile / subappaltabile";
            listaControlli.add(((Object) (new Object[] { "W",
                "Categorie scorporabili", pagina_requisiti, messaggio })));
          }
        }

        // Controllo categorie scorporabili / non subappaltabili
        if ("O".equals(flag_ente_speciale)) {
          Long conteggio = (Long) sqlManager.getObject(
              "select count(*) from w3requ where schedacompleta_id = ? and scorporabile = '1' and subappaltabile = '2'",
              new Object[] { schedacompleta_id });
          if (conteggio == null
              || (conteggio != null && new Long(0).equals(conteggio))) {
            String messaggio = "Non è stata selezionata alcuna categoria scorporabile / non subappaltabile";
            listaControlli.add(((Object) (new Object[] { "W",
                "Categorie scorporabili", pagina_requisiti, messaggio })));
          }
        }

        // Requisiti per settori speciali
        String criteri_selezione = (String) SqlManager.getValueFromVectorParam(
            datiW3APPA, 18).getValue();
        String sistema_qualificazione = (String) SqlManager.getValueFromVectorParam(
            datiW3APPA, 19).getValue();
        if ("S".equals(flag_ente_speciale)) {

        } else if ("O".equals(flag_ente_speciale)) {
          String messaggio = "Dato non compilabile in quanto il CIG non appartiene al settore SPECIALE";
          if (criteri_selezione != null)
            this.addAvviso(listaControlli, nomeTabella,
                "CRITERI_SELEZIONE_STABILITI_SA", "E", pagina_requisiti,
                messaggio);
          if (sistema_qualificazione != null)
            this.addAvviso(listaControlli, nomeTabella,
                "SISTEMA_QUALIFICAZIONE", "E", pagina_requisiti, messaggio);
        }
      }

    } catch (SQLException e) {
      throw new GestoreException(
          "Errore nella lettura delle informazioni relative alla fase di aggiudicazione: prima parte",
          "validazioneW3APPA1", e);
    }

    try {
      String nomeTabella = "W3APPA";

      String selectW3APPA = "select data_manif_interesse, data_scadenza_richiesta_invito, data_invito, "
          + " data_scadenza_pres_offerta, num_manif_interesse, num_imprese_richiedenti, "
          + " num_imprese_invitate, num_imprese_offerenti, num_offerte_ammesse, "
          + " offerta_massimo, offerta_minima, val_soglia_anomalia, "
          + " num_offerte_fuori_soglia, num_offerte_escluse, num_imp_escl_insuf_giust "
          + " from w3appa where schedacompleta_id = ?";
      List datiW3APPA = sqlManager.getVector(selectW3APPA,
          new Object[] { schedacompleta_id });

      if (datiW3APPA != null && datiW3APPA.size() > 0) {

        String pagina = "Inviti e offerte / soglia di anomalia";

        Long id_modo_gara = (Long) sqlManager.getObject(
            "select id_modo_gara from w3appa where schedacompleta_id = ?",
            new Object[] { schedacompleta_id });
        Long id_modo_indizione = (Long) sqlManager.getObject(
            "select id_modo_indizione from w3appa where schedacompleta_id = ?",
            new Object[] { schedacompleta_id });
        Long id_scelta_contraente = (Long) sqlManager.getObject(
            "select id_scelta_contraente from w3appa where schedacompleta_id = ?",
            new Object[] { schedacompleta_id });
        String tipo_contratto = (String) sqlManager.getObject(
            "select tipo_contratto from w3daco where scheda_id = ?",
            new Object[] { schedacompleta_id });

        // Data scadenza per le presentazione delle manifestazioni di interesse
        Date data_manif_interesse = (Date) SqlManager.getValueFromVectorParam(
            datiW3APPA, 0).getValue();
        if (id_modo_indizione != null && id_modo_indizione.longValue() == 1) {
          if (data_manif_interesse == null) {
            String messaggio = "Nel caso di \"Avviso periodico indicativo\" la data di scadenza dovrebbe essere indicata";
            this.addAvviso(listaControlli, nomeTabella, "DATA_MANIF_INTERESSE",
                "W", pagina, messaggio);
          }
        }

        // Data scadenza per la presentazione della richiesta di invito
        Date data_scadenza_richiesta_invito = (Date) SqlManager.getValueFromVectorParam(
            datiW3APPA, 1).getValue();
        if (id_scelta_contraente != null
            && (id_scelta_contraente.longValue() == 2 || id_scelta_contraente.longValue() == 9)) {
          if (data_scadenza_richiesta_invito == null) {
            String messaggio = "Nel caso di \"Procedura ristretta\" o \"Procedura negoziata previa pubblicazione\" la data di scadenza dovrebbe essere indicata";
            this.addAvviso(listaControlli, nomeTabella,
                "DATA_SCADENZA_RICHIESTA_INVITO", "W", pagina, messaggio);
          }
        }

        // Data di invito
        Date data_invito = (Date) SqlManager.getValueFromVectorParam(
            datiW3APPA, 2).getValue();

        // Data scadenza per la presentazione delle offerte
        if (SqlManager.getValueFromVectorParam(datiW3APPA, 3).getValue() == null) {
          this.addCampoObbligatorio(listaControlli, nomeTabella,
              "DATA_SCADENZA_PRES_OFFERTA", pagina);
        }

        // Soggetti che hanno presentato manifestazione di interesse
        Long num_manif_interesse = (Long) SqlManager.getValueFromVectorParam(
            datiW3APPA, 4).getValue();
        // Controllo aggiunto perché nella definizione XSD è sempre obbligatorio
        if (num_manif_interesse == null) {
          this.addCampoObbligatorio(listaControlli, nomeTabella,
              "NUM_MANIF_INTERESSE", pagina);
        }

        if (data_manif_interesse != null) {
          if (num_manif_interesse == null
              || (num_manif_interesse != null && num_manif_interesse.longValue() == 0)) {
            String messaggio = "Non è stato indicato il numero (deve essere > 0)";
            this.addAvviso(listaControlli, nomeTabella, "NUM_MANIF_INTERESSE",
                "E", pagina, messaggio);
          }
        }

        // Soggetti che hanno presentato richiesta di invito
        Long num_imprese_richiedenti = (Long) SqlManager.getValueFromVectorParam(
            datiW3APPA, 5).getValue();
        if (data_scadenza_richiesta_invito != null
            && num_imprese_richiedenti == null) {
          String messaggio = "Non è stato indicato il numero";
          this.addAvviso(listaControlli, nomeTabella,
              "NUM_IMPRESE_RICHIEDENTI", "E", pagina, messaggio);
        }

        // Soggetti invitati a presentare offerta
        Long num_imprese_invitate = (Long) SqlManager.getValueFromVectorParam(
            datiW3APPA, 6).getValue();

        // Controllo aggiunto perché nella definizione XSD è sempre obbligatorio
        if (num_imprese_invitate == null) {
          this.addCampoObbligatorio(listaControlli, nomeTabella,
              "NUM_IMPRESE_INVITATE", pagina);
        }

        if (data_invito != null) {
          if (num_imprese_invitate == null
              || (num_imprese_invitate != null && num_imprese_invitate.longValue() == 0)) {
            String messaggio = "Non è stato indicato il numero (deve essere > 0)";
            this.addAvviso(listaControlli, nomeTabella, "NUM_IMPRESE_INVITATE",
                "E", pagina, messaggio);
          }
        }

        // Soggetti che hanno presentato offerta
        Long num_imprese_offerenti = (Long) SqlManager.getValueFromVectorParam(
            datiW3APPA, 7).getValue();
        if (num_imprese_offerenti == null) {
          this.addCampoObbligatorio(listaControlli, nomeTabella,
              "NUM_IMPRESE_OFFERENTI", pagina);
        }

        if (num_imprese_offerenti != null && num_imprese_invitate == null) {
          if (num_imprese_offerenti.longValue() < 0) {
            String messaggio = "Il valore digitato ("
                + num_imprese_offerenti.toString()
                + ") deve essere maggiore o uguale a 0";
            this.addAvviso(listaControlli, nomeTabella,
                "NUM_IMPRESE_OFFERENTI", "E", pagina, messaggio);
          }
        }

        if (num_imprese_offerenti != null && num_imprese_invitate != null) {
          if (num_imprese_offerenti.longValue() < 0
              || (num_imprese_offerenti.longValue() > num_imprese_invitate.longValue())) {
            String messaggio = "Il valore digitato ("
                + num_imprese_offerenti.toString()
                + ") deve essere compreso tra "
                + " 0 e "
                + num_imprese_invitate.toString();
            this.addAvviso(listaControlli, nomeTabella,
                "NUM_IMPRESE_OFFERENTI", "E", pagina, messaggio);
          }
        }

        // Offerte ammesse
        Long num_offerte_ammesse = (Long) SqlManager.getValueFromVectorParam(
            datiW3APPA, 8).getValue();
        if (num_offerte_ammesse == null) {
          this.addCampoObbligatorio(listaControlli, nomeTabella,
              "NUM_OFFERTE_AMMESSE", pagina);
        }

        if (num_offerte_ammesse != null && num_imprese_offerenti == null) {
          if (num_offerte_ammesse.longValue() < 0) {
            String messaggio = "Il valore digitato ("
                + num_offerte_ammesse.toString()
                + ") deve essere maggiore o uguale a 0";
            this.addAvviso(listaControlli, nomeTabella, "NUM_OFFERTE_AMMESSE",
                "E", pagina, messaggio);
          }
        }

        if (num_offerte_ammesse != null && num_imprese_offerenti != null) {
          if (num_offerte_ammesse.longValue() < 0
              || (num_offerte_ammesse.longValue() > num_imprese_offerenti.longValue())) {
            String messaggio = "Il valore digitato ("
                + num_offerte_ammesse.toString()
                + ") deve essere compreso tra "
                + " 0 e "
                + num_imprese_offerenti.toString();
            this.addAvviso(listaControlli, nomeTabella, "NUM_OFFERTE_AMMESSE",
                "E", pagina, messaggio);
          }
        }

        // Offerta di massimo ribasso
        Double offerta_massimo = (Double) SqlManager.getValueFromVectorParam(
            datiW3APPA, 9).getValue();
        Double offerta_minima = (Double) SqlManager.getValueFromVectorParam(
            datiW3APPA, 10).getValue();

        if ("L".equals(tipo_contratto)) {
          if (num_offerte_ammesse != null
              && num_offerte_ammesse.longValue() > 1) {
            if (id_modo_gara != null && id_modo_gara.longValue() == 1) {
              if (offerta_massimo == null)
                this.addCampoObbligatorio(listaControlli, nomeTabella,
                    "OFFERTA_MASSIMO", pagina);
              if (offerta_minima == null)
                this.addCampoObbligatorio(listaControlli, nomeTabella,
                    "OFFERTA_MINIMA", pagina);
            }
          }
        }

        if ("S".equals(tipo_contratto) || "F".equals(tipo_contratto)) {
          if (id_modo_gara != null && id_modo_gara.longValue() == 1) {
            if (offerta_massimo == null)
              this.addCampoObbligatorio(listaControlli, nomeTabella,
                  "OFFERTA_MASSIMO", pagina);
            if (offerta_minima == null)
              this.addCampoObbligatorio(listaControlli, nomeTabella,
                  "OFFERTA_MINIMA", pagina);
          }
        }

        if (offerta_massimo != null && offerta_minima != null) {
          if (offerta_minima.longValue() > offerta_massimo.longValue()) {
            String messaggio = "Il valore digitato ("
                + offerta_minima.toString()
                + ") non pu&ograve; essere superiore "
                + " a quanto indicato al campo \""
                + this.getDescrizioneCampo(nomeTabella, "OFFERTA_MASSIMO")
                + "\" ("
                + offerta_massimo.toString()
                + ")";
            this.addAvviso(listaControlli, nomeTabella, "OFFERTA_MINIMA", "E",
                pagina, messaggio);
          }
        }

        Double val_soglia_anomalia = (Double) SqlManager.getValueFromVectorParam(
            datiW3APPA, 11).getValue();
        if (id_modo_gara != null
            && id_modo_gara.longValue() == 2
            && val_soglia_anomalia != null) {
          String messaggio = "Dato non richiesto in relazione al criterio di aggiudicazione adottato";
          this.addAvviso(listaControlli, nomeTabella, "VAL_SOGLIA_ANOMALIA",
              "E", pagina, messaggio);
        }

        if (val_soglia_anomalia != null
            && num_offerte_ammesse != null
            && num_offerte_ammesse.longValue() >= 5) {
          if (offerta_massimo != null && offerta_minima != null) {
            if ((val_soglia_anomalia.longValue() > offerta_massimo.longValue())
                || (val_soglia_anomalia.longValue() < offerta_minima.longValue())) {
              String messaggio = "Il valore digitato ("
                  + val_soglia_anomalia.toString()
                  + ") deve essere compreso tra "
                  + " "
                  + offerta_minima.toString()
                  + " e "
                  + offerta_massimo.toString();
              this.addAvviso(listaControlli, nomeTabella,
                  "VAL_SOGLIA_ANOMALIA", "E", pagina, messaggio);
            }
          }
        }

        // Numero offerte fuori soglia
        Long num_offerte_fuori_soglia = (Long) SqlManager.getValueFromVectorParam(
            datiW3APPA, 12).getValue();

        // Controllo aggiunto perché nella definizione XSD è sempre obbligatorio
        if (num_offerte_fuori_soglia == null) {
          this.addCampoObbligatorio(listaControlli, nomeTabella,
              "NUM_OFFERTE_FUORI_SOGLIA", pagina);
        }

        if (num_offerte_ammesse != null) {
          if (num_offerte_fuori_soglia != null) {
            if (num_offerte_fuori_soglia.longValue() < 0
                || num_offerte_fuori_soglia.longValue() > num_offerte_ammesse.longValue()) {
              String messaggio = "Il valore digitato ("
                  + num_offerte_fuori_soglia.toString()
                  + ") deve essere compreso "
                  + " tra 0 e "
                  + num_offerte_ammesse.toString();
              this.addAvviso(listaControlli, nomeTabella,
                  "NUM_OFFERTE_FUORI_SOGLIA", "E", pagina, messaggio);
            }
          }

          // Numero offerte escluse
          Long num_offerte_escluse = (Long) SqlManager.getValueFromVectorParam(
              datiW3APPA, 13).getValue();

          if (num_offerte_escluse == null) {
            this.addCampoObbligatorio(listaControlli, nomeTabella,
                "NUM_OFFERTE_ESCLUSE", pagina);
          }

          if (num_offerte_escluse != null) {
            if (num_offerte_escluse.longValue() < 0
                || num_offerte_escluse.longValue() > num_offerte_ammesse.longValue()) {
              String messaggio = "Il valore digitato ("
                  + num_offerte_escluse.toString()
                  + ") deve essere compreso "
                  + " tra 0 e "
                  + num_offerte_ammesse.toString();
              this.addAvviso(listaControlli, nomeTabella,
                  "NUM_OFFERTE_ESCLUSE", "E", pagina, messaggio);
            }
          }

          // Numero escluse insuff. giust.
          Long num_imp_escl_insuf_giust = (Long) SqlManager.getValueFromVectorParam(
              datiW3APPA, 14).getValue();

          // Controllo aggiunto perché nella definizione XSD è sempre
          // obbligatorio
          if (num_imp_escl_insuf_giust == null) {
            this.addCampoObbligatorio(listaControlli, nomeTabella,
                "NUM_IMP_ESCL_INSUF_GIUST", pagina);
          }

          if (num_imp_escl_insuf_giust != null) {
            if (num_imp_escl_insuf_giust.longValue() < 0
                || num_imp_escl_insuf_giust.longValue() > num_offerte_ammesse.longValue()) {
              String messaggio = "Il valore digitato ("
                  + num_imp_escl_insuf_giust.toString()
                  + ") deve essere compreso "
                  + " tra 0 e "
                  + num_offerte_ammesse.toString();
              this.addAvviso(listaControlli, nomeTabella,
                  "NUM_IMP_ESCL_INSUF_GIUST", "E", pagina, messaggio);
            }
          }
        }
      }

    } catch (SQLException e) {
      throw new GestoreException(
          "Errore nella lettura delle informazioni relative alla fase di aggiudicazione: seconda parte",
          "validazioneW3APPA2", e);
    }

    try {
      String nomeTabella = "W3APPA";

      String selectW3APPA = "select perc_ribasso_agg, perc_off_aumento, importo_aggiudicazione, "
          + " data_verb_aggiudicazione, flag_rich_subappalto, "
          + " importo_subtotale, importo_attuazione_sicurezza, imp_non_assog, importo_progettazione "
          + " from w3appa where schedacompleta_id = ?";
      List datiW3APPA = sqlManager.getVector(selectW3APPA,
          new Object[] { schedacompleta_id });

      if (datiW3APPA != null && datiW3APPA.size() > 0) {

        String pagina = "Aggiudicazione / affidamento";

        Long id_modo_gara = (Long) sqlManager.getObject(
            "select id_modo_gara from w3appa where schedacompleta_id = ?",
            new Object[] { schedacompleta_id });

        // Percentuale ribasso
        Double perc_ribasso_agg = (Double) SqlManager.getValueFromVectorParam(
            datiW3APPA, 0).getValue();
        if (id_modo_gara != null && id_modo_gara.longValue() == 1) {
          if (perc_ribasso_agg == null)
            this.addCampoObbligatorio(listaControlli, nomeTabella,
                "PERC_RIBASSO_AGG", pagina);
        }

        // Offerta in aumento
        Double perc_off_aumento = (Double) SqlManager.getValueFromVectorParam(
            datiW3APPA, 1).getValue();
        if ((perc_ribasso_agg == null && perc_off_aumento == null)
            || (perc_ribasso_agg != null && perc_off_aumento != null)) {
          String descrizione = this.getDescrizioneCampo(nomeTabella,
              "PERC_RIBASSO_AGG");
          descrizione += ", "
              + this.getDescrizioneCampo(nomeTabella, "PERC_OFF_AUMENTO");
          String messaggio = "Valorizzare solamente uno dei due campi";
          listaControlli.add(((Object) (new Object[] { "E", pagina,
              descrizione, messaggio })));
        }

        // Importo di aggiudicazione
        Double importo_aggiudicazione = (Double) SqlManager.getValueFromVectorParam(
            datiW3APPA, 2).getValue();
        if (importo_aggiudicazione == null) {
          this.addCampoObbligatorio(listaControlli, nomeTabella,
              "IMPORTO_AGGIUDICAZIONE", pagina);
        }

        if (importo_aggiudicazione != null
            && importo_aggiudicazione.doubleValue() == 0) {
          String messaggio = "L\'importo digitato deve essere >0";
          this.addAvviso(listaControlli, nomeTabella, "IMPORTO_AGGIUDICAZIONE",
              "E", pagina, messaggio);
        }

        // Verifica dell'importo di aggiudicazione
        if (importo_aggiudicazione != null
            && importo_aggiudicazione.doubleValue() > 0
            && (perc_ribasso_agg != null || perc_off_aumento != null)) {

          double importo_subtotale = 0;
          if (SqlManager.getValueFromVectorParam(datiW3APPA, 5).getValue() != null) {
            importo_subtotale = SqlManager.getValueFromVectorParam(datiW3APPA,
                5).doubleValue().doubleValue();
          }

          double importo_attuazione_sicurezza = 0;
          if (SqlManager.getValueFromVectorParam(datiW3APPA, 6).getValue() != null) {
            importo_attuazione_sicurezza = SqlManager.getValueFromVectorParam(
                datiW3APPA, 6).doubleValue().doubleValue();
          }

          double imp_non_assog = 0;
          if (SqlManager.getValueFromVectorParam(datiW3APPA, 7).getValue() != null) {
            imp_non_assog = SqlManager.getValueFromVectorParam(datiW3APPA, 7).doubleValue().doubleValue();
          }

          double importo_progettazione = 0;
          if (SqlManager.getValueFromVectorParam(datiW3APPA, 8).getValue() != null) {
            importo_progettazione = SqlManager.getValueFromVectorParam(
                datiW3APPA, 8).doubleValue().doubleValue();
          }

          double importo_aggiudicazione_calcolato;
          importo_aggiudicazione_calcolato = (importo_subtotale + importo_progettazione);

          if (perc_ribasso_agg != null) {
            importo_aggiudicazione_calcolato = importo_aggiudicazione_calcolato
                * (1 - perc_ribasso_agg.doubleValue() / 100);
          } else if (perc_off_aumento != null) {
            importo_aggiudicazione_calcolato = importo_aggiudicazione_calcolato
                * (1 + perc_off_aumento.doubleValue() / 100);
          }

          importo_aggiudicazione_calcolato += importo_attuazione_sicurezza
              + imp_non_assog;

          importo_aggiudicazione_calcolato = round(
              importo_aggiudicazione_calcolato, 2);

          if (importo_aggiudicazione.doubleValue() != importo_aggiudicazione_calcolato) {
            String messaggio = "Il valore digitato ("
                + this.importoToStringa(importo_aggiudicazione)
                + ") "
                + " non è congruente con il valore calcolato "
                + this.importoToStringa(new Double(
                    importo_aggiudicazione_calcolato))
                + ": "
                + " verificare l\'importo digitato";
            this.addAvviso(listaControlli, nomeTabella,
                "IMPORTO_AGGIUDICAZIONE", "W", pagina, messaggio);
          }

        }

        // Data di aggiudicazione
        Date data_verb_aggiudicazione = (Date) SqlManager.getValueFromVectorParam(
            datiW3APPA, 3).getValue();
        if (data_verb_aggiudicazione == null) {
          this.addCampoObbligatorio(listaControlli, nomeTabella,
              "DATA_VERB_AGGIUDICAZIONE", pagina);
        } else {
          Date dataMinima = new Date(108, 0, 1);
          Date dataOdierna = new Date();

          if (data_verb_aggiudicazione.getTime() > dataOdierna.getTime()
              || data_verb_aggiudicazione.getTime() < dataMinima.getTime()) {
            String messaggio = "La data digitata ("
                + UtilityDate.convertiData(data_verb_aggiudicazione,
                    UtilityDate.FORMATO_GG_MM_AAAA)
                + ") "
                + "deve essere compresa tra il "
                + UtilityDate.convertiData(dataMinima,
                    UtilityDate.FORMATO_GG_MM_AAAA)
                + " ed il "
                + UtilityDate.convertiData(dataOdierna,
                    UtilityDate.FORMATO_GG_MM_AAAA);
            this.addAvviso(listaControlli, nomeTabella,
                "DATA_VERB_AGGIUDICAZIONE", "E", pagina, messaggio);
          }

          // Controllo con data di scadenza presentazione offerte
          Date data_scadenza_pres_offerta = (Date) sqlManager.getObject(
              "select data_scadenza_pres_offerta from w3appa where schedacompleta_id = ?",
              new Object[] { schedacompleta_id });
          if (data_scadenza_pres_offerta != null) {
            if (data_verb_aggiudicazione.getTime() < data_scadenza_pres_offerta.getTime()) {
              String messaggio = this.getMessaggioConfrontoDate(
                  data_verb_aggiudicazione, ">=", data_scadenza_pres_offerta,
                  nomeTabella, "DATA_SCADENZA_PRES_OFFERTA", null);
              this.addAvviso(listaControlli, nomeTabella,
                  "DATA_VERB_AGGIUDICAZIONE", "W", pagina, messaggio);
            }
          }

        }

        if (SqlManager.getValueFromVectorParam(datiW3APPA, 4).getValue() == null) {
          this.addCampoObbligatorio(listaControlli, nomeTabella,
              "FLAG_RICH_SUBAPPALTO", pagina);
        }

        // Controllo numero incaricati - deve essere indicato almeno un
        // incaricato
        Long conteggiow3incara = (Long) sqlManager.getObject(
            "select count(*) from w3inca where schedacompleta_id = ? and sezione='RA'",
            new Object[] { schedacompleta_id });
        if (conteggiow3incara == null
            || (conteggiow3incara != null && new Long(0).equals(conteggiow3incara))) {
          String messaggio = "Deve essere indicato almeno un incaricato";
          listaControlli.add(((Object) (new Object[] { "W",
              "Soggetti ai quali sono stati conferiti incarichi",
              "Soggetti ai quali sono stati conferiti incarichi", messaggio })));
        }

        Long conteggiow3incaraRUP = (Long) sqlManager.getObject(
            "select count(*) from w3inca where schedacompleta_id = ? and sezione='RA' and id_ruolo = 14",
            new Object[] { schedacompleta_id });
        if (conteggiow3incaraRUP == null
            || (conteggiow3incaraRUP != null && new Long(0).equals(conteggiow3incaraRUP))) {
          String messaggio = "Deve essere indicato il R.U.P.";
          listaControlli.add(((Object) (new Object[] { "E",
              "Soggetti ai quali sono stati conferiti incarichi",
              "Soggetti ai quali sono stati conferiti incarichi", messaggio })));
        }

      }

    } catch (SQLException e) {
      throw new GestoreException(
          "Errore nella lettura delle informazioni relative alla fase di aggiudicazione: terza parte",
          "validazioneW3APPA", e);
    }

    // Controllo altre pagine
    try {

      String flag_ente_speciale = (String) sqlManager.getObject(
          "select flag_ente_speciale from w3daco where scheda_id = ?",
          new Object[] { schedacompleta_id });

      this.validazioneW3INCA(sqlManager, schedacompleta_id, listaControlli,
          "PA");
      if ("O".equals(flag_ente_speciale))
        this.validazioneW3FINA(sqlManager, schedacompleta_id, listaControlli);
      this.validazioneW3REQU(sqlManager, schedacompleta_id, listaControlli);
      this.validazioneW3AGGI(sqlManager, schedacompleta_id, listaControlli,
          new Long(1));
      this.validazioneW3INCA(sqlManager, schedacompleta_id, listaControlli,
          "RA");

    } catch (SQLException e) {
      throw new GestoreException(
          "Errore nella lettura delle informazioni relative alla fase di aggiudicazione: quarta parte",
          "validazioneW3APPA", e);
    }

    if (logger.isDebugEnabled())
      logger.debug("validazioneW3APPA: fine metodo");
  }

  /**
   * Controllo dati per la fase iniziale (W3INIZ)
   * 
   * @param sqlManager
   * @param schedacompleta_id
   * @param listaControlli
   * @throws GestoreException
   */
  private void validazioneW3INIZ(SqlManager sqlManager, Long schedacompleta_id,
      List listaControlli) throws GestoreException {
    if (logger.isDebugEnabled())
      logger.debug("validazioneW3INIZ: inizio metodo");

    try {
      String nomeTabella = "W3PUES";
      String selectW3PUES = "select data_guce, data_guri, quotidiani_naz, "
          + " quotidiani_reg, profilo_committente, sito_ministero_inf_trasp,"
          + " sito_osservatorio_cp "
          + " from w3pues where schedacompleta_id = ?";
      List datiW3PUES = sqlManager.getVector(selectW3PUES,
          new Object[] { schedacompleta_id });

      if (datiW3PUES != null && datiW3PUES.size() > 0) {

        String pagina = "Pubblicazione esito procedura di selezione";

        Date data_verb_aggiudicazione = (Date) sqlManager.getObject(
            "select data_verb_aggiudicazione from w3appa where schedacompleta_id = ?",
            new Object[] { schedacompleta_id });

        // Data_guce
        Date data_guce = (Date) SqlManager.getValueFromVectorParam(datiW3PUES,
            0).getValue();
        if (data_guce != null && data_verb_aggiudicazione != null) {
          if (data_guce.getTime() <= data_verb_aggiudicazione.getTime()) {
            String messaggio = this.getMessaggioConfrontoDate(data_guce, ">",
                data_verb_aggiudicazione, "W3APPA", "DATA_VERB_AGGIUDICAZIONE",
                new Long(1));
            this.addAvviso(listaControlli, nomeTabella, "DATA_GUCE", "E",
                pagina, messaggio);
          }
        }

        // Data_guri
        Date data_guri = (Date) SqlManager.getValueFromVectorParam(datiW3PUES,
            1).getValue();
        if (data_guri != null && data_verb_aggiudicazione != null) {
          if (data_guri.getTime() <= data_verb_aggiudicazione.getTime()) {
            String messaggio = this.getMessaggioConfrontoDate(data_guri, ">",
                data_verb_aggiudicazione, "W3APPA", "DATA_VERB_AGGIUDICAZIONE",
                new Long(1));
            this.addAvviso(listaControlli, nomeTabella, "DATA_GURI", "E",
                pagina, messaggio);
          }
        }

        // Quotidiani_naz
        Long quotidiani_naz = (Long) SqlManager.getValueFromVectorParam(
            datiW3PUES, 2).getValue();
        if (quotidiani_naz == null) {
          this.addCampoObbligatorio(listaControlli, nomeTabella,
              "QUOTIDIANI_NAZ", pagina);
        }
        if (quotidiani_naz != null && quotidiani_naz.longValue() > 20) {
          String messaggio = "Il valore digitato ("
              + quotidiani_naz.toString()
              + ") è maggiore di 20: verificare il valore";
          this.addAvviso(listaControlli, nomeTabella, "QUOTIDIANI_NAZ", "E",
              pagina, messaggio);
        }

        // Quotidiani_reg
        Long quotidiani_reg = (Long) SqlManager.getValueFromVectorParam(
            datiW3PUES, 3).getValue();
        if (quotidiani_reg == null) {
          this.addCampoObbligatorio(listaControlli, nomeTabella,
              "QUOTIDIANI_REG", pagina);
        }
        if (quotidiani_reg != null && quotidiani_reg.longValue() > 20) {
          String messaggio = "Il valore digitato ("
              + quotidiani_reg.toString()
              + ") è maggiore di 20: verificare il valore";
          this.addAvviso(listaControlli, nomeTabella, "QUOTIDIANI_REG", "E",
              pagina, messaggio);
        }

        // profilo_committente
        if (SqlManager.getValueFromVectorParam(datiW3PUES, 4).getValue() == null) {
          this.addCampoObbligatorio(listaControlli, nomeTabella,
              "PROFILO_COMMITTENTE", pagina);
        }

        // sito_ministero_inf_trasp
        if (SqlManager.getValueFromVectorParam(datiW3PUES, 5).getValue() == null) {
          this.addCampoObbligatorio(listaControlli, nomeTabella,
              "SITO_MINISTERO_INF_TRASP", pagina);
        }

        // sito_osservatorio_cp
        if (SqlManager.getValueFromVectorParam(datiW3PUES, 6).getValue() == null) {
          this.addCampoObbligatorio(listaControlli, nomeTabella,
              "SITO_OSSERVATORIO_CP", pagina);
        }
      }
    } catch (SQLException e) {
      throw new GestoreException(
          "Errore nella lettura delle informazioni relative alla fase iniziale: prima parte",
          "validazioneW3INIZ", e);
    }

    try {

      String nomeTabella = "W3INIZ";

      String selectW3INIZ = "select data_stipula, data_esecutivita, importo_cauzione,"
          + " data_ini_prog_esec, data_app_prog_esec, flag_frazionata, "
          + " data_verbale_cons, data_verbale_def, flag_riserva, "
          + " data_verb_inizio, data_termine "
          + " from w3iniz where schedacompleta_id = ?";
      List datiW3INIZ = sqlManager.getVector(selectW3INIZ,
          new Object[] { schedacompleta_id });

      if (datiW3INIZ != null && datiW3INIZ.size() > 0) {

        String pagina = "Contratto di appalto";

        // Data del verbale di aggiudicazione
        Date data_verb_aggiudicazione = (Date) sqlManager.getObject(
            "select data_verb_aggiudicazione from w3appa where schedacompleta_id = ?",
            new Object[] { schedacompleta_id });
        Date dataOdierna = new Date();

        // Data_stipula
        Date data_stipula = (Date) SqlManager.getValueFromVectorParam(
            datiW3INIZ, 0).getValue();
        if (data_stipula != null && data_verb_aggiudicazione != null) {
          if (data_stipula.getTime() < data_verb_aggiudicazione.getTime()) {
            String messaggio = this.getMessaggioConfrontoDate(data_stipula,
                ">=", data_verb_aggiudicazione, "W3APPA",
                "DATA_VERB_AGGIUDICAZIONE", new Long(1));
            this.addAvviso(listaControlli, nomeTabella, "DATA_STIPULA", "E",
                pagina, messaggio);
          }
        }

        // Data esecutivita
        Date data_esecutivita = (Date) SqlManager.getValueFromVectorParam(
            datiW3INIZ, 1).getValue();
        if (data_esecutivita != null) {
          String nomeCampo = "DATA_ESECUTIVITA";
          if (data_stipula != null) {
            if (data_esecutivita.getTime() < data_stipula.getTime()) {
              String messaggio = this.getMessaggioConfrontoDate(
                  data_esecutivita, ">=", data_stipula, "W3INIZ",
                  "DATA_STIPULA", null);
              this.addAvviso(listaControlli, nomeTabella, nomeCampo, "E",
                  pagina, messaggio);
            }
          }

          if (data_esecutivita.getYear() > dataOdierna.getYear()) {
            String messaggio = "L\'anno digitato è superiore all\'anno in corso: si è sicuri del valore della data digitata";
            this.addAvviso(listaControlli, nomeTabella, nomeCampo, "W", pagina,
                messaggio);
          }
        }

        // Importo cauzione
        if (SqlManager.getValueFromVectorParam(datiW3INIZ, 2).getValue() == null) {
          this.addCampoObbligatorio(listaControlli, nomeTabella,
              "IMPORTO_CAUZIONE", pagina);
        }

        pagina = "Termini di esecuzione";

        // data_ini_prog_esec
        // data_app_prog_esec
        Date data_ini_prog_esec = (Date) SqlManager.getValueFromVectorParam(
            datiW3INIZ, 3).getValue();
        Date data_app_prog_esec = (Date) SqlManager.getValueFromVectorParam(
            datiW3INIZ, 4).getValue();
        if (data_app_prog_esec != null && data_ini_prog_esec != null) {
          if (data_app_prog_esec.getTime() <= data_ini_prog_esec.getTime()) {
            String messaggio = this.getMessaggioConfrontoDate(
                data_app_prog_esec, ">", data_ini_prog_esec, "W3INIZ",
                "DATA_INI_PROG_ESEC", null);
            this.addAvviso(listaControlli, nomeTabella, "DATA_APP_PROG_ESEC",
                "E", pagina, messaggio);
          }
        }

        // flag_frazionata
        String flag_frazionata = (String) SqlManager.getValueFromVectorParam(
            datiW3INIZ, 5).getValue();
        if (flag_frazionata == null) {
          this.addCampoObbligatorio(listaControlli, nomeTabella,
              "FLAG_FRAZIONATA", pagina);
        }

        // flag_riserva
        String flag_riserva = (String) SqlManager.getValueFromVectorParam(
            datiW3INIZ, 8).getValue();

        // data_verbale_cons
        Date data_verbale_cons = (Date) SqlManager.getValueFromVectorParam(
            datiW3INIZ, 6).getValue();
        if (data_verbale_cons == null
            && (flag_frazionata != null && "1".equals(flag_frazionata))) {
          this.addCampoObbligatorio(listaControlli, nomeTabella,
              "DATA_VERBALE_CONS", pagina);
        }

        if (data_verbale_cons != null) {
          String nomeCampo = "DATA_VERBALE_CONS";

          if (flag_riserva != null && "2".equals(flag_riserva)) {
            if (data_stipula != null) {
              if (data_verbale_cons.getTime() < data_stipula.getTime()) {
                String messaggio = this.getMessaggioConfrontoDate(
                    data_verbale_cons, ">=", data_stipula, "W3INIZ",
                    "DATA_STIPULA", null);
                this.addAvviso(listaControlli, nomeTabella, nomeCampo, "W",
                    pagina, messaggio);
              }
            }
          }
          if (data_verbale_cons.getYear() > dataOdierna.getYear()) {
            String messaggio = "L\'anno digitato è superiore all\'anno in corso: si è sicuri del valore della data digitata?";
            this.addAvviso(listaControlli, nomeTabella, nomeCampo, "W", pagina,
                messaggio);
          }
        }

        // data_verbale_def
        Date data_verbale_def = (Date) SqlManager.getValueFromVectorParam(
            datiW3INIZ, 7).getValue();
        if (data_verbale_def == null
            && (flag_frazionata != null && "2".equals(flag_frazionata))) {
          this.addCampoObbligatorio(listaControlli, nomeTabella,
              "DATA_VERBALE_DEF", pagina);
        }

        if (data_verbale_def != null) {
          String nomeCampo = "DATA_VERBALE_DEF";
          if (flag_riserva != null && "2".equals(flag_riserva)) {
            if (data_stipula != null) {
              if (data_verbale_def.getTime() < data_stipula.getTime()) {
                String messaggio = this.getMessaggioConfrontoDate(
                    data_verbale_def, ">=", data_stipula, "W3INIZ",
                    "DATA_STIPULA", null);
                this.addAvviso(listaControlli, nomeTabella, nomeCampo, "W",
                    pagina, messaggio);
              }
            }
          }
          if (data_verbale_def.getYear() > dataOdierna.getYear()) {
            String messaggio = "L\'anno digitato è superiore all\'anno in corso: si è sicuri del valore della data digitata?";
            this.addAvviso(listaControlli, nomeTabella, nomeCampo, "W", pagina,
                messaggio);
          }
        }

        if (flag_riserva == null) {
          this.addCampoObbligatorio(listaControlli, nomeTabella,
              "FLAG_RISERVA", pagina);
        }

        // data_verb_inizio
        Date data_verb_inizio = (Date) SqlManager.getValueFromVectorParam(
            datiW3INIZ, 9).getValue();
        if (data_verb_inizio != null) {
          String nomeCampo = "DATA_VERB_INIZIO";
          if (flag_riserva != null && "2".equals(flag_riserva)) {
            if (data_stipula != null) {
              if (data_verb_inizio.getTime() < data_stipula.getTime()) {
                String messaggio = this.getMessaggioConfrontoDate(
                    data_verb_inizio, ">=", data_stipula, "W3INIZ",
                    "DATA_STIPULA", null);
                this.addAvviso(listaControlli, nomeTabella, nomeCampo, "W",
                    pagina, messaggio);
              }
            }
          }
          if (data_verb_inizio.getYear() > dataOdierna.getYear()) {
            String messaggio = "L\'anno digitato è superiore all\'anno in corso: si è sicuri del valore della data digitata?";
            this.addAvviso(listaControlli, nomeTabella, nomeCampo, "W", pagina,
                messaggio);
          }
        }

        // Data_termine
        Date data_termine = (Date) SqlManager.getValueFromVectorParam(
            datiW3INIZ, 10).getValue();
        if (data_termine != null) {
          String nomeCampo = "DATA_TERMINE";
          if (flag_frazionata != null && "2".equals(flag_frazionata)) {
            if (data_stipula != null) {
              if (data_termine.getTime() <= data_stipula.getTime()) {
                String messaggio = this.getMessaggioConfrontoDate(data_termine,
                    ">", data_stipula, "W3INIZ", "DATA_STIPULA", null);
                this.addAvviso(listaControlli, nomeTabella, nomeCampo, "E",
                    pagina, messaggio);
              }
            }
          } else if (flag_frazionata != null && "1".equals(flag_frazionata)) {
            this.addAvviso(listaControlli, nomeTabella, nomeCampo, "W", pagina,
                "Attenzione! Si tratta di consegna frazionata");
          }

          if (data_termine.getYear() > dataOdierna.getYear()) {
            String messaggio = "L\'anno digitato è superiore all\'anno in corso: si è sicuri del valore della data digitata?";
            this.addAvviso(listaControlli, nomeTabella, nomeCampo, "W", pagina,
                messaggio);
          }
        }

      }
    } catch (SQLException e) {
      throw new GestoreException(
          "Errore nella lettura delle informazioni relative alla fase iniziale: seconda parte",
          "validazioneW3INIZ", e);
    }

    this.validazioneW3AGGI(sqlManager, schedacompleta_id, listaControlli,
        new Long(2));
    this.validazioneW3INCA(sqlManager, schedacompleta_id, listaControlli, "IN");

    if (logger.isDebugEnabled())
      logger.debug("validazioneW3INIZ: fine metodo");

  }

  /**
   * Controllo dati per la fase di avanzamento (W3AVAN)
   * 
   * @param sqlManager
   * @param schedacompleta_id
   * @param num
   * @param listaControlli
   * @throws GestoreException
   */
  private void validazioneW3AVAN(SqlManager sqlManager, Long schedacompleta_id,
      Long num, List listaControlli) throws GestoreException {
    if (logger.isDebugEnabled())
      logger.debug("validazioneW3AVAN: inizio metodo");

    String nomeTabella = "W3AVAN";
    String nomeCampo = "";

    try {
      String selectW3AVAN = "select flag_pagamento, importo_anticipazione, data_anticipazione, "
          + " data_raggiungimento, importo_sal, data_certificato, importo_certificato, "
          + " flag_ritardo, num_giorni_scost, num_giorni_proroga from w3avan where schedacompleta_id = ? and num = ?";
      List datiW3AVAN = sqlManager.getVector(selectW3AVAN, new Object[] {
          schedacompleta_id, num });

      if (datiW3AVAN != null && datiW3AVAN.size() > 0) {

        Double importo_compl_appalto = (Double) sqlManager.getObject(
            "select importo_compl_appalto from w3appa where schedacompleta_id = ?",
            new Object[] { schedacompleta_id });
        Date data_verb_aggiudicazione = (Date) sqlManager.getObject(
            "select data_verb_aggiudicazione from w3appa where schedacompleta_id = ?",
            new Object[] { schedacompleta_id });

        // Flag pagamento
        if (SqlManager.getValueFromVectorParam(datiW3AVAN, 0).getValue() == null) {
          nomeCampo = "FLAG_PAGAMENTO";
          this.addCampoObbligatorio(listaControlli, nomeTabella, nomeCampo,
              "Stato di avanzamento");
        }

        // Importo anticipazione
        Double importo_anticipazione = (Double) SqlManager.getValueFromVectorParam(
            datiW3AVAN, 1).getValue();
        if (num.longValue() > 1 && importo_anticipazione != null) {
          nomeCampo = "IMPORTO_ANTICIPAZIONE";
          String messaggio = "Anticipazione non ammessa in uno stato di avanzamento successivo al primo";
          this.addAvviso(listaControlli, nomeTabella, nomeCampo, "W",
              "Stato di avanzamento", messaggio);
        }

        if (importo_anticipazione != null && importo_compl_appalto != null) {
          if (importo_anticipazione.doubleValue() >= importo_compl_appalto.doubleValue()) {
            nomeCampo = "IMPORTO_ANTICIPAZIONE";
            String messaggio = this.getMessaggioControntoImporti(
                importo_anticipazione, "<", importo_compl_appalto, "W3APPA",
                "IMPORTO_COMPL_APPALTO", new Long(1));
            this.addAvviso(listaControlli, nomeTabella, nomeCampo, "E",
                "Stato di avanzamento", messaggio);
          }
        }

        // Data anticipazione
        Date data_anticipazione = (Date) SqlManager.getValueFromVectorParam(
            datiW3AVAN, 2).getValue();
        if (num.longValue() > 1 && data_anticipazione != null) {
          nomeCampo = "DATA_ANTICIPAZIONE";
          String messaggio = "Anticipazione non ammessa in uno stato di avanzamento successivo al primo";
          this.addAvviso(listaControlli, nomeTabella, nomeCampo, "W",
              "Stato di avanzamento", messaggio);
        }

        if (data_anticipazione != null && data_verb_aggiudicazione != null) {
          if (data_anticipazione.getTime() <= data_verb_aggiudicazione.getTime()) {
            nomeCampo = "DATA_ANTICIPAZIONE";
            String messaggio = this.getMessaggioConfrontoDate(
                data_anticipazione, ">", data_verb_aggiudicazione, "W3APPA",
                "DATA_VERB_AGGIUDICAZIONE", new Long(1));
            this.addAvviso(listaControlli, nomeTabella, nomeCampo, "E",
                "Stato di avanzamento", messaggio);
          }
        }

        // Data raggiungimento
        Date data_raggiungimento = (Date) SqlManager.getValueFromVectorParam(
            datiW3AVAN, 3).getValue();
        if (data_raggiungimento == null) {
          nomeCampo = "DATA_RAGGIUNGIMENTO";
          this.addCampoObbligatorio(listaControlli, nomeTabella, nomeCampo,
              "Stato di avanzamento");
        }
        if (data_raggiungimento != null && data_verb_aggiudicazione != null) {
          if (data_raggiungimento.getTime() <= data_verb_aggiudicazione.getTime()) {
            nomeCampo = "DATA_RAGGIUNGIMENTO";
            String messaggio = this.getMessaggioConfrontoDate(
                data_raggiungimento, ">", data_verb_aggiudicazione, "W3APPA",
                "DATA_VERB_AGGIUDICAZIONE", new Long(1));
            this.addAvviso(listaControlli, nomeTabella, nomeCampo, "E",
                "Stato di avanzamento", messaggio);
          }
        }

        // Importo SAL
        Double importo_sal = (Double) SqlManager.getValueFromVectorParam(
            datiW3AVAN, 4).getValue();
        if (importo_sal == null) {
          nomeCampo = "IMPORTO_SAL";
          this.addCampoObbligatorio(listaControlli, nomeTabella, nomeCampo,
              "Stato di avanzamento");
        }

        if (importo_sal != null && importo_compl_appalto != null) {
          if (importo_sal.doubleValue() >= importo_compl_appalto.doubleValue()) {
            nomeCampo = "IMPORTO_SAL";
            String messaggio = this.getMessaggioControntoImporti(importo_sal,
                "<", importo_compl_appalto, "W3APPA", "IMPORTO_COMPL_APPALTO",
                new Long(1));
            this.addAvviso(listaControlli, nomeTabella, nomeCampo, "E",
                "Stato di avanzamento", messaggio);
          }
        }

        // Data certificato
        Date data_certificato = (Date) SqlManager.getValueFromVectorParam(
            datiW3AVAN, 5).getValue();
        if (data_certificato != null && data_verb_aggiudicazione != null) {
          if (data_certificato.getTime() <= data_verb_aggiudicazione.getTime()) {
            nomeCampo = "DATA_CERTIFICATO";
            String messaggio = this.getMessaggioConfrontoDate(data_certificato,
                ">", data_verb_aggiudicazione, "W3APPA",
                "DATA_VERB_AGGIUDICAZIONE", new Long(1));
            this.addAvviso(listaControlli, nomeTabella, nomeCampo, "E",
                "Stato di avanzamento", messaggio);
          }
        }

        // Importo certificato
        Double importo_certificato = (Double) SqlManager.getValueFromVectorParam(
            datiW3AVAN, 6).getValue();
        if (importo_certificato != null && importo_compl_appalto != null) {
          if (importo_certificato.doubleValue() >= importo_compl_appalto.doubleValue()) {
            nomeCampo = "IMPORTO_CERTIFICATO";
            String messaggio = this.getMessaggioControntoImporti(
                importo_certificato, "<", importo_compl_appalto, "W3APPA",
                "IMPORTO_COMPL_APPALTO", new Long(1));
            this.addAvviso(listaControlli, nomeTabella, nomeCampo, "E",
                "Stato di avanzamento", messaggio);
          }
        }

        // flag_ritardo
        if (SqlManager.getValueFromVectorParam(datiW3AVAN, 7).getValue() == null) {
          nomeCampo = "FLAG_RITARDO";
          this.addCampoObbligatorio(listaControlli, nomeTabella, nomeCampo,
              "Stato di avanzamento");
        }

        // num_giorni_scost
        Long num_giorni_scost = (Long) SqlManager.getValueFromVectorParam(
            datiW3AVAN, 8).getValue();
        if (num_giorni_scost == null) {
          nomeCampo = "NUM_GIORNI_SCOST";
          this.addCampoObbligatorio(listaControlli, nomeTabella, nomeCampo,
              "Stato di avanzamento");
        }
        if (num_giorni_scost != null && num_giorni_scost.longValue() > 99) {
          nomeCampo = "NUM_GIORNI_SCOST";
          String messaggio = "Il numero indicato ("
              + num_giorni_scost.toString()
              + ") è superiore a 99: verificare il numero di giorni";
          this.addAvviso(listaControlli, nomeTabella, nomeCampo, "W",
              "Stato di avanzamento", messaggio);
        }

        // num_giorni_proroga
        Long num_giorni_proroga = (Long) SqlManager.getValueFromVectorParam(
            datiW3AVAN, 9).getValue();
        if (num_giorni_proroga == null) {
          nomeCampo = "NUM_GIORNI_PROROGA";
          this.addCampoObbligatorio(listaControlli, nomeTabella, nomeCampo,
              "Stato di avanzamento");
        }
        if (num_giorni_proroga != null && num_giorni_proroga.longValue() > 99) {
          nomeCampo = "NUM_GIORNI_PROROGA";
          String messaggio = "Il numero indicato ("
              + num_giorni_proroga.toString()
              + ") è superiore a 99: verificare il numero di giorni";
          this.addAvviso(listaControlli, nomeTabella, nomeCampo, "W",
              "Stato di avanzamento", messaggio);
        }

      }
    } catch (SQLException e) {
      throw new GestoreException(
          "Errore nella lettura delle informazioni relative alla fase di avanzamento",
          "validazioneW3AVAN", e);
    }

    if (logger.isDebugEnabled())
      logger.debug("validazioneW3AVAN: fine metodo");

  }

  /**
   * COntrollo dati per la fase di conclusione (W3CONC)
   * 
   * @param sqlManager
   * @param schedacompleta_id
   * @param listaControlli
   * @throws GestoreException
   */
  private void validazioneW3CONC(SqlManager sqlManager, Long schedacompleta_id,
      List listaControlli) throws GestoreException {
    if (logger.isDebugEnabled())
      logger.debug("validazioneW3CONC: inizio metodo");

    String nomeTabella = "W3CONC";
    String nomeCampo = "";
    String pagina = "";

    try {
      String selectW3CONC = "select id_motivo_interr, id_motivo_risol, data_risoluzione, "
          + " flag_oneri, oneri_risoluzione, flag_polizza, data_ultimazione, "
          + " num_infortuni, num_inf_perm, num_inf_mort, "
          + " data_verbale_consegna, termine_contratto_ult, "
          + " num_giorni_proroga from w3conc where schedacompleta_id = ?";

      List datiW3CONC = sqlManager.getVector(selectW3CONC,
          new Object[] { schedacompleta_id });

      if (datiW3CONC != null && datiW3CONC.size() > 0) {
        // Causa dell'interruzione anticipata
        Long id_motivo_interr = (Long) SqlManager.getValueFromVectorParam(
            datiW3CONC, 0).getValue();

        boolean b_risoluzione = false;
        boolean b_recesso = false;

        if (id_motivo_interr != null) {
          switch (id_motivo_interr.intValue()) {
          case 2:
            b_risoluzione = true;
            break;
          case 4:
          case 5:
            b_recesso = true;
            break;
          }
        }

        pagina = "Interruzione anticipata del procedimento";

        // Motivo della risoluzione
        Long id_motivo_risol = (Long) SqlManager.getValueFromVectorParam(
            datiW3CONC, 1).getValue();
        if (id_motivo_risol == null && b_risoluzione == true) {
          nomeCampo = "ID_MOTIVO_RISOL";
          this.addCampoObbligatorio(listaControlli, nomeTabella, nomeCampo,
              pagina);
        }

        // Data di risoluzione/recesso
        Date data_risoluzione = (Date) SqlManager.getValueFromVectorParam(
            datiW3CONC, 2).getValue();
        if (data_risoluzione == null && id_motivo_interr != null) {
          nomeCampo = "DATA_RISOLUZIONE";
          this.addCampoObbligatorio(listaControlli, nomeTabella, nomeCampo,
              pagina);
        }

        // Data del verbale di aggiudicazione
        Date data_verb_aggiudicazione = (Date) sqlManager.getObject(
            "select data_verb_aggiudicazione from w3appa where schedacompleta_id = ?",
            new Object[] { schedacompleta_id });

        if (data_risoluzione != null && data_verb_aggiudicazione != null) {
          if (data_risoluzione.getTime() <= data_verb_aggiudicazione.getTime()) {
            nomeCampo = "DATA_RISOLUZIONE";
            String messaggio = this.getMessaggioConfrontoDate(data_risoluzione,
                ">", data_verb_aggiudicazione, "W3APPA",
                "DATA_VERB_AGGIUDICAZIONE", new Long(1));
            this.addAvviso(listaControlli, nomeTabella, nomeCampo, "E", pagina,
                messaggio);
          }
        }

        // Oneri economici derivanti dalla risoluzione / recesso
        String flag_oneri = (String) SqlManager.getValueFromVectorParam(
            datiW3CONC, 3).getValue();
        if (flag_oneri == null && (b_risoluzione == true || b_recesso == true)) {
          nomeCampo = "FLAG_ONERI";
          this.addCampoObbligatorio(listaControlli, nomeTabella, nomeCampo,
              pagina);
        }

        Double oneri_risoluzione = (Double) SqlManager.getValueFromVectorParam(
            datiW3CONC, 4).getValue();
        if (flag_oneri != null && oneri_risoluzione == null) {
          nomeCampo = "ONERI_RISOLUZIONE";
          this.addCampoObbligatorio(listaControlli, nomeTabella, nomeCampo,
              pagina);
        }

        Double importo_compl_intervento = (Double) sqlManager.getObject(
            "select importo_compl_intervento from w3appa where schedacompleta_id = ?",
            new Object[] { schedacompleta_id });
        if (oneri_risoluzione != null && importo_compl_intervento != null) {
          if (oneri_risoluzione.doubleValue() >= importo_compl_intervento.doubleValue()) {
            nomeCampo = "ONERI_RISOLUZIONE";
            String messaggio = this.getMessaggioControntoImporti(
                oneri_risoluzione, "<", importo_compl_intervento, "W3APPA",
                "IMPORTO_COMPL_INTERVENTO", new Long(1));
            this.addAvviso(listaControlli, nomeTabella, nomeCampo, "E", pagina,
                messaggio);
          }
        }

        if (flag_oneri != null && oneri_risoluzione != null) {
          if (!"0".equals(flag_oneri) && oneri_risoluzione.doubleValue() == 0) {
            nomeCampo = "ONERI_RISOLUZIONE";
            String messaggio = "Il valore \"0\" è ammesso solo se al campo \""
                + this.getDescrizioneCampo("W3CONC", "FLAG_ONERI")
                + "\""
                + " è stato selezionato il valore \"Senza Oneri\"";
            this.addAvviso(listaControlli, nomeTabella, nomeCampo, "E", pagina,
                messaggio);
          }
        }

        // Flag polizza
        if (SqlManager.getValueFromVectorParam(datiW3CONC, 5).getValue() == null) {
          nomeCampo = "FLAG_POLIZZA";
          this.addCampoObbligatorio(listaControlli, nomeTabella, nomeCampo,
              pagina);
        }

        // Data verbale consegna definitiva
        if (SqlManager.getValueFromVectorParam(datiW3CONC, 10).getValue() == null) {
          nomeCampo = "DATA_VERBALE_CONSEGNA";
          this.addCampoObbligatorio(listaControlli, nomeTabella, nomeCampo,
              pagina);
        }

        // Termine contrattuale ultimazione
        if (SqlManager.getValueFromVectorParam(datiW3CONC, 11).getValue() == null) {
          nomeCampo = "TERMINE_CONTRATTO_ULT";
          this.addCampoObbligatorio(listaControlli, nomeTabella, nomeCampo,
              pagina);
        }

        pagina = "Ultimazione lavori";

        // Data ultimazione
        Date data_ultimazione = (Date) SqlManager.getValueFromVectorParam(
            datiW3CONC, 6).getValue();
        if (data_ultimazione == null) {
          nomeCampo = "DATA_ULTIMAZIONE";
          this.addCampoObbligatorio(listaControlli, nomeTabella, nomeCampo,
              pagina);
        }

        if (data_ultimazione != null && data_verb_aggiudicazione != null) {
          if (data_ultimazione.getTime() <= data_verb_aggiudicazione.getTime()) {
            nomeCampo = "DATA_ULTIMAZIONE";
            String messaggio = this.getMessaggioConfrontoDate(data_ultimazione,
                ">", data_verb_aggiudicazione, "W3APPA",
                "DATA_VERB_AGGIUDICAZIONE", new Long(1));
            this.addAvviso(listaControlli, nomeTabella, nomeCampo, "E", pagina,
                messaggio);
          }
        }

        Long num_infortuni = (Long) SqlManager.getValueFromVectorParam(
            datiW3CONC, 7).getValue();
        if (num_infortuni == null) {
          this.addCampoObbligatorio(listaControlli, nomeTabella,
              "NUM_INFORTUNI", pagina);
        }

        if (num_infortuni != null && num_infortuni.longValue() > 9) {
          nomeCampo = "NUM_INFORTUNI";
          String messaggio = "Il numero indicato ("
              + num_infortuni.toString()
              + ") è superiore a 9: verificare il numero di infortuni";
          this.addAvviso(listaControlli, nomeTabella, nomeCampo, "W", pagina,
              messaggio);
        }

        // Controllo numero infortuni con postumi permanenti ed infortuni
        // mortali
        Long num_inf_perm = (Long) SqlManager.getValueFromVectorParam(
            datiW3CONC, 8).getValue();
        Long num_inf_mort = (Long) SqlManager.getValueFromVectorParam(
            datiW3CONC, 9).getValue();
        if (num_inf_perm == null)
          this.addCampoObbligatorio(listaControlli, nomeTabella,
              "NUM_INF_PERM", pagina);
        if (num_inf_mort == null)
          this.addCampoObbligatorio(listaControlli, nomeTabella,
              "NUM_INF_MORT", pagina);

        if (num_infortuni != null) {
          if (num_inf_perm == null) num_inf_perm = new Long(0);
          if (num_inf_mort == null) num_inf_mort = new Long(0);
          Long totale = new Long(num_inf_perm.longValue()
              + num_inf_mort.longValue());
          if (totale.longValue() > num_infortuni.longValue()) {
            String descrizione = this.getDescrizioneCampo("W3CONC",
                "NUM_INF_PERM");
            descrizione += ", "
                + this.getDescrizioneCampo("W3CONC", "NUM_INF_MORT");
            String messaggio = "La somma dei due valori ("
                + totale.toString()
                + ") non pu&ograve; essere superiore a quanto indicato al campo \""
                + this.getDescrizioneCampo("W3CONC", "NUM_INFORTUNI")
                + "\" ("
                + num_infortuni.toString()
                + ")";
            listaControlli.add(((Object) (new Object[] { "E", pagina,
                descrizione, messaggio })));
          }
        }

        // Numero giorni proroga
        Long num_giorni_proroga = (Long) SqlManager.getValueFromVectorParam(
            datiW3CONC, 12).getValue();
        if (num_giorni_proroga != null && num_giorni_proroga.longValue() < 0) {
          nomeCampo = "NUM_GIORNI_PROROGA";
          String messaggio = "Il numero indicato ("
              + num_giorni_proroga.toString()
              + ") deve essere maggiore o uguale a 0";
          this.addAvviso(listaControlli, nomeTabella, nomeCampo, "E", pagina,
              messaggio);
        }

      }

    } catch (SQLException e) {
      throw new GestoreException(
          "Errore nella lettura delle informazioni relative alla fase di conclusione",
          "validazioneW3CONC", e);
    }

    if (logger.isDebugEnabled())
      logger.debug("validazioneW3CONC: fine metodo");
  }

  /**
   * Controllo dati per il collaudo (W3COLL)
   * 
   * @param sqlManager
   * @param schedacompleta_id
   * @param listaControlli
   * @throws GestoreException
   */
  private void validazioneW3COLL(SqlManager sqlManager, Long schedacompleta_id,
      List listaControlli) throws GestoreException {
    if (logger.isDebugEnabled())
      logger.debug("validazioneW3COLL: inizio metodo");

    String nomeTabella = "W3COLL";
    String nomeCampo = "";
    String pagina = "";

    try {

      String selectW3COLL = "select data_collaudo_stat, data_regolare_esec, data_cert_collaudo, modo_collaudo, data_nomina_coll,"
          + "data_inizio_oper, data_delibera, esito_collaudo, imp_finale_lavori, "
          + "imp_finale_servizi, imp_finale_fornit, imp_subtotale,"
          + "imp_finale_secur, imp_progettazione, imp_disposizione from w3coll where schedacompleta_id = ?";
      List datiW3COLL = sqlManager.getVector(selectW3COLL,
          new Object[] { schedacompleta_id });

      if (datiW3COLL != null && datiW3COLL.size() > 0) {

        // Tipo contratto (Dati generali)
        String tipo_contratto = (String) sqlManager.getObject(
            "select tipo_contratto from w3daco where scheda_id = ?",
            new Object[] { schedacompleta_id });
        // Importo complessivo intervento (Fase di aggiudicazione)
        Double importo_compl_intervento = (Double) sqlManager.getObject(
            "select importo_compl_intervento from w3appa where schedacompleta_id = ?",
            new Object[] { schedacompleta_id });
        // Data ultimazione (Fase di conclusione del contratto)
        Date data_ultimazione = (Date) sqlManager.getObject(
            "select data_ultimazione from w3conc where schedacompleta_id = ?",
            new Object[] { schedacompleta_id });
        // Data effettivo inizio lavori / servizi / forniture
        Date date_verb_inizio = (Date) sqlManager.getObject(
            "select data_verb_inizio from w3iniz where schedacompleta_id = ?",
            new Object[] { schedacompleta_id });

        pagina = "Collaudo / verifica di conformit&agrave;...";

        // Data collaudo statico
        Date data_collaudo_stat = (Date) SqlManager.getValueFromVectorParam(
            datiW3COLL, 0).getValue();
        if (data_collaudo_stat != null && date_verb_inizio != null) {
          if (data_collaudo_stat.getTime() <= date_verb_inizio.getTime()) {
            nomeCampo = "DATA_COLLAUDO_STAT";
            String messaggio = this.getMessaggioConfrontoDate(
                data_collaudo_stat, ">", date_verb_inizio, "W3CONC",
                "DATA_VERB_INIZIO", new Long(2));
            this.addAvviso(listaControlli, nomeTabella, nomeCampo, "E", pagina,
                messaggio);
          }
        }

        // Modalità di collaudo statico
        Long modo_collaudo = (Long) SqlManager.getValueFromVectorParam(
            datiW3COLL, 3).getValue();

        // Data certificato di regolare esecuzione
        Date data_regolare_esec = (Date) SqlManager.getValueFromVectorParam(
            datiW3COLL, 1).getValue();

        if (data_regolare_esec == null && modo_collaudo == null) {
          String descrizione = this.getDescrizioneCampo(nomeTabella,
              "DATA_REGOLARE_ESEC");
          descrizione += ", "
              + this.getDescrizioneCampo(nomeTabella, "MODO_COLLAUDO");
          String messaggio = "Specificare la data del certificato di regolare esecuzione o le modalit&agrave; del collaudo tecnico / amministrativo";
          listaControlli.add(((Object) (new Object[] { "E", pagina,
              descrizione, messaggio })));
        } else if (data_regolare_esec != null && modo_collaudo != null) {
          String descrizione = this.getDescrizioneCampo(nomeTabella,
              "DATA_REGOLARE_ESEC");
          descrizione += ", "
              + this.getDescrizioneCampo(nomeTabella, "MODO_COLLAUDO");
          String messaggio = "Certificato di regolare esecuzione non previsto in caso di collaudo tecnico / amministrativo";
          listaControlli.add(((Object) (new Object[] { "E", pagina,
              descrizione, messaggio })));
        }

        if (data_regolare_esec != null && data_ultimazione != null) {
          if (data_regolare_esec.getTime() <= data_ultimazione.getTime()) {
            nomeCampo = "DATA_REGOLARE_ESEC";
            String messaggio = this.getMessaggioConfrontoDate(
                data_regolare_esec, ">", data_ultimazione, "W3CONC",
                "DATA_ULTIMAZIONE", new Long(4));
            this.addAvviso(listaControlli, nomeTabella, nomeCampo, "E", pagina,
                messaggio);
          }
        }

        Date data_cert_collaudo = (Date) SqlManager.getValueFromVectorParam(
            datiW3COLL, 2).getValue();

        if (data_regolare_esec != null) {
          if (importo_compl_intervento != null
              && importo_compl_intervento.doubleValue() > 1000000) {
            if ("L".equals(tipo_contratto)) {
              nomeCampo = "DATA_REGOLARE_ESEC";
              String messaggio = "L\' \""
                  + this.getDescrizioneCampo("W3APPA",
                      "IMPORTO_COMPL_INTERVENTO")
                  + "\" indicato nella \""
                  + this.getDescrizioneFase(new Long(1))
                  + "\" è "
                  + " superiore a 1.000.000 euro ed il \""
                  + this.getDescrizioneCampo("W3DACO", "TIPO_CONTRATTO")
                  + "\" è \"Lavori\": "
                  + "redigere certificato di collaudo in luogo del certificato di regolare esecuzione";
              this.addAvviso(listaControlli, nomeTabella, nomeCampo, "W",
                  pagina, messaggio);

            }
          }
        }

        if (modo_collaudo != null) {
          if (importo_compl_intervento != null
              && importo_compl_intervento.doubleValue() < 500000) {
            if ("L".equals(tipo_contratto)) {
              nomeCampo = "MODO_COLLAUDO";
              String messaggio = "L\' \""
                  + this.getDescrizioneCampo("W3APPA",
                      "IMPORTO_COMPL_INTERVENTO")
                  + "\" indicato nella \""
                  + this.getDescrizioneFase(new Long(1))
                  + "\" è "
                  + " inferiore o uguale a 500.000 euro ed il \""
                  + this.getDescrizioneCampo("W3DACO", "TIPO_CONTRATTO")
                  + "\" è \"Lavori\": "
                  + "redigere certificato di regolare esecuzione in luogo del certificato di collaudo";
              this.addAvviso(listaControlli, nomeTabella, nomeCampo, "W",
                  pagina, messaggio);

            }
          }
        }

        // Data nomina collaudatore / commissione
        Date data_nomina_coll = (Date) SqlManager.getValueFromVectorParam(
            datiW3COLL, 4).getValue();
        if (data_nomina_coll == null && modo_collaudo != null) {
          nomeCampo = "DATA_NOMINA_COLL";
          this.addCampoObbligatorio(listaControlli, nomeTabella, nomeCampo,
              pagina);
        }

        // Data inizio operazioni di collaudo
        Date data_inizio_oper = (Date) SqlManager.getValueFromVectorParam(
            datiW3COLL, 5).getValue();
        if (data_inizio_oper == null && modo_collaudo != null) {
          nomeCampo = "DATA_INIZIO_OPER";
          this.addCampoObbligatorio(listaControlli, nomeTabella, nomeCampo,
              pagina);
        }

        if (data_inizio_oper != null && data_nomina_coll != null) {
          if (data_inizio_oper.getTime() < data_nomina_coll.getTime()) {
            nomeCampo = "DATA_INIZIO_OPER";
            String messaggio = this.getMessaggioConfrontoDate(data_inizio_oper,
                ">=", data_nomina_coll, "W3COLL", "DATA_NOMINA_COLL", null);
            this.addAvviso(listaControlli, nomeTabella, nomeCampo, "E", pagina,
                messaggio);
          }
        }

        // Data redazione certificato di collaudo
        if (data_cert_collaudo == null && modo_collaudo != null) {
          nomeCampo = "DATA_CERT_COLLAUDO";
          this.addCampoObbligatorio(listaControlli, nomeTabella, nomeCampo,
              pagina);
        }

        if (data_cert_collaudo != null && data_inizio_oper != null) {
          if (data_cert_collaudo.getTime() < data_inizio_oper.getTime()) {
            nomeCampo = "DATA_CERT_COLLAUDO";
            String messaggio = this.getMessaggioConfrontoDate(
                data_cert_collaudo, ">=", data_inizio_oper, "W3COLL",
                "DATA_INIZIO_OPER", null);
            this.addAvviso(listaControlli, nomeTabella, nomeCampo, "E", pagina,
                messaggio);
          }
        }

        if (data_cert_collaudo != null) {
          if (importo_compl_intervento != null
              && importo_compl_intervento.doubleValue() <= 500000) {
            if ("L".equals(tipo_contratto)) {
              nomeCampo = "DATA_CERT_COLLAUDO";
              String messaggio = "L\' \""
                  + this.getDescrizioneCampo("W3APPA",
                      "IMPORTO_COMPL_INTERVENTO")
                  + "\" indicato nella \""
                  + this.getDescrizioneFase(new Long(1))
                  + "\" è "
                  + " inferiore o pari a 500.000 euro ed il \""
                  + this.getDescrizioneCampo("W3DACO", "TIPO_CONTRATTO")
                  + "\" è \"Lavori\": "
                  + "redigere il certificato di regolare esecuzione in luogo del certificato di collaudo";
              this.addAvviso(listaControlli, nomeTabella, nomeCampo, "E",
                  pagina, messaggio);

            }
          }
        }

        // Data delibera
        Date data_delibera = (Date) SqlManager.getValueFromVectorParam(
            datiW3COLL, 6).getValue();
        if (data_delibera != null && data_cert_collaudo != null) {
          if (data_delibera.getTime() < data_cert_collaudo.getTime()) {
            nomeCampo = "DATA_DELIBERA";
            String messaggio = this.getMessaggioConfrontoDate(data_delibera,
                ">=", data_cert_collaudo, "W3COLL", "DATA_CERT_COLLAUDO", null);
            this.addAvviso(listaControlli, nomeTabella, nomeCampo, "E", pagina,
                messaggio);
          }
        }

        // Esito collaudo
        if (SqlManager.getValueFromVectorParam(datiW3COLL, 7).getValue() == null) {
          nomeCampo = "ESITO_COLLAUDO";
          this.addCampoObbligatorio(listaControlli, nomeTabella, nomeCampo,
              pagina);
        }

        // Importo finale lavori, importo finale servizi, importo finale
        // forniture
        Double imp_finale_lavori = (Double) SqlManager.getValueFromVectorParam(
            datiW3COLL, 8).getValue();
        Double imp_finale_servizi = (Double) SqlManager.getValueFromVectorParam(
            datiW3COLL, 9).getValue();
        Double imp_finale_fornit = (Double) SqlManager.getValueFromVectorParam(
            datiW3COLL, 10).getValue();

        if ("L".equals(tipo_contratto)) {
          if (imp_finale_lavori == null
              || (imp_finale_lavori != null && new Double(0).equals(imp_finale_lavori))) {
            String messaggio = "Il valore indicato deve essere > 0";
            this.addAvviso(listaControlli, nomeTabella, "IMP_FINALE_LAVORI",
                "E", pagina, messaggio);
          }
        }

        if ("S".equals(tipo_contratto)) {
          if (imp_finale_servizi == null
              || (imp_finale_servizi != null && new Double(0).equals(imp_finale_servizi))) {
            String messaggio = "Il valore indicato deve essere > 0";
            this.addAvviso(listaControlli, nomeTabella, "IMP_FINALE_SERVIZI",
                "E", pagina, messaggio);
          }
        }

        if ("F".equals(tipo_contratto)) {
          if (imp_finale_fornit == null
              || (imp_finale_fornit != null && new Double(0).equals(imp_finale_fornit))) {
            String messaggio = "Il valore indicato deve essere > 0";
            this.addAvviso(listaControlli, nomeTabella, "IMP_FINALE_FORNIT",
                "E", pagina, messaggio);
          }
        }

        // Importo finale sicurezza
        Double imp_subtotale = (Double) SqlManager.getValueFromVectorParam(
            datiW3COLL, 11).getValue();
        Double imp_finale_secur = (Double) SqlManager.getValueFromVectorParam(
            datiW3COLL, 12).getValue();
        Double imp_progettazione = (Double) SqlManager.getValueFromVectorParam(
            datiW3COLL, 13).getValue();

        if (imp_subtotale != null) {
          if (imp_finale_secur != null) {
            if (imp_finale_secur.doubleValue() > imp_subtotale.doubleValue()) {
              nomeCampo = "IMP_FINALE_SECUR";
              String messaggio = "L\'importo digitato ("
                  + this.importoToStringa(imp_finale_secur)
                  + ") è superiore al valore del campo \""
                  + this.getDescrizioneCampo("W3COLL", "IMP_SUBTOTALE")
                  + "\" ("
                  + this.importoToStringa(imp_subtotale)
                  + "): "
                  + "verificare l'importo digitato";
              this.addAvviso(listaControlli, nomeTabella, nomeCampo, "W",
                  pagina, messaggio);
            }
          }
          if (imp_progettazione != null) {
            if (imp_progettazione.doubleValue() > imp_subtotale.doubleValue()) {
              nomeCampo = "IMP_PROGETTAZIONE";
              String messaggio = "L\'importo digitato ("
                  + this.importoToStringa(imp_progettazione)
                  + ") è superiore al valore del campo \""
                  + this.getDescrizioneCampo("W3COLL", "IMP_SUBTOTALE")
                  + "\" ("
                  + this.importoToStringa(imp_subtotale)
                  + "): "
                  + "verificare l'importo digitato";
              this.addAvviso(listaControlli, nomeTabella, nomeCampo, "W",
                  pagina, messaggio);
            }
          }
        }

        // Importo disposizione
        Double imp_disposizione = (Double) SqlManager.getValueFromVectorParam(
            datiW3COLL, 14).getValue();

        if (imp_disposizione == null) {
          nomeCampo = "IMP_DISPOSIZIONE";
          this.addCampoObbligatorio(listaControlli, nomeTabella, nomeCampo,
              pagina);
        }

        if (imp_disposizione != null && imp_disposizione.doubleValue() < 0) {
          nomeCampo = "IMP_DISPOSIZIONE";
          String messaggio = "L\'importo digitato ("
              + this.importoToStringa(imp_disposizione)
              + ") deve essere maggiore o uguale a 0";
          this.addAvviso(listaControlli, nomeTabella, nomeCampo, "E", pagina,
              messaggio);
        }
      }
    } catch (SQLException e) {
      throw new GestoreException(
          "Errore nella lettura delle informazioni relative al collaudo: prima parte",
          "validazioneW3COLL", e);
    }

    try {
      String selectW3COLL = "select imp_compl_intervento, "
          + " amm_num_definite,amm_num_dadef,amm_importo_rich,amm_importo_def, "
          + " arb_num_definite,arb_num_dadef,arb_importo_rich,arb_importo_def, "
          + " giu_num_definite,giu_num_dadef,giu_importo_rich,giu_importo_def, "
          + " tra_num_definite,tra_num_dadef,tra_importo_rich,tra_importo_def"
          + " from w3coll where schedacompleta_id = ?";
      List datiW3COLL = sqlManager.getVector(selectW3COLL,
          new Object[] { schedacompleta_id });

      if (datiW3COLL != null && datiW3COLL.size() > 0) {

        Double imp_compl_intervento = (Double) SqlManager.getValueFromVectorParam(
            datiW3COLL, 0).getValue();

        // In via amministrativa
        Long amm_num_definite = (Long) SqlManager.getValueFromVectorParam(
            datiW3COLL, 1).getValue();
        Long amm_num_dadef = (Long) SqlManager.getValueFromVectorParam(
            datiW3COLL, 2).getValue();
        Double amm_importo_rich = (Double) SqlManager.getValueFromVectorParam(
            datiW3COLL, 3).getValue();
        Double amm_importo_def = (Double) SqlManager.getValueFromVectorParam(
            datiW3COLL, 4).getValue();

        if ((amm_num_definite != null && amm_num_definite.longValue() > 0)
            || (amm_num_dadef != null && amm_num_dadef.longValue() > 0)) {
          pagina = "Riserve in via amministrativa...";
          if (amm_importo_rich == null)
            this.addCampoObbligatorio(listaControlli, nomeTabella,
                "AMM_IMPORTO_RICH", pagina);
          if (amm_importo_rich != null && imp_compl_intervento != null) {
            if (amm_importo_rich.doubleValue() > imp_compl_intervento.doubleValue()) {
              String messaggio = this.getMessaggioControntoImporti(
                  amm_importo_rich, "<=", imp_compl_intervento, "W3COLL",
                  "IMP_COMPL_INTERVENTO", null);
              this.addAvviso(listaControlli, nomeTabella, "AMM_IMPORTO_RICH",
                  "E", pagina, messaggio);
            }
          }

          if (amm_importo_def == null)
            this.addCampoObbligatorio(listaControlli, nomeTabella,
                "AMM_IMPORTO_DEF", pagina);
          if (amm_importo_def != null && imp_compl_intervento != null) {
            if (amm_importo_def.doubleValue() > imp_compl_intervento.doubleValue()) {
              String messaggio = this.getMessaggioControntoImporti(
                  amm_importo_def, "<=", imp_compl_intervento, "W3COLL",
                  "IMP_COMPL_INTERVENTO", null);
              this.addAvviso(listaControlli, nomeTabella, "AMM_IMPORTO_DEF",
                  "E", pagina, messaggio);
            }
          }
        }

        // In via arbitrale
        Long arb_num_definite = (Long) SqlManager.getValueFromVectorParam(
            datiW3COLL, 5).getValue();
        Long arb_num_dadef = (Long) SqlManager.getValueFromVectorParam(
            datiW3COLL, 6).getValue();
        Double arb_importo_rich = (Double) SqlManager.getValueFromVectorParam(
            datiW3COLL, 7).getValue();
        Double arb_importo_def = (Double) SqlManager.getValueFromVectorParam(
            datiW3COLL, 8).getValue();

        if ((arb_num_definite != null && arb_num_definite.longValue() > 0)
            || (arb_num_dadef != null && arb_num_dadef.longValue() > 0)) {
          pagina = "Riserve in via arbitrale...";
          if (arb_importo_rich == null)
            this.addCampoObbligatorio(listaControlli, nomeTabella,
                "ARB_IMPORTO_RICH", pagina);
          if (arb_importo_rich != null && imp_compl_intervento != null) {
            if (arb_importo_rich.doubleValue() > imp_compl_intervento.doubleValue()) {
              String messaggio = this.getMessaggioControntoImporti(
                  arb_importo_rich, "<=", imp_compl_intervento, "W3COLL",
                  "IMP_COMPL_INTERVENTO", null);
              this.addAvviso(listaControlli, nomeTabella, "ARB_IMPORTO_RICH",
                  "E", pagina, messaggio);
            }
          }

          if (arb_importo_def == null)
            this.addCampoObbligatorio(listaControlli, nomeTabella,
                "ARB_IMPORTO_DEF", pagina);
          if (arb_importo_def != null && imp_compl_intervento != null) {
            if (arb_importo_def.doubleValue() > imp_compl_intervento.doubleValue()) {
              String messaggio = this.getMessaggioControntoImporti(
                  arb_importo_def, "<=", imp_compl_intervento, "W3COLL",
                  "IMP_COMPL_INTERVENTO", null);
              this.addAvviso(listaControlli, nomeTabella, "ARB_IMPORTO_DEF",
                  "E", pagina, messaggio);
            }
          }
        }

        // In via giudiziale
        Long giu_num_definite = (Long) SqlManager.getValueFromVectorParam(
            datiW3COLL, 9).getValue();
        Long giu_num_dadef = (Long) SqlManager.getValueFromVectorParam(
            datiW3COLL, 10).getValue();
        Double giu_importo_rich = (Double) SqlManager.getValueFromVectorParam(
            datiW3COLL, 11).getValue();
        Double giu_importo_def = (Double) SqlManager.getValueFromVectorParam(
            datiW3COLL, 12).getValue();

        if ((giu_num_definite != null && giu_num_definite.longValue() > 0)
            || (giu_num_dadef != null && giu_num_dadef.longValue() > 0)) {
          pagina = "Riserve in via giudiziale...";
          if (giu_importo_rich == null)
            this.addCampoObbligatorio(listaControlli, nomeTabella,
                "GIU_IMPORTO_RICH", pagina);
          if (giu_importo_rich != null && imp_compl_intervento != null) {
            if (giu_importo_rich.doubleValue() > imp_compl_intervento.doubleValue()) {
              String messaggio = this.getMessaggioControntoImporti(
                  giu_importo_rich, "<=", imp_compl_intervento, "W3COLL",
                  "IMP_COMPL_INTERVENTO", null);
              this.addAvviso(listaControlli, nomeTabella, "GIU_IMPORTO_RICH",
                  "E", pagina, messaggio);
            }
          }

          if (giu_importo_def == null)
            this.addCampoObbligatorio(listaControlli, nomeTabella,
                "GIU_IMPORTO_DEF", pagina);
          if (giu_importo_def != null && imp_compl_intervento != null) {
            if (giu_importo_def.doubleValue() > imp_compl_intervento.doubleValue()) {
              String messaggio = this.getMessaggioControntoImporti(
                  giu_importo_def, "<=", imp_compl_intervento, "W3COLL",
                  "IMP_COMPL_INTERVENTO", null);
              this.addAvviso(listaControlli, nomeTabella, "GIU_IMPORTO_DEF",
                  "E", pagina, messaggio);
            }
          }
        }

        // In via transattiva
        Long tra_num_definite = (Long) SqlManager.getValueFromVectorParam(
            datiW3COLL, 13).getValue();
        Long tra_num_dadef = (Long) SqlManager.getValueFromVectorParam(
            datiW3COLL, 14).getValue();
        Double tra_importo_rich = (Double) SqlManager.getValueFromVectorParam(
            datiW3COLL, 15).getValue();
        Double tra_importo_def = (Double) SqlManager.getValueFromVectorParam(
            datiW3COLL, 16).getValue();

        if ((tra_num_definite != null && tra_num_definite.longValue() > 0)
            || (tra_num_dadef != null && tra_num_dadef.longValue() > 0)) {
          pagina = "Riserve in via transattiva...";
          if (tra_importo_rich == null)
            this.addCampoObbligatorio(listaControlli, nomeTabella,
                "TRA_IMPORTO_RICH", pagina);
          if (tra_importo_rich != null && imp_compl_intervento != null) {
            if (tra_importo_rich.doubleValue() > imp_compl_intervento.doubleValue()) {
              String messaggio = this.getMessaggioControntoImporti(
                  tra_importo_rich, "<=", imp_compl_intervento, "W3COLL",
                  "IMP_COMPL_INTERVENTO", null);
              this.addAvviso(listaControlli, nomeTabella, "TRA_IMPORTO_RICH",
                  "E", pagina, messaggio);
            }
          }

          if (tra_importo_def == null)
            this.addCampoObbligatorio(listaControlli, nomeTabella,
                "TRA_IMPORTO_DEF", pagina);
          if (tra_importo_def != null && imp_compl_intervento != null) {
            if (tra_importo_def.doubleValue() > imp_compl_intervento.doubleValue()) {
              String messaggio = this.getMessaggioControntoImporti(
                  tra_importo_def, "<=", imp_compl_intervento, "W3COLL",
                  "IMP_COMPL_INTERVENTO", null);
              this.addAvviso(listaControlli, nomeTabella, "TRA_IMPORTO_DEF",
                  "E", pagina, messaggio);
            }
          }
        }
      }

    } catch (SQLException e) {
      throw new GestoreException(
          "Errore nella lettura delle informazioni relative al collaudo: seconda parte",
          "validazioneW3COLL", e);
    }

    // Aggiungo, in coda, la validazione delle singole schede dei soggetti
    // incaricati
    validazioneW3INCA(sqlManager, schedacompleta_id, listaControlli, "CO");

    if (logger.isDebugEnabled())
      logger.debug("validazioneW3COLL: fine metodo");
  }

  /**
   * Validazione delle categorie scorporabili W3REQU
   * 
   * @param sqlManager
   * @param schedacompleta_id
   * @param listaControlli
   * @throws GestoreException
   */
  private void validazioneW3REQU(SqlManager sqlManager, Long schedacompleta_id,
      List listaControlli) throws GestoreException {

    try {
      String selectW3REQU = "select num, id_categoria, classe_importo, subappaltabile "
          + " from w3requ where schedacompleta_id = ? and prevalente is null order by num";
      List datiW3REQU = sqlManager.getListVector(selectW3REQU,
          new Object[] { schedacompleta_id });

      String nomeTabella = "W3REQU";

      if (datiW3REQU != null && datiW3REQU.size() > 0) {

        List listaControlliW3REQU = new Vector();
        String pagina = null;
        Long num = null;

        for (int i = 0; i < datiW3REQU.size(); i++) {
          num = new Long(i + 1);
          pagina = "Categoria scorporabile n° " + num.toString();

          if (SqlManager.getValueFromVectorParam(datiW3REQU.get(i), 1).getValue() == null) {
            this.addCampoObbligatorio(listaControlliW3REQU, nomeTabella,
                "ID_CATEGORIA", pagina);
          }
          if (SqlManager.getValueFromVectorParam(datiW3REQU.get(i), 2).getValue() == null) {
            this.addCampoObbligatorio(listaControlliW3REQU, nomeTabella,
                "CLASSE_IMPORTO", pagina);
          }
          if (SqlManager.getValueFromVectorParam(datiW3REQU.get(i), 3).getValue() == null) {
            this.addCampoObbligatorio(listaControlliW3REQU, nomeTabella,
                "SUBAPPALTABILE", pagina);
          }
        }

        if (!listaControlliW3REQU.isEmpty()) {
          this.setTitolo(listaControlli, "Categorie scorporabili");
          for (int i = 0; i < listaControlliW3REQU.size(); i++) {
            listaControlli.add(listaControlliW3REQU.get(i));
          }
        }

      }
    } catch (SQLException e) {
      throw new GestoreException(
          "Errore nella lettura delle informazioni relative alle categorie scorporabili",
          "validazioneW3REQU", e);
    }
  }

  /**
   * Validazione dei finanziamenti W3FINA
   * 
   * @param sqlManager
   * @param schedacompleta_id
   * @param listaControlli
   * @throws GestoreException
   */
  private void validazioneW3FINA(SqlManager sqlManager, Long schedacompleta_id,
      List listaControlli) throws GestoreException {

    try {
      String selectW3FINA = "select num, id_finanziamento, importo_finanziamento "
          + " from w3fina where schedacompleta_id = ?";
      List datiW3FINA = sqlManager.getListVector(selectW3FINA,
          new Object[] { schedacompleta_id });

      String nomeTabella = "W3FINA";

      if (datiW3FINA != null && datiW3FINA.size() > 0) {

        List listaControlliW3FINA = new Vector();
        Long num = null;
        String pagina = null;

        for (int i = 0; i < datiW3FINA.size(); i++) {
          num = (Long) SqlManager.getValueFromVectorParam(datiW3FINA.get(i), 0).getValue();
          pagina = "Finanziamento n° " + num.toString();

          if (SqlManager.getValueFromVectorParam(datiW3FINA.get(i), 1).getValue() == null) {
            this.addCampoObbligatorio(listaControlliW3FINA, nomeTabella,
                "ID_FINANZIAMENTO", pagina);
          }

          if (SqlManager.getValueFromVectorParam(datiW3FINA.get(i), 2).getValue() == null) {
            this.addCampoObbligatorio(listaControlliW3FINA, nomeTabella,
                "IMPORTO_FINANZIAMENTO", pagina);
          }
        }

        if (!listaControlliW3FINA.isEmpty()) {
          this.setTitolo(listaControlli, "Finanziamenti");
          for (int i = 0; i < listaControlliW3FINA.size(); i++) {
            listaControlli.add(listaControlliW3FINA.get(i));
          }
        }
      }
    } catch (SQLException e) {
      throw new GestoreException(
          "Errore nella lettura delle informazioni relative ai finanziamenti",
          "validazioneW3FINA", e);
    }
  }

  private void validazioneW3AGGI(SqlManager sqlManager, Long schedacompleta_id,
      List listaControlli, Long fase_esecuzione) throws GestoreException {

    try {
      String selectW3AGGI = "select num, id_tipoagg, ruolo, "
          + " flag_avvalimento, codice_inps, codice_inail, "
          + " codice_cassa, codimp, codimp_ausiliaria "
          + " from w3aggi where schedacompleta_id = ?";

      List datiW3AGGI = sqlManager.getListVector(selectW3AGGI,
          new Object[] { schedacompleta_id });

      String nomeTabella = "W3AGGI";

      if (datiW3AGGI != null && datiW3AGGI.size() > 0) {

        List listaControlliW3AGGI = new Vector();
        Long num = null;
        String pagina = null;
        String titolo = null;

        if (new Long(1).equals(fase_esecuzione))
          titolo = "Soggetti affidatari / aggiudicatari";
        if (new Long(2).equals(fase_esecuzione))
          titolo = "Posizione contributiva / assicurativa";

        for (int i = 0; i < datiW3AGGI.size(); i++) {
          num = (Long) SqlManager.getValueFromVectorParam(datiW3AGGI.get(i), 0).getValue();
          pagina = "Impresa n° " + num.toString();

          // Controllo dei campi modificabili nella fase di aggiudicazione
          if (new Long(1).equals(fase_esecuzione)) {
            // Denominazione
            if (SqlManager.getValueFromVectorParam(datiW3AGGI.get(i), 7).getValue() == null) {
              String messaggio = "Il campo &egrave; obbligatorio";
              listaControlliW3AGGI.add(((Object) (new Object[] { "E", pagina,
                  "Denominazione", messaggio })));
            }

            // flag_avvalimento
            String flag_avvalimento = (String) SqlManager.getValueFromVectorParam(
                datiW3AGGI.get(i), 3).getValue();
            if (flag_avvalimento == null) {
              this.addCampoObbligatorio(listaControlliW3AGGI, nomeTabella,
                  "FLAG_AVVALIMENTO", pagina);
            }

            // codimp_ausiliaria
            if (flag_avvalimento != null && !"0".equals(flag_avvalimento)) {
              String codimp_ausiliaria = (String) SqlManager.getValueFromVectorParam(
                  datiW3AGGI.get(i), 8).getValue();
              if (codimp_ausiliaria == null) {
                String messaggio = "Il campo &egrave; obbligatorio";
                listaControlliW3AGGI.add(((Object) (new Object[] { "E", pagina,
                    "Denominazione della ditta ausiliaria", messaggio })));
              } else {
                String cfimp_ausiliaria = (String) sqlManager.getObject(
                    "select cfimp from impr where codimp = ?",
                    new Object[] { codimp_ausiliaria });
                if (cfimp_ausiliaria == null) {
                  String messaggio = "Il campo &egrave; obbligatorio";
                  listaControlliW3AGGI.add(((Object) (new Object[] { "E", pagina,
                      "Codice fiscale della ditta ausiliaria", messaggio })));
                }

              }
            }

            // id_tipoagg
            Long id_tipoagg = (Long) SqlManager.getValueFromVectorParam(
                datiW3AGGI.get(i), 1).getValue();
            if (id_tipoagg == null) {
              this.addCampoObbligatorio(listaControlliW3AGGI, nomeTabella,
                  "ID_TIPOAGG", pagina);
            }

            // ruolo
            if (id_tipoagg != null && id_tipoagg.longValue() == 1) {
              if (SqlManager.getValueFromVectorParam(datiW3AGGI.get(i), 2).getValue() == null) {
                this.addCampoObbligatorio(listaControlliW3AGGI, nomeTabella,
                    "RUOLO", pagina);
              }
            }
          }

          // Controllo dei campi modificabili nella fase iniziale
          if (new Long(2).equals(fase_esecuzione)) {
            String messaggio = "Codice assente";
            if (SqlManager.getValueFromVectorParam(datiW3AGGI.get(i), 4).getValue() == null) {
              this.addAvviso(listaControlliW3AGGI, nomeTabella, "CODICE_INPS",
                  "W", pagina, messaggio);
            }
            if (SqlManager.getValueFromVectorParam(datiW3AGGI.get(i), 5).getValue() == null) {
              this.addAvviso(listaControlliW3AGGI, nomeTabella, "CODICE_INAIL",
                  "W", pagina, messaggio);
            }
            if (SqlManager.getValueFromVectorParam(datiW3AGGI.get(i), 6).getValue() == null) {
              this.addAvviso(listaControlliW3AGGI, nomeTabella, "CODICE_CASSA",
                  "W", pagina, messaggio);
            }

          }

        }

        if (!listaControlliW3AGGI.isEmpty()) {
          this.setTitolo(listaControlli, titolo);
          for (int i = 0; i < listaControlliW3AGGI.size(); i++) {
            listaControlli.add(listaControlliW3AGGI.get(i));
          }
        }
      }

    } catch (SQLException e) {
      throw new GestoreException(
          "Errore nella lettura delle informazioni relative alle imprese aggiudicatarie/affidatarie",
          "validazioneW3AGGI", e);
    }

  }

  /**
   * Utilizzata per la validazione dei dati della scheda dell'incaricato W3INCA
   * 
   * @param sqlManager
   * @param schedacompleta_id
   * @param listaControlli
   * @param pagina
   * @param sezione
   * @throws GestoreException
   */
  private void validazioneW3INCA(SqlManager sqlManager, Long schedacompleta_id,
      List listaControlli, String sezione) throws GestoreException {
    try {
      String selectW3INCA = "select num, id_ruolo, codtec, "
          + " cig_prog_esterna, data_aff_prog_esterna, data_cons_prog_esterna "
          + " from w3inca "
          + " where schedacompleta_id = ? and sezione = ?";
      List datiW3INCA = sqlManager.getListVector(selectW3INCA, new Object[] {
          schedacompleta_id, sezione });

      String nomeTabella = "W3INCA";

      if (datiW3INCA != null && datiW3INCA.size() > 0) {

        List listaControlliW3INCA = new Vector();
        Long num = null;
        String pagina = null;
        String titolo = null;

        for (int i = 0; i < datiW3INCA.size(); i++) {
          num = (Long) SqlManager.getValueFromVectorParam(datiW3INCA.get(i), 0).getValue();

          if (sezione == "PA") {
            pagina = "Progettista n° " + num.toString();
            titolo = "Prestazioni progettuali";
          } else {
            pagina = "Incaricato n° " + num.toString();
            titolo = "Soggetti ai quali sono stati conferiti incarichi";
          }

          // id_ruolo
          Long id_ruolo = (Long) SqlManager.getValueFromVectorParam(
              datiW3INCA.get(i), 1).getValue();
          if (id_ruolo == null) {
            this.addCampoObbligatorio(listaControlliW3INCA, nomeTabella,
                "ID_RUOLO", pagina);
          }

          // codtec
          if (SqlManager.getValueFromVectorParam(datiW3INCA.get(i), 2).getValue() == null) {
            String messaggio = "Il campo &egrave; obbligatorio";
            listaControlliW3INCA.add(((Object) (new Object[] { "E", pagina,
                "Denominazione", messaggio })));
          }

          // Controlli SEZIONE = 'PA'
          if ("PA".equals(sezione)) {
            if (id_ruolo != null && id_ruolo.longValue() == 2) {

              // cig_prog_esterna
              if (SqlManager.getValueFromVectorParam(datiW3INCA.get(i), 3).getValue() == null) {
                String messaggio = "Assenza del CIG dell\'affidamento di incarico esterno di progettazione";
                this.addAvviso(listaControlliW3INCA, nomeTabella,
                    "CIG_PROG_ESTERNA", "W", pagina, messaggio);
              }

              // data_aff_prog_esterna
              if (SqlManager.getValueFromVectorParam(datiW3INCA.get(i), 4).getValue() == null) {
                this.addCampoObbligatorio(listaControlliW3INCA, nomeTabella,
                    "DATA_AFF_PROG_ESTERNA", pagina);
              }

              // data_cons_prog_esterna
              if (SqlManager.getValueFromVectorParam(datiW3INCA.get(i), 5).getValue() == null) {
                this.addCampoObbligatorio(listaControlliW3INCA, nomeTabella,
                    "DATA_CONS_PROG_ESTERNA", pagina);
              }
            }
          }
        }

        if (!listaControlliW3INCA.isEmpty()) {
          this.setTitolo(listaControlli, titolo);
          for (int i = 0; i < listaControlliW3INCA.size(); i++) {
            listaControlli.add(listaControlliW3INCA.get(i));
          }
        }

      }

    } catch (SQLException e) {
      throw new GestoreException(
          "Errore nella lettura delle informazioni relative agli incaricati",
          "validazioneW3INCA", e);
    }
  }

  /**
   * Controllo dati per le sospensioni (W3SOSP)
   * 
   * @param sqlManager
   * @param schedacompleta_id
   * @param num
   * @param listaControlli
   * @throws GestoreException
   */
  private void validazioneW3SOSP(SqlManager sqlManager, Long schedacompleta_id,
      Long num, List listaControlli) throws GestoreException {
    if (logger.isDebugEnabled())
      logger.debug("validazioneW3SOSP: inizio metodo");

    String nomeTabella = "W3SOSP";
    String nomeCampo = "";

    try {
      // Data verbale inizio (Fase iniziale del contratto)
      Date data_verb_inizio = (Date) sqlManager.getObject(
          "select data_verb_inizio from w3iniz where schedacompleta_id = ?",
          new Object[] { schedacompleta_id });

      String selectW3SOSP = "select data_verb_sosp, data_verb_ripr, id_motivo_sosp, flag_supero_tempo, flag_riserve, flag_verbale from w3sosp where schedacompleta_id = ? and num = ?";
      List datiW3SOSP = sqlManager.getVector(selectW3SOSP, new Object[] {
          schedacompleta_id, num });

      if (datiW3SOSP != null && datiW3SOSP.size() > 0) {
        // Data verbale di sospensione
        Date data_verb_sosp = (Date) SqlManager.getValueFromVectorParam(
            datiW3SOSP, 0).getValue();
        if (data_verb_sosp == null) {
          nomeCampo = "DATA_VERB_SOSP";
          this.addCampoObbligatorio(listaControlli, nomeTabella, nomeCampo,
              "Sospensione dell'esecuzione");
        }

        if (data_verb_sosp != null && data_verb_inizio != null) {
          if (data_verb_sosp.getTime() <= data_verb_inizio.getTime()) {
            nomeCampo = "DATA_VERB_SOSP";
            String messaggio = this.getMessaggioConfrontoDate(data_verb_sosp,
                ">=", data_verb_inizio, "W3INIZ", "DATA_VERB_INIZIO", new Long(
                    2));
            this.addAvviso(listaControlli, nomeTabella, nomeCampo, "E",
                "Sospensione dell'esecuzione", messaggio);
          }
        }

        // Data del verbale di ripresa
        Date data_verb_ripr = (Date) SqlManager.getValueFromVectorParam(
            datiW3SOSP, 1).getValue();
        if (data_verb_ripr != null && data_verb_sosp != null) {
          if (data_verb_ripr.getTime() <= data_verb_sosp.getTime()) {
            nomeCampo = "DATA_VERB_RIPR";
            String messaggio = this.getMessaggioConfrontoDate(data_verb_ripr,
                ">", data_verb_sosp, "W3SOSP", "DATA_VERB_SOSP", null);
            this.addAvviso(listaControlli, nomeTabella, nomeCampo, "E",
                "Sospensione dell'esecuzione", messaggio);
          }
        }

        if (SqlManager.getValueFromVectorParam(datiW3SOSP, 2).getValue() == null) {
          nomeCampo = "ID_MOTIVO_SOSP";
          this.addCampoObbligatorio(listaControlli, nomeTabella, nomeCampo,
              "Sospensione dell'esecuzione");
        }

        if (SqlManager.getValueFromVectorParam(datiW3SOSP, 3).getValue() == null) {
          nomeCampo = "FLAG_SUPERO_TEMPO";
          this.addCampoObbligatorio(listaControlli, nomeTabella, nomeCampo,
              "Sospensione dell'esecuzione");
        }

        if (SqlManager.getValueFromVectorParam(datiW3SOSP, 4).getValue() == null) {
          nomeCampo = "FLAG_RISERVE";
          this.addCampoObbligatorio(listaControlli, nomeTabella, nomeCampo,
              "Sospensione dell'esecuzione");
        }

        if (SqlManager.getValueFromVectorParam(datiW3SOSP, 5).getValue() == null) {
          nomeCampo = "FLAG_VERBALE";
          this.addCampoObbligatorio(listaControlli, nomeTabella, nomeCampo,
              "Sospensione dell'esecuzione");
        }

      }

    } catch (SQLException e) {
      throw new GestoreException(
          "Errore nella lettura delle informazioni relative alla sospensione",
          "validazioneW3SOSP", e);
    }

    if (logger.isDebugEnabled())
      logger.debug("validazioneW3SOSP: fine metodo");
  }

  /**
   * Controllo dati per le varianti (W3VARI)
   * 
   * @param sqlManager
   * @param schedacompleta_id
   * @param num
   * @param listaControlli
   * @throws GestoreException
   */
  private void validazioneW3VARI(SqlManager sqlManager, Long schedacompleta_id,
      Long num, List listaControlli) throws GestoreException {
    if (logger.isDebugEnabled())
      logger.debug("validazioneW3VARI: inizio metodo");

    String nomeTabella = "W3VARI";
    String nomeCampo = "";

    try {

      // Data verbale inizio (Fase iniziale del contratto)
      Date data_verb_inizio = (Date) sqlManager.getObject(
          "select data_verb_inizio from w3iniz where schedacompleta_id = ?",
          new Object[] { schedacompleta_id });

      // Data stipula contratto della Fase iniziale di esecuzione
      Date data_stipula = (Date) sqlManager.getObject(
          "select data_stipula from w3iniz where schedacompleta_id = ?",
          new Object[] { schedacompleta_id });

      String selectW3VARI = "select data_verb_appr, altre_motivazioni, imp_ridet_lavori, imp_ridet_servizi, imp_ridet_fornit, "
          + " imp_sicurezza, imp_progettazione, imp_disposizione, data_atto_aggiuntivo from w3vari"
          + " where schedacompleta_id = ? and num = ?";
      List datiW3VARI = sqlManager.getVector(selectW3VARI, new Object[] {
          schedacompleta_id, num });

      if (datiW3VARI != null && datiW3VARI.size() > 0) {

        // Data approvazione variante
        Date data_verb_appr = (Date) SqlManager.getValueFromVectorParam(
            datiW3VARI, 0).getValue();
        if (data_verb_appr == null) {
          nomeCampo = "DATA_VERB_APPR";
          this.addCampoObbligatorio(listaControlli, nomeTabella, nomeCampo,
              "Variante");
        }

        if (data_verb_appr != null && data_verb_inizio != null) {
          if (data_verb_appr.getTime() < data_verb_inizio.getTime()) {
            nomeCampo = "DATA_VERB_APPR";
            String messaggio = this.getMessaggioConfrontoDate(data_verb_appr,
                ">=", data_verb_inizio, "W3INIZ", "DATA_VERB_INIZIO", new Long(
                    2));
            this.addAvviso(listaControlli, nomeTabella, nomeCampo, "E",
                "Variante", messaggio);
          }
        }

        // Motivazioni
        if (SqlManager.getValueFromVectorParam(datiW3VARI, 1).getValue() == null) {
          // Se il altre motivazioni è vuoto controllo che ci sia almeno una
          // riga in W3MOTI
          Long conteggio = (Long) sqlManager.getObject(
              "select count(*) from w3moti where schedacompleta_id = ? and num = ?",
              new Object[] { schedacompleta_id, num });
          if (conteggio == null
              || (conteggio != null && new Long(0).equals(conteggio))) {
            nomeCampo = "ALTRE_MOTIVAZIONI";
            String messaggio = "Indicare almeno una motivazione che ha determinato l'insorgere di una variante";
            this.addAvviso(listaControlli, nomeTabella, nomeCampo, "E",
                "Motivazioni della variante", messaggio);
          }
        }

        // Importi
        Double imp_ridet_lavori = (Double) SqlManager.getValueFromVectorParam(
            datiW3VARI, 2).getValue();
        Double imp_ridet_servizi = (Double) SqlManager.getValueFromVectorParam(
            datiW3VARI, 3).getValue();
        Double imp_ridet_fornit = (Double) SqlManager.getValueFromVectorParam(
            datiW3VARI, 4).getValue();

        if (imp_ridet_lavori == null
            || (imp_ridet_lavori != null && new Double(0).equals(imp_ridet_lavori))) {
          if (imp_ridet_servizi == null
              || (imp_ridet_servizi != null && new Double(0).equals(imp_ridet_servizi))) {
            if (imp_ridet_fornit == null
                || (imp_ridet_fornit != null && new Double(0).equals(imp_ridet_fornit))) {

              String descrizione = this.getDescrizioneCampo(nomeTabella,
                  "IMP_RIDET_LAVORI");
              descrizione = descrizione
                  + ", "
                  + this.getDescrizioneCampo(nomeTabella, "IMP_RIDET_SERVIZI");
              descrizione = descrizione
                  + ", "
                  + this.getDescrizioneCampo(nomeTabella, "IMP_RIDET_FORNIT");
              String messaggio = "L\'importo di almeno uno dei tre campi indicati deve essere > 0";

              listaControlli.add(((Object) (new Object[] { "E",
                  "Quadro economico variante", descrizione, messaggio })));
            }
          }
        }

        Double imp_sicurezza = (Double) SqlManager.getValueFromVectorParam(
            datiW3VARI, 5).getValue();
        if (imp_sicurezza == null
            || (imp_sicurezza != null && new Double(0).equals(imp_sicurezza))) {
          nomeCampo = "IMP_SICUREZZA";
          String messaggio = "Il valore indicato dovrebbe essere > 0";
          this.addAvviso(listaControlli, nomeTabella, nomeCampo, "W",
              "Quadro economico variante", messaggio);
        }

        Double imp_progettazione = (Double) SqlManager.getValueFromVectorParam(
            datiW3VARI, 6).getValue();
        if (imp_progettazione == null
            || (imp_progettazione != null && new Double(0).equals(imp_progettazione))) {
          nomeCampo = "IMP_PROGETTAZIONE";
          String messaggio = "Il valore indicato dovrebbe essere > 0";
          this.addAvviso(listaControlli, nomeTabella, nomeCampo, "W",
              "Quadro economico variante", messaggio);
        }

        Double imp_disposizione = (Double) SqlManager.getValueFromVectorParam(
            datiW3VARI, 7).getValue();
        if (imp_disposizione == null
            || (imp_disposizione != null && new Double(0).equals(imp_disposizione))) {
          nomeCampo = "IMP_DISPOSIZIONE";
          String messaggio = "Il valore indicato dovrebbe essere > 0";
          this.addAvviso(listaControlli, nomeTabella, nomeCampo, "W",
              "Quadro economico variante", messaggio);
        }

        // Data atto aggiuntivo
        Date data_atto_aggiuntivo = (Date) SqlManager.getValueFromVectorParam(
            datiW3VARI, 8).getValue();

        if (data_atto_aggiuntivo != null && data_stipula != null) {
          if (data_atto_aggiuntivo.getTime() <= data_stipula.getTime()) {
            nomeCampo = "DATA_ATTO_AGGIUNTIVO";
            String messaggio = this.getMessaggioConfrontoDate(
                data_atto_aggiuntivo, ">", data_stipula, "W3INIZ",
                "DATA_STIPULA", new Long(2));
            this.addAvviso(listaControlli, nomeTabella, nomeCampo, "E",
                "Atti aggiuntivi / sottomissione", messaggio);
          }
        }
      }

    } catch (SQLException e) {
      throw new GestoreException(
          "Errore nella lettura delle informazioni relative alla variante",
          "validazioneW3VARI", e);
    }

    if (logger.isDebugEnabled())
      logger.debug("validazioneW3VARI: fine metodo");
  }

  /**
   * Controllo dati per gli accordi bonari (W3ACCO)
   * 
   * @param sqlManager
   * @param schedacompleta_id
   * @param num
   * @param listaControlli
   * @throws GestoreException
   */
  private void validazioneW3ACCO(SqlManager sqlManager, Long schedacompleta_id,
      Long num, List listaControlli) throws GestoreException {
    if (logger.isDebugEnabled())
      logger.debug("validazioneW3ACCO: inizio metodo");

    String nomeTabella = "W3ACCO";
    String nomeCampo = "";

    try {
      // Data verbale inizio
      Date data_verb_inizio = (Date) sqlManager.getObject(
          "select data_verb_inizio from w3iniz where schedacompleta_id = ?",
          new Object[] { schedacompleta_id });

      String selectW3ACCO = "select data_accordo, oneri_derivanti, num_riserve from w3acco where schedacompleta_id = ? and num = ?";
      List datiW3ACCO = sqlManager.getVector(selectW3ACCO, new Object[] {
          schedacompleta_id, num });
      if (datiW3ACCO != null && datiW3ACCO.size() > 0) {

        // Data accordo
        Date data_accordo = (Date) SqlManager.getValueFromVectorParam(
            datiW3ACCO, 0).getValue();
        if (SqlManager.getValueFromVectorParam(datiW3ACCO, 0).getValue() == null) {
          nomeCampo = "DATA_ACCORDO";
          this.addCampoObbligatorio(listaControlli, nomeTabella, nomeCampo,
              "Accordo bonario");
        }

        if (data_accordo != null && data_verb_inizio != null) {
          if (data_accordo.getTime() < data_verb_inizio.getTime()) {
            nomeCampo = "DATA_ACCORDO";
            String messaggio = this.getMessaggioConfrontoDate(data_accordo,
                ">=", data_verb_inizio, "W3INIZ", "DATA_VERB_INIZIO", new Long(
                    2));
            this.addAvviso(listaControlli, nomeTabella, nomeCampo, "E",
                "Accordo bonario", messaggio);
          }
        }

        // Oneri derivanti
        Double oneri_derivanti = (Double) SqlManager.getValueFromVectorParam(
            datiW3ACCO, 1).getValue();
        if (oneri_derivanti == null
            || (oneri_derivanti != null && new Double(0).equals(oneri_derivanti))) {
          nomeCampo = "ONERI_DERIVANTI";
          String messaggio = "Il valore indicato dovrebbe essere > 0";
          this.addAvviso(listaControlli, nomeTabella, nomeCampo, "W",
              "Accordo bonario", messaggio);
        }

        // Numero riserve
        if (SqlManager.getValueFromVectorParam(datiW3ACCO, 2).getValue() == null) {
          nomeCampo = "NUM_RISERVE";
          this.addCampoObbligatorio(listaControlli, nomeTabella, nomeCampo,
              "Accordo bonario");
        }

      }
    } catch (SQLException e) {
      throw new GestoreException(
          "Errore nella lettura delle informazioni relative all'accordo bonario",
          "validazioneW3ACCO", e);
    }

    if (logger.isDebugEnabled())
      logger.debug("validazioneW3ACCO: fine metodo");
  }

  /**
   * Controllo dati per subappalti (W3SUBA)
   * 
   * @param sqlManager
   * @param schedacompleta_id
   * @param num
   * @param titolo
   * @param listaControlli
   * @throws GestoreException
   */
  private void validazioneW3SUBA(SqlManager sqlManager, Long schedacompleta_id,
      Long num, List listaControlli) throws GestoreException {
    if (logger.isDebugEnabled())
      logger.debug("validazioneW3SUBA: inizio metodo");

    String nomeTabella = "W3SUBA";
    String nomeCampo = "";

    try {

      // Data verbale aggiudicazione
      Date data_verb_aggiudicazione = (Date) sqlManager.getObject(
          "select data_verb_aggiudicazione from w3appa where schedacompleta_id = ?",
          new Object[] { schedacompleta_id });

      // Data stipula contratto
      Date data_stipula = (Date) sqlManager.getObject(
          "select data_stipula from w3iniz where schedacompleta_id = ?",
          new Object[] { schedacompleta_id });

      String selectW3SUBA = "select codimp, data_autorizzazione, importo_presunto, id_categoria, id_cpv, importo_effettivo from w3suba where schedacompleta_id = ? and num = ?";
      List datiW3SUBA = sqlManager.getVector(selectW3SUBA, new Object[] {
          schedacompleta_id, num });
      if (datiW3SUBA != null && datiW3SUBA.size() > 0) {

        // Codice della ditta
        if (SqlManager.getValueFromVectorParam(datiW3SUBA, 0).getValue() == null) {
          nomeCampo = "CODIMP";
          this.addCampoObbligatorio(listaControlli, nomeTabella, nomeCampo,
              "Subappalto");
        }

        // Data di autorizzazione subappalto
        Date data_autorizzazione = (Date) SqlManager.getValueFromVectorParam(
            datiW3SUBA, 1).getValue();
        if (data_autorizzazione != null && data_verb_aggiudicazione != null) {
          if (data_autorizzazione.getTime() < data_verb_aggiudicazione.getTime()) {
            nomeCampo = "DATA_AUTORIZZAZIONE";
            String messaggio = this.getMessaggioConfrontoDate(
                data_autorizzazione, ">=", data_verb_aggiudicazione, "W3APPA",
                "DATA_VERB_AGGIUDICAZIONE", new Long(1));
            this.addAvviso(listaControlli, nomeTabella, nomeCampo, "E",
                "Subappalto", messaggio);
          }
        }

        if (data_autorizzazione != null && data_stipula != null) {
          if (data_autorizzazione.getTime() <= data_stipula.getTime()) {
            nomeCampo = "DATA_AUTORIZZAZIONE";
            String messaggio = this.getMessaggioConfrontoDate(
                data_autorizzazione, ">", data_stipula, "W3INIZ",
                "DATA_STIPULA", new Long(2));
            this.addAvviso(listaControlli, nomeTabella, nomeCampo, "E",
                "Subappalto", messaggio);
          }
        }

        // Importo presunto
        Double importo_presunto = (Double) SqlManager.getValueFromVectorParam(
            datiW3SUBA, 2).getValue();
        if (importo_presunto == null) {
          nomeCampo = "IMPORTO_PRESUNTO";
          this.addCampoObbligatorio(listaControlli, nomeTabella, nomeCampo,
              "Subappalto");
        }
        if (importo_presunto != null && new Double(0).equals(importo_presunto)) {
          nomeCampo = "IMPORTO_PRESUNTO";
          String messaggio = "Il valore indicato deve essere > 0";
          this.addAvviso(listaControlli, nomeTabella, nomeCampo, "E",
              "Subappalto", messaggio);
        }

        // Categoria
        String id_categoria = (String) SqlManager.getValueFromVectorParam(
            datiW3SUBA, 3).getValue();
        if (id_categoria == null) {
          nomeCampo = "ID_CATEGORIA";
          this.addCampoObbligatorio(listaControlli, nomeTabella, nomeCampo,
              "Subappalto");
        }

        // Codice CPV
        if (SqlManager.getValueFromVectorParam(datiW3SUBA, 4).getValue() == null) {
          nomeCampo = "ID_CPV";
          this.addCampoObbligatorio(listaControlli, nomeTabella, nomeCampo,
              "Subappalto");
        }

        // Importo effettivo
        Double importo_effettivo = (Double) SqlManager.getValueFromVectorParam(
            datiW3SUBA, 5).getValue();
        if (importo_effettivo != null
            && new Double(0).equals(importo_effettivo)) {
          nomeCampo = "IMPORTO_EFFETTIVO";
          String messaggio = "Il valore indicato dovrebbe essere > 0";
          this.addAvviso(listaControlli, nomeTabella, nomeCampo, "W",
              "Subappalto", messaggio);
        }
      }
    } catch (SQLException e) {
      throw new GestoreException(
          "Errore nella lettura delle informazioni relative al subappalto",
          "validazioneW3RITA", e);
    }

    if (logger.isDebugEnabled())
      logger.debug("validazioneW3SUBA: fine metodo");
  }

  /**
   * Controllo dati per Ipotesi di recesso (W3RITA)
   * 
   * @param sqlManager
   * @param schedacompleta_id
   * @param num
   * @param listaControlli
   * @throws GestoreException
   */
  private void validazioneW3RITA(SqlManager sqlManager, Long schedacompleta_id,
      Long num, List listaControlli) throws GestoreException {
    if (logger.isDebugEnabled())
      logger.debug("validazioneW3RITA: inizio metodo");

    String selectW3RITA = "select data_termine, tipo_comun, durata_sosp, "
        + " data_ist_recesso, flag_accolta, flag_tardiva, flag_ripresa, flag_riserva, "
        + " importo_spese, importo_oneri, data_consegna "
        + " from w3rita where schedacompleta_id = ? and num = ? ";
    String nomeTabella = "W3RITA";
    String nomeCampo = "";
    String pagina = "";
    try {
      List datiW3RITA = sqlManager.getVector(selectW3RITA, new Object[] {
          schedacompleta_id, num });
      if (datiW3RITA != null && datiW3RITA.size() > 0) {

        pagina = "Ritardo o sospensione nella consegna";

        if (SqlManager.getValueFromVectorParam(datiW3RITA, 0).getValue() == null) {
          nomeCampo = "DATA_TERMINE";
          this.addCampoObbligatorio(listaControlli, nomeTabella, nomeCampo,
              pagina);
        }

        Date data_consegna = (Date) SqlManager.getValueFromVectorParam(
            datiW3RITA, 10).getValue();
        if (data_consegna.getYear() > data_consegna.getYear()) {
          nomeCampo = "DATA_CONSEGNA";
          String messaggio = "L\'anno digitato è superiore all\'anno in corso: si è sicuri del valore della data digitata";
          this.addAvviso(listaControlli, nomeTabella, nomeCampo, "W", pagina,
              messaggio);
        }

        if (SqlManager.getValueFromVectorParam(datiW3RITA, 1).getValue() == null) {
          nomeCampo = "TIPO_COMUN";
          this.addCampoObbligatorio(listaControlli, nomeTabella, nomeCampo,
              pagina);
        }

        if (SqlManager.getValueFromVectorParam(datiW3RITA, 2).getValue() == null) {
          nomeCampo = "DURATA_SOSP";
          this.addCampoObbligatorio(listaControlli, nomeTabella, nomeCampo,
              pagina);
        }

        pagina = "Istanza di recesso";
        if (SqlManager.getValueFromVectorParam(datiW3RITA, 3).getValue() == null) {
          nomeCampo = "DATA_IST_RECESSO";
          this.addCampoObbligatorio(listaControlli, nomeTabella, nomeCampo,
              pagina);
        }

        if (SqlManager.getValueFromVectorParam(datiW3RITA, 4).getValue() == null) {
          nomeCampo = "FLAG_ACCOLTA";
          this.addCampoObbligatorio(listaControlli, nomeTabella, nomeCampo,
              pagina);
        }

        if (SqlManager.getValueFromVectorParam(datiW3RITA, 5).getValue() == null) {
          nomeCampo = "FLAG_TARDIVA";
          this.addCampoObbligatorio(listaControlli, nomeTabella, nomeCampo,
              pagina);
        }

        if (SqlManager.getValueFromVectorParam(datiW3RITA, 6).getValue() == null) {
          nomeCampo = "FLAG_RIPRESA";
          this.addCampoObbligatorio(listaControlli, nomeTabella, nomeCampo,
              pagina);
        }

        if (SqlManager.getValueFromVectorParam(datiW3RITA, 7).getValue() == null) {
          nomeCampo = "FLAG_RISERVA";
          this.addCampoObbligatorio(listaControlli, nomeTabella, nomeCampo,
              pagina);
        }

        Double importoSpese = (Double) SqlManager.getValueFromVectorParam(
            datiW3RITA, 8).getValue();
        if (importoSpese == null
            || (importoSpese != null && new Double(0).equals(importoSpese))) {
          nomeCampo = "IMPORTO_SPESE";
          this.addAvviso(listaControlli, nomeTabella, nomeCampo, "W", pagina,
              "L\'importo indicato dovrebbe essere > 0");
        }

        Double importoOneri = (Double) SqlManager.getValueFromVectorParam(
            datiW3RITA, 9).getValue();
        if (importoOneri == null
            || (importoOneri != null && new Double(0).equals(importoOneri))) {
          nomeCampo = "IMPORTO_ONERI";
          this.addAvviso(listaControlli, nomeTabella, nomeCampo, "W", pagina,
              "L\'importo indicato dovrebbe essere > 0");
        }

      }
    } catch (SQLException e) {
      throw new GestoreException(
          "Errore nella lettura delle informazioni relative all'ipotesi di recesso",
          "validazioneW3RITA", e);
    }
    if (logger.isDebugEnabled())
      logger.debug("validazioneW3RITA: fine metodo");
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
    String messaggio = "Il campo &egrave; obbligatorio";
    listaControlli.add(((Object) (new Object[] { "E", pagina, descrizione,
        messaggio })));
  }

  /**
   * Aggiunge un messaggio di avviso alla listaControlli
   * 
   * @param listaControlli
   * @param messaggio
   */
  private void addAvviso(List listaControlli, String nomeTabella,
      String nomeCampo, String tipo, String pagina, String messaggio) {
    String descrizione = this.getDescrizioneCampo(nomeTabella, nomeCampo);
    listaControlli.add(((Object) (new Object[] { tipo, pagina, descrizione,
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
   * Restituisce la descrizione della fase di esecuzione
   * 
   * @param fase_esecuzione
   * @return
   */
  private String getDescrizioneFase(Long fase_esecuzione) {
    String descrizione = "";

    if (new Long(1).equals(fase_esecuzione))
      descrizione = "Fase di aggiudicazione o definizione di procedura negoziata";
    if (new Long(2).equals(fase_esecuzione))
      descrizione = "Fase iniziale di esecuzione del contratto";
    if (new Long(3).equals(fase_esecuzione))
      descrizione = "Fase di esecuzione e avanzamento del contratto";
    if (new Long(4).equals(fase_esecuzione))
      descrizione = "Fase di conclusione del contratto";
    if (new Long(5).equals(fase_esecuzione)) descrizione = "Fase di collaudo";
    if (new Long(6).equals(fase_esecuzione)) descrizione = "Sospensione";
    if (new Long(7).equals(fase_esecuzione)) descrizione = "Variante";
    if (new Long(8).equals(fase_esecuzione)) descrizione = "Accordo bonario";
    if (new Long(9).equals(fase_esecuzione)) descrizione = "Subappalto";
    if (new Long(10).equals(fase_esecuzione))
      descrizione = "Istanza di recesso";

    return descrizione;
  }

  /**
   * Restituisce una stringa con il messaggio di contronto
   * 
   * @return
   */
  private String getMessaggioConfrontoDate(Date data_confronto1,
      String parametro_confronto, Date data_confronto2,
      String entita_confronto2, String campo_confronto2, Long fase_confronto2) {
    String descrizione = "La data indicata ("
        + UtilityDate.convertiData(data_confronto1,
            UtilityDate.FORMATO_GG_MM_AAAA)
        + ") deve essere ";

    if ("<".equals(parametro_confronto)) descrizione += "precedente";
    if ("<=".equals(parametro_confronto)) descrizione += "precedente o uguale";
    if ("=".equals(parametro_confronto)) descrizione += "uguale";
    if (">".equals(parametro_confronto)) descrizione += "successiva";
    if (">=".equals(parametro_confronto)) descrizione += "successiva o uguale";

    descrizione += " alla \""
        + this.getDescrizioneCampo(entita_confronto2, campo_confronto2)
        + "\" ("
        + UtilityDate.convertiData(data_confronto2,
            UtilityDate.FORMATO_GG_MM_AAAA)
        + ")";

    if (fase_confronto2 != null) {
      descrizione += " indicata nella \""
          + this.getDescrizioneFase(fase_confronto2)
          + "\"";
    }

    return descrizione;

  }

  /**
   * Restituisce una stringa con il messaggio di confronto
   * 
   * @param importo_confronto1
   * @param parametro_confronto
   * @param importo_confronto2
   * @param entita_confronto2
   * @param campo_confronto2
   * @param fase_confronto2
   * @return
   */
  private String getMessaggioControntoImporti(Double importo_confronto1,
      String parametro_confronto, Double importo_confronto2,
      String entita_confronto2, String campo_confronto2, Long fase_confronto2)

  {
    String descrizione = "L\'importo digitato ("
        + this.importoToStringa(importo_confronto1)
        + ") deve essere ";

    if ("<".equals(parametro_confronto)) descrizione += "inferiore";
    if ("<=".equals(parametro_confronto)) descrizione += "inferiore o uguale";
    if ("=".equals(parametro_confronto)) descrizione += "uguale";
    if (">".equals(parametro_confronto)) descrizione += "superiore";
    if (">=".equals(parametro_confronto)) descrizione += "superiore o uguale";

    descrizione += " al valore del campo \""
        + this.getDescrizioneCampo(entita_confronto2, campo_confronto2)
        + "\" ("
        + this.importoToStringa(importo_confronto2)
        + ")";

    if (fase_confronto2 != null) {
      descrizione += " della \""
          + this.getDescrizioneFase(fase_confronto2)
          + "\"";
    }

    return descrizione;
  }

  /**
   * Restituisce la rappresentazione in stringa dell'importo passato
   * 
   * @param importo
   * @return
   */
  private String importoToStringa(Double importo) {

    String ret = "";

    double valore = importo.doubleValue();
    if (valore != 0) {
      DecimalFormatSymbols simbolo = new DecimalFormatSymbols();
      simbolo.setDecimalSeparator(',');
      simbolo.setGroupingSeparator('.');
      DecimalFormat decFormat = new DecimalFormat("###,###,###,##0.00", simbolo);
      ret = decFormat.format(valore) + "&nbsp;&euro;";
    }

    return ret;

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

  /**
   * Arrotonda un double alla posizione decimale indicata
   * 
   * @param value
   * @param decimalPlace
   * @return
   */
  private static double round(double value, int decimalPlace) {
    if (logger.isDebugEnabled())
      logger.debug("double(" + value + "," + decimalPlace + "): inizio metodo");

    double powerOfTen = 1;
    while (decimalPlace-- > 0)
      powerOfTen *= 10.0;

    if (logger.isDebugEnabled())
      logger.debug("double(" + value + "," + decimalPlace + "): fine metodo");

    return Math.round(value * powerOfTen) / powerOfTen;

  }

}
