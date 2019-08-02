package de.wwu.criticalsystems.libhpng.init;

import de.wwu.criticalsystems.libhpng.errorhandling.InvalidModelConnectionException;
import de.wwu.criticalsystems.libhpng.errorhandling.ModelNotReadableException;
import de.wwu.criticalsystems.libhpng.errorhandling.XmlNotValidException;
import de.wwu.criticalsystems.libhpng.model.HPnGModel;
import de.wwu.criticalsystems.libhpng.model.HPnGModelVar;

import javax.xml.bind.JAXBException;
import java.io.File;
import java.util.logging.Logger;

public class ModelReaderVar {

	public ModelReaderVar() {}

	
	public void setLogger(Logger logger) {
		this.logger = logger;
	}
	
	
	private HPnGModelVar model;
	private Logger logger;
	
	
	public HPnGModelVar readModel(String filePath) throws ModelNotReadableException{
		
		try {
			
			//check if file exists
	    	File xmlFile = new File(filePath);	    	
	    	if (!xmlFile.exists()){
	    		if (logger != null) logger.severe("The model file could not be found.");
	    		System.out.println("An error occured while reading the model file: File '" + filePath + "' not found");
				throw new ModelNotReadableException("File '" + filePath + "' not found");
	    	}
	    		
	    	//Read model from XML
	    	XMLReaderVar xmlReader = new XMLReaderVar();
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
			System.out.println("An error occured while reading the model file. Please see the error log and recheck the model.");
			throw new ModelNotReadableException(e.getLocalizedMessage());
			
		} catch(InvalidModelConnectionException e) {
			System.out.println("An error occured while reading the model file. Please see the error log and recheck the model.");
			throw new ModelNotReadableException(e.getLocalizedMessage());
			
		} catch (XmlNotValidException e) {
			if (logger != null) logger.severe("The XML file does not fulfill the XML Schema definition: " + e.getLocalizedMessage());
			System.out.println("An error occured while reading the model file. Please see the error log and recheck the model.");
			throw new ModelNotReadableException(e.getLocalizedMessage());				
		}
	}	
	
}
