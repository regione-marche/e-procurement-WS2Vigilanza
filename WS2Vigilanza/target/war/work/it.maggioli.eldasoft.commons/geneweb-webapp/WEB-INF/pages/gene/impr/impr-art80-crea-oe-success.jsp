
<%
	/*
	 * Created on 15-lug-2008
	 *
	 * Copyright (c) EldaSoft S.p.A.
	 * Tutti i diritti sono riservati.
	 *
	 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
	 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
	 * aver prima formalizzato un accordo specifico con EldaSoft.
	 */
%>


<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>


<gene:template file="scheda-template.jsp">

	<c:set var="contextPath" value="${pageContext.request.contextPath}"/>
	<c:set var="codimp" value="${param.codimp}" />
	
	<gene:setString name="titoloMaschera"  value="Art.80 - richiedi verifica per l'operatore economico"/>
	
	<gene:redefineInsert name="corpo">
	
		<table class="lista">
			<tr>
				<td colspan="2" style="padding: 0 5 0 5;">
					<br>
					<c:choose>
						<c:when test="${responseCode eq 201}">
							L'operatore economico &egrave; stato creato con successo nel sistema di verifica.
						</c:when>
						<c:when test="${responseCode eq 409}">
							<b>I dati dell'operatore economico sono gi&agrave; stati inviati in precedenza.</b>
							<br>
							<br>
							E' stato aggiornato lo 'Stato documentale' nella sezione 'Verifiche Art. 80' della scheda dell'impresa.
						</c:when>
					</c:choose>
					<br>
					<br>
				</td>
			</tr>
			<tr>
				<td class="comandi-dettaglio">
					<INPUT type="button" class="bottone-azione" value="Indietro" title="Indietro" onclick="javascript:historyVaiIndietroDi(1);">&nbsp;&nbsp;
				</td>
			</tr>
		</table>
		
	</gene:redefineInsert>
	
	<gene:redefineInsert name="documentiAzioni"></gene:redefineInsert>
	<gene:redefineInsert name="addToAzioni"></gene:redefineInsert>
	
</gene:template>



