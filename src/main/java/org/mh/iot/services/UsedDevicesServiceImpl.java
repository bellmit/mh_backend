package org.mh.iot.services;

import org.mh.iot.models.Device;
import org.mh.iot.repositories.UsedDevicesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Created by evolshan on 24.10.2018.
 */
@Service("usedDevicesService")
@Transactional
public class UsedDevicesServiceImpl implements UsedDevicesService {

    @Autowired
    private UsedDevicesRepository usedDevicesRepository;

    @Override
    public Device saveDevice(Device deviceModel) {
        return usedDevicesRepository.save(deviceModel);
    }

    @Override
    public List<Device> getUsedDevices() {
        return usedDevicesRepository.findAll();
    }

    @Override
    public Optional<Device> findByName(String name){
        Optional<Device> byId = usedDevicesRepository.findById(name);
        if (byId.isPresent()){
            byId.get().getCommands().iterator(); //to initialize lazy elements
            byId.get().getParameters().iterator(); //to initialize lazy elements
        }
        return byId;
    }

    @Override
    public void deleteDevice(String deviceName){
        usedDevicesRepository.deleteById(deviceName);
    }
}
