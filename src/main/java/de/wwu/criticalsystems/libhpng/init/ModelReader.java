package de.wwu.criticalsystems.libhpng.init;

import java.io.File;

import de.wwu.criticalsystems.libhpng.model.*;
import de.wwu.criticalsystems.libhpng.model.ContinuousArc.ContinuousArcType;
import de.wwu.criticalsystems.libhpng.model.DiscreteArc.DiscreteArcType;

public class ModelReader {
	
	public HPnGModel readModel(String filePath){
		
    	File xmlFile = new File(filePath);
    	XMLReader xmlReader = new XMLReader();
		HPnGModel model = xmlReader.readXmlIntoModel(xmlFile);
		
		boolean fromNodeFound = false;
		boolean toNodeFound = false;
		
		for(Arc arc: model.getArcs()){

		    for(Place place: model.getPlaces()){
		    	
		        if(fromNodeFound == false && place.getId().equals(arc.getFromNode())){
		        	
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
		        		
		        } else if (toNodeFound == false && place.getId().equals(arc.getToNode())){
		        	
		        	toNodeFound = false;
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
		        
	        for(Transition transition: model.getTransitions()){
		        if(fromNodeFound == false && transition.getId().equals(arc.getFromNode())){
		        	
		        	fromNodeFound = true;
		        	arc.setConnectedTransition(transition);		        			       
		        	break;
		        		
		        } else if (toNodeFound == false && transition.getId().equals(arc.getToNode())){
		        	
		        	toNodeFound = false;
		        	arc.setConnectedTransition(transition);
		        	break;
		        }	        	
		    }		
		}
		
		//TODO: errormessage wenn nicht alles gefunden bzw. direction nicht gesetzt
		
		return model;
	}
}
