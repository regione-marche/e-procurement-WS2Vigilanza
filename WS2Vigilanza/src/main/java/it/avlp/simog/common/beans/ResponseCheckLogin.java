/**
 * ResponseCheckLogin.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package it.avlp.simog.common.beans;

public class ResponseCheckLogin  extends it.avlp.simog.common.beans.Response  implements java.io.Serializable {
    private it.avlp.simog.beans.Collaborazioni coll;

    private java.lang.String ticket;

    public ResponseCheckLogin() {
    }

    public ResponseCheckLogin(
           java.lang.String error,
           boolean success,
           it.avlp.simog.beans.Collaborazioni coll,
           java.lang.String ticket) {
        super(
            error,
            success);
        this.coll = coll;
        this.ticket = ticket;
    }


    /**
     * Gets the coll value for this ResponseCheckLogin.
     * 
     * @return coll
     */
    public it.avlp.simog.beans.Collaborazioni getColl() {
        return coll;
    }


    /**
     * Sets the coll value for this ResponseCheckLogin.
     * 
     * @param coll
     */
    public void setColl(it.avlp.simog.beans.Collaborazioni coll) {
        this.coll = coll;
    }


    /**
     * Gets the ticket value for this ResponseCheckLogin.
     * 
     * @return ticket
     */
    public java.lang.String getTicket() {
        return ticket;
    }


    /**
     * Sets the ticket value for this ResponseCheckLogin.
     * 
     * @param ticket
     */
    public void setTicket(java.lang.String ticket) {
        this.ticket = ticket;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof ResponseCheckLogin)) return false;
        ResponseCheckLogin other = (ResponseCheckLogin) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = super.equals(obj) && 
            ((this.coll==null && other.getColl()==null) || 
             (this.coll!=null &&
              this.coll.equals(other.getColl()))) &&
            ((this.ticket==null && other.getTicket()==null) || 
             (this.ticket!=null &&
              this.ticket.equals(other.getTicket())));
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
        if (getColl() != null) {
            _hashCode += getColl().hashCode();
        }
        if (getTicket() != null) {
            _hashCode += getTicket().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(ResponseCheckLogin.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://beans.common.simog.avlp.it", "ResponseCheckLogin"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("coll");
        elemField.setXmlName(new javax.xml.namespace.QName("http://beans.common.simog.avlp.it", "coll"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://beans.simog.avlp.it", "Collaborazioni"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("ticket");
        elemField.setXmlName(new javax.xml.namespace.QName("http://beans.common.simog.avlp.it", "ticket"));
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
