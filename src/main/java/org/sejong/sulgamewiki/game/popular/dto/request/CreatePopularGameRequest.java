package org.sejong.sulgamewiki.game.popular.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
public class CreatePopularGameRequest {
  @Schema(description = "게임의 제목", example = "딸기당근수박참외메론 게임")
  private String title;

  @Schema(description = "게임에 대한 설명", example = "딸기당근수박참외 메론~ 게임! 딸기당근수박참외 메론~ 게임!")
  private String description;

  @Schema(description = "짧은 설명", example = "간단한 설명을 적어주세요")
  private String introduction;

  public static CreatePopularGameRequest of(String title, String description, String introduction) {
    return CreatePopularGameRequest.builder()
        .title(title)
        .description(description)
        .introduction(introduction)
        .build();
  }
}
