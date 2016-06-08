package de.wwu.criticalsystems.libhpng.shell;

import org.springframework.shell.core.CommandMarker;
import org.springframework.shell.core.annotation.CliAvailabilityIndicator;
import org.springframework.shell.core.annotation.CliCommand;
import org.springframework.shell.core.annotation.CliOption;
import org.springframework.stereotype.Component;
import de.wwu.criticalsystems.libhpng.Main.*;

@Component
public class Commands implements CommandMarker {
	
	@CliAvailabilityIndicator({"read"})
	public boolean isReadAvailable() {
		return true;
	}
		
	@CliCommand(value = "read", help = "Reads in an HPnG model")
	public void read(
		@CliOption(key = { "path" }, mandatory = true, help = "The path of the xml file containing the HPnG model") final String xmlPath) {		
		
		Main.libhpngMain(xmlPath);
	}
}
