package com.upgrad.FoodOrderingApp.api.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.upgrad.FoodOrderingApp.service.exception.SignUpRestrictedException;

@ControllerAdvice
public class SignUpRestrictedAdvice {

	@ResponseBody
	@ExceptionHandler(SignUpRestrictedException.class)
	@ResponseStatus(code = HttpStatus.BAD_REQUEST)
	SignupCustomerResponse signupCustomerHandler(SignUpRestrictedException ex) {
		return new SignupCustomerResponse(ex.getCode(), ex.getErrorMessage());
	}
}
