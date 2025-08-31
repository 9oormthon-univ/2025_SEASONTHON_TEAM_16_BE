package com.seasonthon.YEIN.gallery.domain;

import com.seasonthon.YEIN.global.entity.BaseEntity;
import com.seasonthon.YEIN.user.domain.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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

    @Column(name = "detailed_analysis", columnDefinition = "TEXT")
    private String detailedAnalysis;

    @Builder
    public Gallery(User user, String imageUrl, Integer alignmentScore, Integer spacingScore,
                   Integer consistencyScore, Integer lengthScore, Integer totalScore,
                   String feedback, String detailedAnalysis) {
        this.user = user;
        this.imageUrl = imageUrl;
        this.alignmentScore = alignmentScore;
        this.spacingScore = spacingScore;
        this.consistencyScore = consistencyScore;
        this.lengthScore = lengthScore;
        this.totalScore = totalScore;
        this.feedback = feedback;
        this.detailedAnalysis = detailedAnalysis;
    }
}
