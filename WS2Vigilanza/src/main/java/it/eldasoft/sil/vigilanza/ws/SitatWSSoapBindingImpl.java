/**
 * SitatWSSoapBindingImpl.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package it.eldasoft.sil.vigilanza.ws;

import it.eldasoft.sil.vigilanza.bl.WsSitatFacade;
import it.eldasoft.utils.spring.SpringAppContext;

import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

public class SitatWSSoapBindingImpl implements it.eldasoft.sil.vigilanza.ws.SitatWS {
	
	public it.eldasoft.sil.vigilanza.ws.beans.ResponseType istanziaGara(
			it.eldasoft.sil.vigilanza.ws.beans.LoginType login,
			it.eldasoft.sil.vigilanza.ws.beans.IstanzaOggettoType garaLotti)
			throws java.rmi.RemoteException {
		ApplicationContext ctx = WebApplicationContextUtils
				.getWebApplicationContext(SpringAppContext.getServletContext());
		WsSitatFacade wsFacade = (WsSitatFacade) ctx.getBean("wsSitatFacade");

		return wsFacade.istanziaGara(login, garaLotti);
	}

	public it.eldasoft.sil.vigilanza.ws.beans.ResponseType istanziaAggiudicazione(
			it.eldasoft.sil.vigilanza.ws.beans.LoginType login,
			it.eldasoft.sil.vigilanza.ws.beans.IstanzaOggettoType aggiudicazione)
			throws java.rmi.RemoteException {
		ApplicationContext ctx = WebApplicationContextUtils
				.getWebApplicationContext(SpringAppContext.getServletContext());
		WsSitatFacade wsFacade = (WsSitatFacade) ctx.getBean("wsSitatFacade");

		return wsFacade.istanziaAggiudicazione(login, aggiudicazione);

	}

	public it.eldasoft.sil.vigilanza.ws.beans.ResponseType istanziaEsito(
			it.eldasoft.sil.vigilanza.ws.beans.LoginType login,
			it.eldasoft.sil.vigilanza.ws.beans.IstanzaOggettoType esito)
			throws java.rmi.RemoteException {
		ApplicationContext ctx = WebApplicationContextUtils
				.getWebApplicationContext(SpringAppContext.getServletContext());
		WsSitatFacade wsFacade = (WsSitatFacade) ctx.getBean("wsSitatFacade");

		return wsFacade.istanziaEsito(login, esito);
	}

	public it.eldasoft.sil.vigilanza.ws.beans.ResponseType istanziaContratto(
			it.eldasoft.sil.vigilanza.ws.beans.LoginType login,
			it.eldasoft.sil.vigilanza.ws.beans.IstanzaOggettoType contratto)
			throws java.rmi.RemoteException {
		ApplicationContext ctx = WebApplicationContextUtils
				.getWebApplicationContext(SpringAppContext.getServletContext());
		WsSitatFacade wsFacade = (WsSitatFacade) ctx.getBean("wsSitatFacade");

		return wsFacade.istanziaContratto(login, contratto);
	}

	public it.eldasoft.sil.vigilanza.ws.beans.ResponseType istanziaInizio(
			it.eldasoft.sil.vigilanza.ws.beans.LoginType login,
			it.eldasoft.sil.vigilanza.ws.beans.IstanzaOggettoType inizio)
			throws java.rmi.RemoteException {
		ApplicationContext ctx = WebApplicationContextUtils
				.getWebApplicationContext(SpringAppContext.getServletContext());
		WsSitatFacade wsFacade = (WsSitatFacade) ctx.getBean("wsSitatFacade");

		return wsFacade.istanziaInizio(login, inizio);
	}

	public it.eldasoft.sil.vigilanza.ws.beans.ResponseType istanziaAvanzamento(
			it.eldasoft.sil.vigilanza.ws.beans.LoginType login,
			it.eldasoft.sil.vigilanza.ws.beans.IstanzaOggettoType avanzamento)
			throws java.rmi.RemoteException {
		ApplicationContext ctx = WebApplicationContextUtils
				.getWebApplicationContext(SpringAppContext.getServletContext());
		WsSitatFacade wsFacade = (WsSitatFacade) ctx.getBean("wsSitatFacade");

		return wsFacade.istanziaAvanzamento(login, avanzamento);
	}

	public it.eldasoft.sil.vigilanza.ws.beans.ResponseType istanziaSospensione(
			it.eldasoft.sil.vigilanza.ws.beans.LoginType login,
			it.eldasoft.sil.vigilanza.ws.beans.IstanzaOggettoType sospensione)
			throws java.rmi.RemoteException {
		ApplicationContext ctx = WebApplicationContextUtils
				.getWebApplicationContext(SpringAppContext.getServletContext());
		WsSitatFacade wsFacade = (WsSitatFacade) ctx.getBean("wsSitatFacade");

		return wsFacade.istanziaSospensione(login, sospensione);
	}

	public it.eldasoft.sil.vigilanza.ws.beans.ResponseType istanziaVariante(
			it.eldasoft.sil.vigilanza.ws.beans.LoginType login,
			it.eldasoft.sil.vigilanza.ws.beans.IstanzaOggettoType variante)
			throws java.rmi.RemoteException {
		ApplicationContext ctx = WebApplicationContextUtils
				.getWebApplicationContext(SpringAppContext.getServletContext());
		WsSitatFacade wsFacade = (WsSitatFacade) ctx.getBean("wsSitatFacade");

		return wsFacade.istanziaVariante(login, variante);
	}

	public it.eldasoft.sil.vigilanza.ws.beans.ResponseType istanziaSubappalto(
			it.eldasoft.sil.vigilanza.ws.beans.LoginType login,
			it.eldasoft.sil.vigilanza.ws.beans.IstanzaOggettoType subappalto)
			throws java.rmi.RemoteException {
		ApplicationContext ctx = WebApplicationContextUtils
				.getWebApplicationContext(SpringAppContext.getServletContext());
		WsSitatFacade wsFacade = (WsSitatFacade) ctx.getBean("wsSitatFacade");

		return wsFacade.istanziaSubappalto(login, subappalto);
	}

	public it.eldasoft.sil.vigilanza.ws.beans.ResponseType istanziaConclusione(
			it.eldasoft.sil.vigilanza.ws.beans.LoginType login,
			it.eldasoft.sil.vigilanza.ws.beans.IstanzaOggettoType conclusione)
			throws java.rmi.RemoteException {
		ApplicationContext ctx = WebApplicationContextUtils
				.getWebApplicationContext(SpringAppContext.getServletContext());
		WsSitatFacade wsFacade = (WsSitatFacade) ctx.getBean("wsSitatFacade");

		return wsFacade.istanziaConclusione(login, conclusione);
	}

	public it.eldasoft.sil.vigilanza.ws.beans.ResponseType istanziaCollaudo(
			it.eldasoft.sil.vigilanza.ws.beans.LoginType login,
			it.eldasoft.sil.vigilanza.ws.beans.IstanzaOggettoType collaudo)
			throws java.rmi.RemoteException {
		ApplicationContext ctx = WebApplicationContextUtils
				.getWebApplicationContext(SpringAppContext.getServletContext());
		WsSitatFacade wsFacade = (WsSitatFacade) ctx.getBean("wsSitatFacade");

		return wsFacade.istanziaCollaudo(login, collaudo);
	}
	
  public it.eldasoft.sil.vigilanza.ws.beans.ResponseType istanziaElencoImpreseInvitate(
			it.eldasoft.sil.vigilanza.ws.beans.LoginType login, 
			it.eldasoft.sil.vigilanza.ws.beans.IstanzaOggettoType elencoImpreseInvitate) 
			throws java.rmi.RemoteException {
				
		ApplicationContext ctx = WebApplicationContextUtils
				.getWebApplicationContext(SpringAppContext.getServletContext());
		WsSitatFacade wsFacade = (WsSitatFacade) ctx.getBean("wsSitatFacade");
        return wsFacade.istanziaElencoImpreseInvitate(login, elencoImpreseInvitate);
  }

	public it.eldasoft.sil.vigilanza.ws.beans.ResponseAvvisoType istanziaAvviso(
			it.eldasoft.sil.vigilanza.ws.beans.LoginType login,
			it.eldasoft.sil.vigilanza.ws.beans.IstanzaOggettoType avviso)
			throws java.rmi.RemoteException {
		ApplicationContext ctx = WebApplicationContextUtils
				.getWebApplicationContext(SpringAppContext.getServletContext());
		WsSitatFacade wsFacade = (WsSitatFacade) ctx.getBean("wsSitatFacade");

		return wsFacade.istanziaAvviso(login, avviso);
	}

  public it.eldasoft.sil.vigilanza.ws.beans.ResponsePubblicazioneType istanziaPubblicazioneDocumenti(
  		it.eldasoft.sil.vigilanza.ws.beans.LoginType login, 
  		it.eldasoft.sil.vigilanza.ws.beans.IstanzaOggettoType pubblicazioneDocumenti)
  		throws java.rmi.RemoteException {
  	
  	ApplicationContext ctx = WebApplicationContextUtils
				.getWebApplicationContext(SpringAppContext.getServletContext());
  	WsSitatFacade wsFacade = (WsSitatFacade) ctx.getBean("wsSitatFacade");
  	
    return wsFacade.istanziaPubblicazioneDocumenti(login, pubblicazioneDocumenti);
  }

}
