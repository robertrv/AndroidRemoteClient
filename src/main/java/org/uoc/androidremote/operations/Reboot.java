package org.uoc.androidremote.operations;

import java.io.Serializable;

public class Reboot implements Serializable {
    private static final long serialVersionUID = 1L;

    private boolean result;
    private String problemMessage;

	public boolean getResult() {
		return result;
	}

	public void setResult(boolean result) {
		this.result = result;
	}

	public String getProblemMessage() {
		return problemMessage;
	}

	public void setProblemMessage(String problemMessage) {
		this.problemMessage = problemMessage;
	}
}
