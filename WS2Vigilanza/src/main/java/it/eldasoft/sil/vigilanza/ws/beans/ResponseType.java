/**
 * ResponseType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package it.eldasoft.sil.vigilanza.ws.beans;

public class ResponseType  implements java.io.Serializable {
    private boolean success;

    private java.lang.String error;

    private it.eldasoft.sil.vigilanza.ws.beans.ResponseLottoType[] resultLotti;

    public ResponseType() {
    }

    public ResponseType(
           boolean success,
           java.lang.String error,
           it.eldasoft.sil.vigilanza.ws.beans.ResponseLottoType[] resultLotti) {
           this.success = success;
           this.error = error;
           this.resultLotti = resultLotti;
    }


    /**
     * Gets the success value for this ResponseType.
     * 
     * @return success
     */
    public boolean isSuccess() {
        return success;
    }


    /**
     * Sets the success value for this ResponseType.
     * 
     * @param success
     */
    public void setSuccess(boolean success) {
        this.success = success;
    }


    /**
     * Gets the error value for this ResponseType.
     * 
     * @return error
     */
    public java.lang.String getError() {
        return error;
    }


    /**
     * Sets the error value for this ResponseType.
     * 
     * @param error
     */
    public void setError(java.lang.String error) {
        this.error = error;
    }


    /**
     * Gets the resultLotti value for this ResponseType.
     * 
     * @return resultLotti
     */
    public it.eldasoft.sil.vigilanza.ws.beans.ResponseLottoType[] getResultLotti() {
        return resultLotti;
    }


    /**
     * Sets the resultLotti value for this ResponseType.
     * 
     * @param resultLotti
     */
    public void setResultLotti(it.eldasoft.sil.vigilanza.ws.beans.ResponseLottoType[] resultLotti) {
        this.resultLotti = resultLotti;
    }

    public it.eldasoft.sil.vigilanza.ws.beans.ResponseLottoType getResultLotti(int i) {
        return this.resultLotti[i];
    }

    public void setResultLotti(int i, it.eldasoft.sil.vigilanza.ws.beans.ResponseLottoType _value) {
        this.resultLotti[i] = _value;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof ResponseType)) return false;
        ResponseType other = (ResponseType) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            this.success == other.isSuccess() &&
            ((this.error==null && other.getError()==null) || 
             (this.error!=null &&
              this.error.equals(other.getError()))) &&
            ((this.resultLotti==null && other.getResultLotti()==null) || 
             (this.resultLotti!=null &&
              java.util.Arrays.equals(this.resultLotti, other.getResultLotti())));
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
        if (getError() != null) {
            _hashCode += getError().hashCode();
        }
        if (getResultLotti() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getResultLotti());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getResultLotti(), i);
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
        new org.apache.axis.description.TypeDesc(ResponseType.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://beans.ws.vigilanza.sil.eldasoft.it", "ResponseType"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("success");
        elemField.setXmlName(new javax.xml.namespace.QName("http://beans.ws.vigilanza.sil.eldasoft.it", "success"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("error");
        elemField.setXmlName(new javax.xml.namespace.QName("http://beans.ws.vigilanza.sil.eldasoft.it", "error"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("resultLotti");
        elemField.setXmlName(new javax.xml.namespace.QName("http://beans.ws.vigilanza.sil.eldasoft.it", "resultLotti"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://beans.ws.vigilanza.sil.eldasoft.it", "ResponseLottoType"));
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
