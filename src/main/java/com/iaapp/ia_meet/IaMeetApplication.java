package com.iaapp.ia_meet;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.File;

@SpringBootApplication
public class IaMeetApplication {

	public static void main(String[] args) {
		// Correction automatique de l'URL MySQL de Railway avant le démarrage
		String mysqlUrl = System.getenv("MYSQL_URL");
		if (mysqlUrl != null && mysqlUrl.startsWith("mysql://")) {
			String jdbcUrl = "jdbc:" + mysqlUrl;
			System.setProperty("spring.datasource.url", jdbcUrl);
			System.out.println("URL MySQL corrigée automatiquement en JDBC.");
		}
		
		SpringApplication.run(IaMeetApplication.class, args);
	}

	@PostConstruct
	public void init() {
		// Gestion de la clé Google Cloud
		String credentialsJson = System.getenv("GOOGLE_CREDENTIALS_CONTENT");
		if (credentialsJson != null && !credentialsJson.isEmpty()) {
			try {
				String path = "google-credentials.json";
				Files.write(Paths.get(path), credentialsJson.getBytes());
				System.setProperty("GOOGLE_APPLICATION_CREDENTIALS", new File(path).getAbsolutePath());
				System.out.println("Google Cloud Credentials configurées.");
			} catch (IOException e) {
				System.err.println("Erreur credentials Google : " + e.getMessage());
			}
		}
	}
}
