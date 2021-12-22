package it.eldasoft.sil.vigilanza.bl;

import it.eldasoft.gene.bl.GeneManager;
import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.db.datautils.DataColumn;
import it.eldasoft.gene.db.datautils.DataColumnContainer;
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.sil.vigilanza.beans.IncaricoProfessionaleType;
import it.eldasoft.sil.vigilanza.beans.RupType;
import it.eldasoft.sil.vigilanza.utils.UtilitySITAT;
import it.eldasoft.sil.vigilanza.ws.beans.ResponseType;

import java.sql.SQLException;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

public class RupManager {

private static Logger logger = Logger.getLogger(RupManager.class);
  
  private SqlManager sqlManager;

  private GeneManager geneManager;

  /**
   * @param sqlManager the sqlManager to set
   */
  public void setSqlManager(SqlManager sqlManager) {
    this.sqlManager = sqlManager;
  }
  
  /**
   * @param geneManager the geneManager to set
   */
  public void setGeneManager(GeneManager geneManager) {
    this.geneManager = geneManager;
  }
  
  /**
   * Gestione del RUP come utente e tecnico associato alla stazione appaltante.
   * 
   * @param result ResponseType
   * @param codFiscaleStazAppaltante Codice fiscale della S.A.
   * @param credenzialiUtente Oggetto Credenzili Utente
   * @param rupDaXml Oggetto RupType
   * @param isTecnicoRUP Flag per indicare se il tecnico e' il RUP o meno
   * @return Ritorna il campo chiave del tecnico (TECNI) associato all'utente che ha fatto
   *         accesso al WS e associato alla S.A. indicata nel XML.
   * @throws GestoreException GestoreException
   * @throws SQLException SQLException
   */
  public String getRupStazioneAppaltante(ResponseType result, String codFiscaleStazAppaltante,
  		Credenziale credenzialiUtente, RupType rupDaXml)
  				throws GestoreException, SQLException {
  	
  	String codiceChiaveRUP = "codiceChiaveRUP";
  	
  	if (UtilitySITAT.isUtenteAmministratore(credenzialiUtente)) {
  		if (rupDaXml != null) {

  			long numeroTecniciRup = this.geneManager.countOccorrenze("TECNI", " CGENTEI=? and UPPER(CFTEC)=? ",
            new Object[]{ credenzialiUtente.getStazioneAppaltante().getCodice(), rupDaXml.getCFTEC1().toUpperCase() });

  			if (numeroTecniciRup == 1) {
        	String pkTecni = (String) this.sqlManager.getObject(
              "select CODTEC from TECNI where UPPER(CFTEC)=? and CGENTEI=? ",
              new Object[]{ rupDaXml.getCFTEC1().toUpperCase(), credenzialiUtente.getStazioneAppaltante().getCodice() });
         	codiceChiaveRUP = pkTecni;
         	
        } else if (numeroTecniciRup == 0) {
       			
      		// Non esiste in TECNI il tecnico indicato nell'XML. Si procede con la creazione
  	      // dell'occorrenze e associazione dello stesso alla Stazione Appaltante e
  	      // all'utente di USRSYS che ha fatto accesso al WS.
  	      synchronized (codiceChiaveRUP) {
  	        
  	        String pkTecni = this.geneManager.calcolaCodificaAutomatica("TECNI", "CODTEC");
  	        codiceChiaveRUP = pkTecni;
  	        
  	        DataColumn codiceTecnico = new DataColumn("TECNI.CODTEC",
  	            new JdbcParametro(JdbcParametro.TIPO_TESTO, pkTecni));
  	        DataColumn uffint = new DataColumn("TECNI.CGENTEI", new JdbcParametro(
  	            JdbcParametro.TIPO_TESTO, credenzialiUtente.getStazioneAppaltante().getCodice()));
  	        DataColumn codiceFiscale = new DataColumn("TECNI.CFTEC", new JdbcParametro(
  	            JdbcParametro.TIPO_TESTO, rupDaXml.getCFTEC1().toUpperCase()));
  	        
 	        	DataColumnContainer dcc = new DataColumnContainer(
  	        			new DataColumn[] { codiceTecnico, uffint, codiceFiscale });
  	  
  	        // I campi facoltativi del tracciato vanno settati separatamente previo controllo
  	        if (rupDaXml.isSetGCITTECI()) {
  	          dcc.addColumn("TECNI.CITTEC", new JdbcParametro(JdbcParametro.TIPO_TESTO,
  	          		rupDaXml.getGCITTECI()));
  	        }
  	  
  	        String nomeTecnico = null;
  	        if (rupDaXml.isSetCOGTEI()) {
  	          dcc.addColumn("TECNI.COGTEI", new JdbcParametro(JdbcParametro.TIPO_TESTO,
  	          		rupDaXml.getCOGTEI()));
  	          nomeTecnico = rupDaXml.getCOGTEI();
  	        }
  	        if (rupDaXml.isSetNOMETEI()) {
  	          dcc.addColumn("TECNI.NOMETEI", new JdbcParametro(JdbcParametro.TIPO_TESTO,
  	          		rupDaXml.getNOMETEI()));
  	          if (StringUtils.isNotEmpty(nomeTecnico)) {
  	            nomeTecnico += " " + rupDaXml.getNOMETEI();
  	          } else {
  	            nomeTecnico = rupDaXml.getNOMETEI();
  	          }
  	        }
  	        if (StringUtils.isNotEmpty(nomeTecnico)) {
  	          dcc.addColumn("TECNI.NOMTEC", new JdbcParametro(JdbcParametro.TIPO_TESTO, nomeTecnico));
  	        }
  	        if (rupDaXml.isSetINDTEC1()) {
  	          dcc.addColumn("TECNI.INDTEC", JdbcParametro.TIPO_TESTO, rupDaXml.getINDTEC1());
  	        }
  	        if (rupDaXml.isSetNCITEC1()) {
  	          dcc.addColumn("TECNI.NCITEC", JdbcParametro.TIPO_TESTO, rupDaXml.getNCITEC1());
  	        }
  	        if (rupDaXml.isSetLOCTEC1()) {
  	          dcc.addColumn("TECNI.LOCTEC", JdbcParametro.TIPO_TESTO, rupDaXml.getLOCTEC1());
  	        }
  	        if (rupDaXml.isSetCAPTEC1()) {
  	          dcc.addColumn("TECNI.CAPTEC", JdbcParametro.TIPO_TESTO, rupDaXml.getCAPTEC1());
  	        }
  	        if (rupDaXml.isSetTELTEC1()) {
  	          dcc.addColumn("TECNI.TELTEC", JdbcParametro.TIPO_TESTO, rupDaXml.getTELTEC1());
  	        }
  	        if (rupDaXml.isSetFAXTEC1()) {
  	          dcc.addColumn("TECNI.FAXTEC", JdbcParametro.TIPO_TESTO, rupDaXml.getFAXTEC1());
  	        }
  	        if (rupDaXml.isSetGEMATECI()) {
  	          dcc.addColumn("TECNI.EMATEC", JdbcParametro.TIPO_TESTO, rupDaXml.getGEMATECI());
  	        }
  	        if (rupDaXml.isSetPROTEC()) {
  	          dcc.addColumn("TECNI.PROTEC", JdbcParametro.TIPO_TESTO, rupDaXml.getPROTEC());
  	        }
  	
  	        dcc.insert("TECNI", this.sqlManager);
  	      } // chiusura del synchronized

        } else {
        	// Caso che non si dovrebbe mai verificare. Piu' occorrenze in TECNI con stesso CFEIN, CGENTEI.

          StringBuilder strLog = new StringBuilder(credenzialiUtente.getPrefissoLogger());
          strLog.append(" Errore poco probabile: piu' occorrenze in TECNI con stesso CFEIN, CGENTEI (CFTEC=");
          strLog.append(rupDaXml.getCFTEC1());
          strLog.append(" - CGENTEI=");
          strLog.append(codFiscaleStazAppaltante);
          strLog.append(").");
          logger.error(strLog);

          result.setSuccess(false);
          result.setError("Impossibile determinare in modo univoco il RUP nella base dati di destinazione");
        }

  		} else {
  			// In questo caso non si valorizzeranno i campi W9GARA.RUP e W9LOTT.RUP
  			// perche' l'utente che ha inviato i dati e' un utente amministratore
  			// e fra i dati mancano quelli relativi al RUP della gara/del lotto.
  			codiceChiaveRUP = null;
  		}
  	} else {
  		
  		if (rupDaXml != null) {

  			long numeroTecniciRup = this.geneManager.countOccorrenze("TECNI", " CGENTEI=? and UPPER(CFTEC)=? and SYSCON=?",
            new Object[]{ credenzialiUtente.getStazioneAppaltante().getCodice(), rupDaXml.getCFTEC1().toUpperCase(),
      					new Long(credenzialiUtente.getAccount().getIdAccount()) });
     
  			long numeroTecniciPerStazAppCftec = this.geneManager.countOccorrenze("TECNI", " CGENTEI=? and UPPER(CFTEC)=?",
            new Object[]{ credenzialiUtente.getStazioneAppaltante().getCodice(), rupDaXml.getCFTEC1().toUpperCase() });
  			
        long numeroTecniciPerStazAppSysCon = this.geneManager.countOccorrenze("TECNI", " CGENTEI=? and SYSCON=? ",
            new Object[]{ credenzialiUtente.getStazioneAppaltante().getCodice(),
        				new Long(credenzialiUtente.getAccount().getIdAccount()) });
  			
        if (numeroTecniciRup == 1) {
        	String pkTecni = (String) this.sqlManager.getObject(
              "select CODTEC from TECNI where UPPER(CFTEC)=? and CGENTEI=? and SYSCON=?",
              new Object[]{ rupDaXml.getCFTEC1().toUpperCase(), credenzialiUtente.getStazioneAppaltante().getCodice(),
              		new Long(credenzialiUtente.getAccount().getIdAccount()) });
         	codiceChiaveRUP = pkTecni;
         	
        } else if (numeroTecniciRup == 0) {

        	if (numeroTecniciPerStazAppCftec == 0 && numeroTecniciPerStazAppSysCon == 0) {
       			
        		// Non esiste in TECNI il tecnico indicato nell'XML. Si procede con la creazione
    	      // dell'occorrenze e associazione dello stesso alla Stazione Appaltante e
    	      // all'utente di USRSYS che ha fatto accesso al WS.
    	      synchronized (codiceChiaveRUP) {
    	        
    	        String pkTecni = this.geneManager.calcolaCodificaAutomatica("TECNI", "CODTEC");
    	        codiceChiaveRUP = pkTecni;
    	        
    	        DataColumn codiceTecnico = new DataColumn("TECNI.CODTEC",
    	            new JdbcParametro(JdbcParametro.TIPO_TESTO, pkTecni));
    	        DataColumn uffint = new DataColumn("TECNI.CGENTEI", new JdbcParametro(
    	            JdbcParametro.TIPO_TESTO, credenzialiUtente.getStazioneAppaltante().getCodice()));
    	        DataColumn codiceFiscale = new DataColumn("TECNI.CFTEC", new JdbcParametro(
    	            JdbcParametro.TIPO_TESTO, rupDaXml.getCFTEC1().toUpperCase()));
   	          DataColumn syscon = new DataColumn("TECNI.SYSCON", new JdbcParametro(
    	              JdbcParametro.TIPO_NUMERICO, new Long(credenzialiUtente.getAccount().getIdAccount())));

   	          DataColumnContainer dcc = new DataColumnContainer(
   	          		new DataColumn[] {codiceTecnico, uffint, codiceFiscale, syscon });
    	  
    	        // I campi facoltativi del tracciato vanno settati separatamente previo controllo
    	        if (rupDaXml.isSetGCITTECI()) {
    	          dcc.addColumn("TECNI.CITTEC", new JdbcParametro(JdbcParametro.TIPO_TESTO,
    	          		rupDaXml.getGCITTECI()));
    	        }
    	  
    	        String nomeTecnico = null;
    	        if (rupDaXml.isSetCOGTEI()) {
    	          dcc.addColumn("TECNI.COGTEI", new JdbcParametro(JdbcParametro.TIPO_TESTO,
    	          		rupDaXml.getCOGTEI()));
    	          nomeTecnico = rupDaXml.getCOGTEI();
    	        }
    	        if (rupDaXml.isSetNOMETEI()) {
    	          dcc.addColumn("TECNI.NOMETEI", new JdbcParametro(JdbcParametro.TIPO_TESTO,
    	          		rupDaXml.getNOMETEI()));
    	          if (StringUtils.isNotEmpty(nomeTecnico)) {
    	            nomeTecnico += " " + rupDaXml.getNOMETEI();
    	          } else {
    	            nomeTecnico = rupDaXml.getNOMETEI();
    	          }
    	        }
    	        if (StringUtils.isNotEmpty(nomeTecnico)) {
    	          dcc.addColumn("TECNI.NOMTEC", new JdbcParametro(JdbcParametro.TIPO_TESTO, nomeTecnico));
    	        }
    	        if (rupDaXml.isSetINDTEC1()) {
    	          dcc.addColumn("TECNI.INDTEC", JdbcParametro.TIPO_TESTO, rupDaXml.getINDTEC1());
    	        }
    	        if (rupDaXml.isSetNCITEC1()) {
    	          dcc.addColumn("TECNI.NCITEC", JdbcParametro.TIPO_TESTO, rupDaXml.getNCITEC1());
    	        }
    	        if (rupDaXml.isSetLOCTEC1()) {
    	          dcc.addColumn("TECNI.LOCTEC", JdbcParametro.TIPO_TESTO, rupDaXml.getLOCTEC1());
    	        }
    	        if (rupDaXml.isSetCAPTEC1()) {
    	          dcc.addColumn("TECNI.CAPTEC", JdbcParametro.TIPO_TESTO, rupDaXml.getCAPTEC1());
    	        }
    	        if (rupDaXml.isSetTELTEC1()) {
    	          dcc.addColumn("TECNI.TELTEC", JdbcParametro.TIPO_TESTO, rupDaXml.getTELTEC1());
    	        }
    	        if (rupDaXml.isSetFAXTEC1()) {
    	          dcc.addColumn("TECNI.FAXTEC", JdbcParametro.TIPO_TESTO, rupDaXml.getFAXTEC1());
    	        }
    	        if (rupDaXml.isSetGEMATECI()) {
    	          dcc.addColumn("TECNI.EMATEC", JdbcParametro.TIPO_TESTO, rupDaXml.getGEMATECI());
    	        }
    	        if (rupDaXml.isSetPROTEC()) {
    	          dcc.addColumn("TECNI.PROTEC", JdbcParametro.TIPO_TESTO, rupDaXml.getPROTEC());
    	        }
    	
    	        dcc.insert("TECNI", this.sqlManager);
    	      } // chiusura del synchronized
        	} else {
    	    	// La stazione appaltante ha almeno un tecnico, ma tale tecnico non coincide con
        		// il RUP che si e' autenticato al WS. Si da' errore per garantire l'unicita' del
        		// RUP per ogni stazione appaltante. 
    	    	
    	    	StringBuilder strLog = new StringBuilder(credenzialiUtente.getPrefissoLogger());
    	      strLog.append(" Errore: la stazione appaltante ha almeno un tecnico, ma tale "
    	      		+ " tecnico non coincide con il RUP che si e' autenticato al WS. (CFTEC=");
    	      strLog.append(rupDaXml.getCFTEC1());
    	      strLog.append(" - CFEIN=");
    	      strLog.append(codFiscaleStazAppaltante);
    	      strLog.append("). Questo controllo mantiene unico il RUP per ciascuna S.A.");
    	      logger.error(strLog);

    	      result.setSuccess(false);
    	      result.setError("Le credenziali fornite non coincidono con quelle del RUP indicato");
        	}
        } else {
        	// Caso che non si dovrebbe mai verificare. Piu' occorrenze in TECNI con stesso CFEIN, CGENTEI e SYSCON.

          StringBuilder strLog = new StringBuilder(credenzialiUtente.getPrefissoLogger());
          strLog.append(" Errore poco probabile: piu' occorrenze in TECNI con stesso CFEIN, CGENTEI e SYSCON (CFTEC=");
          strLog.append(rupDaXml.getCFTEC1());
          strLog.append(" - CGENTEI=");
          strLog.append(codFiscaleStazAppaltante);
          strLog.append(" - SYSCON=");
          strLog.append(credenzialiUtente.getAccount().getIdAccount());
          strLog.append(").");
          logger.error(strLog);

          result.setSuccess(false);
          result.setError("Impossibile determinare in modo univoco il RUP nella base dati di destinazione");
        }

  		} else {
  			// Errore bloccante: l'utente che ha inviato i dati non e' un utente amministratore
  			// e fra i dati mancano quelli relativi al RUP della gara/del lotto. Questo utente
  			// non ha i diritti per inviare dati.
  			
  			StringBuilder strLog = new StringBuilder(credenzialiUtente.getPrefissoLogger());
        strLog.append(" Errore: l'utente che ha fatto login non e' un utente amministratore e fra i dati mancano quelli relativi al RUP della gara/lotto.");
        strLog.append(" Dettagli dell'utente: SYSCON=");
        strLog.append(credenzialiUtente.getAccount().getIdAccount());
        strLog.append(" LOGIN=");
        strLog.append(credenzialiUtente.getAccount().getLogin());
        strLog.append(". Codice stazione appaltante: ");
        strLog.append(codFiscaleStazAppaltante);
        strLog.append(").");
        logger.error(strLog);

        result.setSuccess(false);
        result.setError("Impossibile determinare in modo univoco il RUP nella base dati di destinazione");
        //Le credenziali fornite non coincidono con quelle del RUP indicato
  			
  		}
  	}
    
    return codiceChiaveRUP;
  }

