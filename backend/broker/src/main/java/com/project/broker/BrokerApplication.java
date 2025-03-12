package com.project.broker;

import com.project.broker.service.BrokerService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.context.WebServerInitializedEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@EnableScheduling
public class BrokerApplication {

	@Value("${server.port}")
	private int port;

	public static void main(String[] args) {
		SpringApplication.run(BrokerApplication.class, args);
	}

	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}

	@EventListener
	public void onWebServerInitialized(WebServerInitializedEvent event) {
		this.port = event.getWebServer().getPort();
		System.out.println("Started application on port: " + port);
		System.out.println("This broker's URL: http://localhost:" + port);
	}

	@Bean
	public BrokerService brokerService(RestTemplate restTemplate) {
		BrokerService brokerService = new BrokerService(restTemplate);
		brokerService.setPort(port);
		return brokerService;
	}
} 