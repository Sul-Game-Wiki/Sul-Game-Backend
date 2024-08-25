package org.sejong.sulgamewiki.repository;

import java.util.List;
import org.sejong.sulgamewiki.object.BasePost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BasePostRepository extends JpaRepository<BasePost, Long> {

  List<BasePost> findByBasePostIdIn(List<Long> ids);
}
