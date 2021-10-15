package com.axway.apim.utils;

import static org.mockserver.integration.ClientAndServer.startClientAndServer;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

import java.util.List;

import org.mockserver.client.MockServerClient;
import org.mockserver.configuration.ConfigurationProperties;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.model.JsonBody;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.testng.reporters.Files;

import com.axway.apim.utils.model.Subscriber;

public class TestSubscriptionProvider {
	
	@BeforeClass
	public void beforeClass() {
		mockServer = startClientAndServer(1080);
		ConfigurationProperties.logLevel("WARNING");
	}

	@AfterClass
	public void afterClass() {
		mockServer.stop();
	}
	
	private ClientAndServer mockServer;
	
	private static final String TEST_PACKAGE = "com/axway/apim/mockedResponses/";
	
	@SuppressWarnings("resource")
	@Test
	public void testSubscribedApplications() throws Exception {
		String subscribedAppList = Files.readFile(this.getClass().getClassLoader().getResourceAsStream(TEST_PACKAGE + "SubscribedApplicationList.json"));
		String userAdmin = Files.readFile(this.getClass().getClassLoader().getResourceAsStream(TEST_PACKAGE + "UserAdmin.json"));
		String userMarkus = Files.readFile(this.getClass().getClassLoader().getResourceAsStream(TEST_PACKAGE + "UserMarkusMultipleOrgs.json"));
		String userAlice = Files.readFile(this.getClass().getClassLoader().getResourceAsStream(TEST_PACKAGE + "UserAlice.json"));
		String apiDetails = Files.readFile(this.getClass().getClassLoader().getResourceAsStream(TEST_PACKAGE + "SingleAPIDetails.json"));
		
		
		new MockServerClient("127.0.01", 1080).when(request()
			.withPath("/api/portal/v1.3/proxies/2fc22ca8-cf55-49aa-ba0f-6a1dc413cee7/applications") )
			.respond(response().withBody(new JsonBody(subscribedAppList))
		);
		new MockServerClient("127.0.01", 1080).when(request()
			.withPath("/api/portal/v1.3/proxies/2fc22ca8-cf55-49aa-ba0f-6a1dc413cee7") ) // API-Details
			.respond(response().withBody(new JsonBody(apiDetails))
		);
		new MockServerClient("127.0.01", 1080).when(request()
			.withPath("/api/portal/v1.3/users/d8554fd4-03a8-4371-be00-ADMIN") ) // This is the Admin-Users created 3 of the 4 applications
			.respond(response().withBody(new JsonBody(userAdmin))
		);
		new MockServerClient("127.0.01", 1080).when(request()
			.withPath("/api/portal/v1.3/users/2e2b0e93-b840-4493-89af-MARKUS") ) // This is user Markus, created 1 application
			.respond(response().withBody(new JsonBody(userMarkus))
		);
		new MockServerClient("127.0.01", 1080).when(request()
			.withPath("/api/portal/v1.3/users/99a509ed-088a-4513-b450-ALICE") ) // This is user Alice, is managing application 1
			.respond(response().withBody(new JsonBody(userAlice))
		);
		
		
		APIManagerUtils subscriptionsProvider = APIManagerUtils.getInstance("https://localhost:1080", "user", "password");
		
		List<Subscriber> subscribers = subscriptionsProvider.getSubscribers("2fc22ca8-cf55-49aa-ba0f-6a1dc413cee7");
		
		Assert.assertEquals(subscribers.size(), 3, "Expected 3 subcribers to the API incl. the managedBy user.");
		Assert.assertEquals(subscribers.get(0).getName(), "API Administrator");
		
		subscribers = subscriptionsProvider.getSubscribers("2fc22ca8-cf55-49aa-ba0f-6a1dc413cee7", "notify");
		
		Assert.assertEquals(subscribers.size(), 1, "Expected 1 subcriber as only markus has a subscription configured.");
		Assert.assertEquals(subscribers.get(0).getName(), "Markus");
	}
}