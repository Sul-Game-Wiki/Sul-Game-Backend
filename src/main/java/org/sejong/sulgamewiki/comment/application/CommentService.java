package org.sejong.sulgamewiki.comment.application;


import lombok.RequiredArgsConstructor;
import org.sejong.sulgamewiki.comment.CommentException.CommentErrorCode;
import org.sejong.sulgamewiki.comment.CommentException.CommentException;
import org.sejong.sulgamewiki.comment.domain.entity.Comment;
import org.sejong.sulgamewiki.comment.domain.entity.CommentType;
import org.sejong.sulgamewiki.comment.domain.repository.CommentRepository;
import org.sejong.sulgamewiki.comment.dto.request.CommentRequest;
import org.sejong.sulgamewiki.comment.dto.response.CommentResponse;
import org.sejong.sulgamewiki.game.common.exception.GameErrorCode;
import org.sejong.sulgamewiki.game.common.exception.GameException;
import org.sejong.sulgamewiki.game.popular.domain.entity.PopularGame;
import org.sejong.sulgamewiki.game.popular.domain.repository.PopularGameRepository;
import org.sejong.sulgamewiki.member.domain.entity.Member;
import org.sejong.sulgamewiki.member.domain.repository.MemberRepository;
import org.sejong.sulgamewiki.member.exception.MemberErrorCode;
import org.sejong.sulgamewiki.member.exception.MemberException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommentService {

  private final CommentRepository commentRepository;
  private final MemberRepository memberRepository;
  private final PopularGameRepository popularGameRepository;

  public CommentResponse createComment(CommentRequest request) {
    Member member = memberRepository.findById(request.getMemberId())
        .orElseThrow(
            () -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));


    // 게임 유형에 따라 다른 엔티티를 조회하는 로직을 추가해야 합니다.
    if (request.getCommentType() == CommentType.POPULAR_GAME) {
      PopularGame popularGame = popularGameRepository.findById(request.getTypeId())
          .orElseThrow(() -> new GameException(GameErrorCode.GAME_NOT_FOUND));
      // 만약 다른 게임 유형이 추가될 경우, 여기에 추가적인 조건문을 작성합니다.
    }

    Comment comment = Comment.builder()
        .content(request.getContent())
        .typeId(request.getTypeId())
        .commentType(request.getCommentType())
        .member(member)
        .build();

    Comment savedComment = commentRepository.save(comment);

    return CommentResponse.from(savedComment);
  }

  public void deleteComment(Long commentId, Long memberId) {

    // 댓글이 존재하는지 확인
    Comment comment = commentRepository.findById(commentId)
        .orElseThrow(
            () -> new CommentException(CommentErrorCode.COMMENT_NOT_FOUND,
                "CommentService.deleteComment"));

    // 댓글 작성자가 요청한 사용자인지 확인
    if (!comment.getMember().getId().equals(memberId)) {
      throw new CommentException(CommentErrorCode.ACCESS_DENIED,
          "CommentService.deleteComment");
    }
    commentRepository.deleteById(commentId);
  }
}
