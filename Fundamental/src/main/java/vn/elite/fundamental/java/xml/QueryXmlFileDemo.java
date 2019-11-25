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

public class QueryXmlFileDemo {

    public static void main(String[] argv) {
        try {
            File inputFile = new File("resource/xmlexamples/QueryXmlFileDemo.xml");
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document document = builder.parse(inputFile);
            document.getDocumentElement().normalize();
            System.out.print("Root element: ");
            System.out.println(document.getDocumentElement().getNodeName());
            NodeList nodeList = document.getElementsByTagName("supercars");
            System.out.println("----------------------------");
            for (int temp = 0; temp < nodeList.getLength(); temp++) {
                Node node = nodeList.item(temp);
                System.out.println("\nCurrent Element :");
                System.out.print(node.getNodeName());
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) node;
                    System.out.print("company : ");
                    System.out.println(eElement.getAttribute("company"));
                    NodeList carNameList = eElement.getElementsByTagName("carname");
                    for (int count = 0; count < carNameList.getLength(); count++) {
                        Node node1 = carNameList.item(count);
                        if (node1.getNodeType() == Node.ELEMENT_NODE) {
                            Element car = (Element) node1;
                            System.out.print("car name : ");
                            System.out.println(car.getTextContent());
                            System.out.print("car type : ");
                            System.out.println(car.getAttribute("type"));
                        }
                    }
                }
            }
        } catch (ParserConfigurationException | SAXException | IOException ex) {
            Logger.getLogger(QueryXmlFileDemo.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
