package de.wwu.criticalsystems.libhpng.simulation;

import de.wwu.criticalsystems.libhpng.model.*;
import org.apache.commons.math3.ode.events.EventHandler;
import org.apache.commons.math3.ode.events.EventHandler.Action;
import org.mariuszgromada.math.mxparser.Argument;
import org.mariuszgromada.math.mxparser.Expression;


import java.util.EventObject;

public class ODEEventHandler implements EventHandler {
    double maxTime;
    SimulationEvent event;
    SimulationEvent.SimulationEventType eventType;
    Object object;
    Expression condition;
    ODESystem ode;

    public ODEEventHandler(double maxTime, SimulationEvent event, SimulationEvent.SimulationEventType eventType, Object object, ODESystem ode, String conditionString) {
        this.maxTime = maxTime;
        this.event = event;
        this.eventType = eventType;
        this.object = object;
        this.condition = new Expression(conditionString);
        this.ode = ode;
    }


    @Override
    public void init(double v, double[] doubles, double v1) {

    }

    @Override
    public double g(double v, double[] doubles) {
//        System.out.println(doubles[equation] - boundary);
        condition.removeAllArguments();
        for (String argument : condition.getMissingUserDefinedArguments()) {
            condition.addArguments(new Argument(argument, doubles[ode.getIdToInt().get(argument)]));
        }
        return condition.calculate();
    }

    @Override
    public Action eventOccurred(double v, double[] doubles, boolean b) {
        if (v < event.getOccurenceTime()) {
            event.setEventType(eventType);
            event.setOccurenceTime(v);
            event.setFirstEventItem(object, 0);
        } else if (v == event.getOccurenceTime()) {
            if (object.equals(Arc.class)) {
                Arc arc = (Arc) object;
                if ((!event.getEventType().equals(SimulationEvent.SimulationEventType.guard_arcs_immediate) && arc.getConnectedTransition().getClass().equals(ImmediateTransition.class))) {

                    //guard arc for immediate transition replaces other guard arcs
                    event.setEventType(SimulationEvent.SimulationEventType.guard_arcs_immediate);
                    event.setFirstEventItem(arc, 0);

                } else if (event.getEventType().equals(SimulationEvent.SimulationEventType.guard_arcs_deterministic) && arc.getConnectedTransition().getClass().equals(ContinuousTransition.class)) {

                    //guard arc for continuous transition replaces guard arcs for deterministic/general transitions
                    event.setEventType(SimulationEvent.SimulationEventType.guard_arcs_continuous);
                    event.setFirstEventItem(arc, 0);

                } else if ((event.getEventType().equals(SimulationEvent.SimulationEventType.guard_arcs_immediate) && arc.getConnectedTransition().getClass().equals(ImmediateTransition.class))
                        || (event.getEventType().equals(SimulationEvent.SimulationEventType.guard_arcs_continuous) && arc.getConnectedTransition().getClass().equals(ContinuousTransition.class))
                        || (event.getEventType().equals(SimulationEvent.SimulationEventType.guard_arcs_deterministic) && (arc.getConnectedTransition().getClass().equals(DeterministicTransition.class) || arc.getConnectedTransition().getClass().equals(GeneralTransition.class)))) {

                    //otherwise, if same kind of transitions, add to list
                    event.getRelatedObjects().add(arc);
                }
            } else if (object.equals(ContinuousPlaceVar.class)) {
                if (v < event.getOccurenceTime()) {

                    event.setEventType(SimulationEvent.SimulationEventType.place_boundary);
                    event.setFirstEventItem(object, 0);
                    event.setOccurenceTime(v);
                    ((ContinuousPlaceVar) object).checkUpperBoundary();
                    ((ContinuousPlaceVar) object).checkLowerBoundary();
                } else if (v == (event.getOccurenceTime())) {

                    if (event.getEventType().equals(SimulationEvent.SimulationEventType.no_event) || event.getEventType().equals(SimulationEvent.SimulationEventType.general_transition) || event.getEventType().equals(SimulationEvent.SimulationEventType.guard_arcs_deterministic) || event.getEventType().equals(SimulationEvent.SimulationEventType.guard_arcs_continuous)) {
                        event.setEventType(SimulationEvent.SimulationEventType.place_boundary);
                        event.setFirstEventItem(object, 0);
                        ((ContinuousPlaceVar) object).checkUpperBoundary();
                        ((ContinuousPlaceVar) object).checkLowerBoundary();
                    } else if (event.getEventType().equals(SimulationEvent.SimulationEventType.place_boundary)) {
                        event.getRelatedObjects().add(object);
                    }
                }

            }
        }
        //TODO systemout
//        System.out.println("time: " + v + ", a: " + doubles[0] + ", b: " + doubles[1]);
        return Action.STOP;
    }

    @Override
    public void resetState(double v, double[] doubles) {

    }
}
