<%
/*
 * Created on 22-set-2006
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
 
 // PAGINA CHE CONTIENE L'ISTANZA DELLA SOTTOPARTE DELLA PAGINA TROVA RICERCA 
 // CONTENENTE LA SEZIONE JAVASCRIPT
%>

<%@ taglib uri="http://www.eldasoft.it/tags" prefix="elda" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<fmt:setBundle basename="AliceResources" />

<script type="text/javascript">
<!-- 

	function apriTrovaRicerche(){
		<c:if test="${!empty (sessionScope.recordDettModificato)}">
		if (confirm('<fmt:message key="info.genRic.salvaDati.confirm"/>')) salvaRicercaETrovaRicerche();
		else
		</c:if>
		document.location.href='InitTrovaRicerche.do';
	}
	
	function salvaRicercaETrovaRicerche() {
		bloccaRichiesteServer();
		document.location.href='DettaglioRicerca.do?metodo=salvaETrova&tab=DG';
	}
	
	function listaRicerche(){
		<c:if test="${!empty (sessionScope.recordDettModificato)}">
		if (confirm('<fmt:message key="info.genRic.salvaDati.confirm"/>')) salvaRicercaEListaRicerche();
		else
		</c:if>
		document.location.href='TrovaRicerche.do?metodo=trovaRicerche';
	}
	
	function salvaRicercaEListaRicerche() {
		bloccaRichiesteServer();
		document.location.href='DettaglioRicerca.do?metodo=salvaELista&tab=DG';
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
		document.location.href='DettaglioRicerca.do?metodo=salvaECrea&tab=DG';
	}
  
	function vaiDettaglioRicerca(){
		document.location.href='CambiaTabRicerca.do';
	}

  function apriPopupGenModelli(){
    var apriPopup = true;
    var numeroOggetti = contaCheckSelezionati(document.risultatoRicerche.id);
    if (numeroOggetti == 0) {
    	var numRecordTotali = ${requestScope.risultatoRicerca.datiRisultato.numeroRecordTotali};
    	var numRecordPagina = ${fn:length(requestScope.risultatoRicerca.datiRisultato.righeRisultato)};
    	var msg = "Nessun elemento selezionato nella lista.\n";
    	if (numRecordTotali == 1)
    		msg += "Procedere ugualmente con l'unico record estratto?";
    	else {
    		msg += "Procedere ugualmente con tutti i " + numRecordTotali + " record estratti?";
    		if (numRecordTotali > numRecordPagina)
    			msg += "\nSi informa che la composizione potrebbe durare molto\no non produrre alcun risultato.";
    	}    	
    	if (!confirm(msg)) apriPopup = false;
    }
    if (apriPopup)
			compositoreModelli('${pageContext.request.contextPath}','${requestScope.risultatoRicerca.datiRisultato.entPrinc}','${fn:join(requestScope.risultatoRicerca.datiRisultato.campiChiave,";")}','risultatoRicerche.id');
	}
  
	function parametriRicerca(){
		bloccaRichiesteServer();
		document.location.href='VerificaParametriRicerca.do';
	}

	function stampa(){
		alert("Attenzione: verr� aperta una popup per consentire la verifica dell'anteprima.\n" + 
			  "La stampa potr� essere attivata selezionando il pulsante \"Stampa\" posto al di sotto dei dati estratti.");
		var w = 600;
		var h = 400;
		var l = Math.floor((screen.width-w)/2);
		var t = Math.floor((screen.height-h)/2); 
		var popup = window.open('${pageContext.request.contextPath}/geneGenric/StampaRisultatoRicerca.do',
			'stampa',"status=1,resizable=1,scrollbars=1,width=" + w + ",height=" + h + ",top=" + t + ",left=" + l); 
		popup.focus();
	}
-->
</script>