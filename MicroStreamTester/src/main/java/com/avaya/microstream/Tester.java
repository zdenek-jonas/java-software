/*
 * 
 * @author Reinhard Klemm, Avaya
 * 
 */

package com.avaya.microstream;

import java.util.HashMap;
import java.util.Map.Entry;

import javax.annotation.PreDestroy;

import org.apache.log4j.Logger;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import one.microstream.concurrency.XThreads;
import one.microstream.storage.embedded.types.EmbeddedStorage;
import one.microstream.storage.embedded.types.EmbeddedStorageManager;

@SpringBootApplication
@RestController
@Configuration
public class Tester implements CommandLineRunner 
{
	private static final Logger LOG = Logger.getLogger(Tester.class);
	
	private static final HashMap<String, String> STATE_DATA = new HashMap<>();

	private EmbeddedStorageManager analysisStorageManager;
	
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
			String value = STATE_DATA.get(keyField);
			
			if (value == null)
			{
				STATE_DATA.put(keyField, valueField);
				analysisStorageManager.storeRoot();
				LOG.debug("Updated state");
			}
			
			LOG.debug("State data:");
			for (Entry<String, String> oneEntry: STATE_DATA.entrySet())
				LOG.debug(oneEntry.getKey() + " => " + oneEntry.getValue());
		}
	}
	
	@Override
	public void run(String... args) throws Exception
	{
		analysisStorageManager = EmbeddedStorage.start(STATE_DATA);
		
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
		analysisStorageManager.shutdown();
	
		LOG.info("MicroStreamTester shutting down");
	}
}
