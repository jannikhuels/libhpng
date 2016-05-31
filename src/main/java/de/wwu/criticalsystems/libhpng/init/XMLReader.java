package de.wwu.criticalsystems.libhpng.init;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.*;

import org.xml.sax.SAXException;

import de.wwu.criticalsystems.libhpng.errorhandling.XmlNotValidException;
import de.wwu.criticalsystems.libhpng.model.*;

public class XMLReader {
	
	public void setLogger(Logger logger) {
		this.logger = logger;
	}
	
	private Logger logger;
	
	public HPnGModel readXmlIntoModel(File xmlFile) throws JAXBException, XmlNotValidException {
		
			validateAgainstSchema(xmlFile);
		
			HPnGModel model=null;
	    	JAXBContext jaxbContext;
	    	
	    	//extract information from xml file into HPnG model
			jaxbContext = JAXBContext.newInstance(HPnGModel.class);
	    	Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
	    	model = (HPnGModel)jaxbUnmarshaller.unmarshal(xmlFile);
	    				
			return model;
		
	}
	
	private void validateAgainstSchema(File xmlFile) throws XmlNotValidException
	{
    	File xsdFile = new File("xmlschema/HPnG.xsd");	
    	
    	if (!xsdFile.exists()){
    		if (logger != null) logger.warning("The XML Schema file could not be found. The model cannot be validated.");
    	
    	} else {
    	
	        SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
	        Schema schema;
			try {
				schema = factory.newSchema(xsdFile);
				Validator validator = schema.newValidator();
				StreamSource xmlSource = new StreamSource(xmlFile); 
		        validator.validate(xmlSource);
		        
		        if (logger != null) logger.info("The XML model file has been validated against the XML Schema file successfully.");
		    	
			} catch (SAXException e) {
				throw new XmlNotValidException(e.getLocalizedMessage());  
			} catch (IOException e) {
				throw new XmlNotValidException(e.getLocalizedMessage());
			}
	        
    	}
	}
}

