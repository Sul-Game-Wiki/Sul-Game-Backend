package org.sejong.sulgamewiki.service;


import lombok.RequiredArgsConstructor;
import org.sejong.sulgamewiki.object.BasePost;
import org.sejong.sulgamewiki.object.BasePostCommand;
import org.sejong.sulgamewiki.object.BasePostDto;
import org.sejong.sulgamewiki.object.Comment;
import org.sejong.sulgamewiki.object.CommentCommand;
import org.sejong.sulgamewiki.object.CommentDto;
import org.sejong.sulgamewiki.repository.BasePostRepository;
import org.sejong.sulgamewiki.repository.CommentRepository;
import org.sejong.sulgamewiki.repository.MemberRepository;
import org.sejong.sulgamewiki.util.exception.CustomException;
import org.sejong.sulgamewiki.util.exception.ErrorCode;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LikeService {

  private final MemberRepository memberRepository;
  private final BasePostRepository basePostRepository;
  private final CommentRepository commentRepository;

  //TODO comment Like 추가해야함
  //FIXME: 지금 basePost에 LikedMemberIds 추가함 : 이전 코드 수정 필요

  public BasePostDto upPostLike(BasePostCommand command) {

    BasePost basePost = basePostRepository.findById(command.getBasePostId())
        .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));

    basePost.upLike(command.getMemberId());

    BasePost savedBasePost = basePostRepository.save(basePost);

    return BasePostDto.builder()
        .basePost(savedBasePost)
        .isLiked(true)
        .build();
  }

  public BasePostDto cancelPostLike(BasePostCommand command) {

    BasePost basePost = basePostRepository.findById(command.getBasePostId())
        .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));

    basePost.cancelLike(command.getMemberId());

    BasePost savedBasePost = basePostRepository.save(basePost);

    return BasePostDto.builder()
        .basePost(savedBasePost)
        .isLiked(false)
        .build();
  }



  public CommentDto upCommentLike(CommentCommand command) {

    BasePost basePost = basePostRepository.findById(command.getBasePostId())
        .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));

    Comment comment = commentRepository.findById(command.getCommentId())
        .orElseThrow(() -> new CustomException(ErrorCode.COMMENT_NOT_FOUND));

    comment.upLike(command.getMemberId());

    basePostRepository.save(basePost);

    return CommentDto.builder()
        .comment(comment)
        .isLiked(true)
        .build();
  }

  public CommentDto cancelCommentLike(CommentCommand command) {

    BasePost basePost = basePostRepository.findById(command.getBasePostId())
        .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));

    Comment comment = commentRepository.findById(command.getCommentId())
        .orElseThrow(() -> new CustomException(ErrorCode.COMMENT_NOT_FOUND));

    comment.cancelLike(command.getMemberId());

    basePostRepository.save(basePost);

    return CommentDto.builder()
        .comment(comment)
        .isLiked(false)
        .build();
  }
}
