package org.sejong.sulgamewiki.game.popular.dto.request;


import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
public class UpdatePopularGameRequest {
  private String title;
  private String description;
  private String introduction;
  private List<String> imageUrls;
}
