package de.wwu.criticalsystems.libhpng.simulation;

import de.wwu.criticalsystems.libhpng.errorhandling.InvalidPropertyException;
import de.wwu.criticalsystems.libhpng.errorhandling.InvalidRandomVariateGeneratorException;
import de.wwu.criticalsystems.libhpng.model.*;
import de.wwu.criticalsystems.libhpng.plotting.MarkingPlotVar;
import de.wwu.criticalsystems.libhpng.simulation.SimulationEvent.SimulationEventType;
import org.apache.commons.math3.ode.FirstOrderIntegrator;
import org.apache.commons.math3.ode.nonstiff.*;

import java.util.Collection;
import java.util.Random;
import java.util.logging.Logger;

public class SimulatorVar {


    public SimulatorVar(HPnGModelVar model, Double maxTime, SimulationHandlerVar simulationHandlerVar) {
        this.model = model;
        this.maxTime = maxTime;
        this.simulationHandlerVar = simulationHandlerVar;

        switch(simulationHandlerVar.getNumericSolverID()){
            case 0:
                integrator = new ClassicalRungeKuttaIntegrator(1.0e-1);
                break;
            case 1:
                integrator= new EulerIntegrator(simulationHandlerVar.getFixedStepSize());
                break;
            case 2:
                integrator = new MidpointIntegrator(simulationHandlerVar.getFixedStepSize());
                break;
            case 3:
                integrator = new GillIntegrator(simulationHandlerVar.getFixedStepSize());
                break;
            case 4:
                integrator = new ThreeEighthesIntegrator(simulationHandlerVar.getFixedStepSize());
                break;
            case 5:
                integrator = new LutherIntegrator(simulationHandlerVar.getFixedStepSize());
                break;
            case 6:
                integrator = new DormandPrince54Integrator(simulationHandlerVar.getMinStep(),simulationHandlerVar.getMaxStep(),simulationHandlerVar.getScalAbsoluteTolerance(), simulationHandlerVar.getScalRelativeTolerance());
                break;
            case 7:
                integrator = new HighamHall54Integrator(simulationHandlerVar.getMinStep(),simulationHandlerVar.getMaxStep(),simulationHandlerVar.getScalAbsoluteTolerance(), simulationHandlerVar.getScalRelativeTolerance());
                break;
            case 8:
                integrator = new DormandPrince853Integrator(simulationHandlerVar.getMinStep(),simulationHandlerVar.getMaxStep(),simulationHandlerVar.getScalAbsoluteTolerance(), simulationHandlerVar.getScalRelativeTolerance());
                break;
            case 9:
                integrator = new GraggBulirschStoerIntegrator(simulationHandlerVar.getMinStep(),simulationHandlerVar.getMaxStep(),simulationHandlerVar.getScalAbsoluteTolerance(), simulationHandlerVar.getScalRelativeTolerance());
        }
        ode = new ODESystem(model);
    }


    public void setLogger(Logger logger) {
        this.logger = logger;
    }


    protected SimulationEvent event;
    protected HPnGModelVar model;
    protected Double maxTime;
    protected Logger logger;
    protected FirstOrderIntegrator integrator;
    //protected FirstOrderIntegrator integrator;
    //TODO initialize
    protected SimulationHandlerVar simulationHandlerVar;
    protected ODESystem ode;


    public Double getAndCompleteNextEvent(Double currentTime, MarkingPlotVar currentPlot, Boolean printRunResults) throws InvalidRandomVariateGeneratorException, InvalidPropertyException {
        integrator.clearEventHandlers();
        integrator.clearStepHandlers();
        double[] result = new double[ode.getDimension()];

//        model.updateFluidRates(false);
        getNextEvent(currentTime);
        System.out.println("time" + currentTime);
        ODEStepHandler stepHandler = new ODEStepHandler(ode, currentPlot);
        integrator.addStepHandler(stepHandler);
        integrator.integrate(ode, currentTime, ode.getCurrentFluidLevels(), event.getOccurenceTime(), result);
        model.advanceMarking(event.getOccurenceTime());
        model.updateEnabling(false);//TODO richtig?
        //complete event and update model marking
        if (maxTime < event.getOccurenceTime() || event.getEventType().equals(SimulationEventType.no_event)) {
//			if (maxTime- currentTime > 0.0)
//				model.advanceMarking(maxTime- currentTime);
            model.updateFluidRates(false);
            ode.updateODESystem();
            currentPlot.saveAll(maxTime);


        } else {

            completeEvent(printRunResults, currentPlot);

            if (printRunResults)
                model.printCurrentMarking(false, false);
        }
//        currentPlot.saveAll(currentTime);

        return event.getOccurenceTime();
    }


