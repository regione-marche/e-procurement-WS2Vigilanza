/**
 * Collaborazione.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package it.avlp.simog.beans;

public class Collaborazione  implements java.io.Serializable {
    private java.lang.String azienda_codiceFiscale;

    private java.lang.String azienda_denominazione;

    private java.lang.String idOsservatorio;

    private java.lang.String index;

    private java.lang.String ufficio_denominazione;

    private java.lang.String ufficio_id;

    private java.lang.String ufficio_profilo;

    public Collaborazione() {
    }

    public Collaborazione(
           java.lang.String azienda_codiceFiscale,
           java.lang.String azienda_denominazione,
           java.lang.String idOsservatorio,
           java.lang.String index,
           java.lang.String ufficio_denominazione,
           java.lang.String ufficio_id,
           java.lang.String ufficio_profilo) {
           this.azienda_codiceFiscale = azienda_codiceFiscale;
           this.azienda_denominazione = azienda_denominazione;
           this.idOsservatorio = idOsservatorio;
           this.index = index;
           this.ufficio_denominazione = ufficio_denominazione;
           this.ufficio_id = ufficio_id;
           this.ufficio_profilo = ufficio_profilo;
    }


    /**
     * Gets the azienda_codiceFiscale value for this Collaborazione.
     * 
     * @return azienda_codiceFiscale
     */
    public java.lang.String getAzienda_codiceFiscale() {
        return azienda_codiceFiscale;
    }


    /**
     * Sets the azienda_codiceFiscale value for this Collaborazione.
     * 
     * @param azienda_codiceFiscale
     */
    public void setAzienda_codiceFiscale(java.lang.String azienda_codiceFiscale) {
        this.azienda_codiceFiscale = azienda_codiceFiscale;
    }


    /**
     * Gets the azienda_denominazione value for this Collaborazione.
     * 
     * @return azienda_denominazione
     */
    public java.lang.String getAzienda_denominazione() {
        return azienda_denominazione;
    }


    /**
     * Sets the azienda_denominazione value for this Collaborazione.
     * 
     * @param azienda_denominazione
     */
    public void setAzienda_denominazione(java.lang.String azienda_denominazione) {
        this.azienda_denominazione = azienda_denominazione;
    }


    /**
     * Gets the idOsservatorio value for this Collaborazione.
     * 
     * @return idOsservatorio
     */
    public java.lang.String getIdOsservatorio() {
        return idOsservatorio;
    }


    /**
     * Sets the idOsservatorio value for this Collaborazione.
     * 
     * @param idOsservatorio
     */
    public void setIdOsservatorio(java.lang.String idOsservatorio) {
        this.idOsservatorio = idOsservatorio;
    }


    /**
     * Gets the index value for this Collaborazione.
     * 
     * @return index
     */
    public java.lang.String getIndex() {
        return index;
    }


    /**
     * Sets the index value for this Collaborazione.
     * 
     * @param index
     */
    public void setIndex(java.lang.String index) {
        this.index = index;
    }


    /**
     * Gets the ufficio_denominazione value for this Collaborazione.
     * 
     * @return ufficio_denominazione
     */
    public java.lang.String getUfficio_denominazione() {
        return ufficio_denominazione;
    }


    /**
     * Sets the ufficio_denominazione value for this Collaborazione.
     * 
     * @param ufficio_denominazione
     */
    public void setUfficio_denominazione(java.lang.String ufficio_denominazione) {
        this.ufficio_denominazione = ufficio_denominazione;
    }


    /**
     * Gets the ufficio_id value for this Collaborazione.
     * 
     * @return ufficio_id
     */
    public java.lang.String getUfficio_id() {
        return ufficio_id;
    }


    /**
     * Sets the ufficio_id value for this Collaborazione.
     * 
     * @param ufficio_id
     */
    public void setUfficio_id(java.lang.String ufficio_id) {
        this.ufficio_id = ufficio_id;
    }


    /**
     * Gets the ufficio_profilo value for this Collaborazione.
     * 
     * @return ufficio_profilo
     */
    public java.lang.String getUfficio_profilo() {
        return ufficio_profilo;
    }


    /**
     * Sets the ufficio_profilo value for this Collaborazione.
     * 
     * @param ufficio_profilo
     */
    public void setUfficio_profilo(java.lang.String ufficio_profilo) {
        this.ufficio_profilo = ufficio_profilo;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof Collaborazione)) return false;
        Collaborazione other = (Collaborazione) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.azienda_codiceFiscale==null && other.getAzienda_codiceFiscale()==null) || 
             (this.azienda_codiceFiscale!=null &&
              this.azienda_codiceFiscale.equals(other.getAzienda_codiceFiscale()))) &&
            ((this.azienda_denominazione==null && other.getAzienda_denominazione()==null) || 
             (this.azienda_denominazione!=null &&
              this.azienda_denominazione.equals(other.getAzienda_denominazione()))) &&
            ((this.idOsservatorio==null && other.getIdOsservatorio()==null) || 
             (this.idOsservatorio!=null &&
              this.idOsservatorio.equals(other.getIdOsservatorio()))) &&
            ((this.index==null && other.getIndex()==null) || 
             (this.index!=null &&
              this.index.equals(other.getIndex()))) &&
            ((this.ufficio_denominazione==null && other.getUfficio_denominazione()==null) || 
             (this.ufficio_denominazione!=null &&
              this.ufficio_denominazione.equals(other.getUfficio_denominazione()))) &&
            ((this.ufficio_id==null && other.getUfficio_id()==null) || 
             (this.ufficio_id!=null &&
              this.ufficio_id.equals(other.getUfficio_id()))) &&
            ((this.ufficio_profilo==null && other.getUfficio_profilo()==null) || 
             (this.ufficio_profilo!=null &&
              this.ufficio_profilo.equals(other.getUfficio_profilo())));
        __equalsCalc = null;
        return _equals;
    }

    private boolean __hashCodeCalc = false;
    public synchronized int hashCode() {
        if (__hashCodeCalc) {
            return 0;
        }
        __hashCodeCalc = true;
        int _hashCode = 1;
        if (getAzienda_codiceFiscale() != null) {
            _hashCode += getAzienda_codiceFiscale().hashCode();
        }
        if (getAzienda_denominazione() != null) {
            _hashCode += getAzienda_denominazione().hashCode();
        }
        if (getIdOsservatorio() != null) {
            _hashCode += getIdOsservatorio().hashCode();
        }
        if (getIndex() != null) {
            _hashCode += getIndex().hashCode();
        }
        if (getUfficio_denominazione() != null) {
            _hashCode += getUfficio_denominazione().hashCode();
        }
        if (getUfficio_id() != null) {
            _hashCode += getUfficio_id().hashCode();
        }
        if (getUfficio_profilo() != null) {
            _hashCode += getUfficio_profilo().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(Collaborazione.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://beans.simog.avlp.it", "Collaborazione"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("azienda_codiceFiscale");
        elemField.setXmlName(new javax.xml.namespace.QName("http://beans.simog.avlp.it", "azienda_codiceFiscale"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("azienda_denominazione");
        elemField.setXmlName(new javax.xml.namespace.QName("http://beans.simog.avlp.it", "azienda_denominazione"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("idOsservatorio");
        elemField.setXmlName(new javax.xml.namespace.QName("http://beans.simog.avlp.it", "idOsservatorio"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("index");
        elemField.setXmlName(new javax.xml.namespace.QName("http://beans.simog.avlp.it", "index"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("ufficio_denominazione");
        elemField.setXmlName(new javax.xml.namespace.QName("http://beans.simog.avlp.it", "ufficio_denominazione"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("ufficio_id");
        elemField.setXmlName(new javax.xml.namespace.QName("http://beans.simog.avlp.it", "ufficio_id"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("ufficio_profilo");
        elemField.setXmlName(new javax.xml.namespace.QName("http://beans.simog.avlp.it", "ufficio_profilo"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
    }

    /**
     * Return type metadata object
     */
    public static org.apache.axis.description.TypeDesc getTypeDesc() {
        return typeDesc;
    }

    /**
     * Get Custom Serializer
     */
    public static org.apache.axis.encoding.Serializer getSerializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanSerializer(
            _javaType, _xmlType, typeDesc);
    }

    /**
     * Get Custom Deserializer
     */
    public static org.apache.axis.encoding.Deserializer getDeserializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanDeserializer(
            _javaType, _xmlType, typeDesc);
    }

}
