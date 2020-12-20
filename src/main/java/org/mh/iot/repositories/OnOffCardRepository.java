package org.mh.iot.repositories;

import org.mh.iot.models.cards.OnOffCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by evolshan on 24.10.2018.
 */
@Repository
public interface OnOffCardRepository extends JpaRepository<OnOffCard, Long> {
    List<OnOffCard> findByRoomId(long roomId);
}