    protected void completeEvent(Boolean printRunResults, MarkingPlotVar currentPlot) throws InvalidRandomVariateGeneratorException {

        if (event.getEventType().equals(SimulationEventType.immediate_transition) || event.getEventType().equals(SimulationEventType.deterministic_transition) || event.getEventType().equals(SimulationEventType.general_transition)) {

            //transition firing
            Transition transition = conflictResolutionByTransitionWeight();
            transition.fireTransition();
            model.checkGuardArcsForDiscretePlaces();
            transition.setEnabled(false);

            if (event.getEventType().equals(SimulationEventType.general_transition)) {
                if (printRunResults)
                    System.out.println(event.getOccurenceTime() + " seconds: General transition " + transition.getId() + " is fired for the " + ((GeneralTransition) transition).getFirings() + ". time");
            } else if (event.getEventType().equals(SimulationEventType.immediate_transition)) {
                if (printRunResults)
                    System.out.println(event.getOccurenceTime() + " seconds: Immediate transition " + transition.getId() + " is fired");
            } else if (event.getEventType().equals(SimulationEventType.deterministic_transition)) {
                if (printRunResults)
                    System.out.println(event.getOccurenceTime() + " seconds: Deterministic transition " + transition.getId() + " is fired");
            }

        } else if (event.getEventType().equals(SimulationEventType.guard_arcs_immediate) || event.getEventType().equals(SimulationEventType.guard_arcs_continuous) || event.getEventType().equals(SimulationEventType.guard_arcs_deterministic)) {

            //guard arc condition
            GuardArc arc;
            for (Object object : event.getRelatedObjects()) {

                arc = (GuardArc) object;
                Boolean fulfilled = arc.checkCondition();

                if (printRunResults && fulfilled && !arc.getInhibitor())
                    System.out.println(event.getOccurenceTime() + " seconds: test arc " + arc.getId() + " has its condition fulfilled for transition " + arc.getConnectedTransition().getId());
                else if (printRunResults && !fulfilled && !arc.getInhibitor())
                    System.out.println(event.getOccurenceTime() + " seconds: test arc " + arc.getId() + " has its condition stopped being fulfilled for transition " + arc.getConnectedTransition().getId());
                else if (printRunResults && fulfilled && arc.getInhibitor())
                    System.out.println(event.getOccurenceTime() + " seconds: inhibitor arc " + arc.getId() + " has its condition fulfilled for transition " + arc.getConnectedTransition().getId());
                else if (printRunResults && !fulfilled && arc.getInhibitor())
                    System.out.println(event.getOccurenceTime() + " seconds: inhibitor arc " + arc.getId() + " has its condition stopped being fulfilled for transition " + arc.getConnectedTransition().getId());
            }
        } else if (event.getEventType().equals(SimulationEventType.place_boundary)) {

            //place boundary reached
            ContinuousPlaceVar place;
            for (Object object : event.getRelatedObjects()) {

                place = (ContinuousPlaceVar) object;
//                place.setExactFluidLevel(place.getCurrentFluidLevel(), event.getOccurenceTime());

                if (place.checkLowerBoundary()) {
                    place.checkUpperBoundary();
                    if (printRunResults)
                        System.out.println(event.getOccurenceTime() + " seconds: continuous place " + place.getId() + " is empty");
                } else {
                    place.checkUpperBoundary();
                    if (printRunResults)
                        System.out.println(event.getOccurenceTime() + " seconds: continuous place " + place.getId() + " has reached its upper boundary");
                }
            }
        }
/*        else if (event.getEventType().equals(SimulationEventType.place_internaltransition)) {

            ContinuousPlace place;

            //internal transition
            for (Object object : event.getRelatedObjects()) {

                place = ((ContinuousPlace) object);

                Boolean lowerBoundary = place.getLowerBoundaryReached();
                Boolean upperBoundary = false;
                if (!place.getUpperBoundaryInfinity())
                    upperBoundary = place.getUpperBoundaryReached();


                place.performInternalTransition(event.getOccurenceTime(), place.getExactDrift(), place.getChangeOfExactDrift(), model.getArcs());

                if (printRunResults)
                    System.out.println(event.getOccurenceTime() + " seconds: continuous place " + place.getId() + " has performed an internal transition");


                if (!lowerBoundary && place.checkLowerBoundary()) {
                    place.checkUpperBoundary();
                    if (printRunResults)
                        System.out.println(event.getOccurenceTime() + " seconds: continuous place " + place.getId() + " is empty");
                } else if (!upperBoundary && place.checkUpperBoundary()) {
                    if (printRunResults)
                        System.out.println(event.getOccurenceTime() + " seconds: continuous place " + place.getId() + " has reached its upper boundary");
                }


                for (Arc arc : model.getArcs()) {

                    if (arc.getClass().equals(GuardArc.class) && arc.getConnectedPlace().equals(place)) {
                        //guard arc starting from fluid place

                        if (!((GuardArc) arc).getConditionFulfilled().equals(((GuardArc) arc).checkCondition())) {
                            Boolean fulfilled = ((GuardArc) arc).getConditionFulfilled();

                            if (printRunResults && fulfilled && !((GuardArc) arc).getInhibitor())
                                System.out.println(event.getOccurenceTime() + " seconds: test arc " + arc.getId() + " has its condition fulfilled for transition " + arc.getConnectedTransition().getId());
                            else if (printRunResults && !fulfilled && !((GuardArc) arc).getInhibitor())
                                System.out.println(event.getOccurenceTime() + " seconds: test arc " + arc.getId() + " has its condition stopped being fulfilled for transition " + arc.getConnectedTransition().getId());
                            else if (printRunResults && fulfilled && ((GuardArc) arc).getInhibitor())
                                System.out.println(event.getOccurenceTime() + " seconds: inhibitor arc " + arc.getId() + " has its condition fulfilled for transition " + arc.getConnectedTransition().getId());
                            else if (printRunResults && !fulfilled && ((GuardArc) arc).getInhibitor())
                                System.out.println(event.getOccurenceTime() + " seconds: inhibitor arc " + arc.getId() + " has its condition stopped being fulfilled for transition " + arc.getConnectedTransition().getId());

                        }
                    }
                }


            }
        }*/
        System.out.println(event.getEventType());
        boolean boundaryHit = false;
        if (event.getEventType().equals(SimulationEventType.place_boundary))
            boundaryHit = true;

        //update model status
        model.updateEnabling(false);
        model.updateFluidRates(boundaryHit);
        ode.updateODESystem();

//        if (event.getEventType() != SimulationEventType.place_internaltransition)
////            model.computeInternalTransitionsAtEvent();
//
//            //plot status
//            currentPlot.saveAll(event.getOccurenceTime());
    }


