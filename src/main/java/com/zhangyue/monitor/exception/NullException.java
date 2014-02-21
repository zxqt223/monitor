package com.zhangyue.monitor.exception;

public class NullException extends Exception {

    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;

    public NullException(Exception e){
        super(e);
    }

    public NullException(String msg){
        super(msg);
    }

    public NullException(String msg, Exception e){
        super(msg, e);
    }
}
