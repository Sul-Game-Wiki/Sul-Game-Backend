package org.sejong.sulgamewiki.common.entity.constants;

import static org.sejong.sulgamewiki.common.exception.GlobalErrorCode.MIME_TYPE_NOT_FOUNT;

import org.sejong.sulgamewiki.common.exception.GlobalException;
import org.springframework.web.multipart.MultipartFile;

public enum MediaType {
  IMAGE_PNG("image/png"),
  IMAGE_JPEG("image/jpeg"),
  IMAGE_JPG("image/jpg"),
  VIDEO_MP4("video/mp4"),
  VIDEO_AVI("video/x-msvideo"),
  VIDEO_MOV("video/quicktime"),
  AUDIO_MP3("audio/mpeg"),
  AUDIO_WAV("audio/wav"),
  AUDIO_AAC("audio/aac");

  private final String mimeType;

  MediaType(String mimeType) {
    this.mimeType = mimeType;
  }

  public String getMimeType() {
    return mimeType;
  }

  public static MediaType getMimeType(String mimeType) {
    for (MediaType type : values()) {
      if (type.getMimeType().equals(mimeType)) {
        return type;
      }
    }
    throw new GlobalException (
        MIME_TYPE_NOT_FOUNT);
  }

  public static MediaType getMediaType(MultipartFile file) {
    return MediaType.getMimeType(file.getContentType());
  }

}



