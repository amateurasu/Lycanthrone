package vn.elite.fundamental.java.xml;

import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;

public class ModifyXmlFileDemo {

    public static void main(String[] argv) {

        try {
            File inputFile = new File("resource/xmlexamples/ModifyXmlFileDemo.vn.duclm.test.xml");
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(inputFile);
            Node cars = document.getFirstChild();
            Node supercar = document.getElementsByTagName("supercars").item(0);

            // update supercar attribute
            NamedNodeMap attr = supercar.getAttributes();
            Node nodeAttr = attr.getNamedItem("company");
            nodeAttr.setTextContent("Lamborigini");
            //supercar.getAttributes().getNamedItem("company").setTextContent("Lamboriginis");

            // loop the supercar child node
            NodeList list = supercar.getChildNodes();
            for (int temp = 0; temp < list.getLength(); temp++) {
                Node node = list.item(temp);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) node;
                    if (eElement.getNodeName().equals("carname")) {
                        if (eElement.getTextContent().equals("Ferrari 101")) {
                            eElement.setTextContent("Lamborigini 001");
                        } else if (eElement.getTextContent().equals("Ferrari 202")) {
                            eElement.setTextContent("Lamborigini 002");
                        }
                    }
                }
            }
            NodeList childNodes = cars.getChildNodes();
            for (int count = 0; count < childNodes.getLength(); count++) {
                Node node = childNodes.item(count);
                if (node.getNodeName().equals("luxurycars")) {
                    cars.removeChild(node);
                }
            }
            // write the content on console
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(document);
            System.out.println("-----------Modified File-----------");
            StreamResult consoleResult = new StreamResult(System.out);
            transformer.transform(source, consoleResult);
        } catch (IOException | ParserConfigurationException | TransformerException | DOMException | SAXException ignored) {
        }
    }
}
