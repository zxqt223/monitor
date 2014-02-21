package com.zhangyue.monitor.exception;

import java.io.IOException;

public class ExitCodeException extends IOException {

    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;
    private int exitCode;

    public ExitCodeException(int exitCode, String message){
        super(message);
        this.exitCode = exitCode;
    }

    public int getExitCode() {
        return exitCode;
    }
}
