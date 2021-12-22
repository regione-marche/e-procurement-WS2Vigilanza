/**
 * TIPIAPPALTO.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package it.avlp.simog.beans;

public class TIPIAPPALTO  implements java.io.Serializable {
    private java.lang.String[] TIPOAPPALTO;

    public TIPIAPPALTO() {
    }

    public TIPIAPPALTO(
           java.lang.String[] TIPOAPPALTO) {
           this.TIPOAPPALTO = TIPOAPPALTO;
    }


    /**
     * Gets the TIPOAPPALTO value for this TIPIAPPALTO.
     * 
     * @return TIPOAPPALTO
     */
    public java.lang.String[] getTIPOAPPALTO() {
        return TIPOAPPALTO;
    }


    /**
     * Sets the TIPOAPPALTO value for this TIPIAPPALTO.
     * 
     * @param TIPOAPPALTO
     */
    public void setTIPOAPPALTO(java.lang.String[] TIPOAPPALTO) {
        this.TIPOAPPALTO = TIPOAPPALTO;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof TIPIAPPALTO)) return false;
        TIPIAPPALTO other = (TIPIAPPALTO) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.TIPOAPPALTO==null && other.getTIPOAPPALTO()==null) || 
             (this.TIPOAPPALTO!=null &&
              java.util.Arrays.equals(this.TIPOAPPALTO, other.getTIPOAPPALTO())));
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
        if (getTIPOAPPALTO() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getTIPOAPPALTO());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getTIPOAPPALTO(), i);
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
        new org.apache.axis.description.TypeDesc(TIPIAPPALTO.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://beans.simog.avlp.it", "TIPIAPPALTO"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("TIPOAPPALTO");
        elemField.setXmlName(new javax.xml.namespace.QName("http://beans.simog.avlp.it", "TIPOAPPALTO"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
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
