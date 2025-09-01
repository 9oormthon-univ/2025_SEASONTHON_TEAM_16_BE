package com.seasonthon.YEIN.user.domain;

import com.seasonthon.YEIN.post.domain.Post;
import com.seasonthon.YEIN.gallery.domain.Gallery;
import com.seasonthon.YEIN.global.entity.BaseEntity;
import com.seasonthon.YEIN.global.oauth.domain.SocialProvider;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Table(name = "users")
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "nickname")
    private String nickname;

    @Column(name = "profile_image_url")
    private String profileImageUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "role_type", nullable = false)
    private RoleType roleType;

    @Enumerated(EnumType.STRING)
    @Column(name = "social_provider")
    private SocialProvider socialProvider;

    @Column(name = "oauth_id")
    private String oauthId;

    @Column(name = "refresh_token")
    private String refreshToken;

    @Column(name = "last_login_at")
    private LocalDateTime lastLoginAt;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Gallery> galleries = new ArrayList<>();

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Post> communities = new ArrayList<>();

    /**
     * Constructs a User with the provided identity and authentication attributes.
     *
     * <p>Initializes email, nickname, profile image URL, role, social provider, OAuth identifier,
     * and the initial last-login timestamp. Fields such as `id`, `refreshToken`, `galleries`,
     * and `communities` are not set by this constructor.</p>
     *
     * @param email the user's email address (unique)
     * @param nickname the user's display name
     * @param profileImageUrl URL of the user's profile image
     * @param roleType the user's role
     * @param socialProvider the social login provider, if any
     * @param oauthId the provider-specific OAuth identifier
     * @param lastLoginAt initial last-login timestamp (may be null)
     */
    @Builder
    public User(String email, String nickname, String profileImageUrl, RoleType roleType, SocialProvider socialProvider, String oauthId, LocalDateTime lastLoginAt) {
        this.email = email;
        this.nickname = nickname;
        this.profileImageUrl = profileImageUrl;
        this.roleType = roleType;
        this.socialProvider = socialProvider;
        this.oauthId = oauthId;
        this.lastLoginAt = lastLoginAt;
    }

    public void updateRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public void updateNickname(String nickname) {
        this.nickname = nickname;
    }

    public void updateProfileImage(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public void markLogin() {
        this.lastLoginAt = LocalDateTime.now();
    }
}
