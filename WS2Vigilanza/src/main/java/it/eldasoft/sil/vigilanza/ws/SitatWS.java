/**
 * SitatWS.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package it.eldasoft.sil.vigilanza.ws;

public interface SitatWS extends java.rmi.Remote {
    public it.eldasoft.sil.vigilanza.ws.beans.ResponseType istanziaGara(it.eldasoft.sil.vigilanza.ws.beans.LoginType login, it.eldasoft.sil.vigilanza.ws.beans.IstanzaOggettoType garaLotti) throws java.rmi.RemoteException;
    public it.eldasoft.sil.vigilanza.ws.beans.ResponseType istanziaAggiudicazione(it.eldasoft.sil.vigilanza.ws.beans.LoginType login, it.eldasoft.sil.vigilanza.ws.beans.IstanzaOggettoType aggiudicazione) throws java.rmi.RemoteException;
    public it.eldasoft.sil.vigilanza.ws.beans.ResponseType istanziaEsito(it.eldasoft.sil.vigilanza.ws.beans.LoginType login, it.eldasoft.sil.vigilanza.ws.beans.IstanzaOggettoType esito) throws java.rmi.RemoteException;
    public it.eldasoft.sil.vigilanza.ws.beans.ResponseType istanziaContratto(it.eldasoft.sil.vigilanza.ws.beans.LoginType login, it.eldasoft.sil.vigilanza.ws.beans.IstanzaOggettoType contratto) throws java.rmi.RemoteException;
    public it.eldasoft.sil.vigilanza.ws.beans.ResponseType istanziaInizio(it.eldasoft.sil.vigilanza.ws.beans.LoginType login, it.eldasoft.sil.vigilanza.ws.beans.IstanzaOggettoType inizio) throws java.rmi.RemoteException;
    public it.eldasoft.sil.vigilanza.ws.beans.ResponseType istanziaAvanzamento(it.eldasoft.sil.vigilanza.ws.beans.LoginType login, it.eldasoft.sil.vigilanza.ws.beans.IstanzaOggettoType avanzamento) throws java.rmi.RemoteException;
    public it.eldasoft.sil.vigilanza.ws.beans.ResponseType istanziaSospensione(it.eldasoft.sil.vigilanza.ws.beans.LoginType login, it.eldasoft.sil.vigilanza.ws.beans.IstanzaOggettoType sospensione) throws java.rmi.RemoteException;
    public it.eldasoft.sil.vigilanza.ws.beans.ResponseType istanziaVariante(it.eldasoft.sil.vigilanza.ws.beans.LoginType login, it.eldasoft.sil.vigilanza.ws.beans.IstanzaOggettoType variante) throws java.rmi.RemoteException;
    public it.eldasoft.sil.vigilanza.ws.beans.ResponseType istanziaSubappalto(it.eldasoft.sil.vigilanza.ws.beans.LoginType login, it.eldasoft.sil.vigilanza.ws.beans.IstanzaOggettoType subappalto) throws java.rmi.RemoteException;
    public it.eldasoft.sil.vigilanza.ws.beans.ResponseType istanziaConclusione(it.eldasoft.sil.vigilanza.ws.beans.LoginType login, it.eldasoft.sil.vigilanza.ws.beans.IstanzaOggettoType conclusione) throws java.rmi.RemoteException;
    public it.eldasoft.sil.vigilanza.ws.beans.ResponseType istanziaCollaudo(it.eldasoft.sil.vigilanza.ws.beans.LoginType login, it.eldasoft.sil.vigilanza.ws.beans.IstanzaOggettoType collaudo) throws java.rmi.RemoteException;
    public it.eldasoft.sil.vigilanza.ws.beans.ResponseType istanziaElencoImpreseInvitate(it.eldasoft.sil.vigilanza.ws.beans.LoginType login, it.eldasoft.sil.vigilanza.ws.beans.IstanzaOggettoType elencoImpreseInvitate) throws java.rmi.RemoteException;
    public it.eldasoft.sil.vigilanza.ws.beans.ResponseAvvisoType istanziaAvviso(it.eldasoft.sil.vigilanza.ws.beans.LoginType login, it.eldasoft.sil.vigilanza.ws.beans.IstanzaOggettoType avviso) throws java.rmi.RemoteException;
    public it.eldasoft.sil.vigilanza.ws.beans.ResponsePubblicazioneType istanziaPubblicazioneDocumenti(it.eldasoft.sil.vigilanza.ws.beans.LoginType login, it.eldasoft.sil.vigilanza.ws.beans.IstanzaOggettoType pubblicazioneDocumenti) throws java.rmi.RemoteException;
}
