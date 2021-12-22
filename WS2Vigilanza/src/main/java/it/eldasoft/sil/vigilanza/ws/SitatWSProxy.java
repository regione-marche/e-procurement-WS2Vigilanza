package it.eldasoft.sil.vigilanza.ws;

public class SitatWSProxy implements it.eldasoft.sil.vigilanza.ws.SitatWS {
  private String _endpoint = null;
  private it.eldasoft.sil.vigilanza.ws.SitatWS sitatWS = null;
  
  public SitatWSProxy() {
    _initSitatWSProxy();
  }
  
  public SitatWSProxy(String endpoint) {
    _endpoint = endpoint;
    _initSitatWSProxy();
  }
  
  private void _initSitatWSProxy() {
    try {
      sitatWS = (new it.eldasoft.sil.vigilanza.ws.SitatWSServiceLocator()).getSitatWS();
      if (sitatWS != null) {
        if (_endpoint != null)
          ((javax.xml.rpc.Stub)sitatWS)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
        else
          _endpoint = (String)((javax.xml.rpc.Stub)sitatWS)._getProperty("javax.xml.rpc.service.endpoint.address");
      }
      
    }
    catch (javax.xml.rpc.ServiceException serviceException) {}
  }
  
  public String getEndpoint() {
    return _endpoint;
  }
  
  public void setEndpoint(String endpoint) {
    _endpoint = endpoint;
    if (sitatWS != null)
      ((javax.xml.rpc.Stub)sitatWS)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
    
  }
  
  public it.eldasoft.sil.vigilanza.ws.SitatWS getSitatWS() {
    if (sitatWS == null)
      _initSitatWSProxy();
    return sitatWS;
  }
  
  public it.eldasoft.sil.vigilanza.ws.beans.ResponseType istanziaGara(it.eldasoft.sil.vigilanza.ws.beans.LoginType login, it.eldasoft.sil.vigilanza.ws.beans.IstanzaOggettoType garaLotti) throws java.rmi.RemoteException{
    if (sitatWS == null)
      _initSitatWSProxy();
    return sitatWS.istanziaGara(login, garaLotti);
  }
  
  public it.eldasoft.sil.vigilanza.ws.beans.ResponseType istanziaAggiudicazione(it.eldasoft.sil.vigilanza.ws.beans.LoginType login, it.eldasoft.sil.vigilanza.ws.beans.IstanzaOggettoType aggiudicazione) throws java.rmi.RemoteException{
    if (sitatWS == null)
      _initSitatWSProxy();
    return sitatWS.istanziaAggiudicazione(login, aggiudicazione);
  }
  
  public it.eldasoft.sil.vigilanza.ws.beans.ResponseType istanziaEsito(it.eldasoft.sil.vigilanza.ws.beans.LoginType login, it.eldasoft.sil.vigilanza.ws.beans.IstanzaOggettoType esito) throws java.rmi.RemoteException{
    if (sitatWS == null)
      _initSitatWSProxy();
    return sitatWS.istanziaEsito(login, esito);
  }
  
  public it.eldasoft.sil.vigilanza.ws.beans.ResponseType istanziaContratto(it.eldasoft.sil.vigilanza.ws.beans.LoginType login, it.eldasoft.sil.vigilanza.ws.beans.IstanzaOggettoType contratto) throws java.rmi.RemoteException{
    if (sitatWS == null)
      _initSitatWSProxy();
    return sitatWS.istanziaContratto(login, contratto);
  }
  
  public it.eldasoft.sil.vigilanza.ws.beans.ResponseType istanziaInizio(it.eldasoft.sil.vigilanza.ws.beans.LoginType login, it.eldasoft.sil.vigilanza.ws.beans.IstanzaOggettoType inizio) throws java.rmi.RemoteException{
    if (sitatWS == null)
      _initSitatWSProxy();
    return sitatWS.istanziaInizio(login, inizio);
  }
  
