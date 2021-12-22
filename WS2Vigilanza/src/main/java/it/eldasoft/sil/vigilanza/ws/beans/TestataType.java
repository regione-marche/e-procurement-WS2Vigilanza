/**
 * TestataType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package it.eldasoft.sil.vigilanza.ws.beans;

public class TestataType  implements java.io.Serializable {
    /* Codice fiscale della Stazione appaltante */
    private java.lang.String CFEIN;

    /* Sovrascrittura dei dati */
    private java.lang.Boolean SOVRASCR;

    public TestataType() {
    }

    public TestataType(
           java.lang.String CFEIN,
           java.lang.Boolean SOVRASCR) {
           this.CFEIN = CFEIN;
           this.SOVRASCR = SOVRASCR;
    }


    /**
     * Gets the CFEIN value for this TestataType.
     * 
     * @return CFEIN   * Codice fiscale della Stazione appaltante
     */
    public java.lang.String getCFEIN() {
        return CFEIN;
    }


    /**
     * Sets the CFEIN value for this TestataType.
     * 
     * @param CFEIN   * Codice fiscale della Stazione appaltante
     */
    public void setCFEIN(java.lang.String CFEIN) {
        this.CFEIN = CFEIN;
    }


    /**
     * Gets the SOVRASCR value for this TestataType.
     * 
     * @return SOVRASCR   * Sovrascrittura dei dati
     */
    public java.lang.Boolean getSOVRASCR() {
        return SOVRASCR;
    }


    /**
     * Sets the SOVRASCR value for this TestataType.
     * 
     * @param SOVRASCR   * Sovrascrittura dei dati
     */
    public void setSOVRASCR(java.lang.Boolean SOVRASCR) {
        this.SOVRASCR = SOVRASCR;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof TestataType)) return false;
        TestataType other = (TestataType) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.CFEIN==null && other.getCFEIN()==null) || 
             (this.CFEIN!=null &&
              this.CFEIN.equals(other.getCFEIN()))) &&
            ((this.SOVRASCR==null && other.getSOVRASCR()==null) || 
             (this.SOVRASCR!=null &&
              this.SOVRASCR.equals(other.getSOVRASCR())));
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
        if (getCFEIN() != null) {
            _hashCode += getCFEIN().hashCode();
        }
        if (getSOVRASCR() != null) {
            _hashCode += getSOVRASCR().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(TestataType.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://beans.ws.vigilanza.sil.eldasoft.it", "TestataType"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("CFEIN");
        elemField.setXmlName(new javax.xml.namespace.QName("http://beans.ws.vigilanza.sil.eldasoft.it", "CFEIN"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("SOVRASCR");
        elemField.setXmlName(new javax.xml.namespace.QName("http://beans.ws.vigilanza.sil.eldasoft.it", "SOVRASCR"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
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
