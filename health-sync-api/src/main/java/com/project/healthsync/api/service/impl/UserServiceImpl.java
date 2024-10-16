package com.project.healthsync.api.service.impl;

import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.healthsync.api.commons.CommonMethods;
import com.project.healthsync.api.dao.UserDao;
import com.project.healthsync.api.dto.request.UserRequestDTO;
import com.project.healthsync.api.dto.response.UserResponseDTO;
import com.project.healthsync.api.entites.User;
import com.project.healthsync.api.service.IUserService;

@Service
public class UserServiceImpl implements IUserService {

    private UserDao userDao;

    UserServiceImpl(UserDao userDao) {
        this.userDao = userDao;
    }

    /**
     * Service method to perform validation and create a user.
     *
     * @param userRequest
     * @return
     */
    @Override
    public ResponseEntity<String> createUser(UserRequestDTO userRequest) {
        User user = populateAndSaveUser(userRequest.getFirstName(), userRequest.getLastName(), userRequest.getEmail(), userRequest.getPassword(), userRequest.getPhone(), null);
        return ResponseEntity.created(ServletUriComponentsBuilder.fromCurrentRequest().path("/id/{id}").buildAndExpand(user.getId()).toUri()).build();
    }

    /**
     * Method to update the user.
     *
     * @param userId
     * @param userRequest
     * @return
     */
    @Override
    public ResponseEntity<String> updateUser(Long userId, UserRequestDTO userRequest) {
        User user = this.userDao.getById(userId);
        user = populateAndSaveUser(userRequest.getFirstName(), userRequest.getLastName(), userRequest.getEmail(), userRequest.getPassword(), userRequest.getPhone(), user);
        return ResponseEntity.noContent().build();
    }
    
    @Override
    public ResponseEntity<String> getUser(Long userId) {
        Optional<User> userOpt = userDao.findById(userId);
        String responseBody = "";
        if(userOpt.isPresent()) {
        	User user = userOpt.get();
        	UserResponseDTO userResponseDTO =  new UserResponseDTO(user.getId(),user.getFirstName(), user.getLastName(), user.getEmail(), user.getPhone());	
        	 ObjectMapper om = new ObjectMapper();
              try {
				responseBody = om.writeValueAsString(userResponseDTO);
			} catch (JsonProcessingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
		return ResponseEntity.ok().body(responseBody);
    }

    /**
     * Method to create or update user based on given parameters.
     *
     * @param firstName
     * @param lastName
     * @param email
     * @param password
     * @param phone
     * @param user
     * @return
     */
    private User populateAndSaveUser(String firstName, String lastName, String email, String password, String phone, User user) {
        /*
         * If user is not passed create an empty user object , else update existing.
         */
        if (user == null) {
            user = new User();
        }
        if (firstName != null)
            user.setFirstName(firstName);
        if (lastName != null)
            user.setLastName(lastName);
        if (email != null)
            user.setEmail(email);
        if (password != null)
            user.setPassword(CommonMethods.generatePasswordHash(password));
        if (phone != null)
            user.setPhone(phone);
        return this.userDao.save(user);
    }
}
