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
		SpringApplication.run(IaMeetApplication.class, args);
	}

	@PostConstruct
	public void init() {
		// Cette logique permet de créer le fichier de clé Google Cloud 
		// à partir d'une variable d'environnement sur Railway/Cloud
		String credentialsJson = System.getenv("GOOGLE_CREDENTIALS_CONTENT");
		if (credentialsJson != null && !credentialsJson.isEmpty()) {
			try {
				String path = "google-credentials.json";
				Files.write(Paths.get(path), credentialsJson.getBytes());
				System.setProperty("GOOGLE_APPLICATION_CREDENTIALS", new File(path).getAbsolutePath());
				System.out.println("Google Cloud Credentials configurées avec succès depuis les variables d'environnement.");
			} catch (IOException e) {
				System.err.println("Erreur lors de la création du fichier de credentials Google : " + e.getMessage());
			}
		}
	}
}
