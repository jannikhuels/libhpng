package de.wwu.criticalsystems.libhpng.init;

import java.io.File;
import java.util.logging.Logger;
import javax.xml.bind.JAXBException;
import de.wwu.criticalsystems.libhpng.errorhandling.*;
import de.wwu.criticalsystems.libhpng.model.*;

public class ModelReader {
	
	public ModelReader() {}

	
	public void setLogger(Logger logger) {
		this.logger = logger;
	}
	
	
	private HPnGModel model;
	private Logger logger;
	
	
	public HPnGModel readModel(String filePath) throws ModelNotReadableException{	
		
		try {
			
			//check if file exists
	    	File xmlFile = new File(filePath);	    	
	    	if (!xmlFile.exists()){
	    		if (logger != null) logger.severe("The model file could not be found.");
	    		System.out.println("File '" + filePath + "' not found");
				throw new ModelNotReadableException("File '" + filePath + "' not found");
	    	}
	    		
	    	//Read model from XML
	    	XMLReader xmlReader = new XMLReader();
	    	if (logger != null) xmlReader.setLogger(logger);
			model = xmlReader.readXmlIntoModel(xmlFile);
			
			if (logger != null) logger.info("Model structure has been read from XML file successfully.");

			//initialize model
			model.setConnectedPlacesAndTransitions(logger);
			model.sortLists(logger);
			model.resetMarking();
			
			return model;
			
		} catch (JAXBException e) {
			if (logger != null) logger.severe("The XML file could not be read in by JAXB.");
			throw new ModelNotReadableException(e.getLocalizedMessage());
			
		} catch(InvalidModelConnectionException e) {
			throw new ModelNotReadableException(e.getLocalizedMessage());
			
		} catch (XmlNotValidException e) {
			if (logger != null) logger.severe("The XML file does not fulfill the XML Schema definition: " + e.getLocalizedMessage());
			throw new ModelNotReadableException(e.getLocalizedMessage());				
		}
	}	
	
}
