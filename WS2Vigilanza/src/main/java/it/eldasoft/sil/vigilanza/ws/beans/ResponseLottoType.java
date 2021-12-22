/**
 * ResponseLottoType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package it.eldasoft.sil.vigilanza.ws.beans;

public class ResponseLottoType  implements java.io.Serializable {
    private java.lang.String CIG;

    private it.eldasoft.sil.vigilanza.ws.beans.ResponseSchedaAType resultSchedaAType;

    private it.eldasoft.sil.vigilanza.ws.beans.ResponseSchedaBType[] resultSchedaBType;

    public ResponseLottoType() {
    }

    public ResponseLottoType(
           java.lang.String CIG,
           it.eldasoft.sil.vigilanza.ws.beans.ResponseSchedaAType resultSchedaAType,
           it.eldasoft.sil.vigilanza.ws.beans.ResponseSchedaBType[] resultSchedaBType) {
           this.CIG = CIG;
           this.resultSchedaAType = resultSchedaAType;
           this.resultSchedaBType = resultSchedaBType;
    }


    /**
     * Gets the CIG value for this ResponseLottoType.
     * 
     * @return CIG
     */
    public java.lang.String getCIG() {
        return CIG;
    }


    /**
     * Sets the CIG value for this ResponseLottoType.
     * 
     * @param CIG
     */
    public void setCIG(java.lang.String CIG) {
        this.CIG = CIG;
    }


    /**
     * Gets the resultSchedaAType value for this ResponseLottoType.
     * 
     * @return resultSchedaAType
     */
    public it.eldasoft.sil.vigilanza.ws.beans.ResponseSchedaAType getResultSchedaAType() {
        return resultSchedaAType;
    }


    /**
     * Sets the resultSchedaAType value for this ResponseLottoType.
     * 
     * @param resultSchedaAType
     */
    public void setResultSchedaAType(it.eldasoft.sil.vigilanza.ws.beans.ResponseSchedaAType resultSchedaAType) {
        this.resultSchedaAType = resultSchedaAType;
    }


    /**
     * Gets the resultSchedaBType value for this ResponseLottoType.
     * 
     * @return resultSchedaBType
     */
    public it.eldasoft.sil.vigilanza.ws.beans.ResponseSchedaBType[] getResultSchedaBType() {
        return resultSchedaBType;
    }


    /**
     * Sets the resultSchedaBType value for this ResponseLottoType.
     * 
     * @param resultSchedaBType
     */
    public void setResultSchedaBType(it.eldasoft.sil.vigilanza.ws.beans.ResponseSchedaBType[] resultSchedaBType) {
        this.resultSchedaBType = resultSchedaBType;
    }

    public it.eldasoft.sil.vigilanza.ws.beans.ResponseSchedaBType getResultSchedaBType(int i) {
        return this.resultSchedaBType[i];
    }

    public void setResultSchedaBType(int i, it.eldasoft.sil.vigilanza.ws.beans.ResponseSchedaBType _value) {
        this.resultSchedaBType[i] = _value;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof ResponseLottoType)) return false;
        ResponseLottoType other = (ResponseLottoType) obj;
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
            ((this.resultSchedaAType==null && other.getResultSchedaAType()==null) || 
             (this.resultSchedaAType!=null &&
              this.resultSchedaAType.equals(other.getResultSchedaAType()))) &&
            ((this.resultSchedaBType==null && other.getResultSchedaBType()==null) || 
             (this.resultSchedaBType!=null &&
              java.util.Arrays.equals(this.resultSchedaBType, other.getResultSchedaBType())));
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
        if (getResultSchedaAType() != null) {
            _hashCode += getResultSchedaAType().hashCode();
        }
        if (getResultSchedaBType() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getResultSchedaBType());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getResultSchedaBType(), i);
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
        new org.apache.axis.description.TypeDesc(ResponseLottoType.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://beans.ws.vigilanza.sil.eldasoft.it", "ResponseLottoType"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("CIG");
        elemField.setXmlName(new javax.xml.namespace.QName("http://beans.ws.vigilanza.sil.eldasoft.it", "CIG"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("resultSchedaAType");
        elemField.setXmlName(new javax.xml.namespace.QName("http://beans.ws.vigilanza.sil.eldasoft.it", "resultSchedaAType"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://beans.ws.vigilanza.sil.eldasoft.it", "ResponseSchedaAType"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("resultSchedaBType");
        elemField.setXmlName(new javax.xml.namespace.QName("http://beans.ws.vigilanza.sil.eldasoft.it", "resultSchedaBType"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://beans.ws.vigilanza.sil.eldasoft.it", "ResponseSchedaBType"));
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
