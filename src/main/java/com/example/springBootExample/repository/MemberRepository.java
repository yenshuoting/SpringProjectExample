package com.example.springBootExample.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.springBootExample.entity.Member;

public interface MemberRepository  extends JpaRepository<Member, Long>{

}
