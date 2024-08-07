package org.sejong.sulgamewiki.common.entity;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.sejong.sulgamewiki.common.like.exception.LikeErrorCode;
import org.sejong.sulgamewiki.common.like.exception.LikeException;
import org.sejong.sulgamewiki.member.domain.entity.Member;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Getter
@Setter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@SuperBuilder
@DiscriminatorColumn(name = "dtype")
public abstract class BasePost extends BaseTimeEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(length = 100)
  private String title;

  @Column(length = 500)
  private String description;

  @Builder.Default
  private int likes = 0;

  @Builder.Default
  private int views = 0;


  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "member_id", nullable = false)
  private Member member;

  public void cancelLike() {
    if( likes == 0) {
      throw new LikeException(LikeErrorCode.LIKE_CANNOT_BE_UNDER_ZERO);
    }
    likes--;
  }

  public void upLike() {
    likes++;
  }
}