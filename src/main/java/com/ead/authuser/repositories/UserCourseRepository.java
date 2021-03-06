package com.ead.authuser.repositories;

import com.ead.authuser.models.UserCourseModel;
import com.ead.authuser.models.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface UserCourseRepository extends JpaRepository<UserCourseModel, UUID> {

    boolean existsByUserAndCourseId(UserModel userModel, UUID courseId);

    boolean existsByCourseId(UUID courseId);

    void deleteAllByCourseId(UUID courseId);

    @Query(value = "SELECT userCourseModel from UserCourseModel userCourseModel where userCourseModel.user.userId = :userId")
    List<UserCourseModel> findAllUserCourseIntoCourse(@Param("userId") UUID userId);
}
