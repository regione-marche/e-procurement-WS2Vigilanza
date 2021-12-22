<%
/*
 * Created on 23-mar-2015
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
 
 // PAGINA CHE CONTIENE L'ISTANZA DEL TITOLO DELLE PAGINE DI DETTAGLIO RICERCA
 // AD ECCEZIONE DELLA PAGINA DI CREAZIONE DI UNA NUOVA RICERCA
%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<c:choose>
	<c:when test='${fn:length(nomeOggetto) gt 0}' >
 		Dettaglio report SQL - "${nomeOggetto}" 
 	</c:when>
 	<c:otherwise>
 		Nuovo report SQL
 	</c:otherwise>
 </c:choose>