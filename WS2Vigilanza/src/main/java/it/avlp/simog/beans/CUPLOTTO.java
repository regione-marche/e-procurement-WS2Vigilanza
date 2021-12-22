/**
 * CUPLOTTO.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package it.avlp.simog.beans;

public class CUPLOTTO  implements java.io.Serializable {
    private java.lang.String CIG;

    private it.avlp.simog.beans.CodiciCup[] CODICICUP;

    public CUPLOTTO() {
    }

    public CUPLOTTO(
           java.lang.String CIG,
           it.avlp.simog.beans.CodiciCup[] CODICICUP) {
           this.CIG = CIG;
           this.CODICICUP = CODICICUP;
    }


    /**
     * Gets the CIG value for this CUPLOTTO.
     * 
     * @return CIG
     */
    public java.lang.String getCIG() {
        return CIG;
    }


    /**
     * Sets the CIG value for this CUPLOTTO.
     * 
     * @param CIG
     */
    public void setCIG(java.lang.String CIG) {
        this.CIG = CIG;
    }


    /**
     * Gets the CODICICUP value for this CUPLOTTO.
     * 
     * @return CODICICUP
     */
    public it.avlp.simog.beans.CodiciCup[] getCODICICUP() {
        return CODICICUP;
    }


    /**
     * Sets the CODICICUP value for this CUPLOTTO.
     * 
     * @param CODICICUP
     */
    public void setCODICICUP(it.avlp.simog.beans.CodiciCup[] CODICICUP) {
        this.CODICICUP = CODICICUP;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof CUPLOTTO)) return false;
        CUPLOTTO other = (CUPLOTTO) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.CIG==null && other.getCIG()==null) || 
             (this.CIG!=null &&
              this.CIG.equals(other.getCIG()))) &&
            ((this.CODICICUP==null && other.getCODICICUP()==null) || 
             (this.CODICICUP!=null &&
              java.util.Arrays.equals(this.CODICICUP, other.getCODICICUP())));
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
        if (getCIG() != null) {
            _hashCode += getCIG().hashCode();
        }
        if (getCODICICUP() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getCODICICUP());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getCODICICUP(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(CUPLOTTO.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://beans.simog.avlp.it", "CUPLOTTO"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("CIG");
        elemField.setXmlName(new javax.xml.namespace.QName("http://beans.simog.avlp.it", "CIG"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("CODICICUP");
        elemField.setXmlName(new javax.xml.namespace.QName("http://beans.simog.avlp.it", "CODICICUP"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://beans.simog.avlp.it", "CodiciCup"));
        elemField.setNillable(true);
        elemField.setItemQName(new javax.xml.namespace.QName("http://beans.ws.simog.avlp.it", "item"));
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
