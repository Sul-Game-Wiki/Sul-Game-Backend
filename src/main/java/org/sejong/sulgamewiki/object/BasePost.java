package org.sejong.sulgamewiki.object;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.ManyToOne;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.sejong.sulgamewiki.util.exception.CustomException;
import org.sejong.sulgamewiki.util.exception.ErrorCode;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Getter
@Setter
@ToString(callSuper = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@SuperBuilder
@DiscriminatorColumn(name = "dtype")
public abstract class BasePost extends BaseTimeEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long basePostId;

  @Column(length = 100)
  private String title;

  @Column(length = 500)
  private String description;

  @Builder.Default
  private int likes = 0;

  @Builder.Default
  @ElementCollection
  private Set<Long> likedMemberIds = new HashSet<>();

  @Builder.Default
  private int views = 0;

  @Builder.Default
  private int reportedCount = 0;

  @ManyToOne(fetch = FetchType.LAZY)
  private Member member;

  public void cancelLike(Long memberId) {
    if(this.likedMemberIds.contains(memberId)) {
      throw new CustomException(ErrorCode.NO_LIKE_TO_CANCEL);
    }
    if (likes > 0) {
      this.likes--;
      this.likedMemberIds.remove(memberId);
    } else if(likes <= 0) {
      throw new CustomException(ErrorCode.LIKE_CANNOT_BE_UNDER_ZERO);
    }
  }

  public void upLike(Long memberId) {
    likes++;
    this.likedMemberIds.add(memberId);
  }


}