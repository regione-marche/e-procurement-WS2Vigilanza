package it.eldasoft.sil.w3.bl;

import java.util.List;

import it.eldasoft.simog.ws.EsitoConsultaCIG;
import it.eldasoft.simog.ws.EsitoConsultaIDGARA;
import it.eldasoft.simog.ws.EsitoInserisciGaraLotto;
import it.eldasoft.simog.ws.EsitoInserisciSmartCIG;
import it.eldasoft.simog.ws.EsitoVerificaCIG;
import it.eldasoft.simog.ws.InformazioneType;
import it.eldasoft.simog.ws.OperazioneDMLType;
import it.eldasoft.simog.ws.OperazioneType;
import it.eldasoft.utils.spring.SpringAppContext;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

public class EldasoftSIMOGWSFacade {

  Logger logger = Logger.getLogger(EldasoftSIMOGWSFacade.class);

  /**
   * Inserimento gara/lotto
   * 
   * @param login
   * @param password
   * @param datiGaraLottoXML
   * @return
   * @throws java.rmi.RemoteException
   */
  public it.eldasoft.simog.ws.EsitoInserisciGaraLotto inserisciGaraLotto(java.lang.String login, java.lang.String password,
      java.lang.String datiGaraLottoXML) throws java.rmi.RemoteException {

    if (logger.isDebugEnabled()) logger.debug("EldasoftSIMOGWSFacade.inserisciGaraLotto: inizio metodo");

    ApplicationContext ctx = WebApplicationContextUtils.getWebApplicationContext(SpringAppContext.getServletContext());
    EldasoftSIMOGWSManager eldasoftSIMOGWSManager = (EldasoftSIMOGWSManager) ctx.getBean("eldasoftSIMOGWSManager");

    EsitoInserisciGaraLotto esitoInserisciGaraLotto = new EsitoInserisciGaraLotto();
    try {
      List<String[]> messaggiDML = eldasoftSIMOGWSManager.inserisciGaraLotto(login, password, datiGaraLottoXML);
      esitoInserisciGaraLotto.setEsito(true);

      if (messaggiDML != null && messaggiDML.size() > 0) {
        OperazioneDMLType[] operazioniDML = null;
        operazioniDML = new OperazioneDMLType[messaggiDML.size()];
        for (int i = 0; i < messaggiDML.size(); i++) {
          String[] messaggioDML = messaggiDML.get(i);
          operazioniDML[i] = new OperazioneDMLType();

          String tipoInformazione = (String) messaggioDML[0];
          operazioniDML[i].setTipoInformazione(InformazioneType.fromString(tipoInformazione));

          String tipoOperazione = (String) messaggioDML[1];
          operazioniDML[i].setTipoOperazione(OperazioneType.fromString(tipoOperazione));

          String uuid = (String) messaggioDML[2];
          operazioniDML[i].setUuid(uuid);
        }
        esitoInserisciGaraLotto.setOperazioniDML(operazioniDML);
      } else {
        esitoInserisciGaraLotto.setMessaggio("Non è stato inserito, aggiornato o cancellato alcun dato");
      }

    } catch (Throwable e) {
      esitoInserisciGaraLotto.setEsito(false);
      esitoInserisciGaraLotto.setMessaggio(e.getMessage());
    }

    if (logger.isDebugEnabled()) logger.debug("EldasoftSIMOGWSFacade.inserisciGaraLotto: fine metodo");

    return esitoInserisciGaraLotto;

  }

  /**
   * Richiesta IDGARA sulla base dell'identificativo univoco UUID
   * 
   * @param uuid
   * @return
   * @throws java.rmi.RemoteException
   */
  public it.eldasoft.simog.ws.EsitoConsultaIDGARA consultaIDGARA(java.lang.String uuid) throws java.rmi.RemoteException {

    if (logger.isDebugEnabled()) logger.debug("EldasoftSIMOGWSFacade.consultaIDGARA: inizio metodo");

    ApplicationContext ctx = WebApplicationContextUtils.getWebApplicationContext(SpringAppContext.getServletContext());
    EldasoftSIMOGWSManager eldasoftSIMOGWSManager = (EldasoftSIMOGWSManager) ctx.getBean("eldasoftSIMOGWSManager");

    EsitoConsultaIDGARA esitoConsultaIDGARA = new EsitoConsultaIDGARA();

    try {
      String idgara = eldasoftSIMOGWSManager.consultaIDGARA(uuid);
      esitoConsultaIDGARA.setEsito(true);
      esitoConsultaIDGARA.setIdgara(idgara);
    } catch (Throwable e) {
      esitoConsultaIDGARA.setEsito(false);
      esitoConsultaIDGARA.setMessaggio(e.getMessage());
    }

    if (logger.isDebugEnabled()) logger.debug("EldasoftSIMOGWSFacade.consultaIDGARA: fine metodo");

    return esitoConsultaIDGARA;

  }

