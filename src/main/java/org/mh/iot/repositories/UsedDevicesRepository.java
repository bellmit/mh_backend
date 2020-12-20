package org.mh.iot.repositories;

import org.mh.iot.models.Device;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by evolshan on 24.10.2018.
 */
@Repository
public interface UsedDevicesRepository extends JpaRepository<Device, String> {
}
