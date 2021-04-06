package com.example.springBootExample.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.springBootExample.entity.Member;
import com.example.springBootExample.repository.MemberRepository;

@RestController
@RequestMapping("api/v1")
public class MemberController {
	
	@Autowired
	MemberRepository memberRepository;
	
	@GetMapping("get/member/{id}")
	public ResponseEntity<Member>  findMemberById(@PathVariable Long id) {
		return ResponseEntity.ok(memberRepository.findById(id).get());
	}
	
	@GetMapping("get/members")
	public ResponseEntity<List<Member>>  findMembers() {
		return ResponseEntity.ok( memberRepository.findAll());
	}
	
	@PostMapping("add/member")
	public ResponseEntity<Member>  addMember(@RequestBody Member member) {
		return ResponseEntity.ok( memberRepository.save(member));
	}

	@PutMapping("update/member")
	public ResponseEntity<Member>  updateMember(@RequestBody Member member) {
		return ResponseEntity.ok( memberRepository.save(member));
	}
	
}
