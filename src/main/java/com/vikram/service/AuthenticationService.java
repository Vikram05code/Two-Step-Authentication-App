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

import com.vikram.model.User;
import com.vikram.model.response.AuthenticationResponse;
import com.vikram.repository.UserRepository;
import com.vikram.tfa.TwoFactorAuthenticationService;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;


public interface AuthenticationService {

	AuthenticationResponse register(RegisterRequest request);
	
	AuthenticationResponse authenticate(AuthenticationRequest request);
	
	void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException;
	
	
	AuthenticationResponse verifyCode(VerificationRequest verificationRequest);
	

}
