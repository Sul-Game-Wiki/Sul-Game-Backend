package org.sejong.sulgamewiki.service;


import static org.sejong.sulgamewiki.object.BasePost.checkCreatorInfoIsPrivate;
import static org.sejong.sulgamewiki.object.constants.ExpRule.POST_CREATION;
import static org.sejong.sulgamewiki.object.constants.ExpRule.POST_DELETION;

import java.util.HashSet;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sejong.sulgamewiki.object.BaseMedia;
import org.sejong.sulgamewiki.object.BasePostCommand;
import org.sejong.sulgamewiki.object.BasePostDto;
import org.sejong.sulgamewiki.object.CreationGame;
import org.sejong.sulgamewiki.object.HomeCommand;
import org.sejong.sulgamewiki.object.HomeDto;
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
@Slf4j
@RequiredArgsConstructor
public class OfficialGameService {

  private final MemberRepository memberRepository;
  private final BaseMediaRepository baseMediaRepository;
  private final BasePostRepository basePostRepository;
  private final BaseMediaService baseMediaService;
  private final ExpManagerService expManagerService;
  private final ReportService reportService;

  /**
   *
   * @param command
   * Long memberId
   * String introduction
   * String title
   * String description
   * List<MulipartFile> mulipartfiles
   * @return
   */

  @Transactional
  public BasePostDto createOfficialGame(BasePostCommand command) {

    Member member = memberRepository.findById(command.getMemberId())
        .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

    if (command.getGameTags().size() > 4) {
      throw new CustomException(ErrorCode.TAG_LIMIT_EXCEEDED);
    }

    OfficialGame savedOfficialGame = basePostRepository.save(
        OfficialGame.builder()
            .isDeleted(false)
            .isUpdated(false)
            .title(command.getTitle())
            .introduction(command.getIntroduction())
            .isIntroExist(command.getIsIntroExist())
            .description(command.getDescription())
            .likes(0)
            .likedMemberIds(new HashSet<>())
            .views(0)
            .reportedCount(0)
            .member(member)
            .dailyScore(0)
            .weeklyScore(0)
            .commentCount(0)
            .sourceType(SourceType.OFFICIAL_GAME)
            .thumbnailIcon(command.getThumbnailIcon())
            .isCreatorInfoPrivate(checkCreatorInfoIsPrivate(command.getIsCreatorInfoPrivate()))
            .gameTags(command.getGameTags())
            .levelTag(command.getLevelTag())
            .headCountTag(command.getHeadCountTag())
            .noiseLevelTag(command.getNoiseLevelTag())
            .build());

    command.setBasePost((savedOfficialGame));
    command.setBasePostId(savedOfficialGame.getBasePostId());

    List<BaseMedia> savedMedias = baseMediaService.uploadMediasFromGame(command);
    BaseMedia savedIntroMedia = baseMediaService.uploadIntroMediaFromGame(command);

    // 창작자 EXP
    expManagerService.updateExp(member, POST_CREATION);

    return BasePostDto.builder()
        .officialGame(savedOfficialGame)
        .baseMedias(savedMedias)
        .introMediaInGame(savedIntroMedia)
        .build();
  }

  @Transactional(readOnly = true)
  public BasePostDto getOfficialGame(BasePostCommand command) {

    OfficialGame officialGame = basePostRepository.findOfficialGameByBasePostId(
        command.getBasePostId())
        .orElseThrow(() -> new CustomException(ErrorCode.GAME_NOT_FOUND));

    List<BaseMedia> medias = basePostRepository.findMediasByBasePostId(
        command.getBasePostId());

    BaseMedia introMediaFileInGamePost = baseMediaRepository.findByMediaUrl(command.getIntroMediaUrlFromGame());

    List<CreationGame> relatedCreationGames = basePostRepository.findCreationGamesByRelatedOfficialGame(officialGame);

    // 조회수 증가로직
    officialGame.increaseViews();

    // 게시글 Score
    officialGame.updateScore(ScoreRule.VIEW);

    return BasePostDto.builder()
        .officialGame(officialGame)
        .baseMedias(medias)
        .introMediaInGame(introMediaFileInGamePost)
        .relatedCreationGames(relatedCreationGames)
        .build();
  }

