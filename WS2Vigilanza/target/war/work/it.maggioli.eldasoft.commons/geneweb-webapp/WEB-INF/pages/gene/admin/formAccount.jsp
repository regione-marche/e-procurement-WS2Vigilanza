<%/*
       * Created on 20-Ott-2006
       *
       * Copyright (c) EldaSoft S.p.A.
       * Tutti i diritti sono riservati.
       *
       * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
       * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
       * aver prima formalizzato un accordo specifico con EldaSoft.
       */

      // PAGINA CHE CONTIENE LA DEFINIZIONE DELLE VOCI DEI MENU COMUNI A TUTTE LE APPLICAZIONI
      %>

<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html"%>
<%@ taglib uri="http://www.eldasoft.it/tags" prefix="elda"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<c:set var="contextPath" value="${pageContext.request.contextPath}" />

<fmt:setBundle basename="AliceResources" />
<c:set var="nomeEntitaParametrizzata">
	<fmt:message key="label.tags.uffint.multiplo" />
</c:set>

<c:set var="listaOpzioniDisponibili" value="${fn:join(opzDisponibili,'#')}#"/>
<c:set var="listaOpzioniUtenteAbilitate" value="${fn:join(profiloUtente.funzioniUtenteAbilitate,'#')}#" />
<c:set var="listaOpzioniUtenteSys" value="${fn:join(accountForm.opzioniUtenteSys,'#')}#" />
<c:set var="account" value="${requestScope.accountForm}"/>

