package com.messageapp.domain.member.dto;

import com.messageapp.domain.member.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UpdateProfileResponse {

    private Long memberId;
    private String nickname;
    private String islandName;
    private Integer profileImageIndex;

    public static UpdateProfileResponse from(Member member) {
        return new UpdateProfileResponse(
            member.getId(),
            member.getName(),
            member.getIslandName(),
            member.getProfileImageIndex()
        );
    }
}
