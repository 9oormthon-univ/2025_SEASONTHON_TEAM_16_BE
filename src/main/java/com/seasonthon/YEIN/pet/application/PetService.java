package com.seasonthon.YEIN.pet.application;

import com.seasonthon.YEIN.global.code.status.ErrorStatus;
import com.seasonthon.YEIN.global.exception.GeneralException;
import com.seasonthon.YEIN.pet.api.dto.request.PetUpdateRequest;
import com.seasonthon.YEIN.pet.api.dto.response.PetStatusResponse;
import com.seasonthon.YEIN.pet.domain.Pet;
import com.seasonthon.YEIN.pet.domain.PetType;
import com.seasonthon.YEIN.pet.domain.repository.PetRepository;
import com.seasonthon.YEIN.user.domain.User;
import com.seasonthon.YEIN.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class PetService {

    private final PetRepository petRepository;
    private final UserRepository userRepository;

    // 레벨업에 필요한 경험치
    private static final int[] XP_REQUIRED_PER_LEVEL_RANGE = {
            200, 200, 200, 200, 200, // Level 1-5
            300, 300, 300, 300, 300, // Level 6-10
            400, 400, 400, 400, 400, // Level 11-15
            600, 600, 600, 600, 600, // Level 16-20
            800, 800, 800, 800, 800, // Level 21-25
            1000, 1000, 1000, 1000, 1000 // Level 26-30
    };
    private static final int MAX_LEVEL = 30;

    @Transactional
    public void createDefaultPet(User user) {
        // 이미 펫이 있는지 확인
        if (petRepository.findByUser(user).isPresent()) {
            log.warn("User {} already has a pet. Skipping default pet creation.", user.getId());
            return;
        }

        Pet defaultPet = Pet.builder()
                .user(user)
                .petType(PetType.DEFAULT)
                .name(null) // 기본 펫은 이름 없음
                .level(1)
                .currentXp(0)
                .evolutionStage(0)
                .build();

        petRepository.save(defaultPet);
        log.info("Default pet created for user {}", user.getId());
    }

    @Transactional
    public PetStatusResponse updatePet(Long userId, PetUpdateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));

        Pet pet = petRepository.findByUser(user)
                .orElseThrow(() -> new GeneralException(ErrorStatus.PET_NOT_FOUND));

        pet.updatePet(request.getPetType(), request.getName());
        // @Transactional에 의해 변경 감지되어 자동 저장됩니다.

        return getPetStatus(userId);
    }

    @Transactional
    public PetStatusResponse addXpToPet(Long userId, int xpGained) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));

        // 펫이 없으면 예외 발생
        Pet pet = petRepository.findByUser(user)
                .orElseThrow(() -> new GeneralException(ErrorStatus.PET_NOT_FOUND));

        pet.addXp(xpGained); // 경험치 추가

        // 레벨업 및 진화 로직
        while (pet.getLevel() < MAX_LEVEL) {
            int xpNeededForNextLevel = getRequiredXpForLevel(pet.getLevel());
            if (pet.getCurrentXp() >= xpNeededForNextLevel) {
                pet.deductXp(xpNeededForNextLevel); // 다음 레벨에 필요한 경험치 차감
                pet.levelUp(); // 레벨업
                log.info("User {}'s pet leveled up to level {}", userId, pet.getLevel());
                checkAndPerformEvolution(pet); // 진화 체크
            } else {
                break; // 다음 레벨업에 필요한 경험치가 부족하면 반복 중단
            }
        }

        petRepository.save(pet); // 변경사항 저장
        return getPetStatus(userId); // 업데이트된 펫 상태 반환
    }

    @Transactional(readOnly = true)
    public PetStatusResponse getPetStatus(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));

        Pet pet = petRepository.findByUser(user)
                .orElseThrow(() -> new GeneralException(ErrorStatus.PET_NOT_FOUND)); // 펫이 없으면 예외 발생

        int xpToNextLevel = (pet.getLevel() < MAX_LEVEL) ? getRequiredXpForLevel(pet.getLevel()) : 0;

        return PetStatusResponse.builder()
                .name(pet.getName())
                .petType(pet.getPetType())
                .level(pet.getLevel())
                .currentXp(pet.getCurrentXp())
                .xpToNextLevel(xpToNextLevel)
                .evolutionStage(pet.getEvolutionStage())
                .build();
    }

    // 특정 레벨에 필요한 총 경험치 계산
    private int getRequiredXpForLevel(int level) {
        if (level < 1 || level > MAX_LEVEL) {
            return Integer.MAX_VALUE; // 유효하지 않은 레벨
        }
        // 배열 인덱스는 0부터 시작하므로 level-1
        return XP_REQUIRED_PER_LEVEL_RANGE[level - 1];
    }

    // 진화 로직
    private void checkAndPerformEvolution(Pet pet) {
        if (pet.getLevel() == 10 && pet.getEvolutionStage() < 1) {
            pet.evolve(1); // 1차
            log.info("User {}'s pet evolved to stage 1 (Level 10)", pet.getUser().getId());
        } else if (pet.getLevel() == 20 && pet.getEvolutionStage() < 2) {
            pet.evolve(2); // 2차
            log.info("User {}'s pet evolved to stage 2 (Level 20)", pet.getUser().getId());
        } else if (pet.getLevel() == MAX_LEVEL && pet.getEvolutionStage() < 3) { // MAX_LEVEL은 30
            pet.evolve(3); // 최종
            log.info("User {}'s pet evolved to final stage 3 (Level 30)", pet.getUser().getId());
        }
    }
}
