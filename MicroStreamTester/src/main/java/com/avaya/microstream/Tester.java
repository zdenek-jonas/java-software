package com.avaya.microstream;

import java.util.Map.Entry;

import javax.annotation.PreDestroy;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import one.microstream.concurrency.XThreads;
import one.microstream.storage.embedded.types.EmbeddedStorageManager;

@SpringBootApplication

public class Tester implements CommandLineRunner
{
	private static final Logger LOG = Logger.getLogger(Tester.class);


	@Override
	public void run(String... args) throws Exception
	{
		LOG.info("MicroStreamTester started successfully");
	}
	

}
