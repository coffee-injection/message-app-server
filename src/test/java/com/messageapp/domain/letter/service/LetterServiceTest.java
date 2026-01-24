package com.messageapp.domain.letter.service;

import com.messageapp.domain.fcm.service.FcmService;
import com.messageapp.domain.letter.dto.LetterResponse;
import com.messageapp.domain.letter.entity.Letter;
import com.messageapp.domain.letter.entity.LetterStatus;
import com.messageapp.domain.letter.repository.LetterRepository;
import com.messageapp.domain.member.entity.Member;
import com.messageapp.domain.member.repository.MemberRepository;
import com.messageapp.fixture.TestFixture;
import com.messageapp.global.exception.business.letter.LetterAccessDeniedException;
import com.messageapp.global.exception.business.letter.LetterNotFoundException;
import com.messageapp.global.exception.business.letter.NoAvailableReceiverException;
import com.messageapp.global.exception.business.letter.SenderNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class LetterServiceTest {

    @InjectMocks
    private LetterServiceImpl letterService;

    @Mock
    private LetterRepository letterRepository;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private FcmService fcmService;

    @Nested
    @DisplayName("sendLetter 메서드")
    class SendLetterTest {

        @Test
        @DisplayName("편지를 성공적으로 발송한다")
        void sendLetter_success() {
            // given
            Long senderId = 1L;
            String content = "안녕하세요!";
            Member sender = TestFixture.createActiveMember(senderId);
            Member receiver = TestFixture.createActiveMember(2L);

            given(memberRepository.findById(senderId)).willReturn(Optional.of(sender));
            given(memberRepository.findRandomActiveMember(senderId)).willReturn(Optional.of(receiver));
            given(letterRepository.save(any(Letter.class))).willAnswer(invocation -> {
                Letter letter = invocation.getArgument(0);
                try {
                    java.lang.reflect.Field idField = Letter.class.getDeclaredField("id");
                    idField.setAccessible(true);
                    idField.set(letter, 1L);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                return letter;
            });
            doNothing().when(fcmService).sendLetterArrivalNotification(any(), any());

            // when
            LetterResponse response = letterService.sendLetter(senderId, content);

            // then
            assertThat(response).isNotNull();
            verify(letterRepository).save(any(Letter.class));
            verify(fcmService).sendLetterArrivalNotification(receiver.getId(), sender.getName());
        }

        @Test
        @DisplayName("발신자가 존재하지 않으면 예외가 발생한다")
        void senderNotFound_throwsException() {
            // given
            Long senderId = 999L;
            given(memberRepository.findById(senderId)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> letterService.sendLetter(senderId, "안녕하세요"))
                    .isInstanceOf(SenderNotFoundException.class);
        }

        @Test
        @DisplayName("수신 가능한 회원이 없으면 예외가 발생한다")
        void noAvailableReceiver_throwsException() {
            // given
            Long senderId = 1L;
            Member sender = TestFixture.createActiveMember(senderId);

            given(memberRepository.findById(senderId)).willReturn(Optional.of(sender));
            given(memberRepository.findRandomActiveMember(senderId)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> letterService.sendLetter(senderId, "안녕하세요"))
                    .isInstanceOf(NoAvailableReceiverException.class);
        }
    }

    @Nested
    @DisplayName("getLetterDetail 메서드")
    class GetLetterDetailTest {

        @Test
        @DisplayName("편지 상세를 성공적으로 조회한다")
        void getLetterDetail_success() {
            // given
            Long letterId = 1L;
            Long memberId = 2L;
            Member sender = TestFixture.createActiveMember(1L);
            Member receiver = TestFixture.createActiveMember(memberId);
            Letter letter = TestFixture.createLetter(letterId, sender, receiver, "테스트 편지 내용");

            given(letterRepository.findById(letterId)).willReturn(Optional.of(letter));

            // when
            LetterResponse response = letterService.getLetterDetail(letterId, memberId);

            // then
            assertThat(response).isNotNull();
            assertThat(response.getContent()).isEqualTo("테스트 편지 내용");
            assertThat(letter.getStatus()).isEqualTo(LetterStatus.READ);
        }

        @Test
        @DisplayName("편지가 존재하지 않으면 예외가 발생한다")
        void letterNotFound_throwsException() {
            // given
            Long letterId = 999L;
            Long memberId = 1L;
            given(letterRepository.findById(letterId)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> letterService.getLetterDetail(letterId, memberId))
                    .isInstanceOf(LetterNotFoundException.class);
        }

        @Test
        @DisplayName("수신자가 아닌 회원이 조회하면 예외가 발생한다")
        void notReceiver_throwsException() {
            // given
            Long letterId = 1L;
            Long requesterId = 3L; // 다른 사용자
            Member sender = TestFixture.createActiveMember(1L);
            Member receiver = TestFixture.createActiveMember(2L);
            Letter letter = TestFixture.createLetter(letterId, sender, receiver, "테스트 편지");

            given(letterRepository.findById(letterId)).willReturn(Optional.of(letter));

            // when & then
            assertThatThrownBy(() -> letterService.getLetterDetail(letterId, requesterId))
                    .isInstanceOf(LetterAccessDeniedException.class);
        }
    }
}
