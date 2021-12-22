<%
/*
 * Created on: 30/ott/2014
 *
 * Copyright (c) Maggioli S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di Maggioli S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con Maggioli.
 */
/* Form di ricerca dell'accesso all'applicativo (W_LOGEVENTI) */
%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>

<fmt:setBundle basename="global" />
<c:set var="tipoDB">
	<fmt:message key="it.eldasoft.dbms" />
</c:set>

	<c:choose>
		<c:when test='${tipoDB eq "ORA"}' >
			<c:set var="campoDataOra" value="TO_DATE(DATAORA)" />
		</c:when>
		<c:when test='${tipoDB eq "MSQ"}' >
			<c:set var="campoDataOra" value="CONVERT(date, DATAORA)" />
		</c:when>
		<c:when test='${tipoDB eq "POS"}' >
			<c:set var="campoDataOra" value="CAST(DATAORA AS DATE)" />
		</c:when>
		<c:when test='${tipoDB eq "DB2"}' >
			<c:set var="campoDataOra" value="DATE(DATAORA)" />
		</c:when>
	</c:choose>

<c:set var="listaOpzioniDisponibili" value="${fn:join(opzDisponibili,'#')}#"/>

<gene:template file="ricerca-template.jsp" gestisciProtezioni="true" schema="GENEWEB" idMaschera="LogEventiTrova" >

	<gene:redefineInsert name="addHistory">
		<c:if test='${param.metodo ne "nuova"}' >
			<gene:historyAdd titolo='${gene:getString(pageContext,"titoloMaschera","Ricerca eventi")}' id="ricerca" />
		</c:if>
	</gene:redefineInsert>
	<gene:insert name="addHistory">
		<gene:historyAdd titolo='Dettaglio server ldap' id="scheda" />
	</gene:insert>
	
	
	<gene:redefineInsert name="addAzioniContestoBottom" >
		<tr>
			<td>
				&nbsp;
			</td>
		</tr>
		<jsp:include page="/WEB-INF/pages/commons/torna.jsp" />
	</gene:redefineInsert>
	
	<gene:setString name="titoloMaschera" value="Ricerca eventi" />

	<gene:redefineInsert name="corpo">
	  	<gene:formTrova entita="W_LOGEVENTI" gestisciProtezioni="true" >
			<tr><td colspan="3"><b>Dati generali</b></td></tr>
			<gene:campoTrova campo="IDEVENTO"/>				
			<gene:campoTrova campo="${campoDataOra}" title="Data evento" computed="true" definizione="D;0;;DATA_ELDA;W_DATAORA" />
			<gene:campoTrova campo="SYSUTE" entita="USRSYS" where="W_LOGEVENTI.SYSCON=USRSYS.SYSCON" />
			<gene:campoTrova campo="LIVEVENTO" />
			<gene:campoTrova campo="CODEVENTO" gestore="it.eldasoft.gene.tags.gestori.decoratori.GestoreCampoCodEvento"  />
			<gene:campoTrova campo="OGGEVENTO" />
			<%-- gene-:-campoTrova campo="IPEVENTO" /--%>
	
		</gene:formTrova>
	</gene:redefineInsert>
	<gene:redefineInsert name="trovaCreaNuovo"/>
	
	<gene:javaScript>
		
		$(document).ready(function() {
		<c:if test='${not requestScope.codAppVisibile}' >
			$("#romCampo1").val('${sessionScope.moduloAttivo}');
			showObj("rowCampo1", false);
		</c:if>
		});
		
	</gene:javaScript>
</gene:template>