    protected void getNextEvent(Double currentTime) {
        Double timeOfCurrentEvent;
        event = new SimulationEvent(maxTime);
        SimulationEventType eventType;

        //check transition events first
        for (Transition transition : model.getTransitions()) {
            if (!transition.getEnabled())
                continue;

            if (transition.getClass().equals(ImmediateTransition.class)) {

                if (((ImmediateTransition) transition).getPriority() > event.getPriority()) {
                    event.setEventType(SimulationEventType.immediate_transition);
                    event.setFirstEventItem(transition, ((ImmediateTransition) transition).getPriority());
                    event.setOccurenceTime(currentTime);
                } else if (((ImmediateTransition) transition).getPriority().equals(event.getPriority())) {
                    event.getRelatedObjects().add(transition);
                } else
                    break; //if immediate transition with higher priority found, transition loop can be exited here
            } else if (transition.getClass().equals(DeterministicTransition.class)) {

                if (event.getEventType().equals(SimulationEventType.immediate_transition))
                    break;

                timeOfCurrentEvent = currentTime + ((DeterministicTransition) transition).getFiringTime() - ((DeterministicTransition) transition).getClock();

                if (timeOfCurrentEvent < event.getOccurenceTime() || (timeOfCurrentEvent.equals(event.getOccurenceTime()) && ((DeterministicTransition) transition).getPriority() > event.getPriority())) {
                    event.setEventType(SimulationEventType.deterministic_transition);
                    event.setFirstEventItem(transition, ((DeterministicTransition) transition).getPriority());
                    event.setOccurenceTime(timeOfCurrentEvent);
                } else if (timeOfCurrentEvent.equals(event.getOccurenceTime()) && ((DeterministicTransition) transition).getPriority().equals(event.getPriority())) {
                    event.getRelatedObjects().add(transition);
                }
            } else if (transition.getClass().equals(GeneralTransition.class)) {

                if (event.getEventType().equals(SimulationEventType.immediate_transition))
                    break;

                timeOfCurrentEvent = currentTime + ((GeneralTransition) transition).getDiscreteFiringTime() - ((GeneralTransition) transition).getEnablingTime();

                if (timeOfCurrentEvent < event.getOccurenceTime() || (timeOfCurrentEvent.equals(event.getOccurenceTime()) && !(event.getEventType().equals(SimulationEventType.deterministic_transition)) && ((GeneralTransition) transition).getPriority() > event.getPriority())) {
                    event.setEventType(SimulationEventType.general_transition);
                    event.setFirstEventItem(transition, ((GeneralTransition) transition).getPriority());
                    event.setOccurenceTime(timeOfCurrentEvent);
                } else if (timeOfCurrentEvent.equals(event.getOccurenceTime()) && ((GeneralTransition) transition).getPriority().equals(event.getPriority())) {
                    event.getRelatedObjects().add(transition);
                }
            } else
                break; //continuous or continuous dynamic transition
        }


        if (!event.getEventType().equals(SimulationEventType.immediate_transition)) {

            //if no immediate transition, check guard arcs next
            for (Arc arc : model.getArcs()) {


                if (arc.getClass().equals(GuardArc.class) && arc.getConnectedPlace().getClass().equals(ContinuousPlaceVar.class)) {
                    //guard arc starting from fluid place

                    ContinuousPlaceVar place = ((ContinuousPlaceVar) arc.getConnectedPlace());
                    //determine possible event type

                    if (arc.getConnectedTransition().getClass().equals(ImmediateTransition.class))
                        eventType = SimulationEventType.guard_arcs_immediate;
                    else if (arc.getConnectedTransition().getClass().equals(ContinuousTransition.class))
                        eventType = SimulationEventType.guard_arcs_continuous;
                    else
                        eventType = SimulationEventType.guard_arcs_deterministic;


                    // create event that stops simulation if event occurs first
                    integrator.addEventHandler(new ODEEventHandler(event.getOccurenceTime(), event, eventType, arc, ode, place.getId() + "-" + arc.getWeight()), 1.0e-2, 0.01, 10000);


                } else if (!arc.getClass().equals(GuardArc.class))
                    break;
            }

            //check continuous places for borders
            for (Place p : model.getPlaces()) {
                if (p.getClass().equals(ContinuousPlaceVar.class)) {

                    ContinuousPlaceVar place = (ContinuousPlaceVar) p;
                    //upper bound
                    if (!place.getUpperBoundaryInfinity())
                        integrator.addEventHandler(new ODEEventHandler(event.getOccurenceTime(), event, SimulationEventType.place_boundary, place, ode, place.getId() + "-" + place.getUpperBoundary()), 1.0e-2, 0.01, 10000);
                    //lower bound
                    integrator.addEventHandler(new ODEEventHandler(event.getOccurenceTime(), event, SimulationEventType.place_boundary, place, ode, place.getId()), 1.0e-3, 0.01, 10000);
                    //TODO angucken wohin damit
                    String placeCondition = place.getRateAdaptionCondition().getExpressionString();
                    if (!placeCondition.equals(""))
                        integrator.addEventHandler(new ODEEventHandler(event.getOccurenceTime(), event, SimulationEventType.place_internaltransition, place, ode, placeCondition), 1.0e-2, 0.01, 10000);
//						if (timeDelta == 0.0 && place.getCurrentFluidLevel() == 0.0)
//							place.checkLowerBoundary();
//						else if (timeDelta == 0.0)
//							place.checkUpperBoundary();


                }
            }
            for (Transition transition : model.getTransitions()) {
                if (transition.getClass().equals(DynamicContinuousTransitionVar.class) || transition.getClass().equals(ContinuousTransitionVar.class)) {
                    Collection<String> events = ((FluidTransition) transition).getRateAdapationEvents().values();
                    for (String transitionEvent : events) {
                        integrator.addEventHandler(new ODEEventHandler(event.getOccurenceTime(), event, SimulationEventType.place_internaltransition, transition, ode, transitionEvent), 1.0e-2, 0.01, 10000);
                    }
                }

            }
        }
    }

