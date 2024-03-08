package com.vikram.service;

import java.io.IOException;

import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vikram.Request.AuthenticationRequest;
import com.vikram.Request.RegisterRequest;
import com.vikram.Request.VerificationRequest;
import com.vikram.config.JwtService;
import com.vikram.exception.AuthenticationFailedException;
import com.vikram.exception.RegistrationFailedException;
import com.vikram.exception.TokenRefreshFailedException;
import com.vikram.exception.VerificationFailedException;
import com.vikram.model.Role;
import com.vikram.model.User;
import com.vikram.model.response.AuthenticationResponse;
import com.vikram.repository.UserRepository;
import com.vikram.tfa.TwoFactorAuthenticationService;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl  implements AuthenticationService{

	private final UserRepository repository;
	private final PasswordEncoder passwordEncoder;
	private final JwtService jwtService;
	private final AuthenticationManager authenticationManager;
	private final TwoFactorAuthenticationService tfaService;

	public AuthenticationResponse register(RegisterRequest request) {
		
		try {
		var user = User.builder().firstname(request.getFirstname()).lastname(request.getLastname())
				.email(request.getEmail()).password(passwordEncoder.encode(request.getPassword())).role(Role.USER)
				.mfaEnabled(request.isMfaEnabled()).build();

		// if MFA enabled --> Generate Secret
		if (request.isMfaEnabled()) {
			user.setSecret(tfaService.generateNewSecret());
		}
		repository.save(user);
		var jwtToken = jwtService.generateToken(user);
		var refreshToken = jwtService.generateRefreshToken(user);
		return AuthenticationResponse.builder().secretImageUri(tfaService.generateQrCodeImageUri(user.getSecret()))
				.accessToken(jwtToken).refreshToken(refreshToken).mfaEnabled(user.isMfaEnabled()).build();
		} catch (Exception e) {
	        throw new RegistrationFailedException("Registration failed: " + e.getMessage());
	    }
	}

	public AuthenticationResponse authenticate(AuthenticationRequest request) {
		
		try {
		authenticationManager
				.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
		var user = repository.findByEmail(request.getEmail()).orElseThrow();
		if (user.isMfaEnabled()) {
			return AuthenticationResponse.builder().accessToken("").refreshToken("").mfaEnabled(true).build();
		}
		var jwtToken = jwtService.generateToken(user);
		var refreshToken = jwtService.generateRefreshToken(user);
		return AuthenticationResponse.builder().accessToken(jwtToken).refreshToken(refreshToken).mfaEnabled(false)
				.build();
		} catch (Exception e) {
	        throw new AuthenticationFailedException("Authentication failed: " + e.getMessage());
	    }
	}

	public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
		
		try {
		final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
		final String refreshToken;
		final String userEmail;
		if (authHeader == null || !authHeader.startsWith("Bearer ")) {
			return;
		}
		refreshToken = authHeader.substring(7);
		userEmail = jwtService.extractUsername(refreshToken);
		if (userEmail != null) {
			var user = this.repository.findByEmail(userEmail).orElseThrow();
			if (jwtService.isTokenValid(refreshToken, user)) {
				var accessToken = jwtService.generateToken(user);
				var authResponse = AuthenticationResponse.builder().accessToken(accessToken).refreshToken(refreshToken)
						.mfaEnabled(false).build();
				new ObjectMapper().writeValue(response.getOutputStream(), authResponse);
			}
		}
		
		} catch (Exception e) {
	        throw new TokenRefreshFailedException("Token refresh failed: " + e.getMessage());
	    }
	}

	public AuthenticationResponse verifyCode(VerificationRequest verificationRequest) {
		
		try {
		User user = repository.findByEmail(verificationRequest.getEmail())
				.orElseThrow(() -> new EntityNotFoundException(
						String.format("No user found with %S", verificationRequest.getEmail())));
		if (tfaService.isOtpNotValid(user.getSecret(), verificationRequest.getCode())) {

			throw new BadCredentialsException("Code is not correct");
		}
		var jwtToken = jwtService.generateToken(user);
		return AuthenticationResponse.builder().accessToken(jwtToken).mfaEnabled(user.isMfaEnabled()).build();
		
		} catch (Exception e) {
	        throw new VerificationFailedException("Verification failed: " + e.getMessage());
	    }
	}
		
	
}
