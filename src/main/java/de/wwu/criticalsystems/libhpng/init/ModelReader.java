package de.wwu.criticalsystems.libhpng.init;

import java.io.File;
import java.util.Collections;

import de.wwu.criticalsystems.libhpng.model.*;
import de.wwu.criticalsystems.libhpng.model.ContinuousArc.ContinuousArcType;
import de.wwu.criticalsystems.libhpng.model.DiscreteArc.DiscreteArcType;

public class ModelReader {
	
	public HPnGModel readModel(String filePath){	
		
		//Read model from XML
    	File xmlFile = new File(filePath);
    	XMLReader xmlReader = new XMLReader();
		HPnGModel model = xmlReader.readXmlIntoModel(xmlFile);
		
		//Set connectedPlace and connectedTransition for all arcs			
		for(Arc arc: model.getArcs()){
			boolean fromNodeFound = false;
			boolean toNodeFound = false;	
			
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
		        
	        for(Transition transition: model.getTransitions()){
		        if(!fromNodeFound && transition.getId().equals(arc.getFromNode())){
		        	
		        	fromNodeFound = true;
		        	arc.setConnectedTransition(transition);		        			       
		        	break;
		        		
		        } else if (!toNodeFound && transition.getId().equals(arc.getToNode())){
		        	
		        	toNodeFound = true;
		        	arc.setConnectedTransition(transition);
		        	break;
		        }	        	
		    }	
	        
	        if (!fromNodeFound)
	        	System.out.println("Model error: 'fromNode' for arc " + arc.getId() + " could not be matched to any place or transition");	        
	        if (!toNodeFound)
	        	System.out.println("Model error: 'toNode' for arc " + arc.getId() + " could not be matched to any place or transition");
	      }
		
		//Sort lists
		Collections.sort(model.getPlaces(), new PlaceComparator());
		Collections.sort(model.getTransitions(), new TransitionComparator());
		Collections.sort(model.getArcs(), new ArcComparator());
		System.out.print(model.getArcs().get(0).getId());

		return model;
	}
}