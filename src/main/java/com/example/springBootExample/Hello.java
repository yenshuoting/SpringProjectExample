package com.example.springBootExample;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Hello {
	
	@RequestMapping("/sayHelloWorld")
	public String sayHelloWorld() {
		return "Hello World !! ";
	}

	@GetMapping("/sayHelloWorld/{name}")
	public String sayHelloWorld( @PathVariable("name") String name ) {
		return  new String("Hello "+name+" !!");
	}
}
