package com.project.Notering.service;


import com.project.Notering.exception.ErrorCode;
import com.project.Notering.exception.NoteringApplicationException;
import com.project.Notering.model.Post;
import com.project.Notering.model.entity.PostEntity;
import com.project.Notering.model.entity.UserEntity;
import com.project.Notering.repository.PostEntityRepository;
import com.project.Notering.repository.UserEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostEntityRepository postEntityRepository;
    private final UserEntityRepository userEntityRepository;

    @Transactional
    public void create(String title, String body, String userName) {

        // user find
        UserEntity userEntity = userEntityRepository.findByUserName(userName)
                .orElseThrow(() -> new NoteringApplicationException(ErrorCode.USER_NOT_FOUND,
                        String.format("%s not founded", userName)));


        // post save
        postEntityRepository.save(PostEntity.of(title, body, userEntity));

        // return
    }

    @Transactional
    public Post modify(String title, String body, String userName, Integer postId) {
        // user find
        UserEntity userEntity = userEntityRepository.findByUserName(userName)
                .orElseThrow(() -> new NoteringApplicationException(ErrorCode.USER_NOT_FOUND,
                        String.format("%s not founded", userName)));

        // post exist
        PostEntity postEntity = postEntityRepository.findById(postId)
                .orElseThrow(() -> new NoteringApplicationException(ErrorCode.POST_NOT_FOUND,
                            String.format("%s not founded", postId)));


        // post permission
        if(postEntity.getUser() != userEntity){
            throw new NoteringApplicationException(ErrorCode.INVALID_PERMISSION,
                    String.format("%s is not permitted with %s", userName, postId));
        }

        postEntity.setTitle(title);
        postEntity.setBody(body);

        return Post.fromEntity(postEntityRepository.saveAndFlush(postEntity));


    }

    @Transactional
    public void delete(String userName, Integer postId) {
        // user find
        UserEntity userEntity = userEntityRepository.findByUserName(userName)
                .orElseThrow(() -> new NoteringApplicationException(ErrorCode.USER_NOT_FOUND,
                        String.format("%s not founded", userName)));

        // post exist
        PostEntity postEntity = postEntityRepository.findById(postId)
                .orElseThrow(() -> new NoteringApplicationException(ErrorCode.POST_NOT_FOUND,
                        String.format("%s not founded", postId)));


        // post permission
        if(postEntity.getUser() != userEntity){
            throw new NoteringApplicationException(ErrorCode.INVALID_PERMISSION,
                    String.format("%s is not permitted with %s", userName, postId));
        }

        postEntityRepository.delete(postEntity);
    }

    public Page<Post> list(Pageable pageable) {
        return postEntityRepository.findAll(pageable).map(Post::fromEntity);
    }

    public Page<Post> my(String userName, Pageable pageable) {
        UserEntity userEntity = userEntityRepository.findByUserName(userName)
                .orElseThrow(() -> new NoteringApplicationException(ErrorCode.USER_NOT_FOUND,
                        String.format("%s not founded", userName)));

        return postEntityRepository.findAllByUser(userEntity, pageable).map(Post::fromEntity);
    }


}
