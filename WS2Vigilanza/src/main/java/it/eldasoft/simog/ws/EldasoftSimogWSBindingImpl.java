/**
 * EldasoftSimogWSBindingImpl.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package it.eldasoft.simog.ws;

import it.eldasoft.sil.w3.bl.EldasoftSIMOGWSFacade;
import it.eldasoft.utils.spring.SpringAppContext;

import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

public class EldasoftSimogWSBindingImpl implements it.eldasoft.simog.ws.EldasoftSimogWS {

  public it.eldasoft.simog.ws.EsitoInserisciGaraLotto inserisciGaraLotto(java.lang.String login, java.lang.String password,
      java.lang.String datiGaraLotto) throws java.rmi.RemoteException {
    ApplicationContext ctx = WebApplicationContextUtils.getWebApplicationContext(SpringAppContext.getServletContext());
    EldasoftSIMOGWSFacade eldasoftSIMOGWSFacade = (EldasoftSIMOGWSFacade) ctx.getBean("eldasoftSIMOGWSFacade");
    return eldasoftSIMOGWSFacade.inserisciGaraLotto(login, password, datiGaraLotto);
  }

  public it.eldasoft.simog.ws.EsitoConsultaIDGARA consultaIDGARA(java.lang.String uuid) throws java.rmi.RemoteException {
    ApplicationContext ctx = WebApplicationContextUtils.getWebApplicationContext(SpringAppContext.getServletContext());
    EldasoftSIMOGWSFacade eldasoftSIMOGWSFacade = (EldasoftSIMOGWSFacade) ctx.getBean("eldasoftSIMOGWSFacade");
    return eldasoftSIMOGWSFacade.consultaIDGARA(uuid);
  }

  public it.eldasoft.simog.ws.EsitoConsultaCIG consultaCIG(java.lang.String uuid) throws java.rmi.RemoteException {
    ApplicationContext ctx = WebApplicationContextUtils.getWebApplicationContext(SpringAppContext.getServletContext());
    EldasoftSIMOGWSFacade eldasoftSIMOGWSFacade = (EldasoftSIMOGWSFacade) ctx.getBean("eldasoftSIMOGWSFacade");
    return eldasoftSIMOGWSFacade.consultaCIG(uuid);
  }

  public it.eldasoft.simog.ws.EsitoVerificaCIG verificaCIG(java.lang.String cig) throws java.rmi.RemoteException {
    ApplicationContext ctx = WebApplicationContextUtils.getWebApplicationContext(SpringAppContext.getServletContext());
    EldasoftSIMOGWSFacade eldasoftSIMOGWSFacade = (EldasoftSIMOGWSFacade) ctx.getBean("eldasoftSIMOGWSFacade");
    return eldasoftSIMOGWSFacade.verificaCIG(cig);
  }

  public it.eldasoft.simog.ws.EsitoInserisciSmartCIG inserisciSmartCIG(java.lang.String login, java.lang.String password, java.lang.String datiSmartCIG) throws java.rmi.RemoteException{
	  ApplicationContext ctx = WebApplicationContextUtils.getWebApplicationContext(SpringAppContext.getServletContext());
	  EldasoftSIMOGWSFacade eldasoftSIMOGWSFacade = (EldasoftSIMOGWSFacade) ctx.getBean("eldasoftSIMOGWSFacade");
	  return eldasoftSIMOGWSFacade.inserisciSmartCIG(login, password, datiSmartCIG);
  }
  
}
