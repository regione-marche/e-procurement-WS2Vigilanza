/**
 * EldasoftSimogWSServiceLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package it.eldasoft.simog.ws;

public class EldasoftSimogWSServiceLocator extends org.apache.axis.client.Service implements it.eldasoft.simog.ws.EldasoftSimogWSService {

    public EldasoftSimogWSServiceLocator() {
    }


    public EldasoftSimogWSServiceLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public EldasoftSimogWSServiceLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for EldasoftSimogWS
    private java.lang.String EldasoftSimogWS_address = "http://localhost:8080/AliceComunicazioni/services/EldasoftSimogWS";

    public java.lang.String getEldasoftSimogWSAddress() {
        return EldasoftSimogWS_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String EldasoftSimogWSWSDDServiceName = "EldasoftSimogWS";

    public java.lang.String getEldasoftSimogWSWSDDServiceName() {
        return EldasoftSimogWSWSDDServiceName;
    }

    public void setEldasoftSimogWSWSDDServiceName(java.lang.String name) {
        EldasoftSimogWSWSDDServiceName = name;
    }

    public it.eldasoft.simog.ws.EldasoftSimogWS getEldasoftSimogWS() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(EldasoftSimogWS_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getEldasoftSimogWS(endpoint);
    }

    public it.eldasoft.simog.ws.EldasoftSimogWS getEldasoftSimogWS(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            it.eldasoft.simog.ws.EldasoftSimogWSBindingStub _stub = new it.eldasoft.simog.ws.EldasoftSimogWSBindingStub(portAddress, this);
            _stub.setPortName(getEldasoftSimogWSWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setEldasoftSimogWSEndpointAddress(java.lang.String address) {
        EldasoftSimogWS_address = address;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (it.eldasoft.simog.ws.EldasoftSimogWS.class.isAssignableFrom(serviceEndpointInterface)) {
                it.eldasoft.simog.ws.EldasoftSimogWSBindingStub _stub = new it.eldasoft.simog.ws.EldasoftSimogWSBindingStub(new java.net.URL(EldasoftSimogWS_address), this);
                _stub.setPortName(getEldasoftSimogWSWSDDServiceName());
                return _stub;
            }
        }
        catch (java.lang.Throwable t) {
            throw new javax.xml.rpc.ServiceException(t);
        }
        throw new javax.xml.rpc.ServiceException("There is no stub implementation for the interface:  " + (serviceEndpointInterface == null ? "null" : serviceEndpointInterface.getName()));
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(javax.xml.namespace.QName portName, Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        if (portName == null) {
            return getPort(serviceEndpointInterface);
        }
        java.lang.String inputPortName = portName.getLocalPart();
        if ("EldasoftSimogWS".equals(inputPortName)) {
            return getEldasoftSimogWS();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("http://ws.simog.eldasoft.it/", "EldasoftSimogWSService");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("http://ws.simog.eldasoft.it/", "EldasoftSimogWS"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        
if ("EldasoftSimogWS".equals(portName)) {
            setEldasoftSimogWSEndpointAddress(address);
        }
        else 
{ // Unknown Port Name
            throw new javax.xml.rpc.ServiceException(" Cannot set Endpoint Address for Unknown Port" + portName);
        }
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(javax.xml.namespace.QName portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        setEndpointAddress(portName.getLocalPart(), address);
    }

}
