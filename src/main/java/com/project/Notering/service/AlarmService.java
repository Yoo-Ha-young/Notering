package com.project.Notering.service;

import com.project.Notering.exception.ErrorCode;
import com.project.Notering.exception.NoteringApplicationException;
import com.project.Notering.repository.EmitterRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
@Service
public class AlarmService {

    private final static Long DEFAULT_TIMEOUT = 60L * 1000 * 60;
    private final static String ALARM_NAME = "alarm";
    private final EmitterRepository emitterRepository;


    public void send(Integer alarmId, Integer userId) {
        emitterRepository.get(userId).ifPresentOrElse(sseEmitter -> {
            try {
                sseEmitter.send(SseEmitter.event().id(alarmId.toString()).name(ALARM_NAME).data("new alarm"));
            } catch (IOException e) {
                emitterRepository.delete(userId);
                throw new NoteringApplicationException(ErrorCode.ALARM_CONNECT_ERROR);
            }
        }, () -> log.info("No emitter founded"));
    }

    public SseEmitter connectAlarm(Integer userId) {

        SseEmitter sseEmitter = new SseEmitter();
        emitterRepository.save(userId, sseEmitter);
        sseEmitter.onCompletion(() -> emitterRepository.delete(userId));
        sseEmitter.onTimeout(() -> emitterRepository.delete(userId));

        try {
            sseEmitter.send(SseEmitter.event().id("id").name(ALARM_NAME).data("connect completed"));
        } catch (IOException e) {
            throw new NoteringApplicationException(ErrorCode.ALARM_CONNECT_ERROR);
        }

        return sseEmitter;
    }

}
