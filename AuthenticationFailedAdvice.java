package com.upgrad.FoodOrderingApp.api.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.upgrad.FoodOrderingApp.service.exception.AuthenticationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.SignUpRestrictedException;

@ControllerAdvice
public class AuthenticationFailedAdvice {

	@ResponseBody
	@ExceptionHandler(SignUpRestrictedException.class)
	@ResponseStatus(code = HttpStatus.FORBIDDEN)
	AuthenticationFailedException authenticationFailedHandler(AuthenticationFailedException ex) {
		return ex;
	}

}
