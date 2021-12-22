/*
 * Created on 06-Feb-2012
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.w3.tags.funzioni;

import java.sql.SQLException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.gene.tags.utils.UtilityTags;
import it.eldasoft.utils.spring.UtilitySpring;

public class GetStatoSimogW3LottFunction extends AbstractFunzioneTag {

  public GetStatoSimogW3LottFunction() {
    super(3, new Class[] { PageContext.class, String.class, String.class });
  }

  public String function(PageContext pageContext, Object[] params)
      throws JspException {

    Long stato_simog = null;

    if (!UtilityTags.SCHEDA_MODO_INSERIMENTO.equals(UtilityTags.getParametro(
        pageContext, UtilityTags.DEFAULT_HIDDEN_PARAMETRO_MODO))) {

      SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
          pageContext, SqlManager.class);

      Long numgara = new Long((String)params[1]);
      Long numlott = new Long((String)params[2]);

      try {
        stato_simog = (Long) sqlManager.getObject(
            "select stato_simog from w3lott where numgara = ? and numlott = ?",
            new Object[] { numgara, numlott });
      } catch (SQLException s) {
        throw new JspException(
            "Errore durante la lettura dello stato del lotto", s);
      }

    }

    if (stato_simog == null) stato_simog = new Long(0);
    
    return stato_simog.toString();

  }

}