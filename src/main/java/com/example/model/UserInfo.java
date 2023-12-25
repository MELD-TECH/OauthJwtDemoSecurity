package com.example.model;


import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Table
@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
//@ToString(exclude = "password")
public class UserInfo implements Serializable{


	private static final long serialVersionUID = -2742681411836300541L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	private String lastname;
	
	private String firstname;
	
	private String email;
	
	private @JsonIgnore String password;
	
	private Gender gender;
	
	private Role role;
	
	
}
