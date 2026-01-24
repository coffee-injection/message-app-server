package com.messageapp.domain.member.entity;

import com.messageapp.global.constant.MemberConstants;
import com.messageapp.global.exception.validation.InvalidIslandNameException;
import com.messageapp.global.exception.validation.InvalidProfileImageIndexException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MemberTest {

    private Member member;

    @BeforeEach
    void setUp() {
        member = Member.builder()
                .email("test@test.com")
                .name("테스트유저")
                .islandName("테스트섬")
                .profileImageIndex(1)
                .oauthId("oauth123")
                .socialInfo("KAKAO")
                .isNew(false)
                .build();
    }

    @Nested
    @DisplayName("updateName 메서드")
    class UpdateNameTest {

        @Test
        @DisplayName("이름을 성공적으로 변경한다")
        void updateName_success() {
            // given
            String newName = "새이름";

            // when
            member.updateName(newName);

            // then
            assertThat(member.getName()).isEqualTo(newName);
        }
    }

    @Nested
    @DisplayName("updateIslandName 메서드")
    class UpdateIslandNameTest {

        @ParameterizedTest
        @ValueSource(strings = {"가", "새섬이름", "12345678"})
        @DisplayName("유효한 섬 이름으로 변경한다")
        void validIslandName_success(String islandName) {
            // when
            member.updateIslandName(islandName);

            // then
            assertThat(member.getIslandName()).isEqualTo(islandName);
        }

        @Test
        @DisplayName("null로 변경할 수 있다")
        void nullIslandName_success() {
            // when
            member.updateIslandName(null);

            // then
            assertThat(member.getIslandName()).isNull();
        }

        @Test
        @DisplayName("빈 문자열로 변경하면 예외가 발생한다")
        void emptyIslandName_throwsException() {
            // when & then
            assertThatThrownBy(() -> member.updateIslandName(""))
                    .isInstanceOf(InvalidIslandNameException.class);
        }

        @Test
        @DisplayName("최대 길이를 초과하면 예외가 발생한다")
        void tooLongIslandName_throwsException() {
            // given
            String tooLong = "가".repeat(MemberConstants.ISLAND_NAME_MAX_LENGTH + 1);

            // when & then
            assertThatThrownBy(() -> member.updateIslandName(tooLong))
                    .isInstanceOf(InvalidIslandNameException.class);
        }
    }

    @Nested
    @DisplayName("updateProfileImageIndex 메서드")
    class UpdateProfileImageIndexTest {

        @ParameterizedTest
        @ValueSource(ints = {1, 10, 21})
        @DisplayName("유효한 인덱스로 변경한다")
        void validIndex_success(int index) {
            // when
            member.updateProfileImageIndex(index);

            // then
            assertThat(member.getProfileImageIndex()).isEqualTo(index);
        }

        @Test
        @DisplayName("null로 변경할 수 있다")
        void nullIndex_success() {
            // when
            member.updateProfileImageIndex(null);

            // then
            assertThat(member.getProfileImageIndex()).isNull();
        }

        @Test
        @DisplayName("최소값 미만이면 예외가 발생한다")
        void belowMinIndex_throwsException() {
            // when & then
            assertThatThrownBy(() -> member.updateProfileImageIndex(0))
                    .isInstanceOf(InvalidProfileImageIndexException.class);
        }

        @Test
        @DisplayName("최대값 초과이면 예외가 발생한다")
        void aboveMaxIndex_throwsException() {
            // when & then
            assertThatThrownBy(() -> member.updateProfileImageIndex(22))
                    .isInstanceOf(InvalidProfileImageIndexException.class);
        }
    }

    @Nested
    @DisplayName("회원 상태 관리")
    class MemberStatusTest {

        @Test
        @DisplayName("초기 상태는 ACTIVE이다")
        void initialStatus_isActive() {
            // then
            assertThat(member.getStatus()).isEqualTo(MemberStatus.ACTIVE);
            assertThat(member.isActive()).isTrue();
        }

        @Test
        @DisplayName("deactivate 후 상태가 INACTIVE가 된다")
        void deactivate_changesStatusToInactive() {
            // when
            member.deactivate();

            // then
            assertThat(member.getStatus()).isEqualTo(MemberStatus.INACTIVE);
            assertThat(member.isActive()).isFalse();
        }

        @Test
        @DisplayName("ban 후 상태가 BANNED가 된다")
        void ban_changesStatusToBanned() {
            // when
            member.ban();

            // then
            assertThat(member.getStatus()).isEqualTo(MemberStatus.BANNED);
            assertThat(member.isActive()).isFalse();
        }
    }
}
