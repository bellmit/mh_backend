package org.mh.iot.services;

import org.mh.iot.models.cards.OnOffCard;
import org.mh.iot.repositories.OnOffCardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Created by evolshan on 24.10.2018.
 */
@Service("onOffCardService")
@Transactional
public class OnOffCardServiceImpl implements OnOffCardService {

    @Autowired
    private OnOffCardRepository onOffCardRepository;


    @Override
    public List<OnOffCard> findAllCardsByRoom(long roomId) {
        return onOffCardRepository.findByRoomId(roomId);
    }

    @Override
    public OnOffCard saveCard(OnOffCard cardModel) {
        return onOffCardRepository.save(cardModel);
    }

    @Override
    public List<OnOffCard> findAll() {
        return onOffCardRepository.findAll();
    }

    @Override
    public void deleteCard(long id) {
        onOffCardRepository.deleteById(id);
    }

    @Override
    public Optional<OnOffCard> findById(long id) {
        return onOffCardRepository.findById(id);
    }
}
