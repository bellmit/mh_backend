package org.mh.iot.bus.devices.interfaces;

import java.util.function.Predicate;

/**
 * Created by evolshan on 10.12.2020.
 */
public interface MessageSelector extends Predicate<String>{
    @Override
    boolean test(String s);
}
