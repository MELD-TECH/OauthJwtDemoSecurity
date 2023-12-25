package com.example.controller;

import java.util.Collection;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.model.ChangePassword;
import com.example.model.ErrorResult;
import com.example.model.LoginDTO;
import com.example.model.LoginMessage;
import com.example.model.UserInfo;
import com.example.services.UserService;

import jakarta.servlet.http.HttpSession;

@RestController
@CrossOrigin(origins = "http://localhost:3000", exposedHeaders = "Access-Control-Allow-Origin:*")
@RequestMapping("/user/")
public class UserController {

	@Autowired
	private UserService service;
	
	@GetMapping("findall")
	public ResponseEntity<Object> getUserList(){
		
		Collection<UserInfo> user = service.getUsers();
		
		return new ResponseEntity<>(user, HttpStatus.OK);
	}
	
	@PostMapping("register")
	public ResponseEntity<Object> createUser(@RequestBody UserInfo user){
		Random rand = new Random();
		
		if(user != null) {
			user.setId(rand.nextLong());
			service.createUser(user);
			
			return new ResponseEntity<>("User Successfully Registered", HttpStatus.CREATED);
		}else {
			return new ResponseEntity<>("Error registering user", HttpStatus.EXPECTATION_FAILED);
		}
		
	}
	
	@PutMapping("update/{id}")
	public ResponseEntity<Object> updateUser(@PathVariable Long id, UserInfo user){
		UserInfo userInfo = service.updateUser(id, user);
		
		if(userInfo != null) {
			return new ResponseEntity<>(user, HttpStatus.OK);
		}else {
			return new ResponseEntity<>("Error occurred while updating user...", HttpStatus.NOT_MODIFIED);
		}
		
	}
	
	@GetMapping("{id}")
	public ResponseEntity<Object> findUserRecord(@PathVariable Long id){
		UserInfo user = service.findUserById(id);
		
		return new ResponseEntity<>(user, HttpStatus.OK);
	}
	
	@DeleteMapping("{id}")
	public ResponseEntity<Object> removeUser(@PathVariable Long id){
		
		service.removeUser(id);
		
		return new ResponseEntity<Object>("user removed from system", HttpStatus.NO_CONTENT);
	}
	

	@PostMapping("login")
	public ResponseEntity<Object> loginUser(@RequestBody LoginDTO logindto) throws Exception{
		
		LoginMessage logMsg = service.findLoggedOnUser(logindto);
		
		return new ResponseEntity<>(logMsg, HttpStatus.OK);
	}
	
	@GetMapping("/getData")
	public String getData(HttpSession session) {
	    String username = (String) session.getAttribute("email");
	    String firstname = (String) session.getAttribute("firstname");
	    String lastname = (String) session.getAttribute("lastname");
	     
	    return "Hello, " + username + " " + lastname + " " + firstname;
	}
	
	@PostMapping("/logout")
	public ResponseEntity<Object> logout(HttpSession session) {
	    // Invalidate the session
	    session.invalidate();
	    System.out.println("logged out successfully");
	    
	    
	    return new ResponseEntity<Object>("logged_out", HttpStatus.OK);
	}
	
	@PostMapping("/change-password")
	public ResponseEntity<Object> changePassword(@RequestBody ChangePassword changePassword){
		
		ErrorResult info = service.changePassword(changePassword);
		
		if(info.getMessage() == "Success") {
			return new ResponseEntity<>(info, HttpStatus.OK);
		}else {
			return new ResponseEntity<>(info, HttpStatus.BAD_REQUEST);
		}
		
	}
}
