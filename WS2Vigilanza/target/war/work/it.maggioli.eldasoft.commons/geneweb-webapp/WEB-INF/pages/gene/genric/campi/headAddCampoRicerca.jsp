<%/*
   * Created on 28-ago-2006
   *
   * Copyright (c) EldaSoft S.p.A.
   * Tutti i diritti sono riservati.
   *
   * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
   * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
   * aver prima formalizzato un accordo specifico con EldaSoft.
   */

  // PAGINA CHE CONTIENE L'ISTANZA DELLA SOTTOPARTE RELATIVA ALLE AZIONI DI CONTESTO
  // DELLA PAGINA DI CREAZIONE DI UN NUOVO ARGOMENTO DA AGGIUNGERE AD UNA RICERCA
%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<c:set var="contextPath" value="${pageContext.request.contextPath}" />

<c:choose>
<c:when test='${fn:length(campoRicercaForm.aliasTabella) gt 0}' > 
	<c:set var="aliasTabella" value='${campoRicercaForm.aliasTabella}' />
</c:when>
<c:otherwise> 
	<c:set var="aliasTabella" value="" />
</c:otherwise>
</c:choose>

<c:choose>
<c:when test='${fn:length(campoRicercaForm.mnemonicoCampo) gt 0}' > 
	<c:set var="mneCampo" value='${campoRicercaForm.mnemonicoCampo}' />
</c:when>
<c:otherwise> 
	<c:set var="mneCampo" value="" />
</c:otherwise>
</c:choose>

<script type="text/javascript" src="${contextPath}/js/controlliFormali.js"></script>

<%@ page import="java.util.Vector,
                it.eldasoft.utils.metadata.domain.Campo,
                it.eldasoft.gene.web.struts.genric.argomenti.TabellaRicercaForm" %>

<script type="text/javascript">
<!-- 

	// Azioni invocate dal menu contestuale
	
	// Azioni invocate dal tab menu

	function cambiaTab(codiceTab){
		document.location.href = 'CambiaTabRicerca.do?tab=' + codiceTab;
	}

	// Azioni di pagina

	function gestisciSubmit(){
		var esito = true;
		if (esito && !controllaCampoInputObbligatorio(document.campoRicercaForm.aliasTabella, 'Tabella')){
		 	esito = false;
		}
		if (esito && !controllaCampoInputObbligatorio(document.campoRicercaForm.descrizioneCampo, 'Campo')){
		 	esito = false;
		}
		if (esito){
			bloccaRichiesteServer();
			document.campoRicercaForm.submit();
		}
	}
	
	function annulla(){
		bloccaRichiesteServer();
		cambiaTab('CAM');
	}

<%
  /* 
   * Generazione degli array javascript contenenti elenco tabelle e 
   * elenco campi..
   */
  
	  Vector elencoTabelle = (Vector) request.getAttribute("elencoTabelle");

		String listaTabelle = "var listaTabelle = new Array (";
		for(int i=0; i < elencoTabelle.size(); i++){
		  if(i>0)
		    listaTabelle += ",\n";
		  listaTabelle += "\"" + ((TabellaRicercaForm)elencoTabelle.elementAt(i)).getAliasTabella().replaceAll("[\"]","\\\\\"") + "\"";;
		}
		listaTabelle += ");\n";
		
    Vector elencoCampi = null;
    TabellaRicercaForm tabella = null;
    Campo campo = null;
    String dichiarazioneListaSelect = null;
    String dichiarazioneValueSelect = null;
    String dichiarazioneCodiciSelect = null;
    for (int i = 0; i < elencoTabelle.size(); i++) {
      tabella = (TabellaRicercaForm) elencoTabelle.elementAt(i);
      dichiarazioneListaSelect = "var " + tabella.getAliasTabella() + " = new Array ( '',";
      dichiarazioneValueSelect = "var valoriCampi" + i + " = new Array ( '',";
      dichiarazioneCodiciSelect = "var codiciCampi" + i + " = new Array ( '',";
      elencoCampi = (Vector) request.getAttribute("elencoCampi" + tabella.getAliasTabella());
      for (int j = 0; j < elencoCampi.size(); j++) {
        campo = (Campo) elencoCampi.elementAt(j);
        if (j>0) {
          dichiarazioneListaSelect += ",\n";
          dichiarazioneValueSelect += ",\n";
          dichiarazioneCodiciSelect += ",\n";
        }
        dichiarazioneListaSelect += "\"" + campo.getCodiceMnemonico() + " - " + campo.getDescrizione().replaceAll("[\"]","\\\\\"") + "\"";
        dichiarazioneValueSelect += "\"" + campo.getDescrizione().replaceAll("[\"]","\\\\\"") + "\"";
        dichiarazioneCodiciSelect += "\"" + campo.getCodiceMnemonico() + "\"";
      }
      dichiarazioneListaSelect += ");\n";
      dichiarazioneValueSelect += ");\n";
      dichiarazioneCodiciSelect += ");\n";
	%>

	<%=dichiarazioneListaSelect%>
	<%=dichiarazioneValueSelect%>
	<%=dichiarazioneCodiciSelect%>
	<%
	    }
  %>
	<%=listaTabelle%>

	function aggiornaOpzioniSelectCampo(indice) {
		var nomeArrayValue = 'valoriCampi' + (indice-1);
		var nomeArrayText = document.campoRicercaForm.aliasTabella.options[indice].value;
		var selectDaAggiornare = document.campoRicercaForm.descrizioneCampo;
	  
		if (indice == 0) {
			selectDaAggiornare.length = 1;
			selectDaAggiornare.options[0].text = "";
			selectDaAggiornare.options[0].value = "";
		} else {
			aggiornaOpzioniSelect(nomeArrayValue, nomeArrayText, selectDaAggiornare);
			selectDaAggiornare.selectedIndex = 0;
			document.campoRicercaForm.mnemonicoCampo.value = "";
		}
	}

	function aggiornaCampiInput(indiceSchemaSelezionato, indiceSelezionato) {
		var arrayValori = eval("codiciCampi"+(indiceSchemaSelezionato-1)); //tolgo il primo elemento vuoto
		document.campoRicercaForm.mnemonicoCampo.value = arrayValori[indiceSelezionato];
	}
-->
</script>
