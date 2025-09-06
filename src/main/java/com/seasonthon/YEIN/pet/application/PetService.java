package com.seasonthon.YEIN.pet.application;

import com.seasonthon.YEIN.global.code.status.ErrorStatus;
import com.seasonthon.YEIN.global.exception.GeneralException;
import com.seasonthon.YEIN.pet.api.dto.request.PetNameUpdateRequest;
import com.seasonthon.YEIN.pet.api.dto.request.PetUpdateRequest;
import com.seasonthon.YEIN.pet.api.dto.response.PetStatusResponse;
import com.seasonthon.YEIN.pet.domain.PetType;
import com.seasonthon.YEIN.pet.domain.UserPet;
import com.seasonthon.YEIN.pet.domain.repository.UserPetRepository;
import com.seasonthon.YEIN.user.domain.User;
import com.seasonthon.YEIN.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class PetService {

    private final UserPetRepository userPetRepository;
    private final UserRepository userRepository;

    @Transactional
    public PetStatusResponse updatePet(Long userId, PetUpdateRequest request) {
        User user = getUser(userId);

        // 현재 활성화된 펫 타입 변경
        user.updateCurrentPetType(request.petType());
        userRepository.save(user); // User 엔티티 저장하여 currentPetType 변경사항 반영

        // 변경된 펫 타입의 UserPet 엔티티를 찾거나 생성 (이름 변경 없음)
        findOrCreateUserPet(user, request.petType());

        return getPetStatus(userId);
    }

    @Transactional
    public PetStatusResponse updatePetName(Long userId, PetNameUpdateRequest request) {
        User user = getUser(userId);
        UserPet userPet = findOrCreateUserPet(user, user.getCurrentPetType());

        userPet.updatePetDetails(request.name());
        userPetRepository.save(userPet);

        return getPetStatus(userId);
    }

    @Transactional
    public PetStatusResponse addXpToPet(Long userId, int xpGained) {
        User user = getUser(userId);
        UserPet userPet = findOrCreateUserPet(user, user.getCurrentPetType());

        userPet.addXp(xpGained); // 경험치 추가 및 레벨업/진화 로직 위임

        userPetRepository.save(userPet); // UserPet 엔티티 저장
        return getPetStatus(userId);
    }

    @Transactional
    public PetStatusResponse getPetStatus(Long userId) {
        User user = getUser(userId);
        UserPet userPet = findOrCreateUserPet(user, user.getCurrentPetType());

        int xpToNextLevel = userPet.getRequiredXpForNextLevel();

        return PetStatusResponse.builder()
                .name(userPet.getName())
                .petType(userPet.getPetType())
                .level(userPet.getLevel())
                .currentXp(userPet.getCurrentXp())
                .xpToNextLevel(xpToNextLevel)
                .evolutionStage(userPet.getEvolutionStage())
                .build();
    }

    // User와 Pet을 찾는 중복 코드를 제거하기 위한 private 메서드
    private User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));
    }

    // UserPet을 찾거나 새로 생성하는 헬퍼 메서드
    private UserPet findOrCreateUserPet(User user, PetType petType) {
        // 만약 petType이 null이면 DEFAULT로 설정 (새로운 유저의 경우)
        PetType actualPetType = Optional.ofNullable(petType).orElse(PetType.DEFAULT);

        return userPetRepository.findByUserAndPetType(user, actualPetType)
                .orElseGet(() -> {
                    UserPet defaultUserPet = UserPet.builder()
                            .user(user)
                            .petType(actualPetType)
                            .name(actualPetType.getDefaultName()) // 기본 이름 사용
                            .level(1)
                            .currentXp(0)
                            .evolutionStage(0)
                            .build();
                    userPetRepository.save(defaultUserPet);
                    log.info("Default UserPet created for user {} with type {}", user.getId(), actualPetType);

                    // 사용자의 currentPetType이 설정되지 않았다면 설정
                    if (user.getCurrentPetType() == null) {
                        user.updateCurrentPetType(actualPetType);
                        userRepository.save(user); // User 엔티티 저장
                    }
                    return defaultUserPet;
                });
    }
}
