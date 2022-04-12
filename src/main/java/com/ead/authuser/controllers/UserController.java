package com.ead.authuser.controllers;

import com.ead.authuser.dtos.UserDto;
import com.ead.authuser.models.UserModel;
import com.ead.authuser.services.UserService;
import com.ead.authuser.specifications.SpecificationTemplate;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Log4j2
@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("/users")
public class UserController {

    public static final String USER_NOT_FOUND = "User not found!";
    @Autowired
    UserService userService;

    @GetMapping
    public ResponseEntity<Page<UserModel>> getAllUsers(
                                    SpecificationTemplate.UserSpec spec,
                                    @PageableDefault(page = 0, size=10,sort ="userId", direction = Sort.Direction.ASC) Pageable pageable,
                                    @RequestParam(required = false) UUID courseId){
        Page<UserModel> userModelPage = null;
        if(courseId!=null){
            userModelPage = userService.findAll(SpecificationTemplate.userCourseId(courseId).and(spec),pageable);
        }else{
            userModelPage = userService.findAll(spec,pageable);
        }
        if(!userModelPage.isEmpty()){
            userModelPage.toList().stream().forEach(user -> user.add(linkTo(methodOn(UserController.class).getOneUser(user.getUserId())).withSelfRel()));
        }
        return  ResponseEntity.status(HttpStatus.OK).body(userModelPage);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Object> getOneUser(@PathVariable(value = "userId") UUID userId){
        Optional<UserModel> userModelOptional = userService.findById(userId);
        if(userModelOptional.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(USER_NOT_FOUND);
        }else{
            return ResponseEntity.status(HttpStatus.OK).body(userModelOptional.get());
        }
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Object> deleteUser(@PathVariable(value="userId") UUID userId){

        log.debug("DELETE deleteUser userID received {} ",userId);
        Optional<UserModel> userModelOptional = userService.findById(userId);
        if(userModelOptional.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(USER_NOT_FOUND);
        }else{
            userService.delete(userModelOptional.get());
            log.debug("DELETE deleteUser userID deleted {} ",userId);
            log.info("User deleted successfuly userId {} ",userId);
            return ResponseEntity.status(HttpStatus.OK).body("User deleted success!");
        }
    }

    @PutMapping("/{userId}")
    public ResponseEntity<Object> updateUser(@PathVariable(value="userId") UUID userId,
                                             @RequestBody
                                             @Validated(UserDto.UserView.UserPut.class)
                                             @JsonView(UserDto.UserView.UserPut.class)
                                             UserDto userDto){
        log.debug("PUT updateUser userDto received {} ",userDto.toString());
        Optional<UserModel> userModelOptional = userService.findById(userId);
        if(userModelOptional.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(USER_NOT_FOUND);
        }else{
            var userModel = userModelOptional.get();
            userModel.setFullName(userDto.getFullName());
            userModel.setPhoneNumber(userDto.getPhoneNumber());
            userModel.setCpf(userDto.getCpf());
            userService.save(userModel);
            log.debug("PUT updateUser userId saved {} ",userModel.getUserId());
            log.debug("PUT updated seccessfuly userId {} ",userModel.getUserId());
            return ResponseEntity.status(HttpStatus.OK).body(userModel);
        }
    }

    @PutMapping("/{userId}/password")
    public ResponseEntity<Object> updatePassword(@PathVariable(value="userId") UUID userId,
                                                 @RequestBody
                                                 @Validated(UserDto.UserView.PasswordPut.class)
                                                 @JsonView(UserDto.UserView.PasswordPut.class)
                                                     UserDto userDto){
        Optional<UserModel> userModelOptional = userService.findById(userId);
        if(userModelOptional.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(USER_NOT_FOUND);
        }
        if(!userModelOptional.get().getPassword().equals(userDto.getOldPassword())){
            log.warn("A senha antiga está diferente! {}  ",userDto.getUserId());
            return ResponseEntity.status(HttpStatus.CONFLICT).body("A senha antiga está diferente!");
        } else{
            var userModel = userModelOptional.get();
            userModel.setPassword(userDto.getPassword());
            userService.save(userModel);
            return ResponseEntity.status(HttpStatus.OK).body("Senha atualizada com sucesso!");
        }
    }

    @PutMapping("/{userId}/image")
    public ResponseEntity<Object> updateImage(@PathVariable(value="userId") UUID userId,
                                              @RequestBody
                                              @Validated(UserDto.UserView.ImagePut.class)
                                              @JsonView(UserDto.UserView.ImagePut.class)
                                                         UserDto userDto){
        Optional<UserModel> userModelOptional = userService.findById(userId);
        if(userModelOptional.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(USER_NOT_FOUND);
        }else{
            var userModel = userModelOptional.get();
            userModel.setImageUrl(userDto.getImageUrl());
            userService.save(userModel);
            return ResponseEntity.status(HttpStatus.OK).body(userModel);
        }
    }

}
