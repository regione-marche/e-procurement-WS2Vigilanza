<%
/*
 * Created on 31-ago-2006
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
 
 // PAGINA CHE CONTIENE L'ISTANZA DELLA SOTTOPARTE DELLA PAGINA DI DETTAGLIO 
 // DATI GENERALI DI UNA RICERCA (IN FASE DI VISUALIZZAZIONE) CONTENENTE LA 
 // SEZIONE JAVASCRIPT
%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<fmt:setBundle basename="AliceResources" />

<script type="text/javascript">
<!--

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
		document.location.href='DettaglioRicerca.do?metodo=salvaETrova&tab=GRP';
	}
	
	function listaRicerche(){
		//<c:if test="${!empty (sessionScope.recordDettModificato)}">
		//if (confirm('<fmt:message key="info.genRic.salvaDati.confirm"/>')) salvaRicercaEListaRicerche();
		//else
		//</c:if>
		document.location.href='TrovaRicerche.do?metodo=trovaRicerche';
	}
	
	function salvaRicercaEListaRicerche() {
		bloccaRichiesteServer();
		document.location.href='DettaglioRicerca.do?metodo=salvaELista&tab=GRP';
	}
	
	function creaNuovaRicerca(){
		//<c:if test="${!empty (sessionScope.recordDettModificato)}">
		//if (confirm('<fmt:message key="info.genRic.salvaDati.confirm"/>')) salvaRicercaECreaRicerca();
		//else
		//</c:if>
		document.location.href='CreaNuovaRicerca.do';
	}
	
	function salvaRicercaECreaRicerca() {
		bloccaRichiesteServer();
		document.location.href='DettaglioRicerca.do?metodo=salvaECrea&tab=GRP';
	}

	// Azioni invocate dal tab menu

	function cambiaTab(codiceTab) {
		document.location.href = 'CambiaTabRicerca.do?tab=' + codiceTab;
	}

	// Azioni di pagina
	
	function modifica(){
		document.location.href='EditGruppiRicerca.do?metodo=editLista';
	}
	
	// azioni generali in sola visualizzazione

	function salvaRicerca(){
		bloccaRichiesteServer();
		document.location.href='DettaglioRicerca.do?metodo=salva&tab=GRP'
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
		  document.location.href = 'DettaglioRicerca.do?metodo=annullaCrea';
		}
	}

	function eseguiRicerca(){
		//<c:if test="${!empty (sessionScope.recordDettModificato)}">
		//if (confirm('<fmt:message key="info.genRic.salvaDati.confirm"/>')) salvaRicercaEEseguiRicerca();
		//else
		//</c:if>
		bloccaRichiesteServer();
		document.location.href='ControllaDatiRicerca.do?tab=GRP'
	}
	
	function salvaRicercaEEseguiRicerca() {
		bloccaRichiesteServer();
		document.location.href='DettaglioRicerca.do?metodo=salvaEEsegui&tab=GRP';
	}

	function esportaRicerca(){
		document.location.href='EsportaRicerca.do';
	}
-->
</script>