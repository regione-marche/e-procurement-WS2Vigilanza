/**
 * SimogWSServiceLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package it.avlp.simog.ws.beans;

public class SimogWSServiceLocator extends org.apache.axis.client.Service implements it.avlp.simog.ws.beans.SimogWSService {

    public SimogWSServiceLocator() {
    }


    public SimogWSServiceLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public SimogWSServiceLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for SimogWS
    private java.lang.String SimogWS_address = "http://localhost:8080/SimogWS/services/SimogWS";

    public java.lang.String getSimogWSAddress() {
        return SimogWS_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String SimogWSWSDDServiceName = "SimogWS";

    public java.lang.String getSimogWSWSDDServiceName() {
        return SimogWSWSDDServiceName;
    }

    public void setSimogWSWSDDServiceName(java.lang.String name) {
        SimogWSWSDDServiceName = name;
    }

    public it.avlp.simog.ws.beans.SimogWS getSimogWS() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(SimogWS_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getSimogWS(endpoint);
    }

    public it.avlp.simog.ws.beans.SimogWS getSimogWS(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            it.avlp.simog.ws.beans.SimogWSSoapBindingStub _stub = new it.avlp.simog.ws.beans.SimogWSSoapBindingStub(portAddress, this);
            _stub.setPortName(getSimogWSWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setSimogWSEndpointAddress(java.lang.String address) {
        SimogWS_address = address;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (it.avlp.simog.ws.beans.SimogWS.class.isAssignableFrom(serviceEndpointInterface)) {
                it.avlp.simog.ws.beans.SimogWSSoapBindingStub _stub = new it.avlp.simog.ws.beans.SimogWSSoapBindingStub(new java.net.URL(SimogWS_address), this);
                _stub.setPortName(getSimogWSWSDDServiceName());
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
        if ("SimogWS".equals(inputPortName)) {
            return getSimogWS();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("http://beans.ws.simog.avlp.it", "SimogWSService");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("http://beans.ws.simog.avlp.it", "SimogWS"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        
if ("SimogWS".equals(portName)) {
            setSimogWSEndpointAddress(address);
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
