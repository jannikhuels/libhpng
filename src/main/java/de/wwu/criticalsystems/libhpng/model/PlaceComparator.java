package de.wwu.criticalsystems.libhpng.model;

import java.util.Comparator;
		
public class PlaceComparator implements Comparator<Place>{

	@Override public int compare(Place p1, Place p2){
		
		try{
			if (p2 == null || p1 == null)
				throw new NullPointerException();
		} catch (Exception e) {
			System.err.println(e);
		}
		  
		if (p1.getClass().equals(ContinuousPlace.class) && p2.getClass().equals(DiscretePlace.class))
			return -1;
	
		if (p2.getClass().equals(ContinuousPlace.class) && p1.getClass().equals(DiscretePlace.class))
			return 1;
		  
		return 0;
	}
}
