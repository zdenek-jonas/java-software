package com.avaya.microstream;

import one.microstream.concurrency.XThreads;
import one.microstream.storage.embedded.types.EmbeddedStorageManager;
import org.apache.log4j.Logger;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PreDestroy;
import java.util.Map;

@RestController
public class TestController {

    private static final Logger LOG = Logger.getLogger(TestController.class);

    private EmbeddedStorageManager microStreamStorageManager;

    private StateData stateData;

    public TestController(EmbeddedStorageManager microStreamStorageManager, StateData stateData) {
        this.microStreamStorageManager = microStreamStorageManager;
        this.stateData = stateData;
    }

    @RequestMapping(name = "ping", method = RequestMethod.GET, path = { "/ping" })
    public String ping()
    {
        XThreads.executeSynchronized(new TestController.StateUpdater("key-" + System.currentTimeMillis(), "value-" + Math.random()));

        return "Updated embedded storage";
    }

    @PreDestroy
    public void shutdown()
    {
        microStreamStorageManager.shutdown();

        LOG.info("MicroStreamTester shutting down");
    }

    private class StateUpdater implements Runnable
    {
        private String keyField,
                valueField;

        StateUpdater(String key, String value)
        {
            keyField = key;
            valueField = value;
        }

        @Override
        public void run()
        {
            String value = stateData.getStateData().get(keyField);

            if (value == null)
            {
                stateData.getStateData().put(keyField, valueField);
                microStreamStorageManager.storeRoot();
                LOG.debug("Updated state");
            }

            LOG.debug("State data:");
            for (Map.Entry<String, String> oneEntry: stateData.getStateData().entrySet())
                LOG.debug(oneEntry.getKey() + " => " + oneEntry.getValue());
        }
    }

}
