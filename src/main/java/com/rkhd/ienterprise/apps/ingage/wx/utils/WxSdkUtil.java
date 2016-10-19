package com.rkhd.ienterprise.apps.ingage.wx.utils;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.StringReader;


public class WxSdkUtil {

    private static final DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

    public static Element paseRootElement (String sMsg) throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilder db = dbf.newDocumentBuilder();
        StringReader sr = new StringReader(sMsg);
        InputSource is = new InputSource(sr);
        Document document = db.parse(is);

        Element root = document.getDocumentElement();
//        NodeList nodelist1 = root.getElementsByTagName("InfoType");
//        String InfoType = nodelist1.item(0).getTextContent();
        return root;
    }
}
