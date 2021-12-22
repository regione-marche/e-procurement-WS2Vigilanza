package it.eldasoft.sil.vigilanza.utils;

import it.eldasoft.gene.bl.GenChiaviManager;
import it.eldasoft.gene.bl.GeneManager;
import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.db.datautils.DataColumn;
import it.eldasoft.gene.db.datautils.DataColumnContainer;
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.sil.vigilanza.beans.ImpresaType;
import it.eldasoft.sil.vigilanza.beans.IncaricoProfessionaleType;
import it.eldasoft.sil.vigilanza.beans.LegaleRappresentanteType;
import it.eldasoft.sil.vigilanza.bl.Credenziale;
import it.eldasoft.sil.vigilanza.bl.RupManager;
import it.eldasoft.sil.vigilanza.commons.CostantiWSW9;
import it.eldasoft.sil.vigilanza.ws.beans.ResponseLottoType;
import it.eldasoft.sil.vigilanza.ws.beans.ResponseSchedaAType;
import it.eldasoft.sil.vigilanza.ws.beans.ResponseSchedaBType;
import it.eldasoft.sil.vigilanza.ws.beans.ResponseType;
import it.eldasoft.utils.properties.ConfigManager;
import it.eldasoft.utils.utility.UtilityDate;
import it.eldasoft.utils.utility.UtilityStringhe;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Vector;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.log4j.Logger;

/**
 * Raccolta di metodi di utilita' per la gestione dei controlli sull'esistenza
 * delle gare, dei lotti e delle fasi, sulla loro abilitazione e visualizzazione
 * e sulle condizioni complesse S1, S2, R, E1, SAQ, AAQ ed EA.
 * 
 * Questa classe e' stata copiata dal progetto sitat-core-engine.
 * 
 * @author Luca.Giacomazzo
 */
public class UtilitySITAT {

  /**
   * Costante per la modalita' di realizzazione pari a "Adesione accordo quadro senza confronto competitivo".
   * (W9GARA.TIPO_APP = 11)
   */
  public static final Long ADESIONE_ACCORDO_QUADRO_SENZA_CONFRONTO_COMPETITIVO = new Long(11);

  /**
   * Costante per la modalita' di realizzazione pari a Stipula Accordo Quadro (W9GARA.TIPO_APP = 9).
   */
  public static final Long STIPULA_ACCORDO_QUADRO = new Long(9);

  /**
   * Costante per la modalita' di realizzazione pari a "Accordo Quadro" (W9GARA.TIPO_APP = 17).
   */
  public static final Long ACCORDO_QUADRO = new Long(17);

  /**
   * Costante per la modalita' di realizzazione pari a "Convenzione" (W9GARA.TIPO_APP = 18).
   */
  public static final Long CONVENZIONE = new Long(18);
  
  /**
   * Logger di classe.
   */
  private static Logger logger = Logger.getLogger(UtilitySITAT.class);
  
  /**
   * Controllo di esistenza di un avviso (AVVISO) identificata dal codice
   * IDAVVISO assegnato da SIMOG.
   * 
   * @param idavviso Id Avviso dell'avviso
   * @param codsistema Codice Sistema dell'avviso
   * @param codein codice della S.A. (campo chiave della UFFINT)
   * @param sqlManager SqlManager
   * @return Ritorna true se esiste una gara identificata dal codice IDAVVISO
   * @throws SQLException SwlException
   */
  public static boolean existsAvviso(final Long idavviso, final Long codsistema, String codein,
  		SqlManager sqlManager) throws SQLException {
    Long numeroOccorrenze = (Long) sqlManager.getObject(
        "select count(*) from AVVISO where IDAVVISO = ? and CODSISTEMA = ? and CODEIN = ? ",
        new Object[] { idavviso, codsistema, codein });
    return numeroOccorrenze.intValue() > 0;
  }
  
  /**
   * Controllo di esistenza di una gara (W9GARA) identificata dal codice
   * IDAVGARA assegnato da SIMOG.
   * 
   * @param idavgara IdAvGara della gara
   * @param sqlManager SqlManager
   * @return Ritorna true se esiste una gara identificata dal codice IDAVGARA
   * @throws SQLException SwlException
   */
  public static boolean existsGara(final String idavgara, final SqlManager sqlManager)
  throws SQLException {
    Long numeroOccorrenze = (Long) sqlManager.getObject(
        "select count(*) from w9gara where idavgara = ?",
        new Object[] { idavgara });
    return numeroOccorrenze.intValue() > 0;
  }

  /**
   * Controllo di esistenza di un lotto (W9LOTT) identificato dal codice CIG
   * assegnato da SIMOG.
   * 
   * @param codiceCig Codice CIG del lotto
   * @param sqlManager SqlManager
   * @return Ritorna true se esiste il lotto identificato dal codice CIG
   * @throws SQLException SqlException
   */
  public static boolean existsLotto(final String codiceCig, final SqlManager sqlManager)
  throws SQLException {
    Long numeroOccorrenze = (Long) sqlManager.getObject(
        "select count(*) from w9lott where cig = ?", new Object[] { codiceCig });
    return numeroOccorrenze.intValue() > 0;
  }

  /**
   * Controllo di esistenza di una fase su un lotto identificato dal CIG. Nel
   * controllo si considerano tutte le occorrenze di fasi del tipo indicato
   * indipendentemente dal numero progressivo.
   * 
   * @param codiceCig Codice CIG del lotto
   * @param faseEsecuzione Fase di esecuzione
   * @param sqlManager SqlMAnager
   * @return Ritorna true se esiste una fase specifica su un lotto identificato dal CIG
   * @throws SQLException SqlException
   */
  public static boolean existsFase(final String codiceCig, final int faseEsecuzione,
      final SqlManager sqlManager) throws SQLException {
    Long numeroOccorrenze = (Long) sqlManager.getObject(
        "select count(*) from w9fasi, w9lott "
        + "where w9fasi.codgara = w9lott.codgara "
        + "and w9fasi.codlott = w9lott.codlott "
        + "and w9lott.cig = ? "
        + "and w9fasi.fase_esecuzione = ? ", new Object[] { codiceCig,
            new Long(faseEsecuzione) });
    return numeroOccorrenze.intValue() > 0;
  }
  
  /**
   * Controllo di esistenza di una fase gia' esportata su un lotto identificato dal CIG. Nel
   * controllo si considerano tutte le occorrenze di fasi del tipo indicato
   * indipendentemente dal numero progressivo.
   * 
   * @param codiceCig Codice CIG del lotto
   * @param faseEsecuzione Fase di esecuzione
   * @param sqlManager SqlMAnager
   * @return Ritorna true se esiste una fase specifica su un lotto identificato dal CIG
   * @throws SQLException SqlException
   */
  public static boolean existsFaseEsportata(final String codiceCig, final int faseEsecuzione,
      final SqlManager sqlManager) throws SQLException {
    Long numeroOccorrenze = (Long) sqlManager.getObject(
        "select count(*) from w9fasi, w9lott "
        + "where w9fasi.codgara = w9lott.codgara "
        + "and w9fasi.codlott = w9lott.codlott "
        + "and w9lott.cig = ? "
        + "and w9fasi.fase_esecuzione = ? " 
        + "and w9fasi.daexport = '2' ", new Object[] { codiceCig,
            new Long(faseEsecuzione) });
    return numeroOccorrenze.intValue() > 0;
  }

  /**
   * Controllo di esistenza di una fase su un lotto identificato dal CIG. Nel
   * controllo si considera anche il numero progressivo (NUM) della fase.
   * 
   * @param codiceCig Codice CIG del lotto
   * @param faseEsecuzione Fase di esecuzione
   * @param num Numero progressivo della fase
   * @param sqlManager SqlMAnager
   * @return Ritorna true se esiste una fase specifica su un lotto identificato dal CIG
   * @throws SQLException SqlException
   */
  public static boolean existsFase(final String codiceCig, final int faseEsecuzione, final Long num,
      final SqlManager sqlManager) throws SQLException {
    Long numeroOccorrenze = (Long) sqlManager.getObject(
        "select count(*) from w9fasi, w9lott "
        + "where w9fasi.codgara = w9lott.codgara "
        + "and w9fasi.codlott = w9lott.codlott "
        + "and w9lott.cig = ? "
        + "and w9fasi.fase_esecuzione = ? "
        + "and w9fasi.num = ?", new Object[] { codiceCig,
            new Long(faseEsecuzione), num });
    return numeroOccorrenze.intValue() > 0;
  }
  
  /**
   * Controllo di esistenza di una fase gia' esportata su un lotto identificato dal CIG. Nel
   * controllo si considera anche il numero progressivo (NUM) della fase.
   * 
   * @param codiceCig Codice CIG del lotto
   * @param faseEsecuzione Fase di esecuzione
   * @param num Numero progressivo della fase
   * @param sqlManager SqlMAnager
   * @return Ritorna true se esiste una fase specifica su un lotto identificato dal CIG
   * @throws SQLException SqlException
   */
  public static boolean existsFaseEsportata(final String codiceCig, final int faseEsecuzione, final Long num,
      final SqlManager sqlManager) throws SQLException {
    Long numeroOccorrenze = (Long) sqlManager.getObject(
        "select count(*) from w9fasi, w9lott "
        + "where w9fasi.codgara = w9lott.codgara "
        + "and w9fasi.codlott = w9lott.codlott "
        + "and w9lott.cig = ? "
        + "and w9fasi.fase_esecuzione = ? "
        + "and w9fasi.num = ? " 
        + "and w9fasi.daexport = '2' ", new Object[] { codiceCig,
            new Long(faseEsecuzione), num });
    return numeroOccorrenze.intValue() > 0;
  }

  /**
   * Controllo di esistenza di una fase su un lotto identificato dal codice gara
   * e dal codice lotto. Nel controllo si considerano tutte le occorrenze di
   * fasi del tipo indicato indipendentemente dal numero progressivo.
   * 
   * @param codgara Codice della gara
   * @param codlott Codice del lotto
   * @param faseEsecuzione Fase di esecuzione
   * @param sqlManager SqlManager
   * @return TRUE se la fase esiste, FALSE altrimenti
   * @throws SQLException SqlException
   */
  public static boolean existsFase(final Long codgara, final Long codlott, final Long numAppa,
      final int faseEsecuzione, final SqlManager sqlManager) throws SQLException {
    Long numeroOccorrenze = (Long) sqlManager.getObject(
        "select count(*) from w9fasi "
        + "where w9fasi.codgara = ? "
        + "and w9fasi.codlott = ? "
        + "and w9fasi.fase_esecuzione = ? "
        + "and num_appa = ?", new Object[] { codgara,
            codlott, new Long(faseEsecuzione), numAppa });
    return numeroOccorrenze.intValue() > 0;
  }
  
  /**
   * Controllo di esistenza di una fase gia' esportata su un lotto identificato dal codice gara
   * e dal codice lotto. Nel controllo si considerano tutte le occorrenze di
   * fasi del tipo indicato indipendentemente dal numero progressivo.
   * 
   * @param codgara Codice della gara
   * @param codlott Codice del lotto
   * @param faseEsecuzione Fase di esecuzione
   * @param sqlManager SqlManager
   * @return TRUE se la fase esiste, FALSE altrimenti
   * @throws SQLException SqlException
   */
  public static boolean existsFaseEsportata(final Long codgara, final Long codlott,
      final int faseEsecuzione, final SqlManager sqlManager) throws SQLException {
    Long numeroOccorrenze = (Long) sqlManager.getObject(
        "select count(*) from w9fasi "
        + "where w9fasi.codgara = ? "
        + "and w9fasi.codlott = ? "
        + "and w9fasi.fase_esecuzione = ? " 
        + "and w9fasi.daexport = '2' ", new Object[] { codgara,
            codlott, new Long(faseEsecuzione) });
    return numeroOccorrenze.intValue() > 0;
  }
  
  /**
   * Controllo di esistenza di una fase su un lotto identificato dal codice gara
   * e dal codice lotto. Nel controllo si considera anche il numero progressivo
   * (NUM) della fase.
   * 
   * @param codgara Codice della gara
   * @param codlott Codice del lotto
   * @param faseEsecuzione Fase di esecuzione
   * @param num Numero della fase
   * @param sqlManager SqlManager
   * @return TRUE se la fase esiste, FALSE altrimenti
   * @throws SQLException SqlExeption
   */
  public static boolean existsFase(final Long codgara, final Long codlott, final Long numappa,
      final int faseEsecuzione, final Long num, final SqlManager sqlManager) throws SQLException {
	  Long numeroOccorrenze = null;
	  if (num != null) {
		  numeroOccorrenze = (Long) sqlManager.getObject(
			        "select count(*) from w9fasi "
			        + "where w9fasi.codgara = ? "
			        + "and w9fasi.codlott = ? "
			        + "and w9fasi.num_appa = ? "
			        + "and w9fasi.fase_esecuzione = ? "
			        + "and w9fasi.num = ?", new Object[] { codgara,
			          codlott, numappa, new Long(faseEsecuzione), num });
		  return numeroOccorrenze.intValue() > 0;
	  } else {
	  	return false;
	  }
  }
  
  /**
   * Controllo di esistenza di una fase gia' esportata su un lotto identificato dal codice gara
   * e dal codice lotto. Nel controllo si considera anche il numero progressivo
   * (NUM) della fase.
   * 
   * @param codgara Codice della gara
   * @param codlott Codice del lotto
   * @param faseEsecuzione Fase di esecuzione
   * @param num Numero della fase
   * @param sqlManager SqlManager
   * @return TRUE se la fase esiste, FALSE altrimenti
   * @throws SQLException SqlExeption
   */
  public static boolean existsFaseEsportata(final Long codgara, final Long codlott,
      final int faseEsecuzione, final Long num, final SqlManager sqlManager)
  throws SQLException {
    Long numeroOccorrenze = (Long) sqlManager.getObject(
        "select count(*) from w9fasi "
        + "where w9fasi.codgara = ? "
        + "and w9fasi.codlott = ? "
        + "and w9fasi.fase_esecuzione = ? "
        + "and w9fasi.num = ? " 
        + "and w9fasi.daexport = '2' ", new Object[] { codgara, codlott,
            new Long(faseEsecuzione), num });
    return numeroOccorrenze.intValue() > 0;
  }

  /**
   * Controllo di esistenza di una pubblicazione identificata dal codice gara
   * e dal codice della pubblicazione. 
   * 
   * @param codgara Codice della gara
   * @param codicePubblicazione Codice della pubblicazione
   * @param sqlManager SqlManager
   * @return TRUE se la pubblicazione esiste, FALSE altrimenti
   * @throws SQLException SqlExeption
   */
  public static boolean existsPubblicazioneDocumenti(final Long codgara, final long codicePubblicazione,
  		final SqlManager sqlManager) throws SQLException {
  	Long numeroOccorrenze = (Long) sqlManager.getObject(
          " select count(*) from W9PUBBLICAZIONI where CODGARA=? and ID_GENERATO=? ",
          new Object[] { codgara, codicePubblicazione });
    return numeroOccorrenze.intValue() > 0;
  }

  /**
   * Controllo di esistenza del record in W9SIC
   * 
   * @param codgara Codice della gara
   * @param codlott Codice del lotto
   * @param num Numero della fase
   * @param sqlManager SqlManager
   * @return Ritorna true se esiste il lotto identificato dal codice CIG
   * @throws SQLException SqlException
   */
  public static boolean existsW9SIC(final Long codgara, final Long codlott,
	      final Long num, final SqlManager sqlManager)
  throws SQLException {
    Long numeroOccorrenze = (Long) sqlManager.getObject(
        "select count(*) from w9sic where codgara = ? "
        + "and codlott = ? "
        + "and num_sic = ?", new Object[] { codgara, codlott, num});
    return numeroOccorrenze.intValue() > 0;
  }
  
  /**
   * Test sulla condizione S1. Restituisce TRUE se l'importo totale del lotto è
   * maggiore di 20000 euro per forniture o servizi oppure se l'importo totale
   * del lotto è maggiore di 40000 euro per lavori
   * 
   * @param codgara Codice della gara
   * @param codlott Codice del lotto
   * @param sqlManager SqlManager
   * @return Ritorna true se S1
   * @throws SQLException SqlException
   */
  /*public static boolean isS1(final Long codgara, final Long codlott, final SqlManager sqlManager)
  throws SQLException {

    Double importoTot = getImportoTotaleLotto(codgara, codlott, sqlManager);
    String tipoContratto = getTipoContrattoLotto(codgara, codlott, sqlManager);
    return isS1(importoTot, tipoContratto);
  }*/

  /**
   * Test sulla condizione S1 a partire dal codice CIG del lotto.
   * Restituisce TRUE se l'importo totale del lotto e' maggiore di 20000 euro per forniture
   * o servizi oppure se l'importo totale del lotto e' maggiore di 40000 euro per lavori
   * 
   * @param codiceCIG Codice CIG del lotto
   * @param sqlManager SqlManager
   * @return Ritorna true se S1
   * @throws SQLException SqlException
   */
  /*public static boolean isS1(final String codiceCIG, final SqlManager sqlManager)
  throws SQLException {

    Double importoTot = getImportoTotaleLotto(codiceCIG, sqlManager);
    String tipoContratto = getTipoContrattoLotto(codiceCIG, sqlManager);
    return isS1(importoTot, tipoContratto);
  }*/

  /**
   * Test sulla condizione S1. Restituisce TRUE se l'importo totale del lotto è
   * maggiore di 20000 euro per forniture o servizi oppure se l'importo totale
   * del lotto è maggiore di 40000 euro per lavori
   * 
   * @param importoTot Importo totale del lotto
   * @param tipoContratto Tipo di contratto
   * @return Ritorna true se S1
   */
  /*private static boolean isS1(final Double importoTot, final String tipoContratto) {
    boolean isS1 = false;
    if (importoTot != null && tipoContratto != null) {
      if (StringUtils.indexOf(tipoContratto, 'L') >= 0) {
        if (importoTot.doubleValue() > 40000) {
          isS1 = true;
        }
      } else {
        if (importoTot.doubleValue() > 20000) {
          isS1 = true;
        }
      }
    }
    return isS1;
  }*/

  /**
   * Ritorna true se il campo W9LOTT.EXSOTTOSOGLIA = '1', false altrimenti.
   * 
   * @param codgara Codice della gara
   * @param codlott Codice del lotto
   * @param sqlManager SqlManager
   * @return Ritorna true se il campo W9LOTT.EXSOTTOSOGLIA = '1', false altrimenti
   * @throws SQLException SqlException 
   */
  public static boolean isExSottosoglia(final Long codgara, final Long codlott,
  		final SqlManager sqlManager) throws SQLException {
  	return "1".equals(sqlManager.getObject("select EXSOTTOSOGLIA from W9LOTT where CODGARA=? and CODLOTT=?", 
  			new Object[] { codgara, codlott }));
  }
  
  /**
   * Test sulla condizione S2. Restituisce TRUE se l'importo totale del lotto (W9LOTT.IMPORTO_TOT)
   * e' maggiore di 40000 euro e il lotto non e' un lotto ex sottosoglia (W9LOTT.EXSOTTOSOGLIA = '2' o null).
   * 
   * @param codgara Codice della gara
   * @param codlott Codice del lotto
   * @param sqlManager SqlManager
   * @return Ritorna true se l'importo totale del lotto e' maggiore di 40000 euro e il lotto non e' un lotto ex sottosoglia
   * @throws SQLException SqlException
   */
  public static boolean isS2(final Long codgara, final Long codlott, final SqlManager sqlManager) throws SQLException {
    boolean isExSottoSoglia = UtilitySITAT.isLottoExSottoSoglia(codgara, codlott, sqlManager); 
    Double importoTot = UtilitySITAT.getImportoTotaleLotto(codgara, codlott, sqlManager);
    return isS2(importoTot, isExSottoSoglia);
  }