  /**
   * Consultazione CIG sulla base dell'identificativo univoco UUID
   * 
   * @param uuid
   * @return
   * @throws java.rmi.RemoteException
   */
  public it.eldasoft.simog.ws.EsitoConsultaCIG consultaCIG(java.lang.String uuid) throws java.rmi.RemoteException {

    if (logger.isDebugEnabled()) logger.debug("EldasoftSIMOGWSFacade.consultaCIG: inizio metodo");

    ApplicationContext ctx = WebApplicationContextUtils.getWebApplicationContext(SpringAppContext.getServletContext());
    EldasoftSIMOGWSManager eldasoftSIMOGWSManager = (EldasoftSIMOGWSManager) ctx.getBean("eldasoftSIMOGWSManager");

    EsitoConsultaCIG esitoConsultaCIG = new EsitoConsultaCIG();

    try {
      String cig = eldasoftSIMOGWSManager.consultaCIG(uuid);
      esitoConsultaCIG.setEsito(true);
      esitoConsultaCIG.setCig(cig);
    } catch (Throwable e) {
      esitoConsultaCIG.setEsito(false);
      esitoConsultaCIG.setMessaggio(e.getMessage());
    }

    if (logger.isDebugEnabled()) logger.debug("EldasoftSIMOGWSFacade.consultaCIG: fine metodo");

    return esitoConsultaCIG;

  }

  public it.eldasoft.simog.ws.EsitoVerificaCIG verificaCIG(java.lang.String cig) throws java.rmi.RemoteException {

    if (logger.isDebugEnabled()) logger.debug("EldasoftSIMOGWSFacade.verificaCIG: inizio metodo");

    ApplicationContext ctx = WebApplicationContextUtils.getWebApplicationContext(SpringAppContext.getServletContext());
    EldasoftSIMOGWSManager eldasoftSIMOGWSManager = (EldasoftSIMOGWSManager) ctx.getBean("eldasoftSIMOGWSManager");
    
    EsitoVerificaCIG esitoVerificaCIG = new EsitoVerificaCIG();
    
    try {
      esitoVerificaCIG = eldasoftSIMOGWSManager.verificaCIG(cig);
    } catch (Throwable e) {
    	logger.error("Errore inaspettato", e);
      esitoVerificaCIG.setEsito(false);
    } 
    

    if (logger.isDebugEnabled()) logger.debug("EldasoftSIMOGWSFacade.verificaCIG: fine metodo");

    return esitoVerificaCIG;
    
  }

  /**
   * Inserimento smartCIG
   * 
   * @param login
   * @param password
   * @param datiSmartCIGXML
   * @return
   * @throws java.rmi.RemoteException
   */
  public it.eldasoft.simog.ws.EsitoInserisciSmartCIG inserisciSmartCIG(java.lang.String login, java.lang.String password,
      java.lang.String datiSmartCIGXML) throws java.rmi.RemoteException {

    if (logger.isDebugEnabled()) logger.debug("EldasoftSIMOGWSFacade.inserisciSmartCIG: inizio metodo");

    ApplicationContext ctx = WebApplicationContextUtils.getWebApplicationContext(SpringAppContext.getServletContext());
    EldasoftSIMOGWSManager eldasoftSIMOGWSManager = (EldasoftSIMOGWSManager) ctx.getBean("eldasoftSIMOGWSManager");

    EsitoInserisciSmartCIG esitoInserisciSmartCIG = new EsitoInserisciSmartCIG();
    try {
      List<String[]> messaggiDML = eldasoftSIMOGWSManager.inserisciSmartCIG(login, password, datiSmartCIGXML);
      esitoInserisciSmartCIG.setEsito(true);

      if (messaggiDML != null && messaggiDML.size() > 0) {
        OperazioneDMLType[] operazioniDML = null;
        operazioniDML = new OperazioneDMLType[messaggiDML.size()];
        for (int i = 0; i < messaggiDML.size(); i++) {
          String[] messaggioDML = messaggiDML.get(i);
          operazioniDML[i] = new OperazioneDMLType();

          String tipoInformazione = (String) messaggioDML[0];
          operazioniDML[i].setTipoInformazione(InformazioneType.fromString(tipoInformazione));

          String tipoOperazione = (String) messaggioDML[1];
          operazioniDML[i].setTipoOperazione(OperazioneType.fromString(tipoOperazione));

          String uuid = (String) messaggioDML[2];
          operazioniDML[i].setUuid(uuid);
        }
        esitoInserisciSmartCIG.setOperazioniDML(operazioniDML);
      } else {
        esitoInserisciSmartCIG.setMessaggio("Non è stato inserito, aggiornato o cancellato alcun dato");
      }

    } catch (Throwable e) {
      esitoInserisciSmartCIG.setEsito(false);
      esitoInserisciSmartCIG.setMessaggio(e.getMessage());
    }

    if (logger.isDebugEnabled()) logger.debug("EldasoftSIMOGWSFacade.inserisciSmartCIG: fine metodo");

    return esitoInserisciSmartCIG;

  }
  
}
