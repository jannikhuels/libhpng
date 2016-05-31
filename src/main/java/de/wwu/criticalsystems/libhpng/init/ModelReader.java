package de.wwu.criticalsystems.libhpng.init;

import java.io.File;
import java.util.Collections;
import java.util.logging.Logger;

import javax.xml.bind.JAXBException;
import de.wwu.criticalsystems.libhpng.errorhandling.*;
import de.wwu.criticalsystems.libhpng.model.*;
import de.wwu.criticalsystems.libhpng.model.ContinuousArc.ContinuousArcType;
import de.wwu.criticalsystems.libhpng.model.DiscreteArc.DiscreteArcType;

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
				throw new ModelNotReadableException("File '" + filePath + "' not found");
	    	}
	    		
	    	//Read model from XML
	    	XMLReader xmlReader = new XMLReader();
	    	if (logger != null) xmlReader.setLogger(logger);
			model = xmlReader.readXmlIntoModel(xmlFile);
			
			if (logger != null) logger.info("Model structure has been read from XML file successfully.");

			//initialize model
			setConnectedPlacesAndTransitions();
			sortLists();
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
	
	
	private void setConnectedPlacesAndTransitions() throws InvalidModelConnectionException{
		
		
		for(Arc arc: model.getArcs()){
			Boolean fromNodeFound = false;
			Boolean toNodeFound = false;	
			
			//search for place id
		    for(Place place: model.getPlaces()){
		    	
		        if(!fromNodeFound && place.getId().equals(arc.getFromNode())){
		        	
		        	fromNodeFound = true;
		        	arc.setConnectedPlace(place);		        			        	
		        	if(arc.getClass().equals(ContinuousArc.class)){
		        		ContinuousArcType dir = ContinuousArcType.output;
		        		((ContinuousArc)arc).setDirection(dir);
		        	} else if (arc.getClass().equals(DiscreteArc.class)){
		        		DiscreteArcType dir = DiscreteArcType.output;
		        		((DiscreteArc)arc).setDirection(dir);		        	
		        	}
		        	break;
		        		
		        } else if (!toNodeFound && place.getId().equals(arc.getToNode())){
		        	
		        	toNodeFound = true;
		        	arc.setConnectedPlace(place);
		        	if(arc.getClass().equals(ContinuousArc.class)){
		        		ContinuousArcType dir = ContinuousArcType.input;
		        		((ContinuousArc)arc).setDirection(dir);
		        	} else if (arc.getClass().equals(DiscreteArc.class)){
		        		DiscreteArcType dir = DiscreteArcType.input;
		        		((DiscreteArc)arc).setDirection(dir);		        	
		        	}
		        	break;
		        }		        
		    }
		       
		    //search for transition id
	        for(Transition transition: model.getTransitions()){
		        if(!fromNodeFound && transition.getId().equals(arc.getFromNode())){
		        	
		        	fromNodeFound = true;
		        	arc.setConnectedTransition(transition);	
		        	transition.getConnectedArcs().add(arc);
		        	break;
		        		
		        } else if (!toNodeFound && transition.getId().equals(arc.getToNode())){
		        	
		        	toNodeFound = true;
		        	arc.setConnectedTransition(transition);
		        	transition.getConnectedArcs().add(arc);
		        	break;
		        }	        	
		    }	
	        
	        if (!fromNodeFound){
	          	if (logger != null) logger.severe("Model error: 'fromNode' for arc " + arc.getId() + " could not be matched to any place or transition");
	        	throw new InvalidModelConnectionException("'fromNode' for arc '" + arc.getId() + "' could not be matched to any place or transition");
	        }
	        if (!toNodeFound){
	        	if (logger != null) logger.severe("Model error: 'toNode' for arc '" + arc.getId() + "' could not be matched to any place or transition");
	        	throw new InvalidModelConnectionException("'toNode' for arc " + arc.getId() + " could not be matched to any place or transition");
	        }
	        
	      }
		  if (logger != null) logger.info("Model object connections were set successfully.");

	}
	
	
	private void sortLists(){
	
		Collections.sort(model.getPlaces(), new PlaceComparator());
		Collections.sort(model.getTransitions(), new TransitionComparator());
		Collections.sort(model.getArcs(), new ArcComparator());
		
		for(Transition transition: model.getTransitions())
			Collections.sort(transition.getConnectedArcs(),new ArcComparatorForTransitions());
		
		if (logger != null) logger.info("Model lists sorted successfully.");
	}

	
}