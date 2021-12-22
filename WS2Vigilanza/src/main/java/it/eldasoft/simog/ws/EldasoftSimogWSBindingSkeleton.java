/**
 * EldasoftSimogWSBindingSkeleton.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package it.eldasoft.simog.ws;

public class EldasoftSimogWSBindingSkeleton implements it.eldasoft.simog.ws.EldasoftSimogWS, org.apache.axis.wsdl.Skeleton {
    private it.eldasoft.simog.ws.EldasoftSimogWS impl;
    private static java.util.Map _myOperations = new java.util.Hashtable();
    private static java.util.Collection _myOperationsList = new java.util.ArrayList();

    /**
    * Returns List of OperationDesc objects with this name
    */
    public static java.util.List getOperationDescByName(java.lang.String methodName) {
        return (java.util.List)_myOperations.get(methodName);
    }

    /**
    * Returns Collection of OperationDescs
    */
    public static java.util.Collection getOperationDescs() {
        return _myOperationsList;
    }

    static {
        org.apache.axis.description.OperationDesc _oper;
        org.apache.axis.description.FaultDesc _fault;
        org.apache.axis.description.ParameterDesc [] _params;
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "login"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false), 
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "password"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false), 
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "datiGaraLotto"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("inserisciGaraLotto", _params, new javax.xml.namespace.QName("", "esito"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://ws.simog.eldasoft.it/", "esitoInserisciGaraLotto"));
        _oper.setElementQName(new javax.xml.namespace.QName("http://ws.simog.eldasoft.it/", "inserisciGaraLotto"));
        _oper.setSoapAction("http://ws.simap.eldasoft.it/inserisciGaraLotto");
        _myOperationsList.add(_oper);
        if (_myOperations.get("inserisciGaraLotto") == null) {
            _myOperations.put("inserisciGaraLotto", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("inserisciGaraLotto")).add(_oper);
        
        _params = new org.apache.axis.description.ParameterDesc [] {
                new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "login"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false), 
                new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "password"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false), 
                new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "datiSmartCIG"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false), 
            };
            _oper = new org.apache.axis.description.OperationDesc("inserisciSmartCIG", _params, new javax.xml.namespace.QName("", "esito"));
            _oper.setReturnType(new javax.xml.namespace.QName("http://ws.simog.eldasoft.it/", "esitoInserisciSmartCIG"));
            _oper.setElementQName(new javax.xml.namespace.QName("http://ws.simog.eldasoft.it/", "inserisciSmartCIG"));
            _oper.setSoapAction("http://ws.simap.eldasoft.it/inserisciSmartCIG");
            _myOperationsList.add(_oper);
            if (_myOperations.get("inserisciSmartCIG") == null) {
                _myOperations.put("inserisciSmartCIG", new java.util.ArrayList());
            }
            ((java.util.List)_myOperations.get("inserisciSmartCIG")).add(_oper);
            
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "uuid"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("consultaIDGARA", _params, new javax.xml.namespace.QName("", "esito"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://ws.simog.eldasoft.it/", "esitoConsultaIDGARA"));
        _oper.setElementQName(new javax.xml.namespace.QName("http://ws.simog.eldasoft.it/", "consultaIDGARA"));
        _oper.setSoapAction("http://ws.simap.eldasoft.it/consultaIDGARA");
        _myOperationsList.add(_oper);
        if (_myOperations.get("consultaIDGARA") == null) {
            _myOperations.put("consultaIDGARA", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("consultaIDGARA")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "uuid"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("consultaCIG", _params, new javax.xml.namespace.QName("", "esito"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://ws.simog.eldasoft.it/", "esitoConsultaCIG"));
        _oper.setElementQName(new javax.xml.namespace.QName("http://ws.simog.eldasoft.it/", "consultaCIG"));
        _oper.setSoapAction("http://ws.simap.eldasoft.it/consultaCIG");
        _myOperationsList.add(_oper);
        if (_myOperations.get("consultaCIG") == null) {
            _myOperations.put("consultaCIG", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("consultaCIG")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "cig"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("verificaCIG", _params, new javax.xml.namespace.QName("", "esito"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://ws.simog.eldasoft.it/", "esitoVerificaCIG"));
        _oper.setElementQName(new javax.xml.namespace.QName("http://ws.simog.eldasoft.it/", "verificaCIG"));
        _oper.setSoapAction("http://ws.simap.eldasoft.it/verificaCIG");
        _myOperationsList.add(_oper);
        if (_myOperations.get("verificaCIG") == null) {
            _myOperations.put("verificaCIG", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("verificaCIG")).add(_oper);
    }

    public EldasoftSimogWSBindingSkeleton() {
        this.impl = new it.eldasoft.simog.ws.EldasoftSimogWSBindingImpl();
    }

    public EldasoftSimogWSBindingSkeleton(it.eldasoft.simog.ws.EldasoftSimogWS impl) {
        this.impl = impl;
    }
    public it.eldasoft.simog.ws.EsitoInserisciGaraLotto inserisciGaraLotto(java.lang.String login, java.lang.String password, java.lang.String datiGaraLotto) throws java.rmi.RemoteException
    {
        it.eldasoft.simog.ws.EsitoInserisciGaraLotto ret = impl.inserisciGaraLotto(login, password, datiGaraLotto);
        return ret;
    }

    public it.eldasoft.simog.ws.EsitoConsultaIDGARA consultaIDGARA(java.lang.String uuid) throws java.rmi.RemoteException
    {
        it.eldasoft.simog.ws.EsitoConsultaIDGARA ret = impl.consultaIDGARA(uuid);
        return ret;
    }

    public it.eldasoft.simog.ws.EsitoConsultaCIG consultaCIG(java.lang.String uuid) throws java.rmi.RemoteException
    {
        it.eldasoft.simog.ws.EsitoConsultaCIG ret = impl.consultaCIG(uuid);
        return ret;
    }

    public it.eldasoft.simog.ws.EsitoVerificaCIG verificaCIG(java.lang.String cig) throws java.rmi.RemoteException
    {
        it.eldasoft.simog.ws.EsitoVerificaCIG ret = impl.verificaCIG(cig);
        return ret;
    }

    public it.eldasoft.simog.ws.EsitoInserisciSmartCIG inserisciSmartCIG(java.lang.String login, java.lang.String password, java.lang.String datiSmartCIG) throws java.rmi.RemoteException
    {
        it.eldasoft.simog.ws.EsitoInserisciSmartCIG ret = impl.inserisciSmartCIG(login, password, datiSmartCIG);
        return ret;
    }
    
}
