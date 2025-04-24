package com.project.Notering.fixture;

import com.project.Notering.model.entity.UserEntity;

public class UserEntityFixture {

    public static UserEntity get(String userName, String password, Integer userid) {
        UserEntity result = new UserEntity();
        result.setId(userid);
        result.setUserName(userName);
        result.setPassword(password);

        return result;

    }

}
