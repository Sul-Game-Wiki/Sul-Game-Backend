package org.sejong.sulgamewiki.common.like.presentation;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.sejong.sulgamewiki.common.auth.domain.CustomUserDetails;
import org.sejong.sulgamewiki.common.like.application.LikeService;
import org.sejong.sulgamewiki.common.like.dto.UpdateLikeResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/like")
@RequiredArgsConstructor
public class LikeController {

  private final LikeService likeService;

  @PostMapping("/up/{basePostId}")
  @Operation(description = "좋아요 눌림")
  public ResponseEntity<UpdateLikeResponse> upLike(
      @PathVariable("basePostId") Long basePostId,
      Long memberId
  ) {
    UpdateLikeResponse response = likeService.upLike(basePostId, memberId);
    return ResponseEntity.ok(response);
  }

  @PostMapping("/cancel/{basePostId}")
  @Operation(description = "좋아요 취소")
  public ResponseEntity<UpdateLikeResponse> cancelLike(
      @PathVariable Long basePostId,
      Long memberId
      //@AuthenticationPrincipal CustomUserDetails customUserDetails
  ) {
    UpdateLikeResponse response = likeService.cancelLike(basePostId, memberId);
    return ResponseEntity.ok(response);
  }
}
