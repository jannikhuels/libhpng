////		
////		Thread t = new SimulationThread(this, model, maxTime, plots);
////		Thread t2 = new SimulationThread(this, model,maxTime, plots);
////		t.start();
////		t2.start();
////		try {
////			t.join();
////			t2.join();;
////		} catch (InterruptedException e) {
////			// TODO Auto-generated catch block
////			e.printStackTrace();
////		}
//
//
//package de.wwu.criticalsystems.libhpng.simulation;
//
//import java.util.ArrayList;
//import java.util.logging.Logger;
//
//import de.wwu.criticalsystems.libhpng.errorhandling.*;
//import de.wwu.criticalsystems.libhpng.model.HPnGModel;
//import de.wwu.criticalsystems.libhpng.plotting.MarkingPlot;
//
//public class SimulationThread extends Thread {
//	
//	public SimulationThread(SimulationHandler handler, HPnGModel model, Double maxTime, ArrayList<MarkingPlot> plots){
//		this.handler = handler;
//		this.originalModel = model;
//		this.maxTime = maxTime;
//		this.plots = plots;
//	}
//	
//	private SimulationHandler handler;
//	private Double currentTime;
//	private HPnGModel model;
//	private HPnGModel originalModel;
//	private MarkingPlot currentPlot;
//	private Double maxTime;
//	private ArrayList<MarkingPlot> plots;
//	private Simulator simulator;
//		
//	@Override public void run() {
//		
//		Logger logger = handler.getLogger();
//		Boolean printRunResults = handler.getPrintRunResults();
//		Integer fixedNumberOfRuns = handler.getFixedNumberOfRuns();
//				
//		try {
//			model = new HPnGModel(originalModel, logger);
//		} catch (ModelCopyingFailedException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		} catch (InvalidModelConnectionException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
//		
//		simulator = new Simulator(model, maxTime);
//		simulator.setLogger(logger);
//		
//		SampleGenerator generator = new SampleGenerator();
//		generator.initializeRandomStream();
//		if (logger != null)
//			logger.info("Simulation started with plotting continuous places only");
//		if (!printRunResults)
//			System.out.println("Running simulation...");
//				
//		for (int run = 0; run < fixedNumberOfRuns; run++){			
//			
//			//TODO
//			/*if (printRunResults)
//				System.out.println("Starting simulation run no." + (run+1));*/
//			
//			currentTime = 0.0;			
//			try {
//				model.resetMarking();
//				if (printRunResults)
//					model.printCurrentMarking(true, false);
//				generator.sampleGeneralTransitions(model, logger);
//			} catch (InvalidDistributionParameterException e) {
//				//TODO
//				//throw new ModelNotReadableException(e.getLocalizedMessage());				
//			} catch (InvalidRandomVariateGeneratorException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//			
//			currentPlot = new MarkingPlot(maxTime);
//			plots.add(currentPlot);
//			currentPlot.initialize(model);
//			
//			//simulation
//			while (currentTime <= maxTime)
//				try {
//					currentTime = simulator.getAndCompleteNextEvent(currentTime, currentPlot, printRunResults);
//				} catch (InvalidRandomVariateGeneratorException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//			
//			//TODO
//			/*if (printRunResults){
//				System.out.println(maxTime + " seconds: simulation run no." + (run+1) + " completed");			
//				model.printCurrentMarking(false, true);	
//			}*/
//			
//		}
//	  }
//	
//}
////aufruf Thread t = new SimulationThread(); t1.start();
//
////int cores = Runtime.getRuntime().availableProcessors();
