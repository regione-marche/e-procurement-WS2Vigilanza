<%
/*
 * Created on 12-set-2006
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
 
 // PAGINA CHE CONTIENE L'ISTANZA DELLA SOTTOPARTE DELLA PAGINA DI LISTA TITOLI COLONNE
 // DI UNA RICERCA (IN FASE DI VISUALIZZAZIONE) CONTENENTE LA SEZIONE JAVASCRIPT
%>

<%@ taglib uri="http://www.eldasoft.it/tags" prefix="elda" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<fmt:setBundle basename="AliceResources" />

<script type="text/javascript">
<!--

	function generaPopupListaOpzioniRecord(id) {
	<elda:jsBodyPopup varJS="linkset" contextPath="${pageContext.request.contextPath}">
	<elda:jsVocePopup functionJS="modifica('\"+id+\"')" descrizione="Modifica dettaglio"/>
	<elda:jsVocePopup functionJS="elimina('\"+id+\"')" descrizione="Elimina"/>
	<elda:jsVocePopup functionJS="spostaSu('\"+id+\"')" descrizione="Sposta su"/>
	<elda:jsVocePopup functionJS="spostaGiu('\"+id+\"')" descrizione="Sposta gi�"/>
	<elda:jsVocePopup functionJS="spostaInPosizioneMarcata('\"+id+\"')" descrizione="Sposta in posizione marcata"/>
	</elda:jsBodyPopup>
		return linkset;
	}
	// Azioni invocate dal menu contestuale

	function apriTrovaRicerche(){
		//<c:if test="${!empty (sessionScope.recordDettModificato)}">
		//if (confirm('<fmt:message key="info.genRic.salvaDati.confirm"/>')) salvaRicercaETrovaRicerche();
		//else
		//</c:if>
		document.location.href='InitTrovaRicerche.do';
	}
	
	function salvaRicercaETrovaRicerche() {
		bloccaRichiesteServer();
		document.location.href='DettaglioRicerca.do?metodo=salvaETrova&tab=LAY';
	}
	
	function listaRicerche(){
		document.location.href='TrovaRicerche.do?metodo=trovaRicerche';
	}
	
	function salvaRicercaEListaRicerche() {
		bloccaRichiesteServer();
		document.location.href='DettaglioRicerca.do?metodo=salvaELista&tab=LAY';
	}
	
	function creaNuovaRicerca(){
		document.location.href='CreaNuovaRicerca.do';
		//document.location.href='DettaglioRicerca.do?metodo=crea';
	}
	
	function salvaRicercaECreaRicerca() {
		bloccaRichiesteServer();
		document.location.href='DettaglioRicerca.do?metodo=salvaECrea&tab=LAY';
	}
	
	// Azioni invocate dal tab menu

	function cambiaTab(codiceTab) {
		document.location.href = 'CambiaTabRicercaBase.do?tab=' + codiceTab;
	}

	// Azioni invocate dalle opzioni sul singolo record
	
	function modifica(id){
		var metodo = "modifica";
		document.location.href = 'ListaTitoliCampiRicercaBase.do?metodo=' + metodo + '&id=' + id;
	}

	function modificaTutti(){
		document.location.href = 'EditTitoliCampiRicerca.do';
	}

	function elimina(id) {
		if(confirm("Attenzione l'eliminazione del record comporta la\ncancellazione del campo dalla ricerca.\nProcedere con l'eliminazione del record?")){
			inviaRichiesta(id, "elimina");
		}
	}
	
	function spostaSu(id){
		inviaRichiesta(id, "spostaSu");
	}

	function spostaGiu(id){
		inviaRichiesta(id, "spostaGiu");
	}

	function inviaRichiesta(id, metodo){
		bloccaRichiesteServer();
		document.location.href = 'ListaTitoliCampiRicercaBase.do?metodo=' + metodo + '&id=' + id;
	}

	function spostaInPosizioneMarcata(id){
		var numeroOggetti = contaCheckSelezionati(document.listaCampi.progressivo);
		if (numeroOggetti == 0)
			alert("Nessuna posizione selezionata nella lista");
		else if (numeroOggetti > 1)
			alert("Selezionare solo una posizione nella lista");
		else {
			// si individua la posizione dell'elemento selezionato e si esegue la chiamata
			var posizioneSelezionata = -1;
			var trovato = false;
			var indice = 0;
			while (posizioneSelezionata == -1 & !trovato & indice < document.listaCampi.progressivo.length) {
				if (document.listaCampi.progressivo[indice].checked) {
					trovato = true;
					posizioneSelezionata = indice;
				} else 
					indice++;
			}
			if (id == posizioneSelezionata)
				alert("Selezionare una posizione diversa dall'elemento da spostare");
			else {
				bloccaRichiesteServer();
				document.location.href = 'ListaTitoliCampiRicercaBase.do?metodo=spostaInPosizioneMarcata&id=' + id + '&idNew=' + posizioneSelezionata;
			}
		}
	}

	// Azioni di pagina
	
	// azioni generali in sola visualizzazione

	function salvaRicerca(){
		bloccaRichiesteServer();
		document.location.href='DettaglioRicerca.do?metodo=salva&tab=LAY'
	}

	function ripristinaRicercaSalvata(){
		if (confirm('<fmt:message key="info.genRic.ripristinaRicerca"/>')){
			bloccaRichiesteServer();
		  document.location.href='DettaglioRicerca.do?metodo=visualizza&idRicerca=${sessionScope.recordDettRicerca.testata.id}';
		}
	}
	
	function annullaCreazioneRicerca(){
		if (confirm('<fmt:message key="info.genRic.annullaCreazione"/>')){
			bloccaRichiesteServer();
		  document.location.href='InitTrovaRicerche.do';
		}
	}

	function eseguiRicerca(){
		//<c:if test="${!empty (sessionScope.recordDettModificato)}">
		//if (confirm('<fmt:message key="info.genRic.salvaDati.confirm"/>')) salvaRicercaEEseguiRicerca();
		//else
		//</c:if>
		bloccaRichiesteServer();
		document.location.href='ControllaDatiRicercaBase.do?tab=LAY'
	}
	
	function salvaRicercaEEseguiRicerca() {
		bloccaRichiesteServer();
		document.location.href='DettaglioRicerca.do?metodo=salvaEEsegui&tab=LAY';
	}

	function esportaRicerca(){
		document.location.href='EsportaRicerca.do';
	}	
-->
</script>