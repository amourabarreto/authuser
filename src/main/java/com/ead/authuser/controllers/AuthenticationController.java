package com.ead.authuser.controllers;

import com.ead.authuser.dtos.UserDto;
import com.ead.authuser.enums.UserStatus;
import com.ead.authuser.enums.UserType;
import com.ead.authuser.models.UserModel;
import com.ead.authuser.services.UserService;
import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.ZoneId;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("/auth")
public class AuthenticationController {

    @Autowired
    UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<Object> registerUser(@RequestBody
                                                @Validated(UserDto.UserView.RegistrationPost.class)
                                                @JsonView(UserDto.UserView.RegistrationPost.class)
                                                           UserDto userDto){
        if(userService.existsByUserName(userDto.getUsername())){
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Error: username já cadastrado!");
        }

        if(userService.existsByEmail(userDto.getEmail())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Error: email já cadastrado!");
        }

        //Só a critério de demonstração
        var userModel = new UserModel();
        BeanUtils.copyProperties(userDto,userModel);
        userModel.setUserStatus(UserStatus.ACTIVE);
        userModel.setUserType(UserType.STUDENT);
        userService.save(userModel);
      return ResponseEntity.status(HttpStatus.CREATED).body(userModel);
    }

}
