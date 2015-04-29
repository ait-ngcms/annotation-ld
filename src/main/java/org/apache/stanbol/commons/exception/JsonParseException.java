package org.apache.stanbol.commons.exception;


public class JsonParseException extends Exception{

	/**
	 * 
	 */
	private static final long serialVersionUID = 9030768033078835554L;
	
	public JsonParseException(String message, Throwable th) {
	
		super(message, th);
	}

	public JsonParseException(String message) {
		
		super(message);
	}
	
}
