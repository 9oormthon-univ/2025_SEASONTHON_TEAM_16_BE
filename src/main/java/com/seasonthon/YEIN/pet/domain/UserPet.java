package com.seasonthon.YEIN.pet.domain;

import com.seasonthon.YEIN.global.entity.BaseEntity;
import com.seasonthon.YEIN.user.domain.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter; // Setter 추가

@Entity
@Getter
@Setter // Setter 추가 (비즈니스 로직 내에서만 사용)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserPet extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PetType petType;

    @Column(nullable = true)
    private String name; // 펫 이름은 UserPet에 저장

    @Column(nullable = false)
    private int level;

    @Column(nullable = false)
    private int currentXp;

    @Column(nullable = false)
    private int evolutionStage;

    private static final int MAX_LEVEL = 30;

    // 레벨업에 필요한 경험치 (0-indexed)
    private static final int[] XP_REQUIRED_PER_LEVEL_RANGE = {
            200, 200, 200, 200, 200,
            300, 300, 300, 300, 300,
            400, 400, 400, 400, 400,
            600, 600, 600, 600, 600,
            800, 800, 800, 800, 800,
            1000, 1000, 1000, 1000, 1000
    };

    @Builder
    public UserPet(User user, PetType petType, String name, int level, int currentXp, int evolutionStage) {
        this.user = user;
        this.petType = petType;
        this.name = name;
        this.level = level;
        this.currentXp = currentXp;
        this.evolutionStage = evolutionStage;
    }

    // 펫 타입 및 이름 변경 (UserPet의 속성 변경)
    public void updatePetDetails(String name) {
        this.name = name;
    }

    // 경험치 추가 및 레벨업, 진화 로직을 모두 포함
    public void addXp(int xp) {
        if (this.level >= MAX_LEVEL) {
            return; // 최대 레벨이면 경험치 추가하지 않음
        }

        this.currentXp += xp;

        while (this.level < MAX_LEVEL && this.currentXp >= getRequiredXpForNextLevel()) {
            this.currentXp -= getRequiredXpForNextLevel();
            this.level++;
            checkAndPerformEvolution();
        }
    }

    // 다음 레벨에 필요한 경험치 반환
    public int getRequiredXpForNextLevel() {
        if (this.level >= MAX_LEVEL) {
            return 0;
        }
        // 배열 인덱스는 0부터 시작하므로 level-1
        return XP_REQUIRED_PER_LEVEL_RANGE[this.level - 1];
    }
    
    // 진화 로직
    private void checkAndPerformEvolution() {
        if (this.level == 10 && this.evolutionStage < 1) {
            this.evolutionStage = 1;
        } else if (this.level == 20 && this.evolutionStage < 2) {
            this.evolutionStage = 2;
        } else if (this.level == MAX_LEVEL && this.evolutionStage < 3) {
            this.evolutionStage = 3;
        }
    }
}
