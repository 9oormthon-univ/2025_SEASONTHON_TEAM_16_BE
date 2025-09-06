package com.seasonthon.YEIN.pet.domain.repository;

import com.seasonthon.YEIN.pet.domain.Pet;
import com.seasonthon.YEIN.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PetRepository extends JpaRepository<Pet, Long> {
    Optional<Pet> findByUser(User user);
}
