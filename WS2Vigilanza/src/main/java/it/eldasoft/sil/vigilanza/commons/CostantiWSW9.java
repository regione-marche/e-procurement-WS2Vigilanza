package it.eldasoft.sil.vigilanza.commons;

public class CostantiWSW9 {

  /**
   * Costruttore.
   */
  private CostantiWSW9() {
  }

  /**
   * Area di invio per le fasi di gara.
   */
  public static final int AREA_FASI_DI_GARA = 1;

  /**
   * Area di invio per anagrafica gara/lotti.
   */
  public static final int AREA_ANAGRAFICA_GARE = 2;

  /**
   * Area di invio per avvisi di gara.
   */
  public static final int AREA_AVVISI = 3;

  /**
   * Area di invio per programmi triennali o annuali.
   */
  public static final int AREA_PROGRAMMA_TRIENNALI_ANNUALI = 4;

  /**
   * Area di invio per Gare di enti nazionali e sotto i 40.000 euro.
   */
  public static final int AREA_GARE_ENTINAZIONALI = 5;

  /**
   * Anagrafica gara lotti.
   */
  public static final int A03 = 988;
  /**
   * Anagrafica gara lotti.
   */
  public static final int ANAGRAFICA_GARA_LOTTI = A03;
  /**
   * Descrizione descrittiva della fase Anagrafica gara lotti.
   */
  public static String STR_ANAGRAFICA_GARA_LOTTI = "Anagrafica gara lotti";
  
  /**
   * Fase semplificata aggiudicazione/affidamento appalto (sotto 150000).
   */
  public static final int A04 = 987;
  /**
   * Fase semplificata aggiudicazione/affidamento appalto (sotto 150000).
   */
  public static final int FASE_SEMPLIFICATA_AGGIUDICAZIONE = A04;
  /**
   * Descrizione descrittiva della fase Aggiudicazione semplificata.
   */
  public static String STR_AGGIUDICAZIONE_SEMPLIFICATA = "Aggiudicazione semplificata";
  
  /**
   * Fase aggiudicazione/affidamento appalto (sopra 150000).
   */
  public static final int A05 = 1;
  /**
   * Fase aggiudicazione/affidamento appalto (sopra 150000).
   */
  public static final int AGGIUDICAZIONE_SOPRA_SOGLIA = A05;
  /**
   * Descrizione descrittiva della fase Aggiudicazione semplificata.
   */
  public static String STR_AGGIUDICAZIONE_SOPRA_SOGLIA = "Aggiudicazione";
  
  /**
   * Fase iniziale esecuzione contratto (sopra 150000).
   */
  public static final int A06 = 2;
  /**
   *  Fase iniziale esecuzione contratto (sopra 150000).
   */
  public static final int INIZIO_CONTRATTO_SOPRA_SOGLIA = A06;
  /**
   * Descrizione descrittiva della fase Inizio contratto.
   */
  public static String STR_INIZIO_CONTRATTO_SOPRA_SOGLIA = "Inizio contratto";
  
  /**
   * Fase semplificata iniziale esecuzione contratto (sotto 150000).
   */
  public static final int A07 = 986;
  /**
   * Fase semplificata iniziale esecuzione contratto (sotto 150000).
   */
  public static final int FASE_SEMPLIFICATA_INIZIO_CONTRATTO = A07;
  /**
   * Descrizione descrittiva della fase Inizio contratto.
   */
  public static String STR_INIZIO_CONTRATTO_SEMPLIFICATA = "Inizio contratto semplificato";
  
  /**
   * Fase esecuzione e avanzamento del contratto (sopra 150000).
   */
  public static final int A08 = 3;
  /**
   * Fase esecuzione e avanzamento del contratto (sopra 150000).
   */
  public static final int AVANZAMENTO_CONTRATTO_SOPRA_SOGLIA = A08;
  /**
   * Stringa descrizione della fase Avanzamento contratto sopra soglia.
   */
  public static final String STR_AVANZAMENTO_CONTRATTO_SOPRA_SOGLIA = "Avanzamento";

  /**
   * Avanzamento contratto appalto semplificato.
   */
  public static final int A25 = 102;
  /**
   * Avanzamento contratto appalto semplificato.
   */
  public static final int AVANZAMENTO_CONTRATTO_APPALTO_SEMPLIFICATO = A25;
  /**
   * Stringa descrizione della fase Avanzamento contratto appalto semplificato.
   */
  public static final String STR_AVANZAMENTO_CONTRATTO_APPALTO_SEMPLIFICATO = "Avanzamento semplificato";
  
