package com.example.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.model.UserInfo;

public interface UserInfoRepository extends JpaRepository<UserInfo, Long> {

	UserInfo findByEmail(String email);
	
	Optional<UserInfo> findByEmailAndPassword(String email, String password);
}
