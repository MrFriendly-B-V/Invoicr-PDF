package dev.array21.invoicrpdf.controllers;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/health")
public class HealthEndpoint {

	@CrossOrigin(origins = {"*"})
	@RequestMapping(method = RequestMethod.GET, produces = MediaType.TEXT_PLAIN_VALUE)
	public String healthGet() {
		return "OK";
	}
	
	@CrossOrigin(origins = {"*"})
	@RequestMapping(method = RequestMethod.POST, produces = MediaType.TEXT_PLAIN_VALUE)
	public String healthPost() {
		return "OK";
	}
}
