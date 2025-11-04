package com.messageapp.domain.report.service;

import com.messageapp.domain.letter.entity.Letter;
import com.messageapp.domain.letter.repository.LetterRepository;
import com.messageapp.domain.member.entity.Member;
import com.messageapp.domain.member.repository.MemberRepository;
import com.messageapp.domain.report.dto.ReportResponse;
import com.messageapp.domain.report.entity.Report;
import com.messageapp.domain.report.repository.ReportRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReportServiceImpl implements ReportService {

    private final ReportRepository reportRepository;
    private final LetterRepository letterRepository;
    private final MemberRepository memberRepository;

    @Override
    @Transactional
    public ReportResponse reportLetter(Long letterId, Long reporterId, String reason) {
        // 1. 편지 조회
        Letter letter = letterRepository.findById(letterId)
                .orElseThrow(() -> new RuntimeException("신고할 편지를 찾을 수 없습니다."));

        // 2. 신고자 조회
        Member reporter = memberRepository.findById(reporterId)
                .orElseThrow(() -> new RuntimeException("신고자를 찾을 수 없습니다."));

        // 3. 중복 신고 체크
        if (reportRepository.existsByLetterIdAndReporterId(letterId, reporterId)) {
            throw new RuntimeException("이미 신고한 편지입니다.");
        }

        // 4. 신고 생성 및 저장
        Report report = Report.builder()
                .letter(letter)
                .reporter(reporter)
                .reason(reason)
                .build();

        Report savedReport = reportRepository.save(report);

        log.info("편지 신고 접수: reportId = {}, letterId = {}, reporterId = {}",
                savedReport.getId(), letterId, reporterId);

        return ReportResponse.from(savedReport);
    }
}
