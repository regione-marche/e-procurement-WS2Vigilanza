<%/*
   * Created on 25-mar-2015
   *
   * Copyright (c) EldaSoft S.p.A.
   * Tutti i diritti sono riservati.
   *
   * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
   * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
   * aver prima formalizzato un accordo specifico con EldaSoft.
   */

  // PAGINA CHE CONTIENE L'ISTANZA DELLA SOTTOPARTE DELLA PAGINA DI DETTAGLIO 
  // DELLA QUERY SQL DI UNA RICERCA (IN FASE DI VISUALIZZAZIONE) CONTENENTE LA 
  // SEZIONE JAVASCRIPT
%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<fmt:setBundle basename="AliceResources" />
<script type="text/javascript">
<!--

	// Azioni invocate dal menu contestuale

	function apriTrovaRicerche(){
		document.location.href='InitTrovaRicerche.do';
	}
	
	function salvaRicercaETrovaRicerche() {
		bloccaRichiesteServer();
		document.location.href='DettaglioRicerca.do?metodo=salvaETrova&tab=DG';
	}
	
	function listaRicerche(){
		  document.location.href='TrovaRicerche.do?metodo=trovaRicerche';
	}
	
	function salvaRicercaEListaRicerche() {
		bloccaRichiesteServer();
		document.location.href='DettaglioRicerca.do?metodo=salvaELista&tab=DG';
	}
	
	function creaNuovaRicerca(){
		document.location.href='CreaNuovaRicerca.do';
	}
	
	function salvaRicercaECreaRicerca() {
		bloccaRichiesteServer();
		document.location.href='DettaglioRicerca.do?metodo=salvaECrea&tab=DG';
	}

	// Azioni invocate dal tab menu

	function cambiaTab(codiceTab) {
		document.location.href = 'CambiaTabRicercaSql.do?tab=' + codiceTab;
	}

	// Azioni di pagina

	function modifica(){
		document.location.href='EditSqlRicerca.do';
	}
	
	// azioni generali in sola visualizzazione

	function salvaRicerca(){
		bloccaRichiesteServer();
		document.location.href='DettaglioRicerca.do?metodo=salva&tab=SQL'
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
		document.location.href='VerificaParametriRicerca.do?tab=DG';
	}
	
	function salvaRicercaEEseguiRicerca() {
		bloccaRichiesteServer();
		document.location.href='DettaglioRicerca.do?metodo=salvaEEsegui&tab=DG';
	}

	function esportaRicerca(){
		document.location.href='EsportaRicerca.do';
	}

-->
</script>