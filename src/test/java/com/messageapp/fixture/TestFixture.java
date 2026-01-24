package com.messageapp.fixture;

import com.messageapp.api.auth.OauthProvider;
import com.messageapp.domain.auth.dto.OAuthUserInfo;
import com.messageapp.domain.letter.entity.Letter;
import com.messageapp.domain.member.entity.Member;

public class TestFixture {

    public static Member createMember(Long id, String email, String name) {
        Member member = Member.builder()
                .email(email)
                .name(name)
                .islandName("테스트섬")
                .profileImageIndex(1)
                .oauthId("oauth123")
                .socialInfo("KAKAO")
                .isNew(false)
                .build();

        // Reflection to set ID
        try {
            java.lang.reflect.Field idField = Member.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(member, id);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return member;
    }

    public static Member createActiveMember(Long id) {
        return createMember(id, "test" + id + "@test.com", "테스트유저" + id);
    }

    public static OAuthUserInfo createKakaoUserInfo() {
        return OAuthUserInfo.builder()
                .oauthId("12345")
                .email("kakao_12345@kakao.com")
                .provider(OauthProvider.KAKAO)
                .build();
    }

    public static OAuthUserInfo createGoogleUserInfo() {
        return OAuthUserInfo.builder()
                .oauthId("google123")
                .email("google_google123@messageapp.com")
                .provider(OauthProvider.GOOGLE)
                .build();
    }

    public static Letter createLetter(Long id, Member sender, Member receiver, String content) {
        Letter letter = Letter.builder()
                .sender(sender)
                .content(content)
                .build();
        letter.assignReceiver(receiver);

        try {
            java.lang.reflect.Field idField = Letter.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(letter, id);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return letter;
    }
}
