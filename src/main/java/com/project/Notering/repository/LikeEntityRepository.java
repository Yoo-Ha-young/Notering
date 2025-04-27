package com.project.Notering.repository;

import com.project.Notering.model.Post;
import com.project.Notering.model.entity.LikeEntity;
import com.project.Notering.model.entity.PostEntity;
import com.project.Notering.model.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface LikeEntityRepository extends JpaRepository<LikeEntity, Integer> {

    Optional<LikeEntity> findByUserAndPost(UserEntity userEntity, PostEntity postEntity);

//    @Query(value = "SELECT COUNT(*) FROM LikeEntity  entity WHERE entity.post =:post")
//    Integer countByPost(@Param("post") PostEntity postEntity);

    long countByPost(PostEntity post);

    List<LikeEntity> findAllByPost(PostEntity postEntity);

    @Transactional
    @Modifying
    @Query("UPDATE LikeEntity entity SET entity.deletedAt = CURRENT_TIMESTAMP where entity.post = :post")
    void deleteAllByPost(@Param("post") PostEntity postEntity);


}
