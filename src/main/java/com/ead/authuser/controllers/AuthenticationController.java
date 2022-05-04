package com.ead.authuser.controllers;

import com.ead.authuser.configs.security.JwtProvider;
import com.ead.authuser.dtos.JwtDto;
import com.ead.authuser.dtos.LoginDto;
import com.ead.authuser.dtos.UserDto;
import com.ead.authuser.enums.RoleType;
import com.ead.authuser.enums.UserStatus;
import com.ead.authuser.enums.UserType;
import com.ead.authuser.models.RoleModel;
import com.ead.authuser.models.UserModel;
import com.ead.authuser.services.RoleService;
import com.ead.authuser.services.UserService;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Log4j2
@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("/auth")
public class AuthenticationController {

    @Autowired
    UserService userService;

    @Autowired
    RoleService roleService;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    JwtProvider jwtProvider;


    @PostMapping("/signup")
    public ResponseEntity<Object> registerUser(@RequestBody
                                                   @Validated(UserDto.UserView.RegistrationPost.class)
                                                   @JsonView(UserDto.UserView.RegistrationPost.class)
                                                           UserDto userDto) {
        log.debug("POST registerUser userDto received {} ", userDto.toString());
        if (userService.existsByUserName(userDto.getUsername())) {
            log.warn("Username {} já cadastrado!  ", userDto.getUsername());
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Error: username já cadastrado!");
        }

        if (userService.existsByEmail(userDto.getEmail())) {
            log.warn("Email {} já cadastrado!  ", userDto.getEmail());
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Error: email já cadastrado!");
        }
        RoleModel rolerModel = roleService.findByRoleName(RoleType.ROLE_STUDENT)
                .orElseThrow(() -> new RuntimeException("Error: Roler não encotnrada!"));
        userDto.setPassword(passwordEncoder.encode(userDto.getPassword()));
        var userModel = new UserModel();
        BeanUtils.copyProperties(userDto, userModel);
        userModel.setUserStatus(UserStatus.ACTIVE);
        userModel.setUserType(UserType.STUDENT);
        userModel.getRoles().add(rolerModel);
        userService.saveUserAndPublish(userModel);
        log.debug("POST registerUser userId saved {} ", userModel.getUserId());
        log.info("User saved successfuly userId {} ", userModel.getUserId());
        return ResponseEntity.status(HttpStatus.CREATED).body(userModel);
    }

    @PostMapping("/login")
    public ResponseEntity<JwtDto> authenticateUser(@Valid @RequestBody LoginDto loginDto){
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginDto.getUsername(),loginDto.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtProvider.generateJwt(authentication);
        return ResponseEntity.ok(new JwtDto(jwt));
    }

}
