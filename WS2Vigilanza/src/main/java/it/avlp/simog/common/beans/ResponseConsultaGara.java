/**
 * ResponseConsultaGara.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package it.avlp.simog.common.beans;

public class ResponseConsultaGara  extends it.avlp.simog.common.beans.Response  implements java.io.Serializable {
    private java.lang.String garaXML;

    public ResponseConsultaGara() {
    }

    public ResponseConsultaGara(
           java.lang.String error,
           boolean success,
           java.lang.String garaXML) {
        super(
            error,
            success);
        this.garaXML = garaXML;
    }


    /**
     * Gets the garaXML value for this ResponseConsultaGara.
     * 
     * @return garaXML
     */
    public java.lang.String getGaraXML() {
        return garaXML;
    }


    /**
     * Sets the garaXML value for this ResponseConsultaGara.
     * 
     * @param garaXML
     */
    public void setGaraXML(java.lang.String garaXML) {
        this.garaXML = garaXML;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof ResponseConsultaGara)) return false;
        ResponseConsultaGara other = (ResponseConsultaGara) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = super.equals(obj) && 
            ((this.garaXML==null && other.getGaraXML()==null) || 
             (this.garaXML!=null &&
              this.garaXML.equals(other.getGaraXML())));
        __equalsCalc = null;
        return _equals;
    }

    private boolean __hashCodeCalc = false;
    public synchronized int hashCode() {
        if (__hashCodeCalc) {
            return 0;
        }
        __hashCodeCalc = true;
        int _hashCode = super.hashCode();
        if (getGaraXML() != null) {
            _hashCode += getGaraXML().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(ResponseConsultaGara.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://beans.common.simog.avlp.it", "ResponseConsultaGara"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("garaXML");
        elemField.setXmlName(new javax.xml.namespace.QName("http://beans.common.simog.avlp.it", "garaXML"));
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
