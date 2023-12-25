package com.example.services;


import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.config.oauth.TokenManager;
import com.example.model.ChangePassword;
import com.example.model.ErrorResult;
import com.example.model.LoginDTO;
import com.example.model.LoginMessage;
import com.example.model.Role;
import com.example.model.UserInfo;
import com.example.repository.UserInfoRepository;


@Service
public class UserService{

	@Autowired
	private UserInfoRepository repo;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Autowired
	private AuthenticationManager authmanager;

	@Autowired
	private TokenManager tokenManager;
	
	@Autowired
	private UserDetailsService userdet;
	
	public Collection<UserInfo> getUsers(){

		return repo.findAll();
	}
	
	public UserInfo createUser(UserInfo user) {
		System.out.println("User is: " + user);
		
		user.setPassword(passwordEncoder.encode(user.getPassword()));	
		
		user.setRole(Role.USER);
		UserInfo us = repo.save(user);
		
		return us;
	}
	
	public UserInfo updateUser(Long id, UserInfo user) {
		Optional<UserInfo> useropt = repo.findById(id);
		
		UserInfo us = new UserInfo();
		
		if(useropt.isPresent()) {
			us = useropt.get();			
			us.setFirstname(user.getFirstname());
			us.setGender(user.getGender());
			us.setLastname(us.getLastname());			
		}
		
		us = repo.save(us);
		
		return us;
	}
	
	public void removeUser(Long id) {
		repo.deleteById(id);
	}
	
	public UserInfo findUserById(Long id) {
		return repo.findById(id).get();
	}
	
	
	public LoginMessage findLoggedOnUser(LoginDTO logindto) throws Exception{
	
		LoginMessage loginMessage = new LoginMessage();
		
        UserInfo info = repo.findByEmail(logindto.getEmail());
		
		String token = null;
		
		if(info != null) {
			
			try {
				Authentication auth = authmanager.authenticate(
                  new UsernamePasswordAuthenticationToken(info.getEmail(), info.getPassword()));
			
			SecurityContextHolder.getContext().setAuthentication(auth);
			}catch(AuthenticationException e) {
				e.getMessage();
			}
			
			UserDetails userdetails = userdet.loadUserByUsername(info.getEmail());
			
			token = tokenManager.generateToken(userdetails);
			
			String subject = tokenManager.getUsernameFromToken(token);
			
			Date expiryDate = tokenManager.getExpiryDate(token);
						
			System.out.println(token + "\r\n" + subject + "\r\n" + expiryDate);

			
			loginMessage.setToken(token);
			loginMessage.setSubject(subject);
			loginMessage.setExpiry(expiryDate);
			
			return loginMessage;
		}else {
			loginMessage.setToken("TOKEN_NOT_FOUND");
			
			return loginMessage;
		}
		
		
		
	}
	
	public ErrorResult changePassword(ChangePassword changePassword) {
		
		ErrorResult result = new ErrorResult();
		
		UserInfo userinfo = null;
		try {
			userinfo = repo.findByEmail(changePassword.getEmail());
			
			if(userinfo != null) {
				String dbpass = userinfo.getPassword();
				
				String currentpassword = changePassword.getCurrentPassword();

				
				boolean validate = passwordEncoder.matches(currentpassword, dbpass);

				
				if(validate) {

						userinfo.setPassword(passwordEncoder.encode(changePassword.getNewPassword()));
						System.out.println("Password encoded successfully");
						
						repo.save(userinfo);
						
						System.out.println("new password " + userinfo.getPassword());
												
					    result.setMessage("Success");
				}else {
					result.setMessage("Bad-Credentials");
				}
			}
			
		}catch(Exception e){
			throw new BadCredentialsException(e.getMessage());
		}

		
		return result;
	}
	

}
