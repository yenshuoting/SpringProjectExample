package com.example.springBootExample.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;

@Configuration
@EnableWebSecurity
public class SecuriyConfig extends WebSecurityConfigurerAdapter {
	
	@Autowired
	UserDetailsService userDetailsService;
	
	  @Override
	    protected void configure(HttpSecurity http) throws Exception {
	        http
	                .authorizeRequests()
	                .antMatchers("/open/**").permitAll()
	                .antMatchers("/web/**").authenticated()
	                .antMatchers("/app/**").authenticated()
	                .antMatchers(HttpMethod.GET).permitAll()
//	                .antMatchers(HttpMethod.POST, "/web").permitAll()
//	                .anyRequest().authenticated()
	                
	                .antMatchers("/h2-console/**").permitAll() 
	                .and().csrf().ignoringAntMatchers("/h2-console/**")
	                .and().headers().frameOptions().sameOrigin()
	                

	                .and()
	                .csrf().disable()
	                .formLogin()
	                ;
	    }
	  
	   @Override
	    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
			auth.userDetailsService(userDetailsService);
	    }
	   
}
