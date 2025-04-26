package com.project.Notering.model.entity;

import com.project.Notering.model.AlarmArgs;
import com.project.Notering.model.AlarmType;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.type.SqlTypes;
import java.sql.Timestamp;
import java.time.Instant;



@Setter
@Getter
@Entity
@SQLDelete(sql = "UPDATE \"alarm\" SET deleted_at = NOW() where id=?")
@Table(name = "\"alarm\"", indexes={
        @Index(name = "user_id_idx", columnList = "user_id")
})
@NoArgsConstructor
public class AlarmEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // 알람을 받은 사람에 대한 정보
    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;

    // 알람의 타입
    @Enumerated(EnumType.STRING)
    private AlarmType alarmType;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private AlarmArgs args;
    // 알람을 눌렀을 때 해당 포스트로 이동을 할 수 있다.
    // 일단 누른 사람, 어느 포스트에서 발생했는지, 알람이 발생시킨 주체(유저, 포스트, 코멘트 등)가 필요하다.
    // 확장을 하거나 db 마이그레이션 시 필요하여 하나 생성해줌.

    // postgresql에서 지정한 타입이 아니기 때문에 json으로 변환을 해준다.

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

    public static AlarmEntity of(UserEntity user, AlarmType alarmType, AlarmArgs args) {
        AlarmEntity entity = new AlarmEntity();
        entity.setUser(user);
        entity.setAlarmType(alarmType);
        entity.setArgs(args);

        return entity;
    }
}
