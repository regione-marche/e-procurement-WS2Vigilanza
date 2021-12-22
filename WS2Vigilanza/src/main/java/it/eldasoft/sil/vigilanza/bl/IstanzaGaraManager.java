package it.eldasoft.sil.vigilanza.bl;

import it.eldasoft.gene.bl.GenChiaviManager;
import it.eldasoft.gene.bl.GeneManager;
import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.db.datautils.DataColumn;
import it.eldasoft.gene.db.datautils.DataColumnContainer;
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.sil.vigilanza.utils.UtilitySITAT;
import it.eldasoft.sil.vigilanza.ws.beans.IstanzaOggettoType;
import it.eldasoft.sil.vigilanza.ws.beans.LoginType;
import it.eldasoft.sil.vigilanza.ws.beans.ResponseLottoType;
import it.eldasoft.sil.vigilanza.ws.beans.ResponseSchedaAType;
import it.eldasoft.sil.vigilanza.ws.beans.ResponseType;
import it.eldasoft.sil.vigilanza.beans.CategoriaType;
import it.eldasoft.sil.vigilanza.beans.CondizioneProcNegType;
import it.eldasoft.sil.vigilanza.beans.CentroCostoType;
import it.eldasoft.sil.vigilanza.beans.GaraType;
import it.eldasoft.sil.vigilanza.beans.LottoType;
import it.eldasoft.sil.vigilanza.beans.ModalitaAcquisizioneFSType;
import it.eldasoft.sil.vigilanza.beans.PubblicitaGaraType;
import it.eldasoft.sil.vigilanza.beans.RichiestaSincronaIstanzaGaraDocument;
import it.eldasoft.sil.vigilanza.beans.TipologiaLavoroType;
import it.eldasoft.sil.vigilanza.commons.CostantiWSW9;
import it.eldasoft.sil.vigilanza.commons.WSVigilanzaException;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlOptions;
import org.apache.xmlbeans.XmlValidationError;

/**
 * Classe per l'import dei dati generali della gara e dei lotti.
 * 
 * @author Luca.Giacomazzo
 */
public class IstanzaGaraManager {

	private static Logger logger = Logger.getLogger(IstanzaGaraManager.class);

	private CredenzialiManager credenzialiManager;

