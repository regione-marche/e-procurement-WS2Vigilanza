package it.eldasoft.sil.vigilanza.utils;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

/**
 * Bean per costruire gli avvisi da inserire nella G_NOTEAVVISI. 
 * 
 * @author Luca.Giacomazzo
 */
public class NotaAvviso {

	private String titolo;
	
	private List<String> messaggi;
	
	public NotaAvviso() {
		this.titolo = null;
		this.messaggi = null;
	}
	
	public NotaAvviso(String titolo) {
		this.titolo = titolo;
	}
	
	public String getTitolo() {
		return titolo;
	}

	public void setTitolo(String titolo) {
		this.titolo = titolo;
	}

	public boolean isSetTitolo() {
		return StringUtils.isNotEmpty(this.titolo);
	}
	
	public List<String> getMessaggi() {
		return messaggi;
	}

	public void addMessaggio(String messaggio) {
		if (this.messaggi == null) {
			this.messaggi = new ArrayList<String>();
		}

		this.messaggi.add(messaggio);
	}
	
	public boolean isSetMessaggi() {
		boolean result = false;
		if (this.messaggi != null && this.messaggi.size() > 0) {
				result = true;
		}
		return result;
	}
	
}
