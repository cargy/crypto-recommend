package com.agileactors.cryptoapi;

import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import io.swagger.v3.oas.models.info.Info;

@SpringBootApplication
public class CryptoApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(CryptoApiApplication.class, args);
	}

	@Bean
	public GroupedOpenApi usersGroup(@Value("${springdoc.version}") String appVersion) {
		return GroupedOpenApi.builder().group("crypto")
				.addOpenApiCustomizer(openApi -> openApi.info(new Info().title("Crypto Recommendations API").version(appVersion)))
				.packagesToScan("com.agileactors.cryptoapi")
				.build();
	}
}
