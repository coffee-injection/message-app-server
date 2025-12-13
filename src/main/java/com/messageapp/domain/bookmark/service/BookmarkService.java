package com.messageapp.domain.bookmark.service;

import com.messageapp.domain.letter.dto.LetterResponse;

import java.util.List;

public interface BookmarkService {

    void saveLetter(Long memberId, Long letterId);

    List<LetterResponse> getBookmarkList(Long memberId);
}