  /**
   * Test sulla condizione S2. Restituisce TRUE se l'importo totale del lotto (W9LOTT.IMPORTO_TOT)
   * e' maggiore di 40000 euro e il lotto non e' un lotto ex sottosoglia (W9LOTT.EXSOTTOSOGLIA = '2' o null)
   * 
   * @param codiceCIG Codice CIG del lotto
   * @param sqlManager SqlManager
   * @return Ritorna true se l'importo totale del lotto e' maggiore di 40000 euro e il lotto non e' un lotto ex sottosoglia
   * @throws SQLException SqlException
   */
  public static boolean isS2(final String codiceCIG, final SqlManager sqlManager) throws SQLException {
    boolean isExSottoSoglia = UtilitySITAT.isLottoExSottoSoglia(codiceCIG, sqlManager);
    Double importoTot = UtilitySITAT.getImportoTotaleLotto(codiceCIG, sqlManager);
    return isS2(importoTot, isExSottoSoglia);
  }
  
  /**
   * Test sulla condizione S2. Restituisce TRUE se l'importo totale del lotto (W9LOTT.IMPORTO_TOT)
   * e' maggiore di 40000 euro e il lotto non e' un lotto ex sottosoglia (W9LOTT.EXSOTTOSOGLIA = '2' o null)
   * 
   * @param importoTot Importo totale del lotto
   * @param isExSottosoglia is lotto ex sottosoglia
   * @return Ritorna true se l'importo totale del lotto e' maggiore di 40000 euro e il lotto non e' un lotto ex sottosoglia
   */
  private static boolean isS2(final Double importoTot, boolean isExSottoSoglia) {
    boolean isS2 = false;
    if (importoTot != null && importoTot.doubleValue() >= 40000 && !isExSottoSoglia) {
      isS2 = true;
    }
    return isS2;
  }

  /**
   * Ritorna true se l'importo del lotto e' sopra i 40000 euro, false altrimenti.
   * 
   * @param codiceCIG
   * @param sqlManager
   * @return
   * @throws SQLException
   */
  public static boolean isImportoLottoSoprasoglia(final String codiceCIG, final SqlManager sqlManager) throws SQLException {
    Double importoTot = UtilitySITAT.getImportoTotaleLotto(codiceCIG, sqlManager);
    if (importoTot != null && importoTot.doubleValue() >= 40000) {
      return true;
    } else {
      return false;
    }
  }
  
  /**
   * Ritorna true se l'importo del lotto e' sopra i 40000 euro, false altrimenti.
   * 
   * @param codgara
   * @param codlott
   * @param sqlManager
   * @return
   * @throws SQLException
   */
  public static boolean isImportoLottoSoprasoglia(final Long codgara, final Long codlott, final SqlManager sqlManager) throws SQLException {
    Double importoTot = UtilitySITAT.getImportoTotaleLotto(codgara, codlott, sqlManager);
    if (importoTot != null && importoTot.doubleValue() >= 40000) {
      return true;
    } else {
      return false;
    }
  }
  
  /**
   * Test is lotto ex sotto soglia. Ritorna TRUE se il lotto ha il campo W9LOTT.EXSOTTOSOGLIA valorizzato a 1.
   * 
   * @param codiceCIG
   * @param sqlManager
   * @return Ritorna true se il lotto ha il campo W9LOTT.EXSOTTOSOGLIA valorizzato a 1, false altrimenti.
   * @throws SQLException 
   */
  private static boolean isLottoExSottoSoglia(final Long codgara, final Long codlott,
      final SqlManager sqlManager) throws SQLException {
    boolean isExSottoSoglia = false;
    
    String exSottoSoglia = (String) sqlManager.getObject(
        "select EXSOTTOSOGLIA from W9LOTT where CODGARA=? and CODLOTT=? ",
        new Object[] { codgara, codlott });
    if ("1".equals(exSottoSoglia)) {
      isExSottoSoglia = true;
    }
    return isExSottoSoglia;
  }
  
  /**
   * Test is lotto ex sotto soglia. Ritorna TRUE se il lotto ha il campo W9LOTT.EXSOTTOSOGLIA valorizzato a 1.
   * 
   * @param codiceCIG
   * @param sqlManager
   * @return Ritorna true se il lotto ha il campo W9LOTT.EXSOTTOSOGLIA valorizzato a 1, false altrimenti.
   * @throws SQLException 
   */
  private static boolean isLottoExSottoSoglia(final String codiceCIG, final SqlManager sqlManager) throws SQLException {
    boolean isExSottoSoglia = false;
    
    String exSottoSoglia = (String) sqlManager.getObject(
        "select EXSOTTOSOGLIA from W9LOTT where CIG=? ",
        new Object[] { codiceCIG });
    
    if ("1".equals(exSottoSoglia)) {
      isExSottoSoglia = true;
    }
    return isExSottoSoglia;
  }
  
  /**
   * Test sulla condizione S3. Restituisce TRUE se l'importo totale del lotto e' maggiore di 500000 euro.
   * 
   * @param codgara Codice della gara
   * @param codlott Codice del lotto
   * @param sqlManager SqlManager
   * @return Ritorna true se l'importo totale del lotto e' maggiore di 500000 euro
   * @throws SQLException SqlException
   */
  public static boolean isS3(final Long codgara, final Long codlott, final SqlManager sqlManager)
  throws SQLException {
    Double importoTot = UtilitySITAT.getImportoTotaleLotto(codgara, codlott, sqlManager);
    return isS3(importoTot);
  }

  /**
   * Test sulla condizione S3. Restituisce TRUE se l'importo totale del lotto e' maggiore di 500000 euro.
   * 
   * @param codiceCIG Codice CIG del lotto
   * @param sqlManager SqlManager
   * @return Ritorna true se l'importo totale del lotto e' maggiore di 500000 euro
   * @throws SQLException SqlException
   */
  public static boolean isS3(final String codiceCIG, final SqlManager sqlManager)
  throws SQLException {
    Double importoTot = UtilitySITAT.getImportoTotaleLotto(codiceCIG, sqlManager);
    return isS3(importoTot);
  }

  /**
   * Test sulla condizione S3. Restituisce TRUE se l'importo totale del lotto e' maggiore di 500000 euro.
   * 
   * @param importoTot Importo totale del lotto
   * @return Ritorna true se l'importo totale del lotto e' maggiore di 500000 euro
   * @throws SQLException
   */
  private static boolean isS3(final Double importoTot) {
    boolean isS3 = false;
    if (importoTot != null && importoTot.doubleValue() >= 500000) {
      isS3 = true;
    }
    return isS3;
  }
  
  
  /**
   * Test sulla condizione R. Restituisce TRUE se Manodopera/Posa in opera vale Si.
   * 
   * @param codgara Codice della gara
   * @param codlott Codice del lotto
   * @param sqlManager SqlManager
   * @return  Ritorna true se Manodopera/Posa in opera vale Si
   * @throws SQLException SqlException
   */
  public static boolean isR(final Long codgara, final Long codlott, final SqlManager sqlManager)
  throws SQLException {
    String manod = (String) sqlManager.getObject("select manod from w9lott "
        + "where w9lott.codgara = ? and w9lott.codlott = ? ",
        new Object[] { codgara, codlott });
    return isR(manod);
  }

  /**
   * Test sulla condizione R a partire dal codice CIG del lotto.
   * Restituisce TRUE se Manodopera/Posa in opera vale Si
   * 
   * @param codiceCIG Codice CIG del lotto
   * @param sqlManager SqlManager
   * @return  Ritorna true se Manodopera/Posa in opera vale Si
   * @throws SQLException SqlException
   */
  public static boolean isR(final String codiceCIG, final SqlManager sqlManager)
  throws SQLException {
    String manod = (String) sqlManager.getObject("select manod from w9lott "
        + "where w9lott.cig = ? ", new Object[] { codiceCIG });
    return isR(manod);
  }

  /**
   * Test sulla condizione R. Restituisce TRUE se Manodopera/Posa in opera vale Si.
   * 
   * @param manod Mano d'opera
   * @return Ritorna true se Manodopera/Posa in opera vale Si
   */
  private static boolean isR(final String manod) {
    boolean isR = false;
    if (manod != null && "1".equals(manod)) {
      isR = true;
    }
    return isR;
  }

  /**
   * Test sulla condizione E1. Restituisce TRUE se il campo "Contratto escluso
   * ex art 10, 20, 21, 22, 23, 24, 25, 26 D. Lgs. 163/06?" = "Si"
   * 
   * @param codgara Codice della gara
   * @param codlott Codice del lotto
   * @param sqlManager SqlManager
   * @return Ritorna true se il campo "Contratto escluso ex art 10, 20, 21, 22, 23, 24, 25, 26 D. Lgs. 163/06?" = "Si"
   * @throws SQLException SqlException
   */
  public static boolean isE1(final Long codgara, final Long codlott, final SqlManager sqlManager)
  throws SQLException {
    String artE1 = (String) sqlManager.getObject("select art_e1 from w9lott "
        + "where w9lott.codgara = ? "
        + "and w9lott.codlott = ? ", new Object[] { codgara, codlott });
    return isE1(artE1);
  }

  /**
   * Test sulla condizione E1 a partire dal codice CIG del lotto.
   * Restituisce TRUE se il campo "Contratto escluso ex art 10, 20, 21, 22,
   * 23, 24, 25, 26 D. Lgs. 163/06?" = "Si"
   * 
   * @param codiceCIG Codice CIG del lotto
   * @param sqlManager SqlManager
   * @return Ritorna true se il campo "Contratto escluso ex art 10, 20, 21, 22, 23, 24, 25, 26 D. Lgs. 163/06?" = "Si"
   * @throws SQLException SqlException
   */
  public static boolean isE1(final String codiceCIG, final SqlManager sqlManager)
  throws SQLException {
    String artE1 = (String) sqlManager.getObject("select art_e1 from w9lott "
        + "where w9lott.cig = ? ", new Object[] { codiceCIG });
    return isE1(artE1);
  }

  /**
   * Test sulla condizione E1. Restituisce TRUE se il campo "Contratto escluso ex art 10, 20, 21, 22, 23, 24, 25, 26 D. Lgs. 163/06?" = "Si"
   * 
   * @param artE1 Campo ARTE1
   * @return Ritorna true se il campo "Contratto escluso ex art 10, 20, 21, 22, 23, 24, 25, 26 D. Lgs. 163/06?" = "Si"
   * @throws SQLException
   */
  private static boolean isE1(final String artE1) {
    boolean isArtE1 = false;
    if (artE1 != null && "1".equals(artE1)) {
      isArtE1 = true;
    }
    return isArtE1;
  }

  /**
   * Test sulla condizione SAQ. Restituisce TRUE se la modalita' di realizzazione (W9GARA.TIPO_APP) e' Stipula Accordo Quadro (TIPO_APP = 9).
   * 
   * @param codgara Codice della gara
   * @param sqlManager SqlManager
   * @return Ritorna true se la modalita' di realizzazione (W9GARA.TIPO_APP) e' Stipula Accordo Quadro (TIPO_APP = 9)
   * @throws SQLException SqlException
   */
  public static boolean isSAQ(final Long codgara, final SqlManager sqlManager)
  throws SQLException {
    return isSAQ(UtilitySITAT.getTipoAppalto(codgara, sqlManager));
  }

  /**
   * Test se la modalita' di realizzazione e' Stipula Accordo Quadro (SAQ) a partire dal codice CIG (TIPO_APP = 9).
   * 
   * @param codiceCIG Codice CIG del lotto
   * @param sqlManager SqlManager
   * @return Ritorna true se la modalita' di realizzazione e' Stipula Accordo Quadro (SAQ) a partire dal codice CIG (TIPO_APP = 9)
   * @throws SQLException SqlException
   */
  public static boolean isSAQ(final String codiceCIG, final SqlManager sqlManager)
  throws SQLException {
    return isSAQ(UtilitySITAT.getTipoAppalto(codiceCIG, sqlManager));
  }

  /**
   * Restituisce TRUE se la modalita' di realizzazione (W9GARA.TIPO_APP) e' Stipula Accordo Quadro, Accordo quadro o Convenzione
   * (TIPO_APP = 9 o 17 o 18) Tabellato TAB1COD = W3999)
   * @param tipoApp Tipo Appalto
   * @return Ritorna true se la modalita' di realizzazione (W9GARA.TIPO_APP) e' Stipula Accordo Quadro, Accordo quadro o Convenzione (TIPO_APP = 9, 17, 18)
   */
  private static boolean isSAQ(final Long tipoApp) {
    boolean isSAQ = false;
    if (tipoApp != null && (UtilitySITAT.STIPULA_ACCORDO_QUADRO.equals(tipoApp)
          || UtilitySITAT.ACCORDO_QUADRO.equals(tipoApp)
              || UtilitySITAT.CONVENZIONE.equals(tipoApp))) {
      isSAQ = true;
    }
    return isSAQ;
  }

  /**
   * Test se la modalita' di realizzazione e' Adesione Accordo Quadro senza
   * confronto competitivo (AAQ).
   * (TIPO_APP = 11)
   * 
   * @param codgara Codice della gara
   * @param sqlManager SqlManager
   * @return Ritorna true se la modalita' di realizzazione e' Adesione Accordo Quadro senza
   * confronto competitivo (AAQ)
   * @throws SQLException SqlException
   */
  public static boolean isAAQ(final Long codgara, final SqlManager sqlManager)
  throws SQLException {
    return isAAQ(UtilitySITAT.getTipoAppalto(codgara, sqlManager));
  }

  /**
   * Test se la modalita' di realizzazione e' Adesione Accordo Quadro senza
   * confronto competitivo a partire dal codice CIG (AAQ). (TIPO_APP = 11)
   * 
   * @param codiceCIG Codice CIG del lotto
   * @param sqlManager SqlManager
   * @return Ritorna true se la modalita' di realizzazione e' Adesione Accordo Quadro senza confronto competitivo a partire dal codice CIG (AAQ). (TIPO_APP = 11)
   * @throws SQLException SqlException
   */
  public static boolean isAAQ(final String codiceCIG, final SqlManager sqlManager)
  throws SQLException {
    return isAAQ(UtilitySITAT.getTipoAppalto(codiceCIG, sqlManager));
  }

  /**
   * Restituisce TRUE se la modalita' di realizzazione e' "Adesione accordo quadro senza confronto competitivo" (TIPO_APP = 11).
   * 
   * @param tipoApp Tipo appalto
   * @return Ritorna true se il tipo appalto e' uguale a 11
   */
  private static boolean isAAQ(final Long tipoApp) {
    boolean isAAQ = false;

    if (tipoApp != null && UtilitySITAT.ADESIONE_ACCORDO_QUADRO_SENZA_CONFRONTO_COMPETITIVO.equals(tipoApp)) {
      isAAQ = true;
    }
    return isAAQ;
  }

  /**
   * Test sulla condizione EA. Restituisce TRUE se il lotto e' aggiudicato ossia
   * se nella fase "Esito" il lotto e' indicato come aggiudicato (W9ESITO.ESITO_PROCEDURA = 1)
   * e non si ha una interruzione anticipata del contratto (cioe' non esiste record in W9CONC con INTANTIC='1')
   * 
   * @param codgara Codice della gara
   * @param codlott Codice del lotto
   * @param sqlManager SqlManager
   * @return Ritorna true se il lotto e' aggiudicato
   * @throws SQLException SqlException
   */
  public static boolean isEA(final Long codgara, final Long codlott, final SqlManager sqlManager)
  throws SQLException {
    Long numOccorrenze = (Long) sqlManager.getObject(
        "select count(*) from W9ESITO where CODGARA=? and CODLOTT=?", new Object[] { codgara, codlott });
    if (numOccorrenze > 0) {
      Long esitoProcedura = (Long) sqlManager.getObject(
        "select esito_procedura from w9esito "
        + "where w9esito.codgara = ? "
        + "and w9esito.codlott = ? ", new Object[] { codgara, codlott });
      Long esisteConclusioneAnticipata = (Long) sqlManager.getObject(
          "select count(*) from W9CONC where CODGARA=? and CODLOTT=? and INTANTIC='1'", 
          new Object[] { codgara, codlott });
      
      boolean isConclusioneAnticipata = false;
      if (esisteConclusioneAnticipata != null && (new Long(1)).equals(esisteConclusioneAnticipata)) {
        isConclusioneAnticipata = true;
      }
      
      return isEA(esitoProcedura, isConclusioneAnticipata);
    } else {
      return true;
    }
  }

  /**
   * Test sulla condizione EA. Restituisce TRUE se il lotto e' aggiudicato ossia
   * se nella fase "Esito" il lotto e' indicato come aggiudicato (W9ESITO.ESITO_PROCEDURA = 1)
   * e non si ha una interruzione anticipata del contratto (cioe' non esiste record in W9CONC con INTANTIC='1')
   * 
   * @param codiceCIG Codice CIG del lotto
   * @param sqlManager SqlManager
   * @return Ritorna true se il lotto e' aggiudicato
   * @throws SQLException SqlException
   */
  public static boolean isEA(final String codiceCIG, final SqlManager sqlManager)
  throws SQLException {
    Long numOccorrenze = (Long) sqlManager.getObject(
        "select count(e.CODGARA) from W9ESITO e, W9LOTT l where e.CODGARA=l.CODGARA and e.CODLOTT=l.CODLOTT and l.CIG=?",
        new Object[] { codiceCIG });
    if (numOccorrenze > 0) {
      Long esitoProcedura = (Long) sqlManager.getObject(
          "select esito_procedura from w9esito, w9lott "
          + "where w9esito.codgara = w9lott.codgara ? "
          + "and w9esito.codlott = w9lott.codlott "
          + "and w9lott.cig = ? ", new Object[] { codiceCIG});
      
      Long esisteConclusioneAnticipata = (Long) sqlManager.getObject(
          "select count(*) from W9CONC, W9LOTT "
         + "where W9CONC.CODGARA=W9LOTT.CODGARA and W9CONC.CODLOTT=W9LOTT.CODLOTT=? and W9LOTT.CIG=? and W9CONC.INTANTIC='1'", 
          new Object[] { codiceCIG });
      
      boolean isConclusioneAnticipata = false;
      if (esisteConclusioneAnticipata != null && (new Long(1)).equals(esisteConclusioneAnticipata)) {
        isConclusioneAnticipata = true;
      }
      return isEA(esitoProcedura, isConclusioneAnticipata);
    } else {
      return true;
    }
  }

  /** Test sulla condizione EA. Restituisce TRUE se il lotto e' aggiudicato ossia
   * se nella fase "Esito" il lotto e' indicato come aggiudicato
   * (W9ESITO.ESITO_PROCEDURA = 1) e non si ha una interruzione anticipata del contratto 
   * (cioe' non esiste record in W9CONC con INTANTIC = '1')
   *
   * @return Ritorna true se il lotto è aggiudicato, false altrimenti
   * @param esitoProcedura Esito della procedura
   */
  private static boolean isEA(final Long esitoProcedura, final boolean isConclusioneAnticipata) {
    boolean isEA = false;
    if (esitoProcedura != null && (new Long(1)).equals(esitoProcedura)) {
      isEA = true;
    }
    return isEA && !isConclusioneAnticipata;
  }

  /**
   * Test sulla condizione AII. Restituisce TRUE se la gara e' per un intervento
   * di ricostruzione alluvione Lunigiana e Elba (W9GARA.RIC_ALLUV = '1').
   * 
   * @param codgara Codice della gara
   * @param sqlManager SqlManager
   * @return Ritorna true se il campo W9GARA.RIC_ALLUV='1', false altrimenti
   * @throws SQLException SQLException
   */
  public static boolean isAII(final Long codgara, final SqlManager sqlManager) throws SQLException {
    boolean result = false;
    String ricostruzioneAlluzione = (String) sqlManager.getObject(
        "select RIC_ALLUV from W9GARA where CODGARA=?", new Object[]{codgara});

    if ("1".equals(ricostruzioneAlluzione)) {
      result = true;
    }
    return result;
  }

