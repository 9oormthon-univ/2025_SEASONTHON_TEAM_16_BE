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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Scrap> scraps = new ArrayList<>();

    /**
     * Creates a Post with its content, optional metadata, and owning user.
     *
     * @param quote    the post text (non-null, persisted as TEXT)
     * @param author   optional author name to display with the post
     * @param imageUrl optional URL of an image associated with the post
     * @param user     the owning User of this post (must be non-null)
     */
    @Builder
    public Post(String quote, String author, String imageUrl, User user) {
        this.quote = quote;
        this.author = author;
        this.imageUrl = imageUrl;
        this.user = user;
    }

    /**
     * Update the post's content fields.
     *
     * @param quote   New quote text; must be non-null (persisted as TEXT).
     * @param author  New author name; may be null.
     * @param imageUrl New image URL; may be null.
     */
    public void updatePost(String quote, String author, String imageUrl) {
        this.quote = quote;
        this.author = author;
        this.imageUrl = imageUrl;
    }

    /**
     * Increments the post's view count by one.
     */
    public void incrementViewCount() {
        this.viewCount++;
    }

    /**
     * Increments this post's scrap count by one.
     *
     * <p>Updates the entity's scrapCount field; intended to be called when a user scraps the post.</p>
     */
    public void incrementScrapCount() {
        this.scrapCount++;
    }

    /**
     * Decrements the post's scrapCount by one if it's greater than zero.
     *
     * Ensures the scrapCount never becomes negative.
     */
    public void decrementScrapCount() {
        if (this.scrapCount > 0) {
            this.scrapCount--;
        }
    }
}
