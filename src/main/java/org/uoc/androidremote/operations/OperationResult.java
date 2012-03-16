package org.uoc.androidremote.operations;

import java.io.Serializable;

/**
 * This is a simple class which is responsible for encapsulating the 
 * information returned to the client after a server action.
 * 
 * @author robertrv [at] gmail
 *
 */
public class OperationResult implements Serializable {
	private static final long serialVersionUID = 1L;
	
	public enum Type {
		OK, KO
	}
	
	private Type type;
	private String message;
	private Exception exception;
	
	public OperationResult() {
	}
	
	public OperationResult(Exception exception) {
		this.exception = exception;
		this.type = Type.KO;
	}
	
	public void setKoMessage(String message) {
		this.message = message;
		this.type = Type.KO;
	}
	
	public void setOkMessage(String message) {
		this.message = message;
		this.type = Type.OK;
	}
	
	public void setKoException(Exception exception){
		this.exception = exception;
	}

	public Type getType() {
		return type;
	}

	public String getMessage() {
		return message;
	}
	
	public Exception getException() {
		return exception;
	}
	
	public String toString() {
		StringBuilder builder = new StringBuilder();
		if (type != null) {
			builder.append("Result type: ").append(type.toString()).append("\n\n");
		}
		if (message != null) {
			builder.append("Message: ").append(message).append("\n\n");
		}
		if (exception != null) {
			builder.append("Exception: ").append(
					exception.getLocalizedMessage());
		}
		return builder.toString();
	}
}
