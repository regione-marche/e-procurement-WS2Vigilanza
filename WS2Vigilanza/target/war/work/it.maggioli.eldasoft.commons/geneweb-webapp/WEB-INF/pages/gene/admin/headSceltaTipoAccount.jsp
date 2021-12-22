<%/*
   * Created on 02-mag-2007
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
<!-- 

	// Azioni di pagina

	function avanti(){
		var href = "";
		var ou = "";
		var accountLdap = document.getElementById("accountLdap");
		var account = document.getElementById("account");

		if(accountLdap.checked){
			href = 'FiltroAccountLdap.do';
			document.location.href = href;
			bloccaRichiesteServer();
			
		} else {
			if (account.checked) {
	  			href = 'DettaglioAccount.do?metodo=nuovo';
				bloccaRichiesteServer();
				document.location.href = href;
			} else {
				alert('Selezionare una delle due opzioni');
			}
		}
	}
	
	
	function annulla(){
		if (confirm("Annullare l'inserimento di un nuovo utente?")){
			bloccaRichiesteServer();
			document.location.href = 'SalvaAccount.do?metodo=annulla';
		}
	}

	function settaRadio() {
		var accountLdap = document.getElementById("accountLdap");
		accountLdap.checked = true;
	}
-->
</script>