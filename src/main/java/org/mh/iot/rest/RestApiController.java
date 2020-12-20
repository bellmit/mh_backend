package org.mh.iot.rest;

import org.mh.iot.bus.BusController;
import org.mh.iot.bus.devices.exception.CommandNotFound;
import org.mh.iot.bus.exception.DeviceNotOnlineException;
import org.mh.iot.models.Device;
import org.mh.iot.models.cards.OnOffCard;
import org.mh.iot.models.commands.Command;
import org.mh.iot.models.commands.CommandReply;
import org.mh.iot.services.OnOffCardService;
import org.mh.iot.services.UsedDevicesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Created by evolshan on 16.12.2020.
 */
@RestController
@RequestMapping("/api")
public class RestApiController {

    @Autowired
    private BusController busController;

    @Autowired
    private OnOffCardService onOffCardService;

    @Autowired
    private UsedDevicesService usedDevicesService;

    @RequestMapping(value = "/onlineDevices", method = RequestMethod.GET)
    public ResponseEntity<?> getOnlineDevices() {
        List<Device> devices = busController.getOnlineDevices();
        return new ResponseEntity<>(devices, HttpStatus.OK);
    }

    @RequestMapping(value = "/onOffCard", method = RequestMethod.POST)
    public ResponseEntity<?> saveOnOffCard(@RequestBody OnOffCard card) {
        return new ResponseEntity<>(onOffCardService.saveCard(card), HttpStatus.CREATED);
    }

    @RequestMapping(value = "/onOffCards/{roomId}", method = RequestMethod.GET)
    public ResponseEntity<?> getOnOffCard(@PathVariable long roomId) {
        return new ResponseEntity<>(onOffCardService.findAllCardsByRoom(roomId), HttpStatus.OK);
    }

    @RequestMapping(value = "/onOffCards", method = RequestMethod.GET)
    public ResponseEntity<?> getAllOnOffCard() {
        return new ResponseEntity<>(onOffCardService.findAll(), HttpStatus.OK);
    }

    @RequestMapping(value = "/device/{deviceName}/{parameter}", method = RequestMethod.GET)
    public ResponseEntity<?> getDeviceParameter(@PathVariable String deviceName, @PathVariable String parameter) {
        String value;
        try {
            value = busController.getCurrentParameterValue(deviceName, parameter);
        } catch (DeviceNotOnlineException e) {
            return new ResponseEntity<>("Device " + deviceName + " not online",HttpStatus.NOT_FOUND);
        }
        if (value == null) {
            return new ResponseEntity<>("Parameter " + parameter + " not found", HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(value, HttpStatus.OK);
    }

    @RequestMapping(value = "/device/{deviceName}/command", method = RequestMethod.POST)
    public ResponseEntity<?> executeCommand(@PathVariable String deviceName, @RequestBody Command command) {
        try {
            CommandReply commandReply = busController.executeCommand(deviceName, command);
            if (commandReply.getCode() == CommandReply.Code.OK)
                return new ResponseEntity<>(commandReply, HttpStatus.OK);
            else
                return new ResponseEntity<>(commandReply.getMessage(), HttpStatus.CONFLICT);
        } catch (DeviceNotOnlineException|CommandNotFound e) {
            return new ResponseEntity<>("Device " + deviceName + " not online", HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping(value = "/device/save", method = RequestMethod.POST)
    public ResponseEntity<?> saveDevice(@RequestBody Device device) {
        if (usedDevicesService.findByName(device.getName()).isPresent()){
            return new ResponseEntity<>("Already used", HttpStatus.CONFLICT);
        }
        return new ResponseEntity<>(usedDevicesService.saveDevice(device), HttpStatus.OK);
    }

    @RequestMapping(value = "/usedDevices", method = RequestMethod.GET)
    public ResponseEntity<?> getUsedDevices() {
        return new ResponseEntity<>(usedDevicesService.getUsedDevices(), HttpStatus.OK);
    }

    @RequestMapping(value = "/usedDevices/{deviceName}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteFromUsedDevices(@PathVariable String deviceName) {
        if (!usedDevicesService.findByName(deviceName).isPresent()){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        usedDevicesService.deleteDevice(deviceName);
        return new ResponseEntity<>(HttpStatus.OK);
    }


    @RequestMapping(value = "/onOffCards/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<?> removeOnOffCard(@PathVariable long id) {
        if (!onOffCardService.findById(id).isPresent()){
            return new ResponseEntity<>("Card not found", HttpStatus.NOT_FOUND);
        }
        onOffCardService.deleteCard(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }


}
