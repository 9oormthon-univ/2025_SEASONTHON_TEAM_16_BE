package com.seasonthon.YEIN.recommendation.api.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "오늘의 문구 응답")
public record TodayQuoteResponse(
        @Schema(description = "문구", example = "인생은 아름다워")
        String quote
) {
    public static TodayQuoteResponse from(String quote) {
        return new TodayQuoteResponse(quote);
    }

    public static TodayQuoteResponse defaultQuote() {
        return new TodayQuoteResponse("아침에 떠올린 작은 긍정 하나가 당신의 하루를 바꿀 수 있다.");
    }
}
