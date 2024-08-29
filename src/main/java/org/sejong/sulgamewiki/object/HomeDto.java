package org.sejong.sulgamewiki.object;

import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Builder
@ToString
public class HomeDto {
  /*
  최신술게임
   */
  private List<CreationGame> latestCreationGames;
  private List<Intro> latestIntros;

  /*
  국룰술게임
   */
  private List<OfficialGame> officialGamesByLikes;

  /*
  실시간 ㅅㄱㅇㅋ차트
   */
  private List<CreationGame> creationGamesByDailyScore;
  private List<Intro> introsByDailyScore;
  private List<OfficialGame> officialGamesByDailyScore;

  /*
  인트로 자랑하기
   */
  private List<Intro> introsByLikes;
  private List<Intro> introsByViews;

  /*
  오늘 가장 핫했던 술게임
   */
  private List<BasePost> gamesByWeeklyScore;
}