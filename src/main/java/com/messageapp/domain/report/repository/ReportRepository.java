package com.messageapp.domain.report.repository;

import com.messageapp.domain.report.entity.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {

    /**
     * 중복 신고 여부 확인
     */
    boolean existsByLetterIdAndReporterId(Long letterId, Long reporterId);
}
