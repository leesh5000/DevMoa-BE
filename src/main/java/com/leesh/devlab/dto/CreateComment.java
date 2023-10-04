package com.leesh.devlab.dto;

import com.leesh.devlab.domain.comment.Comment;
import com.leesh.devlab.domain.member.Member;
import com.leesh.devlab.domain.post.Post;

public class CreateComment {

    public record Request(String contents) {

        public Comment toEntity(Post post, Member member) {
            return Comment.builder()
                    .contents(contents)
                    .post(post)
                    .member(member)
                    .build();
        }

    }

    public record Response(Long commentId) {

    }

}
