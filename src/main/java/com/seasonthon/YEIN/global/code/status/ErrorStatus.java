package com.seasonthon.YEIN.global.code.status;

import com.seasonthon.YEIN.global.code.BaseErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorStatus implements BaseErrorCode {
    // 400 Bad Request
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "COMMON400", "잘못된 요청입니다."),
    VALIDATION_FAILED(HttpStatus.BAD_REQUEST, "VALID4001", "입력값 유효성 검증에 실패했습니다."),
    MISSING_PARAMETER(HttpStatus.BAD_REQUEST, "VALID4002", "필수 파라미터가 누락되었습니다."),
    TYPE_MISMATCH(HttpStatus.BAD_REQUEST, "VALID4003", "파라미터 타입이 올바르지 않습니다."),
    FILE_IS_EMPTY(HttpStatus.BAD_REQUEST, "FILE4004", "업로드할 파일이 없습니다."),
    FILE_DOWNLOAD_FAILED(HttpStatus.BAD_REQUEST, "FILE4005", "이미지 다운로드에 실패했습니다."),
    FILE_SIZE_EXCEEDED(HttpStatus.BAD_REQUEST, "FILE4006", "파일 크기가 10MB를 초과했습니다."),
    INVALID_FILE_TYPE(HttpStatus.BAD_REQUEST, "FILE4007", "지원하지 않는 파일 형식입니다. (JPG, PNG, WEBP만 지원)"),
    INVALID_FILE_NAME(HttpStatus.BAD_REQUEST, "FILE4008", "유효하지 않은 파일명입니다."),
    CANNOT_CHANGE_TO_DEFAULT_PET(HttpStatus.BAD_REQUEST, "PET4009", "기본 펫으로는 변경할 수 없습니다."),

    // 401 Unauthorized
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "AUTH401", "인증이 필요합니다."),
    INVALID_CREDENTIALS(HttpStatus.UNAUTHORIZED, "AUTH4011", "인증 정보가 올바르지 않습니다."),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "AUTH4012", "토큰이 유효하지 않습니다."),
    EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED, "AUTH4013", "토큰이 만료되었습니다."),

    // 403 Forbidden
    FORBIDDEN(HttpStatus.FORBIDDEN, "COMMON403", "접근 권한이 없습니다."),
    ACCESS_DENIED(HttpStatus.FORBIDDEN, "AUTH4031", "권한이 없는 리소스입니다."),

    // 404 Not Found
    NOT_FOUND(HttpStatus.NOT_FOUND, "COMMON404", "요청한 리소스를 찾을 수 없습니다."),
    RESOURCE_NOT_FOUND(HttpStatus.NOT_FOUND, "GEN4041", "리소스를 찾을 수 없습니다."),
    ENDPOINT_NOT_FOUND(HttpStatus.NOT_FOUND, "GEN4042", "존재하지 않는 엔드포인트입니다."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "USER4043", "사용자를 찾을 수 없습니다."),
    PET_NOT_FOUND(HttpStatus.NOT_FOUND, "PET4044", "펫을 찾을 수 없습니다."),
    POST_NOT_FOUND(HttpStatus.NOT_FOUND, "POST4045", "게시글을 찾을 수 없습니다."),
    GALLERY_NOT_FOUND(HttpStatus.NOT_FOUND, "GALLERY4046", "갤러리를 찾을 수 없습니다."),
    COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "COMMENT4047", "댓글을 찾을 수 없습니다."),

    // 405 Method Not Allowed
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "COMMON405", "허용되지 않는 HTTP 메서드입니다."),

    // 409 Conflict
    CONFLICT(HttpStatus.CONFLICT, "COMMON409", "요청이 현재 리소스 상태와 충돌합니다."),
    DUPLICATE_RESOURCE(HttpStatus.CONFLICT, "GEN4091", "이미 존재하는 리소스입니다."),
    VERSION_CONFLICT(HttpStatus.CONFLICT, "GEN4092", "리소스 버전 충돌이 발생했습니다."),

    // 500 Internal Server Error
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON500", "서버 에러, 관리자에게 문의 바랍니다."),
    S3_UPLOAD_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "S35001", "파일 업로드 중 오류가 발생했습니다."),
    S3_DELETE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "S35002", "파일 삭제 중 오류가 발생했습니다."),
    DATABASE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "DB5003", "데이터베이스 처리 중 오류가 발생했습니다."),
    AI_ANALYSIS_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "AI5004", "AI 분석 중 오류가 발생했습니다."),
    FILE_READ_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "FILE5005", "파일 읽기에 실패했습니다."),

    // 502, 503, 504
    BAD_GATEWAY(HttpStatus.BAD_GATEWAY, "COMMON502", "불완전한 게이트웨이 응답을 받았습니다."),
    SERVICE_UNAVAILABLE(HttpStatus.SERVICE_UNAVAILABLE, "COMMON503", "서비스를 일시적으로 사용할 수 없습니다."),
    GATEWAY_TIMEOUT(HttpStatus.GATEWAY_TIMEOUT, "COMMON504", "게이트웨이 연결이 시간 초과되었습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    @Override
    public boolean isSuccess() {
        return false;
    }
}
