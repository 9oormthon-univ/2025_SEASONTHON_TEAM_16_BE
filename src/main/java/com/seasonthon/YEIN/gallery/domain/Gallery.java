package com.seasonthon.YEIN.gallery.domain;

import com.seasonthon.YEIN.global.entity.BaseEntity;
import com.seasonthon.YEIN.user.domain.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "galleries")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Gallery extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "total_score")
    private Integer totalScore;

    @Column(name = "alignment_score")
    private Integer alignmentScore;

    @Column(name = "spacing_score")
    private Integer spacingScore;

    @Column(name = "consistency_score")
    private Integer consistencyScore;

    @Column(name = "length_score")
    private Integer lengthScore;

    @Column(name = "feedback", columnDefinition = "TEXT")
    private String feedback;

    @Column(name = "strengths", columnDefinition = "TEXT")
    private String strengths;

    @Column(name = "detailed_analysis", columnDefinition = "TEXT")
    private String detailedAnalysis;

    @Column(name = "title")
    private String title;

    @ElementCollection(targetClass = MoodTag.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "gallery_mood_tags", joinColumns = @JoinColumn(name = "gallery_id"))
    @Column(name = "mood_tag", nullable = false)
    @Enumerated(EnumType.STRING)
    private Set<MoodTag> moodTags = new HashSet<>();

    @Builder
    public Gallery(User user, String imageUrl, Integer alignmentScore, Integer spacingScore,
                   Integer consistencyScore, Integer lengthScore, Integer totalScore,
                   String feedback, String strengths, String detailedAnalysis, String title, Set<MoodTag> moodTags) {
        this.user = user;
        this.imageUrl = imageUrl;
        this.alignmentScore = alignmentScore;
        this.spacingScore = spacingScore;
        this.consistencyScore = consistencyScore;
        this.lengthScore = lengthScore;
        this.totalScore = totalScore;
        this.feedback = feedback;
        this.strengths = strengths;
        this.detailedAnalysis = detailedAnalysis;
        this.title = title;
        this.moodTags = moodTags != null ? moodTags : new HashSet<>();
    }
}
