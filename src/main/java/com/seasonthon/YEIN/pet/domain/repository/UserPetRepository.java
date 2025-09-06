package com.seasonthon.YEIN.pet.domain.repository;

import com.seasonthon.YEIN.pet.domain.PetType;
import com.seasonthon.YEIN.pet.domain.UserPet;
import com.seasonthon.YEIN.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserPetRepository extends JpaRepository<UserPet, Long> {
    Optional<UserPet> findByUserAndPetType(User user, PetType petType);
    List<UserPet> findByUser(User user);
}
