package de.wwu.criticalsystems.libhpng.init;

import java.io.File;
import java.util.Collections;
import javax.xml.bind.JAXBException;
import de.wwu.criticalsystems.libhpng.errorhandling.InvalidModelConnectionException;
import de.wwu.criticalsystems.libhpng.model.*;
import de.wwu.criticalsystems.libhpng.model.ContinuousArc.ContinuousArcType;
import de.wwu.criticalsystems.libhpng.model.DiscreteArc.DiscreteArcType;

public class ModelReader {
	
	public ModelReader() {}


	private HPnGModel model;
	
	public HPnGModel readModel(String filePath){	
		
		//Read model from XML
    	File xmlFile = new File(filePath);
    	XMLReader xmlReader = new XMLReader();
    	
		try {
			
			model = xmlReader.readXmlIntoModel(xmlFile);

			setConnectedPlacesAndTransitions();
			setUpperBoundaryInfinityValues();
			sortLists();
			model.resetMarking();
			
			return model;
			
		} catch (JAXBException e) {
			System.out.println("An Error occured while reading the model file. Please see the error log and check the model.");
			//e.printStackTrace();
		} catch(InvalidModelConnectionException e) {
			//e.printStackTrace();
		}
				
		return null;
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
	        	System.out.println("Model error: 'fromNode' for arc " + arc.getId() + " could not be matched to any place or transition");
	        	throw new InvalidModelConnectionException("'fromNode' for arc " + arc.getId() + " could not be matched to any place or transition");
	        }
	        if (!toNodeFound){
	        	System.out.println("Model error: 'toNode' for arc " + arc.getId() + " could not be matched to any place or transition");
	        	throw new InvalidModelConnectionException("'toNode' for arc " + arc.getId() + " could not be matched to any place or transition");
	        }	
	      }
	}
	
	
	private void setUpperBoundaryInfinityValues(){
		
		for(Place place: model.getPlaces()){
			if (place.getClass().equals(ContinuousPlace.class)){
				
				if (((ContinuousPlace)place).getUpperBoundary() == null || ((ContinuousPlace)place).getUpperBoundary().equals(("infinity")) | ((ContinuousPlace)place).getUpperBoundary().equals(("inf")))
					((ContinuousPlace)place).setUpperBoundaryInfinity(true);
				else
					((ContinuousPlace)place).setUpperBoundaryInfinity(false);
			}
		}
	}
	
	
	private void sortLists(){
	
		Collections.sort(model.getPlaces(), new PlaceComparator());
		Collections.sort(model.getTransitions(), new TransitionComparator());
		Collections.sort(model.getArcs(), new ArcComparator());
		
		for(Transition transition: model.getTransitions())
			Collections.sort(transition.getConnectedArcs(),new ArcComparatorForTransitions());
	}	
}