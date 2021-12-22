package it.eldasoft.sil.vigilanza.commons;

/**
 * Eccezione creata per effettuare la rollback di tutta una serie di insert/update, nel caso che,
 * dopo l'insert/update dei dati di n lotti, il lotto n+1 presenta errori gestiti.     
 * 
 * @author Luca.Giacomazzo
 */
public class WSVigilanzaException extends RuntimeException {

	/**   UID   */
	private static final long serialVersionUID = 1L;

	public WSVigilanzaException(String error) {
		super(error);
	}
	
}
