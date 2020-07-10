package support;

import java.io.File;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class XML_functions {
	String pathDoFajla = "C:/Users/BUDI/workspace/IBP/Mail Client V2/data/XMLFiles/";
	public String getPathDoFajla(){
		return pathDoFajla;
	}
	public File saveEmailToXMLFile(String primalac,String naslov, String poruka){
		File xmlFajl = null;
		
		try {
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		    DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
		    Document doc = docBuilder.newDocument();
		    Element rootElement = doc.createElement("EMail");
		    doc.appendChild(rootElement);
		   
		    Element prim = doc.createElement("Primalac");
		    prim.appendChild(doc.createTextNode(primalac));
		    rootElement.appendChild(prim);
		    
		    Element nas = doc.createElement("Naslov");
		    nas.appendChild(doc.createTextNode(naslov));
		    rootElement.appendChild(nas);
		    
		    Element por = doc.createElement("Poruka");
		    por.appendChild(doc.createTextNode(poruka));
		    rootElement.appendChild(por);
		    
		    
		    TransformerFactory transformerFactory = TransformerFactory.newInstance();
		    Transformer transformer = transformerFactory.newTransformer();
		    DOMSource source = new DOMSource(doc);
		    StreamResult result = new StreamResult(new File(pathDoFajla+"mail.xml"));
		    transformer.transform(source, result);
		    System.out.println("File saved!");
			
			xmlFajl = new File(pathDoFajla+"mail.xml");
		} catch (Exception e) {
			e.printStackTrace();
			xmlFajl = null;
			
		}
		return xmlFajl;
		
	}
	
}
