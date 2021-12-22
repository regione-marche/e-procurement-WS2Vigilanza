/**
 * EsitoConsultaIDGARA.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package it.eldasoft.simog.ws;

public class EsitoConsultaIDGARA  implements java.io.Serializable {
    private boolean esito;

    private java.lang.String messaggio;

    private java.lang.String idgara;

    public EsitoConsultaIDGARA() {
    }

    public EsitoConsultaIDGARA(
           boolean esito,
           java.lang.String messaggio,
           java.lang.String idgara) {
           this.esito = esito;
           this.messaggio = messaggio;
           this.idgara = idgara;
    }


    /**
     * Gets the esito value for this EsitoConsultaIDGARA.
     * 
     * @return esito
     */
    public boolean isEsito() {
        return esito;
    }


    /**
     * Sets the esito value for this EsitoConsultaIDGARA.
     * 
     * @param esito
     */
    public void setEsito(boolean esito) {
        this.esito = esito;
    }


    /**
     * Gets the messaggio value for this EsitoConsultaIDGARA.
     * 
     * @return messaggio
     */
    public java.lang.String getMessaggio() {
        return messaggio;
    }


    /**
     * Sets the messaggio value for this EsitoConsultaIDGARA.
     * 
     * @param messaggio
     */
    public void setMessaggio(java.lang.String messaggio) {
        this.messaggio = messaggio;
    }


    /**
     * Gets the idgara value for this EsitoConsultaIDGARA.
     * 
     * @return idgara
     */
    public java.lang.String getIdgara() {
        return idgara;
    }


    /**
     * Sets the idgara value for this EsitoConsultaIDGARA.
     * 
     * @param idgara
     */
    public void setIdgara(java.lang.String idgara) {
        this.idgara = idgara;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof EsitoConsultaIDGARA)) return false;
        EsitoConsultaIDGARA other = (EsitoConsultaIDGARA) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            this.esito == other.isEsito() &&
            ((this.messaggio==null && other.getMessaggio()==null) || 
             (this.messaggio!=null &&
              this.messaggio.equals(other.getMessaggio()))) &&
            ((this.idgara==null && other.getIdgara()==null) || 
             (this.idgara!=null &&
              this.idgara.equals(other.getIdgara())));
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
        _hashCode += (isEsito() ? Boolean.TRUE : Boolean.FALSE).hashCode();
        if (getMessaggio() != null) {
            _hashCode += getMessaggio().hashCode();
        }
        if (getIdgara() != null) {
            _hashCode += getIdgara().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(EsitoConsultaIDGARA.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://ws.simog.eldasoft.it/", "esitoConsultaIDGARA"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("esito");
        elemField.setXmlName(new javax.xml.namespace.QName("", "esito"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("messaggio");
        elemField.setXmlName(new javax.xml.namespace.QName("", "messaggio"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("idgara");
        elemField.setXmlName(new javax.xml.namespace.QName("", "idgara"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
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
