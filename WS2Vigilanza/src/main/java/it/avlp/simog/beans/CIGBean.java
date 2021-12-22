/**
 * CIGBean.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package it.avlp.simog.beans;

public class CIGBean  implements java.io.Serializable {
    private java.lang.String applicazione;

    private java.lang.String cfAmministrazione;

    private java.lang.String cfStazione;

    private java.lang.String cfUtente;

    private java.lang.String cig;

    private int cigCicle;

    private java.lang.String cigKKK;

    public CIGBean() {
    }

    public CIGBean(
           java.lang.String applicazione,
           java.lang.String cfAmministrazione,
           java.lang.String cfStazione,
           java.lang.String cfUtente,
           java.lang.String cig,
           int cigCicle,
           java.lang.String cigKKK) {
           this.applicazione = applicazione;
           this.cfAmministrazione = cfAmministrazione;
           this.cfStazione = cfStazione;
           this.cfUtente = cfUtente;
           this.cig = cig;
           this.cigCicle = cigCicle;
           this.cigKKK = cigKKK;
    }


    /**
     * Gets the applicazione value for this CIGBean.
     * 
     * @return applicazione
     */
    public java.lang.String getApplicazione() {
        return applicazione;
    }


    /**
     * Sets the applicazione value for this CIGBean.
     * 
     * @param applicazione
     */
    public void setApplicazione(java.lang.String applicazione) {
        this.applicazione = applicazione;
    }


    /**
     * Gets the cfAmministrazione value for this CIGBean.
     * 
     * @return cfAmministrazione
     */
    public java.lang.String getCfAmministrazione() {
        return cfAmministrazione;
    }


    /**
     * Sets the cfAmministrazione value for this CIGBean.
     * 
     * @param cfAmministrazione
     */
    public void setCfAmministrazione(java.lang.String cfAmministrazione) {
        this.cfAmministrazione = cfAmministrazione;
    }


    /**
     * Gets the cfStazione value for this CIGBean.
     * 
     * @return cfStazione
     */
    public java.lang.String getCfStazione() {
        return cfStazione;
    }


    /**
     * Sets the cfStazione value for this CIGBean.
     * 
     * @param cfStazione
     */
    public void setCfStazione(java.lang.String cfStazione) {
        this.cfStazione = cfStazione;
    }


    /**
     * Gets the cfUtente value for this CIGBean.
     * 
     * @return cfUtente
     */
    public java.lang.String getCfUtente() {
        return cfUtente;
    }


    /**
     * Sets the cfUtente value for this CIGBean.
     * 
     * @param cfUtente
     */
    public void setCfUtente(java.lang.String cfUtente) {
        this.cfUtente = cfUtente;
    }


    /**
     * Gets the cig value for this CIGBean.
     * 
     * @return cig
     */
    public java.lang.String getCig() {
        return cig;
    }


    /**
     * Sets the cig value for this CIGBean.
     * 
     * @param cig
     */
    public void setCig(java.lang.String cig) {
        this.cig = cig;
    }


    /**
     * Gets the cigCicle value for this CIGBean.
     * 
     * @return cigCicle
     */
    public int getCigCicle() {
        return cigCicle;
    }


    /**
     * Sets the cigCicle value for this CIGBean.
     * 
     * @param cigCicle
     */
    public void setCigCicle(int cigCicle) {
        this.cigCicle = cigCicle;
    }


    /**
     * Gets the cigKKK value for this CIGBean.
     * 
     * @return cigKKK
     */
    public java.lang.String getCigKKK() {
        return cigKKK;
    }


    /**
     * Sets the cigKKK value for this CIGBean.
     * 
     * @param cigKKK
     */
    public void setCigKKK(java.lang.String cigKKK) {
        this.cigKKK = cigKKK;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof CIGBean)) return false;
        CIGBean other = (CIGBean) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.applicazione==null && other.getApplicazione()==null) || 
             (this.applicazione!=null &&
              this.applicazione.equals(other.getApplicazione()))) &&
            ((this.cfAmministrazione==null && other.getCfAmministrazione()==null) || 
             (this.cfAmministrazione!=null &&
              this.cfAmministrazione.equals(other.getCfAmministrazione()))) &&
            ((this.cfStazione==null && other.getCfStazione()==null) || 
             (this.cfStazione!=null &&
              this.cfStazione.equals(other.getCfStazione()))) &&
            ((this.cfUtente==null && other.getCfUtente()==null) || 
             (this.cfUtente!=null &&
              this.cfUtente.equals(other.getCfUtente()))) &&
            ((this.cig==null && other.getCig()==null) || 
             (this.cig!=null &&
              this.cig.equals(other.getCig()))) &&
            this.cigCicle == other.getCigCicle() &&
            ((this.cigKKK==null && other.getCigKKK()==null) || 
             (this.cigKKK!=null &&
              this.cigKKK.equals(other.getCigKKK())));
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
        if (getApplicazione() != null) {
            _hashCode += getApplicazione().hashCode();
        }
        if (getCfAmministrazione() != null) {
            _hashCode += getCfAmministrazione().hashCode();
        }
        if (getCfStazione() != null) {
            _hashCode += getCfStazione().hashCode();
        }
        if (getCfUtente() != null) {
            _hashCode += getCfUtente().hashCode();
        }
        if (getCig() != null) {
            _hashCode += getCig().hashCode();
        }
        _hashCode += getCigCicle();
        if (getCigKKK() != null) {
            _hashCode += getCigKKK().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(CIGBean.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://beans.simog.avlp.it", "CIGBean"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("applicazione");
        elemField.setXmlName(new javax.xml.namespace.QName("http://beans.simog.avlp.it", "applicazione"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("cfAmministrazione");
        elemField.setXmlName(new javax.xml.namespace.QName("http://beans.simog.avlp.it", "cfAmministrazione"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("cfStazione");
        elemField.setXmlName(new javax.xml.namespace.QName("http://beans.simog.avlp.it", "cfStazione"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("cfUtente");
        elemField.setXmlName(new javax.xml.namespace.QName("http://beans.simog.avlp.it", "cfUtente"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("cig");
        elemField.setXmlName(new javax.xml.namespace.QName("http://beans.simog.avlp.it", "cig"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("cigCicle");
        elemField.setXmlName(new javax.xml.namespace.QName("http://beans.simog.avlp.it", "cigCicle"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("cigKKK");
        elemField.setXmlName(new javax.xml.namespace.QName("http://beans.simog.avlp.it", "cigKKK"));
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
