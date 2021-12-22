/**
 * EsitoVerificaCIG.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package it.eldasoft.simog.ws;

public class EsitoVerificaCIG  implements java.io.Serializable {
    private boolean esito;

    private java.lang.String oggettogara;

    private java.lang.String numerogara;

    private java.math.BigDecimal importogara;

    private java.lang.String denominazionerup;

    private java.lang.String codicefiscalerup;

    public EsitoVerificaCIG() {
    }

    public EsitoVerificaCIG(
           boolean esito,
           java.lang.String oggettogara,
           java.lang.String numerogara,
           java.math.BigDecimal importogara,
           java.lang.String denominazionerup,
           java.lang.String codicefiscalerup) {
           this.esito = esito;
           this.oggettogara = oggettogara;
           this.numerogara = numerogara;
           this.importogara = importogara;
           this.denominazionerup = denominazionerup;
           this.codicefiscalerup = codicefiscalerup;
    }


    /**
     * Gets the esito value for this EsitoVerificaCIG.
     * 
     * @return esito
     */
    public boolean isEsito() {
        return esito;
    }


    /**
     * Sets the esito value for this EsitoVerificaCIG.
     * 
     * @param esito
     */
    public void setEsito(boolean esito) {
        this.esito = esito;
    }


    /**
     * Gets the oggettogara value for this EsitoVerificaCIG.
     * 
     * @return oggettogara
     */
    public java.lang.String getOggettogara() {
        return oggettogara;
    }


    /**
     * Sets the oggettogara value for this EsitoVerificaCIG.
     * 
     * @param oggettogara
     */
    public void setOggettogara(java.lang.String oggettogara) {
        this.oggettogara = oggettogara;
    }


    /**
     * Gets the numerogara value for this EsitoVerificaCIG.
     * 
     * @return numerogara
     */
    public java.lang.String getNumerogara() {
        return numerogara;
    }


    /**
     * Sets the numerogara value for this EsitoVerificaCIG.
     * 
     * @param numerogara
     */
    public void setNumerogara(java.lang.String numerogara) {
        this.numerogara = numerogara;
    }


    /**
     * Gets the importogara value for this EsitoVerificaCIG.
     * 
     * @return importogara
     */
    public java.math.BigDecimal getImportogara() {
        return importogara;
    }


    /**
     * Sets the importogara value for this EsitoVerificaCIG.
     * 
     * @param importogara
     */
    public void setImportogara(java.math.BigDecimal importogara) {
        this.importogara = importogara;
    }


    /**
     * Gets the denominazionerup value for this EsitoVerificaCIG.
     * 
     * @return denominazionerup
     */
    public java.lang.String getDenominazionerup() {
        return denominazionerup;
    }


    /**
     * Sets the denominazionerup value for this EsitoVerificaCIG.
     * 
     * @param denominazionerup
     */
    public void setDenominazionerup(java.lang.String denominazionerup) {
        this.denominazionerup = denominazionerup;
    }


    /**
     * Gets the codicefiscalerup value for this EsitoVerificaCIG.
     * 
     * @return codicefiscalerup
     */
    public java.lang.String getCodicefiscalerup() {
        return codicefiscalerup;
    }


    /**
     * Sets the codicefiscalerup value for this EsitoVerificaCIG.
     * 
     * @param codicefiscalerup
     */
    public void setCodicefiscalerup(java.lang.String codicefiscalerup) {
        this.codicefiscalerup = codicefiscalerup;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof EsitoVerificaCIG)) return false;
        EsitoVerificaCIG other = (EsitoVerificaCIG) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            this.esito == other.isEsito() &&
            ((this.oggettogara==null && other.getOggettogara()==null) || 
             (this.oggettogara!=null &&
              this.oggettogara.equals(other.getOggettogara()))) &&
            ((this.numerogara==null && other.getNumerogara()==null) || 
             (this.numerogara!=null &&
              this.numerogara.equals(other.getNumerogara()))) &&
            ((this.importogara==null && other.getImportogara()==null) || 
             (this.importogara!=null &&
              this.importogara.equals(other.getImportogara()))) &&
            ((this.denominazionerup==null && other.getDenominazionerup()==null) || 
             (this.denominazionerup!=null &&
              this.denominazionerup.equals(other.getDenominazionerup()))) &&
            ((this.codicefiscalerup==null && other.getCodicefiscalerup()==null) || 
             (this.codicefiscalerup!=null &&
              this.codicefiscalerup.equals(other.getCodicefiscalerup())));
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
        if (getOggettogara() != null) {
            _hashCode += getOggettogara().hashCode();
        }
        if (getNumerogara() != null) {
            _hashCode += getNumerogara().hashCode();
        }
        if (getImportogara() != null) {
            _hashCode += getImportogara().hashCode();
        }
        if (getDenominazionerup() != null) {
            _hashCode += getDenominazionerup().hashCode();
        }
        if (getCodicefiscalerup() != null) {
            _hashCode += getCodicefiscalerup().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(EsitoVerificaCIG.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://ws.simog.eldasoft.it/", "esitoVerificaCIG"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("esito");
        elemField.setXmlName(new javax.xml.namespace.QName("", "esito"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("oggettogara");
        elemField.setXmlName(new javax.xml.namespace.QName("", "oggettogara"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("numerogara");
        elemField.setXmlName(new javax.xml.namespace.QName("", "numerogara"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("importogara");
        elemField.setXmlName(new javax.xml.namespace.QName("", "importogara"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "decimal"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("denominazionerup");
        elemField.setXmlName(new javax.xml.namespace.QName("", "denominazionerup"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("codicefiscalerup");
        elemField.setXmlName(new javax.xml.namespace.QName("", "codicefiscalerup"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
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
