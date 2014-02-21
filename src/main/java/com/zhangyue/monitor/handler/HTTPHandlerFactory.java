package com.zhangyue.monitor.handler;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import com.zhangyue.monitor.exception.HandlerConstructException;
import com.zhangyue.monitor.util.ParamsManager;

/**
 * @Descriptions The class HTTPHandlerFactory.java's implementation：The factory
 *               of Http handler
 * @author scott 2013-8-19 上午10:14:02
 * @version 1.0
 */
public class HTTPHandlerFactory {

    public static HTTPHandler getInstance() throws HandlerConstructException {
        Constructor<?> constructor = null;
        try {
            Class<?> clazz =
                    Class.forName(ParamsManager.getString("http.handler"));
            constructor = clazz.getDeclaredConstructor(new Class[] {});
            constructor.setAccessible(true);
        } catch (ClassNotFoundException e) {
            throw new HandlerConstructException(e);
        } catch (SecurityException e) {
            throw new HandlerConstructException(e);
        } catch (NoSuchMethodException e) {
            throw new HandlerConstructException(e);
        }
        try {
            return (HTTPHandler) constructor.newInstance();
        } catch (IllegalArgumentException e) {
            throw new HandlerConstructException(e);
        } catch (InstantiationException e) {
            throw new HandlerConstructException(e);
        } catch (IllegalAccessException e) {
            throw new HandlerConstructException(e);
        } catch (InvocationTargetException e) {
            throw new HandlerConstructException(e);
        }
    }
}
