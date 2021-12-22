/**
 * IstanzaOggettoType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package it.eldasoft.sil.vigilanza.ws.beans;


/**
 * Oggetto in input comune a tutti i metodi del WS. E' costituito
 * dalla testata (TestataType)
 * 						e dalla stringa che rappresenta l'XML dell'oggetto inviato dal
 * client.
 */
public class IstanzaOggettoType  implements java.io.Serializable {
    private it.eldasoft.sil.vigilanza.ws.beans.TestataType testata;

    private java.lang.String oggettoXML;

    public IstanzaOggettoType() {
    }

    public IstanzaOggettoType(
           it.eldasoft.sil.vigilanza.ws.beans.TestataType testata,
           java.lang.String oggettoXML) {
           this.testata = testata;
           this.oggettoXML = oggettoXML;
    }


    /**
     * Gets the testata value for this IstanzaOggettoType.
     * 
     * @return testata
     */
    public it.eldasoft.sil.vigilanza.ws.beans.TestataType getTestata() {
        return testata;
    }


    /**
     * Sets the testata value for this IstanzaOggettoType.
     * 
     * @param testata
     */
    public void setTestata(it.eldasoft.sil.vigilanza.ws.beans.TestataType testata) {
        this.testata = testata;
    }


    /**
     * Gets the oggettoXML value for this IstanzaOggettoType.
     * 
     * @return oggettoXML
     */
    public java.lang.String getOggettoXML() {
        return oggettoXML;
    }


    /**
     * Sets the oggettoXML value for this IstanzaOggettoType.
     * 
     * @param oggettoXML
     */
    public void setOggettoXML(java.lang.String oggettoXML) {
        this.oggettoXML = oggettoXML;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof IstanzaOggettoType)) return false;
        IstanzaOggettoType other = (IstanzaOggettoType) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.testata==null && other.getTestata()==null) || 
             (this.testata!=null &&
              this.testata.equals(other.getTestata()))) &&
            ((this.oggettoXML==null && other.getOggettoXML()==null) || 
             (this.oggettoXML!=null &&
              this.oggettoXML.equals(other.getOggettoXML())));
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
        if (getTestata() != null) {
            _hashCode += getTestata().hashCode();
        }
        if (getOggettoXML() != null) {
            _hashCode += getOggettoXML().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(IstanzaOggettoType.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://beans.ws.vigilanza.sil.eldasoft.it", "IstanzaOggettoType"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("testata");
        elemField.setXmlName(new javax.xml.namespace.QName("http://beans.ws.vigilanza.sil.eldasoft.it", "testata"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://beans.ws.vigilanza.sil.eldasoft.it", "TestataType"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("oggettoXML");
        elemField.setXmlName(new javax.xml.namespace.QName("http://beans.ws.vigilanza.sil.eldasoft.it", "oggettoXML"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
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