  public it.eldasoft.sil.vigilanza.ws.beans.ResponseType istanziaAvanzamento(it.eldasoft.sil.vigilanza.ws.beans.LoginType login, it.eldasoft.sil.vigilanza.ws.beans.IstanzaOggettoType avanzamento) throws java.rmi.RemoteException{
    if (sitatWS == null)
      _initSitatWSProxy();
    return sitatWS.istanziaAvanzamento(login, avanzamento);
  }
  
  public it.eldasoft.sil.vigilanza.ws.beans.ResponseType istanziaSospensione(it.eldasoft.sil.vigilanza.ws.beans.LoginType login, it.eldasoft.sil.vigilanza.ws.beans.IstanzaOggettoType sospensione) throws java.rmi.RemoteException{
    if (sitatWS == null)
      _initSitatWSProxy();
    return sitatWS.istanziaSospensione(login, sospensione);
  }
  
  public it.eldasoft.sil.vigilanza.ws.beans.ResponseType istanziaVariante(it.eldasoft.sil.vigilanza.ws.beans.LoginType login, it.eldasoft.sil.vigilanza.ws.beans.IstanzaOggettoType variante) throws java.rmi.RemoteException{
    if (sitatWS == null)
      _initSitatWSProxy();
    return sitatWS.istanziaVariante(login, variante);
  }
  
  public it.eldasoft.sil.vigilanza.ws.beans.ResponseType istanziaSubappalto(it.eldasoft.sil.vigilanza.ws.beans.LoginType login, it.eldasoft.sil.vigilanza.ws.beans.IstanzaOggettoType subappalto) throws java.rmi.RemoteException{
    if (sitatWS == null)
      _initSitatWSProxy();
    return sitatWS.istanziaSubappalto(login, subappalto);
  }
  
  public it.eldasoft.sil.vigilanza.ws.beans.ResponseType istanziaConclusione(it.eldasoft.sil.vigilanza.ws.beans.LoginType login, it.eldasoft.sil.vigilanza.ws.beans.IstanzaOggettoType conclusione) throws java.rmi.RemoteException{
    if (sitatWS == null)
      _initSitatWSProxy();
    return sitatWS.istanziaConclusione(login, conclusione);
  }
  
  public it.eldasoft.sil.vigilanza.ws.beans.ResponseType istanziaCollaudo(it.eldasoft.sil.vigilanza.ws.beans.LoginType login, it.eldasoft.sil.vigilanza.ws.beans.IstanzaOggettoType collaudo) throws java.rmi.RemoteException{
    if (sitatWS == null)
      _initSitatWSProxy();
    return sitatWS.istanziaCollaudo(login, collaudo);
  }
  
  public it.eldasoft.sil.vigilanza.ws.beans.ResponseType istanziaElencoImpreseInvitate(it.eldasoft.sil.vigilanza.ws.beans.LoginType login, it.eldasoft.sil.vigilanza.ws.beans.IstanzaOggettoType elencoImpreseInvitate) throws java.rmi.RemoteException{
    if (sitatWS == null)
      _initSitatWSProxy();
    return sitatWS.istanziaElencoImpreseInvitate(login, elencoImpreseInvitate);
  }
  
  public it.eldasoft.sil.vigilanza.ws.beans.ResponseAvvisoType istanziaAvviso(it.eldasoft.sil.vigilanza.ws.beans.LoginType login, it.eldasoft.sil.vigilanza.ws.beans.IstanzaOggettoType avviso) throws java.rmi.RemoteException{
    if (sitatWS == null)
      _initSitatWSProxy();
    return sitatWS.istanziaAvviso(login, avviso);
  }
  
  public it.eldasoft.sil.vigilanza.ws.beans.ResponsePubblicazioneType istanziaPubblicazioneDocumenti(it.eldasoft.sil.vigilanza.ws.beans.LoginType login, it.eldasoft.sil.vigilanza.ws.beans.IstanzaOggettoType pubblicazioneDocumenti) throws java.rmi.RemoteException{
    if (sitatWS == null)
      _initSitatWSProxy();
    return sitatWS.istanziaPubblicazioneDocumenti(login, pubblicazioneDocumenti);
  }
  
  
}