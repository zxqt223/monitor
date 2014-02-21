package com.zhangyue.monitor.exception;

public class HandlerConstructException extends Exception {

    /**
   * 
   */
    private static final long serialVersionUID = 1L;

    public HandlerConstructException(){
        super();
    }

    public HandlerConstructException(String msg){
        super(msg);
    }

    public HandlerConstructException(Exception e){
        super(e);
    }
}
