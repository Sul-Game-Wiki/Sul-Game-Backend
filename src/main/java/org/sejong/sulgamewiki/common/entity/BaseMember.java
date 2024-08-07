package org.sejong.sulgamewiki.common.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.sejong.sulgamewiki.common.entity.constants.MemberRole;

import java.time.LocalDate;


@ToString
@Setter
@Getter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@MappedSuperclass
public abstract class BaseMember extends BaseTimeEntity {


    @Column(unique = true)
    private String email;

    @Column(unique = true)
    private String nickname;

    private LocalDate birthDate;

    @Column(name = "profile_image_url")
    private String profileImageUrl;

    private MemberRole role;
}
