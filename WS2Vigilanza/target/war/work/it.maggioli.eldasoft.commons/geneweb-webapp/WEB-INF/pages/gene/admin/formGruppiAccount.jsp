<%
/*
 * Created on 17-lug-2006
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
 
//PAGINA CHE CONTIENE L'ISTANZA DELLA SOTTOPARTE DELLA PAGINA DI DETTAGLIO GRUPPO 
//PER LA MODIFICA DELL'ASSOCIAZIONE TRA UTENTI E IL GRUPPO IN ANALISI
%>

<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>
<%@ taglib uri="http://www.eldasoft.it/tags" prefix="elda" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<html:form action="/SalvaGruppiAccount" >
	<table class="dettaglio-tab-lista">
		<tr>
			<td>
				<display:table  name="listaGruppiAccount" id="gruppiAccountForm" class="datilista" requestURI="ListaGruppiAccount.do">
					<display:column title="<center>Associato<br><a href='javascript:selezionaTutti(document.gruppiAccountForm.idGruppo);' Title='Seleziona Tutti'> <img src='${pageContext.request.contextPath}/img/ico_check.gif' height='15' width='15' alt='Seleziona Tutti'></a>&nbsp;<a href='javascript:deselezionaTutti(document.gruppiAccountForm.idGruppo);' Title='Deseleziona Tutti'><img src='${pageContext.request.contextPath}/img/ico_uncheck.gif' height='15' width='15' alt='Deseleziona Tutti'></a></center>" style="width:50px">
						<html:multibox property="idGruppo" >
				       <bean:write name="gruppiAccountForm" property="idGruppo"/>
						</html:multibox>						
					</display:column>
					<display:column property="nomeGruppo" title="Nome" sortable="true" headerClass="sortable"> </display:column>
					<display:column property="descrGruppo" title="Descrizione" sortable="true" headerClass="sortable"> </display:column>
					<display:column property="nomeProfilo" title="Profilo" sortable="true" headerClass="sortable"> </display:column>
				</display:table>
			</td>
		</tr>
		<tr>
	    <td class="comandi-dettaglio" colSpan="2">
	    	<html:hidden property="idAccount" value="${idAccount}" />
	    	<html:hidden property="metodo" value="visualizzaLista" />
	      <INPUT type="button" class="bottone-azione" value="Salva" 	title="Salva modifiche" 	onclick="javascript:salvaModifiche('${idAccount}');">
	      <INPUT type="button" class="bottone-azione" value="Annulla" title="Annulla modifiche" onclick="javascript:annullaModifiche('${idAccount}');" >
	        &nbsp;
	    </td>
	  </tr>
	</table>
</html:form>
<script type="text/javascript">
	
	function refreshChange(){
		var i;
		var firstRadio=null;
		var setNewRadio=false;
		for(i=0;i<document.gruppiAccountForm.idGruppo.length;i++){
			if(!document.gruppiAccountForm.idGruppo[i].checked){
				document.gruppiAccountForm.principale[i].style.display="none";
				if(document.gruppiAccountForm.principale[i].checked)
					setNewRadio=true;
			}else{
				if(firstRadio==null)
					firstRadio=document.gruppiAccountForm.principale[i];
				document.gruppiAccountForm.principale[i].style.display="block";
			}
		}
		if(setNewRadio&&firstRadio!=null)
			firstRadio.checked=true;
	}
	//refreshChange();
	
	function selezionaTuttiNew(obj){
		selezionaTutti(obj);
		refreshChange();
	}
	
	function deselezionaTuttiNew(obj){
		deselezionaTutti(obj);
		refreshChange();
	}
</script>