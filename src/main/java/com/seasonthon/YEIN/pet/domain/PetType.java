package com.seasonthon.YEIN.pet.domain;

import lombok.Getter;

@Getter
public enum PetType {
    DEFAULT("예인이"),  // 기본 펫
    TYPE_1("코코"),    // 1번 펫 
    TYPE_2("모모"),    // 2번 펫
    TYPE_3("루루"),    // 3번 펫
    TYPE_4("토토");    // 4번 펫

    private final String defaultName;

    PetType(String defaultName) {
        this.defaultName = defaultName;
    }
}