  /**
   * Fase di conclusione del contratto (sopra 150000).
   */
  public static final int A09 = 4;
  /**
   * Fase di conclusione del contratto (sopra 150000).
   */
  public static final int CONCLUSIONE_CONTRATTO_SOPRA_SOGLIA = A09;
  /**
   * Stringa descrittiva della fase Conclusione contratto sopra soglia.
   */
  public static String STR_CONCLUSIONE_CONTRATTO_SOPRA_SOGLIA = "Conclusione";
  
  /**
   * Fase semplificata di conclusione del contratto (sotto 150000).
   */
  public static final int A10 = 985;
  /**
   * Fase semplificata di conclusione del contratto (sotto 150000).
   */
  public static final int FASE_SEMPLIFICATA_CONCLUSIONE_CONTRATTO = A10;
  /**
   * Stringa descrittiva della fase Conclusione contratto semplificata.
   */
  public static String STR_CONCLUSIONE_CONTRATTO_SEMPLIFICATA = "Conclusione semplificata";
  
  /**
   * Fase di collaudo del contratto.
   */
  public static final int A11 = 5;
  /**
   * Fase di collaudo del contratto.
   */
  public static final int COLLAUDO_CONTRATTO = A11;
  /**
   * Stringa descrittiva della fase Collaudo contratto.
   */
  public static String STR_COLLAUDO_CONTRATTO = "Collaudo contratto";
  
  /**
   * Sospensione del contratto.
   */
  public static final int A12 = 6;
  /**
   * Sospensione del contratto.
   */
  public static final int SOSPENSIONE_CONTRATTO = A12;
  /**
   * Stringa descrittiva della fase Sospensione contratto.
   */
  public static String STR_SOSPENSIONE_CONTRATTO = "Sospensione contratto";
  
  /**
   * Variante del contratto.
   */
  public static final int A13 = 7;
  /**
   * Variante del contratto.
   */
  public static final int VARIANTE_CONTRATTO = A13;
  /**
   * Stringa descrittiva della fase Variante contratto.
   */
  public static final String STR_VARIANTE_CONTRATTO = "Variante contratto";

  /**
   * Accordi bonari.
   */
  public static final int A14 = 8;
  /**
   * Accordi bonari.
   */
  public static final int ACCORDO_BONARIO = A14;
  /**
   * Stringa descrittiva della fase Accordo bonario
   */
  public static final String STR_ACCORDO_BONARIO = "Accordo bonario";
  
  /**
   * Subappalti.
   */
  public static final int A15 = 9;
  /**
   * Subappalti.
   */
  public static final int SUBAPPALTO = A15;
	/**
	 * Stringa descrittiva della fase Subappalto.
	 */
  public static final String STR_SUBAPPALTO = "Subappalto";
  
  /**
   * Istanza di recesso.
   */
  public static final int A16 = 10;
  /**
   * Istanza di recesso.
   */
  public static final int ISTANZA_RECESSO = A16;
  /**
	 * Stringa descrittiva della fase Istanza di recesso.
	 */
  public static final String STR_ISTANZA_RECESSO = "Istanza di recesso";
  
  /**
   * Stipula accordo quadro.
   */
  public static final int A20 = 11;
  /**
   * Stipula accordo quadro.
   */
  public static final int STIPULA_ACCORDO_QUADRO = A20;
  /**
   * Stringa di descrizione della fase Stipula accordo quadro.
   */
  public static final String STR_STIPULA_ACCORDO_QUADRO = "Stipula accordo quadro";
  
  /**
   * Adesione accordo quadro.
   */
  public static final int A21 = 12;
  /**
   * Adesione accordo quadro.
   */
  public static final int ADESIONE_ACCORDO_QUADRO = A21;
  /**
   * Stringa di descrizione della fase Adesione accordo quadro.
   */
  public static final String STR_ADESIONE_ACCORDO_QUADRO = "Adesione accordo quadro";
  
