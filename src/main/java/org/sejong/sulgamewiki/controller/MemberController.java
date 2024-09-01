package org.sejong.sulgamewiki.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sejong.sulgamewiki.object.MemberCommand;
import org.sejong.sulgamewiki.object.MemberDto;
import org.sejong.sulgamewiki.service.CommentService;
import org.sejong.sulgamewiki.service.MemberService;
import org.sejong.sulgamewiki.util.log.LogMonitoringInvocation;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/api/members")
@RequiredArgsConstructor
@Slf4j
@Tag(
    name = "회원 관리 API",
    description = "회원 관리 API 제공"
)
public class MemberController implements MemberControllerDocs {

  private final MemberService memberService;
  private final CommentService commentService;

  @PostMapping(value = "/complete-registration" , consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  @LogMonitoringInvocation
  public ResponseEntity<MemberDto> completeRegistration(
      @ModelAttribute MemberCommand command) {
    return ResponseEntity.ok(memberService.completeRegistration(command));
  }

  @GetMapping("/profile")
  @LogMonitoringInvocation
  public ResponseEntity<MemberDto> getProfile(
      @ModelAttribute MemberCommand command) {
    return ResponseEntity.ok(memberService.getProfile(command));
  }

  @GetMapping("/liked-posts")
  @LogMonitoringInvocation
  public ResponseEntity<MemberDto> getLikedPosts(
      @ModelAttribute MemberCommand command) {
    return ResponseEntity.ok(memberService.getLikedPosts(command));
  }

  @GetMapping("/bookmarked-posts")
  @LogMonitoringInvocation
  public ResponseEntity<MemberDto> getBookmarkedPosts(
      @ModelAttribute MemberCommand command) {
    return ResponseEntity.ok(memberService.getBookmarkedPosts(command));
  }

  @Override
  @PostMapping(value = "/profile-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  @LogMonitoringInvocation
  public ResponseEntity<MemberDto> updateMemberProfileImage(
      @ModelAttribute MemberCommand command) {
    return ResponseEntity.ok(memberService.updateMemberProfileImage(command));
  }

  @Override
  @PostMapping(value = "/nickname", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  @LogMonitoringInvocation
  public ResponseEntity<MemberDto> changeNickname(
      @ModelAttribute MemberCommand command) {
    return ResponseEntity.ok(memberService.changeNickname(command));
  }
}
