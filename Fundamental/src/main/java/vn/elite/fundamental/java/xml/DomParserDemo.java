package vn.elite.fundamental.java.xml;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DomParserDemo {

    public static void main(String[] args) {

        try {
            File inputFile = new File("resource/xmlexamples/DomParserDemo.xml");
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document document = dBuilder.parse(inputFile);
            document.getDocumentElement().normalize();
            System.out.println("Root element :" + document.getDocumentElement().getNodeName());
            NodeList nodeList = document.getElementsByTagName("student");
            System.out.println("----------------------------");
            for (int temp = 0; temp < nodeList.getLength(); temp++) {
                Node node = nodeList.item(temp);
                System.out.println("\nnode: " + node);
                System.out.println("\nCurrent Element :" + node.getNodeName());
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) node;
                    System.out.println("Student roll no : " + element.getAttribute("rollno"));
                    System.out.println("First Name      : "
                        + element.getElementsByTagName("firstname").item(0).getTextContent());
                    System.out.println("Last Name       : "
                        + element.getElementsByTagName("lastname").item(0).getTextContent());
                    System.out.println("Nick Name       : "
                        + element.getElementsByTagName("nickname").item(0).getTextContent());
                    System.out.println("Marks           : "
                        + element.getElementsByTagName("marks").item(0).getTextContent());
                }
            }
        } catch (ParserConfigurationException | SAXException | IOException ex) {
            Logger.getLogger(DomParserDemo.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
