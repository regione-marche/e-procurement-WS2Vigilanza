package it.eldasoft.sil.w3.tags.funzioni;


import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;

import it.eldasoft.sil.w3.bl.ValidazioneSIMOGDatiComuniManager;

import it.eldasoft.utils.spring.UtilitySpring;

import java.util.HashMap;


import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import org.apache.log4j.Logger;

public class GestioneValidazioneSIMOGDatiComuniFunction extends AbstractFunzioneTag {

  static Logger logger = Logger.getLogger(GestioneValidazioneSIMOGDatiComuniFunction.class);

  public GestioneValidazioneSIMOGDatiComuniFunction() {
    super(2, new Class[] { PageContext.class, String.class });
  }

  public String function(PageContext pageContext, Object params[]) throws JspException {

    ValidazioneSIMOGDatiComuniManager validazioneSIMOGDatiComuniManager = (ValidazioneSIMOGDatiComuniManager) UtilitySpring.getBean(
        "validazioneSIMOGDatiComuniManager", pageContext, ValidazioneSIMOGDatiComuniManager.class);

    HashMap infoValidazione = validazioneSIMOGDatiComuniManager.validate(params);
    pageContext.setAttribute("titolo", infoValidazione.get("titolo"));
    pageContext.setAttribute("listaControlliDatiComuni", infoValidazione.get("listaControlliDatiComuni"));
    pageContext.setAttribute("listaControlliAggiudicatari", infoValidazione.get("listaControlliAggiudicatari"));
    pageContext.setAttribute("listaControlliResponsabili", infoValidazione.get("listaControlliResponsabili"));
    
    pageContext.setAttribute("numeroErroriDatiComuni", infoValidazione.get("numeroErroriDatiComuni"));
    pageContext.setAttribute("numeroWarningDatiComuni", infoValidazione.get("numeroWarningDatiComuni"));
    pageContext.setAttribute("numeroErroriAggiudicatari", infoValidazione.get("numeroErroriAggiudicatari"));
    pageContext.setAttribute("numeroWarningAggiudicatari", infoValidazione.get("numeroWarningAggiudicatari"));
    pageContext.setAttribute("numeroErroriResponsabili", infoValidazione.get("numeroErroriResponsabili"));
    pageContext.setAttribute("numeroWarningResponsabili", infoValidazione.get("numeroWarningResponsabili"));
    
    return null;
  }

}