  /**
   * Test sulla condizione AII. Restituisce TRUE se la gara e' per un intervento
   * di ricostruzione alluvione Lunigiana e Elba (W9GARA.RIC_ALLUV = '1').
   * 
   * @param cig Codice CIG del lotto
   * @param sqlManager SqlManager
   * @return Ritorna true se il campo W9GARA.RIC_ALLUV='1', false altrimenti
   * @throws SQLException SQLException
   */
  public static boolean isAII(final String cig, final SqlManager sqlManager) throws SQLException {
    boolean result = false;
    String ricostruzioneAlluzione = (String) sqlManager.getObject(
        "select g.RIC_ALLUV from W9LOTT l, W9GARA g where l.CODGARA=g.CODGARA and l.CIG=?", new Object[]{cig});

    if ("1".equals(ricostruzioneAlluzione)) {
      result = true;
    }
    return result;
  }

  
  /**
   * Test sulla condizione Ord Restituisce TRUE se il flag ente speciale e' uguale a 'O'
   *  
   * @param codgara Codice della gara
   * @param sqlManager SqlManager
   * @return Ritorna true se il campo W9LOTT.FLAG_ENTE_SPECIALE='O', false altrimenti
   * @throws SQLException SQLException
   */
  public static boolean isOrd(final Long codgara, final SqlManager sqlManager) throws SQLException {
    boolean result = false;
    String settoriOrdinari = (String) sqlManager.getObject(
        "select l.FLAG_ENTE_SPECIALE from W9LOTT l, W9GARA g where l.CODGARA=g.CODGARA and g.CODGARA=?", new Object[]{codgara});

    if ("O".equals(settoriOrdinari)) {
      result = true;
    } else {
    	Long versioneSimog = (Long) sqlManager.getObject(
    	    "select VER_SIMOG from W9GARA where CODGARA=?", new Object[] { codgara } );
    	result = (versioneSimog != null && versioneSimog>=4);
    }
    return result;
  }

  /**
   * Test sulla condizione Ord Restituisce TRUE se il flag ente speciale e' uguale a 'O'.
   * 
   * @param cig Codice CIG del lotto
   * @param sqlManager SqlManager
   * @return Ritorna true se il campo W9LOTT.FLAG_ENTE_SPECIALE='O', false altrimenti
   * @throws SQLException SQLException
   */
  public static boolean isOrd(final String cig, final SqlManager sqlManager) throws SQLException {
    boolean result = false;
    String settoriOrdinari = (String) sqlManager.getObject(
        "select l.FLAG_ENTE_SPECIALE from W9LOTT l where l.CIG=?", new Object[]{cig});

    if ("O".equals(settoriOrdinari)) {
      result = true;
    } else {
    	Long versioneSimog = (Long) sqlManager.getObject(
    			"select W9GARA.VER_SIMOG from W9LOTT left join W9GARA on W9LOTT.CODGARA = W9GARA.CODGARA where W9LOTT.CIG=?", new Object[] { cig } );
    	result = (versioneSimog != null && versioneSimog.longValue() >= 4);
    }
    return result;
  }
  
  
  /**
   * Restituisce il tipo appalto (W9GARA.TIPO_APP) a partire da CODGARA.
   * 
   * @param codgara Codice della gara
   * @param sqlManager SqlManager
   * @return Ritorna W9GARA.TIPO_APP a partire da CODGARA
   * @throws SQLException SqlException
   */
  private static Long getTipoAppalto(final Long codgara, final SqlManager sqlManager)
  throws SQLException {

    return (Long) sqlManager.getObject("select tipo_app from w9gara "
        + "where w9gara.codgara = ?", new Object[] { codgara });
  }

  /**
   * Restituisce il tipo appalto (W9GARA.TIPO_APP) a partire dal CIG di un lotto della gara.
   * 
   * @param codiceCIG Codice CIG del lotto
   * @param sqlManager SqlManager
   * @return Ritorna il tipo di appalto della gara
   * @throws SQLException SqlException
   */
  private static Long getTipoAppalto(final String codiceCIG, final SqlManager sqlManager)
  throws SQLException {

    return (Long) sqlManager.getObject("select tipo_app from w9gara, w9lott "
        + "where w9gara.codgara = w9lott.codgara and w9lott.cig = ?",
        new Object[] { codiceCIG });
  }

  /**
   * Restituisce l'importo totale del lotto (W9LOTT.IMPORTO_TOT).
   * 
   * @param codgara Codice della gara
   * @param codlott Codice del lotto
   * @param sqlManager SqlManager
   * @return Ritorna l'importo totale del lotto
   * @throws SQLException SqlException
   */
  private static Double getImportoTotaleLotto(final Long codgara, final Long codlott,
      final SqlManager sqlManager) throws SQLException {
    Double importoTot = (Double) sqlManager.getObject(
        "select importo_tot from w9lott "
        + "where w9lott.codgara = ? "
        + "and w9lott.codlott = ? ", new Object[] { codgara, codlott });
    return importoTot;
  }

  /**
   * Restituisce l'importo totale del lotto a partire dal CIG del lotto (W9LOTT.IMPORTO_TOT).
   * 
   * @param codiceCIG Codice CIG del lotto
   * @param sqlManager SqlManager
   * @return Ritorna l'importo totale del lotto
   * @throws SQLException SqlException
   */
  private static Double getImportoTotaleLotto(final String codiceCIG, final SqlManager sqlManager)
  		throws SQLException {
    Double importoTot = (Double) sqlManager.getObject(
        "select importo_tot from w9lott where w9lott.cig = ? ",
        new Object[] { codiceCIG });
    return importoTot;
  }

  /** Restituisce il tipo di contratto del lotto (W9LOTT.TIPO_CONTRATTO).
   * 
   * @param codgara Codice della gara
   * @param codlott Codice del lotto
   * @param sqlManager SqlManager
   * @return Ritorna il tipo di contratto del lotto
   * @throws SQLException SqlException
   */
  public static String getTipoContrattoLotto(final Long codgara, final Long codlott,
      final SqlManager sqlManager) throws SQLException {
    String tipoContratto = (String) sqlManager.getObject(
        "select tipo_contratto from w9lott "
        + "where w9lott.codgara = ? "
        + "and w9lott.codlott = ? ", new Object[] { codgara, codlott });
    return tipoContratto;
  }

  /**
   * Ritorna true se in W_CONFIG la property it.eldasoft.simog.tipoAccesso e' uguale a '1', false altrimenti.
   * 
   * @param sqlManager
   * @return Ritorna true se in W_CONFIG la property it.eldasoft.simog.tipoAccesso e' uguale a '1', false altrimenti  
   */
  public static boolean isConfigurazioneVigilanza(SqlManager sqlManager) throws SQLException {
  	String valore = (String) sqlManager.getObject("select valore from W_CONFIG where codapp=? and chiave=?", 
  			new Object[] { "W9", "it.eldasoft.simog.tipoAccesso" });
    return "1".equals(valore);
  }

  /** 
   * Ritorna true se il TAB1NORD relativo alla <i>faseEsecuzione</i> e' maggiore di 0, false altrimenti
   * 
   * @param faseEsecuzione
   * @param sqlManager
   * @return
   * @throws SQLException
   */
  public static boolean isFaseAttiva(Long faseEsecuzione, SqlManager sqlManager) throws SQLException {
  	Long countTAB1 = (Long) sqlManager.getObject(
    		"select count(*) from TAB1 where TAB1COD='W3020' and TAB1TIP=? and TAB1NORD > 0", new Object[] { faseEsecuzione });
  	
  	if (countTAB1.longValue() > 0)
  		return true;
  	else
  		return false;
  }
  
  /**
   * Test sulla condizione di abilitazione. Restituisce TRUE se la fase è
   * abilitata.
   * 
   * @param codgara Codice della gara
   * @param codlott Codice del lotto
   * @param faseEsecuzione Fase di esecuzione
   * @param sqlManager SqlManager
   * @return Ritorna true se la fase e' abilitata, false altrimenti
   * @throws SQLException SqlException
   */
  public static boolean isFaseAbilitata(final Long codgara, final Long codlott, final Long numAppa,
      final int faseEsecuzione, final SqlManager sqlManager) throws SQLException {

    boolean isAbilitata = false;
    
    switch (faseEsecuzione) {
    case CostantiWSW9.COMUNICAZIONE_ESITO:  // A22 - 984
    case CostantiWSW9.ELENCO_IMPRESE_INVITATE_PARTECIPANTI: // A24 - 101
    	isAbilitata = !UtilitySITAT.existsFase(codgara, codlott, numAppa, CostantiWSW9.A04, sqlManager)
				&& !UtilitySITAT.existsFase(codgara, codlott, numAppa, CostantiWSW9.A05, sqlManager);
    	break;
    case CostantiWSW9.AGGIUDICAZIONE_SOPRA_SOGLIA:  // A05 - 1
    case CostantiWSW9.FASE_SEMPLIFICATA_AGGIUDICAZIONE:  // A04 - 987
    case CostantiWSW9.ADESIONE_ACCORDO_QUADRO:  // A21 - 12
    case CostantiWSW9.ESITO_NEGATIVO_VERIFICA_CONTRIBUTIVA_ASSICURATIVA: // B03 - 996
    	isAbilitata = true;
      break;

    case CostantiWSW9.STIPULA_ACCORDO_QUADRO: // A20 - 11a
    	isAbilitata = UtilitySITAT.existsFase(codgara, codlott, numAppa, CostantiWSW9.A04, sqlManager)
    							|| UtilitySITAT.existsFase(codgara, codlott, numAppa, CostantiWSW9.A05, sqlManager);
      break;

    case CostantiWSW9.INIZIO_CONTRATTO_SOPRA_SOGLIA: // A06 - 2
   		isAbilitata = (UtilitySITAT.existsFase(codgara, codlott, numAppa, CostantiWSW9.A05, sqlManager)
     			|| UtilitySITAT.existsFase(codgara, codlott, numAppa, CostantiWSW9.A21, sqlManager)) &&
       				!UtilitySITAT.existsFase(codgara, codlott, numAppa, CostantiWSW9.A09, sqlManager);
      break;

    case CostantiWSW9.FASE_SEMPLIFICATA_INIZIO_CONTRATTO: // A07 - 986
   		isAbilitata = (UtilitySITAT.existsFase(codgara, codlott, numAppa, CostantiWSW9.A04, sqlManager) 
   				|| UtilitySITAT.existsFase(codgara, codlott, numAppa, CostantiWSW9.A05, sqlManager)
     					|| UtilitySITAT.existsFase(codgara, codlott, numAppa, CostantiWSW9.A21, sqlManager)) &&
     							!UtilitySITAT.existsFase(codgara, codlott, numAppa, CostantiWSW9.A10, sqlManager);
      break;

    case CostantiWSW9.AVANZAMENTO_CONTRATTO_SOPRA_SOGLIA: // A08 - 3
    	isAbilitata = (UtilitySITAT.existsFase(codgara, codlott, numAppa, CostantiWSW9.A06, sqlManager)
    			|| UtilitySITAT.existsFase(codgara, codlott, numAppa, CostantiWSW9.A07, sqlManager)
    					|| UtilitySITAT.existsFase(codgara, codlott, numAppa, CostantiWSW9.A20, sqlManager)) 
    					&& !UtilitySITAT.existsFase(codgara, codlott, numAppa, CostantiWSW9.A09, sqlManager);
      break;
    case CostantiWSW9.AVANZAMENTO_CONTRATTO_APPALTO_SEMPLIFICATO: // A25 - 102
    	isAbilitata = (UtilitySITAT.existsFase(codgara, codlott, numAppa, CostantiWSW9.A06, sqlManager)
    			|| UtilitySITAT.existsFase(codgara, codlott, numAppa, CostantiWSW9.A07, sqlManager)
							|| UtilitySITAT.existsFase(codgara, codlott, numAppa, CostantiWSW9.A20, sqlManager)) 
    							&& !(UtilitySITAT.existsFase(codgara, codlott, numAppa, CostantiWSW9.A09, sqlManager)
    										|| UtilitySITAT.existsFase(codgara, codlott, numAppa, CostantiWSW9.A10, sqlManager));
      break;

    case CostantiWSW9.SOSPENSIONE_CONTRATTO: // A12 - 6
    	isAbilitata = (UtilitySITAT.existsFase(codgara, codlott, numAppa, CostantiWSW9.A06, sqlManager)
    			|| UtilitySITAT.existsFase(codgara, codlott, numAppa, CostantiWSW9.A07, sqlManager)
    					|| UtilitySITAT.existsFase(codgara, codlott, numAppa, CostantiWSW9.A20, sqlManager)) 
    							&& !UtilitySITAT.existsFase(codgara, codlott, numAppa, CostantiWSW9.A10, sqlManager);
    	break;
      
    case CostantiWSW9.VARIANTE_CONTRATTO: // A13 - 7
    	isAbilitata = (UtilitySITAT.existsFase(codgara, codlott, numAppa, CostantiWSW9.A04, sqlManager)
    			|| UtilitySITAT.existsFase(codgara, codlott, numAppa, CostantiWSW9.A05, sqlManager)
    					|| UtilitySITAT.existsFase(codgara, codlott, numAppa, CostantiWSW9.A21, sqlManager)) 
    							&& !UtilitySITAT.existsFase(codgara, codlott, numAppa, CostantiWSW9.A11, sqlManager);
      break;

    case CostantiWSW9.ACCORDO_BONARIO: // A14 - 8
      isAbilitata = (UtilitySITAT.existsFase(codgara, codlott, numAppa, CostantiWSW9.A06, sqlManager) 
      		|| UtilitySITAT.existsFase(codgara, codlott, numAppa, CostantiWSW9.A07, sqlManager)
      				|| UtilitySITAT.existsFase(codgara, codlott, numAppa, CostantiWSW9.A20, sqlManager));
      break;
      
    case CostantiWSW9.CONCLUSIONE_CONTRATTO_SOPRA_SOGLIA: // A09 - 4
      isAbilitata = UtilitySITAT.existsFase(codgara, codlott, numAppa, CostantiWSW9.A05, sqlManager)
      	|| UtilitySITAT.existsFase(codgara, codlott, numAppa, CostantiWSW9.A21, sqlManager);
      break;

    case CostantiWSW9.FASE_SEMPLIFICATA_CONCLUSIONE_CONTRATTO: // A10 - 985
      isAbilitata = UtilitySITAT.existsFase(codgara, codlott, numAppa, CostantiWSW9.A04, sqlManager)
      		|| UtilitySITAT.existsFase(codgara, codlott, numAppa, CostantiWSW9.A05, sqlManager)
      				|| UtilitySITAT.existsFase(codgara, codlott, numAppa, CostantiWSW9.A21, sqlManager);
      break;

    case CostantiWSW9.COLLAUDO_CONTRATTO: // A11 - 5
      isAbilitata = UtilitySITAT.existsFase(codgara, codlott, numAppa, CostantiWSW9.A09, sqlManager);
      break;

    case CostantiWSW9.SUBAPPALTO: // A15 - 9
   		isAbilitata = (UtilitySITAT.existsFase(codgara, codlott, numAppa, CostantiWSW9.A04, sqlManager)
       			|| UtilitySITAT.existsFase(codgara, codlott, numAppa, CostantiWSW9.A05, sqlManager)
       					|| UtilitySITAT.existsFase(codgara, codlott, numAppa, CostantiWSW9.A21, sqlManager))
       							&& !UtilitySITAT.existsFase(codgara, codlott, numAppa, CostantiWSW9.A09, sqlManager);
      break;
    case CostantiWSW9.ESITO_NEGATIVO_VERIFICA_TECNICO_PROFESSIONALE_IMPRESA_AGGIUDICATARIA: // B02 - 997
    case CostantiWSW9.INADEMPIENZE_SICUREZZA_REGOLARITA_LAVORI: // B06 - 995
   		isAbilitata = UtilitySITAT.existsFase(codgara, codlott, numAppa, CostantiWSW9.A04, sqlManager)
   				|| UtilitySITAT.existsFase(codgara, codlott, numAppa, CostantiWSW9.A05, sqlManager)
   						|| UtilitySITAT.existsFase(codgara, codlott, numAppa, CostantiWSW9.A21, sqlManager);
      break;
      
    case CostantiWSW9.ISTANZA_RECESSO: // A16 - 10
  		// In configurazione Vigilanza non e' richiesta la fase di 'Comunicazione sito' (A22 - 984)
   		isAbilitata = (UtilitySITAT.existsFase(codgara, codlott, numAppa, CostantiWSW9.A05, sqlManager)
					|| UtilitySITAT.existsFase(codgara, codlott, numAppa, CostantiWSW9.A21, sqlManager))
 							&& !UtilitySITAT.existsFase(codgara, codlott, numAppa, CostantiWSW9.A06, sqlManager)
									&& !UtilitySITAT.existsFase(codgara, codlott, numAppa, CostantiWSW9.A07, sqlManager);
      break;

    case CostantiWSW9.SCHEDA_SEGNALAZIONI_INFORTUNI: // B07 - 994
      isAbilitata = UtilitySITAT.existsFase(codgara, codlott, numAppa, CostantiWSW9.A06, sqlManager)
      	|| UtilitySITAT.existsFase(codgara, codlott, numAppa, CostantiWSW9.A07, sqlManager);
      break;
      
    case CostantiWSW9.APERTURA_CANTIERE: // B08 - 998
   		isAbilitata = UtilitySITAT.existsFase(codgara, codlott, numAppa, CostantiWSW9.A05, sqlManager)
					|| UtilitySITAT.existsFase(codgara, codlott, numAppa, CostantiWSW9.A21, sqlManager);
    	break;
    }

    return isAbilitata;
  }
  /**
   * Metodo privato per il test sulla condizione di visualizzazione. In questo
   * test si considerano i controlli sulle fasi diverse da quella di
   * "Esito comunicazione - A22".
   * 
   * @param codgara Codice della gara
   * @param codlott Codice del lotto
   * @param faseEsecuzione Fase di Esecuzione
   * @param sqlManager SqlManager
   * @return Ritorna true se la fase e' visualizzabile, false altrimenti
   * @throws SQLException SqlException
   */
  public static boolean isFaseVisualizzabile(final Long codgara, final Long codlott,
      final int faseEsecuzione, final SqlManager sqlManager) throws SQLException {

    boolean isVisualizzabile = false;

    String tipoContratto = UtilitySITAT.getTipoContrattoLotto(codgara, codlott, sqlManager);

    switch (faseEsecuzione) {
    	case CostantiWSW9.COMUNICAZIONE_ESITO:  // A22 - 984
    	case CostantiWSW9.ELENCO_IMPRESE_INVITATE_PARTECIPANTI:  // A24 - 101
    		isVisualizzabile = !isAAQ(codgara, sqlManager);
    	break;
    	
      case CostantiWSW9.AGGIUDICAZIONE_SOPRA_SOGLIA: // A05 - 1
        isVisualizzabile = isS2(codgara, codlott, sqlManager) && !isAAQ(codgara, sqlManager) && isEA(codgara, codlott, sqlManager);
        break;
  
      case CostantiWSW9.FASE_SEMPLIFICATA_AGGIUDICAZIONE: // A04 - 987
        isVisualizzabile = !isS2(codgara, codlott, sqlManager) && !isAAQ(codgara, sqlManager) && isEA(codgara, codlott, sqlManager);
        break;
        
      case CostantiWSW9.ADESIONE_ACCORDO_QUADRO: // A21 - 12
      	isVisualizzabile = isAAQ(codgara, sqlManager) && isEA(codgara, codlott, sqlManager);
        break;

      case CostantiWSW9.STIPULA_ACCORDO_QUADRO: // A20 - 11
        isVisualizzabile = isSAQ(codgara, sqlManager) && isEA(codgara, codlott, sqlManager) && !isD1(codgara, sqlManager);
        break;

      case CostantiWSW9.INIZIO_CONTRATTO_SOPRA_SOGLIA: // A06 - 2
      	isVisualizzabile = isS2(codgara, codlott, sqlManager) && !isSAQ(codgara, sqlManager) && isEA(codgara, codlott, sqlManager) 
      			 && isOrd(codgara, sqlManager) && !isD1(codgara, sqlManager);
        break;

      case CostantiWSW9.FASE_SEMPLIFICATA_INIZIO_CONTRATTO: // A07 - 986
        isVisualizzabile = (!isS2(codgara, codlott, sqlManager) || !isOrd(codgara, sqlManager)) && !isSAQ(codgara, sqlManager)
        		 && isEA(codgara, codlott, sqlManager) && !isD1(codgara, sqlManager);
        break;
        
      case CostantiWSW9.AVANZAMENTO_CONTRATTO_SOPRA_SOGLIA: // A08 - 3
      	isVisualizzabile = (isS3(codgara, codlott, sqlManager) || isAII(codgara, sqlManager)) && !isSAQ(codgara, sqlManager)
      			&& isEA(codgara, codlott, sqlManager) && isOrd(codgara, sqlManager);
      	break;
      	
      case CostantiWSW9.AVANZAMENTO_CONTRATTO_APPALTO_SEMPLIFICATO: // A25 - 102
      	isVisualizzabile = ((!isS3(codgara, codlott, sqlManager) && !isAII(codgara, sqlManager))
      		 || !isOrd(codgara, sqlManager)) && !isSAQ(codgara, sqlManager) && isEA(codgara, codlott, sqlManager);
      	break;
      	
      case CostantiWSW9.CONCLUSIONE_CONTRATTO_SOPRA_SOGLIA: // A09 - 4
        isVisualizzabile = isS2(codgara, codlott, sqlManager) && isEA(codgara, codlott, sqlManager)
        	&& !isSAQ(codgara, sqlManager) && isOrd(codgara, sqlManager);
        break;
      	
      case CostantiWSW9.FASE_SEMPLIFICATA_CONCLUSIONE_CONTRATTO: // A10 - 985
        isVisualizzabile = (!isS2(codgara, codlott, sqlManager) || !isOrd(codgara, sqlManager))
        		&& isEA(codgara, codlott, sqlManager) && !isSAQ(codgara, sqlManager);
        break;
        
      case CostantiWSW9.COLLAUDO_CONTRATTO: // A11 - 5
      	isVisualizzabile = isS2(codgara, codlott, sqlManager) && !isSAQ(codgara, sqlManager) 
      			 && isEA(codgara, codlott, sqlManager) && isOrd(codgara, sqlManager);
        break;
        
      case CostantiWSW9.SOSPENSIONE_CONTRATTO: // A12 - 6
      	isVisualizzabile = (isS2(codgara, codlott, sqlManager) || isAII(codgara, sqlManager)
      			|| isR(codgara, codlott, sqlManager)) && !isSAQ(codgara, sqlManager)
      			&& isEA(codgara, codlott, sqlManager) && isOrd(codgara, sqlManager);
        break;

      case CostantiWSW9.VARIANTE_CONTRATTO: // A13 - 7
      	isVisualizzabile = (isS2(codgara, codlott, sqlManager) || isAII(codgara, sqlManager))
      				&& !isSAQ(codgara, sqlManager) && isEA(codgara, codlott, sqlManager) 
      				&& isOrd(codgara, sqlManager);
        break;

      case CostantiWSW9.ACCORDO_BONARIO: // A14 - 8
      	isVisualizzabile = isS2(codgara, codlott, sqlManager) && !isSAQ(codgara, sqlManager)
      			&& isEA(codgara, codlott, sqlManager) && isOrd(codgara, sqlManager);
      	break;
  
      case CostantiWSW9.SUBAPPALTO: // A15 - 9
          isVisualizzabile = (isS2(codgara, codlott, sqlManager) || isAII(codgara, sqlManager))
          		&& isEA(codgara, codlott, sqlManager) && isOrd(codgara, sqlManager) && !isD1(codgara, sqlManager);
        break;
  
      case CostantiWSW9.ISTANZA_RECESSO: // A16 _ 10
        if (StringUtils.indexOf(tipoContratto, 'L') >= 0) {
          isVisualizzabile = (isS2(codgara, codlott, sqlManager) || isAII(codgara, sqlManager))
          		&& !isSAQ(codgara, sqlManager) && isEA(codgara, codlott, sqlManager)
          		&& isOrd(codgara, sqlManager) && !isD1(codgara, sqlManager);
        }
        break;
  
      case CostantiWSW9.ESITO_NEGATIVO_VERIFICA_TECNICO_PROFESSIONALE_IMPRESA_AGGIUDICATARIA: // B02 - 997
      case CostantiWSW9.ESITO_NEGATIVO_VERIFICA_CONTRIBUTIVA_ASSICURATIVA: // B03 - 996
        isVisualizzabile = !isSAQ(codgara, sqlManager) && isEA(codgara, codlott, sqlManager) && !isD1(codgara, sqlManager);
        break;
  
      case CostantiWSW9.INADEMPIENZE_SICUREZZA_REGOLARITA_LAVORI: // B06 - 995
      case CostantiWSW9.APERTURA_CANTIERE: // B08 - 998
      	isVisualizzabile = isR(codgara, codlott, sqlManager) && !isSAQ(codgara, sqlManager) && isEA(codgara, codlott, sqlManager)
      			&& !isD1(codgara, sqlManager);
      	break;
      case CostantiWSW9.SCHEDA_SEGNALAZIONI_INFORTUNI: // B07 - 994
      	isVisualizzabile = isR(codgara, codlott, sqlManager) && !isSAQ(codgara, sqlManager) && isEA(codgara, codlott, sqlManager);
        break;
        
    default:
      break;

    }
    return isVisualizzabile;
  }

