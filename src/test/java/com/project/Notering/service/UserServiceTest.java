package com.project.Notering.service;

import com.project.Notering.exception.ErrorCode;
import com.project.Notering.exception.NoteringApplicationException;
import com.project.Notering.fixture.UserEntityFixture;
import com.project.Notering.model.entity.UserEntity;
import com.project.Notering.repository.UserEntityRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest
public class UserServiceTest {

    @Autowired
    private UserService userService;

    @MockBean
    private UserEntityRepository userEntityRepository;

    @MockBean
    private BCryptPasswordEncoder encoder;

    @Test
    void register(){
        String userName = "userName";
        String password = "password";

        // mocking
        when(userEntityRepository.findByUserName(userName)).thenReturn(Optional.empty());
        when(encoder.encode(password)).thenReturn("encrypt_password");
        when(userEntityRepository.save(any())).thenReturn(UserEntityFixture.get(userName, password, 1));

        Assertions.assertDoesNotThrow(() -> userService.join(userName, password));
    }

    @Test
    void register_fail_already_exist_username(){
        String userName = "userName";
        String password = "password";
        UserEntity fixture = UserEntityFixture.get(userName, password, 1);
        // mocking
        when(userEntityRepository.findByUserName(userName)).thenReturn(Optional.of(fixture));
        when(encoder.encode(password)).thenReturn("encrypt_password");
        when(userEntityRepository.save(any())).thenReturn(Optional.of(fixture));

        NoteringApplicationException e  = Assertions.assertThrows(NoteringApplicationException.class, () -> userService.join(userName, password));
        Assertions.assertEquals(ErrorCode.DUPLICATE_USER_NAME, e.getErrorCode());
    }

    @Test
    void login(){
        String userName = "userName";
        String password = "password";

        UserEntity fixture = UserEntityFixture.get(userName, password, 1);

        // mocking
        when(userEntityRepository.findByUserName(userName)).thenReturn(Optional.of(fixture));
        when(encoder.matches(password, fixture.getPassword())).thenReturn(true);
        Assertions.assertDoesNotThrow(() -> userService.login(userName, password));
    }

    @Test
    void login_fail_not_found_username(){
        String userName = "userName";
        String password = "password";

        // mocking
        when(userEntityRepository.findByUserName(userName)).thenReturn(Optional.empty());

        NoteringApplicationException e = Assertions.assertThrows(NoteringApplicationException.class, () -> userService.login(userName, password));

        Assertions.assertEquals(ErrorCode.USER_NOT_FOUND, e.getErrorCode());
    }

    @Test
    void login_fail_invalid_username(){
        String userName = "userName";
        String password = "password";
        String wrongPassword = "wrongPassword";

        UserEntity fixture = UserEntityFixture.get(userName, password, 1);

        // mocking
        when(userEntityRepository.findByUserName(userName)).thenReturn(Optional.of(fixture));

        NoteringApplicationException e = Assertions.assertThrows(NoteringApplicationException.class, () -> userService.login(userName, wrongPassword));
        Assertions.assertEquals(ErrorCode.INVALID_PASSWORD, e.getErrorCode());
    }

}
