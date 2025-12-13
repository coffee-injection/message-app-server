package com.messageapp.api.letter;

import com.messageapp.domain.letter.dto.LetterIdResponse;
import com.messageapp.domain.letter.dto.LetterRequest;
import com.messageapp.domain.letter.dto.LetterResponse;
import com.messageapp.domain.letter.service.LetterService;
import com.messageapp.global.auth.LoginMember;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/letter")
@RestController
public class LetterController {

    private final LetterService letterService;

    /**
     * 수신한 편지 목록 조회
     */
    @GetMapping("/list")
    public List<LetterIdResponse> getReceivedLetters(@LoginMember Long memberId) {
        return letterService.getReceivedLetters(memberId);
    }

    /**
     * 편지 발송 (랜덤 수신자 매칭)
     */
    @PostMapping("/send")
    public LetterResponse sendLetter(
            @LoginMember Long memberId,
            @Valid @RequestBody LetterRequest request) {
        return letterService.sendLetter(memberId, request.getContent());
    }

    /**
     * 편지 상세 조회
     */
    @GetMapping("/{letterId}")
    public LetterResponse getLetterDetail(
            @PathVariable Long letterId,
            @LoginMember Long memberId) {
        return letterService.getLetterDetail(letterId, memberId);
    }
}
