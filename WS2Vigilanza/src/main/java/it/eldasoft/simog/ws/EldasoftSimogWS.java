/**
 * EldasoftSimogWS.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package it.eldasoft.simog.ws;

public interface EldasoftSimogWS extends java.rmi.Remote {
    public it.eldasoft.simog.ws.EsitoInserisciGaraLotto inserisciGaraLotto(java.lang.String login, java.lang.String password, java.lang.String datiGaraLotto) throws java.rmi.RemoteException;
    public it.eldasoft.simog.ws.EsitoConsultaIDGARA consultaIDGARA(java.lang.String uuid) throws java.rmi.RemoteException;
    public it.eldasoft.simog.ws.EsitoConsultaCIG consultaCIG(java.lang.String uuid) throws java.rmi.RemoteException;
    public it.eldasoft.simog.ws.EsitoVerificaCIG verificaCIG(java.lang.String cig) throws java.rmi.RemoteException;
    public it.eldasoft.simog.ws.EsitoInserisciSmartCIG inserisciSmartCIG(java.lang.String login, java.lang.String password, java.lang.String datiSmartCIG) throws java.rmi.RemoteException;
}
