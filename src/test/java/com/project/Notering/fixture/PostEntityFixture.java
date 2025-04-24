package com.project.Notering.fixture;

import com.project.Notering.model.User;
import com.project.Notering.model.entity.PostEntity;
import com.project.Notering.model.entity.UserEntity;

public class PostEntityFixture {

    public static PostEntity get(String userName, Integer postId, Integer userId) {
        UserEntity user = new UserEntity();
        user.setId(userId);
        user.setUserName(userName);

        PostEntity result = new PostEntity();
        result.setUser(user);
        result.setId(postId);


        return result;

    }

}
