package com.leesh.devlab.domain.post.repository;

import com.leesh.devlab.domain.post.Category;
import com.leesh.devlab.dto.PostInfo;
import com.leesh.devlab.dto.QPostInfo;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static com.leesh.devlab.domain.comment.QComment.comment;
import static com.leesh.devlab.domain.hashtag.QHashtag.hashtag;
import static com.leesh.devlab.domain.like.QLike.like;
import static com.leesh.devlab.domain.member.QMember.member;
import static com.leesh.devlab.domain.post.QPost.post;
import static com.leesh.devlab.domain.tag.QTag.tag;
import static com.querydsl.core.group.GroupBy.groupBy;
import static com.querydsl.core.types.ExpressionUtils.count;
import static com.querydsl.core.types.dsl.Expressions.numberTemplate;
import static com.querydsl.core.types.dsl.Expressions.stringTemplate;
import static io.micrometer.common.util.StringUtils.isBlank;

@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostRepositoryImpl implements PostRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<PostInfo> getPostInfoByPaging(Category category, Pageable pageable, String keyword) {

        List<PostInfo> postInfos = queryFactory
                .from(post)
                .innerJoin(post.member, member)
                .leftJoin(post.hashtags, hashtag)
                .leftJoin(hashtag.tag, tag)
                .where(categoryEq(category))
                .where(keywordEq(keyword))
                .groupBy(post.id)
                .orderBy(getOrderBy(pageable.getSort()))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .transform(groupBy(post.id).list(
                        new QPostInfo(
                                post.id,
                                post.title,
                                post.contents,
                                post.category,
                                post.createdAt,
                                post.modifiedAt,
                                member.nickname,
                                ExpressionUtils.as(
                                        JPAExpressions
                                                .select(count(comment.id))
                                                .from(comment)
                                                .where(comment.post.eq(post)),
                                        "commentCount"),
                                ExpressionUtils.as(
                                        JPAExpressions
                                                .select(count(like.id))
                                                .from(like)
                                                .where(like.post.eq(post)),
                                        "likeCount"),
                                stringTemplate("group_concat({0})", tag.name).as("tags")
                        )
                ));

        int totalSize = queryFactory
                .select(post.count())
                .from(post)
                .leftJoin(post.hashtags, hashtag)
                .leftJoin(hashtag.tag, tag)
                .where(categoryEq(category))
                .where(keywordEq(keyword))
                .groupBy(post.id)
                .fetch()
                .size();

        /**
         * 1. 태크 , 로 붙어서오는 문제
         * 2. 태그 누르면 제목 , 내용 말고 오직 태그만 검색어에 되도록
         */

        return new PageImpl<>(postInfos, pageable, totalSize);
    }

    private BooleanExpression keywordEq(String keyword) {

        if (isBlank(keyword)) {
            return null;
        }

        String decodedKeyword = URLDecoder.decode(keyword, StandardCharsets.UTF_8);
        return numberTemplate(Double.class, "function('matches', {0}, {1}, {2})", post.title, post.contents, "\"" + decodedKeyword + "\"").gt(0)
                .or(numberTemplate(Double.class, "function('match', {0}, {1})", tag.name, "\"" + decodedKeyword + "\"").gt(0));
    }

    private static BooleanExpression categoryEq(Category category) {
        return category == null ?  null : post.category.eq(category);
    }

    private OrderSpecifier<?>[] getOrderBy(Sort sort) {

        final List<OrderSpecifier<?>> orders = new ArrayList<>();

        for (Sort.Order order : sort) {

            Order direction = order.isAscending() ? Order.ASC : Order.DESC;

            switch (order.getProperty()) {
                case "like_count" -> orders.add(new OrderSpecifier<>(direction, post.likes.size()));
                case "created_at" -> orders.add(new OrderSpecifier<>(direction, post.createdAt));
                case "modified_at" -> orders.add(new OrderSpecifier<>(direction, post.modifiedAt));
                default -> {}
            }
        }

        return orders.toArray(OrderSpecifier[]::new);
    }
}
