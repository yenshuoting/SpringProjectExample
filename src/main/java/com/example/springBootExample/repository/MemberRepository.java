package com.example.springBootExample.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import com.example.springBootExample.entity.Member;

@RepositoryRestResource
public interface MemberRepository  extends JpaRepository<Member, Long>{
	
//	@Query(value = "select id, e_mail, usr_name from member where e_mail = :email and usr_name = :usrname ",nativeQuery = true)
	@Query(value = "select id, eMail, usrName from Member where eMail = :email and usrName = :usrname ")
	List<Member> selectWhereEmailAndUsrName(@Param("email") String email , @Param("usrname") String usrname);


	@Query(value = "select id, e_mail, usr_name from member where id > :numlast and id < :numfast " ,nativeQuery = true)
	List<Member> selectWhereNumLastlqIdANdNumFastrqId(@Param("numlast") String email , @Param("numfast") String usrname);
}
