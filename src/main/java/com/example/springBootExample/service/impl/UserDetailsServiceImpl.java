package com.example.springBootExample.service.impl;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.springBootExample.entity.Member;
import com.example.springBootExample.repository.MemberRepository;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private MemberRepository memberRepository;


	@Override
	public UserDetails loadUserByUsername(String usrName) throws UsernameNotFoundException {
		Member newMember = new Member();
		newMember.setId(memberRepository.count());
		newMember.setEMail(usrName);
		newMember.setUsrName(usrName);
		newMember.setUsrPwd("123456");
		memberRepository.save(newMember);
		
		
		Member member = new Member();
		member.setEMail(usrName);
		
		Example<Member> example  = Example.of(member);
		List<Member> optList = memberRepository.findAll(example);
		if(optList.isEmpty()) {
			throw new UsernameNotFoundException("Username Not Found !! ");
		}
		member = optList.get(0);
		
		
		return new User(member.getEMail(), "{noop}"+member.getUsrPwd(), Collections.emptyList());
	}

}
