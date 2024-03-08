package com.vikram.model;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.core.authority.SimpleGrantedAuthority;

import lombok.Getter;
import lombok.RequiredArgsConstructor;


import  static com.vikram.model.Permission.USER_CREATE;
import  static com.vikram.model.Permission.USER_READ;
import  static com.vikram.model.Permission.USER_UPDATE;

@RequiredArgsConstructor
public enum Role {

	USER(
			Set.of(
				USER_READ,
				USER_UPDATE,
				USER_CREATE )
			  )

	         ;
	
	@Getter
	  private final Set<Permission> permissions;
	
	public List<SimpleGrantedAuthority> getAuthorities() {
	    var authorities = getPermissions()
	            .stream()
	            .map(permission -> new SimpleGrantedAuthority(permission.getPermission()))
	            .collect(Collectors.toList());
	    authorities.add(new SimpleGrantedAuthority("ROLE_" + this.name()));
	    return authorities;
	  }
	
}
