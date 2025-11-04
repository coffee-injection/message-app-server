package com.messageapp.api.letter;

import com.messageapp.domain.letter.dto.LetterResponse;
import com.messageapp.domain.letter.service.LetterService;
import com.messageapp.global.auth.LoginMember;
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
    public List<LetterResponse> getReceivedLetters(@LoginMember Long memberId) {
        return letterService.getReceivedLetters(memberId);
    }
}
