package com.project.Notering.controller;

import com.project.Notering.controller.request.UserJoinRequest;
import com.project.Notering.controller.request.UserLoginRequest;
import com.project.Notering.controller.response.AlarmResponse;
import com.project.Notering.controller.response.Response;
import com.project.Notering.controller.response.UserJoinResponse;
import com.project.Notering.controller.response.UserLoginResponse;
import com.project.Notering.exception.ErrorCode;
import com.project.Notering.exception.NoteringApplicationException;
import com.project.Notering.model.User;
import com.project.Notering.model.entity.UserEntity;
import com.project.Notering.service.AlarmService;
import com.project.Notering.service.UserService;
import com.project.Notering.utils.ClassUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {


    private final UserService userService;
    private final AlarmService alarmService;

    @PostMapping("/join")
    public Response<UserJoinResponse> join(@RequestBody UserJoinRequest request) {
        // join
        User user = userService.join(request.getUserName(), request.getPassword());
        return Response.success(UserJoinResponse.fromUser(user));
    }


    @PostMapping("/login")
    public Response<UserLoginResponse> login(@RequestBody UserLoginRequest request) {
        String token = userService.login(request.getUserName(), request.getPassword());
        return Response.success(new UserLoginResponse(token));
    }

    @GetMapping("/alarm")
    public Response<Page<AlarmResponse>>  alarm(Pageable pageable, Authentication authentication) {
        User user = ClassUtils.getSafeCastInstance(authentication.getPrincipal(), User.class).orElseThrow(() ->
                new NoteringApplicationException(ErrorCode.INTERNAL_SERVER_ERROR,
                        "Casting to User class failed"));

        return Response.success(userService.alarmList(user.getId(), pageable).map(AlarmResponse::fromAlarm));
    }

    @GetMapping("/alarm/subscribe")
    public SseEmitter subscribe(Authentication authentication) {
        User user = ClassUtils.getSafeCastInstance(authentication.getPrincipal(), User.class).orElseThrow(() ->
                new NoteringApplicationException(ErrorCode.INTERNAL_SERVER_ERROR,
                        "Casting to User class failed"));
        return alarmService.connectAlarm(user.getId());
    }
}
