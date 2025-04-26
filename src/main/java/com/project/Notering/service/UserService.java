package com.project.Notering.service;

import com.project.Notering.exception.ErrorCode;
import com.project.Notering.exception.NoteringApplicationException;
import com.project.Notering.model.Alarm;
import com.project.Notering.model.User;
import com.project.Notering.model.entity.UserEntity;
import com.project.Notering.repository.AlarmEntityRepository;
import com.project.Notering.repository.UserEntityRepository;
import com.project.Notering.utils.JwtTokenUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserEntityRepository userEntityRepository;
    private final AlarmEntityRepository alarmEntityRepository;
    private final BCryptPasswordEncoder encoder;

    @Value("${jwt.secret-key}")
    private String secretKey;

    @Value("${jwt.token.expired-time-ms}")
    private Long expiredTimeMs;


    public User loadUserByUsername(String name) {
        return userEntityRepository.findByUserName(name).map(User::fromEntity).orElseThrow(() ->
                new NoteringApplicationException(ErrorCode.USER_NOT_FOUND, String.format("%s not fonded", name)));
    }



    @Transactional
    public User join(String name, String password) {

        // 회원가입하려는 userName으로 회원가입된 user가 있는지
        userEntityRepository.findByUserName(name
        ).ifPresent(it -> {
            throw new NoteringApplicationException(ErrorCode.DUPLICATE_USER_NAME, String.format("%s is duplicated", name));
        });

        // 회원가입 진행 = user를 등록, db에 저장
        UserEntity userEntity = userEntityRepository.save(UserEntity.of(name, encoder.encode(password)));

        return User.fromEntity(userEntity);
    }

    // TODO : implement
    public String login(String name, String password) {

        // 회원입 여부 체크
        UserEntity userEntity = userEntityRepository.findByUserName(name)
                .orElseThrow(() -> new NoteringApplicationException(
                        ErrorCode.USER_NOT_FOUND, String.format("%s is not founded", name)));

        // 비밀번호 체크 - 등록된 비밀번호와 같은지
            if(!encoder.matches(password, userEntity.getPassword())){
            throw new NoteringApplicationException(ErrorCode.INVALID_PASSWORD);
        }

        // 토큰 생성
        String token = JwtTokenUtils.generateToken(name, secretKey, expiredTimeMs);

        return token;
    }

    public Page<Alarm> alarmList(String name, Pageable pageable) {

        UserEntity userEntity = userEntityRepository.findByUserName(name)
                .orElseThrow(() -> new NoteringApplicationException(
                        ErrorCode.USER_NOT_FOUND, String.format("%s is not founded", name)));

        return alarmEntityRepository
                .findAllByUser(userEntity, pageable).map(Alarm::fromEntity);
    }

}
