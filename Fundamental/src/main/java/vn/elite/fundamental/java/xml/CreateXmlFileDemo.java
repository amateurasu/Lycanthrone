package vn.elite.fundamental.java.xml;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CreateXmlFileDemo {

    public static void main(String[] argv) {
        try {
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document document = builder.newDocument();
            // root element
            Element rootElement = document.createElement("cars");
            document.appendChild(rootElement);

            Element supercar = document.createElement("supercars");
            rootElement.appendChild(supercar);

            // setting attribute to element
            Attr attr = document.createAttribute("company");
            attr.setValue("Ferrari");
            supercar.setAttributeNode(attr);

            // carname element
            Element carname = document.createElement("carname");
            Attr attrType = document.createAttribute("type");
            attrType.setValue("formula one");
            carname.setAttributeNode(attrType);
            carname.appendChild(document.createTextNode("Ferrari 101"));
            supercar.appendChild(carname);

            Element carname1 = document.createElement("carname");
            Attr attrType1 = document.createAttribute("type");
            attrType1.setValue("sports");
            carname1.setAttributeNode(attrType1);
            carname1.appendChild(document.createTextNode("Ferrari 202"));
            supercar.appendChild(carname1);

            // write the content into xml file
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(document);
            StreamResult result = new StreamResult(new File("resource/xmlexamples/CreateXmlFileDemo.xml"));
            transformer.transform(source, result);

            // Output to console for testing
            StreamResult consoleResult = new StreamResult(System.out);
            transformer.transform(source, consoleResult);
        } catch (ParserConfigurationException | TransformerException ex) {
            Logger.getLogger(CreateXmlFileDemo.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
