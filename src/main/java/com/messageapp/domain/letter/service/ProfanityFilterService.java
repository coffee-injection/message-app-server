package com.messageapp.domain.letter.service;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;

/**
 * 욕설/비속어 필터링 서비스
 *
 * <p>편지 내용에 금칙어가 포함되어 있는지 검사합니다.</p>
 *
 * <h3>금칙어 목록:</h3>
 * <p>{@code resources/profanity-words.txt} 파일에서 로드됩니다.</p>
 *
 * @author MessageApp Team
 * @since 1.0
 */
@Slf4j
@Service
public class ProfanityFilterService {

    private static final String PROFANITY_FILE_PATH = "profanity-words.txt";

    private final Set<String> profanityWords = new HashSet<>();

    /**
     * 애플리케이션 시작 시 금칙어 목록을 로드합니다.
     */
    @PostConstruct
    public void loadProfanityWords() {
        try {
            ClassPathResource resource = new ClassPathResource(PROFANITY_FILE_PATH);
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {

                String line;
                while ((line = reader.readLine()) != null) {
                    String trimmed = line.trim();
                    // 빈 줄이거나 주석(#으로 시작)이면 건너뛰기
                    if (!trimmed.isEmpty() && !trimmed.startsWith("#")) {
                        profanityWords.add(normalize(trimmed));
                    }
                }
            }
            log.info("금칙어 목록 로드 완료: {} 개", profanityWords.size());
        } catch (IOException e) {
            log.error("금칙어 목록 로드 실패: {}", e.getMessage());
        }
    }

    /**
     * 욕설 포함 여부를 확인합니다.
     *
     * @param content 검사할 내용
     * @return true면 욕설 포함 (차단 대상)
     */
    public boolean containsProfanity(String content) {
        if (content == null || content.isEmpty()) {
            return false;
        }

        String normalizedContent = normalize(content);

        for (String word : profanityWords) {
            if (normalizedContent.contains(word)) {
                log.warn("욕설 감지됨 - 쉐도우 밴 처리 대상");
                return true;
            }
        }
        return false;
    }

    /**
     * 문자열을 정규화합니다.
     * <ul>
     *   <li>소문자 변환</li>
     *   <li>공백 제거</li>
     *   <li>특수문자 제거</li>
     * </ul>
     *
     * @param content 원본 문자열
     * @return 정규화된 문자열
     */
    private String normalize(String content) {
        return content
                .toLowerCase()
                .replaceAll("\\s+", "")  // 공백 제거
                .replaceAll("[^a-z가-힣ㄱ-ㅎㅏ-ㅣ0-9]", "");  // 특수문자 제거 (한글, 영문, 숫자만 남김)
    }
}
