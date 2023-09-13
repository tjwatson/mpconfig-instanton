package io.openliberty.example.instanton;

import java.util.NoSuchElementException;

import org.eclipse.microprofile.config.Config;

import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;

@ApplicationPath("/")
public class RestApplication extends Application {
	final static String PROP_CONFIG1 = "io.openliberty.example.instanton.config1";
	final static String PROP_CONFIG2 = "io.openliberty.example.instanton.config2";

	static String lookup(Config config, String prop, String defaultValue) {
		try {
			return config.getValue(prop, String.class);
		} catch (NoSuchElementException e) {
			System.out.println("falling back to default for " + prop);
		}
		return defaultValue;
	}
}