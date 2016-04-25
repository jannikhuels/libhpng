package de.wwu.criticalsystems.libhpng.init;

import java.io.File;
import java.util.Collections;

import de.wwu.criticalsystems.libhpng.model.*;
import de.wwu.criticalsystems.libhpng.model.ContinuousArc.ContinuousArcType;
import de.wwu.criticalsystems.libhpng.model.DiscreteArc.DiscreteArcType;

public class ModelReader {
	
	private HPnGModel model;
	
	public HPnGModel readModel(String filePath){	
		
		//Read model from XML
    	File xmlFile = new File(filePath);
    	XMLReader xmlReader = new XMLReader();
		model = xmlReader.readXmlIntoModel(xmlFile);
		
		setConnectedPlacesAndTransitions();
		setUpperBoundaryInfinityValues();
		sortLists();
		setInitialMarking();
		
		return model;
		
	}	
	
	private void setConnectedPlacesAndTransitions(){
		for(Arc arc: model.getArcs()){
			Boolean fromNodeFound = false;
			Boolean toNodeFound = false;	
			
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
		        	transition.getConnectedArcs().add(arc);
		        	break;
		        		
		        } else if (!toNodeFound && transition.getId().equals(arc.getToNode())){
		        	
		        	toNodeFound = true;
		        	arc.setConnectedTransition(transition);
		        	transition.getConnectedArcs().add(arc);
		        	break;
		        }	        	
		    }	
	        
	        if (!fromNodeFound)
	        	System.out.println("Model error: 'fromNode' for arc " + arc.getId() + " could not be matched to any place or transition");	        
	        if (!toNodeFound)
	        	System.out.println("Model error: 'toNode' for arc " + arc.getId() + " could not be matched to any place or transition");
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
		
	
	private void setInitialMarking(){
			
		model.checkGuardArcs();
		model.updateEnabling();
		model.updateFluidRates();
	}
	
}