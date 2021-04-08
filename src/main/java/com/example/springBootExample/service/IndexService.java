package com.example.springBootExample.service;

import org.springframework.web.servlet.ModelAndView;

import com.example.springBootExample.entity.Member;

public interface IndexService {

	Member findeMember(Long id);
	
}
