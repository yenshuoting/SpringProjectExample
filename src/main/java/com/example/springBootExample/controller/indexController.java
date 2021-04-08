package com.example.springBootExample.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.example.springBootExample.service.IndexService;

@Controller
@RequestMapping("app")
public class indexController {

	private static final String PAGE = "index";
	
	@Autowired
	private IndexService indexService;

	@GetMapping("index")
	public ModelAndView index() {
		ModelAndView modelAndView = new ModelAndView(PAGE);
		modelAndView.addObject("member", indexService.findeMember(1L));
		return modelAndView;
	}
	
}
