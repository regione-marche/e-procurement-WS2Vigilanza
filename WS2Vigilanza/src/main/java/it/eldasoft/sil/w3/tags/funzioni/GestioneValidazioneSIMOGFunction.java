package it.eldasoft.sil.w3.tags.funzioni;


import java.util.HashMap;

import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;

import it.eldasoft.sil.w3.bl.ValidazioneSIMOGManager;
import it.eldasoft.utils.spring.UtilitySpring;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import org.apache.log4j.Logger;

public class GestioneValidazioneSIMOGFunction extends AbstractFunzioneTag {

  static Logger logger = Logger.getLogger(GestioneValidazioneSIMOGFunction.class);

  public GestioneValidazioneSIMOGFunction() {
    super(5, new Class[] { PageContext.class, String.class, String.class, String.class, String.class });
  }

  public String function(PageContext pageContext, Object params[]) throws JspException {
    
    ValidazioneSIMOGManager validazioneSIMOGManager = (ValidazioneSIMOGManager) UtilitySpring.getBean(
        "validazioneSIMOGManager", pageContext,
        ValidazioneSIMOGManager.class);

    HashMap infoValidazione = validazioneSIMOGManager.validate(params);
    pageContext.setAttribute("titolo", infoValidazione.get("titolo"));
    pageContext.setAttribute("listaControlli", infoValidazione.get("listaControlli"));
    pageContext.setAttribute("numeroWarning", infoValidazione.get("numeroWarning"));
    pageContext.setAttribute("numeroErrori", infoValidazione.get("numeroErrori"));

    return null;
    
  }

}
