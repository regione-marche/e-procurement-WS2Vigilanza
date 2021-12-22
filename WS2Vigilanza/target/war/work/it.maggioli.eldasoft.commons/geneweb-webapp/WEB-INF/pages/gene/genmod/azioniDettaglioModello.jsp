<%/*
   * Created on 30-giu-2006
   *
   * Copyright (c) EldaSoft S.p.A.
   * Tutti i diritti sono riservati.
   *
   * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
   * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
   * aver prima formalizzato un accordo specifico con EldaSoft.
   */

  // PAGINA CHE CONTIENE L'ISTANZA DELLA SOTTOPARTE DELLA PAGINA DI VISUALIZZAZIONE DEL 
  // DETTAGLIO DI UN GRUPPO RELATIVA ALLE AZIONI DI CONTESTO
%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>

<!-- METODO ${param.metodo} o ${metodo} -->
<c:if test="${(param.metodo ne 'creaModello' and param.metodo ne 'modificaModello') and (metodo ne 'creaModello' and metodo ne 'modificaModello')}">
	<gene:template file="menuAzioni-template.jsp">
		<c:set var="titoloHistory" value=""/>
		<c:if test="${fn:length(nomeOggetto) gt 0}" >
			<c:set var="titoloHistory" value=" - ${nomeOggetto}"/>
		</c:if>
		<gene:insert name="addHistory">
			<gene:historyAdd titolo='Dettaglio Modello${titoloHistory}' id="scheda" />
		</gene:insert>
	</gene:template>
</c:if>

			<!-- INSERITO ENNESIMO TACCONE DOVUTO ALLA GESTIONE IN UN UNICO FILE DI TUTTE LE AZIONI DI SINISTRA... -->
<c:set var="isNavigazioneDisattiva" value="${isNavigazioneDisabilitata}" />

		<tr>
			<td class="titolomenulaterale">Dettaglio: Azioni</td>
		</tr>
		<c:choose>
			<c:when test="${param.metodo eq 'dettaglioModello'}">
				<tr>
					<td class="vocemenulaterale"><a href="javascript:modifica('<c:out value="${idModello}" />');"  
							tabindex="1500" title="Modifica modello">Modifica</a></td>
				</tr>
			</c:when>
			<c:when test="${param.metodo eq 'modificaModello' || metodo eq 'modificaModello'}">
				<tr>
					<td class="vocemenulaterale"><a href="javascript:update();"  
							tabindex="1500" title="Salva">Salva</a></td>
				</tr>
				<tr>
					<td class="vocemenulaterale"><a href="javascript:annulla();"  
							tabindex="1500" title="Annulla">Annulla</a></td>
				</tr>
			</c:when>
			<c:when test="${param.metodo eq 'creaModello' || metodo eq 'creaModello'}">
				<tr>
					<td class="vocemenulaterale"><a href="javascript:update();"  
							tabindex="1500" title="Salva">Salva</a></td>
				</tr>
				<tr>
					<td class="vocemenulaterale"><a href="javascript:annulla();"  
							tabindex="1500" title="Annulla">Annulla</a></td>
				</tr>
			</c:when>
			<c:when test="${param.metodo eq 'modificaGruppiModello'}">
				<tr>
					<td class="vocemenulaterale"><a href="javascript:updateListaGruppi();"  
							tabindex="1500" title="Modifica modello">Salva</a></td>
				</tr>
				<tr>
					<td class="vocemenulaterale"><a href="javascript:listaGruppiModello(${idModello});"  
							tabindex="1500" title="Modifica modello">Annulla</a></td>
				</tr>
			</c:when>
			<c:when test="${param.metodo eq 'modificaGruppiModello'}">
				<tr>
					<td class="vocemenulaterale"><a href="javascript:updateListaGruppi();"  
							tabindex="1500" title="Modifica modello">Salva</a></td>
				</tr>
				<tr>
					<td class="vocemenulaterale"><a href="javascript:listaGruppiModello(${idModello});"  
							tabindex="1500" title="Modifica modello">Annulla</a></td>
				</tr>
			</c:when>
			<c:when test="${param.metodo eq 'listaGruppiModello'}">
				<tr>
					<td class="vocemenulaterale"><a href="javascript:modificaAssModelliGruppo(${idModello});"  
							tabindex="1500" title="Modifica associazioni dei gruppi al modello">Modifica associazioni</a></td>
				</tr>
			</c:when>
			<c:when test="${param.metodo eq 'listaParametriModello'}">
				<tr>
					<td class="vocemenulaterale"><a href="javascript:creaParametro(${idModello});"  
							tabindex="1500" title="Aggiungi parametro al modello">Aggiungi parametro</a></td>
				</tr>
			</c:when>
			<c:when test="${param.metodo eq 'creaParametroModello'}">
				<tr>
					<td class="vocemenulaterale"><a href="javascript:inserisciParametro();"  
							tabindex="1500" title="Aggiungi parametro al modello">Salva</a></td>
				</tr>
				<tr>
					<td class="vocemenulaterale"><a href="javascript:listaParametriModello(${idModello});"  
							tabindex="1501" title="Annulla">Annulla</a></td>
				</tr>
			</c:when>
		</c:choose>
	<c:if test='${isNavigazioneDisattiva ne "1"}'>
		<tr>
			<td>&nbsp;</td>
		</tr>
		<tr>
    	<td class="titolomenulaterale">Gestione Modelli</td>
	  </tr>
		<!--tr>
    	<td class="vocemenulaterale"><a href="javascript:ricercaModelli();" 
    			tabindex="1510" title="Vai a ricerca modelli">Ricerca</a></td>
	  </tr>	
	  <tr>
    	<td class="vocemenulaterale"><a href="javascript:listaModelli();" 
    			tabindex="1511" title="Vai a lista modelli">Lista</a></td>
	  </tr-->
  	<tr>
    	<td class="vocemenulaterale"><a href="javascript:creaModello();" 
    			tabindex="1512" title="Crea nuovo modello">Nuovo</a></td>
	  </tr>
	</c:if>
	  <tr>
	  	<td>&nbsp;</td>
	  </tr>
<jsp:include page="/WEB-INF/pages/commons/torna.jsp" />
