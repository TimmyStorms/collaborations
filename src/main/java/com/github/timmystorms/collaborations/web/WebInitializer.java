package com.github.timmystorms.collaborations.web;

import java.util.Set;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

import com.github.timmystorms.collaborations.SpringConfiguration;

public class WebInitializer implements WebApplicationInitializer {

	private static final Logger LOGGER = LoggerFactory.getLogger(WebInitializer.class);

	public void onStartup(final ServletContext ctx) throws ServletException {
		final AnnotationConfigWebApplicationContext rootCtx = new AnnotationConfigWebApplicationContext();
		rootCtx.register(SpringConfiguration.class);
		ctx.addListener(new ContextLoaderListener(rootCtx));
		final ServletRegistration.Dynamic appServlet = ctx.addServlet("rest", new DispatcherServlet(rootCtx));
		appServlet.setLoadOnStartup(1);
		final Set<String> mappingConflicts = appServlet.addMapping("/");
		if (!mappingConflicts.isEmpty()) {
			for (String s : mappingConflicts) {
				LOGGER.error("Mapping conflict: {}", s);
			}
		}
	}

}