  /**
   * Comunicazione esito.
   */
  public static final int A22 = 984;
  /**
   * Comunicazione esito.
   */
  public static final int COMUNICAZIONE_ESITO = A22;
  /**
   * Stringa di descrizione della fase Comunicazione esito.
   */
  public static final String STR_COMUNICAZIONE_ESITO = "Comunicazione esito";
  
  /**
   * Elenco imprese invitate/partecipanti
   */
  public static final int A24 = 101;
  /**
   * Elenco imprese invitate/partecipanti
   */
  public static final int ELENCO_IMPRESE_INVITATE_PARTECIPANTI = A24;
  /**
   * Stringa di descrizione della fase Elenco imprese invitate / partecipanti.
   */
  public static final String STR_ELENCO_IMPRESE_INVITATE_PARTECIPANTI = "Elenco imprese invitate/partecipanti";
  
  /**
   * Esito negativo verifica idoneita' tecnico-professionale dell'impresa aggiudicataria.
   */
  public static final int B02 = 997;
  /**
   * Esito negativo verifica idoneita' tecnico-professionale dell'impresa aggiudicataria.
   */
  public static final int ESITO_NEGATIVO_VERIFICA_TECNICO_PROFESSIONALE_IMPRESA_AGGIUDICATARIA = B02;

  /**
   * Esito negativo verifica regolarità contributiva ed assicurativa (versione 2.0.5)
   * Mancata aggiud. def. o pagamento per irregolarità contrib. ed assicurativa (art.17c.1 e 2 LR 38/07) (versione 2.1.0)
   */
  public static final int B03 = 996;
  /**
   * Esito negativo verifica regolarità contributiva ed assicurativa (versione 2.0.5)
   * Mancata aggiud. def. o pagamento per irregolarità contrib. ed assicurativa (art.17c.1 e 2 LR 38/07) (versione 2.1.0)
   * public static final int B03 = 996;
   */
  public static final int ESITO_NEGATIVO_VERIFICA_CONTRIBUTIVA_ASSICURATIVA = B03;

  /**
   * Misure aggiuntive e migliorative sicurezza.
   */
  public static final int B04 = 993;
  /**
   * Misure aggiuntive e migliorative sicurezza.
   */
  public static final int MISURE_AGGIUNTIVE_SICUREZZA = B04;

  /**
   * Inadempienze predisposizioni sicurezza e regolarita' lavori (versione 2.0.5).
   * Inadempienze disposizioni sicurezza e regolarità del lavoro ai sensi dell’art.23 LR 38/07 (versione 2.1.0)
   */
  public static final int B06 = 995;
  /**
   * Inadempienze predisposizioni sicurezza e regolarita' lavori (versione 2.0.5).
   * Inadempienze disposizioni sicurezza e regolarità del lavoro ai sensi dell’art.23 LR 38/07 (versione 2.1.0)
   */
  public static final int INADEMPIENZE_SICUREZZA_REGOLARITA_LAVORI = B06;

  /**
   * Scheda segnalazione infortuni.
   */
  public static final int B07 = 994;
  /**
   * Scheda segnalazione infortuni.
   */
  public static final int SCHEDA_SEGNALAZIONI_INFORTUNI = B07;

  /**
   * Scheda cantiere/notifica preliminare.
   */
  public static final int B08 = 998;
  /**
   * Scheda cantiere/notifica preliminare.
   */
  public static final int APERTURA_CANTIERE = B08;

  /**
   * Pubblicazione avviso.
   */
  public static final int PUBBLICAZIONE_AVVISO = 989;

  /**
   * Pubblicazione programma triennale lavori.
   */
  public static final int PROGRAMMA_TRIENNALE_LAVORI = 992;

  /**
   *  Pubblicazione programma triennale forniture e servizi.
   */
  public static final int PROGRAMMA_TRIENNALE_FORNITURE_SERVIZI = 991;

  /**
   * Gare per enti nazionali o sotto i 40.000 euro.
   */
  public static final int A23 = 983;

  /**
   * Gare per enti nazionali o sotto i 40.000 euro.
   */
  public static final int GARE_ENTI_NAZIONALI_O_SOTTO_40000 = A23;
  
  /**
   * Pubblicazione documenti della gara / lotti.
   */
  public static final int D01 = 901;
  
  /**
   * Pubblicazione documenti della gara / lotti.
   */
  public static final int PUBBLICAZIONE_DOCUMENTI = D01;
  


