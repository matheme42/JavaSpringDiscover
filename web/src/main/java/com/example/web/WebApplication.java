package com.example.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

import lombok.Data;

@Data
@ServletComponentScan
@SpringBootApplication
public class WebApplication {

	@Autowired
	private CustomProperties props;

	/** Entry Point
	 *
	 * @param args
	 */
	public static void main(String[] args) {
		SpringApplication.run(WebApplication.class, args);
	}
}
