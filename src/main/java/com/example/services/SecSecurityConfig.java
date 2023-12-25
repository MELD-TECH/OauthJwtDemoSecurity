package com.example.services;

import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.EncodedKeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;



@Configuration
public class SecSecurityConfig {

	@Autowired
	private TokenAuthenticationEntryPoint authEntryPoint;
		
	  @Bean 
	  public static PasswordEncoder passwordEncoder() { 
		  return new BCryptPasswordEncoder(); 
	  
	  }
	 	
	  @Bean 
	  public static UserDetailsService userDetailsService() { 
		  return new JwtUserDetailsService(); 
		 }
	 
	
	  @Bean 
	  public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception { 		  
		  return authConfig.getAuthenticationManager(); 
	}

	  @Bean 
	  public DaoAuthenticationProvider authProvider() {
	      DaoAuthenticationProvider auth = new DaoAuthenticationProvider();
	      auth.setPasswordEncoder(passwordEncoder());
	      auth.setUserDetailsService(userDetailsService());
	  
	  return auth; 
	  }
	 
	
	
	  @Bean 
	  public SecurityFilterChain filter(HttpSecurity http) throws Exception{
//	  http.cors();
      
      
      
          http
                  .csrf(csrf -> csrf.disable())
                  .authorizeHttpRequests((authorize) ->
                  {
                      try {
                          authorize
                                  .requestMatchers("/user/login", "/user/register", "/user/logout").permitAll()
                                  .requestMatchers(HttpMethod.OPTIONS).permitAll()
                                  .anyRequest().authenticated()
                                  .and()
                                  .exceptionHandling(handling -> handling.authenticationEntryPoint(authEntryPoint))
                                  .sessionManagement(management -> management.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                                  .authenticationProvider(authProvider())
                                  .oauth2ResourceServer((oauth) -> oauth.jwt((jwk) -> jwk.decoder(jwtDecoder())))
                                  ;
                          

                      } catch (Exception e) {
                          // TODO Auto-generated catch block
                          e.printStackTrace();
                      }
                  }

                  )                  
                  ;         
	  
          return http.build();
	  
	  }
	 
	  @Bean
	  public JwtDecoder jwtDecoder() {
		  
		  PublicKey publicKey = null;
		  
		  try {
			  Path pathpublic = Paths.get("public.pub");
				 byte[] bytespub = Files.readAllBytes(pathpublic);
					 
				 EncodedKeySpec pubEnc = new X509EncodedKeySpec(bytespub);
				  KeyFactory factory = KeyFactory.getInstance("RSA");
					 			 
			      publicKey = factory.generatePublic(pubEnc);	
		  }catch(Exception e) {
			  e.getMessage();
		  }
			  		  
		  System.out.println("publicKey in jwtDecoder " + publicKey.toString());
		  
		  return NimbusJwtDecoder.withPublicKey((RSAPublicKey) publicKey).build();
	  }
	  
	  @Bean
	  public JwtEncoder jwtEncoder() throws Exception{
		  generatePair();
		  
		 Path pathprivate = Paths.get("private.key");
		 byte[] bytespvt = Files.readAllBytes(pathprivate);
		 
		 Path pathpublic = Paths.get("public.pub");
		 byte[] bytespub = Files.readAllBytes(pathpublic);
		 
		 PKCS8EncodedKeySpec keyencode = new PKCS8EncodedKeySpec(bytespvt);
		 KeyFactory factory = KeyFactory.getInstance("RSA");
		 
         EncodedKeySpec pubEnc = new X509EncodedKeySpec(bytespub);
		 
		  PrivateKey privateKey = factory.generatePrivate(keyencode);
		  
		  PublicKey publicKey = factory.generatePublic(pubEnc);
		  
		  System.out.println("publicKey in jwtEncoder " + publicKey.toString());
		  
		  JWK jwk = new RSAKey.Builder((RSAPublicKey) publicKey).privateKey((RSAPrivateKey) privateKey).build();
		  
		  JWKSource<SecurityContext> jwksource = new ImmutableJWKSet<SecurityContext>(new JWKSet(jwk));
		  
		  System.out.println("Private key format: " + privateKey.getFormat() + " Algorithm " + publicKey.getAlgorithm());
		  System.out.println("Public key format: " + publicKey.getFormat() + " Algorithm " + publicKey.getAlgorithm());
		  
		  return new NimbusJwtEncoder(jwksource);
	  }
	  
	  public void generatePair() throws Exception{
		  
		  KeyPairGenerator keygenerator = KeyPairGenerator.getInstance("RSA");
		  keygenerator.initialize(2048);
		  
		  KeyPair pair = keygenerator.generateKeyPair();
		  
		  FileOutputStream privateFile = new FileOutputStream("private" + ".key");
		  privateFile.write(pair.getPrivate().getEncoded());
		  privateFile.close();
		  
		  FileOutputStream publicKey = new FileOutputStream("public" + ".pub");
		  publicKey.write(pair.getPublic().getEncoded());
		  publicKey.close();
		 	
		  System.out.println("Files created successfully... ");
	  }

}
