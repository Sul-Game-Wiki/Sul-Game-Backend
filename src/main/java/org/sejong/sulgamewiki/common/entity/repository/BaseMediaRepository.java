package org.sejong.sulgamewiki.common.entity.repository;

import org.sejong.sulgamewiki.common.entity.BaseMedia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BaseMediaRepository extends JpaRepository<BaseMedia, Long> {

}
