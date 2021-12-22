/**
 * SitatWSSoapBindingStub.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package it.eldasoft.sil.vigilanza.ws;

public class SitatWSSoapBindingStub extends org.apache.axis.client.Stub implements it.eldasoft.sil.vigilanza.ws.SitatWS {
    private java.util.Vector cachedSerClasses = new java.util.Vector();
    private java.util.Vector cachedSerQNames = new java.util.Vector();
    private java.util.Vector cachedSerFactories = new java.util.Vector();
    private java.util.Vector cachedDeserFactories = new java.util.Vector();

    static org.apache.axis.description.OperationDesc [] _operations;

    static {
        _operations = new org.apache.axis.description.OperationDesc[14];
        _initOperationDesc1();
        _initOperationDesc2();
    }

    private static void _initOperationDesc1(){
        org.apache.axis.description.OperationDesc oper;
        org.apache.axis.description.ParameterDesc param;
        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("istanziaGara");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://beans.ws.vigilanza.sil.eldasoft.it", "login"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://beans.ws.vigilanza.sil.eldasoft.it", "LoginType"), it.eldasoft.sil.vigilanza.ws.beans.LoginType.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://beans.ws.vigilanza.sil.eldasoft.it", "garaLotti"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://beans.ws.vigilanza.sil.eldasoft.it", "IstanzaOggettoType"), it.eldasoft.sil.vigilanza.ws.beans.IstanzaOggettoType.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://beans.ws.vigilanza.sil.eldasoft.it", "ResponseType"));
        oper.setReturnClass(it.eldasoft.sil.vigilanza.ws.beans.ResponseType.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://beans.ws.vigilanza.sil.eldasoft.it", "istanziaGaraReturn"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[0] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("istanziaAggiudicazione");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://beans.ws.vigilanza.sil.eldasoft.it", "login"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://beans.ws.vigilanza.sil.eldasoft.it", "LoginType"), it.eldasoft.sil.vigilanza.ws.beans.LoginType.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://beans.ws.vigilanza.sil.eldasoft.it", "aggiudicazione"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://beans.ws.vigilanza.sil.eldasoft.it", "IstanzaOggettoType"), it.eldasoft.sil.vigilanza.ws.beans.IstanzaOggettoType.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://beans.ws.vigilanza.sil.eldasoft.it", "ResponseType"));
        oper.setReturnClass(it.eldasoft.sil.vigilanza.ws.beans.ResponseType.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://beans.ws.vigilanza.sil.eldasoft.it", "aggiudicazioneReturn"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[1] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("istanziaEsito");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://beans.ws.vigilanza.sil.eldasoft.it", "login"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://beans.ws.vigilanza.sil.eldasoft.it", "LoginType"), it.eldasoft.sil.vigilanza.ws.beans.LoginType.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://beans.ws.vigilanza.sil.eldasoft.it", "esito"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://beans.ws.vigilanza.sil.eldasoft.it", "IstanzaOggettoType"), it.eldasoft.sil.vigilanza.ws.beans.IstanzaOggettoType.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://beans.ws.vigilanza.sil.eldasoft.it", "ResponseType"));
        oper.setReturnClass(it.eldasoft.sil.vigilanza.ws.beans.ResponseType.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://beans.ws.vigilanza.sil.eldasoft.it", "esitoReturn"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[2] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("istanziaContratto");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://beans.ws.vigilanza.sil.eldasoft.it", "login"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://beans.ws.vigilanza.sil.eldasoft.it", "LoginType"), it.eldasoft.sil.vigilanza.ws.beans.LoginType.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://beans.ws.vigilanza.sil.eldasoft.it", "contratto"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://beans.ws.vigilanza.sil.eldasoft.it", "IstanzaOggettoType"), it.eldasoft.sil.vigilanza.ws.beans.IstanzaOggettoType.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://beans.ws.vigilanza.sil.eldasoft.it", "ResponseType"));
        oper.setReturnClass(it.eldasoft.sil.vigilanza.ws.beans.ResponseType.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://beans.ws.vigilanza.sil.eldasoft.it", "contrattoResult"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[3] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("istanziaInizio");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://beans.ws.vigilanza.sil.eldasoft.it", "login"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://beans.ws.vigilanza.sil.eldasoft.it", "LoginType"), it.eldasoft.sil.vigilanza.ws.beans.LoginType.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://beans.ws.vigilanza.sil.eldasoft.it", "inizio"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://beans.ws.vigilanza.sil.eldasoft.it", "IstanzaOggettoType"), it.eldasoft.sil.vigilanza.ws.beans.IstanzaOggettoType.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://beans.ws.vigilanza.sil.eldasoft.it", "ResponseType"));
        oper.setReturnClass(it.eldasoft.sil.vigilanza.ws.beans.ResponseType.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://beans.ws.vigilanza.sil.eldasoft.it", "inizioResult"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[4] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("istanziaAvanzamento");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://beans.ws.vigilanza.sil.eldasoft.it", "login"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://beans.ws.vigilanza.sil.eldasoft.it", "LoginType"), it.eldasoft.sil.vigilanza.ws.beans.LoginType.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://beans.ws.vigilanza.sil.eldasoft.it", "avanzamento"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://beans.ws.vigilanza.sil.eldasoft.it", "IstanzaOggettoType"), it.eldasoft.sil.vigilanza.ws.beans.IstanzaOggettoType.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://beans.ws.vigilanza.sil.eldasoft.it", "ResponseType"));
        oper.setReturnClass(it.eldasoft.sil.vigilanza.ws.beans.ResponseType.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://beans.ws.vigilanza.sil.eldasoft.it", "avanzamentoResult"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[5] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("istanziaSospensione");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://beans.ws.vigilanza.sil.eldasoft.it", "login"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://beans.ws.vigilanza.sil.eldasoft.it", "LoginType"), it.eldasoft.sil.vigilanza.ws.beans.LoginType.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://beans.ws.vigilanza.sil.eldasoft.it", "sospensione"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://beans.ws.vigilanza.sil.eldasoft.it", "IstanzaOggettoType"), it.eldasoft.sil.vigilanza.ws.beans.IstanzaOggettoType.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://beans.ws.vigilanza.sil.eldasoft.it", "ResponseType"));
        oper.setReturnClass(it.eldasoft.sil.vigilanza.ws.beans.ResponseType.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://beans.ws.vigilanza.sil.eldasoft.it", "sospensioneResult"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[6] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("istanziaVariante");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://beans.ws.vigilanza.sil.eldasoft.it", "login"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://beans.ws.vigilanza.sil.eldasoft.it", "LoginType"), it.eldasoft.sil.vigilanza.ws.beans.LoginType.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://beans.ws.vigilanza.sil.eldasoft.it", "variante"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://beans.ws.vigilanza.sil.eldasoft.it", "IstanzaOggettoType"), it.eldasoft.sil.vigilanza.ws.beans.IstanzaOggettoType.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://beans.ws.vigilanza.sil.eldasoft.it", "ResponseType"));
        oper.setReturnClass(it.eldasoft.sil.vigilanza.ws.beans.ResponseType.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://beans.ws.vigilanza.sil.eldasoft.it", "varianteResult"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[7] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("istanziaSubappalto");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://beans.ws.vigilanza.sil.eldasoft.it", "login"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://beans.ws.vigilanza.sil.eldasoft.it", "LoginType"), it.eldasoft.sil.vigilanza.ws.beans.LoginType.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://beans.ws.vigilanza.sil.eldasoft.it", "subappalto"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://beans.ws.vigilanza.sil.eldasoft.it", "IstanzaOggettoType"), it.eldasoft.sil.vigilanza.ws.beans.IstanzaOggettoType.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://beans.ws.vigilanza.sil.eldasoft.it", "ResponseType"));
        oper.setReturnClass(it.eldasoft.sil.vigilanza.ws.beans.ResponseType.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://beans.ws.vigilanza.sil.eldasoft.it", "subappaltoResult"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[8] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("istanziaConclusione");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://beans.ws.vigilanza.sil.eldasoft.it", "login"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://beans.ws.vigilanza.sil.eldasoft.it", "LoginType"), it.eldasoft.sil.vigilanza.ws.beans.LoginType.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://beans.ws.vigilanza.sil.eldasoft.it", "conclusione"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://beans.ws.vigilanza.sil.eldasoft.it", "IstanzaOggettoType"), it.eldasoft.sil.vigilanza.ws.beans.IstanzaOggettoType.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://beans.ws.vigilanza.sil.eldasoft.it", "ResponseType"));
        oper.setReturnClass(it.eldasoft.sil.vigilanza.ws.beans.ResponseType.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://beans.ws.vigilanza.sil.eldasoft.it", "conclusioneResult"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[9] = oper;

    }

    private static void _initOperationDesc2(){
        org.apache.axis.description.OperationDesc oper;
        org.apache.axis.description.ParameterDesc param;
        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("istanziaCollaudo");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://beans.ws.vigilanza.sil.eldasoft.it", "login"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://beans.ws.vigilanza.sil.eldasoft.it", "LoginType"), it.eldasoft.sil.vigilanza.ws.beans.LoginType.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://beans.ws.vigilanza.sil.eldasoft.it", "collaudo"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://beans.ws.vigilanza.sil.eldasoft.it", "IstanzaOggettoType"), it.eldasoft.sil.vigilanza.ws.beans.IstanzaOggettoType.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://beans.ws.vigilanza.sil.eldasoft.it", "ResponseType"));
        oper.setReturnClass(it.eldasoft.sil.vigilanza.ws.beans.ResponseType.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://beans.ws.vigilanza.sil.eldasoft.it", "collaudoResult"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[10] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("istanziaElencoImpreseInvitate");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://beans.ws.vigilanza.sil.eldasoft.it", "login"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://beans.ws.vigilanza.sil.eldasoft.it", "LoginType"), it.eldasoft.sil.vigilanza.ws.beans.LoginType.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://beans.ws.vigilanza.sil.eldasoft.it", "elencoImpreseInvitate"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://beans.ws.vigilanza.sil.eldasoft.it", "IstanzaOggettoType"), it.eldasoft.sil.vigilanza.ws.beans.IstanzaOggettoType.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://beans.ws.vigilanza.sil.eldasoft.it", "ResponseType"));
        oper.setReturnClass(it.eldasoft.sil.vigilanza.ws.beans.ResponseType.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://beans.ws.vigilanza.sil.eldasoft.it", "elencoImpreseInvitateResult"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[11] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("istanziaAvviso");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://beans.ws.vigilanza.sil.eldasoft.it", "login"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://beans.ws.vigilanza.sil.eldasoft.it", "LoginType"), it.eldasoft.sil.vigilanza.ws.beans.LoginType.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://beans.ws.vigilanza.sil.eldasoft.it", "avviso"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://beans.ws.vigilanza.sil.eldasoft.it", "IstanzaOggettoType"), it.eldasoft.sil.vigilanza.ws.beans.IstanzaOggettoType.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://beans.ws.vigilanza.sil.eldasoft.it", "ResponseAvvisoType"));
        oper.setReturnClass(it.eldasoft.sil.vigilanza.ws.beans.ResponseAvvisoType.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://beans.ws.vigilanza.sil.eldasoft.it", "avvisoResult"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[12] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("istanziaPubblicazioneDocumenti");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://beans.ws.vigilanza.sil.eldasoft.it", "login"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://beans.ws.vigilanza.sil.eldasoft.it", "LoginType"), it.eldasoft.sil.vigilanza.ws.beans.LoginType.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://beans.ws.vigilanza.sil.eldasoft.it", "pubblicazioneDocumenti"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://beans.ws.vigilanza.sil.eldasoft.it", "IstanzaOggettoType"), it.eldasoft.sil.vigilanza.ws.beans.IstanzaOggettoType.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://beans.ws.vigilanza.sil.eldasoft.it", "ResponsePubblicazioneType"));
        oper.setReturnClass(it.eldasoft.sil.vigilanza.ws.beans.ResponsePubblicazioneType.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://beans.ws.vigilanza.sil.eldasoft.it", "pubblicazioneDocumentiResult"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[13] = oper;

    }

    public SitatWSSoapBindingStub() throws org.apache.axis.AxisFault {
         this(null);
    }

    public SitatWSSoapBindingStub(java.net.URL endpointURL, javax.xml.rpc.Service service) throws org.apache.axis.AxisFault {
         this(service);
         super.cachedEndpoint = endpointURL;
    }

    public SitatWSSoapBindingStub(javax.xml.rpc.Service service) throws org.apache.axis.AxisFault {
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
            qName = new javax.xml.namespace.QName("http://beans.ws.vigilanza.sil.eldasoft.it", "IstanzaOggettoType");
            cachedSerQNames.add(qName);
            cls = it.eldasoft.sil.vigilanza.ws.beans.IstanzaOggettoType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://beans.ws.vigilanza.sil.eldasoft.it", "LoginType");
            cachedSerQNames.add(qName);
            cls = it.eldasoft.sil.vigilanza.ws.beans.LoginType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://beans.ws.vigilanza.sil.eldasoft.it", "ResponseAvvisoType");
            cachedSerQNames.add(qName);
            cls = it.eldasoft.sil.vigilanza.ws.beans.ResponseAvvisoType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://beans.ws.vigilanza.sil.eldasoft.it", "ResponseLottoType");
            cachedSerQNames.add(qName);
            cls = it.eldasoft.sil.vigilanza.ws.beans.ResponseLottoType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://beans.ws.vigilanza.sil.eldasoft.it", "ResponsePubblicazioneType");
            cachedSerQNames.add(qName);
            cls = it.eldasoft.sil.vigilanza.ws.beans.ResponsePubblicazioneType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://beans.ws.vigilanza.sil.eldasoft.it", "ResponseSchedaAType");
            cachedSerQNames.add(qName);
            cls = it.eldasoft.sil.vigilanza.ws.beans.ResponseSchedaAType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://beans.ws.vigilanza.sil.eldasoft.it", "ResponseSchedaBType");
            cachedSerQNames.add(qName);
            cls = it.eldasoft.sil.vigilanza.ws.beans.ResponseSchedaBType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://beans.ws.vigilanza.sil.eldasoft.it", "ResponseType");
            cachedSerQNames.add(qName);
            cls = it.eldasoft.sil.vigilanza.ws.beans.ResponseType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://beans.ws.vigilanza.sil.eldasoft.it", "TestataType");
            cachedSerQNames.add(qName);
            cls = it.eldasoft.sil.vigilanza.ws.beans.TestataType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

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

    public it.eldasoft.sil.vigilanza.ws.beans.ResponseType istanziaGara(it.eldasoft.sil.vigilanza.ws.beans.LoginType login, it.eldasoft.sil.vigilanza.ws.beans.IstanzaOggettoType garaLotti) throws java.rmi.RemoteException {
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
        _call.setOperationName(new javax.xml.namespace.QName("http://beans.ws.vigilanza.sil.eldasoft.it", "istanziaGara"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {login, garaLotti});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (it.eldasoft.sil.vigilanza.ws.beans.ResponseType) _resp;
            } catch (java.lang.Exception _exception) {
                return (it.eldasoft.sil.vigilanza.ws.beans.ResponseType) org.apache.axis.utils.JavaUtils.convert(_resp, it.eldasoft.sil.vigilanza.ws.beans.ResponseType.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public it.eldasoft.sil.vigilanza.ws.beans.ResponseType istanziaAggiudicazione(it.eldasoft.sil.vigilanza.ws.beans.LoginType login, it.eldasoft.sil.vigilanza.ws.beans.IstanzaOggettoType aggiudicazione) throws java.rmi.RemoteException {
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
        _call.setOperationName(new javax.xml.namespace.QName("http://beans.ws.vigilanza.sil.eldasoft.it", "istanziaAggiudicazione"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {login, aggiudicazione});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (it.eldasoft.sil.vigilanza.ws.beans.ResponseType) _resp;
            } catch (java.lang.Exception _exception) {
                return (it.eldasoft.sil.vigilanza.ws.beans.ResponseType) org.apache.axis.utils.JavaUtils.convert(_resp, it.eldasoft.sil.vigilanza.ws.beans.ResponseType.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public it.eldasoft.sil.vigilanza.ws.beans.ResponseType istanziaEsito(it.eldasoft.sil.vigilanza.ws.beans.LoginType login, it.eldasoft.sil.vigilanza.ws.beans.IstanzaOggettoType esito) throws java.rmi.RemoteException {
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
        _call.setOperationName(new javax.xml.namespace.QName("http://beans.ws.vigilanza.sil.eldasoft.it", "istanziaEsito"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {login, esito});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (it.eldasoft.sil.vigilanza.ws.beans.ResponseType) _resp;
            } catch (java.lang.Exception _exception) {
                return (it.eldasoft.sil.vigilanza.ws.beans.ResponseType) org.apache.axis.utils.JavaUtils.convert(_resp, it.eldasoft.sil.vigilanza.ws.beans.ResponseType.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public it.eldasoft.sil.vigilanza.ws.beans.ResponseType istanziaContratto(it.eldasoft.sil.vigilanza.ws.beans.LoginType login, it.eldasoft.sil.vigilanza.ws.beans.IstanzaOggettoType contratto) throws java.rmi.RemoteException {
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
        _call.setOperationName(new javax.xml.namespace.QName("http://beans.ws.vigilanza.sil.eldasoft.it", "istanziaContratto"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {login, contratto});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (it.eldasoft.sil.vigilanza.ws.beans.ResponseType) _resp;
            } catch (java.lang.Exception _exception) {
                return (it.eldasoft.sil.vigilanza.ws.beans.ResponseType) org.apache.axis.utils.JavaUtils.convert(_resp, it.eldasoft.sil.vigilanza.ws.beans.ResponseType.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public it.eldasoft.sil.vigilanza.ws.beans.ResponseType istanziaInizio(it.eldasoft.sil.vigilanza.ws.beans.LoginType login, it.eldasoft.sil.vigilanza.ws.beans.IstanzaOggettoType inizio) throws java.rmi.RemoteException {
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
        _call.setOperationName(new javax.xml.namespace.QName("http://beans.ws.vigilanza.sil.eldasoft.it", "istanziaInizio"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {login, inizio});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (it.eldasoft.sil.vigilanza.ws.beans.ResponseType) _resp;
            } catch (java.lang.Exception _exception) {
                return (it.eldasoft.sil.vigilanza.ws.beans.ResponseType) org.apache.axis.utils.JavaUtils.convert(_resp, it.eldasoft.sil.vigilanza.ws.beans.ResponseType.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public it.eldasoft.sil.vigilanza.ws.beans.ResponseType istanziaAvanzamento(it.eldasoft.sil.vigilanza.ws.beans.LoginType login, it.eldasoft.sil.vigilanza.ws.beans.IstanzaOggettoType avanzamento) throws java.rmi.RemoteException {
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
        _call.setOperationName(new javax.xml.namespace.QName("http://beans.ws.vigilanza.sil.eldasoft.it", "istanziaAvanzamento"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {login, avanzamento});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (it.eldasoft.sil.vigilanza.ws.beans.ResponseType) _resp;
            } catch (java.lang.Exception _exception) {
                return (it.eldasoft.sil.vigilanza.ws.beans.ResponseType) org.apache.axis.utils.JavaUtils.convert(_resp, it.eldasoft.sil.vigilanza.ws.beans.ResponseType.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public it.eldasoft.sil.vigilanza.ws.beans.ResponseType istanziaSospensione(it.eldasoft.sil.vigilanza.ws.beans.LoginType login, it.eldasoft.sil.vigilanza.ws.beans.IstanzaOggettoType sospensione) throws java.rmi.RemoteException {
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
        _call.setOperationName(new javax.xml.namespace.QName("http://beans.ws.vigilanza.sil.eldasoft.it", "istanziaSospensione"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {login, sospensione});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (it.eldasoft.sil.vigilanza.ws.beans.ResponseType) _resp;
            } catch (java.lang.Exception _exception) {
                return (it.eldasoft.sil.vigilanza.ws.beans.ResponseType) org.apache.axis.utils.JavaUtils.convert(_resp, it.eldasoft.sil.vigilanza.ws.beans.ResponseType.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public it.eldasoft.sil.vigilanza.ws.beans.ResponseType istanziaVariante(it.eldasoft.sil.vigilanza.ws.beans.LoginType login, it.eldasoft.sil.vigilanza.ws.beans.IstanzaOggettoType variante) throws java.rmi.RemoteException {
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
        _call.setOperationName(new javax.xml.namespace.QName("http://beans.ws.vigilanza.sil.eldasoft.it", "istanziaVariante"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {login, variante});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (it.eldasoft.sil.vigilanza.ws.beans.ResponseType) _resp;
            } catch (java.lang.Exception _exception) {
                return (it.eldasoft.sil.vigilanza.ws.beans.ResponseType) org.apache.axis.utils.JavaUtils.convert(_resp, it.eldasoft.sil.vigilanza.ws.beans.ResponseType.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public it.eldasoft.sil.vigilanza.ws.beans.ResponseType istanziaSubappalto(it.eldasoft.sil.vigilanza.ws.beans.LoginType login, it.eldasoft.sil.vigilanza.ws.beans.IstanzaOggettoType subappalto) throws java.rmi.RemoteException {
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
        _call.setOperationName(new javax.xml.namespace.QName("http://beans.ws.vigilanza.sil.eldasoft.it", "istanziaSubappalto"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {login, subappalto});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (it.eldasoft.sil.vigilanza.ws.beans.ResponseType) _resp;
            } catch (java.lang.Exception _exception) {
                return (it.eldasoft.sil.vigilanza.ws.beans.ResponseType) org.apache.axis.utils.JavaUtils.convert(_resp, it.eldasoft.sil.vigilanza.ws.beans.ResponseType.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public it.eldasoft.sil.vigilanza.ws.beans.ResponseType istanziaConclusione(it.eldasoft.sil.vigilanza.ws.beans.LoginType login, it.eldasoft.sil.vigilanza.ws.beans.IstanzaOggettoType conclusione) throws java.rmi.RemoteException {
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
        _call.setOperationName(new javax.xml.namespace.QName("http://beans.ws.vigilanza.sil.eldasoft.it", "istanziaConclusione"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {login, conclusione});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (it.eldasoft.sil.vigilanza.ws.beans.ResponseType) _resp;
            } catch (java.lang.Exception _exception) {
                return (it.eldasoft.sil.vigilanza.ws.beans.ResponseType) org.apache.axis.utils.JavaUtils.convert(_resp, it.eldasoft.sil.vigilanza.ws.beans.ResponseType.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public it.eldasoft.sil.vigilanza.ws.beans.ResponseType istanziaCollaudo(it.eldasoft.sil.vigilanza.ws.beans.LoginType login, it.eldasoft.sil.vigilanza.ws.beans.IstanzaOggettoType collaudo) throws java.rmi.RemoteException {
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
        _call.setOperationName(new javax.xml.namespace.QName("http://beans.ws.vigilanza.sil.eldasoft.it", "istanziaCollaudo"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {login, collaudo});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (it.eldasoft.sil.vigilanza.ws.beans.ResponseType) _resp;
            } catch (java.lang.Exception _exception) {
                return (it.eldasoft.sil.vigilanza.ws.beans.ResponseType) org.apache.axis.utils.JavaUtils.convert(_resp, it.eldasoft.sil.vigilanza.ws.beans.ResponseType.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public it.eldasoft.sil.vigilanza.ws.beans.ResponseType istanziaElencoImpreseInvitate(it.eldasoft.sil.vigilanza.ws.beans.LoginType login, it.eldasoft.sil.vigilanza.ws.beans.IstanzaOggettoType elencoImpreseInvitate) throws java.rmi.RemoteException {
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
        _call.setOperationName(new javax.xml.namespace.QName("http://beans.ws.vigilanza.sil.eldasoft.it", "istanziaElencoImpreseInvitate"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {login, elencoImpreseInvitate});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (it.eldasoft.sil.vigilanza.ws.beans.ResponseType) _resp;
            } catch (java.lang.Exception _exception) {
                return (it.eldasoft.sil.vigilanza.ws.beans.ResponseType) org.apache.axis.utils.JavaUtils.convert(_resp, it.eldasoft.sil.vigilanza.ws.beans.ResponseType.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public it.eldasoft.sil.vigilanza.ws.beans.ResponseAvvisoType istanziaAvviso(it.eldasoft.sil.vigilanza.ws.beans.LoginType login, it.eldasoft.sil.vigilanza.ws.beans.IstanzaOggettoType avviso) throws java.rmi.RemoteException {
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
        _call.setOperationName(new javax.xml.namespace.QName("http://beans.ws.vigilanza.sil.eldasoft.it", "istanziaAvviso"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {login, avviso});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (it.eldasoft.sil.vigilanza.ws.beans.ResponseAvvisoType) _resp;
            } catch (java.lang.Exception _exception) {
                return (it.eldasoft.sil.vigilanza.ws.beans.ResponseAvvisoType) org.apache.axis.utils.JavaUtils.convert(_resp, it.eldasoft.sil.vigilanza.ws.beans.ResponseAvvisoType.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public it.eldasoft.sil.vigilanza.ws.beans.ResponsePubblicazioneType istanziaPubblicazioneDocumenti(it.eldasoft.sil.vigilanza.ws.beans.LoginType login, it.eldasoft.sil.vigilanza.ws.beans.IstanzaOggettoType pubblicazioneDocumenti) throws java.rmi.RemoteException {
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
        _call.setOperationName(new javax.xml.namespace.QName("http://beans.ws.vigilanza.sil.eldasoft.it", "istanziaPubblicazioneDocumenti"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {login, pubblicazioneDocumenti});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (it.eldasoft.sil.vigilanza.ws.beans.ResponsePubblicazioneType) _resp;
            } catch (java.lang.Exception _exception) {
                return (it.eldasoft.sil.vigilanza.ws.beans.ResponsePubblicazioneType) org.apache.axis.utils.JavaUtils.convert(_resp, it.eldasoft.sil.vigilanza.ws.beans.ResponsePubblicazioneType.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

}
