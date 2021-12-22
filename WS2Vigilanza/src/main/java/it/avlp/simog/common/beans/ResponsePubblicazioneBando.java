/**
 * ResponsePubblicazioneBando.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package it.avlp.simog.common.beans;

public class ResponsePubblicazioneBando  extends it.avlp.simog.common.beans.Response  implements java.io.Serializable {
    private it.avlp.simog.beans.CUPLOTTO[] CUPLOTTO;

    private java.lang.String messaggio;

    public ResponsePubblicazioneBando() {
    }

    public ResponsePubblicazioneBando(
           java.lang.String error,
           boolean success,
           it.avlp.simog.beans.CUPLOTTO[] CUPLOTTO,
           java.lang.String messaggio) {
        super(
            error,
            success);
        this.CUPLOTTO = CUPLOTTO;
        this.messaggio = messaggio;
    }


    /**
     * Gets the CUPLOTTO value for this ResponsePubblicazioneBando.
     * 
     * @return CUPLOTTO
     */
    public it.avlp.simog.beans.CUPLOTTO[] getCUPLOTTO() {
        return CUPLOTTO;
    }


    /**
     * Sets the CUPLOTTO value for this ResponsePubblicazioneBando.
     * 
     * @param CUPLOTTO
     */
    public void setCUPLOTTO(it.avlp.simog.beans.CUPLOTTO[] CUPLOTTO) {
        this.CUPLOTTO = CUPLOTTO;
    }


    /**
     * Gets the messaggio value for this ResponsePubblicazioneBando.
     * 
     * @return messaggio
     */
    public java.lang.String getMessaggio() {
        return messaggio;
    }


    /**
     * Sets the messaggio value for this ResponsePubblicazioneBando.
     * 
     * @param messaggio
     */
    public void setMessaggio(java.lang.String messaggio) {
        this.messaggio = messaggio;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof ResponsePubblicazioneBando)) return false;
        ResponsePubblicazioneBando other = (ResponsePubblicazioneBando) obj;
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
              java.util.Arrays.equals(this.CUPLOTTO, other.getCUPLOTTO()))) &&
            ((this.messaggio==null && other.getMessaggio()==null) || 
             (this.messaggio!=null &&
              this.messaggio.equals(other.getMessaggio())));
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
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getCUPLOTTO());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getCUPLOTTO(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getMessaggio() != null) {
            _hashCode += getMessaggio().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(ResponsePubblicazioneBando.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://beans.common.simog.avlp.it", "ResponsePubblicazioneBando"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("CUPLOTTO");
        elemField.setXmlName(new javax.xml.namespace.QName("http://beans.common.simog.avlp.it", "CUPLOTTO"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://beans.simog.avlp.it", "CUPLOTTO"));
        elemField.setNillable(true);
        elemField.setItemQName(new javax.xml.namespace.QName("http://beans.ws.simog.avlp.it", "item"));
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("messaggio");
        elemField.setXmlName(new javax.xml.namespace.QName("http://beans.common.simog.avlp.it", "messaggio"));
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
