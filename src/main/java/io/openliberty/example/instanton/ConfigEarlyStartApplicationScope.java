package io.openliberty.example.instanton;

import static io.openliberty.example.instanton.RestApplication.PROP_CONFIG1;
import static io.openliberty.example.instanton.RestApplication.PROP_CONFIG2;
import static io.openliberty.example.instanton.RestApplication.lookup;

import org.crac.Context;
import org.crac.Core;
import org.crac.Resource;
import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.Initialized;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import jakarta.inject.Provider;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;

@Path("/earlystart-appscope")
@ApplicationScoped
public class ConfigEarlyStartApplicationScope implements Resource {

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

	public ConfigEarlyStartApplicationScope() {
		Core.getGlobalContext().register(this);
	}

	@Override
	public void beforeCheckpoint(Context<? extends Resource> context) throws Exception {
		// do nothing; could check config values before checkpoint
	}
	
	@Override
	public void afterRestore(Context<? extends Resource> context) throws Exception {
		// For non dynamic config injections (e.g. config2) need to check for updates
		config2 = lookup(config, PROP_CONFIG2, config2);
	}

	// observing initialized events for the ApplicationScope will force this bean to activate
	// early, before checkpoint when using afterAppStart
	public void observeInit(@Observes @Initialized(ApplicationScoped.class) Object event) {
        System.out.println(getClass() + ": " + "Initializing application context");
    }
}
