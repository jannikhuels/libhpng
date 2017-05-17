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
	
	@CliAvailabilityIndicator({"read", "parse", "change logfile", "change guess", "change testruns", "set algorithm", "change halfintervalwidth", "change halfwidthindifferenceregion", "change confidencelevel", "change type1error", "change type2error", "change fixedruns", "change minruns", "change maxruns", "set fixedruns", "set optimalruns", "printresults on", "printresults off", "printparameters", "loadparameters", "storeparameters"})
	public boolean isAvailable() {
		return true;
	}
	
	@CliAvailabilityIndicator({"check"})
	public boolean isCheckFormulaAvailable() {
		if (handler.getModel() == null) return false;
		return true;
	}
	
	@CliAvailabilityIndicator({"plot"})
	public boolean isPlotAvailable() {
		if (handler.getModel() == null) return false;
		return true;
	}	
	
	
	@CliCommand(value = "read", help = "Reads in an HPnG model")
	public void readModel(
		@CliOption(key = { "p" }, mandatory = true, help = "The path of the xml file containing the HPnG model") final String xmlPath) {		
		
		handler.readModel(xmlPath);		
	}
	
		
	@CliCommand(value = "parse", help = "Parse an STL formula and print the tree structure.")
	public void parseFormula(){
		
		SimpleNode root = handler.readFormula();
		if (root != null)
			root.dump("");		
	}
	
	
	@CliCommand(value = "check", help = "Check an STL formula by simulating the HPnG model. ('read model' has to be executed first.)")
	public void checkFormula(){
		
		SimpleNode root = handler.readFormula();
		if (root != null )
			handler.checkFormula(root);		
	}
	
	
	@CliCommand(value = "plot", help = "Plot continuous places by simulating the HPnG model. ('read model' has to be executed first.)")
	public void plot(
		@CliOption(key = { "t" }, mandatory = true, help = "The maximum time for the simulation") final Double maxTime) {		
		
		handler.plotPlaces(maxTime);
	}
	
	
	@CliCommand(value = "change logfile", help = "Change the path of the log file")
	public void changeLogFile(
			@CliOption(key = { "n" }, mandatory = true, help = "The new path of the log file") final String logPath){
		
		handler.setLoggerPath(logPath);
	}
	
	
	//Parameter setting:
	
	
	@CliCommand(value = "change halfintervalwidth", help = "Change the half width of the confidence interval parameter")
	public void ChangeHalfIntervalWidth(
			@CliOption(key = { "n" }, mandatory = true, help = "The new half width of the confidence interval") final Double halfIntervalWidth){
		
		handler.changeParameter((byte)0, halfIntervalWidth);
	}
	
	
	
	@CliCommand(value = "change guess", help = "Change the value of the guess parameter (|p-p0|)")
	public void ChangeGuessValue(
			@CliOption(key = { "n" }, mandatory = true, help = "The new value of the guess parameter") final Double guess){
		
		handler.changeParameter((byte)11, guess);
	}
	
	
	
	@CliCommand(value = "change testruns", help = "Change the number of runs for hypothesis testing")
	public void ChangeNumberOfTestRuns(
			@CliOption(key = { "n" }, mandatory = true, help = "The new number of runs for hypothesis testing") final Integer testRuns){
		
		handler.changeParameter((byte)12, testRuns);
	}
	
	
	
	@CliCommand(value = "set hypothesis algorithm", help = "Set algorithm used for hypothesis testing (SPR, GCI, CR, Azuma)")
	public void setAlgorithm(
			@CliOption(key = { "n" }, mandatory = true, help = "The abbreviation of the used algorithm") final String algorithmName){
		
		handler.changeParameter((byte)10, algorithmName);		
	}
	
	
	
	@CliCommand(value = "change halfwidthindifferenceregion", help = "Change the half width of the indifference region parameter")
	public void ChangeHalflWidthOfIndifferenceRegion(
			@CliOption(key = { "n" }, mandatory = true, help = "The new half width of the indiffrence region") final Double halfWidthOfIndifferenceRegion){
		
		handler.changeParameter((byte)1, halfWidthOfIndifferenceRegion);
	}
	
	
		
	@CliCommand(value = "change confidencelevel", help = "Change the confidence level parameter")
	public void ChangeConfidenceLevel(
			@CliOption(key = { "n" }, mandatory = true, help = "The new confidence level") final Double confidenceLevel){
		
		handler.changeParameter((byte)2, confidenceLevel);
	}
	
	
	
	@CliCommand(value = "change type1error", help = "Change the type 1 error parameter")
	public void ChangeType1Error(
			@CliOption(key = { "n" }, mandatory = true, help = "The new type 1 error") final Double type1Error){
		
		handler.changeParameter((byte)3, type1Error);
	}
	
	
	
	@CliCommand(value = "change type2error", help = "Change the type 2 error parameter")
	public void ChangeType2Error(
			@CliOption(key = { "n" }, mandatory = true, help = "The new type 2 error") final Double type2Error){
		
		handler.changeParameter((byte)4, type2Error);
	}
	
	
	
	@CliCommand(value = "change fixedruns", help = "Change the fixed number of runs parameter")
	public void ChangeFixedNumberOfRuns(
			@CliOption(key = { "n" }, mandatory = true, help = "The new fixed number of runs") final Integer fixedNumberOfRuns){
		
		handler.changeParameter((byte)5, fixedNumberOfRuns);
	}
	
	
	
	@CliCommand(value = "change minruns", help = "Change the minimum number of runs parameter")
	public void ChangeMinNumberOfRuns(
			@CliOption(key = { "n" }, mandatory = true, help = "The new minimum number of runs") final Integer minNumberOfRuns){
		
		handler.changeParameter((byte)6, minNumberOfRuns);
	}
	
	
	
	@CliCommand(value = "change maxruns", help = "Change the maximum number of runs parameter")
	public void ChangeMaxNumberOfRuns(
		@CliOption(key = { "n" }, mandatory = true, help = "The new maximum number of runs") final Integer maxNumberOfRuns){
		
		handler.changeParameter((byte)7, maxNumberOfRuns);
	}
	
	
	
	@CliCommand(value = "set fixedruns", help = "Set the simulation to run with a fixed number of runs")
	public void ChangeSimulationWithFixedNumberOfRuns(){			
		
		handler.changeParameter((byte)8, true);
	}
	
	@CliCommand(value = "set optimalruns", help = "Set the simulation to run with the optimal number of runs")
	public void ChangeSimulationWitOptimalNumberOfRuns(){			
		
		handler.changeParameter((byte)8, false);
	}
	
	
	
	@CliCommand(value = "printresults on", help = "Enable that the results of the single simulation runs are printed to the console")
	public void ChangePrintRunResultsOn(){			
		
		handler.changeParameter((byte)9, true);
	}
	
	@CliCommand(value = "printresults off", help = "Disable that the results of the single simulation runs are printed to the console")
	public void ChangePrintRunResultsOff(){			
		
		handler.changeParameter((byte)9, false);
	}
	
	

	@CliCommand(value = "printparameters", help = "Print all simulation parameters and settings")
	public void PrintParameters(){			
		
		handler.printParameters();	
	}
	
	
	
	@CliCommand(value = "loadparameters", help = "Load all simulation parameters and settings from the configuration file")
	public void LoadParameters(){			
		
		handler.loadParameters();	
	}
	
	@CliCommand(value = "storeparameters", help = "Store all simulation parameters and settings into the configuration file")
	public void StoreParameters(){			
		
		handler.storeParameters();	
	}
	
} 
