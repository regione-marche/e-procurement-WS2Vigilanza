/**
 * SimogWSSoapBindingStub.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package it.avlp.simog.ws.beans;

public class SimogWSSoapBindingStub extends org.apache.axis.client.Stub implements it.avlp.simog.ws.beans.SimogWS {
    private java.util.Vector cachedSerClasses = new java.util.Vector();
    private java.util.Vector cachedSerQNames = new java.util.Vector();
    private java.util.Vector cachedSerFactories = new java.util.Vector();
    private java.util.Vector cachedDeserFactories = new java.util.Vector();

    static org.apache.axis.description.OperationDesc [] _operations;

    static {
        _operations = new org.apache.axis.description.OperationDesc[16];
        _initOperationDesc1();
        _initOperationDesc2();
    }

    private static void _initOperationDesc1(){
        org.apache.axis.description.OperationDesc oper;
        org.apache.axis.description.ParameterDesc param;
        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("chiudiSessione");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://beans.ws.simog.avlp.it", "ticket"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://beans.common.simog.avlp.it", "ResponseChiudiSession"));
        oper.setReturnClass(it.avlp.simog.common.beans.ResponseChiudiSession.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://beans.ws.simog.avlp.it", "chiudiSessioneReturn"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[0] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("modificaGara");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://beans.ws.simog.avlp.it", "ticket"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://beans.ws.simog.avlp.it", "indexCollaborazione"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://beans.ws.simog.avlp.it", "id_gara"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://beans.ws.simog.avlp.it", "datiGara"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://beans.common.simog.avlp.it", "ResponseModificaGara"));
        oper.setReturnClass(it.avlp.simog.common.beans.ResponseModificaGara.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://beans.ws.simog.avlp.it", "modificaGaraReturn"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[1] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("modificaLotto");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://beans.ws.simog.avlp.it", "ticket"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://beans.ws.simog.avlp.it", "indexCollaborazione"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://beans.ws.simog.avlp.it", "datiGara"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://beans.ws.simog.avlp.it", "cig"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://beans.common.simog.avlp.it", "ResponseModificaLotto"));
        oper.setReturnClass(it.avlp.simog.common.beans.ResponseModificaLotto.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://beans.ws.simog.avlp.it", "modificaLottoReturn"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[2] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("login");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://beans.ws.simog.avlp.it", "login"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://beans.ws.simog.avlp.it", "password"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://beans.common.simog.avlp.it", "ResponseCheckLogin"));
        oper.setReturnClass(it.avlp.simog.common.beans.ResponseCheckLogin.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://beans.ws.simog.avlp.it", "loginReturn"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[3] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("integraDL133");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://beans.ws.simog.avlp.it", "ticket"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://beans.ws.simog.avlp.it", "indexCollaborazione"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://beans.ws.simog.avlp.it", "id_gara"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://beans.ws.simog.avlp.it", "flagDL133"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://beans.common.simog.avlp.it", "ResponseModificaGara"));
        oper.setReturnClass(it.avlp.simog.common.beans.ResponseModificaGara.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://beans.ws.simog.avlp.it", "integraDL133Return"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[4] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("cancellaGara");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://beans.ws.simog.avlp.it", "ticket"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://beans.ws.simog.avlp.it", "indexCollaborazione"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://beans.ws.simog.avlp.it", "id_gara"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://beans.ws.simog.avlp.it", "id_motivazione"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://beans.ws.simog.avlp.it", "note_canc"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://beans.common.simog.avlp.it", "ResponseCancellaGara"));
        oper.setReturnClass(it.avlp.simog.common.beans.ResponseCancellaGara.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://beans.ws.simog.avlp.it", "cancellaGaraReturn"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[5] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("inviaRequisiti");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://beans.ws.simog.avlp.it", "ticket"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://beans.ws.simog.avlp.it", "indexCollaborazione"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://beans.ws.simog.avlp.it", "datiGara"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://beans.ws.simog.avlp.it", "id_gara"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://beans.common.simog.avlp.it", "ResponseInviaRequisiti"));
        oper.setReturnClass(it.avlp.simog.common.beans.ResponseInviaRequisiti.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://beans.ws.simog.avlp.it", "inviaRequisitiReturn"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[6] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("perfezionaGara");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://beans.ws.simog.avlp.it", "ticket"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://beans.ws.simog.avlp.it", "indexCollaborazione"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://beans.ws.simog.avlp.it", "id_gara"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://beans.common.simog.avlp.it", "ResponsePerfezionaGara"));
        oper.setReturnClass(it.avlp.simog.common.beans.ResponsePerfezionaGara.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://beans.ws.simog.avlp.it", "perfezionaGaraReturn"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[7] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("consultaGara");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://beans.ws.simog.avlp.it", "ticket"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://beans.ws.simog.avlp.it", "CIG"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://beans.ws.simog.avlp.it", "schede"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://beans.common.simog.avlp.it", "ResponseConsultaGara"));
        oper.setReturnClass(it.avlp.simog.common.beans.ResponseConsultaGara.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://beans.ws.simog.avlp.it", "consultaGaraReturn"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[8] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("inserisciLotto");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://beans.ws.simog.avlp.it", "ticket"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://beans.ws.simog.avlp.it", "indexCollaborazione"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://beans.ws.simog.avlp.it", "datiGara"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://beans.ws.simog.avlp.it", "id_gara"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://beans.common.simog.avlp.it", "ResponseInserisciLotto"));
        oper.setReturnClass(it.avlp.simog.common.beans.ResponseInserisciLotto.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://beans.ws.simog.avlp.it", "inserisciLottoReturn"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[9] = oper;

    }

    private static void _initOperationDesc2(){
        org.apache.axis.description.OperationDesc oper;
        org.apache.axis.description.ParameterDesc param;
        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("perfezionaLotto");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://beans.ws.simog.avlp.it", "ticket"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://beans.ws.simog.avlp.it", "indexCollaborazione"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://beans.ws.simog.avlp.it", "dataPubblicazione"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://beans.ws.simog.avlp.it", "dataScadenzaPagamenti"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://beans.ws.simog.avlp.it", "cig"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://beans.ws.simog.avlp.it", "oraScadenza"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://beans.common.simog.avlp.it", "ResponsePerfezionaLotto"));
        oper.setReturnClass(it.avlp.simog.common.beans.ResponsePerfezionaLotto.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://beans.ws.simog.avlp.it", "perfezionaLottoReturn"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[10] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("inserisciGara");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://beans.ws.simog.avlp.it", "ticket"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://beans.ws.simog.avlp.it", "indexCollaborazione"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://beans.ws.simog.avlp.it", "datiGara"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://beans.common.simog.avlp.it", "ResponseInserisciGara"));
        oper.setReturnClass(it.avlp.simog.common.beans.ResponseInserisciGara.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://beans.ws.simog.avlp.it", "inserisciGaraReturn"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[11] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("pubblica");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://beans.ws.simog.avlp.it", "ticket"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://beans.ws.simog.avlp.it", "indexCollaborazione"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://beans.ws.simog.avlp.it", "dataPubblicazione"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://beans.ws.simog.avlp.it", "dataScadenzaPagamenti"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://beans.ws.simog.avlp.it", "cig"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://beans.ws.simog.avlp.it", "progCui"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://beans.ws.simog.avlp.it", "datiPubblicazione"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://beans.ws.simog.avlp.it", "tipoOperazione"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://beans.ws.simog.avlp.it", "allegato"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://beans.simog.avlp.it", "AllegatoType"), it.avlp.simog.beans.AllegatoType[].class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://beans.ws.simog.avlp.it", "oraScadenza"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://beans.ws.simog.avlp.it", "dataScadenzaRichiestaInvito"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://beans.ws.simog.avlp.it", "dataLetteraInvito"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://beans.ws.simog.avlp.it", "CUPLOTTO"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://beans.simog.avlp.it", "CUPLOTTO"), it.avlp.simog.beans.CUPLOTTO[].class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://beans.common.simog.avlp.it", "ResponsePubblicazioneBando"));
        oper.setReturnClass(it.avlp.simog.common.beans.ResponsePubblicazioneBando.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://beans.ws.simog.avlp.it", "pubblicaReturn"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[12] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("integraCUP");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://beans.ws.simog.avlp.it", "ticket"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://beans.ws.simog.avlp.it", "indexCollaborazione"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://beans.ws.simog.avlp.it", "cig"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://beans.ws.simog.avlp.it", "flagCUP"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://beans.ws.simog.avlp.it", "TIPIAPPALTOL"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://beans.simog.avlp.it", "TIPIAPPALTO"), it.avlp.simog.beans.TIPIAPPALTO.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://beans.ws.simog.avlp.it", "TIPIAPPALTOFS"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://beans.simog.avlp.it", "TIPIAPPALTO"), it.avlp.simog.beans.TIPIAPPALTO.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://beans.ws.simog.avlp.it", "CUPLOTTO"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://beans.simog.avlp.it", "CUPLOTTO"), it.avlp.simog.beans.CUPLOTTO.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://beans.common.simog.avlp.it", "ResponseIntegraCUP"));
        oper.setReturnClass(it.avlp.simog.common.beans.ResponseIntegraCUP.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://beans.ws.simog.avlp.it", "integraCUPReturn"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[13] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("cancellaLotto");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://beans.ws.simog.avlp.it", "ticket"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://beans.ws.simog.avlp.it", "indexCollaborazione"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://beans.ws.simog.avlp.it", "id_motivazione"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://beans.ws.simog.avlp.it", "note_canc"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://beans.ws.simog.avlp.it", "cig"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://beans.common.simog.avlp.it", "ResponseCancellaLotto"));
        oper.setReturnClass(it.avlp.simog.common.beans.ResponseCancellaLotto.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://beans.ws.simog.avlp.it", "cancellaLottoReturn"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[14] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("presaInCarico");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://beans.ws.simog.avlp.it", "ticket"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://beans.ws.simog.avlp.it", "indexCollaborazione"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://beans.ws.simog.avlp.it", "garaOcig"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://beans.ws.simog.avlp.it", "estremiProvv"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://beans.ws.simog.avlp.it", "flagDatiComuni"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://beans.common.simog.avlp.it", "ResponsePresaCarico"));
        oper.setReturnClass(it.avlp.simog.common.beans.ResponsePresaCarico.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://beans.ws.simog.avlp.it", "presaInCaricoReturn"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[15] = oper;

    }

    public SimogWSSoapBindingStub() throws org.apache.axis.AxisFault {
         this(null);
    }

    public SimogWSSoapBindingStub(java.net.URL endpointURL, javax.xml.rpc.Service service) throws org.apache.axis.AxisFault {
         this(service);
         super.cachedEndpoint = endpointURL;
    }

    public SimogWSSoapBindingStub(javax.xml.rpc.Service service) throws org.apache.axis.AxisFault {
        if (service == null) {
            super.service = new org.apache.axis.client.Service();
        } else {
            super.service = service;
        }
        ((org.apache.axis.client.Service)super.service).setTypeMappingVersion("1.2");
            java.lang.Class cls;
            javax.xml.namespace.QName qName;
            javax.xml.namespace.QName qName2;
            java.lang.Class beansf = org.apache.axis.encoding.ser.BeanSerializerFactory.class;
            java.lang.Class beandf = org.apache.axis.encoding.ser.BeanDeserializerFactory.class;
            java.lang.Class enumsf = org.apache.axis.encoding.ser.EnumSerializerFactory.class;
            java.lang.Class enumdf = org.apache.axis.encoding.ser.EnumDeserializerFactory.class;
            java.lang.Class arraysf = org.apache.axis.encoding.ser.ArraySerializerFactory.class;
            java.lang.Class arraydf = org.apache.axis.encoding.ser.ArrayDeserializerFactory.class;
            java.lang.Class simplesf = org.apache.axis.encoding.ser.SimpleSerializerFactory.class;
            java.lang.Class simpledf = org.apache.axis.encoding.ser.SimpleDeserializerFactory.class;
            java.lang.Class simplelistsf = org.apache.axis.encoding.ser.SimpleListSerializerFactory.class;
            java.lang.Class simplelistdf = org.apache.axis.encoding.ser.SimpleListDeserializerFactory.class;
            qName = new javax.xml.namespace.QName("http://beans.common.simog.avlp.it", "Response");
            cachedSerQNames.add(qName);
            cls = it.avlp.simog.common.beans.Response.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://beans.common.simog.avlp.it", "ResponseCancellaGara");
            cachedSerQNames.add(qName);
            cls = it.avlp.simog.common.beans.ResponseCancellaGara.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://beans.common.simog.avlp.it", "ResponseCancellaLotto");
            cachedSerQNames.add(qName);
            cls = it.avlp.simog.common.beans.ResponseCancellaLotto.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://beans.common.simog.avlp.it", "ResponseCheckLogin");
            cachedSerQNames.add(qName);
            cls = it.avlp.simog.common.beans.ResponseCheckLogin.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://beans.common.simog.avlp.it", "ResponseChiudiSession");
            cachedSerQNames.add(qName);
            cls = it.avlp.simog.common.beans.ResponseChiudiSession.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://beans.common.simog.avlp.it", "ResponseConsultaGara");
            cachedSerQNames.add(qName);
            cls = it.avlp.simog.common.beans.ResponseConsultaGara.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://beans.common.simog.avlp.it", "ResponseInserisciGara");
            cachedSerQNames.add(qName);
            cls = it.avlp.simog.common.beans.ResponseInserisciGara.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://beans.common.simog.avlp.it", "ResponseInserisciLotto");
            cachedSerQNames.add(qName);
            cls = it.avlp.simog.common.beans.ResponseInserisciLotto.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://beans.common.simog.avlp.it", "ResponseIntegraCUP");
            cachedSerQNames.add(qName);
            cls = it.avlp.simog.common.beans.ResponseIntegraCUP.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://beans.common.simog.avlp.it", "ResponseInviaRequisiti");
            cachedSerQNames.add(qName);
            cls = it.avlp.simog.common.beans.ResponseInviaRequisiti.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://beans.common.simog.avlp.it", "ResponseModificaGara");
            cachedSerQNames.add(qName);
            cls = it.avlp.simog.common.beans.ResponseModificaGara.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://beans.common.simog.avlp.it", "ResponseModificaLotto");
            cachedSerQNames.add(qName);
            cls = it.avlp.simog.common.beans.ResponseModificaLotto.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://beans.common.simog.avlp.it", "ResponsePerfezionaGara");
            cachedSerQNames.add(qName);
            cls = it.avlp.simog.common.beans.ResponsePerfezionaGara.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://beans.common.simog.avlp.it", "ResponsePerfezionaLotto");
            cachedSerQNames.add(qName);
            cls = it.avlp.simog.common.beans.ResponsePerfezionaLotto.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://beans.common.simog.avlp.it", "ResponsePresaCarico");
            cachedSerQNames.add(qName);
            cls = it.avlp.simog.common.beans.ResponsePresaCarico.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://beans.common.simog.avlp.it", "ResponsePubblicazioneBando");
            cachedSerQNames.add(qName);
            cls = it.avlp.simog.common.beans.ResponsePubblicazioneBando.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://beans.simog.avlp.it", "AllegatoType");
            cachedSerQNames.add(qName);
            cls = it.avlp.simog.beans.AllegatoType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://beans.simog.avlp.it", "CIGBean");
            cachedSerQNames.add(qName);
            cls = it.avlp.simog.beans.CIGBean.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://beans.simog.avlp.it", "CodiciCup");
            cachedSerQNames.add(qName);
            cls = it.avlp.simog.beans.CodiciCup.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://beans.simog.avlp.it", "Collaborazione");
            cachedSerQNames.add(qName);
            cls = it.avlp.simog.beans.Collaborazione.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://beans.simog.avlp.it", "Collaborazioni");
            cachedSerQNames.add(qName);
            cls = it.avlp.simog.beans.Collaborazioni.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://beans.simog.avlp.it", "CUPLOTTO");
            cachedSerQNames.add(qName);
            cls = it.avlp.simog.beans.CUPLOTTO.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://beans.simog.avlp.it", "TIPIAPPALTO");
            cachedSerQNames.add(qName);
            cls = it.avlp.simog.beans.TIPIAPPALTO.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://beans.ws.simog.avlp.it", "ArrayOf_tns2_CodiciCup");
            cachedSerQNames.add(qName);
            cls = it.avlp.simog.beans.CodiciCup[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://beans.simog.avlp.it", "CodiciCup");
            qName2 = new javax.xml.namespace.QName("http://beans.ws.simog.avlp.it", "item");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://beans.ws.simog.avlp.it", "ArrayOf_tns2_Collaborazione");
            cachedSerQNames.add(qName);
            cls = it.avlp.simog.beans.Collaborazione[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://beans.simog.avlp.it", "Collaborazione");
            qName2 = new javax.xml.namespace.QName("http://beans.ws.simog.avlp.it", "item");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://beans.ws.simog.avlp.it", "ArrayOf_tns2_CUPLOTTO");
            cachedSerQNames.add(qName);
            cls = it.avlp.simog.beans.CUPLOTTO[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://beans.simog.avlp.it", "CUPLOTTO");
            qName2 = new javax.xml.namespace.QName("http://beans.ws.simog.avlp.it", "item");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://beans.ws.simog.avlp.it", "ArrayOf_xsd_string");
            cachedSerQNames.add(qName);
            cls = java.lang.String[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string");
            qName2 = new javax.xml.namespace.QName("http://beans.ws.simog.avlp.it", "item");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

    }

    protected org.apache.axis.client.Call createCall() throws java.rmi.RemoteException {
        try {
            org.apache.axis.client.Call _call = super._createCall();
            if (super.maintainSessionSet) {
                _call.setMaintainSession(super.maintainSession);
            }
            if (super.cachedUsername != null) {
                _call.setUsername(super.cachedUsername);
            }
            if (super.cachedPassword != null) {
                _call.setPassword(super.cachedPassword);
            }
            if (super.cachedEndpoint != null) {
                _call.setTargetEndpointAddress(super.cachedEndpoint);
            }
            if (super.cachedTimeout != null) {
                _call.setTimeout(super.cachedTimeout);
            }
            if (super.cachedPortName != null) {
                _call.setPortName(super.cachedPortName);
            }
            java.util.Enumeration keys = super.cachedProperties.keys();
            while (keys.hasMoreElements()) {
                java.lang.String key = (java.lang.String) keys.nextElement();
                _call.setProperty(key, super.cachedProperties.get(key));
            }
            // All the type mapping information is registered
            // when the first call is made.
            // The type mapping information is actually registered in
            // the TypeMappingRegistry of the service, which
            // is the reason why registration is only needed for the first call.
            synchronized (this) {
                if (firstCall()) {
                    // must set encoding style before registering serializers
                    _call.setEncodingStyle(null);
                    for (int i = 0; i < cachedSerFactories.size(); ++i) {
                        java.lang.Class cls = (java.lang.Class) cachedSerClasses.get(i);
                        javax.xml.namespace.QName qName =
                                (javax.xml.namespace.QName) cachedSerQNames.get(i);
                        java.lang.Object x = cachedSerFactories.get(i);
                        if (x instanceof Class) {
                            java.lang.Class sf = (java.lang.Class)
                                 cachedSerFactories.get(i);
                            java.lang.Class df = (java.lang.Class)
                                 cachedDeserFactories.get(i);
                            _call.registerTypeMapping(cls, qName, sf, df, false);
                        }
                        else if (x instanceof javax.xml.rpc.encoding.SerializerFactory) {
                            org.apache.axis.encoding.SerializerFactory sf = (org.apache.axis.encoding.SerializerFactory)
                                 cachedSerFactories.get(i);
                            org.apache.axis.encoding.DeserializerFactory df = (org.apache.axis.encoding.DeserializerFactory)
                                 cachedDeserFactories.get(i);
                            _call.registerTypeMapping(cls, qName, sf, df, false);
                        }
                    }
                }
            }
            return _call;
        }
        catch (java.lang.Throwable _t) {
            throw new org.apache.axis.AxisFault("Failure trying to get the Call object", _t);
        }
    }

    public it.avlp.simog.common.beans.ResponseChiudiSession chiudiSessione(java.lang.String ticket) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[0]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://beans.ws.simog.avlp.it", "chiudiSessione"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {ticket});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (it.avlp.simog.common.beans.ResponseChiudiSession) _resp;
            } catch (java.lang.Exception _exception) {
                return (it.avlp.simog.common.beans.ResponseChiudiSession) org.apache.axis.utils.JavaUtils.convert(_resp, it.avlp.simog.common.beans.ResponseChiudiSession.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public it.avlp.simog.common.beans.ResponseModificaGara modificaGara(java.lang.String ticket, java.lang.String indexCollaborazione, java.lang.String id_gara, java.lang.String datiGara) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[1]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://beans.ws.simog.avlp.it", "modificaGara"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {ticket, indexCollaborazione, id_gara, datiGara});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (it.avlp.simog.common.beans.ResponseModificaGara) _resp;
            } catch (java.lang.Exception _exception) {
                return (it.avlp.simog.common.beans.ResponseModificaGara) org.apache.axis.utils.JavaUtils.convert(_resp, it.avlp.simog.common.beans.ResponseModificaGara.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public it.avlp.simog.common.beans.ResponseModificaLotto modificaLotto(java.lang.String ticket, java.lang.String indexCollaborazione, java.lang.String datiGara, java.lang.String cig) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[2]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://beans.ws.simog.avlp.it", "modificaLotto"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {ticket, indexCollaborazione, datiGara, cig});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (it.avlp.simog.common.beans.ResponseModificaLotto) _resp;
            } catch (java.lang.Exception _exception) {
                return (it.avlp.simog.common.beans.ResponseModificaLotto) org.apache.axis.utils.JavaUtils.convert(_resp, it.avlp.simog.common.beans.ResponseModificaLotto.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public it.avlp.simog.common.beans.ResponseCheckLogin login(java.lang.String login, java.lang.String password) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[3]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://beans.ws.simog.avlp.it", "login"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {login, password});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (it.avlp.simog.common.beans.ResponseCheckLogin) _resp;
            } catch (java.lang.Exception _exception) {
                return (it.avlp.simog.common.beans.ResponseCheckLogin) org.apache.axis.utils.JavaUtils.convert(_resp, it.avlp.simog.common.beans.ResponseCheckLogin.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public it.avlp.simog.common.beans.ResponseModificaGara integraDL133(java.lang.String ticket, java.lang.String indexCollaborazione, java.lang.String id_gara, java.lang.String flagDL133) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[4]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://beans.ws.simog.avlp.it", "integraDL133"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {ticket, indexCollaborazione, id_gara, flagDL133});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (it.avlp.simog.common.beans.ResponseModificaGara) _resp;
            } catch (java.lang.Exception _exception) {
                return (it.avlp.simog.common.beans.ResponseModificaGara) org.apache.axis.utils.JavaUtils.convert(_resp, it.avlp.simog.common.beans.ResponseModificaGara.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public it.avlp.simog.common.beans.ResponseCancellaGara cancellaGara(java.lang.String ticket, java.lang.String indexCollaborazione, java.lang.String id_gara, java.lang.String id_motivazione, java.lang.String note_canc) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[5]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://beans.ws.simog.avlp.it", "cancellaGara"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {ticket, indexCollaborazione, id_gara, id_motivazione, note_canc});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (it.avlp.simog.common.beans.ResponseCancellaGara) _resp;
            } catch (java.lang.Exception _exception) {
                return (it.avlp.simog.common.beans.ResponseCancellaGara) org.apache.axis.utils.JavaUtils.convert(_resp, it.avlp.simog.common.beans.ResponseCancellaGara.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public it.avlp.simog.common.beans.ResponseInviaRequisiti inviaRequisiti(java.lang.String ticket, java.lang.String indexCollaborazione, java.lang.String datiGara, java.lang.String id_gara) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[6]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://beans.ws.simog.avlp.it", "inviaRequisiti"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {ticket, indexCollaborazione, datiGara, id_gara});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (it.avlp.simog.common.beans.ResponseInviaRequisiti) _resp;
            } catch (java.lang.Exception _exception) {
                return (it.avlp.simog.common.beans.ResponseInviaRequisiti) org.apache.axis.utils.JavaUtils.convert(_resp, it.avlp.simog.common.beans.ResponseInviaRequisiti.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public it.avlp.simog.common.beans.ResponsePerfezionaGara perfezionaGara(java.lang.String ticket, java.lang.String indexCollaborazione, java.lang.String id_gara) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[7]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://beans.ws.simog.avlp.it", "perfezionaGara"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {ticket, indexCollaborazione, id_gara});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (it.avlp.simog.common.beans.ResponsePerfezionaGara) _resp;
            } catch (java.lang.Exception _exception) {
                return (it.avlp.simog.common.beans.ResponsePerfezionaGara) org.apache.axis.utils.JavaUtils.convert(_resp, it.avlp.simog.common.beans.ResponsePerfezionaGara.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public it.avlp.simog.common.beans.ResponseConsultaGara consultaGara(java.lang.String ticket, java.lang.String CIG, java.lang.String schede) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[8]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://beans.ws.simog.avlp.it", "consultaGara"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {ticket, CIG, schede});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (it.avlp.simog.common.beans.ResponseConsultaGara) _resp;
            } catch (java.lang.Exception _exception) {
                return (it.avlp.simog.common.beans.ResponseConsultaGara) org.apache.axis.utils.JavaUtils.convert(_resp, it.avlp.simog.common.beans.ResponseConsultaGara.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public it.avlp.simog.common.beans.ResponseInserisciLotto inserisciLotto(java.lang.String ticket, java.lang.String indexCollaborazione, java.lang.String datiGara, java.lang.String id_gara) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[9]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://beans.ws.simog.avlp.it", "inserisciLotto"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {ticket, indexCollaborazione, datiGara, id_gara});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (it.avlp.simog.common.beans.ResponseInserisciLotto) _resp;
            } catch (java.lang.Exception _exception) {
                return (it.avlp.simog.common.beans.ResponseInserisciLotto) org.apache.axis.utils.JavaUtils.convert(_resp, it.avlp.simog.common.beans.ResponseInserisciLotto.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public it.avlp.simog.common.beans.ResponsePerfezionaLotto perfezionaLotto(java.lang.String ticket, java.lang.String indexCollaborazione, java.lang.String dataPubblicazione, java.lang.String dataScadenzaPagamenti, java.lang.String cig, java.lang.String oraScadenza) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[10]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://beans.ws.simog.avlp.it", "perfezionaLotto"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {ticket, indexCollaborazione, dataPubblicazione, dataScadenzaPagamenti, cig, oraScadenza});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (it.avlp.simog.common.beans.ResponsePerfezionaLotto) _resp;
            } catch (java.lang.Exception _exception) {
                return (it.avlp.simog.common.beans.ResponsePerfezionaLotto) org.apache.axis.utils.JavaUtils.convert(_resp, it.avlp.simog.common.beans.ResponsePerfezionaLotto.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public it.avlp.simog.common.beans.ResponseInserisciGara inserisciGara(java.lang.String ticket, java.lang.String indexCollaborazione, java.lang.String datiGara) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[11]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://beans.ws.simog.avlp.it", "inserisciGara"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {ticket, indexCollaborazione, datiGara});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (it.avlp.simog.common.beans.ResponseInserisciGara) _resp;
            } catch (java.lang.Exception _exception) {
                return (it.avlp.simog.common.beans.ResponseInserisciGara) org.apache.axis.utils.JavaUtils.convert(_resp, it.avlp.simog.common.beans.ResponseInserisciGara.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public it.avlp.simog.common.beans.ResponsePubblicazioneBando pubblica(java.lang.String ticket, java.lang.String indexCollaborazione, java.lang.String dataPubblicazione, java.lang.String dataScadenzaPagamenti, java.lang.String cig, java.lang.String progCui, java.lang.String datiPubblicazione, java.lang.String tipoOperazione, it.avlp.simog.beans.AllegatoType[] allegato, java.lang.String oraScadenza, java.lang.String dataScadenzaRichiestaInvito, java.lang.String dataLetteraInvito, it.avlp.simog.beans.CUPLOTTO[] CUPLOTTO) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[12]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://beans.ws.simog.avlp.it", "pubblica"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {ticket, indexCollaborazione, dataPubblicazione, dataScadenzaPagamenti, cig, progCui, datiPubblicazione, tipoOperazione, allegato, oraScadenza, dataScadenzaRichiestaInvito, dataLetteraInvito, CUPLOTTO});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (it.avlp.simog.common.beans.ResponsePubblicazioneBando) _resp;
            } catch (java.lang.Exception _exception) {
                return (it.avlp.simog.common.beans.ResponsePubblicazioneBando) org.apache.axis.utils.JavaUtils.convert(_resp, it.avlp.simog.common.beans.ResponsePubblicazioneBando.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public it.avlp.simog.common.beans.ResponseIntegraCUP integraCUP(java.lang.String ticket, java.lang.String indexCollaborazione, java.lang.String cig, java.lang.String flagCUP, it.avlp.simog.beans.TIPIAPPALTO TIPIAPPALTOL, it.avlp.simog.beans.TIPIAPPALTO TIPIAPPALTOFS, it.avlp.simog.beans.CUPLOTTO CUPLOTTO) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[13]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://beans.ws.simog.avlp.it", "integraCUP"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {ticket, indexCollaborazione, cig, flagCUP, TIPIAPPALTOL, TIPIAPPALTOFS, CUPLOTTO});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (it.avlp.simog.common.beans.ResponseIntegraCUP) _resp;
            } catch (java.lang.Exception _exception) {
                return (it.avlp.simog.common.beans.ResponseIntegraCUP) org.apache.axis.utils.JavaUtils.convert(_resp, it.avlp.simog.common.beans.ResponseIntegraCUP.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public it.avlp.simog.common.beans.ResponseCancellaLotto cancellaLotto(java.lang.String ticket, java.lang.String indexCollaborazione, java.lang.String id_motivazione, java.lang.String note_canc, java.lang.String cig) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[14]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://beans.ws.simog.avlp.it", "cancellaLotto"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {ticket, indexCollaborazione, id_motivazione, note_canc, cig});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (it.avlp.simog.common.beans.ResponseCancellaLotto) _resp;
            } catch (java.lang.Exception _exception) {
                return (it.avlp.simog.common.beans.ResponseCancellaLotto) org.apache.axis.utils.JavaUtils.convert(_resp, it.avlp.simog.common.beans.ResponseCancellaLotto.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public it.avlp.simog.common.beans.ResponsePresaCarico presaInCarico(java.lang.String ticket, java.lang.String indexCollaborazione, java.lang.String garaOcig, java.lang.String estremiProvv, java.lang.String flagDatiComuni) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[15]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://beans.ws.simog.avlp.it", "presaInCarico"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {ticket, indexCollaborazione, garaOcig, estremiProvv, flagDatiComuni});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (it.avlp.simog.common.beans.ResponsePresaCarico) _resp;
            } catch (java.lang.Exception _exception) {
                return (it.avlp.simog.common.beans.ResponsePresaCarico) org.apache.axis.utils.JavaUtils.convert(_resp, it.avlp.simog.common.beans.ResponsePresaCarico.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

}
