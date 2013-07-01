package com.github.timmystorms.collaborations;

import java.util.List;

import org.neo4j.cypher.javacompat.ExecutionEngine;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.kernel.GraphDatabaseAPI;
import org.neo4j.kernel.impl.transaction.SpringTransactionManager;
import org.neo4j.kernel.impl.transaction.UserTransactionImpl;
import org.neo4j.server.WrappingNeoServerBootstrapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.aspectj.EnableSpringConfigured;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.TransactionManagementConfigurer;
import org.springframework.transaction.jta.JtaTransactionManager;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
@EnableSpringConfigured
@EnableTransactionManagement
@EnableWebMvc
@ComponentScan("com.github.timmystorms.collaborations")
public class SpringConfiguration extends WebMvcConfigurerAdapter implements TransactionManagementConfigurer {
	
	// Database

	@Bean(destroyMethod = "shutdown")
	public GraphDatabaseService graphDatabaseService() {
		return new GraphDatabaseFactory().newEmbeddedDatabase("/neo-collab");
//		return new TestGraphDatabaseFactory().newImpermanentDatabase("/neo-collab");
	}

	@Bean
	public ExecutionEngine executionEngine() {
		return new ExecutionEngine(graphDatabaseService());
	}

	@Bean(name = { "neo4jTransactionManager", "transactionManager" })
	@Qualifier("neo4jTransactionManager")
	public PlatformTransactionManager neo4jTransactionManager() {
		return new JtaTransactionManager(new UserTransactionImpl((GraphDatabaseAPI) graphDatabaseService()),
				new SpringTransactionManager((GraphDatabaseAPI) graphDatabaseService()));
	}
	
	public PlatformTransactionManager annotationDrivenTransactionManager() {
		return neo4jTransactionManager();
	}
	
	// Web
	
	@Bean(initMethod = "start", destroyMethod = "stop")
    public WrappingNeoServerBootstrapper neo4jServer() {
        return new WrappingNeoServerBootstrapper((GraphDatabaseAPI) graphDatabaseService());
    }
	
	@Override
    public void configureMessageConverters(final List<HttpMessageConverter<?>> converters) {
        converters.add(getJackson2MappingConverter());
    }

    @Bean
    public HttpMessageConverter<Object> getJackson2MappingConverter() {
        return new MappingJackson2HttpMessageConverter();
    }

}
