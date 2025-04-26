package com.project.Notering.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
@Getter
@AllArgsConstructor
public enum AlarmType {
    NEW_COMMENNT_ON_POST("new comment"),
    NEW_LIKE_ON_POST("new post");

    private final String alarmText;
}