  /**
   * Ritorna true se il flusso e' un flusso dell'area 1, cioe' 
   * se e' un flusso per le fasi di gara (Area 1).
   * 
   * @param codiceFlusso Codice del flusso (vedi CostantiWSW9)
   * @return Ritorna true se il flusso e' un flusso per le fasi di gara, false altrimenti
   */
  public static boolean isArea1(final int codiceFlusso) {
    int areaFlusso = UtilitySITAT.getAreaFlusso(codiceFlusso);
    if (CostantiWSW9.AREA_FASI_DI_GARA == areaFlusso) {
      return true;
    } else {
      return false;
    }
  }
  
  /**
   * Ritorna true se il flusso e' un flusso dell'area 2, cioe' 
   * se e' il flusso anagrafica gara lotto (Area 2).
   * 
   * @param codiceFlusso Codice del flusso (vedi CostantiWSW9)
   * @return Ritorna true se il flusso e' anagrafica gara lotto, false altrimenti
   */
  public static boolean isArea2(final int codiceFlusso) {
    int areaFlusso = UtilitySITAT.getAreaFlusso(codiceFlusso);
    if (CostantiWSW9.AREA_ANAGRAFICA_GARE == areaFlusso) {
      return true;
    } else {
      return false;
    }
  }
  
  /**
   * Ritorna true se il flusso e' un flusso dell'area 3, cioe' 
   * se e' il flusso pubblicazione avvisi (Area 3).
   * 
   * @param codiceFlusso Codice del flusso (vedi CostantiWSW9)
   * @return Ritorna true se il flusso e' pubblicazione avvisi, false altrimenti
   */
  public static boolean isArea3(final int codiceFlusso) {
    int areaFlusso = UtilitySITAT.getAreaFlusso(codiceFlusso);
    if (CostantiWSW9.AREA_AVVISI == areaFlusso) {
      return true;
    } else {
      return false;
    }
  }
  
  /**
   * Ritorna true se il flusso e' un flusso dell'area 4, cioe' 
   * se e' un flusso per piani triennali o annuali (Area 4).
   * 
   * @param codiceFlusso Codice del flusso (vedi CostantiWSW9)
   * @return Ritorna true se il flusso e' piano triennale/annuale, false altrimenti
   */
  public static boolean isArea4(final int codiceFlusso) {
    int areaFlusso = UtilitySITAT.getAreaFlusso(codiceFlusso);
    if (CostantiWSW9.AREA_PROGRAMMA_TRIENNALI_ANNUALI == areaFlusso) {
      return true;
    } else {
      return false;
    }
  }
  
  /**
   * Ritorna true se il flusso e' un flusso dell'area 5, cioe' 
   * se e' il flusso Gara per enti nazionali o sotto i 40000 euro (Area 5).
   * 
   * @param codiceFlusso Codice del flusso (vedi CostantiWSW9)
   * @return Ritorna true se il flusso e' Gara per enti nazionali o sotto i 40000 euro,
   *         false altrimenti
   */
  public static boolean isArea5(final int codiceFlusso) {
    int areaFlusso = UtilitySITAT.getAreaFlusso(codiceFlusso);
    if (CostantiWSW9.AREA_GARE_ENTINAZIONALI == areaFlusso) {
      return true;
    } else {
      return false;
    }
  }
  
  /**
   * Metodo per determinare se l'utente che ha fatto accesso al WS e' un utente amministratore
   * o compilatore, cioe' il campo USRSYS.SYSAB3='A' oppure 'C', false altrimenti.
   * 
   * @param credenzialiUtente
   * @return Ritorna true se l'utente che ha fatto accesso al WS e' un utente amministratore o compilatore, cioe'
   * il campo USRSYS.SYSAB3 = 'A' oppure 'C', false altrimenti. 
   */
  public static boolean isUtenteAmministratore(Credenziale credenzialiUtente) {
  	if (credenzialiUtente != null) {
  		if (credenzialiUtente.getAccount() != null) {
  			if ("A".equals(credenzialiUtente.getAccount().getAbilitazioneStd())
  					|| 
  				  "C".equals(credenzialiUtente.getAccount().getAbilitazioneStd())) {
  				return true;
  			} else {
  				return false;
  			}
  		} else {
  			return false;
  		}
  	} else {
  		return false;
  	}
  }
  
  /**
   * Metodo per determinare se l'utente collegato al WS e' RUP del lotto con CIG pari a <i>codiceCig</i>.
   * 
   * @param codiceCig Codice Cig del lotto
   * @param credenzialiUtente 
   * @param sqlManager SqlManager
   * @return Ritorna true se l'utente collegato al WS e' RUP del lotto con CIG pari a <i>codiceCig</i>, false altrimenti 
   * @throws SQLException
   */
  public static boolean isUtenteRupDelLotto(final String codiceCig, final Credenziale credenzialiUtente,
  		final SqlManager sqlManager) throws SQLException {
  	
  	Long conteggio = (Long) sqlManager.getObject(
        "select count(*) from TECNI T where T.CGENTEI=? and T.CFTEC=? and " +
        " exists (select 1 from W9LOTT L where L.RUP=T.CODTEC and CIG=?)", 
        new Object[] { credenzialiUtente.getStazioneAppaltante().getCodice(),
        		credenzialiUtente.getAccount().getCodfisc(), codiceCig });
  	if (conteggio == null || (conteggio != null && conteggio.intValue() == 0)) {
  		return false;
  	} else {
  		return true;
  	}
  }
  
  /**
   * Ritorna true se il lotto con codice CIG=<i>cig</i> e' un lotto della gara con
   * IdGara=<i>idavgara</i>.
   *
   * @param idavgara
   * @param codiceCig
   * @param sqlManager
   * @return Ritorna true se il lotto con codice CIG=<i>cig</i> e' un lotto della gara con
   * IdGara=<i>idavgara</i>.
   * @throws SQLException
   */
  public static boolean isCigLottoDellaGara(final String idavgara, final String codiceCig,
      final SqlManager sqlManager) throws SQLException {
    Long numeroOccorrenze = (Long) sqlManager.getObject(
        "select count(*) from W9GARA g, W9LOTT l " +
         "where g.CODGARA=l.CODGARA " +
           "and g.IDAVGARA=? " +
           "and l.CIG=?", new Object[] { idavgara, codiceCig });
    return numeroOccorrenze.intValue() == 1;
  }
  
  /**
   * Ritorna true se il codice della stazione appaltante e' lo stesso di quello ricavabile dal DB a partire dall'idGara.
   *  
   * @param codFisStazAppaltante Codice fiscale della stazione appaltante (vedi CODEIN UFFINT)
   * @param idAvGara Id della gara (vedi W9GARA IDAVGARA)
   * @return Ritorna l'area di appartenenza del flusso
   */
  public static boolean isGaraInStazioneAppaltante(final String codFisStazAppaltante, 
		  final String idAvGara, final SqlManager sqlManager)throws SQLException {
	  
	  boolean result = false;
	  String codFisStazAppaltanteFromDB = (String) sqlManager.getObject(
	        "SELECT CFEIN from UFFINT where CODEIN = (SELECT CODEIN from W9GARA where IDAVGARA=?)",
	        new Object[]{ idAvGara });
	  
	  if (codFisStazAppaltante.equalsIgnoreCase(codFisStazAppaltanteFromDB)) {
		  result = true;
	  }
	  return result;
  }
  
  /**
   * Ritorna true se il codice del RUP e' lo stesso di quello ricavabile dal DB a partire dall'idGara.
   *  
   * @param codFisStazAppaltante Codice fiscale della stazione appaltante (vedi CODEIN UFFINT)
   * @param idGara Id della gara (vedi W9GARA.IDAVGARA)
   * @return Ritorna l'area di appartenenza del flusso
   */
  public static boolean haveSameRUP(final String codFisRUP, 
  		final String idGara, final SqlManager sqlManager)throws SQLException {
  	
  	boolean result = false;
  	String codFisRUPFromDB = (String) sqlManager.getObject(
	        "select CFTEC from W9GARA join TECNI on W9GARA.RUP = TECNI.CODTEC where W9GARA.IDAVGARA=?",
	        new Object[]{idGara});
  	if (codFisRUP.equals(codFisRUPFromDB)) {
		  result = true;
  	}  
  	return result;
  }
  
  /**
   * Ritorna l'area di appartenenza del flusso, a partire dal codice del flusso stesso.
   *  
   * Le aree sono 5:
   * - area 1: fasi di gara;
   * - area 2: anagrafica gara/lotto;
   * - area 3: pubblicazione avvisi
   * - area 4: piani triennali per lavori/annuali per forniture e servizi 
   * - area 5: Gare per enti nazionali o sotto i 40000 euro
   * 
   * @param codiceFlusso Codice del flusso (vedi CostantiWSW9)
   * @return Ritorna l'area di appartenenza del flusso
   */
  public static int getAreaFlusso(final int codiceFlusso) {
    int areaInvio = 0;
    
    switch (codiceFlusso) {
    case CostantiWSW9.AGGIUDICAZIONE_SOPRA_SOGLIA:
      // Fase aggiudicazione/affidamento (>150.000 euro)
    case CostantiWSW9.INIZIO_CONTRATTO_SOPRA_SOGLIA:
      // Fase iniziale esecuzione contratto (>150.000 euro)
    case CostantiWSW9.AVANZAMENTO_CONTRATTO_SOPRA_SOGLIA:
      // Fase esecuzione e avanzamento del contratto
    case CostantiWSW9.CONCLUSIONE_CONTRATTO_SOPRA_SOGLIA:
      // Fase di conclusione del contratto (>150.000 euro)
    case CostantiWSW9.COLLAUDO_CONTRATTO:
      // Fase di collaudo del contratto
    case CostantiWSW9.SOSPENSIONE_CONTRATTO:
      // Sospensione del contratto
    case CostantiWSW9.VARIANTE_CONTRATTO:
      // Variante del contratto
    case CostantiWSW9.ACCORDO_BONARIO:
      // Accordi bonari
    case CostantiWSW9.SUBAPPALTO:
      // Subappalti
    case CostantiWSW9.ISTANZA_RECESSO:
      // Istanza di recesso
    case CostantiWSW9.STIPULA_ACCORDO_QUADRO:
      // Stipula accordo quadro
    case CostantiWSW9.ADESIONE_ACCORDO_QUADRO:
      // Adesioneaccordo quadro
    case CostantiWSW9.FASE_SEMPLIFICATA_AGGIUDICAZIONE:
      // Fase aggiudicazione/affidamento appalto (<150.000 euro)
    case CostantiWSW9.FASE_SEMPLIFICATA_INIZIO_CONTRATTO:
      // Fase iniziale esecuzione contratto (<150.000 euro)
    case CostantiWSW9.FASE_SEMPLIFICATA_CONCLUSIONE_CONTRATTO:
      // Fase di conclusione del contratto (<150.000 euro)
    case CostantiWSW9.AVANZAMENTO_CONTRATTO_APPALTO_SEMPLIFICATO:
      // Fase di avanzamento del contratto (<150.000 euro)
    //case CostantiWSW9.MISURE_AGGIUNTIVE_SICUREZZA:
      // Misure aggiuntive e migliorative sicurezza
    case CostantiWSW9.SCHEDA_SEGNALAZIONI_INFORTUNI:
      // Scheda segnalazione infortuni
    case CostantiWSW9.INADEMPIENZE_SICUREZZA_REGOLARITA_LAVORI:
      // Inadempienze predisposizioni sicurezza e regolarità lavoro
    case CostantiWSW9.ESITO_NEGATIVO_VERIFICA_CONTRIBUTIVA_ASSICURATIVA:
      // Esito negativo verifica regolarità contributiva ed assicurativa
    case CostantiWSW9.ESITO_NEGATIVO_VERIFICA_TECNICO_PROFESSIONALE_IMPRESA_AGGIUDICATARIA:
      // Esito negativo verifica idoneità tecnico-professionale
      // dell'impresa aggiudicataria
    case CostantiWSW9.APERTURA_CANTIERE: // Apertura cantiere
    case CostantiWSW9.COMUNICAZIONE_ESITO: // Comunicazione esito

      areaInvio = CostantiWSW9.AREA_FASI_DI_GARA;
      break;

    case CostantiWSW9.ANAGRAFICA_GARA_LOTTI: // Anagrafica Gara e Lotto/i + CIG + Bando di gara + Pubblicita'
      // gara
      areaInvio = CostantiWSW9.AREA_ANAGRAFICA_GARE;
      break;

    case CostantiWSW9.PUBBLICAZIONE_AVVISO: // Avviso
      areaInvio = CostantiWSW9.AREA_AVVISI;
      break;

    case CostantiWSW9.PROGRAMMA_TRIENNALE_LAVORI: // Programma triennale ed elenco annuale Lavori
    case CostantiWSW9.PROGRAMMA_TRIENNALE_FORNITURE_SERVIZI: // Programma triennale ed elenco annuale Forniture e Servizi
      areaInvio = CostantiWSW9.AREA_PROGRAMMA_TRIENNALI_ANNUALI;
      break;
    case CostantiWSW9.GARE_ENTI_NAZIONALI_O_SOTTO_40000:
      areaInvio = CostantiWSW9.AREA_GARE_ENTINAZIONALI;

    default:
      break;
    }
    
    return areaInvio;
  }

