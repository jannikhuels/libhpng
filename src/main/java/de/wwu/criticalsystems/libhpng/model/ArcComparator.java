package de.wwu.criticalsystems.libhpng.model;

import java.util.Comparator;


public class ArcComparator implements Comparator<Arc> {

    //compare arcs by 1. type, 2. priority, 3. place comparison, 4. transition comparison

    @Override
    public int compare(Arc a1, Arc a2) {

        try {
            if (a2 == null || a1 == null)
                throw new NullPointerException();
        } catch (Exception e) {
            System.err.println(e);
        }

        int a1Type = 3;
        int a1Prio = 0;
        switch (a1.getClass().getSimpleName()) {
            case "GuardArc":
            case "GuardArcVar":
                a1Type = 0;
                break;
            case "ContinuousArc":
                a1Type = 1;
                a1Prio = ((ContinuousArc) a1).getPriority();
                break;
            case "DiscreteArc":
                a1Type = 2;
                break;
        }

        int a2Type = 3;
        int a2Prio = 0;
        switch (a2.getClass().getSimpleName()) {
            case "GuardArc":
            case "GuardArcVar":
                a2Type = 0;
                break;
            case "ContinuousArc":
                a2Type = 1;
                a2Prio = ((ContinuousArc) a2).getPriority();
                break;
            case "DiscreteArc":
                a2Type = 2;
                break;
        }

        PlaceComparator placeComparator = new PlaceComparator();
        TransitionComparator transitionComparator = new TransitionComparator();


        if (a1Type < a2Type || (a1Type == a2Type && a1Prio > a2Prio) || (a1Type == a2Type && a1Prio == a2Prio && placeComparator.compare(a1.getConnectedPlace(), a2.getConnectedPlace()) == -1) || (a1Type == a2Type && a1Prio == a2Prio && placeComparator.compare(a1.getConnectedPlace(), a2.getConnectedPlace()) == 0 && transitionComparator.compare(a1.getConnectedTransition(), a2.getConnectedTransition()) == -1))
            return -1;

        if (a1Type > a2Type || (a1Type == a2Type && a1Prio < a2Prio) || (a1Type == a2Type && a1Prio == a2Prio && placeComparator.compare(a1.getConnectedPlace(), a2.getConnectedPlace()) == 1) || (a1Type == a2Type && a1Prio == a2Prio && placeComparator.compare(a1.getConnectedPlace(), a2.getConnectedPlace()) == 0 && transitionComparator.compare(a1.getConnectedTransition(), a2.getConnectedTransition()) == 1))
            return 1;


        return 0;
    }
}
