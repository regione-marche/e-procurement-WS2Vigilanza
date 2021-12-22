/**
 * EsitoInserisciGaraLotto.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package it.eldasoft.simog.ws;

public class EsitoInserisciGaraLotto  implements java.io.Serializable {
    private boolean esito;

    private java.lang.String messaggio;

    private it.eldasoft.simog.ws.OperazioneDMLType[] operazioniDML;

    public EsitoInserisciGaraLotto() {
    }

    public EsitoInserisciGaraLotto(
           boolean esito,
           java.lang.String messaggio,
           it.eldasoft.simog.ws.OperazioneDMLType[] operazioniDML) {
           this.esito = esito;
           this.messaggio = messaggio;
           this.operazioniDML = operazioniDML;
    }


    /**
     * Gets the esito value for this EsitoInserisciGaraLotto.
     * 
     * @return esito
     */
    public boolean isEsito() {
        return esito;
    }


    /**
     * Sets the esito value for this EsitoInserisciGaraLotto.
     * 
     * @param esito
     */
    public void setEsito(boolean esito) {
        this.esito = esito;
    }


    /**
     * Gets the messaggio value for this EsitoInserisciGaraLotto.
     * 
     * @return messaggio
     */
    public java.lang.String getMessaggio() {
        return messaggio;
    }


    /**
     * Sets the messaggio value for this EsitoInserisciGaraLotto.
     * 
     * @param messaggio
     */
    public void setMessaggio(java.lang.String messaggio) {
        this.messaggio = messaggio;
    }


    /**
     * Gets the operazioniDML value for this EsitoInserisciGaraLotto.
     * 
     * @return operazioniDML
     */
    public it.eldasoft.simog.ws.OperazioneDMLType[] getOperazioniDML() {
        return operazioniDML;
    }


    /**
     * Sets the operazioniDML value for this EsitoInserisciGaraLotto.
     * 
     * @param operazioniDML
     */
    public void setOperazioniDML(it.eldasoft.simog.ws.OperazioneDMLType[] operazioniDML) {
        this.operazioniDML = operazioniDML;
    }

    public it.eldasoft.simog.ws.OperazioneDMLType getOperazioniDML(int i) {
        return this.operazioniDML[i];
    }

    public void setOperazioniDML(int i, it.eldasoft.simog.ws.OperazioneDMLType _value) {
        this.operazioniDML[i] = _value;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof EsitoInserisciGaraLotto)) return false;
        EsitoInserisciGaraLotto other = (EsitoInserisciGaraLotto) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            this.esito == other.isEsito() &&
            ((this.messaggio==null && other.getMessaggio()==null) || 
             (this.messaggio!=null &&
              this.messaggio.equals(other.getMessaggio()))) &&
            ((this.operazioniDML==null && other.getOperazioniDML()==null) || 
             (this.operazioniDML!=null &&
              java.util.Arrays.equals(this.operazioniDML, other.getOperazioniDML())));
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
        _hashCode += (isEsito() ? Boolean.TRUE : Boolean.FALSE).hashCode();
        if (getMessaggio() != null) {
            _hashCode += getMessaggio().hashCode();
        }
        if (getOperazioniDML() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getOperazioniDML());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getOperazioniDML(), i);
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
        new org.apache.axis.description.TypeDesc(EsitoInserisciGaraLotto.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://ws.simog.eldasoft.it/", "esitoInserisciGaraLotto"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("esito");
        elemField.setXmlName(new javax.xml.namespace.QName("", "esito"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("messaggio");
        elemField.setXmlName(new javax.xml.namespace.QName("", "messaggio"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("operazioniDML");
        elemField.setXmlName(new javax.xml.namespace.QName("", "operazioniDML"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://ws.simog.eldasoft.it/", "operazioneDMLType"));
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
