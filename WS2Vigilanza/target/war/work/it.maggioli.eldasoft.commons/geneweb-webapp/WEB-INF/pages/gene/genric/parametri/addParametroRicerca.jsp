<%
/*
 * Created on 19-set-2006
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */

 // PAGINA CHE CONTIENE L'ISTANZA DELLA SOTTOPARTE DELLA PAGINA CONTENENTE IL FORM 
 // PER LA CREAZIONE DI UN NUOVO PARAMETRO DA AGGIUNGERE AD UNA RICERCA
%>

<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://www.eldasoft.it/tags" prefix="elda" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<html:form action="/AddParametroRicerca" >
	<table class="dettaglio-tab">
    <tr>
      <td class="etichetta-dato" >Codice (*)</td>
      <td class="valore-dato">
      	<html:select property="codiceParametro">
	      	<html:options name="listaVariabiliUtente" labelName="listaVariabiliUtente"/>
      	</html:select>
      </td>
    </tr>
    <tr>
      <td class="etichetta-dato" >Descrizione per inserimento (*)</td>
      <td class="valore-dato">
      	<html:text property="nome" maxlength="30"/>
      </td>
    </tr>
		<tr>
	    <td class="etichetta-dato">Descrizione</td>
      <td class="valore-dato">
				<html:textarea property="descrizione" cols="70" rows="3" />
      </td>
    </tr>
    <tr>
      <td class="comandi-dettaglio" colSpan="2">
		    <html:hidden property="tipoParametro" value="T"/>
      	<html:hidden property="progressivo"/>
      	<html:hidden property="metodo" value="insert"/>
        <INPUT type="button" class="bottone-azione" value="Conferma" title="Conferma" onclick="javascript:gestisciSubmit();">
        <INPUT type="button" class="bottone-azione" value="Annulla" title="Annulla" onclick="javascript:annulla();">
        &nbsp;
      </td>
    </tr>
	</table>
</html:form>

<script type="text/javascript">
<!--
	$('textarea[name="descrizione"]').bind('input propertychange', function() {checkInputLength( $(this)[0], 2000)});
-->
</script>