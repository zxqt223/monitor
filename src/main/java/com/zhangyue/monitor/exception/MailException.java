package com.zhangyue.monitor.exception;

public class MailException extends Exception {

    /**
   * 
   */
    private static final long serialVersionUID = 1L;

    public MailException(){
        super();
    }

    public MailException(String msg){
        super(msg);
    }

    public MailException(Exception e){
        super(e);
    }
}
