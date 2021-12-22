/**
 * SimogWSService.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package it.avlp.simog.ws.beans;

public interface SimogWSService extends javax.xml.rpc.Service {
    public java.lang.String getSimogWSAddress();

    public it.avlp.simog.ws.beans.SimogWS getSimogWS() throws javax.xml.rpc.ServiceException;

    public it.avlp.simog.ws.beans.SimogWS getSimogWS(java.net.URL portAddress) throws javax.xml.rpc.ServiceException;
}
