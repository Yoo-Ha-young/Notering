package com.project.Notering.repository;

import com.project.Notering.model.entity.PostEntity;
import com.project.Notering.model.entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PostEntityRepository extends JpaRepository<PostEntity, Integer> {


    @Query("SELECT p FROM PostEntity p WHERE p.user = :user AND p.deletedAt IS NULL")
    Page<PostEntity> findAllByUser(@Param("user") UserEntity userEntity, Pageable pageable);

//    Page<PostEntity> findAllByUser(UserEntity userEntity, Pageable pageable);

}