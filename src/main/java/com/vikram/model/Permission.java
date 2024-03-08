package com.vikram.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum Permission {

	USER_READ("user:read"),
    USER_UPDATE("user:update"),
    USER_CREATE("user:create")
   
    ;
	
	
	 @Getter
	    private final String permission;
	
}
