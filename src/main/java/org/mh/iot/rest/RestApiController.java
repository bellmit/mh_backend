package org.mh.iot.rest;

import org.mh.iot.bus.BusController;
import org.mh.iot.bus.devices.exception.CommandNotFound;
import org.mh.iot.bus.exception.DeviceNotOnlineException;
import org.mh.iot.models.Device;
import org.mh.iot.models.Parameter;
import org.mh.iot.models.cards.OnOffCard;
import org.mh.iot.models.commands.Command;
import org.mh.iot.models.commands.CommandReply;
import org.mh.iot.services.OnOffCardService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Created by evolshan on 16.12.2020.
 */
@RestController
@RequestMapping("/api")
public class RestApiController {
    private static final Logger logger = LoggerFactory.getLogger(RestApiController.class);

    @Autowired
    private BusController busController; //Service which will do all data retrieval/manipulation work

    @Autowired
    private OnOffCardService onOffCardService;

    // -------------------Retrieve All online devices ---------------------------------------------
    @RequestMapping(value = "/onlineDevices", method = RequestMethod.GET)
    public ResponseEntity<?> getOnlineDevices() {
        List<Device> devices = busController.getOnlineDevices();
        if (devices.isEmpty()) {
            return new ResponseEntity(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(devices, HttpStatus.OK);
    }

    // -------------------Retrieve All online devices ---------------------------------------------
    @RequestMapping(value = "/onOffCard", method = RequestMethod.POST)
    public ResponseEntity<?> saveOnOffCard(@RequestBody OnOffCard card) {
        OnOffCard savedCard = onOffCardService.saveCard(card);
        return new ResponseEntity<>(savedCard, HttpStatus.CREATED);
    }

    // -------------------Retrieve All online devices ---------------------------------------------
    @RequestMapping(value = "/onOffCard/{roomId}", method = RequestMethod.GET)
    public ResponseEntity<?> getOnOffCard(@PathVariable long roomId) {
        List<OnOffCard> allCardsByRoom = onOffCardService.findAllCardsByRoom(roomId);
        if (allCardsByRoom.isEmpty()) {
            return new ResponseEntity(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(allCardsByRoom, HttpStatus.OK);
    }

    // -------------------Current parameter value -------------------------------------------------
    @RequestMapping(value = "/device/{deviceName}/{parameter}", method = RequestMethod.GET)
    public ResponseEntity<?> getDeviceParameter(@PathVariable String deviceName, @PathVariable String parameter) {
        String value;
        try {
            value = busController.getCurrentParameterValue(deviceName, parameter);
        } catch (DeviceNotOnlineException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        if (value == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(value, HttpStatus.OK);
    }


    // -------------------Execute command ----------------------------------------------------------
    @RequestMapping(value = "/device/{deviceName}/command", method = RequestMethod.POST)
    public ResponseEntity<?> executeCommand(@PathVariable String deviceName, @RequestBody Command command) {
        try {
            CommandReply commandReply = busController.executeCommand(deviceName, command);
            if (commandReply.getCode() == CommandReply.Code.OK)
                return new ResponseEntity<>(commandReply, HttpStatus.OK);
            else
                return new ResponseEntity<>(commandReply, HttpStatus.CONFLICT);
        } catch (DeviceNotOnlineException|CommandNotFound e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // -------------------Execute command ----------------------------------------------------------
    @RequestMapping(value = "/device/save", method = RequestMethod.POST)
    public ResponseEntity<?> saveDevice(@RequestBody Device device) {
        //ToDo
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(value = "/usedDevices", method = RequestMethod.GET)
    public ResponseEntity<?> getUsedDevices() {
        //ToDo
        return null;
    }


}