  /**
   * Controlli per verificare che il RUP del lotto sia uguale al RUP della gara
   * e sia anche uguale al RUP dei lotti precedenti. Inoltre controllo che la
   * stazione appaltante del RUP che ha fatto accesso al WS sia uguale alla
   * stazione appaltante indicata nella gara.
   * 
   * @param result ResponseType
   * @param idAvGara IdAvGara
   * @param idAvGaraTemp IdAvGara (associato ai lotti precedenti)
   * @param credenzialiUtente Credenziale
   * @param cigLotto Cig del lotto
   * @param codiceGara Codice della gara (W9GARA.CODGARA)
   * @param codiceUffInt Codice Ufficio Intestatario (Stazione appaltante)
   * @param rupGara Rup della Gara (associato ai lotti precedenti)
   * @param rupGaraTemp Rup della Gara (associato al lotto i-esimo)
   * @param rupLotto Rup associato al lotto
   * @param logger Logger di log4j
   * @throws SQLException SQLException
   */
  public static void controlliGaraLottiRUP(ResponseType result, String idAvGara, String idAvGaraTemp, 
      Credenziale credenzialiUtente, String cigLotto, Long codiceGara, String codiceUffInt, String rupGara, 
      String rupGaraTemp, String rupLotto, Logger logger, SqlManager sqlManager) throws SQLException {
    
    if (StringUtils.isEmpty(idAvGara)) {
      if (StringUtils.isEmpty(idAvGaraTemp)) {
        // La gara associata al lotto non ha IDAVGARA valorizzato

        StringBuilder strLog = new StringBuilder(credenzialiUtente.getPrefissoLogger());
        strLog.append("Uno o piu' lotti appartengono a gare diverse."); 
        strLog.append(" Gara priva di IDAVGARA. Il lotto con CIG=");
        strLog.append(cigLotto);
        strLog.append("e' associato alla gara con CODGARA=");
        strLog.append(codiceGara);
        strLog.append(", ed ha il campo IDAVGARA non valorizzato");
        logger.error(strLog);

        result.setSuccess(false);
        result.setError("Il lotto e' associato ad una gara priva del Codice gara restituito dall'AVCP");
        
      } else {
        idAvGara = idAvGaraTemp;
      }
    } else {
      if (!idAvGara.equals(idAvGaraTemp)) {
        // Un lotto non ha lo stesso IDAVGARA del lotto/dei lotti precedenti:
        // il CIG specificato e' di un lotto di un altra gara.
        
        StringBuilder strLog = new StringBuilder(credenzialiUtente.getPrefissoLogger());
        strLog.append("Uno o piu' lotti appartengono a gare diverse. Il lotto con CIG=");
        strLog.append(cigLotto);
        strLog.append(" e' relativo alla gara con IDAVGARA=");
        strLog.append(idAvGaraTemp);
        strLog.append(", invece che alla gara con IDAVGARA=");
        strLog.append(idAvGara);
        strLog.append(". L'i-esimo lotto appartiene ad una gara diversa rispetto ai lotti precedenti.");
        logger.error(strLog);

        result.setSuccess(false);
        result.setError("Uno o piu' lotti appartengono a gare diverse.");
      }
    }

    //if (!"A".equals(credenzialiUtente.getAccount().getAbilitazioneStd())) {
    if (! UtilitySITAT.isUtenteAmministratore(credenzialiUtente)) {
    	
	    if (result.isSuccess()) {
	      if (StringUtils.isEmpty(rupGara)) {
	        if (StringUtils.isNotEmpty(rupGaraTemp)) {
	          rupGara = rupGaraTemp;
	        } else if (StringUtils.isNotEmpty(rupLotto)) {
	          rupGara = rupLotto; 
	        } else {
	          // Tutti i RUP sono null. Impossibile valorizzate la variabile
	          // che permette di stabilire se il RUP dei vari lotti coincide con
	          // il RUP della gara e/o del lotto precedente.
	          
	          StringBuilder strLog = new StringBuilder(credenzialiUtente.getPrefissoLogger());
	          strLog.append("Le credenziali fornite non coincidono con quelle del RUP indicato. Il lotto con CIG=");
	          strLog.append(cigLotto);
	          strLog.append(" non ha RUP. La gara ad esso associata, con IDAVGARA=");
	          strLog.append(idAvGaraTemp);
	          strLog.append(", non ha RUP. Non e' possibile verificare la correttezza sul RUP che ha fatto accesso al WS rispetto alla gara.");
	          logger.error(strLog);
	          
	          result.setSuccess(false);
	          result.setError("Le credenziali fornite non coincidono con quelle del RUP indicato.");
	        }
	      }

	      if (result.isSuccess()) {
	        if (StringUtils.isNotEmpty(rupLotto)) {
	          
	          if (!rupLotto.equalsIgnoreCase(rupGaraTemp)) {
	            // Il RUP del lotto e' diverso dal RUP indicato a livello di gara.
	            
	            StringBuilder strLog = new StringBuilder(credenzialiUtente.getPrefissoLogger());
	            strLog.append("Le credenziali fornite non coincidono con quelle del RUP indicato. Il lotto con CIG=");
	            strLog.append(cigLotto);
	            strLog.append(", associato alla gara con IDAVGARA=");
	            strLog.append(idAvGaraTemp);
	            strLog.append(", ha RUP diverso da quello della gara. ");
	            strLog.append("Non e' possibile verificare la correttezza sul RUP che ha fatto accesso al WS rispetto alla gara.");
	            logger.error(strLog);
	            
	            result.setSuccess(false);
	            result.setError("Le credenziali fornite non coincidono con quelle del RUP indicato.");
	          
	          } else if (StringUtils.isNotEmpty(rupGaraTemp)) {
	            if (!rupGaraTemp.equalsIgnoreCase(rupGara)) {
	
	              // Il RUP del lotto e' uguale al RUP indicato a livello di gara, ma questo e' 
	              // diverso da quello del lotto precedente/dei lotti precedenti.
	              
	              // E' un cosa poco probabile (sarebbe piu' probabile che l'i-esimo lotto sia
	              // associato ad un'altra gara, quindi l'errore sarebbe sull'IDAVGARA).
	              
	              StringBuilder strLog = new StringBuilder(credenzialiUtente.getPrefissoLogger());
	              strLog.append("Le credenziali fornite non coincidono con quelle del RUP indicato. Il lotto con CIG=");
	              strLog.append(cigLotto);
	              strLog.append(", associato alla gara con IDAVGARA=");
	              strLog.append(idAvGaraTemp);
	              strLog.append(", ha RUP uguale a quello della gara, ma quest'ultimo e' diverso da quello dei lotti precedenti.");
	              strLog.append(" Non e' possibile verificare la correttezza sul RUP che ha fatto accesso al WS rispetto alla gara.");
	              logger.error(strLog);
	              
	              result.setSuccess(false);
	              result.setError("Le credenziali fornite non coincidono con quelle del RUP indicato.");
	            }
	          }
	        } else {
	          // Il RUP del lotto non e' valorizzato e per questo non e' possibile 
	          // stabilire se e' lo stesso RUP che ha fatto accesso al WS.
	
	          StringBuilder strLog = new StringBuilder(credenzialiUtente.getPrefissoLogger());
	          strLog.append("Le credenziali fornite non coincidono con quelle del RUP indicato. Il lotto con CIG=");
	          strLog.append(cigLotto);
	          strLog.append(", associato alla gara con IDAVGARA=");
	          strLog.append(idAvGaraTemp);
	          strLog.append(", non ha RUP. Non e' possibile verificare la correttezza sul RUP che ha fatto accesso al WS rispetto alla gara.");
	          logger.error(strLog);
	          
	          result.setSuccess(false);
	          result.setError("Le credenziali fornite non coincidono con quelle del RUP indicato.");
	        }
	      }
	    }
    
	    if (result.isSuccess()) {
	      if (!credenzialiUtente.getStazioneAppaltante().getCodice().equals(codiceUffInt)) {
	        // Un lotto ha una stazione appaltante diversa da quella S.A.
	        // indicata nella testata del messaggio XML.
	        
	        StringBuilder strLog = new StringBuilder(credenzialiUtente.getPrefissoLogger());
	        strLog.append("Stazione appaltante non valida. Il lotto con CIG=");
	        strLog.append(cigLotto);
	        strLog.append(", relativo alla gara con IDAVGARA=");
	        strLog.append(idAvGaraTemp);
	        strLog.append(", e' associato ad una S.A. diversa da quella del RUP che ha fatto accesso al WS.");
	        logger.error(strLog);
	        
	        result.setSuccess(false);
	        result.setError("Uno o piu' lotti fanno riferimento ad una stazione appaltante non valida.");
	      }
	    }
    
	    if (result.isSuccess()) {
	      try {
	        String codiceTecnico = (String) sqlManager.getObject(
	            "select CODTEC from TECNI where SYSCON=? and CGENTEI=?",
	            new Object[]{new Long(credenzialiUtente.getAccount().getIdAccount()),
	                credenzialiUtente.getStazioneAppaltante().getCodice()});
	        
	        if (StringUtils.isEmpty(codiceTecnico) || 
	            (StringUtils.isNotEmpty(codiceTecnico) && !codiceTecnico.equalsIgnoreCase(rupLotto))) {
	
	          StringBuilder strLog = new StringBuilder(credenzialiUtente.getPrefissoLogger());
	          strLog.append("Le credenziali fornite non coincidono con quelle del RUP indicato. Il RUP del lotto con CIG=");
	          strLog.append(cigLotto);
	          strLog.append(", relativo alla gara con IDAVGARA=");
	          strLog.append(idAvGaraTemp);
	          strLog.append(", e' diverso dal RUP che ha fatto accesso al WS.");
	          logger.error(strLog);
	          
	          result.setSuccess(false);
	          result.setError("Le credenziali fornite non coincidono con quelle del RUP indicato.");
	
	        }
	
	      } catch (SQLException sqle) {
	        logger.error("Errore nell'estrazione del CODTEC ", sqle);
	        throw sqle;
	      }
	    }
    }
  }

  /**
   * Controllo dell'esistenza o meno di un lotto con CIG = <i>cigLotto</i>.
   * Se non esiste, l'attributo <i>success</i> dell'oggetto <i>result</i> viene settato a false,
   * con il relativo messaggio di errore nell'attributo <i>error</i>
   * 
   * @param geneManager GeneManager
   * @param result ResponseType
   * @param credenzialiUtente Credenziale
   * @param cigLotto Cig del lotto
   * @param logger Logger
   */
  public static void controlloCigLotto(GeneManager geneManager, ResponseType result,
      Credenziale credenzialiUtente, String cigLotto, Logger logger) {
    long numeroLotti = geneManager.countOccorrenze("W9LOTT", " UPPER(CIG)=? ",
    		new Object[]{ cigLotto.toUpperCase() });

    if (numeroLotti != 1) {
      // Caso poco probabile: il numero lotti con il CIG specificato e' diverso da 1.
 
      if (numeroLotti == 0) {
        StringBuilder strLog =
            new StringBuilder(credenzialiUtente.getPrefissoLogger());
        strLog.append(" Uno o piu' lotti indicati non sono presenti in archivio. Nella base dati non esiste il lotto con CIG='");
        strLog.append(cigLotto);
        strLog.append("'");
        logger.error(strLog);
        
        result.setSuccess(false);
        result.setError("Uno o piu' lotti indicati non sono presenti in archivio");
 
      } else {
        StringBuilder strLog =
            new StringBuilder(credenzialiUtente.getPrefissoLogger());
        strLog.append(" Nella base dati esistono piu' lotti con lo stesso CIG (CIG='");
        strLog.append(cigLotto);
        strLog.append("')");
        logger.error(strLog);
  
        result.setSuccess(false);
        result.setError("Nella base dati di destinazione esistono piu' lotti con lo stesso CIG");
        
      }
    }
  }

  
  /**
   * Controllo dell'esistenza o meno di un lotto con CIG = <i>cigLotto</i>.
   * Se non esiste, l'attributo <i>success</i> dell'oggetto <i>result</i> viene settato a false,
   * con il relativo messaggio di errore nell'attributo <i>error</i>
   * 
   * @param geneManager GeneManager
   * @param credenzialiUtente Credenziale
   * @param cigLotto Cig del lotto
   * @param logger Logger
   * @return Ritorna la descrizione dell'errore, null altrimenti
   */
  public static String controlloCigLotto(GeneManager geneManager,
      Credenziale credenzialiUtente, String cigLotto, Logger logger) {
    long numeroLotti = geneManager.countOccorrenze("W9LOTT", " UPPER(CIG)=? ",
    		new Object[]{ cigLotto.toUpperCase() });

    if (numeroLotti != 1) {
      // Caso poco probabile: il numero lotti con il CIG specificato e' diverso da 1.
 
      if (numeroLotti == 0) {
        StringBuilder strLog =
            new StringBuilder(credenzialiUtente.getPrefissoLogger());
        strLog.append(" Nella base dati non esiste il lotto con CIG=");
        strLog.append(cigLotto);
        logger.error(strLog);
 
        return "Nella base dati di destinazione non esiste il lotto con il CIG indicato";
      } else {
        StringBuilder strLog =
            new StringBuilder(credenzialiUtente.getPrefissoLogger());
        strLog.append(" Nella base dati esistono piu' lotti con lo stesso CIG (CIG=");
        strLog.append(cigLotto);
        logger.error(strLog);
        
        return "Nella base dati di destinazione esistono piu' lotti con il CIG indicato";
      }
    } else {
    	return null;
    }
  }
  
  /**
   * Gestione dell'archivio Impresa.
   * 
   * @param codFiscaleStazAppaltante 
   * @param impresaAggiudicataria ImpresaType
   * @param eseguiInsertImpresa
   * @param codImpresa
   * @param sqlManager SqlManager
   * @param geneManager GeneManager
   * @throws GestoreException GestoreException
   * @throws SQLException SQLException
   */
  public static HashMap<String, Object> gestioneImpresa(String codFiscaleStazAppaltante, ImpresaType impresaAggiudicataria,
		  boolean eseguiInsertImpresa, SqlManager sqlManager, GeneManager geneManager)
  		throws GestoreException, SQLException {
  	
  	HashMap<String, Object> result = new HashMap<String, Object>();
  	boolean impresaModificata = false;
  	
  	String codein = (String) sqlManager.getObject("select CODEIN from UFFINT where UPPER(CFEIN)=?",
        new Object[] { codFiscaleStazAppaltante.toUpperCase() });
  	
    String codImpresa = (String) sqlManager.getObject(
        "select CODIMP from IMPR where CGENIMP=? and UPPER(CFIMP)=?",
        new Object[] { codein, impresaAggiudicataria.getCFIMP().toUpperCase() });
    
    DataColumn uffint = new DataColumn("IMPR.CGENIMP", new JdbcParametro(
        JdbcParametro.TIPO_TESTO, codein));
    DataColumn cfImpresa = new DataColumn("IMPR.CFIMP", new JdbcParametro(
        JdbcParametro.TIPO_TESTO, impresaAggiudicataria.getCFIMP()));
    DataColumn nome = new DataColumn("IMPR.NOMEST", new JdbcParametro(
        JdbcParametro.TIPO_TESTO, impresaAggiudicataria.getNOMIMP()));
    DataColumn nomeImp = new DataColumn("IMPR.NOMIMP", new JdbcParametro(
        JdbcParametro.TIPO_TESTO, StringUtils.substring(impresaAggiudicataria.getNOMIMP(), 0, 60)));

    DataColumnContainer dccImpresa = new DataColumnContainer(new DataColumn[] {
        uffint, cfImpresa, nome, nomeImp } );

    // i campi facoltativi del tracciato vanno settati separatamente previo controllo
    if (impresaAggiudicataria.isSetGNAZIMP()) {
      dccImpresa.addColumn("IMPR.NAZIMP", JdbcParametro.TIPO_NUMERICO,
          Long.parseLong(impresaAggiudicataria.getGNAZIMP().toString()));
    }
    if (impresaAggiudicataria.isSetINDIMP()) {
      dccImpresa.addColumn("IMPR.INDIMP", JdbcParametro.TIPO_TESTO,
          impresaAggiudicataria.getINDIMP());
    }
    if (impresaAggiudicataria.isSetNCIIMP()) {
      dccImpresa.addColumn("IMPR.NCIIMP", JdbcParametro.TIPO_TESTO,
          impresaAggiudicataria.getNCIIMP());
    }
    if (impresaAggiudicataria.isSetCAPIMP()) {
      dccImpresa.addColumn("IMPR.CAPIMP", JdbcParametro.TIPO_TESTO,
          impresaAggiudicataria.getCAPIMP());
    }
    if (impresaAggiudicataria.isSetLOCIMP()) {
      dccImpresa.addColumn("IMPR.LOCIMP", JdbcParametro.TIPO_TESTO,
          impresaAggiudicataria.getLOCIMP());
    }
    if (impresaAggiudicataria.isSetCODCIT()) {
      dccImpresa.addColumn("IMPR.CODCIT", JdbcParametro.TIPO_TESTO,
          impresaAggiudicataria.getCODCIT());
    }
    if (impresaAggiudicataria.isSetTELIMP()) {
      dccImpresa.addColumn("IMPR.TELIMP", JdbcParametro.TIPO_TESTO,
          impresaAggiudicataria.getTELIMP());
    }
    if (impresaAggiudicataria.isSetFAXIMP()) {
      dccImpresa.addColumn("IMPR.FAXIMP", JdbcParametro.TIPO_TESTO,
          impresaAggiudicataria.getFAXIMP());
    }
    if (impresaAggiudicataria.isSetTELCEL()) {
      dccImpresa.addColumn("IMPR.TELCEL", JdbcParametro.TIPO_TESTO,
          impresaAggiudicataria.getTELCEL());
    }
    if (impresaAggiudicataria.isSetEMAIIP()) {
      dccImpresa.addColumn("IMPR.EMAIIP", JdbcParametro.TIPO_TESTO,
          impresaAggiudicataria.getEMAIIP());
    }
    if (impresaAggiudicataria.isSetEMAI2IP()) {
      dccImpresa.addColumn("IMPR.EMAI2IP", JdbcParametro.TIPO_TESTO,
          impresaAggiudicataria.getEMAI2IP());
    }
    if (impresaAggiudicataria.isSetINDWEB()) {
      dccImpresa.addColumn("IMPR.INDWEB", JdbcParametro.TIPO_TESTO,
          impresaAggiudicataria.getINDWEB());
    }
    if (impresaAggiudicataria.isSetNCCIAA()) {
      dccImpresa.addColumn("IMPR.NCCIAA", JdbcParametro.TIPO_TESTO,
          impresaAggiudicataria.getNCCIAA());
    }
    if (impresaAggiudicataria.isSetREGDIT()) {
      dccImpresa.addColumn("IMPR.REGDIT", JdbcParametro.TIPO_TESTO,
          impresaAggiudicataria.getREGDIT());
    }
    if (impresaAggiudicataria.isSetNINPS()) {
      dccImpresa.addColumn("IMPR.NINPS", JdbcParametro.TIPO_TESTO,
          impresaAggiudicataria.getNINPS());
    }
    if (impresaAggiudicataria.isSetNINAIL()) {
      dccImpresa.addColumn("IMPR.NINAIL", JdbcParametro.TIPO_TESTO,
          impresaAggiudicataria.getNINAIL());
    }
    if (impresaAggiudicataria.isSetALBTEC()) {
      dccImpresa.addColumn("IMPR.ALBTEC", JdbcParametro.TIPO_TESTO,
          impresaAggiudicataria.getALBTEC());
    }
    if (impresaAggiudicataria.isSetVINIMP()) {
      dccImpresa.addColumn("IMPR.TIPIMP", JdbcParametro.TIPO_NUMERICO,
          new Long(impresaAggiudicataria.getVINIMP().toString()));
    }
    if (impresaAggiudicataria.isSetGNATGIUI() && StringUtils.isNotEmpty(impresaAggiudicataria.getGNATGIUI().toString())) {
      dccImpresa.addColumn("IMPR.NATGIUI", JdbcParametro.TIPO_NUMERICO,
          new Long(impresaAggiudicataria.getGNATGIUI().toString()));
    }
    if (impresaAggiudicataria.isSetPIVIMP()) {
      dccImpresa.addColumn("IMPR.PIVIMP", JdbcParametro.TIPO_TESTO,
          impresaAggiudicataria.getPIVIMP());
    }

    if (codImpresa == null) {
      // se non esiste, si inserisce previa creazione della chiave
    	synchronized (codein) {
	      codImpresa = geneManager.calcolaCodificaAutomatica("IMPR", "CODIMP");
	      eseguiInsertImpresa = true;

	      dccImpresa.addColumn("IMPR.CODIMP",
	          new JdbcParametro(JdbcParametro.TIPO_TESTO, codImpresa));
    
	      dccImpresa.insert("IMPR", sqlManager);
	      impresaModificata = true;
	    }
    } else {

    	dccImpresa.addColumn("IMPR.CODIMP", JdbcParametro.TIPO_TESTO, codImpresa);
    	
    	DataColumnContainer dccImpresaDB = new DataColumnContainer(sqlManager, "IMPR",
    			"select CODIMP, CGENIMP, CFIMP, NOMEST, NOMIMP, NAZIMP, INDIMP, NCIIMP, CAPIMP," +
    						" LOCIMP, CODCIT, TELIMP, FAXIMP, TELCEL, EMAIIP, EMAI2IP, INDWEB, NCCIAA," +
    						" REGDIT, NINPS, NINAIL, ALBTEC, PIVIMP, NATGIUI, TIPIMP " +
    			      " from IMPR where CODIMP=? and CGENIMP=?", new Object[] {codImpresa, codein});
    	
    	DataColumn codiceImpresa = dccImpresaDB.getColumn("IMPR.CODIMP");
    	codiceImpresa.setChiave(true);
    	codiceImpresa.setOriginalValue(codiceImpresa.getValue());

      Iterator<Entry<String, DataColumn>> iterDB = dccImpresaDB.getColonne().entrySet().iterator();
      while (iterDB.hasNext()) {
      	Entry<String, DataColumn> entry = iterDB.next(); 
        String nomeCampo = entry.getKey();
        if (dccImpresa.isColumn(nomeCampo)) {
        	dccImpresaDB.setValue(nomeCampo, dccImpresa.getColumn(nomeCampo).getValue());
        }
      }
    	
    	/*if (StringUtils.isNotEmpty(impresaAggiudicataria.getNOMIMP())) {
    		dccImpresaDB.setValue("IMPR.NOMIMP", StringUtils.substring(impresaAggiudicataria.getNOMIMP(), 0, 60));
    		dccImpresaDB.setValue("IMPR.NOMEST", impresaAggiudicataria.getNOMIMP());
    	}
    	if (impresaAggiudicataria.isSetGNAZIMP()) {
        dccImpresaDB.setValue(("IMPR.NAZIMP"),
            Long.parseLong(impresaAggiudicataria.getGNAZIMP().toString()));
      }
      if (impresaAggiudicataria.isSetINDIMP()) {
        dccImpresaDB.setValue("IMPR.INDIMP", impresaAggiudicataria.getINDIMP());
      }
      if (impresaAggiudicataria.isSetNCIIMP()) {
        dccImpresaDB.setValue("IMPR.NCIIMP", impresaAggiudicataria.getNCIIMP());
      }
      if (impresaAggiudicataria.isSetCAPIMP()) {
        dccImpresaDB.setValue("IMPR.CAPIMP", impresaAggiudicataria.getCAPIMP());
      }
      if (impresaAggiudicataria.isSetLOCIMP()) {
        dccImpresaDB.setValue("IMPR.LOCIMP", impresaAggiudicataria.getLOCIMP());
      }
      if (impresaAggiudicataria.isSetCODCIT()) {
        dccImpresaDB.setValue("IMPR.CODCIT", impresaAggiudicataria.getCODCIT());
      }
      if (impresaAggiudicataria.isSetTELIMP()) {
        dccImpresaDB.setValue("IMPR.TELIMP", impresaAggiudicataria.getTELIMP());
      }
      if (impresaAggiudicataria.isSetFAXIMP()) {
        dccImpresaDB.setValue("IMPR.FAXIMP", impresaAggiudicataria.getFAXIMP());
      }
      if (impresaAggiudicataria.isSetTELCEL()) {
        dccImpresaDB.setValue("IMPR.TELCEL", impresaAggiudicataria.getTELCEL());
      }
      if (impresaAggiudicataria.isSetEMAIIP()) {
        dccImpresaDB.setValue("IMPR.EMAIIP", impresaAggiudicataria.getEMAIIP());
      }
      if (impresaAggiudicataria.isSetEMAI2IP()) {
        dccImpresaDB.setValue("IMPR.EMAI2IP", impresaAggiudicataria.getEMAI2IP());
      }
      if (impresaAggiudicataria.isSetINDWEB()) {
        dccImpresaDB.setValue("IMPR.INDWEB", impresaAggiudicataria.getINDWEB());
      }
      if (impresaAggiudicataria.isSetNCCIAA()) {
        dccImpresaDB.setValue("IMPR.NCCIAA", impresaAggiudicataria.getNCCIAA());
      }
      if (impresaAggiudicataria.isSetREGDIT()) {
        dccImpresaDB.setValue("IMPR.REGDIT", impresaAggiudicataria.getREGDIT());
      }
      if (impresaAggiudicataria.isSetNINPS()) {
        dccImpresaDB.setValue("IMPR.NINPS", impresaAggiudicataria.getNINPS());
      }
      if (impresaAggiudicataria.isSetNINAIL()) {
        dccImpresaDB.setValue("IMPR.NINAIL", impresaAggiudicataria.getNINAIL());
      }
      if (impresaAggiudicataria.isSetALBTEC()) {
        dccImpresaDB.setValue("IMPR.ALBTEC", impresaAggiudicataria.getNCCIAA());
      }
      if (impresaAggiudicataria.isSetPIVIMP()) {
        dccImpresaDB.setValue("IMPR.PIVIMP", impresaAggiudicataria.getPIVIMP());
      }
      if (impresaAggiudicataria.isSetGNATGIUI()) {
        dccImpresaDB.setValue("IMPR.NATGIUI", 
        		Long.parseLong(impresaAggiudicataria.getGNATGIUI().toString()));
      }
      if (impresaAggiudicataria.isSetVINIMP()) {
        dccImpresaDB.setValue("IMPR.TIPIMP", 
        		Long.parseLong(impresaAggiudicataria.getVINIMP().toString()));
      }*/
      
      if (dccImpresaDB.isModifiedTable("IMPR")) {
      	dccImpresaDB.update("IMPR", sqlManager);
      	
      	impresaModificata = true;
      }
    }
    
    result.put("CODIMP", codImpresa);
    result.put("MOD", new Boolean(impresaModificata));
    
    return result;
  }
  
