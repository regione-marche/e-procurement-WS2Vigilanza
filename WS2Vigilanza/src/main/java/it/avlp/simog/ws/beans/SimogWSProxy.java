package it.avlp.simog.ws.beans;

public class SimogWSProxy implements it.avlp.simog.ws.beans.SimogWS {
  private String _endpoint = null;
  private it.avlp.simog.ws.beans.SimogWS simogWS = null;
  
  public SimogWSProxy() {
    _initSimogWSProxy();
  }
  
  public SimogWSProxy(String endpoint) {
    _endpoint = endpoint;
    _initSimogWSProxy();
  }
  
  private void _initSimogWSProxy() {
    try {
      simogWS = (new it.avlp.simog.ws.beans.SimogWSServiceLocator()).getSimogWS();
      if (simogWS != null) {
        if (_endpoint != null)
          ((javax.xml.rpc.Stub)simogWS)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
        else
          _endpoint = (String)((javax.xml.rpc.Stub)simogWS)._getProperty("javax.xml.rpc.service.endpoint.address");
      }
      
    }
    catch (javax.xml.rpc.ServiceException serviceException) {}
  }
  
  public String getEndpoint() {
    return _endpoint;
  }
  
  public void setEndpoint(String endpoint) {
    _endpoint = endpoint;
    if (simogWS != null)
      ((javax.xml.rpc.Stub)simogWS)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
    
  }
  
  public it.avlp.simog.ws.beans.SimogWS getSimogWS() {
    if (simogWS == null)
      _initSimogWSProxy();
    return simogWS;
  }
  
  public it.avlp.simog.common.beans.ResponseChiudiSession chiudiSessione(java.lang.String ticket) throws java.rmi.RemoteException{
    if (simogWS == null)
      _initSimogWSProxy();
    return simogWS.chiudiSessione(ticket);
  }
  
  public it.avlp.simog.common.beans.ResponseModificaGara modificaGara(java.lang.String ticket, java.lang.String indexCollaborazione, java.lang.String id_gara, java.lang.String datiGara) throws java.rmi.RemoteException{
    if (simogWS == null)
      _initSimogWSProxy();
    return simogWS.modificaGara(ticket, indexCollaborazione, id_gara, datiGara);
  }
  
  public it.avlp.simog.common.beans.ResponseModificaLotto modificaLotto(java.lang.String ticket, java.lang.String indexCollaborazione, java.lang.String datiGara, java.lang.String cig) throws java.rmi.RemoteException{
    if (simogWS == null)
      _initSimogWSProxy();
    return simogWS.modificaLotto(ticket, indexCollaborazione, datiGara, cig);
  }
  
  public it.avlp.simog.common.beans.ResponseCheckLogin login(java.lang.String login, java.lang.String password) throws java.rmi.RemoteException{
    if (simogWS == null)
      _initSimogWSProxy();
    return simogWS.login(login, password);
  }
  
  public it.avlp.simog.common.beans.ResponseModificaGara integraDL133(java.lang.String ticket, java.lang.String indexCollaborazione, java.lang.String id_gara, java.lang.String flagDL133) throws java.rmi.RemoteException{
    if (simogWS == null)
      _initSimogWSProxy();
    return simogWS.integraDL133(ticket, indexCollaborazione, id_gara, flagDL133);
  }
  
  public it.avlp.simog.common.beans.ResponseCancellaGara cancellaGara(java.lang.String ticket, java.lang.String indexCollaborazione, java.lang.String id_gara, java.lang.String id_motivazione, java.lang.String note_canc) throws java.rmi.RemoteException{
    if (simogWS == null)
      _initSimogWSProxy();
    return simogWS.cancellaGara(ticket, indexCollaborazione, id_gara, id_motivazione, note_canc);
  }
  
  public it.avlp.simog.common.beans.ResponseInviaRequisiti inviaRequisiti(java.lang.String ticket, java.lang.String indexCollaborazione, java.lang.String datiGara, java.lang.String id_gara) throws java.rmi.RemoteException{
    if (simogWS == null)
      _initSimogWSProxy();
    return simogWS.inviaRequisiti(ticket, indexCollaborazione, datiGara, id_gara);
  }
  
  public it.avlp.simog.common.beans.ResponsePerfezionaGara perfezionaGara(java.lang.String ticket, java.lang.String indexCollaborazione, java.lang.String id_gara) throws java.rmi.RemoteException{
    if (simogWS == null)
      _initSimogWSProxy();
    return simogWS.perfezionaGara(ticket, indexCollaborazione, id_gara);
  }
  