  /**
   * Ritorna la descrizione di una fase a partire dalla fase di esecuzione.
   * 
   * @param faseEsecuzione
   * @return Ritorna la stringa descrittiva della fase indicata.
   */
  public static String getDescrizioneFase(int faseEsecuzione) {
  	String descrizione = null;
  	
  	switch (faseEsecuzione) {
  	case ANAGRAFICA_GARA_LOTTI:
  		descrizione = STR_ANAGRAFICA_GARA_LOTTI;
  		break;
  	case COMUNICAZIONE_ESITO:
  		descrizione = STR_COMUNICAZIONE_ESITO;
  		break;
  	case AGGIUDICAZIONE_SOPRA_SOGLIA:
  		descrizione = STR_AGGIUDICAZIONE_SOPRA_SOGLIA;
  		break;
  	case FASE_SEMPLIFICATA_AGGIUDICAZIONE:
  		descrizione = STR_AGGIUDICAZIONE_SEMPLIFICATA;
  		break;
  	case STIPULA_ACCORDO_QUADRO:
  		descrizione = STR_STIPULA_ACCORDO_QUADRO;
  		break;
  	case ADESIONE_ACCORDO_QUADRO:
  		descrizione = STR_ADESIONE_ACCORDO_QUADRO;
  		break;
  	case INIZIO_CONTRATTO_SOPRA_SOGLIA:
  		descrizione = STR_INIZIO_CONTRATTO_SOPRA_SOGLIA;
  		break;
  	case FASE_SEMPLIFICATA_INIZIO_CONTRATTO:
  		descrizione = STR_INIZIO_CONTRATTO_SEMPLIFICATA;
  		break;
		case AVANZAMENTO_CONTRATTO_SOPRA_SOGLIA:
			descrizione = STR_AVANZAMENTO_CONTRATTO_SOPRA_SOGLIA;
			break;
  	case AVANZAMENTO_CONTRATTO_APPALTO_SEMPLIFICATO:
			descrizione = STR_AVANZAMENTO_CONTRATTO_APPALTO_SEMPLIFICATO;
			break;
  	case CONCLUSIONE_CONTRATTO_SOPRA_SOGLIA:
  		descrizione = STR_CONCLUSIONE_CONTRATTO_SOPRA_SOGLIA;
			break;
  	case FASE_SEMPLIFICATA_CONCLUSIONE_CONTRATTO:
  		descrizione = STR_CONCLUSIONE_CONTRATTO_SEMPLIFICATA;
			break;
  	case COLLAUDO_CONTRATTO:
  		descrizione = STR_COLLAUDO_CONTRATTO;
			break;
  	case SOSPENSIONE_CONTRATTO:
  		descrizione = STR_SOSPENSIONE_CONTRATTO;
			break;
  	case VARIANTE_CONTRATTO:
  		descrizione = STR_VARIANTE_CONTRATTO;
			break;
  	case ACCORDO_BONARIO:
  		descrizione = STR_ACCORDO_BONARIO;
			break;
  	case SUBAPPALTO:
  		descrizione = STR_SUBAPPALTO;
			break;
  	case ISTANZA_RECESSO:
  		descrizione = STR_ISTANZA_RECESSO;
			break;
  	case ELENCO_IMPRESE_INVITATE_PARTECIPANTI:
  		descrizione = STR_ELENCO_IMPRESE_INVITATE_PARTECIPANTI;
  		break;
  	default:
			break;
		}
  	
  	return descrizione;
  }

  
  // Operazioni del WS
  
  /**
   * Operazione durante l'importazione dei dati: controlli preliminari.
   */
  public static final String OPERAZIONE_CONTROLLI_PRELIMINARI = "Controlli preliminari";
  
  /**
   * Operazione durante l'importazione dei dati per fare controlli specifici per ciascuna fase.
   */
  public static final String OPERAZIONE_CONTROLLI_SPECIFICI_FASE = "Controlli specifici ";
  
  /**
   * Operazione durante l'importazione dei dati: inserimento nuova scheda. 
   */
  public static final String OPERAZIONE_INSERIMENTO_SCHEDA   = "Inserimento";
  
  /**
   * Operazione durante l'importazione dei dati: aggiornamento scheda.
   */
  public static final String OPERAZIONE_AGGIORNAMENTO_SCHEDA = "Aggiornamento";
  
}
