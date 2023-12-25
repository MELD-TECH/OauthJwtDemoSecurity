package com.example.config.oauth;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Component;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jwt.JWTClaimsSet;

//import io.jsonwebtoken.Claims;
//import io.jsonwebtoken.JwtParser;
//import io.jsonwebtoken.Jwts;
//import io.jsonwebtoken.security.Keys;

@Component
public class TokenManager {

//	private static final long serialVersionUID = 5438315676917584697L;

	
//	public static final long TOKEN_VALIDITY = 10 * 60 * 60 ;
	
	@Autowired
	private JwtEncoder jwtEncoders;
	
	public String generateToken(UserDetails userdetails) {
		
		Instant time = Instant.now();
		
		JwtClaimsSet claims = JwtClaimsSet.builder()
				.subject(userdetails.getUsername())
				.issuedAt(time)
				.expiresAt(time.plus(10, ChronoUnit.HOURS))
				.issuer("self")
				.build()
				;
				
		return jwtEncoders.encode(JwtEncoderParameters.from(claims)).getTokenValue();
	}
	
	public String getUsernameFromToken(String token) {
		JWSObject jwsObject;
		
		JWTClaimsSet claims;
		
		try {
			jwsObject = JWSObject.parse(token);
			
			claims = JWTClaimsSet.parse(jwsObject.getPayload().toJSONObject());
			
			return claims.getSubject();
		}catch(ParseException e) {
			throw new BadCredentialsException(e.getMessage());
		}
	}
	
	public Date getExpiryDate(String token) {
		JWSObject jwsObject;
		
		JWTClaimsSet claims;
		
		try {
			jwsObject = JWSObject.parse(token);
			
			claims = JWTClaimsSet.parse(jwsObject.getPayload().toJSONObject());
			
			return claims.getExpirationTime();
		}catch(ParseException e) {
			throw new BadCredentialsException(e.getMessage());
		}
	}
	
	/*
	 * public String getUsernameFromToken(String token) { SecretKey key =
	 * Keys.hmacShaKeyFor(secret.getBytes());
	 * 
	 * JwtParser parser = Jwts.parser().verifyWith(key).build();
	 * 
	 * Claims claims = parser.parseSignedClaims(token).getPayload();
	 * 
	 * return claims.getSubject();
	 * 
	 * }
	 */
	
//	public Boolean validateToken(String token, UserDetails userdetails) {
//		String username = getUsernameFromToken(token);
//		
//		SecretKey keys = Keys.hmacShaKeyFor(secret.getBytes());
//		
//		JwtParser jwtparser = Jwts.parser().verifyWith(keys).build();
//		
//		Claims claim = jwtparser.parseSignedClaims(token).getPayload();
//		
//		boolean expiryDate = claim.getExpiration().before(new Date());
//		
//		Boolean validity = username.equals(userdetails.getUsername()) && !expiryDate;
//		
//		return validity;
//	}
}
