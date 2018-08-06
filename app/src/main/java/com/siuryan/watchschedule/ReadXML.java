package com.siuryan.watchschedule;


import android.util.Log;

import java.io.InputStream;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class ReadXML {

    private HashMap<String, String> items = new HashMap<>();

    public ReadXML(String filename) {
        try {
            InputStream inputStream = MainActivity.class.getResourceAsStream(filename);
            Log.d("testingis:", inputStream.toString());
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document document = db.parse(inputStream);
            Log.d("testingdoc:", document.toString());

            document.getDocumentElement().normalize();

            NodeList nList = document.getElementsByTagName("item");
            Log.d("testing:", nList.toString());

            for (int i = 0; i < nList.getLength(); i++) {
                Node nNode = nList.item(i);
                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) nNode;
                    items.put(element.getAttribute("name"), element.getAttribute("time"));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public HashMap<String, String> getItems() {
        return items;
    }

}
