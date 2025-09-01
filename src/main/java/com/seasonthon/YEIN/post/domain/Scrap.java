package com.seasonthon.YEIN.post.domain;

import com.seasonthon.YEIN.global.entity.BaseEntity;
import com.seasonthon.YEIN.user.domain.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "scraps", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "post_id"})
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Scrap extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    /**
     * Creates a Scrap linking a User to a Post.
     *
     * <p>Prefer using the Lombok-generated builder; this constructor initializes the association
     * between the given user and post. Both parameters are required and persisted as non-nullable
     * foreign keys.
     *
     * @param user the user who created the scrap (must not be null)
     * @param post the post being scrapped (must not be null)
     */
    @Builder
    public Scrap(User user, Post post) {
        this.user = user;
        this.post = post;
    }
}
