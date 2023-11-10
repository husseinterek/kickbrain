package com.kickbrain;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import com.kickbrain.db.repository.GenericRepositoryImpl;

@Configuration
@EnableJpaRepositories(basePackages = "com.kickbrain.db.repository", repositoryBaseClass = GenericRepositoryImpl.class)
public class JPAConfig {
	// additional JPA Configuration
}