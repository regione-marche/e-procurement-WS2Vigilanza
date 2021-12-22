/**
 * ResponseInserisciGara.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package it.avlp.simog.common.beans;

public class ResponseInserisciGara  extends it.avlp.simog.common.beans.Response  implements java.io.Serializable {
    private java.lang.String id_gara;

    public ResponseInserisciGara() {
    }

    public ResponseInserisciGara(
           java.lang.String error,
           boolean success,
           java.lang.String id_gara) {
        super(
            error,
            success);
        this.id_gara = id_gara;
    }


    /**
     * Gets the id_gara value for this ResponseInserisciGara.
     * 
     * @return id_gara
     */
    public java.lang.String getId_gara() {
        return id_gara;
    }


    /**
     * Sets the id_gara value for this ResponseInserisciGara.
     * 
     * @param id_gara
     */
    public void setId_gara(java.lang.String id_gara) {
        this.id_gara = id_gara;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof ResponseInserisciGara)) return false;
        ResponseInserisciGara other = (ResponseInserisciGara) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = super.equals(obj) && 
            ((this.id_gara==null && other.getId_gara()==null) || 
             (this.id_gara!=null &&
              this.id_gara.equals(other.getId_gara())));
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
        if (getId_gara() != null) {
            _hashCode += getId_gara().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(ResponseInserisciGara.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://beans.common.simog.avlp.it", "ResponseInserisciGara"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("id_gara");
        elemField.setXmlName(new javax.xml.namespace.QName("http://beans.common.simog.avlp.it", "id_gara"));
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
