/*
 * Created on 16-apr-2013
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.vigilanza.utils;

import it.eldasoft.gene.bl.MetadatiManager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.utils.properties.ConfigManager;

import java.io.InputStream;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionServlet;
import org.apache.struts.action.PlugIn;
import org.apache.struts.config.ModuleConfig;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 * Implementazione dell'interfaccia org.apache.struts.action.PlugIn.
 * 
 * @author Luca.Giacomazzo
 */
public class WsW9StartupPluginBase implements PlugIn {

  /** Logger Log4J di classe */
  static Logger               logger                              = Logger.getLogger(WsW9StartupPluginBase.class);

  public void init(ActionServlet servlet, ModuleConfig config) throws ServletException {

    ServletContext context = servlet.getServletContext();
    
    // Caricamento incondizionato del file non cifrato con le properties necessarie al WS.
    InputStream streamNonCifrato = context.getResourceAsStream(
        CostantiGenerali.DEFAULT_PATH_CARTELLA_PROPERTIES
        + "application.properties");

    if (streamNonCifrato != null) {
      ConfigManager.reload(streamNonCifrato);
    
    } else {
      // il file application.properties non esiste
      logger.fatal("Problemi di accesso al file application.properties");
    }
    
    // Caricamento dei metadati 
    ApplicationContext ctx = WebApplicationContextUtils.getRequiredWebApplicationContext(servlet.getServletContext());
    MetadatiManager mm = (MetadatiManager) ctx.getBean("metadatiManager");
    mm.carica();
  }

  public void destroy() {
  }
  
}
