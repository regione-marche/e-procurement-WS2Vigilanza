<%/*
       * Created on 07-Nov-2006
       *
       * Copyright (c) EldaSoft S.p.A.
       * Tutti i diritti sono riservati.
       *
       * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
       * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
       * aver prima formalizzato un accordo specifico con EldaSoft.
       */
%>
<% //Inserisco la Tag Library %>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.eldasoft.it/tags" prefix="elda" %>

<c:set var="protocolloSSO" value='${gene:callFunction("it.eldasoft.gene.tags.functions.GetProtocolloSSOFunction",  pageContext)}' scope="request"/>
<c:set var="isRegistrazioneAutomatica" value='${gene:callFunction("it.eldasoft.gene.tags.functions.IsRegistrazioneAutomaticaFunction",  pageContext)}' scope="request"/>

<!-- Dati anagrafica utente -->
<gene:template file="scheda-nomenu-template.jsp">
<gene:javaScript>
	function gestioneAction() {
		
	}
	
</gene:javaScript>
	
	<% //Settaggio delle stringhe utilizate nel template %>
	<gene:setString name="titoloMaschera" value='Registrazione Utente'/>
	
	<gene:redefineInsert name="corpo">

		<table class="dettaglio-home">
			<tr>
				<td class="sotto-voce">
					<p>Registrazione avvenuta con successo.</p>
					<c:if test='${isRegistrazioneAutomatica eq "false"}'>
						<p>Ti verr&agrave; inviata una mail non appena sarai abilitato ad accedere all'applicativo.</p>
					</c:if>
					<c:if test='${isRegistrazioneAutomatica eq "true"}'>
						<c:choose>
						<c:when test='${protocolloSSO eq "1"}'>
		    				<p><a href="ShibbolethLogin.do" class="link-generico">Accedi all'applicativo</a></p>
						</c:when>
						<c:when test='${protocolloSSO eq "2"}'>
		    				<p><a href="CohesionLoginResponseAction.do" class="link-generico">Accedi all'applicativo</a></p>
						</c:when>
						<c:when test='${protocolloSSO eq "3"}'>
		    				<p><a href="BartLogin.do" class="link-generico">Accedi all'applicativo</a></p>
						</c:when>
						<c:otherwise>
						   <p><a href="InitLogin.do" class="link-generico">Accedi all'applicativo</a></p>
						</c:otherwise>
						</c:choose>
					 </c:if>
				</td>
			</tr>
		</table>

</gene:redefineInsert>
</gene:template>