  /**
   * Gestione dei legali rappresentanti.
   * 
   * @param codFiscaleStazAppaltante
   * @param impresa ImpresaType
   * @param eseguiInsertImpresa
   * @param codImpresa Codice impresa
   * @throws SQLException SQLException
   * @throws GestoreException GestoreException
   */
  public static void gestioneLegaliRappresentanti(String codFiscaleStazAppaltante,
      ImpresaType impresa, boolean eseguiInsertImpresa, String codImpresa,
      SqlManager sqlManager, GenChiaviManager genChiaviManager) throws SQLException, GestoreException {
    
  	String codein = (String) sqlManager.getObject("select CODEIN from UFFINT where UPPER(CFEIN)=?",
        new Object[] { codFiscaleStazAppaltante.toUpperCase() });
  	
    // Numero di legali rappresentanti dell'impresa
    Long numeroLegaliRappresentanti = (Long) sqlManager.getObject(
        "select count(IMPLEG.CODLEG) from IMPLEG, IMPR where IMPR.CODIMP=IMPLEG.CODIMP2 and IMPR.CODIMP=? and IMPR.CGENIMP=? ",
        new Object[] { codImpresa, codein });
   
    if (eseguiInsertImpresa || (numeroLegaliRappresentanti != null && numeroLegaliRappresentanti.longValue() == 0)) {
      if (impresa.getListaLegaliRappresentantiArray() != null
          && impresa.getListaLegaliRappresentantiArray().length > 0) {
        
        LegaleRappresentanteType legaleRappresentante = impresa.getListaLegaliRappresentantiArray(0);
                                      
        String pkTecnicoImpresa = (String) sqlManager.getObject(
            "select codtim from teim where cgentim=? and upper(cftim)=? and codtim=?",
            new Object[] { codein, legaleRappresentante.getCFTIM().toUpperCase(), codImpresa });
 
        if (StringUtils.isEmpty(pkTecnicoImpresa)) {
        	synchronized(codImpresa) {
            pkTecnicoImpresa = new String(codImpresa);
   
            DataColumn codiceTecnicoImpresa = new DataColumn("TEIM.CODTIM",
                new JdbcParametro(JdbcParametro.TIPO_TESTO, pkTecnicoImpresa));
            DataColumn uffintTEIM = new DataColumn("TEIM.CGENTIM", new JdbcParametro(
                JdbcParametro.TIPO_TESTO, codein.toUpperCase()));
            
            String denominazioneTecnico = null;
            if (legaleRappresentante.isSetCOGTIM()) {
              denominazioneTecnico = legaleRappresentante.getCOGTIM();
            }

            if (legaleRappresentante.isSetNOMETIM()) {
              if (StringUtils.isNotEmpty(denominazioneTecnico)) {
              	denominazioneTecnico = denominazioneTecnico.concat(" " + legaleRappresentante.getNOMETIM());
              } else {
              	denominazioneTecnico = legaleRappresentante.getNOMETIM();
              }
            }
            DataColumn cognomeTEIM = new DataColumn("TEIM.COGTIM", new JdbcParametro(
                JdbcParametro.TIPO_TESTO, legaleRappresentante.getCOGTIM()));
            DataColumn nomeTEIM = new DataColumn("TEIM.NOMETIM", new JdbcParametro(
                JdbcParametro.TIPO_TESTO, legaleRappresentante.getNOMETIM()));
            DataColumn denominazioneTEIM = new DataColumn("TEIM.NOMTIM",
                new JdbcParametro(JdbcParametro.TIPO_TESTO, denominazioneTecnico));
            DataColumn codiceFiscaleTecImp = new DataColumn("TEIM.CFTIM",
                new JdbcParametro(JdbcParametro.TIPO_TESTO, legaleRappresentante.getCFTIM()));
    
            DataColumnContainer dcc = new DataColumnContainer(new DataColumn[] {
                codiceTecnicoImpresa, uffintTEIM, cognomeTEIM, nomeTEIM,
                denominazioneTEIM, codiceFiscaleTecImp });

            dcc.insert("TEIM", sqlManager);

            int numProgIMPLEG = genChiaviManager.getNextId("IMPLEG");
            sqlManager.update("insert into IMPLEG (ID, CODIMP2, CODLEG, NOMLEG) values (?, ?, ?, ?)",
                new Object[]{ new Long(numProgIMPLEG), codImpresa, pkTecnicoImpresa, denominazioneTecnico });
          }
        }
      }
    } else {
    	if (impresa.getListaLegaliRappresentantiArray() != null
          && impresa.getListaLegaliRappresentantiArray().length > 0) {
			LegaleRappresentanteType legaleRappresentante = impresa.getListaLegaliRappresentantiArray(0);
			
			String sqlUpdateTEIM = "update TEIM set CFTIM=?, COGTIM=?, NOMETIM=?, NOMTIM=? where CODTIM=?";
			List<Object> sqlParameter = new ArrayList<Object>();
			sqlParameter.add(legaleRappresentante.getCFTIM());
			
			String denominazioneTecnico = null;
			if (legaleRappresentante.isSetCOGTIM()) {
			  denominazioneTecnico = legaleRappresentante.getCOGTIM();
			  sqlParameter.add(legaleRappresentante.getCOGTIM());
			} else {
				sqlUpdateTEIM = StringUtils.replace(sqlUpdateTEIM, "COGTIM=?", "COGTIM=null");
			}

			if (legaleRappresentante.isSetNOMETIM()) {
				sqlParameter.add(legaleRappresentante.getNOMETIM());
			  
				if (StringUtils.isNotEmpty(denominazioneTecnico)) {
				denominazioneTecnico = denominazioneTecnico.concat(" " + legaleRappresentante.getNOMETIM());
			  } else {
				denominazioneTecnico = legaleRappresentante.getNOMETIM();
			  }
			} else {
				sqlUpdateTEIM = StringUtils.replace(sqlUpdateTEIM, "NOMETIM=?", "NOMETIM=null");
			}
			
			if (StringUtils.isNotEmpty(denominazioneTecnico)) {
				sqlParameter.add(legaleRappresentante.getNOMETIM());
			} else {
				sqlUpdateTEIM = StringUtils.replace(sqlUpdateTEIM, "=NOMTIM=?", "NOMTIM=null");
			}
			
			sqlParameter.add(codImpresa);
			sqlManager.update(sqlUpdateTEIM, sqlParameter.toArray());
			
			if (StringUtils.isNotEmpty(denominazioneTecnico)) {
				sqlManager.update("update IMPLEG set NOMLEG=? where CODIMP2=? and CODLEG=?",
						new Object[] { denominazioneTecnico, codImpresa, codImpresa });
			} else {
				sqlManager.update("update IMPLEG set NOMLEG=null where CODIMP2=? and CODLEG=?",
						new Object[] { codImpresa, codImpresa });
			}
    	} else {
    		// Cancellazione del legale rappresentante e dell'associazione impresa e legale rappresentante
    		sqlManager.update("delete from TEIM where CODTIM=?", new Object[] { codImpresa });
    		sqlManager.update("delete from IMPLEG where CODIMP2=? and CODLEG=?", new Object[] { codImpresa, codImpresa });
    	}
    }
  }

  /**
   * Lettura della property it.eldasoft.ws.attivaNoteAvvisi per determinare se attivare o meno 
   * l'inserimento nella tabella G_NOTEAVVISI di messaggi relativi all'aggiornamento della base dati.
   * 
   * @param sqlManager
   * @return Ritorna true se la property it.eldasoft.ws.attivaNoteAvvisi=1, false altrimenti
   * @throws SQLException
   */
  public static boolean isScritturaNoteAvvisiAttiva() {
  	boolean result = false;
  	
  	String attivaNoteAvvisi = ConfigManager.getValore("it.eldasoft.ws.attivaNoteAvvisi");
  	if ("1".equals(attivaNoteAvvisi)) { 
  		result = true;
  	}
  	return result;
  }

  /**
   * Inserimento di un avviso nella tabella G_NOTEAVVISI.
   * 
   * @param entita
   * @param key01
   * @param key02
   * @param key03
   * @param key04
   * @param key05
   * @param syscon
   * @param titoloNota
   * @param testoNota
   * @param sqlManager
   * @return
   * @throws SQLException
   */
  public static boolean insertNoteAvvisi(String entita, String key01, String key02, String key03,
  		String key04, String key05, String titoloNota, String testoNota,
  		SqlManager sqlManager) throws SQLException {
  	
  	boolean result = true;
  	
  	if (StringUtils.isNotEmpty(entita)) {

  		if (StringUtils.isEmpty(testoNota)) {
  			testoNota = null;
  		}
  		
  		if (result) {
		  	synchronized (entita) {
		  		Long maxNoteCod = (Long) sqlManager.getObject("select max(NOTECOD) from G_NOTEAVVISI", null);
		  		
		  		if (maxNoteCod == null) {
		  			maxNoteCod = new Long(0);
		  		}
		  		
		  		int recordInseriti = sqlManager.update(
		  				"INSERT INTO G_NOTEAVVISI (NOTECOD, NOTEPRG, NOTEENT, NOTEKEY1, NOTEKEY2, NOTEKEY3, NOTEKEY4, NOTEKEY5," +
		  				 "AUTORENOTA, STATONOTA, TIPONOTA, DATANOTA, TITOLONOTA, TESTONOTA) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?) ",
		  				 new Object[] { new Long(maxNoteCod.longValue()+1), "W9", entita, key01, key02, key03, key04, key05,
		  						new Long(50), new Long(1), new Long(3), new Timestamp(new GregorianCalendar().getTimeInMillis()),
		  						titoloNota, testoNota } );
		
		  		result = recordInseriti == 1;
		  	}
  		} else {
  			// Alla funzione non sono stati passati tutti i valori necessari delle chiavi
  			logger.error("Alla funzione non sono stati passati tutti i valori necessari delle chiavi");
  			result = false;
  		}
  	} else {
  		// Non e' stato indicato il nome dell'entita
  		logger.error("Indicare una entita' per inserire record nella G_NOTEAVVISI");
  		result = false;
  	}

  	return result;
  }
  
