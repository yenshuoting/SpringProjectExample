package com.example.springBootExample.service.impl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;

import com.example.springBootExample.entity.Member;
import com.example.springBootExample.repository.MemberRepository;
import com.example.springBootExample.service.IndexService;

@Service
public class IndexServiceImpl implements IndexService {
	
	@Autowired
	private MemberRepository memberRepository;

	@Override
	public Member findeMember(Long id) {
		Optional<Member> o = memberRepository.findById(id);
		if(o.isEmpty()) {
			return new Member();
		}
		
		return memberRepository.findById(id).get();
	}

}
