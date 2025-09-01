package com.seasonthon.YEIN.ai.application;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.genai.Client;
import com.google.genai.types.Content;
import com.google.genai.types.GenerateContentResponse;
import com.google.genai.types.Part;
import com.seasonthon.YEIN.ai.api.dto.response.HandwritingAnalysisResponse;
import com.seasonthon.YEIN.gallery.domain.Gallery;
import com.seasonthon.YEIN.gallery.domain.repository.GalleryRepository;
import com.seasonthon.YEIN.global.code.status.ErrorStatus;
import com.seasonthon.YEIN.global.exception.GeneralException;
import com.seasonthon.YEIN.global.s3.S3UploadService;
import com.seasonthon.YEIN.user.domain.User;
import com.seasonthon.YEIN.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class HandwritingAnalysisService {

    private final Client geminiClient;
    private final ObjectMapper objectMapper;
    private final S3UploadService s3UploadService;
    private final GalleryRepository galleryRepository;
    private final UserRepository userRepository;

    private static final List<String> ALLOWED_TYPES = Arrays.asList(
            "image/jpeg", "image/jpg", "image/png", "image/webp"
    );
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB

    public HandwritingAnalysisResponse analyzeHandwriting(MultipartFile image, Long userId) {
        String imageUrl = null;

        try {
            // 1. 파일 검증
            validateImageFile(image);

            // 2. S3에 이미지 업로드
            imageUrl = s3UploadService.uploadFile(image);

            // 3. AI 분석 수행
            HandwritingAnalysisResponse response = performAIAnalysis(image);

            User user = getUser(userId);
            saveToGallery(user, imageUrl, response);
            return response;

        } catch (Exception e) {
            // 4. 분석 실패 시 업로드된 이미지 삭제
            if (imageUrl != null) {
                cleanupFailedUpload(imageUrl);
            }

            if (e instanceof GeneralException) {
                throw e;
            } else {
                log.error("손글씨 분석 중 예상치 못한 오류 발생", e);
                throw new GeneralException(ErrorStatus.AI_ANALYSIS_FAILED);
            }
        }
    }

    private void validateImageFile(MultipartFile image) {
        // 파일 존재 확인
        if (image == null || image.isEmpty()) {
            throw new GeneralException(ErrorStatus.FILE_IS_EMPTY);
        }

        // 파일 크기 확인
        if (image.getSize() > MAX_FILE_SIZE) {
            throw new GeneralException(ErrorStatus.FILE_SIZE_EXCEEDED);
        }

        // 파일 타입 확인
        String contentType = image.getContentType();
        if (contentType == null || !ALLOWED_TYPES.contains(contentType.toLowerCase())) {
            throw new GeneralException(ErrorStatus.INVALID_FILE_TYPE);
        }

        // 파일명 확인
        String originalFilename = image.getOriginalFilename();
        if (originalFilename == null || originalFilename.trim().isEmpty()) {
            throw new GeneralException(ErrorStatus.INVALID_FILE_NAME);
        }
    }

    private HandwritingAnalysisResponse performAIAnalysis(MultipartFile image) {
        try {
            // 1. MultipartFile을 바이트 배열로 변환
            byte[] imageData = image.getBytes();

            // 2. Content-Type 결정
            String mimeType = determineImageMimeType(image.getContentType());

            // 3. Gemini API용 Part 생성
            Part textPart = Part.fromText(createAnalysisPrompt());
            Part imagePart = Part.fromBytes(imageData, mimeType);

            // 4. Content 생성
            Content content = Content.fromParts(textPart, imagePart);

            // 5. Gemini API 호출
            GenerateContentResponse response = geminiClient.models.generateContent(
                    "gemini-2.5-flash-lite",
                    content,
                    null
            );

            // 6. 응답 파싱
            String responseText = response.text();
            log.debug("Gemini 원본 응답: {}", responseText);

            return parseAnalysisResponse(responseText);

        } catch (IOException e) {
            log.error("이미지 파일 읽기 실패", e);
            throw new GeneralException(ErrorStatus.FILE_READ_FAILED);
        } catch (Exception e) {
            log.error("AI 분석 호출 실패", e);
            throw new GeneralException(ErrorStatus.AI_ANALYSIS_FAILED);
        }
    }

    private String determineImageMimeType(String contentType) {
        if (contentType == null) return "image/jpeg";

        return switch (contentType.toLowerCase()) {
            case "image/png" -> "image/png";
            case "image/webp" -> "image/webp";
            case "image/jpg", "image/jpeg" -> "image/jpeg";
            default -> "image/jpeg";
        };
    }

    private String createAnalysisPrompt() {
        return """
              이 이미지 속 한국어 손글씨를 분석해서 다음 4가지 기준으로 점수를 매겨주세요:

              1. 정렬 (0-25점): 글자들이 일정한 베이스라인 위에 정렬되어 있는가?
              2. 간격 (0-25점): 글자와 글자 사이의 간격이 일정하고 적절한가?
              3. 일관성 (0-25점): 글자들의 크기가 균등하고 일관된가?
              4. 길이 (0-25점): 필사한 글자의 양이 얼마나 되는가? 글자가 많을수록 높은 점수를 부여해주세요.

              또한 필사한 글의 내용을 읽고 그 의미를 분석해주세요. 글의 주제, 전달하고자 하는 메시지, 감정, 교훈 등을 포함해주세요.

              반드시 아래 JSON 형식으로만 응답해주세요. 다른 텍스트는 포함하지 마세요:

              {
                "alignmentScore": 점수,
                "spacingScore": 점수,
                "consistencyScore": 점수,
                "lengthScore": 점수,
                "totalScore": 총점,
                "feedback": "구체적인 개선사항과 잘한 점을 포함한 피드백",
                "detailedAnalysis": "필사한 글의 의미 분석 - 주제, 메시지, 감정, 교훈 등을 포함"
              }
              """;
    }

    private HandwritingAnalysisResponse parseAnalysisResponse(String responseText) {
        try {
            String jsonText = extractJsonFromResponse(responseText);
            HandwritingAnalysisResponse result = objectMapper.readValue(jsonText, HandwritingAnalysisResponse.class);

            // 응답 검증
            validateAnalysisResponse(result);

            return result;

        } catch (Exception e) {
            log.error("AI 응답 파싱 실패: {}", responseText, e);
            return HandwritingAnalysisResponse.createDefault();
        }
    }

    private void validateAnalysisResponse(HandwritingAnalysisResponse response) {
        if (response.totalScore() < 0 || response.totalScore() > 100) {
            log.warn("비정상적인 총점: {}", response.totalScore());
        }
    }

    private String extractJsonFromResponse(String response) {
        int startIndex = response.indexOf('{');
        int endIndex = response.lastIndexOf('}');
        return (startIndex != -1 && endIndex > startIndex) ? response.substring(startIndex, endIndex + 1) : response;
    }

    private void cleanupFailedUpload(String imageUrl) {
        try {
            s3UploadService.deleteFile(imageUrl);
            log.info("분석 실패로 인한 이미지 정리 완료: {}", imageUrl);
        } catch (Exception deleteException) {
            log.warn("업로드된 이미지 정리 실패: {}", imageUrl, deleteException);
        }
    }

    private void saveToGallery(User user, String imageUrl, HandwritingAnalysisResponse response) {
        try {
            Gallery gallery = Gallery.builder()
                    .user(user)
                    .imageUrl(imageUrl)
                    .alignmentScore(response.alignmentScore())
                    .spacingScore(response.spacingScore())
                    .consistencyScore(response.consistencyScore())
                    .lengthScore(response.lengthScore())
                    .totalScore(response.totalScore())
                    .feedback(response.feedback())
                    .detailedAnalysis(response.detailedAnalysis())
                    .build();

            galleryRepository.save(gallery);

        } catch (Exception e) {
            log.error("갤러리 저장 실패: userId={}, imageUrl={}", user.getId(), imageUrl, e);
        }
    }

    private User getUser(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));
    }
}
