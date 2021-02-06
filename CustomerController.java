package com.upgrad.FoodOrderingApp.api.controller;

import java.time.ZonedDateTime;
import java.util.Base64;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.upgrad.FooOrderingApp.database.repository.CustomerRepository;
import com.upgrad.FoodOrderingApp.service.businness.JwtTokenProvider;
import com.upgrad.FoodOrderingApp.service.exception.AuthenticationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.SignUpRestrictedException;

@RestController
@CrossOrigin
public class CustomerController {

	private final CustomerRepository repository;
	private JwtTokenProvider jwtTokenProvider;

	CustomerController(CustomerRepository repository) {
		this.repository = repository;
		jwtTokenProvider = new JwtTokenProvider("Secret"); // TODO: Add secret
	}

	@PostMapping("/customer/signup")
	SignupCustomerResponse signup(@RequestBody SignupCustomerRequest request) {
		if (StringUtils.isNoneEmpty(request.getFirstName()) || StringUtils.isNoneEmpty(request.getEmailAddress())
				|| StringUtils.isNoneEmpty(request.getContactNumber())
				|| StringUtils.isNoneEmpty(request.getPassword())) {
			throw new SignUpRestrictedException("SGR-005", "Except last name all fields should be filled");
		}
		if (repository.isExistingContactNumber(request.getContactNumber())) {
			throw new SignUpRestrictedException("SGR-001",
					"This contact number is already registered! Try other contact number.");
		}
		if (!isValidEmailAddress(request.getEmailAddress())) {
			throw new SignUpRestrictedException("SGR-002", "Invalid email-id format!");
		}
		if (!isValidPhoneNumber(request.getContactNumber())) {
			throw new SignUpRestrictedException("SGR-003", "Invalid contact number!");
		}
		if (!isStrongPassword(request.getPassword)) {
			throw new SignUpRestrictedException("SGR-004", "Weak password!");
		}
	}

	@PostMapping("/customer/login")
	ResponseEntity login(@RequestHeader("authorization") String authHeader) {
		String decodedToken = new String(Base64.getDecoder().decode(authHeader.substring(authHeader.indexOf(" ") + 1)));
		if (decodedToken.split(":").length != 2) {
			throw new AuthenticationFailedException("ATH-003",
					"Incorrect format of decoded customer name and password");
		}
		if (!repository.isExistingContactNumber(decodedToken.split(":")[0])) {
			throw new AuthenticationFailedException("ATH-001", "This contact number has not been registered!");
		}
		if (!repository.validatePassword(decodedToken.split(":")[1])) {
			throw new AuthenticationFailedException("ATH-002", "Invalid Credentials");
		}
		LoginResponse loginResponse = repository.getCustomer(decodedToken.split(":")[0]);
		String jwtToken = jwtTokenProvider.generateToken(customer.getCustomerUuid(), ZonedDateTime.now(),
				ZonedDateTime.now().plusHours(1));
		HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, "access-token");
        headers.add("access-token", jwtToken);
 
        return ResponseEntity.ok()
                       .headers(headers)
                       .body(loginResponse);
		
	}

	public boolean isValidEmailAddress(String email) {
		String ePattern = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";
		java.util.regex.Pattern p = java.util.regex.Pattern.compile(ePattern);
		java.util.regex.Matcher m = p.matcher(email);
		return m.matches();
	}

	private boolean isValidPhoneNumber(String phoneNo) {
		// validate phone numbers of format "1234567890"
		if (phoneNo.matches("\\d{10}"))
			return true;
		else
			return false;

	}

	private boolean isStrongPassword(String password) {

		if (password.length() >= 8) {
			// if it contains one digit
			if (password.matches("(?=.*[0-9]).*")) {
				// if it contains one upper case letter
				if (password.matches("(?=.*[A-Z]).*")) {
					// if it contains one special character
					if (password.matches("(?=.*[#@$%&*!^]).*")) {
						return true;
					}
				}
			}
		}
		return false;

	}

}
