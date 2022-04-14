package com.ead.authuser.controllers;

import com.ead.authuser.clients.CourseClient;
import com.ead.authuser.dtos.CourseDto;
import com.ead.authuser.dtos.UserCourseDto;
import com.ead.authuser.models.UserModel;
import com.ead.authuser.services.UserCourseService;
import com.ead.authuser.services.UserService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Optional;
import java.util.UUID;

@Log4j2
@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
public class UserCourseController {

    @Autowired
    CourseClient courseClient;

    @Autowired
    UserService userService;

    @Autowired
    UserCourseService userCourseService;

    @GetMapping("/users/{userId}/courses")
    public ResponseEntity<Page<CourseDto>> getAllCoursesByUser(@PageableDefault(page = 0,size = 10, sort = "courseId", direction = Sort.Direction.ASC) Pageable pageable,
                                                               @PathVariable(value = "userId") UUID userId){
        Page<CourseDto> allCoursesByUser= courseClient.getAllCoursesByUser(userId,pageable);
        if(allCoursesByUser.isEmpty()){
            ResponseEntity.status(HttpStatus.NOT_FOUND).body("Insuário não se inscreveu em nenhum curso!");
        }

        return ResponseEntity.status(HttpStatus.OK).body(allCoursesByUser);
    }

    @PostMapping("/users/{userId}/courses/subscription")
    public ResponseEntity<Object> saveSubscriptionUserInCourse(@PathVariable(value = "userId") UUID userId,
                                                                        @RequestBody @Valid UserCourseDto userCourseDto){
        Optional<UserModel> userModelOptional = userService.findById(userId);
        if(userModelOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuário não encontrado!");
        }
        if(userCourseService.existsByUserAndCourseId(userModelOptional.get(),userCourseDto.getCourseId())){
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Essa inscrição já existe!");
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(userCourseService.save(userModelOptional.get().convertToUserCourseModel(userCourseDto.getCourseId())));
    }
}
