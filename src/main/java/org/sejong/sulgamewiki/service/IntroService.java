package org.sejong.sulgamewiki.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sejong.sulgamewiki.object.BaseMedia;
import org.sejong.sulgamewiki.object.BasePostCommand;
import org.sejong.sulgamewiki.object.BasePostDto;
import org.sejong.sulgamewiki.object.Intro;
import org.sejong.sulgamewiki.object.IntroDto;
import org.sejong.sulgamewiki.object.Member;
import org.sejong.sulgamewiki.object.constants.SourceType;
import org.sejong.sulgamewiki.object.constants.MediaType;
import org.sejong.sulgamewiki.repository.BaseMediaRepository;
import org.sejong.sulgamewiki.repository.BasePostRepository;
import org.sejong.sulgamewiki.repository.MemberRepository;
import org.sejong.sulgamewiki.util.S3Service;
import org.sejong.sulgamewiki.util.exception.CustomException;
import org.sejong.sulgamewiki.util.exception.ErrorCode;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Slf4j
public class IntroService {

  private final MemberRepository memberRepository;
  private final BasePostRepository basePostRepository;
  private final BaseMediaService baseMediaService;

  public BasePostDto createIntro(BasePostCommand command) {

    Member member = memberRepository.findById(command.getMemberId())
        .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

    Intro savedIntro = basePostRepository.save(
        Intro.builder()
            .title(command.getTitle())
            .introduction(command.getIntroduction())
            .lyrics(command.getLyrics())
            .description(command.getDescription())
            .likes(0)
            .views(0)
            .member(member)
            .build());

    command.setSourceType(SourceType.INTRO);
    command.setBasePost(savedIntro);
    List<BaseMedia> savedMedias = baseMediaService.uploadMedias(command);

    return BasePostDto.builder()
        .basePost(savedIntro)
        .baseMedias(savedMedias)
        .build();
  }
}