  /**
   * @param result
   * @param codFiscaleStazAppaltante
   * @param credenzialiUtente
   * @param codiceGara
   * @param codiceLotto
   * @param esisteFase
   * @param arrayIncarichiProfessionali
   * @param sezione
   * @param SqlManager SqlManager
   * @param GeneManager GeneManager
   * @param RupManager RupManager
   * @param isAAQ is Adesione Accordo Quadro
   * @param isE1 is lotto escluso
   * @param isE2 is lotto con importo >= 40000 e ExSottoSoglia='2' o null 
   * @throws SQLException
   * @throws GestoreException
   */
  public static boolean gestioneIncarichiProfessionali(ResponseType result,
      String codFiscaleStazAppaltante, Credenziale credenzialiUtente, int fase,
      Long codiceGara, Long codiceLotto, boolean esisteFase,
      IncaricoProfessionaleType[] arrayIncarichiProfessionali,
      SqlManager sqlManager, GeneManager geneManager, RupManager rupManager,
      boolean isAAQ, boolean isE1, boolean isS2, boolean isExSottosoglia) throws SQLException, GestoreException {
  	
  	boolean incarichiProfessionaliModificati = false;
  	
    if (arrayIncarichiProfessionali != null && arrayIncarichiProfessionali.length > 0) {
    
    	if (!UtilitySITAT.isScritturaNoteAvvisiAttiva()) {
    		String sezione = null;
    		
    		if (esisteFase) {
          switch(fase) {
          case CostantiWSW9.AGGIUDICAZIONE_SOPRA_SOGLIA:
          case CostantiWSW9.FASE_SEMPLIFICATA_AGGIUDICAZIONE:
          case CostantiWSW9.ADESIONE_ACCORDO_QUADRO:
            sqlManager.update("delete from W9INCA where CODGARA=? and CODLOTT=? and NUM=1 and SEZIONE in ('PA', 'RS', 'RA', 'RE', 'RQ')",
                new Object[]{ codiceGara, codiceLotto });
            break;

          case CostantiWSW9.INIZIO_CONTRATTO_SOPRA_SOGLIA:
          case CostantiWSW9.FASE_SEMPLIFICATA_INIZIO_CONTRATTO:
            sqlManager.update("delete from W9INCA where CODGARA=? and CODLOTT=? and NUM=1 and SEZIONE in ('IN')",
                new Object[]{ codiceGara, codiceLotto });
            break;
            
          case CostantiWSW9.COLLAUDO_CONTRATTO:
            sqlManager.update("delete from W9INCA where CODGARA=? and CODLOTT=? and NUM=1 and SEZIONE in ('CO')",
                new Object[]{ codiceGara, codiceLotto });
            sezione = "CO";
            break;
          }
        }

        for (int numIncarico = 0; numIncarico < arrayIncarichiProfessionali.length; numIncarico++) {
          IncaricoProfessionaleType incaricoProfessionale = arrayIncarichiProfessionali[numIncarico];
          
          switch(fase) {
          case CostantiWSW9.AGGIUDICAZIONE_SOPRA_SOGLIA:
          case CostantiWSW9.FASE_SEMPLIFICATA_AGGIUDICAZIONE:
          case CostantiWSW9.ADESIONE_ACCORDO_QUADRO:
          	Long w3IdRuolo = new Long(incaricoProfessionale.getW3IDRUOLO().toString());
          	sezione = "PA";
            if (isAAQ) {
            	sezione = "RQ";
            } else if (isE1) {
              sezione = "RE";
            } else if (!isS2 | isExSottosoglia) {
              sezione = "RS";
            } else if (w3IdRuolo.longValue() >= 5) {
              sezione = "RA";
            }
            break;

          case CostantiWSW9.INIZIO_CONTRATTO_SOPRA_SOGLIA:
          case CostantiWSW9.FASE_SEMPLIFICATA_INIZIO_CONTRATTO:
          	sezione = "IN";
            break;
            
          case CostantiWSW9.COLLAUDO_CONTRATTO:
            sezione = "CO";
            break;
          }

          DataColumn codGaraInca = new DataColumn("W9INCA.CODGARA", 
              new JdbcParametro(JdbcParametro.TIPO_NUMERICO, codiceGara));
          DataColumn codLottInca = new DataColumn("W9INCA.CODLOTT", 
              new JdbcParametro(JdbcParametro.TIPO_NUMERICO, codiceLotto));
          DataColumn numAppaInca = new DataColumn("W9INCA.NUM", 
              new JdbcParametro(JdbcParametro.TIPO_NUMERICO, new Long(1)));
          DataColumn numInca = new DataColumn("W9INCA.NUM_INCA", 
              new JdbcParametro(JdbcParametro.TIPO_NUMERICO, new Long(numIncarico+1)));
          DataColumn sezioneInca = new DataColumn("W9INCA.SEZIONE", 
                  new JdbcParametro(JdbcParametro.TIPO_TESTO, sezione));
          DataColumn idRuoloInca = new DataColumn("W9INCA.ID_RUOLO",
              new JdbcParametro(JdbcParametro.TIPO_NUMERICO,
              Long.parseLong(incaricoProfessionale.getW3IDRUOLO().toString())));
          
          DataColumnContainer dccInca = new DataColumnContainer(new DataColumn[] {
              codGaraInca, codLottInca, numAppaInca, numInca, sezioneInca, idRuoloInca} );
          
          if (incaricoProfessionale.isSetW3CIGPROG()) {
            dccInca.addColumn("W9INCA.CIG_PROG_ESTERNA", JdbcParametro.TIPO_TESTO, 
                incaricoProfessionale.getW3CIGPROG());
          }
          if (incaricoProfessionale.isSetW3DATAAFF()) {
            dccInca.addColumn("W9INCA.DATA_AFF_PROG_ESTERNA", JdbcParametro.TIPO_DATA, 
                incaricoProfessionale.getW3DATAAFF().getTime());
          }
          if (incaricoProfessionale.isSetW3DATACON()) {
            dccInca.addColumn("W9INCA.DATA_CONS_PROG_ESTERNA", JdbcParametro.TIPO_DATA, 
                incaricoProfessionale.getW3DATACON().getTime());
          }
      
          // Gestione del tecnico come incaricato professionale
          String codiceChiaveTecnico = rupManager.getIncaricoProfessionale(
          		credenzialiUtente.getStazioneAppaltante().getCodice(), incaricoProfessionale);
   
          if (StringUtils.isNotEmpty(codiceChiaveTecnico)) {
          	dccInca.addColumn("W9INCA.CODTEC", JdbcParametro.TIPO_TESTO, codiceChiaveTecnico);
          } else {
          	throw new GestoreException("Errore nel determinare il codice del tecnico incaricato", null);
          }

          if (result.isSuccess()) {
          	// Inserimento record W9INCA
          	dccInca.insert("W9INCA", sqlManager);
        	} else {
        	  throw new GestoreException("Errore nell'inserimento del tecnico indicato", null);
        	}
        }
    	} else {
    		
    		String filtroSezione = "";
    		String sezione = null;
    		
    		int numeroIncarichiEsistenti = 0;
    		int numeroIncarichiInseriti = 0;
    		    		
    		if (esisteFase) {
          switch(fase) {
          case CostantiWSW9.AGGIUDICAZIONE_SOPRA_SOGLIA:
          case CostantiWSW9.FASE_SEMPLIFICATA_AGGIUDICAZIONE:
          case CostantiWSW9.ADESIONE_ACCORDO_QUADRO:
          	filtroSezione = " and SEZIONE in ('PA', 'RS', 'RA', 'RE', 'RQ') ";
            break;
          case CostantiWSW9.INIZIO_CONTRATTO_SOPRA_SOGLIA:
          case CostantiWSW9.FASE_SEMPLIFICATA_INIZIO_CONTRATTO:
          	filtroSezione = " and SEZIONE = 'IN' ";
            break;
           
          case CostantiWSW9.COLLAUDO_CONTRATTO:
          	filtroSezione = " and SEZIONE = 'CO' ";
            break;
          }

          numeroIncarichiEsistenti = sqlManager.update(
          		"UPDATE W9INCA SET NUM=-1 WHERE CODGARA=? and CODLOTT=? and NUM=1" + filtroSezione,
              new Object[]{ codiceGara, codiceLotto });
    		}
    		
        for (int numIncarico = 0; numIncarico < arrayIncarichiProfessionali.length; numIncarico++) {
          IncaricoProfessionaleType incaricoProfessionale = arrayIncarichiProfessionali[numIncarico];
          Long w3IdRuolo = new Long(incaricoProfessionale.getW3IDRUOLO().toString());
          switch(fase) {
          case CostantiWSW9.AGGIUDICAZIONE_SOPRA_SOGLIA:
          case CostantiWSW9.FASE_SEMPLIFICATA_AGGIUDICAZIONE:
          case CostantiWSW9.ADESIONE_ACCORDO_QUADRO:
          	sezione = "PA";
            if (isAAQ) {
            	sezione = "RQ";
            } else if (isE1) {
              sezione = "RE";
            } else if (!isS2 | isExSottosoglia) {
              sezione = "RS";
            } else if (w3IdRuolo.longValue() >= 5) {
            	sezione = "RA";
            }
            break;

          case CostantiWSW9.INIZIO_CONTRATTO_SOPRA_SOGLIA:
          case CostantiWSW9.FASE_SEMPLIFICATA_INIZIO_CONTRATTO:
          	sezione = "IN";
            break;
            
          case CostantiWSW9.COLLAUDO_CONTRATTO:
            sezione = "CO";
            break;
          }
          
          DataColumn codGaraInca = new DataColumn("W9INCA.CODGARA", 
              new JdbcParametro(JdbcParametro.TIPO_NUMERICO, codiceGara));
          DataColumn codLottInca = new DataColumn("W9INCA.CODLOTT", 
              new JdbcParametro(JdbcParametro.TIPO_NUMERICO, codiceLotto));
          DataColumn numAppaInca = new DataColumn("W9INCA.NUM", 
              new JdbcParametro(JdbcParametro.TIPO_NUMERICO, new Long(1)));
          DataColumn numInca = new DataColumn("W9INCA.NUM_INCA", 
              new JdbcParametro(JdbcParametro.TIPO_NUMERICO, new Long(numIncarico+1)));
          DataColumn sezioneInca = new DataColumn("W9INCA.SEZIONE", 
                  new JdbcParametro(JdbcParametro.TIPO_TESTO, sezione));
          DataColumn idRuoloInca = new DataColumn("W9INCA.ID_RUOLO",
              new JdbcParametro(JdbcParametro.TIPO_NUMERICO,
              Long.parseLong(incaricoProfessionale.getW3IDRUOLO().toString())));
          
          DataColumnContainer dccInca = new DataColumnContainer(new DataColumn[] {
              codGaraInca, codLottInca, numAppaInca, numInca, sezioneInca, idRuoloInca} );
          
          if (incaricoProfessionale.isSetW3CIGPROG()) {
            dccInca.addColumn("W9INCA.CIG_PROG_ESTERNA", JdbcParametro.TIPO_TESTO, 
                incaricoProfessionale.getW3CIGPROG());
          }
          if (incaricoProfessionale.isSetW3DATAAFF()) {
            dccInca.addColumn("W9INCA.DATA_AFF_PROG_ESTERNA", JdbcParametro.TIPO_DATA, 
                incaricoProfessionale.getW3DATAAFF().getTime());
          }
          if (incaricoProfessionale.isSetW3DATACON()) {
            dccInca.addColumn("W9INCA.DATA_CONS_PROG_ESTERNA", JdbcParametro.TIPO_DATA, 
                incaricoProfessionale.getW3DATACON().getTime());
          }
      
          // Gestione del tecnico come incaricato professionale
          String codiceChiaveTecnico = rupManager.getIncaricoProfessionale(
          		credenzialiUtente.getStazioneAppaltante().getCodice(), incaricoProfessionale);
   
          if (StringUtils.isNotEmpty(codiceChiaveTecnico)) {
          	dccInca.addColumn("W9INCA.CODTEC", JdbcParametro.TIPO_TESTO, codiceChiaveTecnico);
          } else {
          	throw new GestoreException("Errore nel determinare il codice del tecnico incaricato", null);
          }

          if (result.isSuccess()) {
          	// Inserimento record W9INCA
          	dccInca.insert("W9INCA", sqlManager);
          	numeroIncarichiInseriti++;
        	} else {
        	  throw new GestoreException("Errore nell'inserimento del tecnico indicato", null);
        	}
        }

        // Ora devo capire se c'è stato una qualche modifica dei record in W9INCA e TECNI.
        if (numeroIncarichiEsistenti == numeroIncarichiInseriti) {
        	// Devo cercare di capire se almeno un incarico professionale e' cambiato
        	
        	for (int numIncarico = 0; numIncarico < arrayIncarichiProfessionali.length && !incarichiProfessionaliModificati; numIncarico++) {
      			IncaricoProfessionaleType incaricoProfessionale = arrayIncarichiProfessionali[numIncarico];
        	
      			String sqlIncaricoProf =
      				"SELECT W9INCA.ID_RUOLO, W9INCA.CIG_PROG_ESTERNA," +
      							" W9INCA.DATA_AFF_PROG_ESTERNA, W9INCA.DATA_CONS_PROG_ESTERNA, W9INCA.CODTEC, " +
										" TECNI.CODTEC, TECNI.CGENTEI, TECNI.CFTEC, TECNI.CITTEC, TECNI.COGTEI, " +
										" TECNI.NOMETEI, TECNI.NOMTEC, TECNI.INDTEC, TECNI.NCITEC, TECNI.LOCTEC, " +
										" TECNI.CAPTEC, TECNI.TELTEC, TECNI.FAXTEC, TECNI.EMATEC, TECNI.PROTEC" +
										"  from W9INCA, TECNI, UFFINT " +
										" where W9INCA.CODGARA = ? " +
										  " and W9INCA.CODLOTT = ? " +
										  " and W9INCA.NUM = ? " +
										  " and W9INCA.CODTEC = TECNI.CODTEC " +
										  " and UPPER(TECNI.CFTEC) = ? " +
										  " and TECNI.CGENTEI = UFFINT.CODEIN " +
										  " and UFFINT.CFEIN= ? " + filtroSezione;
      			
      			DataColumnContainer dccIncaricoProfOrig = new DataColumnContainer(sqlManager, "W9INCA", sqlIncaricoProf, 
      					new Object[]{ codiceGara, codiceLotto, new Long(-1),
      					incaricoProfessionale.getCFTEC1().toUpperCase(), codFiscaleStazAppaltante } );

      			DataColumnContainer dccIncaricoProf = new DataColumnContainer(sqlManager, "W9INCA", sqlIncaricoProf,
      					new Object[]{ codiceGara, codiceLotto, new Long(1),
      					incaricoProfessionale.getCFTEC1().toUpperCase(), codFiscaleStazAppaltante } );

      			if (dccIncaricoProfOrig.getColonne().isEmpty()) {
      				// prima dell'aggiornamento non esisteva un incarico professionale
      				// uguale a quello appena inserito --> qualcosa e' cambiato fra
      				//  gli incarichi professionali
      				incarichiProfessionaliModificati = true;
      			} else {
      				// Verifica se dccIncaricoProfOrig e dccIncaricoProf hanno colonne con valori diversi.
      				Iterator< ? > iter = dccIncaricoProfOrig.getColonne().keySet().iterator();
      				
      				while (iter.hasNext() && !incarichiProfessionaliModificati) {
      					String key = (String) iter.next();
      					
      					DataColumn objOrig = dccIncaricoProfOrig.getColumn(key);
      					DataColumn obj = dccIncaricoProf.getColumn(key);
      					if (objOrig.getValue().getValue() == null && obj.getValue().getValue() == null) {
      						incarichiProfessionaliModificati = false;
      					} else if (objOrig.getValue().getValue() == null && obj.getValue().getValue() != null) {
      						incarichiProfessionaliModificati = true;
      					} else if (objOrig.getValue().getValue() != null && obj.getValue().getValue() == null) { 
      						incarichiProfessionaliModificati = true;
      					} else if (!objOrig.getValue().getValue().equals(obj.getValue().getValue())) {
      						incarichiProfessionaliModificati = true;
      					}
      				}
      			}
        	}
        } else {
        	// Il numero di record iniziale e' diverso da quello finale, quindi ci sono state
        	// delle variazioni. Posso dire che gli incarichi professionali sono stati modificati.
        	incarichiProfessionaliModificati = true;
        }

    		// Ora cancello i record con NUM=-1
        sqlManager.update("DELETE from W9INCA where CODGARA=? and CODLOTT=? and NUM=-1" + filtroSezione,
            new Object[]{ codiceGara, codiceLotto });
    	}
    } else {
    	// Se l'array arrayIncarichiProfessionali e' arrivato vuoto, allora non si cancellano
    	// i record esistenti nella tabella W9INCA filtrati per sezione
    	
    	/*String filtroSezione = "";
  		    		
			switch(fase) {
				case CostantiWSW9.AGGIUDICAZIONE_SOPRA_SOGLIA:
				case CostantiWSW9.FASE_SEMPLIFICATA_AGGIUDICAZIONE:
				case CostantiWSW9.ADESIONE_ACCORDO_QUADRO:
					filtroSezione = " and SEZIONE in ('PA', 'RS', 'RA', 'RE', 'RQ') ";
				break;
				case CostantiWSW9.INIZIO_CONTRATTO_SOPRA_SOGLIA:
				case CostantiWSW9.FASE_SEMPLIFICATA_INIZIO_CONTRATTO:
					filtroSezione = " and SEZIONE = 'IN' ";
				break;
		
				case CostantiWSW9.COLLAUDO_CONTRATTO:
					filtroSezione = " and SEZIONE = 'CO' ";
				break;
			}
    	
    	int numIncarichiProfEliminati = sqlManager.update(
    			"delete from W9INCA where CODGARA=? and CODLOTT=? and NUM=1 " + filtroSezione,
    					new Object[] { codiceGara, codiceLotto} );
    	
    	if (numIncarichiProfEliminati > 0) {
      	incarichiProfessionaliModificati = true;
    	}*/
    }
    
    return incarichiProfessionaliModificati;
  }

  /**
   * Ritorna una stringa se il DataColumnContainer ha dei campi modificati, altrimenti torna null.
   * Nel primo caso la stringa e' cosi' fatta: 
   *
   * 	Campi modificati: <lista campi modificati> 
   *  
   * @param entita
   * @param descrizioneEntita
   * @param dcc
   * @param progressivoOccorrenza
   * @return
   */
  /*public static String getListaCampiModificati(DataColumnContainer dcc) {
  	return UtilitySITAT.getListaCampiModificati(dcc, null);
  }*/
  
  /**
   * Ritorna una stringa se il DataColumnContainer ha dei campi modificati, altrimenti torna null.
   * Nel primo caso la stringa e' cosi' fatta: 
   *
   * 	Modificato record n. <n>. Campi modificati: <lista campi modificati> 
   *  
   * @param entita
   * @param descrizioneEntita
   * @param dcc
   * @param progressivoOccorrenza
   * @return
   */
  /*public static String getListaCampiModificati(DataColumnContainer dcc, Long progressivoOccorrenza) {
  	
  	boolean isEntitaConOccorrenzeMultiple = true;
  	if (progressivoOccorrenza == null || (progressivoOccorrenza != null && progressivoOccorrenza.longValue() <= 0)) {
  		isEntitaConOccorrenzeMultiple = false;
  	}
  	
  	Vector<String> vetNomeFisicoCampiModificati = new Vector<String>();
		
		HashMap<?, ?> colonne = (HashMap<?, ?>) dcc.getColonne();
		Iterator<?> iter = colonne.keySet().iterator();
		
		int numeroCampiModificati = 0;
		
		while (iter.hasNext()) {
			String keyCampo = (String) iter.next();
			DataColumn campo = (DataColumn) colonne.get(keyCampo);
			if (campo.isModified()) {
				vetNomeFisicoCampiModificati.add(campo.getNomeFisico());
				numeroCampiModificati++;
			}
		}

		DizionarioCampi dizCampi = DizionarioCampi.getInstance();
		
		if (numeroCampiModificati > 0) {
			StringBuffer strBuf = new StringBuffer("");
			if (isEntitaConOccorrenzeMultiple) {
				strBuf.append("Modificato record n. ");
				strBuf.append(progressivoOccorrenza.toString());
				if (numeroCampiModificati > 1) {
					strBuf.append(". Campi modificati: ");
				} else {
					strBuf.append("'. Campo modificato: ");
				}
			} else {
				if (numeroCampiModificati > 1) {
					strBuf.append(". Campi modificati: ");
				} else {
					strBuf.append(". Campo modificato: ");
				}
			}
			
			for (int i = 0; i < vetNomeFisicoCampiModificati.size() && i < 5; i++) {
				strBuf.append(dizCampi.getCampoByNomeFisico(
						vetNomeFisicoCampiModificati.get(i)).getDescrizioneBreve());
				if (i < vetNomeFisicoCampiModificati.size()-1 || i < 4) {
					strBuf.append(", ");
				}
			}
			if (numeroCampiModificati > 5) {
				strBuf.append(" e altri");
			}
			return strBuf.toString();
		} else {
			return null;
		}
  }*/
  
  /**
   * Estrae il codice CIG di un lotto a partire dalla chiave della tabella (CODGARA, CODLOTT),
   * null altrimenti.
   * 
   * @param codiceGara
   * @param codiceLotto
   * @param sqlManager
   * @return Ritorna il codice CIG di un lotto a partire dalla chiave della tabella (CODGARA, CODLOTT),
   * null altrimenti
   * @throws SQLException
   */
  public static String getCIGLotto(final Long codiceGara, final Long codiceLotto,
  		SqlManager sqlManager) throws SQLException {
  	return (String) sqlManager.getObject("select CIG from W9LOTT where CODGARA=? and CODLOTT=?",
  			new Object[] { codiceGara, codiceLotto } );
  }

  /**
   * Ritorna codice gara a partire da IDAVGARA.
   * 
   * @param idAvGAra
   * @param sqlManager
   * @return
   * @throws SQLException
   */
  public static Long getCodGaraByIdAvGara(final String idAvGAra, SqlManager sqlManager) throws SQLException {
    return (Long) sqlManager.getObject("select CODGARA from W9GARA where IDAVGARA=?",
      new Object[] { idAvGAra } );
  }

  /**
   * Ritorna IDAVGARA a partire da codice gara e CF della S.A.
   * 
   * @param idAvGAra
   * @param sqlManager
   * @return
   * @throws SQLException
   */
  public static String getIdAvGaraByCodGaraCfStazApp(final Long codiceGara, final String cfStazApp, SqlManager sqlManager) throws SQLException {
    return (String) sqlManager.getObject("select IDAVGARA from W9GARA g where g.CODGARA=? and exists (select 1 from UFFINT u where u.CODEIN=g.CODEIN and u.CFEIN=?)",
      new Object[] { codiceGara, cfStazApp } );
  }
  
  /**
   * Ritorna una HashMap con due oggetti di tipo Long, individuati dalle chiavi
   * CODGARA e CODLOTT.
   * 
   * @param codiceCIG
   * @param sqlManager
   * @return
   * @throws SQLException
   */
  public static HashMap<String, Long> getCodGaraCodLottByCIG(final String codiceCIG,
      SqlManager sqlManager) throws SQLException {

    HashMap<String, Long> hm = null;
    Vector<?> temp = sqlManager.getVector("select CODGARA, CODLOTT from W9LOTT where CIG=?",
        new Object[] { codiceCIG } );
    
    if (temp != null) {
      hm = new HashMap<String, Long>();
      hm.put("CODGARA", (Long) SqlManager.getValueFromVectorParam(temp, 0).getValue());
      hm.put("CODLOTT", (Long) SqlManager.getValueFromVectorParam(temp, 1).getValue());
    }
    
    return hm;
  }
  
  /**
   * Inserimento del record in W9FASI per ogni flusso
   * 
   * @param sqlManager SqlManager
   * @param codiceGara Codice della gara
   * @param codiceLotto Codice del lotto
   * @param numeroFase Numero della fase
   * @param faseEsecuz Fase esecuzione
   * 
   * @throws SQLException SQLException
   */
	public static void istanziaFase(SqlManager sqlManager, Long codiceGara, Long codiceLotto, 
			Long faseEsecuz, Long numeroFase) throws SQLException {

	  // Creazione del record di W9FASI.
	  DataColumn codiceGaraFasi = new DataColumn("W9FASI.CODGARA",
	      new JdbcParametro(JdbcParametro.TIPO_NUMERICO, codiceGara));
	  DataColumn codiceLottoFasi = new DataColumn("W9FASI.CODLOTT", 
	      new JdbcParametro(JdbcParametro.TIPO_NUMERICO, codiceLotto));
	  DataColumn faseEsecuzione = new DataColumn("W9FASI.FASE_ESECUZIONE", 
	        new JdbcParametro(JdbcParametro.TIPO_NUMERICO, faseEsecuz));
	  DataColumn w9FasiNum = new DataColumn("W9FASI.NUM", new JdbcParametro(
	      JdbcParametro.TIPO_NUMERICO, numeroFase));
	  DataColumn w9FasiNumAppa = new DataColumn("W9FASI.NUM_APPA", new JdbcParametro(
	      JdbcParametro.TIPO_NUMERICO, new Long(1)));
	  codiceGaraFasi.setChiave(true);
	  codiceLottoFasi.setChiave(true);
	  faseEsecuzione.setChiave(true);
	  w9FasiNum.setChiave(true);
	  
	  String cig = (String) sqlManager.getObject("select cig from w9lott where codlott=? and codgara=?", new Object[]{codiceLotto,codiceGara});
	  String idSchedaLocale =  cig + "_" + UtilityStringhe.fillLeft(faseEsecuz.toString(), '0', 3) + "_" + UtilityStringhe.fillLeft(numeroFase.toString(), '0', 3);
	  DataColumn w9FasiIdSchedaLocale = new DataColumn("W9FASI.ID_SCHEDA_LOCALE", new JdbcParametro(
		      JdbcParametro.TIPO_TESTO,idSchedaLocale));
	  
	  if( !existsFase(codiceGara, codiceLotto, new Long(1), faseEsecuz.intValue(), numeroFase, sqlManager)) {
	  	DataColumn w9FasiDaExport = new DataColumn("W9FASI.DAEXPORT", new JdbcParametro(JdbcParametro.TIPO_TESTO, "1"));
		  // Inserimento in W9FASI
		  DataColumnContainer dccFasi = new DataColumnContainer(
				new DataColumn[] {codiceGaraFasi, codiceLottoFasi, faseEsecuzione, w9FasiNum, w9FasiIdSchedaLocale, w9FasiNumAppa, w9FasiDaExport });
		  dccFasi.insert("W9FASI", sqlManager);
	  } else {
		  if (existsFaseEsportata(codiceGara, codiceLotto, faseEsecuz.intValue(), numeroFase, sqlManager)) {
			  codiceGaraFasi.setOriginalValue(new JdbcParametro(JdbcParametro.TIPO_NUMERICO, codiceGara));
			  codiceLottoFasi.setOriginalValue(new JdbcParametro(JdbcParametro.TIPO_NUMERICO, codiceLotto));
			  faseEsecuzione.setObjectOriginalValue(new JdbcParametro(JdbcParametro.TIPO_NUMERICO, faseEsecuz));
			  w9FasiNum.setOriginalValue(new JdbcParametro(JdbcParametro.TIPO_NUMERICO, numeroFase));
		  	
			  // Aggiornamento di W9FASI per una fase gia' esportata
			  DataColumn w9FasiDaExport = new DataColumn("W9FASI.DAEXPORT", new JdbcParametro(
				      JdbcParametro.TIPO_TESTO, "1"));
			  DataColumnContainer dccFasi = new DataColumnContainer(
				      new DataColumn[] {codiceGaraFasi, codiceLottoFasi, faseEsecuzione, w9FasiNum, w9FasiIdSchedaLocale, w9FasiDaExport});
			  dccFasi.update("W9FASI", sqlManager);
		  }
		  // else
		  // Non serve aggiornare il record di W9FASI.
	  }
	}
  
