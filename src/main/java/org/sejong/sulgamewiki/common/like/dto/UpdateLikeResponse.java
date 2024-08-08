package org.sejong.sulgamewiki.common.like.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Builder
@Getter
@ToString
public class UpdateLikeResponse {
  private Long postId;
  private int likeCount;
}
