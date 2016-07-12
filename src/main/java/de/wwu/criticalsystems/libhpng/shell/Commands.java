package de.wwu.criticalsystems.libhpng.shell;

import org.springframework.shell.core.CommandMarker;
import org.springframework.shell.core.annotation.CliAvailabilityIndicator;
import org.springframework.shell.core.annotation.CliCommand;
import org.springframework.shell.core.annotation.CliOption;
import org.springframework.stereotype.Component;
import de.wwu.criticalsystems.libhpng.Main.*;
import de.wwu.criticalsystems.libhpng.formulaparsing.SimpleNode;

@Component
public class Commands implements CommandMarker {
	
	private ModelHandler handler = new ModelHandler();
	
	@CliAvailabilityIndicator({"read model", "parse formula", "change logfile", "change half interval width", "change half width of indifference region", "change confidence level", "change type 1 error", "change type 2 error", "change fixed number of runs", "change min number of runs", "change max number of runs", "set fixed number of runs", "set optimal number of runs", "set print run results on", "set print run results off", "print simulation parameters"})
	public boolean isAvailable() {
		return true;
	}
	
	@CliAvailabilityIndicator({"check formula"})
	public boolean isCheckFormulaAvailable() {
		if (handler.getModel() == null) return false;
		return true;
	}
	
	@CliAvailabilityIndicator({"plot"})
	public boolean isPlotAvailable() {
		if (handler.getModel() == null) return false;
		return true;
	}	
	
	
	@CliCommand(value = "read model", help = "Reads in an HPnG model")
	public void readModel(
		@CliOption(key = { "path" }, mandatory = true, help = "The path of the xml file containing the HPnG model") final String xmlPath) {		
		
		handler.readModel(xmlPath);		
	}
	
		
	@CliCommand(value = "parse formula", help = "Parse a model checking formula and print the tree structure.")
	public void parseFormula(){
		
		SimpleNode root = handler.readFormula();
		if (root != null)
			root.dump("");		
	}
	
	
	@CliCommand(value = "check formula", help = "Check a model checking formula by simulation the model. ('read model' has to be executed first.)")
	public void checkFormula(){
		
		SimpleNode root = handler.readFormula();
		if (root != null )
			handler.checkFormula(root);		
	}
	
	
	@CliCommand(value = "plot", help = "Plot continuous places by simulating the model. ('read model' has to be executed first.)")
	public void plot(
		@CliOption(key = { "maxtime" }, mandatory = true, help = "The maximum time for the simulation") final Double maxTime) {		
		
		handler.plotPlaces(maxTime);
	}
	
	
	@CliCommand(value = "change logfile", help = "Change the path of the log file")
	public void changeLogFile(
			@CliOption(key = { "path" }, mandatory = true, help = "The new path of the log file") final String logPath){
		
		handler.setLoggerPath(logPath);
	}
	
	
	//Parameter setting:
	
	
	@CliCommand(value = "change half interval width", help = "Change the half width of the confidence interval parameter")
	public void ChangeHalfIntervalWidth(
			@CliOption(key = { "value" }, mandatory = true, help = "The new half width of the confidence interval") final Double halfIntervalWidth){
		
		handler.changeParameter("half interval width", halfIntervalWidth);
	}
	
	
	
	@CliCommand(value = "change half width of indifference region", help = "Change the half width of the indifference region parameter")
	public void ChangeHalflWidthOfIndifferenceRegion(
			@CliOption(key = { "value" }, mandatory = true, help = "The new half width of the indiffrence region") final Double halfWidthOfIndifferenceRegion){
		
		handler.changeParameter("half width of indifference region", halfWidthOfIndifferenceRegion);
	}
	
	
	
	@CliCommand(value = "change confidence level", help = "Change the confidence level parameter")
	public void ChangeConfidenceLevel(
			@CliOption(key = { "value" }, mandatory = true, help = "The new confidence level") final Double confidenceLevel){
		
		handler.changeParameter("confidence level", confidenceLevel);
	}
	
	
	
	@CliCommand(value = "change type 1 error", help = "Change the type 1 error parameter")
	public void ChangeType1Error(
			@CliOption(key = { "value" }, mandatory = true, help = "The new type 1 error") final Double type1Error){
		
		handler.changeParameter("type 1 error", type1Error);
	}
	
	
	
	@CliCommand(value = "change type 2 error", help = "Change the type 2 error parameter")
	public void ChangeType2Error(
			@CliOption(key = { "value" }, mandatory = true, help = "The new type 2 error") final Double type2Error){
		
		handler.changeParameter("type 2 error", type2Error);
	}
	
	
	
	@CliCommand(value = "change fixed number of runs", help = "Change the fixed number of runs parameter")
	public void ChangeFixedNumberOfRuns(
			@CliOption(key = { "value" }, mandatory = true, help = "The new fixed number of runs") final Integer fixedNumberOfRuns){
		
		handler.changeParameter("fixed number of runs", fixedNumberOfRuns);
	}
	
	
	
	@CliCommand(value = "change min number of runs", help = "Change the minimum number of runs parameter")
	public void ChangeMinNumberOfRuns(
			@CliOption(key = { "value" }, mandatory = true, help = "The new minimum number of runs") final Integer minNumberOfRuns){
		
		handler.changeParameter("min number of runs", minNumberOfRuns);
	}
	
	
	
	@CliCommand(value = "change max number of runs", help = "Change the maximum number of runs parameter")
	public void ChangeMaxNumberOfRuns(
			@CliOption(key = { "value" }, mandatory = true, help = "The new maximum number of runs") final Integer maxNumberOfRuns){
		
		handler.changeParameter("max number of runs", maxNumberOfRuns);
	}
	
	
	
	@CliCommand(value = "set fixed number of runs", help = "Set that the simulation for the property check should run with a fixed number of runs")
	public void ChangeSimulationWithFixedNumberOfRuns(){			
		handler.changeParameter("simulation with fixed number of runs", true);
	}
	
	@CliCommand(value = "set optimal number of runs", help = "Set that the simulation for the property check should run with the optimal number of runs")
	public void ChangeSimulationWitOptimalNumberOfRuns(){			
		handler.changeParameter("simulation with fixed number of runs", false);
	}
	
	
	
	@CliCommand(value = "set print run results on", help = "Enable that the results of the single simulation runs are printed to the console")
	public void ChangePrintRunResultsOn(){			
		handler.changeParameter("print run results", true);
	}
	
	@CliCommand(value = "set print run results off", help = "Disable that the results of the single simulation runs are printed to the console")
	public void ChangePrintRunResultsOff(){			
		handler.changeParameter("print run results", false);
	}
	
	

	@CliCommand(value = "print simulation parameters", help = "Print all simulation parameters and settings")
	public void PrintParameters(){			
		handler.printParameters();	
	}

	
} 
