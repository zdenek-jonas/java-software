package com.avaya.microstream;

import one.microstream.integrations.spring.boot.types.Storage;
import org.apache.log4j.Logger;

import java.util.HashMap;

@Storage
public class StateData {
    private static final Logger LOG = Logger.getLogger(StateData.class);

    private final HashMap<String, String> stateDataField = new HashMap<>();

    public StateData() {
        LOG.debug("Created StateData");
    }

    public HashMap<String, String> getStateData() {
        return stateDataField;
    }
}
