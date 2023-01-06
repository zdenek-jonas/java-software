package com.avaya.microstream;

import java.util.Map.Entry;

import javax.annotation.PreDestroy;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import one.microstream.concurrency.XThreads;
import one.microstream.storage.embedded.types.EmbeddedStorageManager;

@SpringBootApplication
@RestController
public class Tester implements CommandLineRunner 
{
	private static final Logger LOG = Logger.getLogger(Tester.class);

	@Autowired
	private EmbeddedStorageManager embeddedStorageManager;
	
	@Autowired
	private StateData stateData;
	
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
				embeddedStorageManager.storeRoot();
				LOG.debug("Updated state");
			}
			
			LOG.debug("State data:");
			for (Entry<String, String> oneEntry: stateData.getStateData().entrySet())
				LOG.debug(oneEntry.getKey() + " => " + oneEntry.getValue());
		}
	}
	
	@Override
	public void run(String... args) throws Exception
	{
		//embeddedStorageManager = EmbeddedStorage.start(stateData);
		embeddedStorageManager.setRoot(stateData);
		LOG.debug("MicroStream object ID: " + embeddedStorageManager.storeRoot());
		LOG.info("MicroStreamTester started successfully");
	}
	
	@RequestMapping(name = "ping", method = RequestMethod.GET, path = { "/ping" })
	public String ping()
	{
		XThreads.executeSynchronized(new StateUpdater("key-" + System.currentTimeMillis(), "value-" + Math.random()));
		
		return "Updated embedded storage";
	}
		
	public static void main(String[] args)
	{
		SpringApplication.run(Tester.class, args);
	}

	@PreDestroy
	public void shutdown() 
	{
		embeddedStorageManager.shutdown();
	
		LOG.info("MicroStreamTester shutting down");
	}
}
