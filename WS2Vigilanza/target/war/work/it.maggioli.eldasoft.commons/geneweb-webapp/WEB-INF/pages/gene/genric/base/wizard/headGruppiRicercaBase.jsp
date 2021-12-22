<%/*
   * Created on 04-mag-2007
   *
   * Copyright (c) EldaSoft S.p.A.
   * Tutti i diritti sono riservati.
   *
   * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
   * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
   * aver prima formalizzato un accordo specifico con EldaSoft.
   */

  // PAGINA CHE CONTIENE L'ISTANZA DELLA SOTTOPARTE RELATIVA ALLE FUNZIONI 
  // JAVASCRIPT DELLA PAGINA CON LA DOMANDA DI DEFINIZIONE DI UNA CONDIZIONE DI
  // FILTRO PER UNA RICERCA BASE
%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<fmt:setBundle basename="AliceResources" />

<c:set var="contextPath" value="${pageContext.request.contextPath}" />
<c:set var="contenitore" value="${sessionScope.recordDettRicerca}" />

<script type="text/javascript" src="${contextPath}/js/controlliFormali.js"></script>
<script type="text/javascript">

	// Azioni di pagina

	// profiloSenzaGruppi = ${profiloSenzaGruppi} 

	function avanti(){
<c:choose>
	<c:when test='${empty profiloSenzaGruppi}'>
		var numeroGruppi = "" + document.gruppiRicercaForm.idGruppo.length;
		var isGruppoChecked = false;
		if(numeroGruppi == 'undefined'){
			isGruppoChecked = document.gruppiRicercaForm.idGruppo.checked;
		} else {
			for(var i=0; i < numeroGruppi; i++){
				isGruppoChecked = isGruppoChecked || document.gruppiRicercaForm.idGruppo[i].checked;
			}
		}
	</c:when>
	<c:otherwise>
		var isGruppoChecked = true;
	</c:otherwise>
</c:choose>
		if(isGruppoChecked){
			bloccaRichiesteServer();
			document.gruppiRicercaForm.submit();
		} else {
			alert('Spuntare almeno un gruppo a cui rendere disponibile il report per proseguire');
		}
	}
		
	function indietro(){
		document.location.href = 'WizardBase.do?pageTo=PUB';
	}

	function annulla(){
		if (confirm('<fmt:message key="info.genRic.annullaCreazione"/>')){
			bloccaRichiesteServer();
			document.location.href = 'DettaglioRicerca.do?metodo=annullaCrea';
		}
	}

</script>