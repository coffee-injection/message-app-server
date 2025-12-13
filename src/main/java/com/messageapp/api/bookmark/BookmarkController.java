package com.messageapp.api.bookmark;

import com.messageapp.domain.bookmark.dto.BookmarkRequest;
import com.messageapp.domain.bookmark.service.BookmarkService;
import com.messageapp.domain.letter.dto.LetterResponse;
import com.messageapp.global.auth.LoginMember;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/bookmark")
@RestController
public class BookmarkController {

    private final BookmarkService bookmarkService;

    /**
     * 북마크에 편지 저장
     */
    @PostMapping
    public void saveLetter(
            @LoginMember Long memberId,
            @RequestBody BookmarkRequest bookmarkRequest
    ) {
        bookmarkService.saveLetter(memberId, bookmarkRequest.getLetterId());
    }

    /**
     * 북마크 편지 리스트 조회
     */
    @GetMapping("/list")
    public List<LetterResponse> getBookmarkList(@LoginMember Long memberId) {
        return bookmarkService.getBookmarkList(memberId);
    }

}
