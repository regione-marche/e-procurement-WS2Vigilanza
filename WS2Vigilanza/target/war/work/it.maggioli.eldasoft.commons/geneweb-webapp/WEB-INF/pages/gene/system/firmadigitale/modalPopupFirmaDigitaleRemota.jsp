<%
/*
 * Created on: 04/06/2010
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */

/*
	Descrizione:
		Finestra che visualizza la conferma per l'inserimento delle pubblicazioni bando/esito predefinite
*/
%>

<style type="text/css">
			.ui-dialog-titlebar {
				display: none;
			}
			#sottoTitolo{
				font:11px Verdana, Arial, Helvetica, sans-serif;
			}
</style>
		
<div id="dialog-form" style="display:none">
  <br>
  <b>Firma digitale del documento</b>
  <br>
  <br>
 <span id="sottoTitolo">Documento da firmare: <span id='nomeDocumento'> </span></span>
  <br>
  <p class="validateTips">Per procedere alla firma digitale del documento, immettere le credenziali e la tipologia di firma:</p>
 <span id="errorMessage" style="color:red;display:none"><br></span>
  <form>
	  <table class="dati-login" style="width:auto!important;">
	  <tr>
		<td class="etichetta-dato">Utente&nbsp&nbsp
		</td>
		<td class="valore-dato">
			<input id="user-dialog" name="username" title="Username" class="testo" type="text" size="24" value="" maxlength="100"/>
		</td>
	  </tr>
	  <tr>
		<td class="etichetta-dato">Password&nbsp&nbsp
		</td>
		<td class="valore-dato">
			<input id="pass-dialog" name="password" title="Password" class="testo" type="password" size="24" value="" maxlength="100" autocomplete="new-password"/>
		</td>
	  </tr>
	  <tr>
		<td colspan="2">
		<br>
		<input type="radio" value="cades" name="modalitaFirma" id="CAdES" checked="true"/><span>Firma P7M (CAdES)&nbsp&nbsp&nbsp&nbsp</span>
		<input type="radio" value="pades" name="modalitaFirma" id="PAdES"/><span id="padesLabel" >Firma PDF (PAdES)</span>
		</td>
	  </tr>
	  </table>
  </form>
</div>

<script type="text/javascript"> 

function _nowait() {
	document.getElementById('bloccaScreen').style.visibility = 'hidden';
	document.getElementById('wait').style.visibility = 'hidden';
}

var passwordFirma;
var usernameFirma;

function openModal(idprg,iddocdig,nome,contextPath,coacod){
	
	$( "#nomeDocumento" ).html("<b>" + nome + "</b>");
	
	var vet = nome.split(".");
	var ext = vet[vet.length-1];
	ext = ext.toUpperCase();
	if(ext != 'PDF'){
		$("#PAdES").attr('disabled', true);
		$("#padesLabel").css('color', 'grey');
	}
	
	$( "#dialog-form" ).css({
		"display":"block",
	});
	$( "#dialog-form" ).dialog({
	  resizable: false,
	  height: "auto",
	  width: 500,
	  position: { my: "center", of: ".contenitore-arealavoro"},
	  modal: true,
	  close: function() {
	  },
	  buttons: {
		"Conferma": function() {
			var psw = $("#pass-dialog").val();
			var usr = $("#user-dialog").val();
			if(!psw || !usr){
				alert("I campi username e password sono obbligatori");
				return;
			}
			setCredenziali(idprg,iddocdig,contextPath,coacod);
		},
		"Annulla": function() {
		  $( this ).dialog( "close" );
		}
	  }
	});
	
	$.ajax({
		url: contextPath+'/SetFirmaDigitaleRemota.do',
		type: 'POST',
		async: false,
		dataType: 'json',
		data: {
			metodo: 'getCredenziali'
		},
		success: function(data) {
				passwordFirma = data.password;
				usernameFirma = data.username;
				$("#pass-dialog").val(passwordFirma);
				$("#user-dialog").val(usernameFirma);
		},
		error: function() {
			_nowait();
			alert("Errore nel caricamento della tabella");
		}
	});
	
}

function setCredenziali(idprg,iddocdig,contextPath,coacod){
	document.getElementById('bloccaScreen').style.visibility='visible';
	$('#bloccaScreen').css("width",$(document).width());
	$('#bloccaScreen').css("height",$(document).height());
	document.getElementById('wait').style.visibility='visible';
	$("#wait").offset({ top: $(window).height() / 2, left: ($(window).width() / 2) - 200});
	var modalitaFirma = $('input[name=modalitaFirma]:checked').val();
	if(modalitaFirma == null){alert("Selezionare una tipologia di firma");return;}
	$.ajax({
		url: contextPath+'/SetFirmaDigitaleRemota.do',
		type: 'POST',
		async: true,
		dataType: 'json',
		data: {
			metodo: 'setFirmaDigitale',
			password: $("#pass-dialog").val(),
			username: $("#user-dialog").val(),
			idprg: idprg,
			iddocdig: iddocdig,
			c0acod: coacod,
			modalitaFirma : modalitaFirma,
		},
		success: function(data) {
			var response = data.message;
			if(response != "ok"){
				$("#errorMessage").html(response);
				$("#errorMessage").css("display","block");
				_nowait();
			}else{
				historyReload();
			}
			
		},
		error: function() {
			_nowait();
			alert("Errore nel caricamento della tabella");
		}
	});
}

</script>



