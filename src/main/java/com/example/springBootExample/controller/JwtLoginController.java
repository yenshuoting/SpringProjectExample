package com.example.springBootExample.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.springBootExample.entity.Member;
import com.example.springBootExample.utils.JwtTokenUtils;

import io.swagger.v3.oas.annotations.parameters.RequestBody;

@RestController
public class JwtLoginController {

	@Autowired
	JwtTokenUtils jwtTokenUtils ;
	
	@GetMapping("open/login")
	public ResponseEntity<String> login(@RequestBody Member member) {

		String token = jwtTokenUtils.generateToken(member); // 取得token

		return ResponseEntity.ok(token);
	}
}