  /**
   * Aggiunta di un messaggio di errore per oggetti di tipo SchedaAType.
   * 
   * @param hmResponseLotti HashMap con chiave il CIG del lotto e valore un oggetto di tipo ResponseLottoType
   * @param cigLotto CIG del lotto
   * @param msg Messaggio di errore da aggiungere
   */
	public static void aggiungiMsgSchedaA(HashMap<String, ResponseLottoType> hmResponseLotti,
			String cigLotto, boolean isSuccess, String msg) {
		
		if (hmResponseLotti.containsKey(cigLotto)) {
			ResponseSchedaAType schedaA = hmResponseLotti.get(cigLotto).getResultSchedaAType();
			if (schedaA != null) {
				if (schedaA.getMsgScheda() != null) {
					if (StringUtils.isNotEmpty(msg)) {
						List<String> listaMsgSchedaA = Arrays.asList(schedaA.getMsgScheda());
						listaMsgSchedaA.add(msg);
				
						schedaA.setMsgScheda(listaMsgSchedaA.toArray(new String[]{}));
						
						hmResponseLotti.get(cigLotto).setResultSchedaAType(schedaA);
					}
				} else if (StringUtils.isNotEmpty(msg)) {
					schedaA.setMsgScheda(new String[] {msg} );
					
					hmResponseLotti.get(cigLotto).setResultSchedaAType(schedaA);
				}
			} else {
				schedaA = new ResponseSchedaAType();
				schedaA.setSuccess(isSuccess);
				if (StringUtils.isNotEmpty(msg)) {
					schedaA.setMsgScheda(new String[] {msg} );
				}
				
				hmResponseLotti.get(cigLotto).setResultSchedaAType(schedaA);
			}
		} else {
			ResponseLottoType responseLotto = new ResponseLottoType();
			responseLotto.setCIG(cigLotto);

			ResponseSchedaAType schedaA = new ResponseSchedaAType();
			schedaA.setSuccess(isSuccess);
			if (StringUtils.isNotEmpty(msg)) {
				schedaA.setMsgScheda(new String[] {msg} );
			}
			
			responseLotto.setResultSchedaAType(schedaA);
			hmResponseLotti.put(cigLotto, responseLotto);
		}
	}
  
	/**
   * Aggiunta di un messaggio di errore per oggetti di tipo SchedaBType.
   * 
   * @param hmResponseLotti HashMap con chiave il CIG del lotto e valore un oggetto di tipo ResponseLottoType
   * @param cigLotto CIG del lotto
   * @param progressivo della scheda
   * @param msg Messaggio di errore da aggiungere
   */
	public static void aggiungiMsgSchedaB(HashMap<String, ResponseLottoType> hmResponseLotti,
			String cigLotto, int progressivoScheda, boolean isSuccess, String msg) {
		
		if (hmResponseLotti.containsKey(cigLotto)) {
			ResponseSchedaBType[] schedaB = hmResponseLotti.get(cigLotto).getResultSchedaBType();
			if (schedaB != null) {
				if (schedaB.length > 0) {
					boolean schedaTrovata = false;
					int i = 0;
					for (i = 0; i < schedaB.length && !schedaTrovata; i++) {
						ResponseSchedaBType tempSchedaB = schedaB[i];
						if (tempSchedaB != null) {
							if (tempSchedaB.getNumeroScheda() == progressivoScheda) {
								schedaTrovata = true;
							}
						}
					}
					
					if (schedaTrovata) {
						if (StringUtils.isNotEmpty(msg)) {
							if (schedaB[i].getMsgScheda() != null) {
								List<String> listaMsgSchedaB = Arrays.asList(schedaB[i].getMsgScheda());
								listaMsgSchedaB.add(msg);
								schedaB[i].setMsgScheda(listaMsgSchedaB.toArray(new String[]{}));
							} else {
								schedaB[i].setMsgScheda(new String[] { msg });
							}
						}
					} else {
						ResponseSchedaBType tempSchedaB = new ResponseSchedaBType(); 
						tempSchedaB.setSuccess(isSuccess);
						tempSchedaB.setNumeroScheda(progressivoScheda);
						if (StringUtils.isNotEmpty(msg)) {
							tempSchedaB.setMsgScheda(new String[] {msg} );
						}
						List<ResponseSchedaBType> listaResponseB = new ArrayList<ResponseSchedaBType>(Arrays.asList(schedaB));
						listaResponseB.add( tempSchedaB);
						
						schedaB = listaResponseB.toArray(new ResponseSchedaBType[]{});
						hmResponseLotti.get(cigLotto).setResultSchedaBType(schedaB);
					}
					
				} else {
					ResponseSchedaBType tempSchedaB = new ResponseSchedaBType(); 
					tempSchedaB.setSuccess(isSuccess);
					tempSchedaB.setNumeroScheda(progressivoScheda);
					if (StringUtils.isNotEmpty(msg)) {
						tempSchedaB.setMsgScheda(new String[] {msg} );
					}
					schedaB = new ResponseSchedaBType[]{ tempSchedaB };
					hmResponseLotti.get(cigLotto).setResultSchedaBType(schedaB);
				}
			} else {
				ResponseSchedaBType tempSchedaB = new ResponseSchedaBType(); 
				tempSchedaB.setSuccess(isSuccess);
				tempSchedaB.setNumeroScheda(progressivoScheda);
				if (StringUtils.isNotEmpty(msg)) {
					tempSchedaB.setMsgScheda(new String[] {msg} );
				}
				schedaB = new ResponseSchedaBType[]{ tempSchedaB };
				hmResponseLotti.get(cigLotto).setResultSchedaBType(schedaB);
			}
		} else {
			ResponseLottoType responseLotto = new ResponseLottoType();
			responseLotto.setCIG(cigLotto);

			ResponseSchedaBType schedaB = new ResponseSchedaBType();
			schedaB.setSuccess(isSuccess);
			if (StringUtils.isNotEmpty(msg)) {
				schedaB.setMsgScheda(new String[] {msg} );
			}
			schedaB.setNumeroScheda(progressivoScheda);

			responseLotto.setResultSchedaBType(new ResponseSchedaBType[]{ schedaB } );
			hmResponseLotti.put(cigLotto, responseLotto);
		}
	}
	
	/**
	 * 
	 * @param result
	 * @param sovrascrivereDatiEsistenti
	 * @param hmResponseLotti
	 * @param numeroLottiImportati
	 * @param numeroLottiNonImportati
	 * @param numeroLottiAggiornati
	 */
	public static void preparaRisultatoMessaggio(ResponseType result, boolean sovrascrivereDatiEsistenti,
			HashMap<String, ResponseLottoType> hmResponseLotti, int numeroLottiImportati,
			int numeroLottiNonImportati, int numeroLottiAggiornati) {
		
		if ((numeroLottiImportati + numeroLottiAggiornati) > 0) {
			// Per almeno un lotto e' stata importata/aggiornata la fase di esito
			result.setSuccess(true);
		  StringBuilder strMsg = new StringBuilder("Importazione terminata con successo. Schede caricate: ");
		  strMsg.append(numeroLottiImportati);
		  strMsg.append("; schede non importate: ");
		  strMsg.append(numeroLottiNonImportati);
		  if (sovrascrivereDatiEsistenti) {
		    strMsg.append("; schede aggiornate: ");
		    strMsg.append(numeroLottiAggiornati);
		  }
		  result.setError(strMsg.toString());
		} else {
			// Per nessun lotto e' stata importata/aggiornata la fase di esito
			result.setSuccess(false);
			result.setError("Importazione terminata. Nessuna scheda importata");
		}
		
		if (hmResponseLotti.size() > 0) {
			List< ResponseLottoType > listaResponse = new ArrayList< ResponseLottoType >();
			Iterator<Entry<String, ResponseLottoType>> iterator = hmResponseLotti.entrySet().iterator();
			
			while(iterator.hasNext()) {
				Entry<String, ResponseLottoType> item = iterator.next();
				listaResponse.add(item.getValue());
			}

			result.setResultLotti(listaResponse.toArray(new ResponseLottoType[] {}));
		}
	}
	
	
	/**
	 * 
	 * @param result
	 * @return
	 */
	public static String messaggioLogEsteso(ResponseType result) {
		String message = "L'esecuzione dell'operazione ha prodotto il seguente risultato:";
		String aCapo = "\r\n";
		message = message + aCapo + "Is success : " + result.isSuccess();
		message = message + aCapo + "Error : " + result.getError();
		if(result.getResultLotti() != null && result.getResultLotti().length > 0) {
			message = message + aCapo + "Risultato Lotti ";
			//ciclo
			for(int i = 0; i < result.getResultLotti().length; i++) {
				message = message + aCapo + " - CIG : " + result.getResultLotti(i).getCIG();
				if(result.getResultLotti(i).getResultSchedaAType() != null) {
					message = message + aCapo + " - Risultato Scheda A ";
					message = message + aCapo + " - - success : " + result.getResultLotti(i).getResultSchedaAType().isSuccess();
					if(result.getResultLotti(i).getResultSchedaAType().getMsgScheda() != null){
						for(int j = 0; j < result.getResultLotti(i).getResultSchedaAType().getMsgScheda().length; j++){
							message = message + aCapo + " - - - message : " + result.getResultLotti(i).getResultSchedaAType().getMsgScheda(j);
						}
					}
				}
				if(result.getResultLotti(i).getResultSchedaBType() != null && result.getResultLotti(i).getResultSchedaBType().length > 0) {
					message = message + aCapo + " - Risultato Scheda B ";
					for(int j = 0; j < result.getResultLotti(i).getResultSchedaBType().length; j++){
						message = message + aCapo + " - - numero scheda : " + result.getResultLotti(i).getResultSchedaBType(j).getNumeroScheda();
						message = message + aCapo + " - - success : " + result.getResultLotti(i).getResultSchedaBType(j).isSuccess();
						if(result.getResultLotti(i).getResultSchedaBType(j) != null && result.getResultLotti(i).getResultSchedaBType(j).getMsgScheda() != null){
							for(int k = 0; k < result.getResultLotti(i).getResultSchedaBType(j).getMsgScheda().length; k++){
								message = message + aCapo + " - - - message : " + result.getResultLotti(i).getResultSchedaBType(j).getMsgScheda(k);
							}
						}
					}			
				}
			}
		}
		
		return message;
	}	

	
	// DA CANCELLARE
	// MODIFICA TEMPORANEA
	// ho bisogno di modificare il messaggio in uscita poiche' attualmente il programma di LFS ed Appalti 
	// non recuperano il messaggio correttamente e stamparlo a video per l'utente che non riceve la segnalazione corretta
	/**
	 * 
	 * @param result
	 * @return
	 */
	public static String messaggioEsteso(ResponseType result) {
		String message = result.getError();
		if (result.getResultLotti() != null && result.getResultLotti().length > 0) {
			//ciclo
			for (int i = 0; i < result.getResultLotti().length; i++){
				if (result.getResultLotti(i).getResultSchedaAType() != null) {
					if (result.getResultLotti(i).getResultSchedaAType().getMsgScheda() != null) {
						for (int j = 0; j < result.getResultLotti(i).getResultSchedaAType().getMsgScheda().length; j++) {
							message = message + " \r\n" + result.getResultLotti(i).getResultSchedaAType().getMsgScheda(j);
						}
					}
				}
				message = message + " (CIG: " + result.getResultLotti(i).getCIG() + ") ";
			}
		}
		return message;
	}
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
  /**
   * Estrazione della Data attuazione Simog 3.04.2: data discrimanante di attuazione della nuova versione di SIMOG
   * dal tabellato W9023 in TAB1
   * 
   * @return
   * @throws SQLException
   */
  public static GregorianCalendar getDataAttuazioneSimog2(final SqlManager sqlManager) throws SQLException {
    String strDataAttuazioneSimog2 = (String) sqlManager.getObject(
        "select TAB1DESC from TAB1 where TAB1COD='W9023' and TAB1TIP=1", null);
    
    GregorianCalendar dataAttuazioneSimog2 = null;
    if (StringUtils.isNotEmpty(strDataAttuazioneSimog2)) {
      Date tempDataAttuazioneSimog2 = UtilityDate.convertiData(strDataAttuazioneSimog2.substring(0, 10), 
          UtilityDate.FORMATO_GG_MM_AAAA);
      if (tempDataAttuazioneSimog2 != null) {
        dataAttuazioneSimog2 = new GregorianCalendar(tempDataAttuazioneSimog2.getYear()+1900, 
        		tempDataAttuazioneSimog2.getMonth(), tempDataAttuazioneSimog2.getDate(), 0, 0, 0);
      }
    }
    return dataAttuazioneSimog2;
  }
 
  
  /**
   * Ritorna true se in base date il lotto risulta essere ri-aggiudicato, false altrimenti
   *  
   * @param codgara
   * @param codlott
   * @param sqlManager
   * @return
   * @throws SQLException
   */
  public static boolean isLottoRiaggiudicato(final Long codgara, final Long codlott, final SqlManager sqlManager) throws SQLException {
  	Long counter = (Long) sqlManager.getObject(
  			"select count(*) from W9APPA where CODGARA=? and CODLOTT=? and NUM_APPA > 1", 
  			new Object[] { codgara, codlott });
  	return counter.longValue() > 0;
  }

  public static GregorianCalendar getDataAttuazioneSimogVer2() throws SQLException {
    GregorianCalendar dataAttuazioneSimog1 = new GregorianCalendar(2019,Calendar.JUNE,23);
    return dataAttuazioneSimog1;
  }
  
  public static GregorianCalendar getDataAttuazioneSimogVer3() throws SQLException {
    GregorianCalendar dataAttuazioneSimog2 = new GregorianCalendar(2019,Calendar.OCTOBER,21);
    return dataAttuazioneSimog2;
  }
  
  public static GregorianCalendar getDataAttuazioneSimogVer4() throws SQLException {
    GregorianCalendar dataAttuazioneSimog4 = new GregorianCalendar(2020,Calendar.JANUARY,1);
    return dataAttuazioneSimog4;
  }
  
  public static GregorianCalendar getDataAttuazioneSimogVer5() throws SQLException {
    GregorianCalendar dataAttuazioneSimog5 = new GregorianCalendar(2020,Calendar.MAY,12);
    return dataAttuazioneSimog5;
  }
  
  public static GregorianCalendar getDataAttuazioneSimogVer6() throws SQLException {
	GregorianCalendar dataAttuazioneSimog6 = new GregorianCalendar(2020,Calendar.DECEMBER,10);
	return dataAttuazioneSimog6;
  }  
  
  /**
   * Ritorna il valore del campo W9GARA.VER_SIMOG. 
   * 
   * @param sqlManager
   * @param codgara
   * @return ritorna il valore del campo W9GARA.VER_SIMOG
   * @throws SQLException
   */
  public static int getVersioneSimog(final SqlManager sqlManager, final Long codgara) throws SQLException {
    int result = 1;
    Long versioneSimog = (Long) sqlManager.getObject(
        "select VER_SIMOG from W9GARA where CODGARA=?", new Object[] { codgara } );
    
    if (versioneSimog != null) {
      result = versioneSimog.intValue();
    }
    return result;
  }

  public static int getVersioneSimog(final SqlManager sqlManager, final String idAvGara) throws SQLException {
    int result = 1;
    Long versioneSimog = (Long) sqlManager.getObject(
        "select VER_SIMOG from W9GARA where IDAVGARA=?", new Object[] {idAvGara } );
    
    if (versioneSimog != null) {
      result = versioneSimog.intValue();
    }
    return result;
  }
  
  
  /**
   * Ritorna la versione SIMOG della gara in funzione della data di creazione della gara su SIMOG e 
   * della data di perfezionamento del bando.
   * 
   * @param dataCreazioneGara
   * @param dataPerfezionamentoBando
   * @return Ritorna la versione SIMOG della gara
   * @throws SQLException
   */
  public static int getVersioneSimog(GregorianCalendar dataCreazioneGara, GregorianCalendar dataPerfezionamentoBando) throws SQLException {
    int versioneSimog = 6;
    
    GregorianCalendar dataAttuazioneSimogVer2 = UtilitySITAT.getDataAttuazioneSimogVer2();  //23/06/2019
    GregorianCalendar dataAttuazioneSimogVer3 = UtilitySITAT.getDataAttuazioneSimogVer3();  //21/10/2019
    GregorianCalendar dataAttuazioneSimogVer4 = UtilitySITAT.getDataAttuazioneSimogVer4();  //01/01/2020
    GregorianCalendar dataAttuazioneSimogVer5 = UtilitySITAT.getDataAttuazioneSimogVer5();  //12/05/2020
    GregorianCalendar dataAttuazioneSimogVer6 = UtilitySITAT.getDataAttuazioneSimogVer6();  //10/12/2020
    
    if (dataCreazioneGara != null && dataCreazioneGara.compareTo(dataAttuazioneSimogVer6) >= 0) {
      versioneSimog = 6;
    } else if (dataCreazioneGara != null && dataCreazioneGara.before(dataAttuazioneSimogVer2)) {
      versioneSimog = 1;
    } else if (dataCreazioneGara != null && dataCreazioneGara.before(dataAttuazioneSimogVer3)) {
      versioneSimog = 2;
    } else if (dataCreazioneGara != null && (DateUtils.isSameDay(dataCreazioneGara, dataAttuazioneSimogVer5)
        || dataCreazioneGara.after(dataAttuazioneSimogVer5))) {
      versioneSimog = 5;
    } else if (dataPerfezionamentoBando != null && dataPerfezionamentoBando.before(dataAttuazioneSimogVer4)) {
      versioneSimog = 3;
    } else if (dataCreazioneGara != null && dataCreazioneGara.before(dataAttuazioneSimogVer5)) {
      versioneSimog = 4;
    }
    
    return versioneSimog;
  }
  
  
  /**
   * Ritorna true se la gara e' in carico alla stazione appaltante delegata con compito di 
   * aggiudicazione, cioe' UFFINT.CFEIN <> W9GARA.CF_SA_AGENTE e ID_F_DELEGATE = 1 o 2, false altrimenti
   * 
   * @param sqlManager
   * @param codgara
   * @return Ritorna true se la gara e' in carico alla stazione appaltante delegata con compito di aggiudicazione, false altrimenti
   * @throws SQLException
   */
  public static boolean isD1(final Long codgara, final SqlManager sqlManager) throws SQLException {
    Long count = (Long) sqlManager.getObject(
        "select count(u.CFEIN) from UFFINT u, W9GARA g where g.CODGARA=? and g.CODEIN=u.CODEIN and UPPER(u.CFEIN) <> UPPER(g.CF_SA_AGENTE) and g.ID_F_DELEGATE in (1,2)", 
        new Object[] { codgara });
    if (count.longValue() > 0) {
      return true;
    } else {
      return false;
    }
  }
  
}
