package org.sejong.sulgamewiki.common.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@ToString
@SuperBuilder
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@Getter
@NoArgsConstructor
public abstract class BaseTimeEntity {

  @CreatedDate
  @Column(nullable = false, updatable = false)
  private LocalDateTime createdDate;

  @LastModifiedDate
  @Column(nullable = false)
  private LocalDateTime updatedDate;

  private Boolean isDeleted = false;

  public void markAsDeleted() {
    isDeleted = true;
  }

  public void markAsNotDeleted() {
    isDeleted = false;
  }
}
