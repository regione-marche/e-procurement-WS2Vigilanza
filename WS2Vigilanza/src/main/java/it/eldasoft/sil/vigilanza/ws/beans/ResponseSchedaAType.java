/**
 * ResponseSchedaAType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package it.eldasoft.sil.vigilanza.ws.beans;

public class ResponseSchedaAType  implements java.io.Serializable {
    private boolean success;

    private java.lang.String[] msgScheda;

    public ResponseSchedaAType() {
    }

    public ResponseSchedaAType(
           boolean success,
           java.lang.String[] msgScheda) {
           this.success = success;
           this.msgScheda = msgScheda;
    }


    /**
     * Gets the success value for this ResponseSchedaAType.
     * 
     * @return success
     */
    public boolean isSuccess() {
        return success;
    }


    /**
     * Sets the success value for this ResponseSchedaAType.
     * 
     * @param success
     */
    public void setSuccess(boolean success) {
        this.success = success;
    }


    /**
     * Gets the msgScheda value for this ResponseSchedaAType.
     * 
     * @return msgScheda
     */
    public java.lang.String[] getMsgScheda() {
        return msgScheda;
    }


    /**
     * Sets the msgScheda value for this ResponseSchedaAType.
     * 
     * @param msgScheda
     */
    public void setMsgScheda(java.lang.String[] msgScheda) {
        this.msgScheda = msgScheda;
    }

    public java.lang.String getMsgScheda(int i) {
        return this.msgScheda[i];
    }

    public void setMsgScheda(int i, java.lang.String _value) {
        this.msgScheda[i] = _value;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof ResponseSchedaAType)) return false;
        ResponseSchedaAType other = (ResponseSchedaAType) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            this.success == other.isSuccess() &&
            ((this.msgScheda==null && other.getMsgScheda()==null) || 
             (this.msgScheda!=null &&
              java.util.Arrays.equals(this.msgScheda, other.getMsgScheda())));
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
        _hashCode += (isSuccess() ? Boolean.TRUE : Boolean.FALSE).hashCode();
        if (getMsgScheda() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getMsgScheda());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getMsgScheda(), i);
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
        new org.apache.axis.description.TypeDesc(ResponseSchedaAType.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://beans.ws.vigilanza.sil.eldasoft.it", "ResponseSchedaAType"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("success");
        elemField.setXmlName(new javax.xml.namespace.QName("http://beans.ws.vigilanza.sil.eldasoft.it", "success"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("msgScheda");
        elemField.setXmlName(new javax.xml.namespace.QName("http://beans.ws.vigilanza.sil.eldasoft.it", "msgScheda"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setMaxOccursUnbounded(true);
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
