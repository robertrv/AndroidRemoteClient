package org.uoc.androidremote.operations;

import java.io.Serializable;

public class Reboot implements Serializable {
    private static final long serialVersionUID = 1L;

    private boolean result;

	public boolean getResult() {
		return result;
	}

	public void setResult(boolean result) {
		this.result = result;
	}
}
