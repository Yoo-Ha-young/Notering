package com.project.Notering.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.sql.Timestamp;
import java.time.Instant;

@Setter
@Getter
@Entity
@SQLDelete(sql = "UPDATE \"comment\" SET deleted_at = NOW() where id=?")
@Table(name = "\"comment\"", indexes={
        @Index(name = "post_id_idx", columnList = "post_id")
        })
@SQLRestriction("deleted_at is NULL")
@NoArgsConstructor
public class CommentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @ManyToOne
    @JoinColumn(name = "post_id")
    private PostEntity post;

    @Column(name = "comment")
    private String comment;

    @Column(name = "registered_at")
    private Timestamp registeredAt;

    @Column(name = "updated_at")
    private Timestamp updatedAt;

    @Column(name = "deleted_at")
    private Timestamp deletedAt;


    @PrePersist
    void registeredAt() {
        this.registeredAt = Timestamp.from(Instant.now());
    }

    @PreUpdate
    void updatedAt() {
        this.updatedAt = Timestamp.from(Instant.now());
    }

    public static CommentEntity of(String comment, PostEntity post, UserEntity user) {
        CommentEntity entity = new CommentEntity();
        entity.setComment(comment);
        entity.setPost(post);
        entity.setUser(user);
        return entity;
    }
}
