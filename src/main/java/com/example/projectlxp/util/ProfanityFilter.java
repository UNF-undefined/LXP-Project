package com.example.projectlxp.util;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.stereotype.Component;

/**
 * 비속어를 필터링하는 유틸리티 컴포넌트입니다. Service 계층에 주입되어 사용됩니다. (VaneProject의 아이디어를 참고하여, '정규식'과 'Set'을 사용하는
 * 방식으로 구현)
 */
@Component
public class ProfanityFilter {

    private final Set<String> badWords = new HashSet<>();

    private final Pattern pattern;

    private static final List<String> DEFAULT_BAD_WORDS =
            List.of(
                    "바보", "멍청이", "나쁜말", "심한욕", "테스트욕", "examplebadword" // 예시 단어
                    );

    /** ProfanityFilter 생성자 기본 비속어 목록을 Set에 추가하고, 이를 바탕으로 정규식 패턴을 컴파일합니다. */
    public ProfanityFilter() {
        addBadWords(DEFAULT_BAD_WORDS);

        this.pattern =
                Pattern.compile(
                        "\\b(" + String.join("|", this.badWords) + ")\\b",
                        Pattern.CASE_INSENSITIVE // 영문 대소문자 구분 없이
                        );
    }

    /**
     * 비속어 목록을 '동적'으로 추가할 수 있는 메서드 (선택 사항)
     *
     * @param words 추가할 비속어 리스트
     */
    public void addBadWords(List<String> words) {
        this.badWords.addAll(words);
    }

    /**
     * 원본 문자열을 받아 비속어를 필터링합니다. (핵심 로직)
     *
     * @param rawContent 필터링 전의 원본 리뷰 내용
     * @return 비속어가 '*'로 마스킹된 깨끗한 내용
     */
    public String filter(String rawContent) {
        if (rawContent == null || rawContent.isBlank()) {
            return rawContent;
        }

        Matcher matcher = this.pattern.matcher(rawContent);
        StringBuilder sb = new StringBuilder();

        while (matcher.find()) {
            String replacement = "*".repeat(matcher.group(1).length());
            matcher.appendReplacement(sb, replacement);
        }
        matcher.appendTail(sb);

        return sb.toString();
    }
}
