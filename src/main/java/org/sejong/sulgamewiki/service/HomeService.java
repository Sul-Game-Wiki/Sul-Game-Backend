package org.sejong.sulgamewiki.service;

import lombok.RequiredArgsConstructor;
import org.sejong.sulgamewiki.object.BasePost;
import org.sejong.sulgamewiki.object.CreationGame;
import org.sejong.sulgamewiki.object.HomeCommand;
import org.sejong.sulgamewiki.object.HomeDto;
import org.sejong.sulgamewiki.object.Intro;
import org.sejong.sulgamewiki.object.OfficialGame;
import org.sejong.sulgamewiki.object.constants.SortBy;
import org.sejong.sulgamewiki.object.constants.SourceType;
import org.sejong.sulgamewiki.repository.BasePostRepository;
import org.sejong.sulgamewiki.util.exception.CustomException;
import org.sejong.sulgamewiki.util.exception.ErrorCode;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HomeService {

  private final BasePostRepository basePostRepository;

  public HomeDto getHomepage() {

    /*
    최신게시물
   */
    // 최신 창작게시물 가져오기 (Slice 방식)
    List<CreationGame> latestCreationGames = basePostRepository.getSliceCreationGames(
        createPageable(7, SortBy.CREATION_DATE)
    ).getContent();

    // 최신 인트로 게시물 가져오기 (Slice 방식)
    List<Intro> latestIntros = basePostRepository.getSliceIntros(
        createPageable(7, SortBy.CREATION_DATE)
    ).getContent();

    /*
    국룰술게임
    */
    // 좋아요 순으로 오피셜 게임 가져오기
    List<OfficialGame> officialGamesByLikes = basePostRepository.getSliceOfficialGames(
        createPageable(9, SortBy.LIKES)
    ).getContent();

    /*
    실시간 ㅅㄱㅇㅋ차트
     */
    // 실시간 창작술게임 차트 가져오기
    List<CreationGame> creationGamesByRealTimeScore = basePostRepository.getSliceCreationGames(
        createPageable(9, SortBy.DAILY_SCORE)
    ).getContent();

    // 실시간 인트로 차트 가져오기
    List<Intro> introsByRealTimeScore = basePostRepository.getSliceIntros(
        createPageable(9, SortBy.DAILY_SCORE)
    ).getContent();

    // 실시간 공식술게임 차트 가져오기
    List<OfficialGame> officialGamesByRealTimeScore = basePostRepository.getSliceOfficialGames(
        createPageable(9, SortBy.DAILY_SCORE)
    ).getContent();

    /*
    인트로 자랑하기
     */
    // 좋아요 순으로 인트로 자랑하기 데이터 가져오기
    List<Intro> introsByLikes = basePostRepository.getSliceIntros(
        createPageable(3, SortBy.LIKES)
    ).getContent();

    // 조회수 순으로 인트로 자랑하기 데이터 가져오기
    List<Intro> introsByViews = basePostRepository.getSliceIntros(
        createPageable(3, SortBy.VIEWS)
    ).getContent();

    /*
    오늘 가장 핫했던 술게임
     */
    // 오늘 가장 핫했던 게임 가져오기
    List<BasePost> gamesByDailyScore = basePostRepository.getSliceGames(
        createPageable(9, SortBy.WEEKLY_SCORE)
    ).getContent();

    // 빌더 패턴을 사용하여 HomeDto 생성
    return HomeDto.builder()
        // 최신 게시물 설정
        .latestCreationGames(latestCreationGames)
        .latestIntros(latestIntros)

        // 국룰술게임 설정
        .officialGamesByLikes(officialGamesByLikes)

        // 실시간 ㅅㄱㅇㅋ차트 설정
        .creationGamesByDailyScore(creationGamesByRealTimeScore)
        .introsByDailyScore(introsByRealTimeScore)
        .officialGamesByDailyScore(officialGamesByRealTimeScore)

        // 인트로 자랑하기 설정
        .introsByLikes(introsByLikes)
        .introsByViews(introsByViews)

        // 오늘 가장 핫했던 술게임 설정
        .gamesByWeeklyScore(gamesByDailyScore)

        // HomeDto 빌드
        .build();
  }


  // 정렬 기준에 따른 Pageable 객체 생성
  private Pageable createPageable(int pageSize, SortBy sortBy) {
    return PageRequest.of(
        0,
        pageSize,
        Sort.by(Direction.DESC, sortBy.getValue())
    );
  }
}
