package de.wwu.criticalsystems.libhpng.shell;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.shell.plugin.support.DefaultHistoryFileNameProvider;
import org.springframework.stereotype.Component;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class HistoryFileNameProvider extends DefaultHistoryFileNameProvider {

	@Override
	public String getHistoryFileName() {
		return "shellLogFile.log";
	}

	@Override
	public String getProviderName() {
		return "history file name provider";
	}
	
}
