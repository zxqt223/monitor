package com.zhangyue.monitor.util;

import java.io.IOException;
import java.net.URL;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;

public class ParamsManager {

    private static Properties props;
    private static final Log LOG = LogFactory.getLog(ParamsManager.class);

    static {
        props = new Properties();
        URL url =
                ParamsManager.class.getClassLoader().getResource(
                    "monitor-default.xml");
        if (url != null) {
            loadResource(props, url);
        } else {
            LOG.error("monitor-default.xml not found.");
        }
        url =
                ParamsManager.class.getClassLoader().getResource(
                    "monitor.xml");
        if (url != null) {
            LOG.info("load monitor.xml ......");
            loadResource(props, url);
        } else {
            LOG.info("Can't find monitor.xml.");
        }
    }

    private static void loadResource(Properties properties, Object name) {
        try {
            DocumentBuilderFactory docBuilderFactory =
                    DocumentBuilderFactory.newInstance();
            // ignore all comments inside the xml file
            docBuilderFactory.setIgnoringComments(true);

            // allow includes in the xml file
            docBuilderFactory.setNamespaceAware(true);
            try {
                docBuilderFactory.setXIncludeAware(true);
            } catch (UnsupportedOperationException e) {
                LOG.error("Failed to set setXIncludeAware(true) for parser "
                          + docBuilderFactory + ":" + e, e);
            }
            DocumentBuilder builder = docBuilderFactory.newDocumentBuilder();
            Document doc = null;
            Element root = null;
            if (name instanceof URL) {
                doc = builder.parse(((URL) name).toString());
            } else if (name instanceof Element) {
                root = (Element) name;
            }
            if (root == null) {
                root = doc.getDocumentElement();
            }
            if (!"configuration".equals(root.getTagName())) LOG.fatal("bad conf file: top-level element not <configuration>");
            NodeList props = root.getChildNodes();
            for (int i = 0; i < props.getLength(); i++) {
                Node propNode = props.item(i);
                if (!(propNode instanceof Element)) continue;
                Element prop = (Element) propNode;
                if ("configuration".equals(prop.getTagName())) {
                    loadResource(properties, prop);
                    continue;
                }
                if (!"property".equals(prop.getTagName())) LOG.warn("bad conf file: element not <property>");
                NodeList fields = prop.getChildNodes();
                String attr = null;
                String value = null;
                for (int j = 0; j < fields.getLength(); j++) {
                    Node fieldNode = fields.item(j);
                    if (!(fieldNode instanceof Element)) continue;
                    Element field = (Element) fieldNode;
                    if ("name".equals(field.getTagName())
                        && field.hasChildNodes()) attr =
                            ((Text) field.getFirstChild()).getData().trim();
                    if ("value".equals(field.getTagName())
                        && field.hasChildNodes()) value =
                            ((Text) field.getFirstChild()).getData();
                }
                if (attr != null && value != null) {
                    properties.setProperty(attr, value);
                }
            }
        } catch (IOException e) {
            LOG.fatal("error parsing conf file: " + e);
            throw new RuntimeException(e);
        } catch (DOMException e) {
            LOG.fatal("error parsing conf file: " + e);
            throw new RuntimeException(e);
        } catch (SAXException e) {
            LOG.fatal("error parsing conf file: " + e);
            throw new RuntimeException(e);
        } catch (ParserConfigurationException e) {
            LOG.fatal("error parsing conf file: " + e);
            throw new RuntimeException(e);
        }
    }

    public static String getString(String paramName) {
        return props.getProperty(paramName);
    }

    public static void setString(String key, String value) {
        props.setProperty(key, value);
    }

    public static String getString(String paramName, String defaultValue) {
        String strValue = props.getProperty(paramName);
        if (strValue == null || "".equals(strValue)) {
            return defaultValue;
        }

        return strValue;
    }

    public static int getInt(String paramName, int defaultValue) {
        String strValue = props.getProperty(paramName);
        if (strValue == null || "".equals(strValue)) {
            return defaultValue;
        }
        return Integer.parseInt(strValue);
    }

    public static long getLong(String paramName, long defaultValue) {
        String strValue = props.getProperty(paramName);
        if (strValue == null || "".equals(strValue)) {
            return defaultValue;
        }
        return Long.parseLong(strValue);
    }

    public static double getDouble(String paramName, double defaultValue) {
        String strValue = props.getProperty(paramName);
        if (strValue == null || "".equals(strValue)) {
            return defaultValue;
        }
        return Double.parseDouble(strValue);
    }

    public static Boolean getBoolean(String paramName, boolean defaultValue) {
        String strValue = props.getProperty(paramName);
        if (strValue == null || "".equals(strValue)) {
            return defaultValue;
        }
        return Boolean.parseBoolean(strValue);
    }
}
