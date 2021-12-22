/**
 * SimogWS.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package it.avlp.simog.ws.beans;

public interface SimogWS extends java.rmi.Remote {
    public it.avlp.simog.common.beans.ResponseChiudiSession chiudiSessione(java.lang.String ticket) throws java.rmi.RemoteException;
    public it.avlp.simog.common.beans.ResponseModificaGara modificaGara(java.lang.String ticket, java.lang.String indexCollaborazione, java.lang.String id_gara, java.lang.String datiGara) throws java.rmi.RemoteException;
    public it.avlp.simog.common.beans.ResponseModificaLotto modificaLotto(java.lang.String ticket, java.lang.String indexCollaborazione, java.lang.String datiGara, java.lang.String cig) throws java.rmi.RemoteException;
    public it.avlp.simog.common.beans.ResponseCheckLogin login(java.lang.String login, java.lang.String password) throws java.rmi.RemoteException;
    public it.avlp.simog.common.beans.ResponseModificaGara integraDL133(java.lang.String ticket, java.lang.String indexCollaborazione, java.lang.String id_gara, java.lang.String flagDL133) throws java.rmi.RemoteException;
    public it.avlp.simog.common.beans.ResponseCancellaGara cancellaGara(java.lang.String ticket, java.lang.String indexCollaborazione, java.lang.String id_gara, java.lang.String id_motivazione, java.lang.String note_canc) throws java.rmi.RemoteException;
    public it.avlp.simog.common.beans.ResponseInviaRequisiti inviaRequisiti(java.lang.String ticket, java.lang.String indexCollaborazione, java.lang.String datiGara, java.lang.String id_gara) throws java.rmi.RemoteException;
    public it.avlp.simog.common.beans.ResponsePerfezionaGara perfezionaGara(java.lang.String ticket, java.lang.String indexCollaborazione, java.lang.String id_gara) throws java.rmi.RemoteException;
    public it.avlp.simog.common.beans.ResponseConsultaGara consultaGara(java.lang.String ticket, java.lang.String CIG, java.lang.String schede) throws java.rmi.RemoteException;
    public it.avlp.simog.common.beans.ResponseInserisciLotto inserisciLotto(java.lang.String ticket, java.lang.String indexCollaborazione, java.lang.String datiGara, java.lang.String id_gara) throws java.rmi.RemoteException;
    public it.avlp.simog.common.beans.ResponsePerfezionaLotto perfezionaLotto(java.lang.String ticket, java.lang.String indexCollaborazione, java.lang.String dataPubblicazione, java.lang.String dataScadenzaPagamenti, java.lang.String cig, java.lang.String oraScadenza) throws java.rmi.RemoteException;
    public it.avlp.simog.common.beans.ResponseInserisciGara inserisciGara(java.lang.String ticket, java.lang.String indexCollaborazione, java.lang.String datiGara) throws java.rmi.RemoteException;
    public it.avlp.simog.common.beans.ResponsePubblicazioneBando pubblica(java.lang.String ticket, java.lang.String indexCollaborazione, java.lang.String dataPubblicazione, java.lang.String dataScadenzaPagamenti, java.lang.String cig, java.lang.String progCui, java.lang.String datiPubblicazione, java.lang.String tipoOperazione, it.avlp.simog.beans.AllegatoType[] allegato, java.lang.String oraScadenza, java.lang.String dataScadenzaRichiestaInvito, java.lang.String dataLetteraInvito, it.avlp.simog.beans.CUPLOTTO[] CUPLOTTO) throws java.rmi.RemoteException;
    public it.avlp.simog.common.beans.ResponseIntegraCUP integraCUP(java.lang.String ticket, java.lang.String indexCollaborazione, java.lang.String cig, java.lang.String flagCUP, it.avlp.simog.beans.TIPIAPPALTO TIPIAPPALTOL, it.avlp.simog.beans.TIPIAPPALTO TIPIAPPALTOFS, it.avlp.simog.beans.CUPLOTTO CUPLOTTO) throws java.rmi.RemoteException;
    public it.avlp.simog.common.beans.ResponseCancellaLotto cancellaLotto(java.lang.String ticket, java.lang.String indexCollaborazione, java.lang.String id_motivazione, java.lang.String note_canc, java.lang.String cig) throws java.rmi.RemoteException;
    public it.avlp.simog.common.beans.ResponsePresaCarico presaInCarico(java.lang.String ticket, java.lang.String indexCollaborazione, java.lang.String garaOcig, java.lang.String estremiProvv, java.lang.String flagDatiComuni) throws java.rmi.RemoteException;
}
