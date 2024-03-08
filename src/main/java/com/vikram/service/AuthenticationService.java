package com.vikram.service;

import java.io.IOException;
import com.vikram.Request.AuthenticationRequest;
import com.vikram.Request.RegisterRequest;
import com.vikram.Request.VerificationRequest;
import com.vikram.model.response.AuthenticationResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface AuthenticationService {

	AuthenticationResponse register(RegisterRequest request);
	
	AuthenticationResponse authenticate(AuthenticationRequest request);
	
	void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException;
	
	
	AuthenticationResponse verifyCode(VerificationRequest verificationRequest);
	

}
