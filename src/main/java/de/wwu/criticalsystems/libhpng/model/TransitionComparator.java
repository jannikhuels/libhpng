package de.wwu.criticalsystems.libhpng.model;

import java.util.Comparator;
		
public class TransitionComparator implements Comparator<Transition>{

	@Override public int compare(Transition t1, Transition t2){

		try{
			if (t2 == null || t1 == null)
				throw new NullPointerException();
		} catch (Exception e) {
			System.err.println(e);
		}
	 
		int t1Type = 5;
		int t1Prio = 0;
		switch (t1.getClass().getSimpleName()) {
		case "ImmediateTransition":
		    t1Type = 0;
		    t1Prio = ((ImmediateTransition)t1).getPriority();
		    break;
		case "DeterministicTransition":
		    t1Type = 1;
		    t1Prio = ((DeterministicTransition)t1).getPriority();
		    break;
		case "GeneralTransition":
		    t1Type = 2;
		    t1Prio = ((GeneralTransition)t1).getPriority();
		    break;
		case "ContinuousTransition":
		    t1Type = 3;
		    break;
		case "DynamicContinuousTransition":
		    t1Type = 4;
		    break;
		}
		
		int t2Type = 5;
		int t2Prio = 0;
		switch (t2.getClass().getSimpleName()) {
		case "ImmediateTransition":
		    t2Type = 0;
		    t2Prio = ((ImmediateTransition)t2).getPriority();
		    break;
		case "DeterministicTransition":
		    t2Type = 1;
		    t2Prio = ((DeterministicTransition)t2).getPriority();
		    break;
		case "GeneralTransition":
		    t2Type = 2;
		    t2Prio = ((GeneralTransition)t2).getPriority();
		    break;
		case "ContinuousTransition":
		    t2Type = 3;
		    break;
		case "DynamicContinuousTransition":
		    t2Type = 4;
		    break;
		}
	  
	  
		if (t1Type < t2Type || (t1Type == t2Type && t1Prio > t2Prio))
			return -1;
	
		if (t1Type > t2Type|| (t1Type == t2Type && t1Prio > t2Prio))
			return 1;
	  
		return 0;
	}
}
