package com.example.library_management.bdd;

import org.assertj.swing.edt.FailOnThreadViolationRepaintManager;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.testcontainers.containers.MongoDBContainer;

@RunWith(Cucumber.class)
@CucumberOptions(features = "src/bdd/resources", monochrome = true, strict = true)
public class LibrarySwingAppBDD {

	private static MongoDBContainer mongo;
	public static int mongoPort;

	@BeforeClass
	public static void setUpOnce() {
		FailOnThreadViolationRepaintManager.install();
		mongo = new MongoDBContainer("mongo:4.4.3");
		mongo.start();
		mongoPort = mongo.getMappedPort(27017);
		System.setProperty("mongo.port", String.valueOf(mongoPort));
	}

	@AfterClass
	public static void tearDownOnce() {
		if (mongo != null) {
			mongo.stop();
		}
	}
}