<html:form action="/SalvaAccount">
	<input type="hidden" name="metodo" value="${metodo}"/>
	<table  class="dettaglio-tab">
		<tr>
			<td colspan="2">
				<b>Dati generali</b>
			</td>
		</tr>
		<c:if test='${metodo eq "carica" || metodo eq "modifica"}'>
			<tr id="rowCodiceUtente" >
				<td class="etichetta-dato">Codice</td>
				<td>&nbsp;<c:out value="${account.idAccount}"/>
					<html:hidden property="idAccount" name="accountForm"/>
				</td>
			</tr>
		</c:if>
			<tr id="rowDescrizioneUtente" >
				<td class="etichetta-dato">Descrizione (*)</td>
     		<td class="valore-dato">
				<html:text property="nome" size="40" maxlength="161" name="accountForm"/>
				</td>
   		</tr>
			<tr id="rowNomeUtente" >
				<td class="etichetta-dato">Nome utente (*)</td>
     		<td class="valore-dato">
     			<c:choose>
					<c:when test='${(account.idAccount eq 48) || (account.idAccount eq 49) || (account.idAccount eq 50)}'>
						<c:out value="${account.login}"/>
						<html:hidden property="login" styleId="login"/>
					</c:when>
					<c:otherwise>
						<html:text property="login" styleId="login" maxlength="60" size="15" onblur="javascript:controllaCampoNome(this,'Nome');"/>
					</c:otherwise>
				</c:choose>
					
			</td>
   		</tr>
  	<c:choose>
  		<c:when test='${fn:contains(listaOpzioniDisponibili, "OP100#")}'>
   		<tr id="rowUtenteLDAP" >
				<td class="etichetta-dato" >Utente Ldap</td>
   			<td class="valore-dato"> 
					<c:choose>
						<c:when test='${accountForm.flagLdap == "1" }'>
							Si
						</c:when>
						<c:otherwise>
							No
						</c:otherwise>
					</c:choose>
   				<html:hidden property="flagLdap" name="accountForm"/>
				</td>
   		</tr>
   	</c:when>
   	<c:otherwise>
   		<html:hidden property="flagLdap" name="accountForm"/>
   	</c:otherwise>
	</c:choose>
  <c:choose>
  	<c:when test='${(not empty account.dn) && (account.dn != "")}'>
  		<tr id="rowNomeUtenteLDAP" >
				<td class="etichetta-dato">Nome&nbsp;univoco&nbsp;utente&nbsp;per&nbsp;LDAP&nbsp;(*)</td>
   			<td class="valore-dato">
   				<html:text property="dn" maxlength="2000" size="50"/>
				</td>
			</tr>
		</c:when>
		<c:otherwise>
   	<c:if test='${metodo != "carica" && metodo != "modifica"}'>
    	<tr>
				<td class="etichetta-dato" >Password <c:if test='${"1" ne passwordNullable}'>(*)</c:if></td>
   			<td class="valore-dato">
   				<input type="password" id="password" name="password" class="testo" maxlength="30" size="15"/>
				</td>
			</tr>
			<tr>
				<td class="etichetta-dato" >Conferma Password <c:if test='${"1" ne passwordNullable}'>(*)</c:if></td>
   			<td class="valore-dato"> 
					<input type="password" id="confPassword" name="confPassword" class="testo" 
						maxlength="30" size="15" onblur="javascript:passwordOk();" />
				</td>
    	</tr>
    </c:if>
    </c:otherwise>
  </c:choose>
  		<c:if test="${! empty listaUffAppartenenza }">
			<tr id="rowUfficioAppartenenza" >
			<td class="etichetta-dato">Ufficio Appartenenza<c:if test='${"1" eq requestScope.uffAppObbligatorio}'> (*)</c:if></td>
			  <td class="valore-dato">
		      	<html:select property="ufficioAppartenenza" >
		      		<html:option value="">&nbsp;</html:option>
			      	<html:options collection="listaUffAppartenenza" property="tipoTabellato" labelProperty="descTabellato" />
		      	</html:select>
			  </td>
			</tr>
		</c:if>
		<c:if test="${! empty listaCategorie }">
			<tr id="rowCategoria" >
			<td class="etichetta-dato">Categoria</td>
			  <td class="valore-dato">
				<html:select property="categoria" >
					<html:option value="">&nbsp;</html:option>
					<html:options collection="listaCategorie" property="tipoTabellato" labelProperty="descTabellato" />
				</html:select>
			  </td>
			</tr>
		</c:if>
   	<tr id="rowEmail" >
			<td class="etichetta-dato">E-Mail</td>
   		<td class="valore-dato">
	 			<html:text property="email" name="accountForm" onblur="javascript:controllaEmail(this,'document.accountForm.email');" maxlength="100" size="50"/>
			</td>
   	</tr>
   	<tr id="rowCodfisc" >
			<td class="etichetta-dato">Codice fiscale</td>
   		<td class="valore-dato">
	 			<html:text property="codfisc" name="accountForm"  onchange="javascript:controllaCodfisc(this,'document.accountForm.codfisc');" maxlength="18" size="18"/>
			</td>
   	</tr>
  
  	<tr>
			<td colspan="2">
				<b>Opzioni</b>
			</td>
		</tr>
		<tr id="rowUtentiApplicativo" >
			<td class="etichetta-dato">Gestione utenti</td>
			<td class="valore-dato">
				<c:choose>
					<c:when test='${((account.idAccount ne 48) && (account.idAccount ne 49)) }'>
						<html:select  name="accountForm" property="opzioniUtenteSys">	
							<html:options property="listaValuePrivilegi" labelProperty="listaTextPrivilegi"/>
						</html:select>
					</c:when>
					<c:otherwise>
						<input type="hidden" name="opzioniUtenteSys" value="ou11"/>
							Gestione Completa
					</c:otherwise>
				</c:choose>
			</td>
		</tr>
 		<tr id="rowParametriSistema" >
			<td class="etichetta-dato">Amministrazione di sistema</td>
 			<td class="valore-dato">
 				<c:choose>
		 			<c:when test='${fn:contains(listaOpzioniUtenteAbilitate, "ou89#") && ((account.idAccount ne 48) && (account.idAccount ne 49)) }'>
			 			<input type="checkbox" name="opzioniUtenteSys" value="ou89" <c:if test='${fn:contains(listaOpzioniUtenteSys, "ou89#")}'>checked="true"</c:if> />
					</c:when>
					<c:otherwise>
						<c:choose>
			 				<c:when test='${fn:contains(listaOpzioniUtenteSys, "ou89#")}'>
			 				<input type="hidden" name="opzioniUtenteSys" value="ou89"/>
			 					Si
			 				</c:when>
			 				<c:otherwise>
			 				 	No
			 				</c:otherwise>
	 					</c:choose>
					</c:otherwise>
				</c:choose>
			</td>
	  </tr>
 		<tr id="rowAbilitaFunzioniAvanzate" >
			<td class="etichetta-dato">Abilita funzioni avanzate men&ugrave; "Utilit&agrave;"</td>
 			<td class="valore-dato">
	 			<input type="checkbox" name="opzioniUtenteSys" value="ou56" <c:if test='${fn:contains(listaOpzioniUtenteSys, "ou56#")}'>checked="true"</c:if> />
			</td>
	  </tr>
	  <% // si inserisce la configurazione solo se l'applicativo � abilitato ad almeno una delle sottovoci del menu' %>
	  <c:if test='${fn:contains(listaOpzioniDisponibili, "OP1#") ||fn:contains(listaOpzioniDisponibili, "OP2#")}'>
			<tr id="rowNascondiMenuStrumenti" >
				<td class="etichetta-dato">Nascondi men&ugrave; "Strumenti"</td>
   			<td class="valore-dato">
	 				<input type="checkbox" name="opzioniUtenteSys" id="opzioniUtente" value="ou30" <c:if test='${fn:contains(listaOpzioniUtenteSys, "ou30#")}'>checked="true"</c:if> onclick="nascondiModelliReport();"/>
				</td>
   		</tr>
   	</c:if>
  	<c:if test='${fn:contains(listaOpzioniDisponibili, "OP1#")}'>
   		<tr id="rModelli">
				<td class="etichetta-dato">Gestione modelli</td>
  	 		<td class="valore-dato">
					<html:select  name="accountForm" property="opzioniUtenteSys" styleId="opzioniGenmod" onchange="nascondiProspetto();">	
						<html:options property="listaValueGenmod" labelProperty="listaTextGenmod"/>
		 			</html:select>
				</td>
   		</tr>
  	</c:if>
   	<c:if test='${fn:contains(listaOpzioniDisponibili, "OP2#")}'>
   		<tr id="rReport">
				<td class="etichetta-dato" >Gestione report</td>
   			<td class="valore-dato"> 
					<html:select  name="accountForm" property="opzioniUtenteSys" styleId="opzioniGenric" onchange="settaReport();">	
						<html:options property="listaValueGenric" labelProperty="listaTextGenric" />
	    		</html:select>
    			<span id="rGenric">
	    			&nbsp;&nbsp;
	    			<input type="checkbox" name="opzioniUtenteSys" value="ou53" id="opzioniGenricBase" <c:if test='${fn:contains(listaOpzioniUtenteSys, "ou53#")}'>checked="checked" </c:if>/>Base
	    			&nbsp;&nbsp;
	    			<input type="checkbox" name="opzioniUtenteSys" value="ou54" id="opzioniGenricAvanzati" <c:if test='${fn:contains(listaOpzioniUtenteSys, "ou54#")}'>checked="checked" </c:if>/>Avanzato
	    			<span id="rProspetto">
		    			&nbsp;&nbsp;
		    			<input type="checkbox" name="opzioniUtenteSys" value="ou55" id="opzioniGenricProspetto" <c:if test='${fn:contains(listaOpzioniUtenteSys, "ou55#")}'>checked="checked" </c:if>/>Modello
		    		</span>
    			</span>
				</td>
   		</tr>
		<tr id="rowSchedulazioni">
			<td class="etichetta-dato">Amministrazione schedulazioni di report</td>
			<td class="valore-dato">
				<input type="checkbox" name="opzioniUtenteSys" value="ou62" <c:if test='${fn:contains(listaOpzioniUtenteSys, "ou62#")}'>checked="checked" </c:if>/>
			</td>
		</tr>
  	</c:if>

		<c:if test='${fn:contains(listaOpzioniDisponibili, "OP128#")}'>
		<tr id="rowAmmScadenzario">
			<td class="etichetta-dato">Amministrazione modelli di scadenzario</td>
			<td class="valore-dato">
				<input type="checkbox" name="opzioniUtenteSys" value="ou212" <c:if test='${fn:contains(listaOpzioniUtenteSys, "ou212#")}'>checked="checked" </c:if>/>
			</td>
		</tr>
		</c:if>
   	
			<tr id="rowAbilitaInserimentoNote" >
				<td class="etichetta-dato" >Abilita utente all'inserimento note</td>
   				<td class="valore-dato">
   					<c:if test='${fn:contains(listaOpzioniUtenteSys, "ou59#")}'>
   						<c:set var="tmp" value='checked="checked"'/>
   					</c:if>
   					<input type="checkbox" name="opzioniUtenteSys" value="ou59" ${tmp} />
				</td>
   		</tr>
   		<tr id="rowBloccaEditUffint" >
				<td class="etichetta-dato" >Blocca utente nella modifica ${fn:toLowerCase(nomeEntitaParametrizzata)}</td>
   				<td class="valore-dato">
   					<c:if test='${fn:contains(listaOpzioniUtenteSys, "ou214#")}'>
   						<c:set var="tmp" value='checked="checked"'/>
   					</c:if>
   					<input type="checkbox" name="opzioniUtenteSys" value="ou214" <c:if test='${fn:contains(listaOpzioniUtenteSys, "ou214#")}'>checked="checked" </c:if>/>
				</td>
   		</tr>
   		<tr id="rowBloccaEliminazioneEntitaPrincipale" >
				<td class="etichetta-dato" >Blocca eliminazione su entita principale</td>
   				<td class="valore-dato">
   					<c:if test='${fn:contains(listaOpzioniUtenteSys, "ou215#")}'>
   						<c:set var="tmp" value='checked="checked"'/>
   					</c:if>
   					<input type="checkbox" name="opzioniUtenteSys" value="ou215" <c:if test='${fn:contains(listaOpzioniUtenteSys, "ou215#")}'>checked="checked" </c:if>/>
				</td>
   		</tr>
   		<tr>
			<td colspan="3">
				<b>Sicurezza</b>
			</td>
	  	</tr>
	  	<c:if test='${(empty account.dn) || (account.dn == "")}'>
	  	<tr id="rowScadenzaAccount" >
			<td class="etichetta-dato" >Scadenza Account</td>
      		<td class="valore-dato"> 
      			<select name="selectScadenzaAccount" id="selectScadenzaAccount" onchange="javascript:gestioneCampoDataScadenza();">
		      		<option value="" <c:if test='${(empty accountForm.scadenzaAccount) && (metodo eq "modifica")}'>selected="selected"</c:if> >Mai</option>
					<option value="Alla fine di:" <c:if test='${(not empty accountForm.scadenzaAccount)  }'>selected="selected"</c:if> >Alla fine di: </option>
				</select>    			
				<span id="spanScadenzaAccount">
					<html:text property="scadenzaAccount" styleId="scadenzaAccount" onblur="javascript:controllaInputData(this);"  size="10" maxlength="10" styleClass="data"/> <!-- value="${formAccount.scadenzaAccount}" -->
				</span>
			</td>
	  	</tr>
	  	</c:if>
	  	<tr id="rowUtenteDisabilitato" >
			<td class="etichetta-dato">Utente disabilitato</td>
   			<td class="valore-dato">
	   				<c:choose>
						<c:when test='${accountForm.utenteDisabilitato == "1" }'>
							Si
						</c:when>
						<c:otherwise>
							No
						</c:otherwise>
					</c:choose>
   					<html:hidden property="utenteDisabilitato" name="accountForm"/>
				</td>
   		</tr>
   		<c:if test='${account.flagLdap == 0 && (empty account.dn || account.dn == "")}'>
		   	<tr id="rSicurezza">
					<td class="etichetta-dato" >Attiva password sicura</td>
		  		<td class="valore-dato"> 
		  			<input type="checkbox" name="opzioniUtenteSys" value="ou39" <c:if test='${fn:contains(listaOpzioniUtenteSys, "ou39#")}'>checked="checked" </c:if> />
					</td>
		  	</tr>
	    </c:if>
	  	
	  	<c:set var="art80wsurl" value='${gene:callFunction("it.eldasoft.gene.tags.functions.GetPropertyFunction", "art80.ws.url")}'/>
	  	<c:if test="${!empty art80wsurl}">
		  	<tr>
				<td colspan="3">
					<b>Verifiche Art.80</b>
				</td>
		  	</tr>
		   	<tr id="rowReadArt80">
				<td class="etichetta-dato" >Utente abilitato alla consultazione dei documenti</td>
		  		<td class="valore-dato"> 
		  			<input type="checkbox" name="opzioniUtenteSys" value="ou225" <c:if test='${fn:contains(listaOpzioniUtenteSys, "ou225#")}'>checked="checked" </c:if> />
				</td>
		  	</tr>
		 </c:if>
	  	
  	  <jsp:include page="/WEB-INF/pages/gene/admin/editSottoSezioniDatiGen.jsp" />
			<tr>
   			<td class="comandi-dettaglio" colSpan="2">
					<INPUT type="button" class="bottone-azione" value="Salva" title="Conferma delle modifiche" onClick="javascript:schedaSalva();" />
					<Input type="button" class="bottone-azione" value="Annulla" onClick="javascript:schedaAnnulla();">
		        &nbsp;
	      </td>
	    </tr>
	</table>
</html:form>
<script type="text/javascript">
<!--

initVarGlobali();
settaReport();

-->
</script>