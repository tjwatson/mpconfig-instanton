package io.openliberty.example.instanton;

import static io.openliberty.example.instanton.RestApplication.PROP_CONFIG1;
import static io.openliberty.example.instanton.RestApplication.PROP_CONFIG2;
import static io.openliberty.example.instanton.RestApplication.lookup;

import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.inject.Provider;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;

@Path("/requestscope")
@RequestScoped
public class ConfigRequestScope {

	@Inject
	private Config config;

	@Inject
	@ConfigProperty(name=PROP_CONFIG1, defaultValue = "default")
	private Provider<String> config1;

	@Inject
	@ConfigProperty(name=PROP_CONFIG2, defaultValue = "default")
	private String config2;

	@GET
	@Path("config")
	public String getConfig() {
		return "config1=" + lookup(config, PROP_CONFIG1, config1.get()) + " config2=" + lookup(config, PROP_CONFIG2, config2);
	}

	@GET
	@Path("config1")
	public String getConfig1() {
		return "config1=" + config1.get();
	}

	@GET
	@Path("config2")
	public String getConfig2() {
		return "config2=" + config2;
	}
}
