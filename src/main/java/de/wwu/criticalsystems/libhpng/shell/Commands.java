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
	
	@CliAvailabilityIndicator({"read model","change logfile", "parse formula"})
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
	public void parse(
			@CliOption(key = { "path" }, mandatory = true, help = "The new path of the log file") final String logPath){
		
		handler.setLoggerPath(logPath);
	}
	
}