  public String getIncaricoProfessionale(String codiceStazAppaltante, 
  		IncaricoProfessionaleType incaricoProfDaXml) throws GestoreException, SQLException {

  	String codiceChiaveTecnico = "";
  	
  	long numeroTecnicoByCodFisStazApp = this.geneManager.countOccorrenze("TECNI", " CGENTEI=? and UPPER(CFTEC)=?",
  		  new Object[]{ codiceStazAppaltante, incaricoProfDaXml.getCFTEC1().toUpperCase() });
  	
  	if (numeroTecnicoByCodFisStazApp == 1) {
  		String pkTecni = (String) this.sqlManager.getObject(
          "select CODTEC from TECNI where CGENTEI=? and UPPER(CFTEC)=?",
          new Object[]{ codiceStazAppaltante, incaricoProfDaXml.getCFTEC1().toUpperCase()});
     	codiceChiaveTecnico = pkTecni;
  	} else {
  		// Non esiste in TECNI il tecnico indicato nell'XML. Si procede con la creazione
	    // dell'occorrenze e associazione dello stesso alla Stazione Appaltante e
	    // all'utente di USRSYS che ha fatto accesso al WS.
	    synchronized (codiceChiaveTecnico) {
	        
	      String pkTecni = this.geneManager.calcolaCodificaAutomatica("TECNI", "CODTEC");
	      codiceChiaveTecnico = pkTecni;
	        
	      DataColumn codiceTecnico = new DataColumn("TECNI.CODTEC",
	          new JdbcParametro(JdbcParametro.TIPO_TESTO, pkTecni));
	      DataColumn uffint = new DataColumn("TECNI.CGENTEI", new JdbcParametro(
	          JdbcParametro.TIPO_TESTO, codiceStazAppaltante));
	      DataColumn codiceFiscale = new DataColumn("TECNI.CFTEC", new JdbcParametro(
	          JdbcParametro.TIPO_TESTO, incaricoProfDaXml.getCFTEC1().toUpperCase()));

	      DataColumnContainer dcc = new DataColumnContainer(new DataColumn[] { codiceTecnico, uffint, codiceFiscale });
	  
        // I campi facoltativi del tracciato vanno settati separatamente previo controllo
        if (incaricoProfDaXml.isSetGCITTECI()) {
          dcc.addColumn("TECNI.CITTEC", new JdbcParametro(JdbcParametro.TIPO_TESTO, incaricoProfDaXml.getGCITTECI()));
        }
  
        String nomeTecnico = null;
        if (incaricoProfDaXml.isSetCOGTEI()) {
          dcc.addColumn("TECNI.COGTEI", new JdbcParametro(JdbcParametro.TIPO_TESTO, incaricoProfDaXml.getCOGTEI()));
          nomeTecnico = incaricoProfDaXml.getCOGTEI();
        }
        if (incaricoProfDaXml.isSetNOMETEI()) {
          dcc.addColumn("TECNI.NOMETEI", new JdbcParametro(JdbcParametro.TIPO_TESTO, incaricoProfDaXml.getNOMETEI()));
          if (StringUtils.isNotEmpty(nomeTecnico)) {
            nomeTecnico += " " + incaricoProfDaXml.getNOMETEI();
          } else {
            nomeTecnico = incaricoProfDaXml.getNOMETEI();
          }
        }
        if (StringUtils.isNotEmpty(nomeTecnico)) {
          dcc.addColumn("TECNI.NOMTEC", new JdbcParametro(JdbcParametro.TIPO_TESTO, nomeTecnico));
        }
        if (incaricoProfDaXml.isSetINDTEC1()) {
          dcc.addColumn("TECNI.INDTEC", JdbcParametro.TIPO_TESTO, incaricoProfDaXml.getINDTEC1());
        }
        if (incaricoProfDaXml.isSetNCITEC1()) {
          dcc.addColumn("TECNI.NCITEC", JdbcParametro.TIPO_TESTO, incaricoProfDaXml.getNCITEC1());
        }
        if (incaricoProfDaXml.isSetLOCTEC1()) {
          dcc.addColumn("TECNI.LOCTEC", JdbcParametro.TIPO_TESTO, incaricoProfDaXml.getLOCTEC1());
        }
        if (incaricoProfDaXml.isSetCAPTEC1()) {
          dcc.addColumn("TECNI.CAPTEC", JdbcParametro.TIPO_TESTO, incaricoProfDaXml.getCAPTEC1());
        }
        if (incaricoProfDaXml.isSetTELTEC1()) {
          dcc.addColumn("TECNI.TELTEC", JdbcParametro.TIPO_TESTO, incaricoProfDaXml.getTELTEC1());
        }
        if (incaricoProfDaXml.isSetFAXTEC1()) {
          dcc.addColumn("TECNI.FAXTEC", JdbcParametro.TIPO_TESTO, incaricoProfDaXml.getFAXTEC1());
        }
        if (incaricoProfDaXml.isSetGEMATECI()) {
          dcc.addColumn("TECNI.EMATEC", JdbcParametro.TIPO_TESTO, incaricoProfDaXml.getGEMATECI());
        }
        if (incaricoProfDaXml.isSetPROTEC()) {
          dcc.addColumn("TECNI.PROTEC", JdbcParametro.TIPO_TESTO, incaricoProfDaXml.getPROTEC());
        }

        dcc.insert("TECNI", this.sqlManager);
      } // chiusura del synchronized
  	}
  	
  	if (StringUtils.isNotEmpty(codiceChiaveTecnico)) {
  		return codiceChiaveTecnico;
  	} else {
  		return null;
  	}
  }
  
}
