	package com.example.api;

	import org.springframework.boot.SpringApplication;
	import org.springframework.boot.autoconfigure.SpringBootApplication;
	import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
	import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;

	/**
	 * Api Application
	 * <p>
	 * This is the main class of the Spring Boot application.
	 * The default Security Context has been replaced by a new one.
	 */
	@SpringBootApplication(exclude = {SecurityAutoConfiguration.class, UserDetailsServiceAutoConfiguration.class})
	public class ApiApplication {

		/**
		 * EntryPoint
		 * <p>
		 * Starts the Spring framework with ApiApplication.class as the primary source.
		 *
		 * @param args command-line arguments
		 */
		public static void main(String[] args) {
			SpringApplication.run(ApiApplication.class, args);
		}
	}
