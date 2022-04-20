package com.ead.authuser.services;

import com.ead.authuser.models.UserModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


public interface UserService {

    UserModel save(UserModel userModel);

    UserModel saveUserAndPublish(UserModel userModel);

    List<UserModel> findAll();

    Optional<UserModel> findById(UUID id);

    void delete(UserModel userModel);

    boolean existsByUserName(String username);

    boolean existsByEmail(String email);

    Page<UserModel> findAll(Pageable pageable);

    Page<UserModel> findAll(Specification<UserModel> spec, Pageable pageable);

    void deleteUser(UserModel userModel);

    UserModel updateUser(UserModel userModel);

    UserModel updatePassword(UserModel userModel);
}
