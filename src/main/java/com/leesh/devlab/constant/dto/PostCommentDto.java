package com.leesh.devlab.constant.dto;

import com.leesh.devlab.domain.comment.Comment;
import com.querydsl.core.annotations.QueryProjection;

public record PostCommentDto(Long id, String contents, AuthorDto author, long likeCount, Long createdAt, Long modifiedAt) {

    @QueryProjection
    public PostCommentDto {
    }

    public static PostCommentDto from(Comment comment) {
        return new PostCommentDto(
                comment.getId(),
                comment.getContents(),
                new AuthorDto(comment.getMember().getId(), comment.getMember().getNickname()),
                comment.getLikes().size(),
                comment.getCreatedAt(),
                comment.getModifiedAt()
        );
    }

}
