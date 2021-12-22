/**
 * ResponseInserisciLotto.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package it.avlp.simog.common.beans;

public class ResponseInserisciLotto  extends it.avlp.simog.common.beans.Response  implements java.io.Serializable {
    private it.avlp.simog.beans.CUPLOTTO CUPLOTTO;

    private it.avlp.simog.beans.CIGBean cig;

    public ResponseInserisciLotto() {
    }

    public ResponseInserisciLotto(
           java.lang.String error,
           boolean success,
           it.avlp.simog.beans.CUPLOTTO CUPLOTTO,
           it.avlp.simog.beans.CIGBean cig) {
        super(
            error,
            success);
        this.CUPLOTTO = CUPLOTTO;
        this.cig = cig;
    }


    /**
     * Gets the CUPLOTTO value for this ResponseInserisciLotto.
     * 
     * @return CUPLOTTO
     */
    public it.avlp.simog.beans.CUPLOTTO getCUPLOTTO() {
        return CUPLOTTO;
    }


    /**
     * Sets the CUPLOTTO value for this ResponseInserisciLotto.
     * 
     * @param CUPLOTTO
     */
    public void setCUPLOTTO(it.avlp.simog.beans.CUPLOTTO CUPLOTTO) {
        this.CUPLOTTO = CUPLOTTO;
    }


    /**
     * Gets the cig value for this ResponseInserisciLotto.
     * 
     * @return cig
     */
    public it.avlp.simog.beans.CIGBean getCig() {
        return cig;
    }


    /**
     * Sets the cig value for this ResponseInserisciLotto.
     * 
     * @param cig
     */
    public void setCig(it.avlp.simog.beans.CIGBean cig) {
        this.cig = cig;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof ResponseInserisciLotto)) return false;
        ResponseInserisciLotto other = (ResponseInserisciLotto) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = super.equals(obj) && 
            ((this.CUPLOTTO==null && other.getCUPLOTTO()==null) || 
             (this.CUPLOTTO!=null &&
              this.CUPLOTTO.equals(other.getCUPLOTTO()))) &&
            ((this.cig==null && other.getCig()==null) || 
             (this.cig!=null &&
              this.cig.equals(other.getCig())));
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
        if (getCUPLOTTO() != null) {
            _hashCode += getCUPLOTTO().hashCode();
        }
        if (getCig() != null) {
            _hashCode += getCig().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(ResponseInserisciLotto.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://beans.common.simog.avlp.it", "ResponseInserisciLotto"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("CUPLOTTO");
        elemField.setXmlName(new javax.xml.namespace.QName("http://beans.common.simog.avlp.it", "CUPLOTTO"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://beans.simog.avlp.it", "CUPLOTTO"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("cig");
        elemField.setXmlName(new javax.xml.namespace.QName("http://beans.common.simog.avlp.it", "cig"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://beans.simog.avlp.it", "CIGBean"));
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
