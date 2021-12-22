/**
 * SitatWSSoapBindingSkeleton.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package it.eldasoft.sil.vigilanza.ws;

import it.eldasoft.sil.vigilanza.ws.beans.IstanzaOggettoType;
import it.eldasoft.sil.vigilanza.ws.beans.LoginType;
import it.eldasoft.sil.vigilanza.ws.beans.ResponseType;

import java.rmi.RemoteException;

public class SitatWSSoapBindingSkeleton implements it.eldasoft.sil.vigilanza.ws.SitatWS, org.apache.axis.wsdl.Skeleton {
    private it.eldasoft.sil.vigilanza.ws.SitatWS impl;
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
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://beans.ws.vigilanza.sil.eldasoft.it", "login"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://beans.ws.vigilanza.sil.eldasoft.it", "LoginType"), it.eldasoft.sil.vigilanza.ws.beans.LoginType.class, false, false), 
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://beans.ws.vigilanza.sil.eldasoft.it", "garaLotti"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://beans.ws.vigilanza.sil.eldasoft.it", "IstanzaOggettoType"), it.eldasoft.sil.vigilanza.ws.beans.IstanzaOggettoType.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("istanziaGara", _params, new javax.xml.namespace.QName("http://beans.ws.vigilanza.sil.eldasoft.it", "istanziaGaraReturn"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://beans.ws.vigilanza.sil.eldasoft.it", "ResponseType"));
        _oper.setElementQName(new javax.xml.namespace.QName("http://beans.ws.vigilanza.sil.eldasoft.it", "istanziaGara"));
        _oper.setSoapAction("");
        _myOperationsList.add(_oper);
        if (_myOperations.get("istanziaGara") == null) {
            _myOperations.put("istanziaGara", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("istanziaGara")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://beans.ws.vigilanza.sil.eldasoft.it", "login"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://beans.ws.vigilanza.sil.eldasoft.it", "LoginType"), it.eldasoft.sil.vigilanza.ws.beans.LoginType.class, false, false), 
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://beans.ws.vigilanza.sil.eldasoft.it", "aggiudicazione"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://beans.ws.vigilanza.sil.eldasoft.it", "IstanzaOggettoType"), it.eldasoft.sil.vigilanza.ws.beans.IstanzaOggettoType.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("istanziaAggiudicazione", _params, new javax.xml.namespace.QName("http://beans.ws.vigilanza.sil.eldasoft.it", "aggiudicazioneReturn"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://beans.ws.vigilanza.sil.eldasoft.it", "ResponseType"));
        _oper.setElementQName(new javax.xml.namespace.QName("http://beans.ws.vigilanza.sil.eldasoft.it", "istanziaAggiudicazione"));
        _oper.setSoapAction("");
        _myOperationsList.add(_oper);
        if (_myOperations.get("istanziaAggiudicazione") == null) {
            _myOperations.put("istanziaAggiudicazione", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("istanziaAggiudicazione")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://beans.ws.vigilanza.sil.eldasoft.it", "login"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://beans.ws.vigilanza.sil.eldasoft.it", "LoginType"), it.eldasoft.sil.vigilanza.ws.beans.LoginType.class, false, false), 
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://beans.ws.vigilanza.sil.eldasoft.it", "esito"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://beans.ws.vigilanza.sil.eldasoft.it", "IstanzaOggettoType"), it.eldasoft.sil.vigilanza.ws.beans.IstanzaOggettoType.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("istanziaEsito", _params, new javax.xml.namespace.QName("http://beans.ws.vigilanza.sil.eldasoft.it", "esitoReturn"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://beans.ws.vigilanza.sil.eldasoft.it", "ResponseType"));
        _oper.setElementQName(new javax.xml.namespace.QName("http://beans.ws.vigilanza.sil.eldasoft.it", "istanziaEsito"));
        _oper.setSoapAction("");
        _myOperationsList.add(_oper);
        if (_myOperations.get("istanziaEsito") == null) {
            _myOperations.put("istanziaEsito", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("istanziaEsito")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://beans.ws.vigilanza.sil.eldasoft.it", "login"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://beans.ws.vigilanza.sil.eldasoft.it", "LoginType"), it.eldasoft.sil.vigilanza.ws.beans.LoginType.class, false, false), 
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://beans.ws.vigilanza.sil.eldasoft.it", "contratto"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://beans.ws.vigilanza.sil.eldasoft.it", "IstanzaOggettoType"), it.eldasoft.sil.vigilanza.ws.beans.IstanzaOggettoType.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("istanziaContratto", _params, new javax.xml.namespace.QName("http://beans.ws.vigilanza.sil.eldasoft.it", "contrattoResult"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://beans.ws.vigilanza.sil.eldasoft.it", "ResponseType"));
        _oper.setElementQName(new javax.xml.namespace.QName("http://beans.ws.vigilanza.sil.eldasoft.it", "istanziaContratto"));
        _oper.setSoapAction("");
        _myOperationsList.add(_oper);
        if (_myOperations.get("istanziaContratto") == null) {
            _myOperations.put("istanziaContratto", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("istanziaContratto")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://beans.ws.vigilanza.sil.eldasoft.it", "login"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://beans.ws.vigilanza.sil.eldasoft.it", "LoginType"), it.eldasoft.sil.vigilanza.ws.beans.LoginType.class, false, false), 
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://beans.ws.vigilanza.sil.eldasoft.it", "inizio"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://beans.ws.vigilanza.sil.eldasoft.it", "IstanzaOggettoType"), it.eldasoft.sil.vigilanza.ws.beans.IstanzaOggettoType.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("istanziaInizio", _params, new javax.xml.namespace.QName("http://beans.ws.vigilanza.sil.eldasoft.it", "inizioResult"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://beans.ws.vigilanza.sil.eldasoft.it", "ResponseType"));
        _oper.setElementQName(new javax.xml.namespace.QName("http://beans.ws.vigilanza.sil.eldasoft.it", "istanziaInizio"));
        _oper.setSoapAction("");
        _myOperationsList.add(_oper);
        if (_myOperations.get("istanziaInizio") == null) {
            _myOperations.put("istanziaInizio", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("istanziaInizio")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://beans.ws.vigilanza.sil.eldasoft.it", "login"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://beans.ws.vigilanza.sil.eldasoft.it", "LoginType"), it.eldasoft.sil.vigilanza.ws.beans.LoginType.class, false, false), 
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://beans.ws.vigilanza.sil.eldasoft.it", "avanzamento"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://beans.ws.vigilanza.sil.eldasoft.it", "IstanzaOggettoType"), it.eldasoft.sil.vigilanza.ws.beans.IstanzaOggettoType.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("istanziaAvanzamento", _params, new javax.xml.namespace.QName("http://beans.ws.vigilanza.sil.eldasoft.it", "avanzamentoResult"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://beans.ws.vigilanza.sil.eldasoft.it", "ResponseType"));
        _oper.setElementQName(new javax.xml.namespace.QName("http://beans.ws.vigilanza.sil.eldasoft.it", "istanziaAvanzamento"));
        _oper.setSoapAction("");
        _myOperationsList.add(_oper);
        if (_myOperations.get("istanziaAvanzamento") == null) {
            _myOperations.put("istanziaAvanzamento", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("istanziaAvanzamento")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://beans.ws.vigilanza.sil.eldasoft.it", "login"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://beans.ws.vigilanza.sil.eldasoft.it", "LoginType"), it.eldasoft.sil.vigilanza.ws.beans.LoginType.class, false, false), 
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://beans.ws.vigilanza.sil.eldasoft.it", "sospensione"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://beans.ws.vigilanza.sil.eldasoft.it", "IstanzaOggettoType"), it.eldasoft.sil.vigilanza.ws.beans.IstanzaOggettoType.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("istanziaSospensione", _params, new javax.xml.namespace.QName("http://beans.ws.vigilanza.sil.eldasoft.it", "sospensioneResult"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://beans.ws.vigilanza.sil.eldasoft.it", "ResponseType"));
        _oper.setElementQName(new javax.xml.namespace.QName("http://beans.ws.vigilanza.sil.eldasoft.it", "istanziaSospensione"));
        _oper.setSoapAction("");
        _myOperationsList.add(_oper);
        if (_myOperations.get("istanziaSospensione") == null) {
            _myOperations.put("istanziaSospensione", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("istanziaSospensione")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://beans.ws.vigilanza.sil.eldasoft.it", "login"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://beans.ws.vigilanza.sil.eldasoft.it", "LoginType"), it.eldasoft.sil.vigilanza.ws.beans.LoginType.class, false, false), 
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://beans.ws.vigilanza.sil.eldasoft.it", "variante"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://beans.ws.vigilanza.sil.eldasoft.it", "IstanzaOggettoType"), it.eldasoft.sil.vigilanza.ws.beans.IstanzaOggettoType.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("istanziaVariante", _params, new javax.xml.namespace.QName("http://beans.ws.vigilanza.sil.eldasoft.it", "varianteResult"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://beans.ws.vigilanza.sil.eldasoft.it", "ResponseType"));
        _oper.setElementQName(new javax.xml.namespace.QName("http://beans.ws.vigilanza.sil.eldasoft.it", "istanziaVariante"));
        _oper.setSoapAction("");
        _myOperationsList.add(_oper);
        if (_myOperations.get("istanziaVariante") == null) {
            _myOperations.put("istanziaVariante", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("istanziaVariante")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://beans.ws.vigilanza.sil.eldasoft.it", "login"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://beans.ws.vigilanza.sil.eldasoft.it", "LoginType"), it.eldasoft.sil.vigilanza.ws.beans.LoginType.class, false, false), 
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://beans.ws.vigilanza.sil.eldasoft.it", "subappalto"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://beans.ws.vigilanza.sil.eldasoft.it", "IstanzaOggettoType"), it.eldasoft.sil.vigilanza.ws.beans.IstanzaOggettoType.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("istanziaSubappalto", _params, new javax.xml.namespace.QName("http://beans.ws.vigilanza.sil.eldasoft.it", "subappaltoResult"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://beans.ws.vigilanza.sil.eldasoft.it", "ResponseType"));
        _oper.setElementQName(new javax.xml.namespace.QName("http://beans.ws.vigilanza.sil.eldasoft.it", "istanziaSubappalto"));
        _oper.setSoapAction("");
        _myOperationsList.add(_oper);
        if (_myOperations.get("istanziaSubappalto") == null) {
            _myOperations.put("istanziaSubappalto", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("istanziaSubappalto")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://beans.ws.vigilanza.sil.eldasoft.it", "login"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://beans.ws.vigilanza.sil.eldasoft.it", "LoginType"), it.eldasoft.sil.vigilanza.ws.beans.LoginType.class, false, false), 
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://beans.ws.vigilanza.sil.eldasoft.it", "conclusione"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://beans.ws.vigilanza.sil.eldasoft.it", "IstanzaOggettoType"), it.eldasoft.sil.vigilanza.ws.beans.IstanzaOggettoType.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("istanziaConclusione", _params, new javax.xml.namespace.QName("http://beans.ws.vigilanza.sil.eldasoft.it", "conclusioneResult"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://beans.ws.vigilanza.sil.eldasoft.it", "ResponseType"));
        _oper.setElementQName(new javax.xml.namespace.QName("http://beans.ws.vigilanza.sil.eldasoft.it", "istanziaConclusione"));
        _oper.setSoapAction("");
        _myOperationsList.add(_oper);
        if (_myOperations.get("istanziaConclusione") == null) {
            _myOperations.put("istanziaConclusione", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("istanziaConclusione")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://beans.ws.vigilanza.sil.eldasoft.it", "login"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://beans.ws.vigilanza.sil.eldasoft.it", "LoginType"), it.eldasoft.sil.vigilanza.ws.beans.LoginType.class, false, false), 
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://beans.ws.vigilanza.sil.eldasoft.it", "collaudo"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://beans.ws.vigilanza.sil.eldasoft.it", "IstanzaOggettoType"), it.eldasoft.sil.vigilanza.ws.beans.IstanzaOggettoType.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("istanziaCollaudo", _params, new javax.xml.namespace.QName("http://beans.ws.vigilanza.sil.eldasoft.it", "collaudoResult"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://beans.ws.vigilanza.sil.eldasoft.it", "ResponseType"));
        _oper.setElementQName(new javax.xml.namespace.QName("http://beans.ws.vigilanza.sil.eldasoft.it", "istanziaCollaudo"));
        _oper.setSoapAction("");
        _myOperationsList.add(_oper);
        if (_myOperations.get("istanziaCollaudo") == null) {
            _myOperations.put("istanziaCollaudo", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("istanziaCollaudo")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://beans.ws.vigilanza.sil.eldasoft.it", "login"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://beans.ws.vigilanza.sil.eldasoft.it", "LoginType"), it.eldasoft.sil.vigilanza.ws.beans.LoginType.class, false, false), 
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://beans.ws.vigilanza.sil.eldasoft.it", "avviso"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://beans.ws.vigilanza.sil.eldasoft.it", "IstanzaOggettoType"), it.eldasoft.sil.vigilanza.ws.beans.IstanzaOggettoType.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("istanziaAvviso", _params, new javax.xml.namespace.QName("http://beans.ws.vigilanza.sil.eldasoft.it", "avvisoResult"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://beans.ws.vigilanza.sil.eldasoft.it", "ResponseAvvisoType"));
        _oper.setElementQName(new javax.xml.namespace.QName("http://beans.ws.vigilanza.sil.eldasoft.it", "istanziaAvviso"));
        _oper.setSoapAction("");
        _myOperationsList.add(_oper);
        if (_myOperations.get("istanziaAvviso") == null) {
            _myOperations.put("istanziaAvviso", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("istanziaAvviso")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://beans.ws.vigilanza.sil.eldasoft.it", "login"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://beans.ws.vigilanza.sil.eldasoft.it", "LoginType"), it.eldasoft.sil.vigilanza.ws.beans.LoginType.class, false, false), 
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://beans.ws.vigilanza.sil.eldasoft.it", "pubblicazioneDocumenti"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://beans.ws.vigilanza.sil.eldasoft.it", "IstanzaOggettoType"), it.eldasoft.sil.vigilanza.ws.beans.IstanzaOggettoType.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("istanziaPubblicazioneDocumenti", _params, new javax.xml.namespace.QName("http://beans.ws.vigilanza.sil.eldasoft.it", "pubblicazioneDocumentiResult"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://beans.ws.vigilanza.sil.eldasoft.it", "ResponsePubblicazioneType"));
        _oper.setElementQName(new javax.xml.namespace.QName("http://beans.ws.vigilanza.sil.eldasoft.it", "istanziaPubblicazioneDocumenti"));
        _oper.setSoapAction("");
        _myOperationsList.add(_oper);
        if (_myOperations.get("istanziaPubblicazioneDocumenti") == null) {
            _myOperations.put("istanziaPubblicazioneDocumenti", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("istanziaPubblicazioneDocumenti")).add(_oper);
    }

    public SitatWSSoapBindingSkeleton() {
        this.impl = new it.eldasoft.sil.vigilanza.ws.SitatWSSoapBindingImpl();
    }

    public SitatWSSoapBindingSkeleton(it.eldasoft.sil.vigilanza.ws.SitatWS impl) {
        this.impl = impl;
    }
    public it.eldasoft.sil.vigilanza.ws.beans.ResponseType istanziaGara(it.eldasoft.sil.vigilanza.ws.beans.LoginType login, it.eldasoft.sil.vigilanza.ws.beans.IstanzaOggettoType garaLotti) throws java.rmi.RemoteException
    {
        it.eldasoft.sil.vigilanza.ws.beans.ResponseType ret = impl.istanziaGara(login, garaLotti);
        return ret;
    }

    public it.eldasoft.sil.vigilanza.ws.beans.ResponseType istanziaAggiudicazione(it.eldasoft.sil.vigilanza.ws.beans.LoginType login, it.eldasoft.sil.vigilanza.ws.beans.IstanzaOggettoType aggiudicazione) throws java.rmi.RemoteException
    {
        it.eldasoft.sil.vigilanza.ws.beans.ResponseType ret = impl.istanziaAggiudicazione(login, aggiudicazione);
        return ret;
    }

    public it.eldasoft.sil.vigilanza.ws.beans.ResponseType istanziaEsito(it.eldasoft.sil.vigilanza.ws.beans.LoginType login, it.eldasoft.sil.vigilanza.ws.beans.IstanzaOggettoType esito) throws java.rmi.RemoteException
    {
        it.eldasoft.sil.vigilanza.ws.beans.ResponseType ret = impl.istanziaEsito(login, esito);
        return ret;
    }

    public it.eldasoft.sil.vigilanza.ws.beans.ResponseType istanziaContratto(it.eldasoft.sil.vigilanza.ws.beans.LoginType login, it.eldasoft.sil.vigilanza.ws.beans.IstanzaOggettoType contratto) throws java.rmi.RemoteException
    {
        it.eldasoft.sil.vigilanza.ws.beans.ResponseType ret = impl.istanziaContratto(login, contratto);
        return ret;
    }

    public it.eldasoft.sil.vigilanza.ws.beans.ResponseType istanziaInizio(it.eldasoft.sil.vigilanza.ws.beans.LoginType login, it.eldasoft.sil.vigilanza.ws.beans.IstanzaOggettoType inizio) throws java.rmi.RemoteException
    {
        it.eldasoft.sil.vigilanza.ws.beans.ResponseType ret = impl.istanziaInizio(login, inizio);
        return ret;
    }

    public it.eldasoft.sil.vigilanza.ws.beans.ResponseType istanziaAvanzamento(it.eldasoft.sil.vigilanza.ws.beans.LoginType login, it.eldasoft.sil.vigilanza.ws.beans.IstanzaOggettoType avanzamento) throws java.rmi.RemoteException
    {
        it.eldasoft.sil.vigilanza.ws.beans.ResponseType ret = impl.istanziaAvanzamento(login, avanzamento);
        return ret;
    }

    public it.eldasoft.sil.vigilanza.ws.beans.ResponseType istanziaSospensione(it.eldasoft.sil.vigilanza.ws.beans.LoginType login, it.eldasoft.sil.vigilanza.ws.beans.IstanzaOggettoType sospensione) throws java.rmi.RemoteException
    {
        it.eldasoft.sil.vigilanza.ws.beans.ResponseType ret = impl.istanziaSospensione(login, sospensione);
        return ret;
    }

    public it.eldasoft.sil.vigilanza.ws.beans.ResponseType istanziaVariante(it.eldasoft.sil.vigilanza.ws.beans.LoginType login, it.eldasoft.sil.vigilanza.ws.beans.IstanzaOggettoType variante) throws java.rmi.RemoteException
    {
        it.eldasoft.sil.vigilanza.ws.beans.ResponseType ret = impl.istanziaVariante(login, variante);
        return ret;
    }

    public it.eldasoft.sil.vigilanza.ws.beans.ResponseType istanziaSubappalto(it.eldasoft.sil.vigilanza.ws.beans.LoginType login, it.eldasoft.sil.vigilanza.ws.beans.IstanzaOggettoType subappalto) throws java.rmi.RemoteException
    {
        it.eldasoft.sil.vigilanza.ws.beans.ResponseType ret = impl.istanziaSubappalto(login, subappalto);
        return ret;
    }

    public it.eldasoft.sil.vigilanza.ws.beans.ResponseType istanziaConclusione(it.eldasoft.sil.vigilanza.ws.beans.LoginType login, it.eldasoft.sil.vigilanza.ws.beans.IstanzaOggettoType conclusione) throws java.rmi.RemoteException
    {
        it.eldasoft.sil.vigilanza.ws.beans.ResponseType ret = impl.istanziaConclusione(login, conclusione);
        return ret;
    }

    public it.eldasoft.sil.vigilanza.ws.beans.ResponseType istanziaCollaudo(it.eldasoft.sil.vigilanza.ws.beans.LoginType login, it.eldasoft.sil.vigilanza.ws.beans.IstanzaOggettoType collaudo) throws java.rmi.RemoteException
    {
        it.eldasoft.sil.vigilanza.ws.beans.ResponseType ret = impl.istanziaCollaudo(login, collaudo);
        return ret;
    }

    public it.eldasoft.sil.vigilanza.ws.beans.ResponseAvvisoType istanziaAvviso(it.eldasoft.sil.vigilanza.ws.beans.LoginType login, it.eldasoft.sil.vigilanza.ws.beans.IstanzaOggettoType avviso) throws java.rmi.RemoteException
    {
        it.eldasoft.sil.vigilanza.ws.beans.ResponseAvvisoType ret = impl.istanziaAvviso(login, avviso);
        return ret;
    }

    public it.eldasoft.sil.vigilanza.ws.beans.ResponsePubblicazioneType istanziaPubblicazioneDocumenti(it.eldasoft.sil.vigilanza.ws.beans.LoginType login, it.eldasoft.sil.vigilanza.ws.beans.IstanzaOggettoType pubblicazioneDocumenti) throws java.rmi.RemoteException
    {
        it.eldasoft.sil.vigilanza.ws.beans.ResponsePubblicazioneType ret = impl.istanziaPubblicazioneDocumenti(login, pubblicazioneDocumenti);
        return ret;
    }

		public ResponseType istanziaElencoImpreseInvitate(LoginType login, IstanzaOggettoType elencoImpreseInvitate) throws RemoteException
		{
			it.eldasoft.sil.vigilanza.ws.beans.ResponseType ret = impl.istanziaElencoImpreseInvitate(login, elencoImpreseInvitate);
			return ret;
		}

}
