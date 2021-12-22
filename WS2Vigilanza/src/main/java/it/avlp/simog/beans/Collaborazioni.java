/**
 * Collaborazioni.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package it.avlp.simog.beans;

public class Collaborazioni  implements java.io.Serializable {
    private it.avlp.simog.beans.Collaborazione[] collaborazioni;

    public Collaborazioni() {
    }

    public Collaborazioni(
           it.avlp.simog.beans.Collaborazione[] collaborazioni) {
           this.collaborazioni = collaborazioni;
    }


    /**
     * Gets the collaborazioni value for this Collaborazioni.
     * 
     * @return collaborazioni
     */
    public it.avlp.simog.beans.Collaborazione[] getCollaborazioni() {
        return collaborazioni;
    }


    /**
     * Sets the collaborazioni value for this Collaborazioni.
     * 
     * @param collaborazioni
     */
    public void setCollaborazioni(it.avlp.simog.beans.Collaborazione[] collaborazioni) {
        this.collaborazioni = collaborazioni;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof Collaborazioni)) return false;
        Collaborazioni other = (Collaborazioni) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.collaborazioni==null && other.getCollaborazioni()==null) || 
             (this.collaborazioni!=null &&
              java.util.Arrays.equals(this.collaborazioni, other.getCollaborazioni())));
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
        if (getCollaborazioni() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getCollaborazioni());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getCollaborazioni(), i);
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
        new org.apache.axis.description.TypeDesc(Collaborazioni.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://beans.simog.avlp.it", "Collaborazioni"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("collaborazioni");
        elemField.setXmlName(new javax.xml.namespace.QName("http://beans.simog.avlp.it", "collaborazioni"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://beans.simog.avlp.it", "Collaborazione"));
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
