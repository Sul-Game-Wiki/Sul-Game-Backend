package org.sejong.sulgamewiki.object;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TestCommand {
  private Integer eachPostCreateCount;
  private String email;
}
