/**
 * OperazioneDMLType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package it.eldasoft.simog.ws;

public class OperazioneDMLType  implements java.io.Serializable {
    private it.eldasoft.simog.ws.InformazioneType tipoInformazione;

    private it.eldasoft.simog.ws.OperazioneType tipoOperazione;

    private java.lang.String uuid;

    public OperazioneDMLType() {
    }

    public OperazioneDMLType(
           it.eldasoft.simog.ws.InformazioneType tipoInformazione,
           it.eldasoft.simog.ws.OperazioneType tipoOperazione,
           java.lang.String uuid) {
           this.tipoInformazione = tipoInformazione;
           this.tipoOperazione = tipoOperazione;
           this.uuid = uuid;
    }


    /**
     * Gets the tipoInformazione value for this OperazioneDMLType.
     * 
     * @return tipoInformazione
     */
    public it.eldasoft.simog.ws.InformazioneType getTipoInformazione() {
        return tipoInformazione;
    }


    /**
     * Sets the tipoInformazione value for this OperazioneDMLType.
     * 
     * @param tipoInformazione
     */
    public void setTipoInformazione(it.eldasoft.simog.ws.InformazioneType tipoInformazione) {
        this.tipoInformazione = tipoInformazione;
    }


    /**
     * Gets the tipoOperazione value for this OperazioneDMLType.
     * 
     * @return tipoOperazione
     */
    public it.eldasoft.simog.ws.OperazioneType getTipoOperazione() {
        return tipoOperazione;
    }


    /**
     * Sets the tipoOperazione value for this OperazioneDMLType.
     * 
     * @param tipoOperazione
     */
    public void setTipoOperazione(it.eldasoft.simog.ws.OperazioneType tipoOperazione) {
        this.tipoOperazione = tipoOperazione;
    }


    /**
     * Gets the uuid value for this OperazioneDMLType.
     * 
     * @return uuid
     */
    public java.lang.String getUuid() {
        return uuid;
    }


    /**
     * Sets the uuid value for this OperazioneDMLType.
     * 
     * @param uuid
     */
    public void setUuid(java.lang.String uuid) {
        this.uuid = uuid;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof OperazioneDMLType)) return false;
        OperazioneDMLType other = (OperazioneDMLType) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.tipoInformazione==null && other.getTipoInformazione()==null) || 
             (this.tipoInformazione!=null &&
              this.tipoInformazione.equals(other.getTipoInformazione()))) &&
            ((this.tipoOperazione==null && other.getTipoOperazione()==null) || 
             (this.tipoOperazione!=null &&
              this.tipoOperazione.equals(other.getTipoOperazione()))) &&
            ((this.uuid==null && other.getUuid()==null) || 
             (this.uuid!=null &&
              this.uuid.equals(other.getUuid())));
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
        if (getTipoInformazione() != null) {
            _hashCode += getTipoInformazione().hashCode();
        }
        if (getTipoOperazione() != null) {
            _hashCode += getTipoOperazione().hashCode();
        }
        if (getUuid() != null) {
            _hashCode += getUuid().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(OperazioneDMLType.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://ws.simog.eldasoft.it/", "operazioneDMLType"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("tipoInformazione");
        elemField.setXmlName(new javax.xml.namespace.QName("", "tipoInformazione"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://ws.simog.eldasoft.it/", "informazioneType"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("tipoOperazione");
        elemField.setXmlName(new javax.xml.namespace.QName("", "tipoOperazione"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://ws.simog.eldasoft.it/", "operazioneType"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("uuid");
        elemField.setXmlName(new javax.xml.namespace.QName("", "uuid"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
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
