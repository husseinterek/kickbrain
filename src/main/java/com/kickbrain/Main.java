package com.kickbrain;

import java.io.IOException;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;

@EnableAutoConfiguration
@ComponentScan("com.kickbrain")
@EnableScheduling
@EnableAsync
public class Main extends SpringBootServletInitializer{

	public static void main(String[] args) {
		SpringApplication.run(Main.class, args);
	}
	 
	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(Main.class);
	}
	
	@Bean
    public ThreadPoolTaskExecutor timerExecutor() {
         ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
         executor.setCorePoolSize(50);
         executor.setBeanName("threadPoolTaskExecutor");
         executor.setMaxPoolSize(500);
         executor.setQueueCapacity(500);
         executor.setThreadNamePrefix("TimerThread-");
         executor.initialize();
         return executor;
    }
	
	@Bean
    public ThreadPoolTaskExecutor gameProceedExecutor() {
         ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
         executor.setCorePoolSize(5);
         executor.setBeanName("threadPoolTaskExecutor");
         executor.setMaxPoolSize(10);
         executor.setQueueCapacity(50);
         executor.setThreadNamePrefix("GameProceedThread-");
         executor.initialize();
         return executor;
    }
	
	@Bean
	public FirebaseMessaging firebaseMessaging() throws IOException {
	    GoogleCredentials googleCredentials = GoogleCredentials
	            .fromStream(new ClassPathResource("firebase-service-account.json").getInputStream());
	    FirebaseOptions firebaseOptions = FirebaseOptions
	            .builder()
	            .setCredentials(googleCredentials)
	            .build();
	    FirebaseApp app = FirebaseApp.initializeApp(firebaseOptions, "my-app");
	    return FirebaseMessaging.getInstance(app);
	}
}
