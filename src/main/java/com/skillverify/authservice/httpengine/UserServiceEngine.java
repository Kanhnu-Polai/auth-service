package com.skillverify.authservice.httpengine;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import com.skillverify.authservice.dto.UserCreateReqDto;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class UserServiceEngine {
	
	private final RestClient restClient;
	
	public UserServiceEngine(RestClient.Builder restClient) {
		this.restClient = restClient.baseUrl("http://localhost:8083").build();
	}
	
	
	public boolean callUserService(String email,String token,String role){
		  log.info("Calling UserService to create user for email: {}", email);
		  
		 try {
			 UserCreateReqDto reqDto = new UserCreateReqDto(email, role);
			  
				ResponseEntity<String> response  =  restClient
						  .post()
						  .uri("/api/users/create")
						  .header("Authorization", "Bearer " + token)
						  .contentType(MediaType.APPLICATION_JSON)
						  .body(reqDto)
						  .retrieve()
						  .toEntity(String.class);
				
				if(response.getStatusCode().is2xxSuccessful()) {
					log.info("Successfully created user in UserService for email: {}", email);
					return true;
				}else {
					log.error("Failed to create user in UserService. Status: {}, Response: {}", 
		                      response.getStatusCode(), response.getBody());
					return false;
				}
		} catch (Exception e) {
			
			 log.error("Exception while calling UserService for email: {}: {}", email, e.getMessage());
			 return false;
			

		}
	}

}
