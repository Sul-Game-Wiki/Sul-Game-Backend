package org.sejong.sulgamewiki.util.auth.service;


import lombok.RequiredArgsConstructor;
import org.sejong.sulgamewiki.object.constants.AccountStatus;
import org.sejong.sulgamewiki.object.constants.Role;
import org.sejong.sulgamewiki.util.auth.domain.CustomUserDetails;
import org.sejong.sulgamewiki.object.constants.MemberRole;
import org.sejong.sulgamewiki.object.constants.MemberStatus;
import org.sejong.sulgamewiki.object.Member;
import org.sejong.sulgamewiki.repository.MemberRepository;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
  private final MemberRepository memberRepository;

  @Override
  public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
    OAuth2User oAuth2User = super.loadUser(userRequest);
    String email = oAuth2User.getAttribute("email");
    Member member = memberRepository.findByEmail(email)
        .orElseGet(() -> createPendingMember(oAuth2User));
    return new CustomUserDetails(member, oAuth2User.getAttributes());
  }

  private Member createPendingMember(OAuth2User oAuth2User) {
    String email = oAuth2User.getAttribute("email");
    Member newMember = Member.builder()
        .email(email)
        .role(Role.ROLE_USER)
        .accountStatus(AccountStatus.PENDING)
        .build();
    return memberRepository.save(newMember);
  }
}