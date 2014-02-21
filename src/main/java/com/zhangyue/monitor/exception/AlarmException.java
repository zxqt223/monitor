package com.zhangyue.monitor.exception;

public class AlarmException extends Exception {

    /**
	* 
    */
    private static final long serialVersionUID = 1L;

    public AlarmException(){
        super();
    }

    public AlarmException(String msg){
        super(msg);
    }

    public AlarmException(Exception e){
        super(e);
    }
}
