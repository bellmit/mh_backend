package org.mh.iot.services;

import org.mh.iot.models.cards.OnOffCard;

import java.util.List;
import java.util.Optional;

/**
 * Created by evolshan on 24.10.2018.
 */
public interface OnOffCardService {
    List<OnOffCard> findAllCardsByRoom(long roomId);
    OnOffCard saveCard(OnOffCard cardModel);
    List<OnOffCard> findAll();
    void deleteCard(long id);
    Optional<OnOffCard> findById(long id);
}