    private Transition conflictResolutionByTransitionWeight() {

        Double sum = 0.0;
        Double probability = 0.0;
        Double winner = new Random().nextDouble();

        if (event.getEventType().equals(SimulationEventType.immediate_transition)) {
            for (Object object : event.getRelatedObjects())
                sum += ((ImmediateTransition) object).getWeight();

            for (Object object : event.getRelatedObjects()) {
                probability += ((ImmediateTransition) object).getWeight() / sum;
                if (winner < probability)
                    return ((Transition) object);
            }
        } else if (event.getEventType().equals(SimulationEventType.deterministic_transition)) {
            for (Object object : event.getRelatedObjects())
                sum += ((DeterministicTransition) object).getWeight();

            for (Object object : event.getRelatedObjects()) {
                probability += ((DeterministicTransition) object).getWeight() / sum;
                if (winner < probability)
                    return ((Transition) object);
            }
        } else if (event.getEventType().equals(SimulationEventType.general_transition)) {
            for (Object object : event.getRelatedObjects())
                sum += ((GeneralTransition) object).getWeight();

            for (Object object : event.getRelatedObjects()) {
                probability += ((GeneralTransition) object).getWeight() / sum;
                if (winner < probability)
                    return ((Transition) object);
            }
        }

        return null;
    }
}