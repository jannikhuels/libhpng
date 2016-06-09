package de.wwu.criticalsystems.libhpng.shell;

import org.springframework.shell.core.CommandMarker;
import org.springframework.shell.core.annotation.CliAvailabilityIndicator;
import org.springframework.shell.core.annotation.CliCommand;
import org.springframework.shell.core.annotation.CliOption;
import org.springframework.stereotype.Component;
import de.wwu.criticalsystems.libhpng.Main.*;
import de.wwu.criticalsystems.libhpng.formulaparsing.ParseException;
import de.wwu.criticalsystems.libhpng.formulaparsing.SMCParser;
import de.wwu.criticalsystems.libhpng.formulaparsing.SimpleNode;

@Component
public class Commands implements CommandMarker {
	
	private ModelHandler handler = new ModelHandler();
	
	@CliAvailabilityIndicator({"read model","change logfile"})
	public boolean isAvailable() {
		return true;
	}
	
	@CliAvailabilityIndicator({"parse formula"})
	public boolean isParseFormulaAvailable() {
		//if (handler.getModel() == null) return false;
		return true;
	}
	
	
	@CliCommand(value = "read model", help = "Reads in an HPnG model")
	public void readModel(
		@CliOption(key = { "path" }, mandatory = true, help = "The path of the xml file containing the HPnG model") final String xmlPath) {		
		
		handler.readModel(xmlPath);
	}
	
	
	
	@CliCommand(value = "parse formula", help = "Parse a model checking formula and print the tree structure.")
	public void parseFormula(){
		
		System.out.println("Please enter the model checking formula to check:");		
		try {
			SMCParser parser = new SMCParser(System.in);
			SimpleNode root = parser.Input();
			root.dump("");
		} catch (ParseException e) {
			System.out.println("The formula could not be parsed.");
		}
		
	}
	
	
	@CliCommand(value = "change logfile", help = "Change the path of the log file")
	public void parse(
			@CliOption(key = { "path" }, mandatory = true, help = "The path of the log file") final String logPath){
		
		handler.setLoggerPath(logPath);
	}
	
}
