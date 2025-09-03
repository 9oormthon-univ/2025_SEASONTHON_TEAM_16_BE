package com.seasonthon.YEIN.post.domain;

import com.seasonthon.YEIN.global.entity.BaseAuditEntity;
import com.seasonthon.YEIN.user.domain.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Table(name = "posts")
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Post extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "quote", nullable = false, columnDefinition = "TEXT")
    private String quote;

    @Column(name = "author")
    private String author;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "view_count", nullable = false)
    private Long viewCount = 0L;

    @Column(name = "scrap_count", nullable = false)
    private Long scrapCount = 0L;

    @Column(name = "like_count", nullable = false)
    private Integer likeCount = 0;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Scrap> scraps = new ArrayList<>();

    @Builder
    public Post(User user, String quote, String author, String imageUrl) {
        this.user = user;
        this.quote = quote;
        this.author = author;
        this.imageUrl = imageUrl;
        this.likeCount = 0;
        this.viewCount = 0L;
        this.scrapCount = 0L;
    }

    public void updatePost(String quote, String author, String imageUrl) {
        this.quote = quote;
        this.author = author;
        this.imageUrl = imageUrl;
    }

    public void incrementViewCount() {
        this.viewCount++;
    }

    public void incrementScrapCount() {
        this.scrapCount++;
    }

    public void decrementScrapCount() {
        if (this.scrapCount > 0) {
            this.scrapCount--;
        }
    }

    public void incrementLikeCount() {
        this.likeCount++;
    }

    public void decrementLikeCount() {
        if (this.likeCount > 0) {
            this.likeCount--;
        }
    }
}
