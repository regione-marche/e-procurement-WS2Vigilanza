/**
 * CodiciCup.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package it.avlp.simog.beans;

public class CodiciCup  implements java.io.Serializable {
    private java.lang.String CUP;

    private java.lang.String DATI_DIPE;

    private java.lang.String ID_RICHIESTA;

    private java.lang.String OK_UTENTE;

    private java.lang.String VALIDO;

    public CodiciCup() {
    }

    public CodiciCup(
           java.lang.String CUP,
           java.lang.String DATI_DIPE,
           java.lang.String ID_RICHIESTA,
           java.lang.String OK_UTENTE,
           java.lang.String VALIDO) {
           this.CUP = CUP;
           this.DATI_DIPE = DATI_DIPE;
           this.ID_RICHIESTA = ID_RICHIESTA;
           this.OK_UTENTE = OK_UTENTE;
           this.VALIDO = VALIDO;
    }


    /**
     * Gets the CUP value for this CodiciCup.
     * 
     * @return CUP
     */
    public java.lang.String getCUP() {
        return CUP;
    }


    /**
     * Sets the CUP value for this CodiciCup.
     * 
     * @param CUP
     */
    public void setCUP(java.lang.String CUP) {
        this.CUP = CUP;
    }


    /**
     * Gets the DATI_DIPE value for this CodiciCup.
     * 
     * @return DATI_DIPE
     */
    public java.lang.String getDATI_DIPE() {
        return DATI_DIPE;
    }


    /**
     * Sets the DATI_DIPE value for this CodiciCup.
     * 
     * @param DATI_DIPE
     */
    public void setDATI_DIPE(java.lang.String DATI_DIPE) {
        this.DATI_DIPE = DATI_DIPE;
    }


    /**
     * Gets the ID_RICHIESTA value for this CodiciCup.
     * 
     * @return ID_RICHIESTA
     */
    public java.lang.String getID_RICHIESTA() {
        return ID_RICHIESTA;
    }


    /**
     * Sets the ID_RICHIESTA value for this CodiciCup.
     * 
     * @param ID_RICHIESTA
     */
    public void setID_RICHIESTA(java.lang.String ID_RICHIESTA) {
        this.ID_RICHIESTA = ID_RICHIESTA;
    }


    /**
     * Gets the OK_UTENTE value for this CodiciCup.
     * 
     * @return OK_UTENTE
     */
    public java.lang.String getOK_UTENTE() {
        return OK_UTENTE;
    }


    /**
     * Sets the OK_UTENTE value for this CodiciCup.
     * 
     * @param OK_UTENTE
     */
    public void setOK_UTENTE(java.lang.String OK_UTENTE) {
        this.OK_UTENTE = OK_UTENTE;
    }


    /**
     * Gets the VALIDO value for this CodiciCup.
     * 
     * @return VALIDO
     */
    public java.lang.String getVALIDO() {
        return VALIDO;
    }


    /**
     * Sets the VALIDO value for this CodiciCup.
     * 
     * @param VALIDO
     */
    public void setVALIDO(java.lang.String VALIDO) {
        this.VALIDO = VALIDO;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof CodiciCup)) return false;
        CodiciCup other = (CodiciCup) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.CUP==null && other.getCUP()==null) || 
             (this.CUP!=null &&
              this.CUP.equals(other.getCUP()))) &&
            ((this.DATI_DIPE==null && other.getDATI_DIPE()==null) || 
             (this.DATI_DIPE!=null &&
              this.DATI_DIPE.equals(other.getDATI_DIPE()))) &&
            ((this.ID_RICHIESTA==null && other.getID_RICHIESTA()==null) || 
             (this.ID_RICHIESTA!=null &&
              this.ID_RICHIESTA.equals(other.getID_RICHIESTA()))) &&
            ((this.OK_UTENTE==null && other.getOK_UTENTE()==null) || 
             (this.OK_UTENTE!=null &&
              this.OK_UTENTE.equals(other.getOK_UTENTE()))) &&
            ((this.VALIDO==null && other.getVALIDO()==null) || 
             (this.VALIDO!=null &&
              this.VALIDO.equals(other.getVALIDO())));
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
        if (getCUP() != null) {
            _hashCode += getCUP().hashCode();
        }
        if (getDATI_DIPE() != null) {
            _hashCode += getDATI_DIPE().hashCode();
        }
        if (getID_RICHIESTA() != null) {
            _hashCode += getID_RICHIESTA().hashCode();
        }
        if (getOK_UTENTE() != null) {
            _hashCode += getOK_UTENTE().hashCode();
        }
        if (getVALIDO() != null) {
            _hashCode += getVALIDO().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(CodiciCup.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://beans.simog.avlp.it", "CodiciCup"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("CUP");
        elemField.setXmlName(new javax.xml.namespace.QName("http://beans.simog.avlp.it", "CUP"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("DATI_DIPE");
        elemField.setXmlName(new javax.xml.namespace.QName("http://beans.simog.avlp.it", "DATI_DIPE"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("ID_RICHIESTA");
        elemField.setXmlName(new javax.xml.namespace.QName("http://beans.simog.avlp.it", "ID_RICHIESTA"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("OK_UTENTE");
        elemField.setXmlName(new javax.xml.namespace.QName("http://beans.simog.avlp.it", "OK_UTENTE"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("VALIDO");
        elemField.setXmlName(new javax.xml.namespace.QName("http://beans.simog.avlp.it", "VALIDO"));
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
