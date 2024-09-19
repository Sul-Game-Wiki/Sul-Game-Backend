package org.sejong.sulgamewiki.service;

import static org.sejong.sulgamewiki.object.BasePost.checkCreatorInfoIsPrivate;
import static org.sejong.sulgamewiki.object.constants.ExpRule.POST_CREATION;
import static org.sejong.sulgamewiki.object.constants.ExpRule.POST_DELETION;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sejong.sulgamewiki.object.BaseMedia;
import org.sejong.sulgamewiki.object.BasePostCommand;
import org.sejong.sulgamewiki.object.BasePostDto;
import org.sejong.sulgamewiki.object.HomeCommand;
import org.sejong.sulgamewiki.object.HomeDto;
import org.sejong.sulgamewiki.object.Intro;
import org.sejong.sulgamewiki.object.Member;
import org.sejong.sulgamewiki.object.OfficialGame;
import org.sejong.sulgamewiki.object.constants.ScoreRule;
import org.sejong.sulgamewiki.object.constants.SourceType;
import org.sejong.sulgamewiki.repository.BaseMediaRepository;
import org.sejong.sulgamewiki.repository.BasePostRepository;
import org.sejong.sulgamewiki.repository.MemberRepository;
import org.sejong.sulgamewiki.util.exception.CustomException;
import org.sejong.sulgamewiki.util.exception.ErrorCode;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class IntroService {

  private final MemberRepository memberRepository;
  private final BasePostRepository basePostRepository;
  private final BaseMediaRepository baseMediaRepository;
  private final BaseMediaService baseMediaService;
  private final ExpManagerService expManagerService;

  public BasePostDto createIntro(BasePostCommand command) {

    // 멤버 조회
    Member member = memberRepository.findById(command.getMemberId())
        .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

    // 태그 최대 개수 검사
    if (command.getGameTags().size() > 4) {
      throw new CustomException(ErrorCode.TAG_LIMIT_EXCEEDED);
    }

    // 공식 게임 조회 및 예외 처리
    Optional<OfficialGame> officialGame = Optional.empty();
    if (command.getRelatedOfficialGameId() != null) {
      officialGame = basePostRepository.findOfficialGameByBasePostId(command.getRelatedOfficialGameId());

      // 존재하지 않는 공식 게임 ID일 경우 예외 발생
      officialGame.orElseThrow(() -> new CustomException(ErrorCode.GAME_NOT_FOUND));
    }

    // Intro 빌더 패턴 시작
    Intro.IntroBuilder introBuilder = Intro.builder()
        .introType(command.getIntroType())
        .title(command.getTitle())
        .lyrics(command.getLyrics())
        .description(command.getDescription())
        .thumbnailIcon(command.getThumbnailIcon())
        .introTags(command.getIntroTags())
        .isCreatorInfoPrivate(checkCreatorInfoIsPrivate(command.getIsCreatorInfoPrivate()))
        .likes(0)
        .views(0)
        .likedMemberIds(new HashSet<>())
        .member(member)
        .dailyScore(0)
        .weeklyScore(0)
        .sourceType(SourceType.INTRO);

    // 연관 공식 게임이 있으면 설정
    officialGame.ifPresent(introBuilder::officialGame);

    // Intro 객체 생성 및 저장
    Intro savedIntro = basePostRepository.save(introBuilder.build());

    command.setBasePost(savedIntro);
    List<BaseMedia> savedMedias = baseMediaService.uploadMediasFromIntro(command);

    // 창작자 EXP
    expManagerService.updateExp(member, POST_CREATION);

    return BasePostDto.builder()
        .basePost(savedIntro)
        .baseMedias(savedMedias)
        .build();
  }


  // Read: 특정 Intro 조회
  @Transactional(readOnly = true)
  public BasePostDto getIntro(BasePostCommand command) {

    Intro intro = basePostRepository.findIntroByBasePostId(
            command.getBasePostId())
        .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));

    List<BaseMedia> medias = basePostRepository.findMediasByBasePostId(
        command.getBasePostId());

    // 조회수 증가로직
    intro.increaseViews();

    // 게시글 Score
    intro.updateScore(ScoreRule.VIEW);

    return BasePostDto.builder()
        .basePost(intro)
        .baseMedias(medias)
        .build();
  }

  public HomeDto getSortedIntroGames(HomeCommand command) {
    Pageable pageable = createPageable(command);  // 기본값으로 설정

    Slice<Intro> introSlices;

    introSlices = basePostRepository.getSliceIntros(pageable);

    return HomeDto.builder()
        .introSlice(introSlices)
        .hasNext(introSlices.hasNext())
        .build();
  }

  @Transactional
  public BasePostDto updateIntro(BasePostCommand command) {
    // 기존 Intro 게시글 조회
    Intro existingIntro = basePostRepository.findIntroByBasePostId(
            command.getBasePostId())
        .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));

    // 요청한 멤버가 게시글의 작성자인지 확인
    Member requestingMember = memberRepository.findById(command.getMemberId())
        .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

    if (!existingIntro.getMember().equals(requestingMember)) {
      // 작성자가 아니거나 권한이 없는 경우 예외 발생
      throw new CustomException(ErrorCode.UNAUTHORIZED_ACTION);
    }

    // 공식 게임 조회 및 예외 처리
    Optional<OfficialGame> officialGame = Optional.empty();
    if (command.getRelatedOfficialGameId() != null) {
      // 공식 게임이 전달된 경우 해당 게임을 찾아 설정
      officialGame = basePostRepository.findOfficialGameByBasePostId(command.getRelatedOfficialGameId());

      // 존재하지 않는 공식 게임 ID일 경우 예외 발생
      officialGame.orElseThrow(() -> new CustomException(ErrorCode.GAME_NOT_FOUND));
    }

    // 게시글의 제목, 가사, 설명 등을 업데이트
    existingIntro.setTitle(command.getTitle());
    existingIntro.setLyrics(command.getLyrics());
    existingIntro.setDescription(command.getDescription());
    existingIntro.setIntroType(command.getIntroType());
    existingIntro.setThumbnailIcon(command.getThumbnailIcon());
    existingIntro.setIntroTags(command.getIntroTags());
    existingIntro.setCreatorInfoPrivate(command.getIsCreatorInfoPrivate());
    existingIntro.setOfficialGame(officialGame.orElse(null));

    // 업데이트 표시
    existingIntro.markAsUpdated();
    command.setBasePost(existingIntro);

    // 연관된 미디어 파일 업데이트
    List<BaseMedia> updatedMedias = baseMediaService.updateMedias(command);

    // 게시글과 미디어 파일 저장
    basePostRepository.save(existingIntro);

    // BasePostDto를 반환하여 업데이트된 정보를 반환
    return BasePostDto.builder()
        .basePost(existingIntro)
        .baseMedias(updatedMedias)
        .build();
  }


  // Delete: 특정 Intro 삭제 (소프트 삭제, 미디어 파일은 진짜 삭제)
  @Transactional
  public void deleteIntro(BasePostCommand command) {
    Intro intro = basePostRepository.findIntroByBasePostId(
            command.getBasePostId())
        .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));

    // 요청한 멤버가 게시글의 작성자인지 확인
    Member requestingMember = memberRepository.findById(command.getMemberId())
        .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
    if (!intro.getMember().equals(requestingMember)) {
      // 작성자가 아니거나 권한이 없는 경우 예외 발생
      throw new CustomException(ErrorCode.UNAUTHORIZED_ACTION);
    }

    // 연관된 미디어 파일을 실제로 삭제
    List<BaseMedia> medias = baseMediaRepository.findAllByBasePost_BasePostId(
        command.getBasePostId());
    if (medias != null && !medias.isEmpty()) {
      baseMediaService.deleteMedias(medias);
    }
    // 게시글을 삭제된 것으로 표시
    intro.markAsDeleted();

    // 창작자 EXP
    expManagerService.updateExp(requestingMember, POST_DELETION);

    // 변경사항을 저장합니다.
    basePostRepository.save(intro);
  }

  private Pageable createPageable(HomeCommand command) {
    return PageRequest.of(
        command.getPageNumber(),
        command.getPageSize(),
        Sort.by(command.getDirection(),command.getSortBy().getValue())
    );
  }
}
