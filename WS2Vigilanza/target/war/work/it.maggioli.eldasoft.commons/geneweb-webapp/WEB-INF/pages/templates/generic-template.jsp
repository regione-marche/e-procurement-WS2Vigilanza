<%/*
       * Created on 22-set-2006
       *
       * Copyright (c) EldaSoft S.p.A.
       * Tutti i diritti sono riservati.
       *
       * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
       * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
       * aver prima formalizzato un accordo specifico con EldaSoft.
       */

      // PAGINA CHE CONTIENE IL TEMPLATE DELLA PAGINA DI RICERCA
      %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">

<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>

<c:set var="contextPath" value="${pageContext.request.contextPath}" />

<HTML>
<HEAD>
<jsp:include page="/WEB-INF/pages/commons/headStd.jsp" />
<script type="text/javascript" src="${contextPath}/js/forms.js"></script>

<script type="text/javascript">
<!--
<jsp:include page="/WEB-INF/pages/commons/checkDisabilitaBack.jsp" />

$(function() {
	$( "input.data" ).datepicker($.datepicker.regional[ "it" ]);
});
	window.onfocus=fnFocus;
 
  // al click nel documento si chiudono popup e menu
  if (ie4||ns6) document.onclick=hideSovrapposizioni;

  function hideSovrapposizioni() {
    hideMenuPopup();
    hideSubmenuNavbar();
  }
-->
</script>
  <gene:insert name="linkJSSubMenuComune"    src="/WEB-INF/pages/commons/jsSubMenuComune.jsp" />
  <gene:insert name="linkJSSubMenuSpecifico" src="/WEB-INF/pages/commons/jsSubMenuSpecifico.jsp" />
  <gene:insert name="jsAzioniTags" src="/WEB-INF/pages/commons/jsAzioniTags.jsp" />
  <gene:insert name="head" />
</HEAD>

<BODY onload="setVariables();checkLocation();initPage();">
<jsp:include page="/WEB-INF/pages/commons/bloccaRichieste.jsp" />
<TABLE class="arealayout">
<%/*questa definizione dei gruppi di colonne serve a fissare la dimensione dei td in modo da vincolare la posizione iniziale del men� di navigazione
	     sopra l area lavoro appena al termine del men� contestuale */%>
	<colgroup width="150px"></colgroup>
	<colgroup width="*"></colgroup>
  <TBODY>
    <TR class="testata">
      <TD colspan="2"><gene:insert name="testata" src="/WEB-INF/pages/commons/testata.jsp"/></TD>
    </TR>
		<TR class="menuprincipale" >
			<TD><img src="${contextPath}/img/spacer-azionicontesto.gif" alt=""></TD>
			<TD>
        <table class="contenitore-navbar">
          <tbody>
            <tr>
            <c:if test="${! empty sessionScope.profiloUtente && ! empty profiloAttivo}">
              <gene:insert name="menuSpecifico" src="/WEB-INF/pages/commons/menuSpecifico.jsp" />
              <gene:insert name="menuComune" src="/WEB-INF/pages/commons/menuComune.jsp"/>
            </c:if>
            </tr>
          </tbody>
        </table>

        <%//PARTE NECESSARIA PER VISUALIZZARE I SOTTOMENU DEL MENU PRINCIPALE DI NAVIGAZIONE %>
        <IFRAME class="gene" id="iframesubnavmenu"></iframe>
        <div id="subnavmenu" class="subnavbarmenuskin"
          onMouseover="highlightSubmenuNavbar(event,'on');"
          onMouseout="highlightSubmenuNavbar(event,'off');"></div>
      </TD>
    </TR>
    <TR>
      <TD class="menuazioni" valign="top"><gene:insert name="menuAzioni" /></TD>
      <TD class="arealavoro">

	  <gene:insert name="preTitolo" >
	  <jsp:include page="/WEB-INF/pages/commons/areaPreTitolo.jsp" />
	  </gene:insert>
				
					<gene:insert name="corpo" >
						<b><big>INSERISCI IL CORPO !!!</big></b>
					</gene:insert>
					<%//!-- Insert tag lasciato per la ridefinizione dei messaggi di debug --%>
					<%// Insert tag lasciato per la ridefinizione dei messaggi di debug %>
					<gene:insert name="debug" />
					<gene:insert name="debugDefault" src="/WEB-INF/pages/commons/sviluppo/sviluppo-debug.jsp"/>
				
      <%//!-- PARTE NECESSARIA PER VISUALIZZARE I POPUP MENU DI OPZIONI PER CAMPO --%>
      <IFRAME class="gene" id="iframepopmenu"></iframe>
      <div id="popmenu" class="popupmenuskin"
        onMouseover="highlightMenuPopup(event,'on');"
        onMouseout="highlightMenuPopup(event,'off');"></div>
      </TD>
    </TR>
		
	<TR>
		<TD COLSPAN="2">
		<div id="footer">
			<gene:insert name="footer" src="/WEB-INF/pages/commons/footer.jsp" />
		</div>
		</TD>
	</TR>

  </TBODY>
</TABLE>

<%//!-- CAMPO INDISPENSABILE PER ESEGUIRE IL COPIA/INCOLLA NEGLI APPUNTI DEL MNEMONICO CON IE --%>
<form action=""><input type="hidden" id="clipboard"></form>
<%//!-- Inserimento degli eventuali Javascript {MF280906} --%>
<gene:outJavaScript/>

</BODY>
</HTML>