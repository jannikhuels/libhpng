package de.wwu.criticalsystems.libhpng.shell;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.shell.plugin.support.DefaultBannerProvider;
import org.springframework.stereotype.Component;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class BannerProvider extends DefaultBannerProvider  {

	public String getBanner() {
		StringBuffer buf = new StringBuffer();
		buf.append("Welcome to libhpng!");
		return buf.toString();
	}

	public String getWelcomeMessage() {
		return "test";
	}
	
	@Override
	public String getProviderName() {
		return "libhpng";
	}
}