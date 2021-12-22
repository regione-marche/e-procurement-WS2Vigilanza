<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">

<html>
	<head>
		<title>${applicationScope.appTitle}</title>
		<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
		<meta http-equiv="Cache-Control" content="no-cache">
		<meta http-equiv="Pragma" content="no-cache">
		<meta http-equiv="Expires" content="-1">
	</head>
	<body>
		<div id="startup">
			Test startup webapp = <c:choose><c:when test='${applicationScope.appLoaded eq "1"}'>OK</c:when><c:otherwise>KO</c:otherwise></c:choose>
		</div>
		<div id="attivazione">
			Test attivazione = <c:choose><c:when test='${applicationScope.bloccoAttivazione eq "1"}'>Da attivare</c:when><c:otherwise>Attivata</c:otherwise></c:choose>
		</div>
		<c:set var='esisteELDAVER' value='${gene:isTable(pageContext, "ELDAVER")}' />
		<div id="db">
			Test DB = <c:choose> <c:when test="${esisteELDAVER}">OK</c:when><c:otherwise>KO</c:otherwise></c:choose>
		</div>
	</body>
</html>