	private RupManager rupManager;

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
	 * @param rupManager
	 *            the rupManager to set
	 */
	public void setRupManager(RupManager rupManager) {
		this.rupManager = rupManager;
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
	 *            the genChiaviManager to set
	 */
	public void setGenChiaviManager(GenChiaviManager genChiaviManager) {
		this.genChiaviManager = genChiaviManager;
	}

	/**
	 * Metodo per la creazione di gara e lotti.
	 * 
	 * @param login
	 *            LoginType
	 * @param garaLotti
	 *            IstanzaOggettoType
	 * @return Ritorna l'oggetto ResponseType con l'esito dell'operazione
	 * @throws XmlException
	 * @throws GestoreException
	 * @throws SQLException
	 * @throws Throwable
	 */
	public ResponseType istanziaGaraLotti(LoginType login, IstanzaOggettoType garaLotti) throws XmlException, GestoreException, SQLException, Throwable {
		ResponseType result = null;

		if (logger.isDebugEnabled()) {
			logger.debug("istanziaGara: inizio metodo");
			
			logger.debug("XML : " + garaLotti.getOggettoXML());
		}

		// Codice fiscale della Stazione appaltante
		String codFiscaleStazAppaltante = garaLotti.getTestata().getCFEIN();

		// Verifica di login, password e determinazione della S
		HashMap<String, Object> hm = this.credenzialiManager.verificaCredenziali(login, codFiscaleStazAppaltante);
		result = (ResponseType) hm.get("RESULT");

		if (result.isSuccess()) {
			Credenziale credenzialiUtente = (Credenziale) hm.get("USER");
			String xmlGaraLotti = garaLotti.getOggettoXML();

			try {
				RichiestaSincronaIstanzaGaraDocument istanzaGaraDocument =
						RichiestaSincronaIstanzaGaraDocument.Factory.parse(xmlGaraLotti);
				boolean isMessaggioDiTest = istanzaGaraDocument.getRichiestaSincronaIstanzaGara().isSetTest()
						&& istanzaGaraDocument.getRichiestaSincronaIstanzaGara().getTest();

				if (!isMessaggioDiTest) {

					// si esegue il controllo sintattico del messaggio
					XmlOptions validationOptions = new XmlOptions();
					ArrayList<XmlValidationError> validationErrors = new ArrayList<XmlValidationError>();
					validationOptions.setErrorListener(validationErrors);
					boolean esitoCheckSintassi = istanzaGaraDocument.validate(validationOptions);

					if (!esitoCheckSintassi) {
						synchronized (validationErrors) {
							// Sincronizzazione dell'oggetto validationErrors per scrivere
							// sul log il dettaglio dell'errore su righe successive.
							StringBuilder strLog = new StringBuilder(credenzialiUtente.getPrefissoLogger());
							strLog.append(" Errore nella validazione del messaggio ricevuto per la gestione di una istanza di Gara e lotti.");

							for (int i = 0; i < validationErrors.size(); i++) {
								strLog.append("\n" + validationErrors.get(i).getMessage());
							}
							logger.error(strLog);
						}

						// Costruzione del messaggio da ritornare al client
						StringBuilder strError = new StringBuilder("Errore di validazione del messaggio XML: \n");
						for (int i = 0; i < validationErrors.size(); i++) {
							strError.append(validationErrors.get(i).getMessage() + "\n");
						}

						result.setSuccess(false);
						result.setError(strError.toString());

					} else {
						GaraType oggettoGara = istanzaGaraDocument.getRichiestaSincronaIstanzaGara().getGara();

						// Gestione del RUP:
						String codiceChiaveRUP = this.rupManager.getRupStazioneAppaltante(result, codFiscaleStazAppaltante,
								credenzialiUtente, oggettoGara.getRup());

						if (result.isSuccess()) {
							// L’importazione viene eseguita solo se non esiste in banca dati un gara con l’ID GARA
							// specificato e un lotto con uno dei CIG indicati;

							Boolean isNuovaGara = true;
							// Controllo che non esista in banca dati un gara con l’ID GARA specificato
							if (this.geneManager.countOccorrenze("W9GARA", " IDAVGARA=? ", new Object[] { oggettoGara.getW3IDGARA() }) > 0) {
								// la gara esiste e va gestito l'aggiornamento
								isNuovaGara = false;
								
								// Controlli per Gara controllo il permesso di sovrascrittura
								boolean sovrascrivereDatiEsistenti = false;
								if (garaLotti.getTestata().getSOVRASCR() != null) {
									sovrascrivereDatiEsistenti = garaLotti.getTestata().getSOVRASCR().booleanValue();

									if (!(sovrascrivereDatiEsistenti)) {
										logger.error( 
												"Gara con IDAVGARA="+ oggettoGara.getW3IDGARA() + " gia' esistente. La scheda anagrafica gara-lotti e' stata inviata dall'utente "
												+ credenzialiUtente.getAccount().getNome() + " (USRSYS.SYSCON=" + credenzialiUtente.getAccount().getIdAccount()
												+ ") per la stazione appaltante " + credenzialiUtente.getStazioneAppaltante().getNome()
												+ " (UFFINT.CODEIN=" + credenzialiUtente.getStazioneAppaltante().getCodice()
												+ ", UFFINT.CFEIN=" + credenzialiUtente.getStazioneAppaltante().getCodFiscale()
												+ ", UFFINT.IVAEIN=" +  credenzialiUtente.getStazioneAppaltante().getPartitaIVA()
												+ ")");
										//result.setSuccess(false);
										//result.setError("Gara non creata perche' gia' esistente");
										throw new WSVigilanzaException("La gara " + oggettoGara.getW3IDGARA() + " e' gia' presente nel sistema di destinazione, non e' pertanto necessario l'invio");
									}
								}

								// controllo della corrispondeza tra la stazione appaltante con l'id della gara
								if (!UtilitySITAT.isGaraInStazioneAppaltante(codFiscaleStazAppaltante, oggettoGara.getW3IDGARA(), this.sqlManager)) {
									logger.error("Gara con IDAVGARA="+ oggettoGara.getW3IDGARA() + " scartata per incoerenza gara - stazione appaltante. "
											+ "La scheda anagrafica gara-lotti e' stata inviata dall'utente "
											+ credenzialiUtente.getAccount().getNome() + " (USRSYS.SYSCON=" + credenzialiUtente.getAccount().getIdAccount()
											+ ") per la stazione appaltante " + credenzialiUtente.getStazioneAppaltante().getNome()
											+ " (UFFINT.CODEIN=" + credenzialiUtente.getStazioneAppaltante().getCodice()
											+ ", UFFINT.CFEIN=" + credenzialiUtente.getStazioneAppaltante().getCodFiscale()
											+ ", UFFINT.IVAEIN=" +  credenzialiUtente.getStazioneAppaltante().getPartitaIVA()
											+ ")");
									//result.setSuccess(false);
									//result.setError("Dati scartati per incoerenza gara - stazione appaltante");
									throw new WSVigilanzaException("Dati scartati per incoerenza gara - stazione appaltante");
								}

								if (UtilitySITAT.isUtenteAmministratore(credenzialiUtente)) {
									if (oggettoGara.isSetRup() && 
											!UtilitySITAT.haveSameRUP(oggettoGara.getRup().getCFTEC1(), oggettoGara.getW3IDGARA(), this.sqlManager)) {
										// controllo della corrispondenza tra il RUP con l'id della gara, 
										// solo se l'utente e' un utente amministratore
										logger.error("Gara con IDAVGARA="+ oggettoGara.getW3IDGARA() + " scartata per incoerenza gara - RUP. "
												+ "La scheda anagrafica gara-lotti e' stata inviata dall'utente "
												+ credenzialiUtente.getAccount().getNome() + " (USRSYS.SYSCON=" + credenzialiUtente.getAccount().getIdAccount()
												+ ") per la stazione appaltante " + credenzialiUtente.getStazioneAppaltante().getNome()
												+ " (UFFINT.CODEIN=" + credenzialiUtente.getStazioneAppaltante().getCodice()
												+ ", UFFINT.CFEIN=" + credenzialiUtente.getStazioneAppaltante().getCodFiscale()
												+ ", UFFINT.IVAEIN=" +  credenzialiUtente.getStazioneAppaltante().getPartitaIVA()
												+ ")");
										//result.setSuccess(false);
										//result.setError("Dati scartati per incoerenza gara - RUP");
										throw new WSVigilanzaException("Dati scartati per incoerenza gara - RUP");
									}
								}
							}

							// se soddisfo le condizioni indicate per la gara procedo, se non presente
							// procedo con l'inserimento in caso contrario effettuo l'aggiornamento

							// recupero l'array con i lotti della gara inviata
							LottoType[] arrayLotti = oggettoGara.getListaLottiArray();
							// HashMap per caricare tutti i CIG dei vari lotti: serve per
							// determinare i casi di lotti con lo stesso CIG.
							HashMap<String, String> hmCigLotti = new HashMap<String, String>();
							Vector<String> vectorCigEsistenti = new Vector<String>();
							Vector<String> vectorCigDoppi = new Vector<String>();

							if (arrayLotti != null && arrayLotti.length > 0) {

								for (int i = 0; i < arrayLotti.length; i++) {
									LottoType lotto = arrayLotti[i];

									if (isNuovaGara) {
										if (this.geneManager.countOccorrenze("W9LOTT", " CIG=? ", new Object[] { lotto.getW3CIG() } ) > 0) {
											vectorCigEsistenti.add(lotto.getW3CIG());
										}
									} else {
										if (this.geneManager.countOccorrenze("W9LOTT", " CIG=? ", new Object[] { lotto.getW3CIG() } ) > 0) {
											// Verifica che il CIG non sia un lotto della gara
											if (!UtilitySITAT.isCigLottoDellaGara(oggettoGara.getW3IDGARA(), lotto.getW3CIG(), this.sqlManager)) {
												vectorCigEsistenti.add(lotto.getW3CIG());
											}
										}
									}
									if (hmCigLotti.containsKey(lotto.getW3CIG())) {
										vectorCigDoppi.add(lotto.getW3CIG());
									}
									hmCigLotti.put(lotto.getW3CIG(), lotto.getW3CIG());
								}
							}

							// verifico la presenza di Cig duplicati o essitenti in altre 
							if (vectorCigDoppi.size() > 0 || vectorCigEsistenti.size() > 0) {

								if (vectorCigDoppi.size() > 0) {
									StringBuilder msgControlloLogTemp = new StringBuilder(" CIG doppi = ");

									for (int vcd = 0; vcd < vectorCigDoppi.size(); vcd++) {
										msgControlloLogTemp.append(vectorCigDoppi.get(vcd));
										if (vcd + 1 != vectorCigDoppi.size()) {
											msgControlloLogTemp.append(", ");
										}
									}

									// se presenti riporto l'errore e blocco il processo
									logger.error("Gara con IDAVGARA="+ oggettoGara.getW3IDGARA() + " non importata perche' presenti piu' lotti con lo stesso CIG. "
										+ msgControlloLogTemp.toString()
										+ ". La scheda anagrafica gara-lotti e' stata inviata dall'utente "
										+ credenzialiUtente.getAccount().getNome() + " (USRSYS.SYSCON=" + credenzialiUtente.getAccount().getIdAccount()
										+ ") per la stazione appaltante " + credenzialiUtente.getStazioneAppaltante().getNome()
										+ " (UFFINT.CODEIN=" + credenzialiUtente.getStazioneAppaltante().getCodice()
										+ ", UFFINT.CFEIN=" + credenzialiUtente.getStazioneAppaltante().getCodFiscale()
										+ ", UFFINT.IVAEIN=" + credenzialiUtente.getStazioneAppaltante().getPartitaIVA()
										+ ")");

									throw new WSVigilanzaException("Gara non importata per la presenza di piu' lotti con lo stesso CIG");
								}
								
								// verifico la presenza di Cig esistenti
								if (vectorCigEsistenti.size() > 0) {

									// se la gara non esiste ma trovo dei cig esistenti, lancio un errore e blocco il processo
									if (isNuovaGara) {
										logger.error("Gara con IDAVGARA="+ oggettoGara.getW3IDGARA() + " non importata perche' uno o piu' CIG sono gia' "
												+ "presenti nella base dati di destinazione. La scheda anagrafica gara-lotti e' stata inviata dall'utente "
												+ credenzialiUtente.getAccount().getNome() + " (USRSYS.SYSCON=" + credenzialiUtente.getAccount().getIdAccount()
												+ ") per la stazione appaltante " + credenzialiUtente.getStazioneAppaltante().getNome()
												+ " (UFFINT.CODEIN=" + credenzialiUtente.getStazioneAppaltante().getCodice()
												+ ", UFFINT.CFEIN=" + credenzialiUtente.getStazioneAppaltante().getCodFiscale()
												+ ", UFFINT.IVAEIN=" +  credenzialiUtente.getStazioneAppaltante().getPartitaIVA()
												+ ")");
										throw new WSVigilanzaException("Gara non importata perche' uno o piu' CIG sono gia' presenti nella base dati di destinazione");
									} else {

										StringBuilder msgControlloLogTemp = new StringBuilder(" CIG esistenti = ");
										
										for (int i = 0; i < vectorCigEsistenti.size(); i++) {
											String cigEsistente = vectorCigEsistenti.get(i);
											
											// se la gara esiste, e posso eseguire un aggiornamento, ma uno dei cig appartiene ad 
											// un'altra gara lancio un errore e blocco il procedimento
											if (!UtilitySITAT.isCigLottoDellaGara(oggettoGara.getW3IDGARA(), cigEsistente, this.sqlManager)) {
												msgControlloLogTemp.append(vectorCigEsistenti.get(i));
												if (i + 1 != vectorCigEsistenti.size()) {
													msgControlloLogTemp.append(", ");
												}
											}
										}

										logger.error("Gara con IDAVGARA="+ oggettoGara.getW3IDGARA() + " non importata per incoerenza fra la Gara "
												+ "e i lotti: uno o piu' CIG sono associati ad una gara diversa. "
												+ msgControlloLogTemp.toString() + ". "
												+ "La scheda anagrafica gara-lotti e' stata inviata dall'utente "
												+ credenzialiUtente.getAccount().getNome() + " (USRSYS.SYSCON=" + credenzialiUtente.getAccount().getIdAccount()
												+ ") per la stazione appaltante " + credenzialiUtente.getStazioneAppaltante().getNome()
												+ " (UFFINT.CODEIN=" + credenzialiUtente.getStazioneAppaltante().getCodice()
												+ ", UFFINT.CFEIN=" + credenzialiUtente.getStazioneAppaltante().getCodFiscale()
												+ ", UFFINT.IVAEIN=" +  credenzialiUtente.getStazioneAppaltante().getPartitaIVA()
												+ ")");

										throw new WSVigilanzaException("Dati scartati per incoerenza ID GARA - Lotti (CIG esistenti e/o associati ad un'altra gara)");
									}
								}
							}

							// Controlli preliminari superati con successo.
							// Inizio inserimento dei dati della gara e dei lotti nella base dati.

							// ----------------------------------------------------
							// --- Inserimento del Centro di Costo (W9CCCODICE) ---
							// ----------------------------------------------------
							Long idCentroDiCosto = this.istanziaCentroCosto(credenzialiUtente.getStazioneAppaltante().getCodice(),
									oggettoGara.getCentroDiCosto());

							// ---------------------------------------------
							// ------- Inserimento della gara (W9GARA)------
							// ---------------------------------------------
							Long codgara = this.istanziaGara(credenzialiUtente, oggettoGara, codiceChiaveRUP, idCentroDiCosto,
									arrayLotti.length, isNuovaGara);

							// ------------------------------------------------------
							// --- Inserimento della pubblicita' di gara (W9PUBB) ---
							// ------------------------------------------------------
							this.istanziaPubblicita(codgara, oggettoGara.getPubblicitaGara());

							// ------------------------------------------------
							// --- Inserimento della documentazione di gara ---
							// ------------------------------------------------
							//this.istanziaDocGara(oggettoGara, codgara);
							
							// ----------------------------------------
							// ---- Inserimento dei lotti (W9LOTT) ----
							// ----------------------------------------
							LottoType[] listaLotti = oggettoGara.getListaLottiArray();
							ResponseLottoType[] arrayResponseLotto = new ResponseLottoType[listaLotti.length];
							
							int numeroLottiImportati = 0;
							for (int nlotti = 0; nlotti < listaLotti.length; nlotti++) {
								
								int numeroLotto = nlotti + 1;

								LottoType lottoSingolo = listaLotti[nlotti];
								String codiceCIG = lottoSingolo.getW3CIG();
								
								ResponseLottoType responseLotto = new ResponseLottoType();
								responseLotto.setCIG(codiceCIG);
								ResponseSchedaAType resultSchedaA = new ResponseSchedaAType();
								
								boolean importaLotto = true;
								String motivoDiNonImportazioneDelLotto = null;
								
								// Controlli sul singolo lotto:
								// se in uno dei lotti esiste una fase di aggiudicazione, 
								// aggiudicazione semplificata o adesione ad accordo quadro 
								if (UtilitySITAT.existsFase(codiceCIG, CostantiWSW9.AGGIUDICAZIONE_SOPRA_SOGLIA, this.sqlManager) 
										|| UtilitySITAT.existsFase(codiceCIG, CostantiWSW9.ADESIONE_ACCORDO_QUADRO, this.sqlManager) 
										|| UtilitySITAT.existsFase(codiceCIG, CostantiWSW9.FASE_SEMPLIFICATA_AGGIUDICAZIONE, this.sqlManager)) {
								
									// ed l’importo del lotto nel db (campo IMPORTO_TOT) è inferiore a 40.000 euro mentre
									// quello specificato nel file XML non lo è, o viceversa;
									
									double importoTotale = lottoSingolo.getW3ILOTTO() + lottoSingolo.getW3IATTSIC();
									if (logicalXOR(UtilitySITAT.isS2(codiceCIG, this.sqlManager),(importoTotale>=40000))) {
										importaLotto = false;
										motivoDiNonImportazioneDelLotto = "Lotto non importato per incoerenza nell'importo totale del lotto";
									}

									// ed il lotto nel db è una adesione ad accordo quadro senza successivo
									// confronto competitivo (TIPO_APP=11) mentre nel file XML non lo è, o viceversa
	
									if (importaLotto && logicalXOR(UtilitySITAT.isAAQ(codiceCIG, this.sqlManager),
											(oggettoGara.isSetW3TIPOAPP() && oggettoGara.getW3TIPOAPP().intValue() == UtilitySITAT.ADESIONE_ACCORDO_QUADRO_SENZA_CONFRONTO_COMPETITIVO.intValue()))) {
										importaLotto = false;
										motivoDiNonImportazioneDelLotto = "Lotto non importato per incoerenza di informazioni nella modalita' di realizzazione (W3TIPOAPP)";
									}
								}
								
								// Se in uno dei lotti esiste una fase di inizio contratto o stipula accordo quadro 
								if (importaLotto && (UtilitySITAT.existsFase(codiceCIG, CostantiWSW9.INIZIO_CONTRATTO_SOPRA_SOGLIA, this.sqlManager)
										|| UtilitySITAT.existsFase(codiceCIG, CostantiWSW9.STIPULA_ACCORDO_QUADRO, this.sqlManager)
										|| UtilitySITAT.existsFase(codiceCIG, CostantiWSW9.FASE_SEMPLIFICATA_INIZIO_CONTRATTO, this.sqlManager))) {
									
									// ed il lotto nel db è una stipula accordo quadro (TIPO_APP=9) 
									// mentre nel file XML non lo è, o viceversa
									if (logicalXOR(UtilitySITAT.isSAQ(codiceCIG, this.sqlManager),
											(oggettoGara.isSetW3TIPOAPP() && (oggettoGara.getW3TIPOAPP().intValue()== UtilitySITAT.STIPULA_ACCORDO_QUADRO.intValue() ||
													oggettoGara.getW3TIPOAPP().intValue()== UtilitySITAT.ACCORDO_QUADRO ||
													oggettoGara.getW3TIPOAPP().intValue()== UtilitySITAT.CONVENZIONE)))) {
										importaLotto = false;
										motivoDiNonImportazioneDelLotto = "Lotto non importato per incoerenza di informazioni nella modalita' di realizzazione (W3TIPOAPP)";
									}
								}

								if (importaLotto) {
									HashMap<String, HashMap<String, Object>> hmCig = this.backupDatiLotti(codgara);
									
									// --- Inserimento del lotto (W9LOTT) ---
									long codLott = this.istanziaLotto(codiceChiaveRUP, codgara, numeroLotto, listaLotti[nlotti], hmCig);
									// --- Inserimento delle categorie d'iscrizione (W9LOTTCATE) ---
									this.istanziaCategorie(codgara, codLott, listaLotti[nlotti]);
									// --- Inserimento delle condizioni (W9COND) ---
									this.istanziaCondizioneProceduraNegoziata(codgara, codLott, listaLotti[nlotti]);
									// --- Inserimento delle modalita' di acquisizione F/S (W9APPAFORN) ---
									this.istanziaModalitaAcquisizione(codgara, codLott, listaLotti[nlotti]);
									// --- Inserimento della tipologia di lavoro (W9APPALAV) ---
									this.istanziaTipologiaLavoro(codgara, codLott, listaLotti[nlotti]);
									// --- Inserimento CPV secondari (W9CPV) ---
									this.istanziaCPVSecondari(codgara, codLott, listaLotti[nlotti]);

									resultSchedaA.setSuccess(true);
									resultSchedaA.setMsgScheda(new String [] {"Lotto importato"} );

									numeroLottiImportati++;
								} else {
									// aggiunte informazioni di controllo nel messaggio di output
									StringBuilder msgControlloLogTemp = new StringBuilder(credenzialiUtente.getPrefissoLogger());
									msgControlloLogTemp.append("Lotto con CIG ='");
									msgControlloLogTemp.append(codiceCIG);
									msgControlloLogTemp.append("' - ");
									msgControlloLogTemp.append(motivoDiNonImportazioneDelLotto);
									logger.error(msgControlloLogTemp.toString());
									
									resultSchedaA.setSuccess(false);
									resultSchedaA.setMsgScheda(new String [] { motivoDiNonImportazioneDelLotto } );
								}
								responseLotto.setResultSchedaAType(resultSchedaA);
								arrayResponseLotto[nlotti] = responseLotto;
							}

							if (numeroLottiImportati < listaLotti.length) {
								this.sqlManager.update("update W9GARA set NLOTTI=? where CODGARA=?", new Object[] { numeroLottiImportati, codgara} );
							}
							
							if (UtilitySITAT.isScritturaNoteAvvisiAttiva()) {
								if (isNuovaGara) {
									UtilitySITAT.insertNoteAvvisi("W9GARA", codgara.toString(), null, null, null, null, 
											"Inserita nuova gara", "Inserita nuova gara (Id Gara: " + oggettoGara.getW3IDGARA() + ")",
											this.sqlManager);
								} else {
									UtilitySITAT.insertNoteAvvisi("W9GARA", codgara.toString(), null, null, null, null, 
											"Aggiornata gara", "Aggiornata gara (Id Gara: " + oggettoGara.getW3IDGARA() + ")",
											this.sqlManager);
								}
							}

							// Se si e' arrivati qui l'importazione dell'anagrafica gara e lotti e' terminata con successo 
							result.setSuccess(true);

							String strMsg = "Importazione anagrafica gara-lotti terminata con successo";
							if (numeroLottiImportati > 0) {
								if (listaLotti.length == 1) {
									if (numeroLottiImportati < 1) {
										result.setError(strMsg.concat(". Lotto non importato"));
									} else {
										result.setError(strMsg);
									}
								} else {
									if (numeroLottiImportati < listaLotti.length) {
										result.setError(strMsg.concat(". Importati " + numeroLottiImportati + " lotti su " + listaLotti.length));
									} else {
										result.setError(strMsg.concat(". Importati tutti i lotti").trim());
									}
								}
							} else {
								result.setError(strMsg.concat(". Nessun lotto importato").trim());
							}
							result.setResultLotti(arrayResponseLotto);
						}
					}
				} else {
					// E' stato inviato un messaggio di test.
					StringBuilder strLog = new StringBuilder(credenzialiUtente.getPrefissoLogger());
					strLog.append(" E' stato inviato un messaggio di test. Tale messaggio non e' stato elaborato.'\n");
					strLog.append("Messaggio: ");
					strLog.append(garaLotti.toString());
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
			logger.error("La verifica delle credenziali non e' stato superato. Messaggio di errore: " + result.getError());
		}

		if (logger.isDebugEnabled()) {
			logger.debug("istanziaGara: fine metodo");
			
			logger.debug("Response : " + UtilitySITAT.messaggioLogEsteso(result));
		}
	    
	  // MODIFICA TEMPORANEA -- DA CANCELLARE ---------------
	  result.setError(UtilitySITAT.messaggioEsteso(result));
	  // ----------------------------------------------------

		return result;
	}

	private Long istanziaGara(Credenziale credenzialiUtente, GaraType oggettoGara, String codiceChiaveRUP,
			Long idCentroDiCosto, int numeroLotti, boolean isNuovaGara) throws GestoreException, SQLException {

		Long codgara = null;

		if (!isNuovaGara) {
			codgara = UtilitySITAT.getCodGaraByIdAvGara(oggettoGara.getW3IDGARA(), this.sqlManager);
		}
		
		DataColumn codiceGara = new DataColumn("W9GARA.CODGARA", 
				new JdbcParametro(JdbcParametro.TIPO_NUMERICO, codgara));
		codiceGara.setChiave(true);

		DataColumnContainer dccGara = new DataColumnContainer(new DataColumn[] { codiceGara });

		if (oggettoGara.isSetW3OGGETTO1()) {
			dccGara.addColumn("W9GARA.OGGETTO", JdbcParametro.TIPO_TESTO, oggettoGara.getW3OGGETTO1());
		}

		dccGara.addColumn("W9GARA.IDAVGARA", JdbcParametro.TIPO_TESTO, oggettoGara.getW3IDGARA());

		if (oggettoGara.isSetW3IGARA()) {
			dccGara.addColumn("W9GARA.IMPORTO_GARA", JdbcParametro.TIPO_DECIMALE, oggettoGara.getW3IGARA());
		} else {
			dccGara.addColumn("W9GARA.IMPORTO_GARA", JdbcParametro.TIPO_DECIMALE, new Double(0));
		}

		dccGara.addColumn("W9GARA.NLOTTI", JdbcParametro.TIPO_NUMERICO, Long.parseLong("" + numeroLotti));

		if (oggettoGara.isSetW9GAFLAGENT()) {
			dccGara.addColumn("W9GARA.FLAG_ENTE_SPECIALE", JdbcParametro.TIPO_TESTO, oggettoGara.getW9GAFLAGENT().toString());
		}

		if (oggettoGara.isSetW9GAMODIND()) {
			dccGara.addColumn("W9GARA.ID_MODO_INDIZIONE", JdbcParametro.TIPO_NUMERICO, Long.parseLong(oggettoGara.getW9GAMODIND().toString()));
		}

		if (oggettoGara.isSetW3TIPOAPP()) {
			dccGara.addColumn("W9GARA.TIPO_APP", JdbcParametro.TIPO_NUMERICO, Long.parseLong(oggettoGara.getW3TIPOAPP().toString()));
		}
		
		dccGara.addColumn("W9GARA.CODEIN", JdbcParametro.TIPO_TESTO, credenzialiUtente.getStazioneAppaltante().getCodice());

		//if (oggettoGara.isSetW3DGURI()) {
		//	dccGara.addColumn("W9GARA.DGURI", JdbcParametro.TIPO_DATA, oggettoGara.getW3DGURI().getTime());
		//}
		//if (oggettoGara.isSetW3DSCADB()) {
		//	dccGara.addColumn("W9GARA.DSCADE", JdbcParametro.TIPO_DATA, oggettoGara.getW3DSCADB().getTime());
		//}

		dccGara.addColumn("W9GARA.SITUAZIONE", JdbcParametro.TIPO_NUMERICO, new Long(1));
		// FLAG_SA_AGENTE
		if (oggettoGara.isSetW3FLAGSA()) {
			dccGara.addColumn("W9GARA.FLAG_SA_AGENTE", JdbcParametro.TIPO_TESTO, oggettoGara.getW3FLAGSA() ? "1" : "2");
		} else {
			dccGara.addColumn("W9GARA.FLAG_SA_AGENTE", JdbcParametro.TIPO_TESTO, "2");
		}
		// ID_TIPOLOGIA_SA
		if (oggettoGara.isSetW3IDTIPOL()) {
			dccGara.addColumn("W9GARA.ID_TIPOLOGIA_SA", JdbcParametro.TIPO_NUMERICO, Long.parseLong(oggettoGara.getW3IDTIPOL().toString()));
		}
		// DENOM_SA_AGENTE
		if (oggettoGara.isSetW3GASAAGENTE()) {
			dccGara.addColumn("W9GARA.DENOM_SA_AGENTE", JdbcParametro.TIPO_TESTO, oggettoGara.getW3GASAAGENTE());
		}
		// CF_SA_AGENT
		if (oggettoGara.isSetW3GACFAGENTE()) {
			dccGara.addColumn("W9GARA.CF_SA_AGENTE", JdbcParametro.TIPO_TESTO, oggettoGara.getW3GACFAGENTE());
		}
		// TIPOLOGIA_PROCEDURA
		if (oggettoGara.isSetW9GATIPROC()) {
			dccGara.addColumn("W9GARA.TIPOLOGIA_PROCEDURA", JdbcParametro.TIPO_NUMERICO, Long.parseLong(oggettoGara.getW9GATIPROC().toString()));
		}
		// FLAG_CENTRALE_STIPULA
		if (oggettoGara.isSetW9GASTIPULA()) {
			dccGara.addColumn("W9GARA.FLAG_CENTRALE_STIPULA", JdbcParametro.TIPO_TESTO, oggettoGara.getW9GASTIPULA() ? "1" : "2");
		}

		// OGGETTO CENTRO COSTO
		if (oggettoGara.isSetCentroDiCosto()) {
			if (idCentroDiCosto != null) {
				dccGara.addColumn("W9GARA.IDCC", JdbcParametro.TIPO_NUMERICO, idCentroDiCosto);
			}
		}

		dccGara.addColumn("W9GARA.RIC_ALLUV", JdbcParametro.TIPO_TESTO, "2");

		if (StringUtils.isNotEmpty(codiceChiaveRUP)) {
			dccGara.addColumn("W9GARA.RUP", JdbcParametro.TIPO_TESTO, codiceChiaveRUP);
		}

		dccGara.addColumn("W9GARA.PROV_DATO", JdbcParametro.TIPO_NUMERICO, new Long(3));

		if (oggettoGara.isSetW9GACIGAQ()) {
			dccGara.addColumn("W9GARA.CIG_ACCQUADRO", JdbcParametro.TIPO_TESTO, oggettoGara.getW9GACIGAQ());
		}
		
		if (oggettoGara.isSetW3GASOMMUR()) {
			dccGara.addColumn("W9GARA.SOMMA_URGENZA", JdbcParametro.TIPO_TESTO, oggettoGara.getW3GASOMMUR() ? "1" : "2");
		}

		if (oggettoGara.isSetW9GADURACCQ()) {
			dccGara.addColumn("W9GARA.DURATA_ACCQUADRO", JdbcParametro.TIPO_NUMERICO, new Long(oggettoGara.getW9GADURACCQ()));
		}
		
		GregorianCalendar dataPubblicazioneBando = null;
		GregorianCalendar dataCreazioneGaraSimog = null;
		if (oggettoGara.isSetW9GADPUBB()) {
			dccGara.addColumn("W9GARA.DATA_PUBBLICAZIONE", JdbcParametro.TIPO_DATA, oggettoGara.getW9GADPUBB().getTime());
			dataPubblicazioneBando = new GregorianCalendar(oggettoGara.getW9GADPUBB().get(Calendar.YEAR), oggettoGara.getW9GADPUBB().get(Calendar.MONTH),
					oggettoGara.getW9GADPUBB().get(Calendar.DAY_OF_MONTH), 0, 0, 0);
		}
		
		if (oggettoGara.isSetW9GADSIMOG()) {
			dccGara.addColumn("W9GARA.DATA_CREAZIONE", JdbcParametro.TIPO_DATA, oggettoGara.getW9GADSIMOG().getTime());
			dataCreazioneGaraSimog = new GregorianCalendar(oggettoGara.getW9GADSIMOG().get(Calendar.YEAR), oggettoGara.getW9GADSIMOG().get(Calendar.MONTH),
					oggettoGara.getW9GADSIMOG().get(Calendar.DAY_OF_MONTH), 0, 0, 0);
		}
		
		int versioneSimog = UtilitySITAT.getVersioneSimog(dataCreazioneGaraSimog, dataPubblicazioneBando);
		dccGara.addColumn("W9GARA.VER_SIMOG", JdbcParametro.TIPO_NUMERICO, new Long(versioneSimog));
		
		// Inizializzazione dei campi CAM (Criteri Ambientali Minimi) e SISMA (eventi sismici maggio 2012)
		if (oggettoGara.isSetW9GACAM()) {
			dccGara.addColumn("W9GARA.CAM", JdbcParametro.TIPO_TESTO, oggettoGara.getW9GACAM() ? "1" : "2");

		}
		if (oggettoGara.isSetW9GASISMA()) {
			dccGara.addColumn("W9GARA.SISMA", JdbcParametro.TIPO_TESTO, oggettoGara.getW9GASISMA() ? "1" : "2");
		}
		
		synchronized (dccGara) {
			if (isNuovaGara) {
				codgara = new Long(this.genChiaviManager.getMaxId("W9GARA", "CODGARA") + 1);
				dccGara.getColumn("W9GARA.CODGARA").setObjectValue(codgara);
			} else {
				this.sqlManager.update("delete from W9GARA where CODGARA=?", new Object[] { codgara });
			}
			// Insert in W9GARA
			dccGara.insert("W9GARA", this.sqlManager);
		}
		return codgara;
	}

	private boolean logicalXOR(boolean x, boolean y) {
	    return ( ( x || y ) && ! ( x && y ) );
	}

	private long istanziaLotto(String codiceChiaveRUP, Long codGara, int numeroLotto, LottoType lotto,
			HashMap<String, HashMap<String, Object>> hmCig) throws GestoreException, SQLException {

		boolean isNuovoLotto = true;
		Long codLott = new Long(-1);  // valore temporaneo
		
		if (hmCig.containsKey(lotto.getW3CIG())) {
			isNuovoLotto = false;
			codLott = (Long) hmCig.get(lotto.getW3CIG()).get("CODLOTT");
		}
		
		DataColumnContainer dccLotto = new DataColumnContainer(new DataColumn[] { 
				new DataColumn("W9LOTT.CODGARA", new JdbcParametro(JdbcParametro.TIPO_NUMERICO, codGara)),
				new DataColumn("W9LOTT.CODLOTT", new JdbcParametro(JdbcParametro.TIPO_NUMERICO, codLott)),
				new DataColumn("W9LOTT.CIG", new JdbcParametro(JdbcParametro.TIPO_TESTO, lotto.getW3CIG())),
				new DataColumn("W9LOTT.ID_SCHEDA_LOCALE", new JdbcParametro(JdbcParametro.TIPO_TESTO, lotto.getW3CIG()))});

		if (lotto.isSetW3OGGETTO2()) {
			dccLotto.addColumn("W9LOTT.OGGETTO", JdbcParametro.TIPO_TESTO, lotto.getW3OGGETTO2());
		}

		if (lotto.isSetW9CUIINT()) {
			dccLotto.addColumn("W9LOTT.CUIINT", JdbcParametro.TIPO_TESTO, lotto.getW9CUIINT());
		}
		
		double importoTotaleLotto = Double.MIN_VALUE;
		double importoLotto = Double.MIN_VALUE;
		if (lotto.isSetW3ILOTTO()) {
			dccLotto.addColumn("W9LOTT.IMPORTO_LOTTO", JdbcParametro.TIPO_DECIMALE, lotto.getW3ILOTTO());
			if (lotto.getW3ILOTTO() > 0) {
				importoTotaleLotto = lotto.getW3ILOTTO();
				importoLotto = lotto.getW3ILOTTO();
			}
		}

		if (lotto.isSetW3IATTSIC() && lotto.getW3IATTSIC() > 0) {
			dccLotto.addColumn("W9LOTT.IMPORTO_ATTUAZIONE_SICUREZZA", JdbcParametro.TIPO_DECIMALE, lotto.getW3IATTSIC());
			if (importoTotaleLotto != Double.MIN_VALUE) {
				importoTotaleLotto += lotto.getW3IATTSIC();
			} else {
				importoTotaleLotto = lotto.getW3IATTSIC();
			}
		} else {
			dccLotto.addColumn("W9LOTT.IMPORTO_ATTUAZIONE_SICUREZZA", JdbcParametro.TIPO_DECIMALE, new Double(0));
		}

		if (importoTotaleLotto != Double.MIN_VALUE) {
			dccLotto.addColumn("W9LOTT.IMPORTO_TOT", JdbcParametro.TIPO_DECIMALE, importoTotaleLotto);
		}
		if (lotto.isSetW3CPV()) {
			dccLotto.addColumn("W9LOTT.CPV", JdbcParametro.TIPO_TESTO, lotto.getW3CPV());
		}
		if (lotto.isSetW3IDSCEL2()) {
			dccLotto.addColumn("W9LOTT.ID_SCELTA_CONTRAENTE", JdbcParametro.TIPO_NUMERICO, Long.parseLong(lotto.getW3IDSCEL2().toString()));
		}
		if (lotto.isSetW3IDCATE4()) {
			dccLotto.addColumn("W9LOTT.ID_CATEGORIA_PREVALENTE", JdbcParametro.TIPO_TESTO, 
					StringUtils.remove(lotto.getW3IDCATE4().toString(),"-"));
		}
		if (lotto.isSetW3CLASCAT()) {
			dccLotto.addColumn("W9LOTT.CLASCAT", JdbcParametro.TIPO_TESTO, lotto.getW3CLASCAT().toString());
		}
		if (lotto.isSetW3MANOLO()) {
			dccLotto.addColumn("W9LOTT.MANOD", JdbcParametro.TIPO_TESTO, lotto.getW3MANOLO() ? "1" : "2");
		}
		if (lotto.isSetW3TIPOCON()) {
			dccLotto.addColumn("W9LOTT.TIPO_CONTRATTO", JdbcParametro.TIPO_TESTO, lotto.getW3TIPOCON().toString());
			if ("L".equals(lotto.getW3TIPOCON().toString())) {
				dccLotto.addColumn("W9LOTT.MANOD", JdbcParametro.TIPO_TESTO, "1");
			} else if (lotto.isSetW3MANOLO()) {
				dccLotto.addColumn("W9LOTT.MANOD", JdbcParametro.TIPO_TESTO, lotto.getW3MANOLO() ? "1" : "2");
			}
		}
		if (lotto.isSetW3MODGAR()) {
			dccLotto.addColumn("W9LOTT.ID_MODO_GARA", JdbcParametro.TIPO_NUMERICO, Long.parseLong(lotto.getW3MODGAR().toString()));
		}
		if (lotto.isSetW3LUOGOIS()) {
			dccLotto.addColumn("W9LOTT.LUOGO_ISTAT", JdbcParametro.TIPO_TESTO, lotto.getW3LUOGOIS());
		}
		if (lotto.isSetW3LUOGONU()) {
			dccLotto.addColumn("W9LOTT.LUOGO_NUTS", JdbcParametro.TIPO_TESTO, lotto.getW3LUOGONU());
		}
		if (lotto.isSetW3IDTIPO()) {
			dccLotto.addColumn("W9LOTT.ID_TIPO_PRESTAZIONE", JdbcParametro.TIPO_NUMERICO, Long.parseLong(lotto.getW3IDTIPO().toString()));
		}
		if (lotto.isSetW3FLAGENT()) {
			dccLotto.addColumn("W9LOTT.FLAG_ENTE_SPECIALE", JdbcParametro.TIPO_TESTO, lotto.getW3FLAGENT().toString());
		}
		if (lotto.isSetW3CUP()) {
			dccLotto.addColumn("W9LOTT.CUP", JdbcParametro.TIPO_TESTO, lotto.getW3CUP());
			dccLotto.addColumn("W9LOTT.CUPESENTE", JdbcParametro.TIPO_TESTO, "2");
		} else {
			dccLotto.addColumn("W9LOTT.CUPESENTE", JdbcParametro.TIPO_TESTO, "1");
		}
		if (lotto.isSetW3NLOTTO()) {
			dccLotto.addColumn("W9LOTT.NLOTTO", JdbcParametro.TIPO_NUMERICO, new Long(lotto.getW3NLOTTO()));
		} else {
			dccLotto.addColumn("W9LOTT.NLOTTO", JdbcParametro.TIPO_NUMERICO, codLott);
		}

		dccLotto.addColumn("W9LOTT.SITUAZIONE", JdbcParametro.TIPO_NUMERICO, new Long(1));
		dccLotto.addColumn("W9LOTT.COMCON", JdbcParametro.TIPO_TESTO, "2");

		Long versioneSimog = (Long) this.sqlManager.getObject(
				"select VER_SIMOG from W9GARA where CODGARA=?", new Object[] { codGara });
		if (lotto.isSetW3LOARTE1() && (versioneSimog != null && versioneSimog.intValue() == 1)) {
			dccLotto.addColumn("W9LOTT.ART_E1", JdbcParametro.TIPO_TESTO, lotto.getW3LOARTE1() ? "1" : "2");
		} else {
			dccLotto.addColumn("W9LOTT.ART_E1", JdbcParametro.TIPO_TESTO, "2");
		}
		
		dccLotto.addColumn("W9LOTT.DAEXPORT", JdbcParametro.TIPO_TESTO, "2");
		if (StringUtils.isNotEmpty(codiceChiaveRUP)) {
			dccLotto.addColumn("W9LOTT.RUP", JdbcParametro.TIPO_TESTO, codiceChiaveRUP);
		}
		
		Date dataPubblicazione = (Date) this.sqlManager.getObject(
				"select DATA_PUBBLICAZIONE from W9GARA where CODGARA=?", new Object[] { codGara });
		if (importoLotto != Double.MIN_VALUE && dataPubblicazione != null) {
			GregorianCalendar temp = new GregorianCalendar(2013,9,29,23,59,59);
			if (dataPubblicazione.before(temp.getTime()) && importoLotto < 150000) {
				dccLotto.addColumn("W9LOTT.EXSOTTOSOGLIA", JdbcParametro.TIPO_TESTO, "1");
			} else {
				dccLotto.addColumn("W9LOTT.EXSOTTOSOGLIA", JdbcParametro.TIPO_TESTO, "2");
			}
		}

		synchronized (lotto) {
			if (!isNuovoLotto) {
				// dal backup dei dati del lotto in aggiornamento si valorizzano i seguenti campi
				// CODCUI, ID_SCHEDA_SIMOG, ID_SCHEDA_LOCALE
				dccLotto.addColumn("W9LOTT.CODCUI", JdbcParametro.TIPO_TESTO, hmCig.get(lotto.getW3CIG()).get("CODCUI"));
				dccLotto.addColumn("W9LOTT.ID_SCHEDA_SIMOG", JdbcParametro.TIPO_TESTO, 
						hmCig.get(lotto.getW3CIG()).get("ID_SCHEDA_SIMOG"));
				dccLotto.addColumn("W9LOTT.ID_SCHEDA_LOCALE", JdbcParametro.TIPO_TESTO,
						hmCig.get(lotto.getW3CIG()).get("ID_SCHEDA_LOCALE"));
				dccLotto.setValue("W9LOTT.CODLOTT", hmCig.get(lotto.getW3CIG()).get("CODLOTT"));
				this.sqlManager.update("delete from W9LOTT where CODGARA=? and CODLOTT=?", new Object[] { codGara, codLott });
			} else {
				int numLot = this.genChiaviManager.getMaxId("W9LOTT", "CODLOTT", " CODGARA=" + codGara)+1;
				dccLotto.setValue("W9LOTT.CODLOTT", new Long(numLot));
				dccLotto.setValue("W9LOTT.NLOTTO", new Long(numLot));
				codLott = new Long(numLot);
			}
		
			// Insert in W9LOTT
			dccLotto.insert("W9LOTT", this.sqlManager);
			// Allienamento del campo FLAG_ENTE_SPECIALE di W9LOTT con quello di W9GARA
			this.sqlManager.update("update W9LOTT set FLAG_ENTE_SPECIALE=(select FLAG_ENTE_SPECIALE from W9GARA where CODGARA=?) " +
					" where CODGARA=? and CODLOTT=?", new Object[] { codGara, codGara, codLott }); 
		}
		return codLott.longValue();
	}

	/**
	 * 
	 * @param codiceUffInt
	 * @param centroCosto
	 * @return
	 * @throws GestoreException
	 * @throws SQLException
	 */
	private Long istanziaCentroCosto(String codiceUffInt, CentroCostoType centroCosto) throws GestoreException, SQLException {

		Long idCentro = null;
		if (centroCosto != null) {
			// se il codice del centro di costo e' settato
			if (centroCosto.isSetW9CCCODICE()) {
				Long idCentroFromDB = (Long) this.sqlManager.getObject(
						"select IDCENTRO from CENTRICOSTO where CODCENTRO = ?", new Object[] { centroCosto.getW9CCCODICE() });
				// se l'id estratto e' presente provvedo con un update
				if (idCentroFromDB != null) {
					DataColumnContainer dccCentroCostoFromDB = new DataColumnContainer(new DataColumn[] { new DataColumn("CENTRICOSTO.IDCENTRO", new JdbcParametro(JdbcParametro.TIPO_NUMERICO, idCentroFromDB)) });
					dccCentroCostoFromDB.addColumn("CENTRICOSTO.CODCENTRO", JdbcParametro.TIPO_TESTO, centroCosto.getW9CCCODICE());
					DataColumn idCentroCosto = dccCentroCostoFromDB.getColumn("CENTRICOSTO.IDCENTRO");
					idCentroCosto.setChiave(true);
					idCentroCosto.setOriginalValue(idCentroCosto.getValue());
					
					if (centroCosto.isSetW9CCDENOM()) {
						dccCentroCostoFromDB.addColumn("CENTRICOSTO.DENOMCENTRO", JdbcParametro.TIPO_TESTO, centroCosto.getW9CCDENOM());
					}
					// Update del Record
					dccCentroCostoFromDB.update("CENTRICOSTO", this.sqlManager);
					return idCentroFromDB;
				}
			}

			// in caso non fosse presente preparo il mio data column container
			// per l'inserimento
			DataColumnContainer dccCentroCosto = new DataColumnContainer(new DataColumn[] { new DataColumn("CENTRICOSTO.IDCENTRO", new JdbcParametro(JdbcParametro.TIPO_NUMERICO, null)) });

			dccCentroCosto.addColumn("CENTRICOSTO.CODEIN", JdbcParametro.TIPO_TESTO, codiceUffInt);

			if (centroCosto.isSetW9CCCODICE()) {
				dccCentroCosto.addColumn("CENTRICOSTO.CODCENTRO", JdbcParametro.TIPO_TESTO, centroCosto.getW9CCCODICE());
			}
			if (centroCosto.isSetW9CCDENOM()) {
				dccCentroCosto.addColumn("CENTRICOSTO.DENOMCENTRO", JdbcParametro.TIPO_TESTO, centroCosto.getW9CCDENOM());
			}

			synchronized (dccCentroCosto) {
				// qui calcolo il max + 1
				idCentro = new Long(this.genChiaviManager.getMaxId("CENTRICOSTO", "IDCENTRO") + 1);
				dccCentroCosto.getColumn("CENTRICOSTO.IDCENTRO").setObjectValue(idCentro);

				// Insert del Record
				dccCentroCosto.insert("CENTRICOSTO", this.sqlManager);
			}
		}
		return idCentro;
	}

	private void istanziaPubblicita(Long codgara, PubblicitaGaraType pubblicitaGara) throws GestoreException, SQLException {
		
		boolean esistePubblicita = true;
		if (this.geneManager.countOccorrenze("W9PUBB", " CODGARA=? and CODLOTT=1 and NUM_APPA=1 and NUM_PUBB=1",
				new Object[] { codgara }) == 0) {
			esistePubblicita = false;
		}
		
		DataColumnContainer dccPubblicazioni = new DataColumnContainer(new DataColumn[] { 
				new DataColumn("W9PUBB.CODGARA", new JdbcParametro(JdbcParametro.TIPO_NUMERICO, codgara)),
				new DataColumn("W9PUBB.CODLOTT", new JdbcParametro(JdbcParametro.TIPO_NUMERICO, new Long(1))), 
				new DataColumn("W9PUBB.NUM_APPA", new JdbcParametro(JdbcParametro.TIPO_NUMERICO, new Long(1))),
				new DataColumn("W9PUBB.NUM_PUBB", new JdbcParametro(JdbcParametro.TIPO_NUMERICO, new Long(1))) });

		if (pubblicitaGara.isSetW3GUCE1()) {
			dccPubblicazioni.addColumn("W9PUBB.DATA_GUCE", JdbcParametro.TIPO_DATA, pubblicitaGara.getW3GUCE1().getTime());
		}
		if (pubblicitaGara.isSetW3GURI1()) {
			dccPubblicazioni.addColumn("W9PUBB.DATA_GURI", JdbcParametro.TIPO_DATA, pubblicitaGara.getW3GURI1().getTime());
		}
		if (pubblicitaGara.isSetW3ALBO1()) {
			dccPubblicazioni.addColumn("W9PUBB.DATA_ALBO", JdbcParametro.TIPO_DATA, pubblicitaGara.getW3ALBO1().getTime());
		}
		if (pubblicitaGara.isSetW3NAZ1()) {
			dccPubblicazioni.addColumn("W9PUBB.QUOTIDIANI_NAZ", JdbcParametro.TIPO_NUMERICO, new Long(pubblicitaGara.getW3NAZ1()));
		}
		if (pubblicitaGara.isSetW3REG1()) {
			dccPubblicazioni.addColumn("W9PUBB.QUOTIDIANI_REG", JdbcParametro.TIPO_NUMERICO, new Long(pubblicitaGara.getW3REG1()));
		}
		if (pubblicitaGara.isSetW3PROFILO1()) {
			dccPubblicazioni.addColumn("W9PUBB.PROFILO_COMMITTENTE", JdbcParametro.TIPO_TESTO, pubblicitaGara.getW3PROFILO1() ? "1" : "2");
		}
		if (pubblicitaGara.isSetW3MIN1()) {
			dccPubblicazioni.addColumn("W9PUBB.SITO_MINISTERO_INF_TRASP", JdbcParametro.TIPO_TESTO, pubblicitaGara.getW3MIN1() ? "1" : "2");
		}
		if (pubblicitaGara.isSetW3OSS1()) {
			dccPubblicazioni.addColumn("W9PUBB.SITO_OSSERVATORIO_CP", JdbcParametro.TIPO_TESTO, pubblicitaGara.getW3OSS1() ? "1" : "2");
		}
		if (pubblicitaGara.isSetW3BORE()) {
			dccPubblicazioni.addColumn("W9PUBB.DATA_BORE", JdbcParametro.TIPO_DATA, pubblicitaGara.getW3BORE().getTime());
		}
		if (pubblicitaGara.isSetW3PERIODIC()) {
			dccPubblicazioni.addColumn("W9PUBB.PERIODICI", JdbcParametro.TIPO_NUMERICO, new Long(pubblicitaGara.getW3PERIODIC()));
		}

		synchronized (dccPubblicazioni) {
			if (esistePubblicita) {
				this.sqlManager.update("delete from W9PUBB where CODGARA=? and CODLOTT=1 and NUM_APPA=1 and NUM_PUBB=1", 
						new Object[] { codgara });
			}

			// Insert in W9PUBB
			dccPubblicazioni.insert("W9PUBB", this.sqlManager);
		}
	}

	private void istanziaTipologiaLavoro(Long codGara, long codLott, LottoType lotto) throws GestoreException, SQLException {
		this.sqlManager.update("delete from W9APPALAV where CODGARA=? and CODLOTT=?", new Object[] { codGara, codLott });

		TipologiaLavoroType[] listaTipologiaLavoro = lotto.getListaTipologiaLavoroArray();
		if (listaTipologiaLavoro != null && listaTipologiaLavoro.length > 0) {
			TipologiaLavoroType tipologiaLavoro = null;
			for (int j = 0; j < listaTipologiaLavoro.length; j++) {
				tipologiaLavoro = listaTipologiaLavoro[j];
				DataColumnContainer dccAppaLav = new DataColumnContainer(new DataColumn[] {
						new DataColumn("W9APPALAV.CODGARA", new JdbcParametro(JdbcParametro.TIPO_NUMERICO, codGara)),
						new DataColumn("W9APPALAV.CODLOTT", new JdbcParametro(JdbcParametro.TIPO_NUMERICO, codLott)),
						new DataColumn("W9APPALAV.NUM_APPAL", new JdbcParametro(JdbcParametro.TIPO_NUMERICO, j + 1)) });
				dccAppaLav.addColumn("W9APPALAV.ID_APPALTO", JdbcParametro.TIPO_NUMERICO, 
						Long.parseLong(tipologiaLavoro.getW3IDAPP05().toString()));

				// Insert in W9APPALAV
				dccAppaLav.insert("W9APPALAV", this.sqlManager);
			}
		}
	}

	private void istanziaModalitaAcquisizione(Long codGara, long codLott, LottoType lotto) throws GestoreException, SQLException {
		this.sqlManager.update("delete from W9APPAFORN where CODGARA=? and CODLOTT=?", new Object[] { codGara, codLott });
		
		ModalitaAcquisizioneFSType[] listaModalitaAcqFS = lotto.getListaModalitaAcquisizioneFSArray();
		if (listaModalitaAcqFS != null && listaModalitaAcqFS.length > 0) {
			ModalitaAcquisizioneFSType modalitaAcqFS = null;
			for (int j = 0; j < listaModalitaAcqFS.length; j++) {
				modalitaAcqFS = listaModalitaAcqFS[j];
				DataColumnContainer dccAppaForn = new DataColumnContainer(new DataColumn[] { 
						new DataColumn("W9APPAFORN.CODGARA", new JdbcParametro(JdbcParametro.TIPO_NUMERICO, codGara)),
						new DataColumn("W9APPAFORN.CODLOTT", new JdbcParametro(JdbcParametro.TIPO_NUMERICO, codLott)), 
						new DataColumn("W9APPAFORN.NUM_APPAF", new JdbcParametro(JdbcParametro.TIPO_NUMERICO, j + 1)) });
				dccAppaForn.addColumn("W9APPAFORN.ID_APPALTO", JdbcParametro.TIPO_NUMERICO, 
						Long.parseLong(modalitaAcqFS.getW3IDAPP04().toString()));

				// Insert in W9APPAFORN
				dccAppaForn.insert("W9APPAFORN", this.sqlManager);
			}
		}
	}

	private void istanziaCategorie(Long codGara, long codLott, LottoType lotto) throws GestoreException, SQLException {

		CategoriaType[] listaCategorie = lotto.getListaCategorieArray();

		this.sqlManager.update("delete from W9LOTTCATE where CODGARA=? and CODLOTT=?", new Object[] { codGara, codLott });
		if (listaCategorie != null && listaCategorie.length > 0) {
			CategoriaType categoria = null;
			for (int j = 0; j < listaCategorie.length; j++) {
				categoria = listaCategorie[j];
				DataColumnContainer dccLottCate = new DataColumnContainer(new DataColumn[] { 
						new DataColumn("W9LOTTCATE.CODGARA", new JdbcParametro(JdbcParametro.TIPO_NUMERICO, codGara)),
						new DataColumn("W9LOTTCATE.CODLOTT", new JdbcParametro(JdbcParametro.TIPO_NUMERICO, codLott)), 
						new DataColumn("W9LOTTCATE.NUM_CATE", new JdbcParametro(JdbcParametro.TIPO_NUMERICO, j + 1)) });
				if (categoria.isSetW3CATEGORI()) {
					dccLottCate.addColumn("W9LOTTCATE.CATEGORIA", JdbcParametro.TIPO_TESTO, 
							StringUtils.remove(categoria.getW3CATEGORI().toString(),"-"));
				}
				if (categoria.isSetW3CLASCATCA()) {
					dccLottCate.addColumn("W9LOTTCATE.CLASCAT", JdbcParametro.TIPO_TESTO, categoria.getW3CLASCATCA().toString());
				}
				if (categoria.isSetW9LCSCORPORA()) {
					dccLottCate.addColumn("W9LOTTCATE.SCORPORABILE", JdbcParametro.TIPO_TESTO, categoria.getW9LCSCORPORA() ? "1" : "2");
				}
				if (categoria.isSetW9LCSUBAPPAL()) {
					dccLottCate.addColumn("W9LOTTCATE.SUBAPPALTABILE", JdbcParametro.TIPO_TESTO, categoria.getW9LCSUBAPPAL() ? "1" : "2");
				}

				// Insert in W9LOTTCATE
				dccLottCate.insert("W9LOTTCATE", this.sqlManager);
			}
		}
	}

	private void istanziaCPVSecondari(Long codGara, long codLott, LottoType lotto) throws GestoreException, SQLException {
		this.sqlManager.update("delete from W9CPV where CODGARA=? and CODLOTT=?", new Object[] { codGara, codLott });

		String[] listaCpvSecondari = lotto.getListaCpvSecondariArray();
		if (listaCpvSecondari != null && listaCpvSecondari.length > 0) {
			String cpvSecondario = null;

			for (int j = 0; j < listaCpvSecondari.length; j++) {
				cpvSecondario = listaCpvSecondari[j];
				DataColumnContainer dccAppaLav = new DataColumnContainer(new DataColumn[] {
						new DataColumn("W9CPV.CODGARA", new JdbcParametro(JdbcParametro.TIPO_NUMERICO, codGara)),
						new DataColumn("W9CPV.CODLOTT", new JdbcParametro(JdbcParametro.TIPO_NUMERICO, codLott)),
						new DataColumn("W9CPV.NUM_CPV", new JdbcParametro(JdbcParametro.TIPO_NUMERICO, j + 1)) });
				dccAppaLav.addColumn("W9CPV.CPV", JdbcParametro.TIPO_TESTO,cpvSecondario);

				// Insert in W9CPV
				dccAppaLav.insert("W9CPV", this.sqlManager);
			}
		}
	}
	
	private void istanziaCondizioneProceduraNegoziata(Long codGara, long codLott, LottoType lotto) throws GestoreException, SQLException {
		this.sqlManager.update("delete from W9COND where CODGARA=? and CODLOTT=?", new Object[] { codGara, codLott });
		CondizioneProcNegType[] listaCondizProcNeg = lotto.getListaCondizioniArray();

		if (listaCondizProcNeg != null && listaCondizProcNeg.length > 0) {
			CondizioneProcNegType condizioneProcNeg = null;
			for (int j = 0; j < listaCondizProcNeg.length; j++) {
				condizioneProcNeg = listaCondizProcNeg[j];
				DataColumnContainer dccCond = new DataColumnContainer(new DataColumn[] {
						new DataColumn("W9COND.CODGARA", new JdbcParametro(JdbcParametro.TIPO_NUMERICO, codGara)),
						new DataColumn("W9COND.CODLOTT", new JdbcParametro(JdbcParametro.TIPO_NUMERICO, codLott)),
						new DataColumn("W9COND.NUM_COND", new JdbcParametro(JdbcParametro.TIPO_NUMERICO, j + 1)) });
				dccCond.addColumn("W9COND.ID_CONDIZIONE", JdbcParametro.TIPO_NUMERICO,
						Long.parseLong(condizioneProcNeg.getW3IDCONDI().toString()));

				// Insert in W9COND
				dccCond.insert("W9COND", this.sqlManager);
			}
		}
	}

	
  /**
   * In caso di aggiornamento dei lotti (e quindi in caso di cancellazione e reinserimento dei lotti),
   * si salvano alcuni campi dei lotti inserendoli in una mappa, la cui chiave e' il codice CIG di
   * ciascun lotto. 
   * I campi del lotto salvati sono: CODLOTT, CIG, CODCUI, ID_SCHEDA_SIMOG e ID_SCHEDA_LOCALE.
   * 
   * @param codgara
   * @return Ritorna una 
   * @throws SQLException
   */
  private HashMap<String, HashMap<String, Object>> backupDatiLotti(Long codgara) throws SQLException {
    List< ? > listaCodLottCigCodCUI = this.sqlManager.getListVector(
        "select CODLOTT, CIG, CODCUI, ID_SCHEDA_SIMOG, ID_SCHEDA_LOCALE "
        + "from W9LOTT where CODGARA=? ORDER by CODLOTT ASC", new Object[] { codgara });
    
    HashMap<String, HashMap<String, Object>> hmCig = new HashMap<String, HashMap<String, Object>>();
    for (int y = 0; y < listaCodLottCigCodCUI.size(); y++) {
      Vector< ? > vector = (Vector< ? >) listaCodLottCigCodCUI.get(y);
      
      Long codLott = (Long) ((JdbcParametro) vector.get(0)).getValue();
      String codiceCig = (String) ((JdbcParametro) vector.get(1)).getValue();
      String codiceCUI = (String) ((JdbcParametro) vector.get(2)).getValue();
      String idSchedaSimog = (String) ((JdbcParametro) vector.get(3)).getValue();
      String idSchedaLocale = (String) ((JdbcParametro) vector.get(4)).getValue();

      HashMap<String, Object> hmObjects = new HashMap<String, Object>();
      hmObjects.put("CODLOTT", codLott);
      
      if (StringUtils.isNotEmpty(codiceCUI)) {
        hmObjects.put("CODCUI", codiceCUI);
      }
      
      if (StringUtils.isNotEmpty(idSchedaSimog)) {
        hmObjects.put("ID_SCHEDA_SIMOG", idSchedaSimog);
      }
      
      if (StringUtils.isNotEmpty(idSchedaLocale)) {
        hmObjects.put("ID_SCHEDA_LOCALE", idSchedaLocale);
      }       
      hmCig.put(codiceCig, hmObjects);
    }
    return hmCig;
	}

}
