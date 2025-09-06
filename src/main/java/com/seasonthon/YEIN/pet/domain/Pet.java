package com.seasonthon.YEIN.pet.domain;

import com.seasonthon.YEIN.global.entity.BaseEntity;
import com.seasonthon.YEIN.user.domain.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Pet extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user; // 펫은 사용자당 하나만 존재

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PetType petType; // 펫의 종류

    @Column(nullable = true) // 기본 펫은 이름이 없을 수 있고, 나중에 설정 가능
    private String name;

    @Column(nullable = false)
    private int level; // 현재 레벨

    @Column(nullable = false)
    private int currentXp; // 현재 경험치

    @Column(nullable = false)
    private int evolutionStage; // 진화 단계 (0: 기본, 1: 1차 진화, 2: 2차 진화, 3: 최종 진화)

    @Builder
    public Pet(User user, PetType petType, String name, int level, int currentXp, int evolutionStage) {
        this.user = user;
        this.petType = petType;
        this.name = name;
        this.level = level;
        this.currentXp = currentXp;
        this.evolutionStage = evolutionStage;
    }

    // 펫 타입 및 이름 변경
    public void updatePet(PetType petType, String name) {
        this.petType = petType;
        this.name = name;
    }

    // 경험치 추가 및 레벨업
    public void addXp(int xp) {
        this.currentXp += xp;
    }

    // 레벨업
    public void levelUp() {
        this.level++;
    }

    // 경험치 초기화 (레벨업 후 남은 경험치)
    public void deductXp(int xp) {
        this.currentXp -= xp;
    }

    // 진화
    public void evolve(int stage) {
        this.evolutionStage = stage;
    }
}