  public it.avlp.simog.common.beans.ResponseConsultaGara consultaGara(java.lang.String ticket, java.lang.String CIG, java.lang.String schede) throws java.rmi.RemoteException{
    if (simogWS == null)
      _initSimogWSProxy();
    return simogWS.consultaGara(ticket, CIG, schede);
  }
  
  public it.avlp.simog.common.beans.ResponseInserisciLotto inserisciLotto(java.lang.String ticket, java.lang.String indexCollaborazione, java.lang.String datiGara, java.lang.String id_gara) throws java.rmi.RemoteException{
    if (simogWS == null)
      _initSimogWSProxy();
    return simogWS.inserisciLotto(ticket, indexCollaborazione, datiGara, id_gara);
  }
  
  public it.avlp.simog.common.beans.ResponsePerfezionaLotto perfezionaLotto(java.lang.String ticket, java.lang.String indexCollaborazione, java.lang.String dataPubblicazione, java.lang.String dataScadenzaPagamenti, java.lang.String cig, java.lang.String oraScadenza) throws java.rmi.RemoteException{
    if (simogWS == null)
      _initSimogWSProxy();
    return simogWS.perfezionaLotto(ticket, indexCollaborazione, dataPubblicazione, dataScadenzaPagamenti, cig, oraScadenza);
  }
  
  public it.avlp.simog.common.beans.ResponseInserisciGara inserisciGara(java.lang.String ticket, java.lang.String indexCollaborazione, java.lang.String datiGara) throws java.rmi.RemoteException{
    if (simogWS == null)
      _initSimogWSProxy();
    return simogWS.inserisciGara(ticket, indexCollaborazione, datiGara);
  }
  
  public it.avlp.simog.common.beans.ResponsePubblicazioneBando pubblica(java.lang.String ticket, java.lang.String indexCollaborazione, java.lang.String dataPubblicazione, java.lang.String dataScadenzaPagamenti, java.lang.String cig, java.lang.String progCui, java.lang.String datiPubblicazione, java.lang.String tipoOperazione, it.avlp.simog.beans.AllegatoType[] allegato, java.lang.String oraScadenza, java.lang.String dataScadenzaRichiestaInvito, java.lang.String dataLetteraInvito, it.avlp.simog.beans.CUPLOTTO[] CUPLOTTO) throws java.rmi.RemoteException{
    if (simogWS == null)
      _initSimogWSProxy();
    return simogWS.pubblica(ticket, indexCollaborazione, dataPubblicazione, dataScadenzaPagamenti, cig, progCui, datiPubblicazione, tipoOperazione, allegato, oraScadenza, dataScadenzaRichiestaInvito, dataLetteraInvito, CUPLOTTO);
  }
  
  public it.avlp.simog.common.beans.ResponseIntegraCUP integraCUP(java.lang.String ticket, java.lang.String indexCollaborazione, java.lang.String cig, java.lang.String flagCUP, it.avlp.simog.beans.TIPIAPPALTO TIPIAPPALTOL, it.avlp.simog.beans.TIPIAPPALTO TIPIAPPALTOFS, it.avlp.simog.beans.CUPLOTTO CUPLOTTO) throws java.rmi.RemoteException{
    if (simogWS == null)
      _initSimogWSProxy();
    return simogWS.integraCUP(ticket, indexCollaborazione, cig, flagCUP, TIPIAPPALTOL, TIPIAPPALTOFS, CUPLOTTO);
  }
  
  public it.avlp.simog.common.beans.ResponseCancellaLotto cancellaLotto(java.lang.String ticket, java.lang.String indexCollaborazione, java.lang.String id_motivazione, java.lang.String note_canc, java.lang.String cig) throws java.rmi.RemoteException{
    if (simogWS == null)
      _initSimogWSProxy();
    return simogWS.cancellaLotto(ticket, indexCollaborazione, id_motivazione, note_canc, cig);
  }
  
  public it.avlp.simog.common.beans.ResponsePresaCarico presaInCarico(java.lang.String ticket, java.lang.String indexCollaborazione, java.lang.String garaOcig, java.lang.String estremiProvv, java.lang.String flagDatiComuni) throws java.rmi.RemoteException{
    if (simogWS == null)
      _initSimogWSProxy();
    return simogWS.presaInCarico(ticket, indexCollaborazione, garaOcig, estremiProvv, flagDatiComuni);
  }
  
  
}