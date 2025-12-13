package com.messageapp.domain.bookmark.entity;

import com.messageapp.domain.letter.entity.Letter;
import com.messageapp.domain.member.entity.Member;
import com.messageapp.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "bookmarks",
        uniqueConstraints = @UniqueConstraint(columnNames = {"member_id", "letter_id"}))
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Bookmark extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "bookmark_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "letter_id", nullable = false)
    private Letter letter;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Builder
    public Bookmark(Letter letter, Member member) {
        this.letter = letter;
        this.member = member;
    }
}
