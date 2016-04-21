package de.wwu.criticalsystems.libhpng.model;

import java.util.Comparator;
		
public class ArcComparator implements Comparator<Arc>{

	@Override public int compare(Arc a1, Arc a2){

		try{
			if (a2 == null || a1 == null)
				throw new NullPointerException();
		} catch (Exception e) {
			System.err.println(e);
		}
	 
		int a1Type = 3;
		switch (a1.getClass().getSimpleName()) {
		case "GuardArc":
		    a1Type = 0;
		    break;
		case "ContinuousArc":
		    a1Type = 1;
		    break;
		case "DiscreteArc":
		    a1Type = 2;
		    break;
		}
		
		int a2Type = 3;
		switch (a2.getClass().getSimpleName()) {
		case "GuardArc":
		    a2Type = 0;
		    break;
		case "ContinuousArc":
		    a2Type = 1;
		    break;
		case "DiscreteArc":
		    a2Type = 2;
		    break;
		}	  
	  
		PlaceComparator placeComparator = new PlaceComparator();
		TransitionComparator transitionComparator = new TransitionComparator();
		
		//System.out.println(a1.getId() + "_" + a1Type + "  "+  a2.getId() + "_"+ a2Type);
		
		if (a1Type < a2Type || (a1Type == a2Type && placeComparator.compare(a1.getConnectedPlace(),a2.getConnectedPlace()) == -1) || (a1Type == a2Type && placeComparator.compare(a1.getConnectedPlace(),a2.getConnectedPlace()) == 0 && transitionComparator.compare(a1.getConnectedTransition(),a2.getConnectedTransition()) == -1))
			return -1;

		if (a1Type > a2Type || (a1Type == a2Type && placeComparator.compare(a1.getConnectedPlace(),a2.getConnectedPlace()) == 1) || (a1Type == a2Type && placeComparator.compare(a1.getConnectedPlace(),a2.getConnectedPlace()) == 0 && transitionComparator.compare(a1.getConnectedTransition(),a2.getConnectedTransition()) == 1))
			return 1;
		
		return 0;
  }
}
