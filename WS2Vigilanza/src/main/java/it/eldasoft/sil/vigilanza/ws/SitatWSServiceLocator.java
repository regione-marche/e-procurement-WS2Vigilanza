/**
 * SitatWSServiceLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package it.eldasoft.sil.vigilanza.ws;

public class SitatWSServiceLocator extends org.apache.axis.client.Service implements it.eldasoft.sil.vigilanza.ws.SitatWSService {

    public SitatWSServiceLocator() {
    }


    public SitatWSServiceLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public SitatWSServiceLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for SitatWS
    private java.lang.String SitatWS_address = "http://localhost:8080/WebService2Project/services/SitatWS";

    public java.lang.String getSitatWSAddress() {
        return SitatWS_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String SitatWSWSDDServiceName = "SitatWS";

    public java.lang.String getSitatWSWSDDServiceName() {
        return SitatWSWSDDServiceName;
    }

    public void setSitatWSWSDDServiceName(java.lang.String name) {
        SitatWSWSDDServiceName = name;
    }

    public it.eldasoft.sil.vigilanza.ws.SitatWS getSitatWS() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(SitatWS_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getSitatWS(endpoint);
    }

    public it.eldasoft.sil.vigilanza.ws.SitatWS getSitatWS(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            it.eldasoft.sil.vigilanza.ws.SitatWSSoapBindingStub _stub = new it.eldasoft.sil.vigilanza.ws.SitatWSSoapBindingStub(portAddress, this);
            _stub.setPortName(getSitatWSWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setSitatWSEndpointAddress(java.lang.String address) {
        SitatWS_address = address;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (it.eldasoft.sil.vigilanza.ws.SitatWS.class.isAssignableFrom(serviceEndpointInterface)) {
                it.eldasoft.sil.vigilanza.ws.SitatWSSoapBindingStub _stub = new it.eldasoft.sil.vigilanza.ws.SitatWSSoapBindingStub(new java.net.URL(SitatWS_address), this);
                _stub.setPortName(getSitatWSWSDDServiceName());
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
        if ("SitatWS".equals(inputPortName)) {
            return getSitatWS();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("http://ws.vigilanza.sil.eldasoft.it", "SitatWSService");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("http://ws.vigilanza.sil.eldasoft.it", "SitatWS"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        
if ("SitatWS".equals(portName)) {
            setSitatWSEndpointAddress(address);
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