  public HomeDto getSortedOfficialGames(HomeCommand command) {
    Pageable pageable = createPageable(command);  // 기본값으로 설정

    Slice<OfficialGame> officialGamesSlice;

    officialGamesSlice = basePostRepository.getSliceOfficialGames(pageable);

    return HomeDto.builder()
        .officialGameSlice(officialGamesSlice)
        .hasNext(officialGamesSlice.hasNext())
        .build();
  }

  @Transactional(readOnly = true)
  public BasePostDto getOfficialGames() {

    List<OfficialGame> officialGames = basePostRepository.findAllOfficialGame();

    return BasePostDto.builder()
        .officialGames(officialGames)
        .build();
  }

  @Transactional
  public BasePostDto updateOfficialGame(BasePostCommand command) {

    OfficialGame existingOfficialGame = basePostRepository.findOfficialGameByBasePostId(
            command.getBasePostId())
        .orElseThrow(() -> new CustomException(ErrorCode.GAME_NOT_FOUND));

    Member requestingMember = memberRepository.findById(command.getMemberId())
        .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
    if (!existingOfficialGame.getMember().equals(requestingMember)) {
      // 작성자가 아니거나 권한이 없는 경우 예외 발생
      throw new CustomException(ErrorCode.UNAUTHORIZED_ACTION);
    }

    if (command.getGameTags().size() > 4) {
      throw new CustomException(ErrorCode.TAG_LIMIT_EXCEEDED);
    }


    // 기존 게임 정보 업데이트
    existingOfficialGame.setTitle(command.getTitle());
    existingOfficialGame.setIntroduction(command.getIntroduction());
    existingOfficialGame.setDescription(command.getDescription());
    existingOfficialGame.setIntroLyricsInGamePost(command.getIntroLyricsInGame());
    // 인트로 미디어
    existingOfficialGame.setThumbnailIcon(command.getThumbnailIcon());
    existingOfficialGame.setGameTags(command.getGameTags());
    existingOfficialGame.setCreatorInfoPrivate(checkCreatorInfoIsPrivate(command.getIsCreatorInfoPrivate()));
    // 업데이트시 널값 들어오면 자동으로 공개로 설정되도록 수정


    existingOfficialGame.markAsUpdated();
    command.setBasePost(existingOfficialGame);

    List<BaseMedia> updatedMedias = baseMediaService.updateMedias(command);
    // 게시글과 미디어 파일 저장

    basePostRepository.save(existingOfficialGame);

    BaseMedia introMediaFileInGamePost = baseMediaRepository.findByMediaUrl(
        existingOfficialGame.getIntroMediaFileInGamePostUrl());


    return BasePostDto.builder()
        .officialGame(existingOfficialGame)
        .baseMedias(updatedMedias)
        .introMediaFileInGame(introMediaFileInGamePost)
        .build();
  }

  @Transactional
  public void deleteOfficialGame(BasePostCommand command) {
    OfficialGame officialGame = basePostRepository.findOfficialGameByBasePostId(
            command.getBasePostId())
        .orElseThrow(() -> new CustomException(ErrorCode.GAME_NOT_FOUND));

    Member requestingMember = memberRepository.findById(command.getMemberId())
        .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
    if (!officialGame.getMember().equals(requestingMember)) {
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
    officialGame.markAsDeleted();
    officialGame.setIntroMediaFileInGamePostUrl(null);

    // 창작자 경험치 회수
    expManagerService.updateExp(requestingMember, POST_DELETION);

    // 변경사항을 저장
    basePostRepository.save(officialGame);
  }

  private Pageable createPageable(HomeCommand command) {
    return PageRequest.of(
        command.getPageNumber(),
        command.getPageSize(),
        Sort.by(command.getDirection(),command.getSortBy().getValue())
    );
  }

}