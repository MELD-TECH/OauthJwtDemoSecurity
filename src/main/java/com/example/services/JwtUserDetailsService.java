package com.example.services;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.example.model.UserInfo;
import com.example.repository.UserInfoRepository;


public class JwtUserDetailsService implements UserDetailsService {

	@Autowired
	private UserInfoRepository sourcerep;

	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		// TODO Auto-generated method stub
		
           UserInfo us = sourcerep.findByEmail(username);
		
		
		UserDetails detail = User.builder().username(us.getEmail())
				.password(us.getPassword())
				.authorities(new ArrayList<>())				
				.build();
		
		return detail;
	}

}
