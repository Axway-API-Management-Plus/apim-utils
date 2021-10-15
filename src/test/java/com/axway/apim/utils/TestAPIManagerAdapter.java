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

import com.axway.apim.utils.adapter.APIManagerAdapter;
import com.axway.apim.utils.model.API;
import com.axway.apim.utils.model.Application;
import com.axway.apim.utils.model.User;

public class TestAPIManagerAdapter {
	
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
		new MockServerClient("127.0.01", 1080)
		.when(request()
			.withPath("/api/portal/v1.3/proxies/2fc22ca8-cf55-49aa-ba0f-6a1dc413cee7/applications") )
			.respond(response().withBody(new JsonBody(subscribedAppList))
		);
		APIManagerAdapter adapter = APIManagerAdapter.getInstance("https://localhost:1080", "user", "password");
		
		List<Application> apps = adapter.proxiesAdapter.getSubscribedApps("2fc22ca8-cf55-49aa-ba0f-6a1dc413cee7");
		Assert.assertEquals(apps.size(), 4, "Expected 4 applications subscribed to the API");
		Assert.assertEquals(apps.get(0).getName(), "Plexus Suite - Surgery Center");
		Assert.assertEquals(apps.get(0).getCreatedBy(), "d8554fd4-03a8-4371-be00-ADMIN");
		Assert.assertEquals(apps.get(0).getManagedBy().size(), 1);
	}
	
	@SuppressWarnings("resource")
	@Test
	public void testNoSubscribedApps() throws Exception {
		new MockServerClient("127.0.01", 1080)
		.when(request()
			.withPath("/api/portal/v1.3/proxies/36e3d6ae-9cfc-4a8e-859c-b35fe96ec9f9/applications") )
			.respond(response().withBody(new JsonBody("[]"))
		);
		APIManagerAdapter adapter = APIManagerAdapter.getInstance("https://localhost:1080", "user", "password");
		
		List<Application> apps = adapter.proxiesAdapter.getSubscribedApps("36e3d6ae-9cfc-4a8e-859c-b35fe96ec9f9");
		Assert.assertEquals(apps.size(), 0, "Expected 0 applications subscribed to the API");
	}

	
	@SuppressWarnings("resource")
	@Test
	public void testUserLookup() throws Exception {
		String standardUserMultipleOrgs = Files.readFile(this.getClass().getClassLoader().getResourceAsStream(TEST_PACKAGE + "UserMarkusMultipleOrgs.json"));
		new MockServerClient("127.0.01", 1080)
		.when(request()
			.withPath("/api/portal/v1.3/users/2e2b0e93-b840-4493-89af-MARKUS"))
			.respond(response().withStatusCode(200).withBody(new JsonBody(standardUserMultipleOrgs))
		);
		APIManagerAdapter adapter = APIManagerAdapter.getInstance("https://localhost:1080", "user", "password");
		
		User user = adapter.userAdapter.getUser("2e2b0e93-b840-4493-89af-MARKUS");
		Assert.assertEquals(user.getName(), "Markus");
		Assert.assertEquals(user.getId(), "2e2b0e93-b840-4493-89af-MARKUS");
	}
	
	@SuppressWarnings("resource")
	@Test
	public void testInvalidUserIdLookup() throws Exception {
		String standardUserMultipleOrgs = Files.readFile(this.getClass().getClassLoader().getResourceAsStream(TEST_PACKAGE + "UserNotFoundResponse.json"));
		new MockServerClient("127.0.01", 1080)
		.when(request()
			.withPath("/api/portal/v1.3/users/2e2b0e93-b840-4493-89af-WRONG"))
			.respond(response().withStatusCode(404).withBody(new JsonBody(standardUserMultipleOrgs))
		);
		APIManagerAdapter adapter = APIManagerAdapter.getInstance("https://localhost:1080", "user", "password");
		
		User user = adapter.userAdapter.getUser("2e2b0e93-b840-4493-89af-WRONG");
		Assert.assertNull(user);
	}
	
	@SuppressWarnings("resource")
	@Test
	public void testAPILookup() throws Exception {
		String apiDetails = Files.readFile(this.getClass().getClassLoader().getResourceAsStream(TEST_PACKAGE + "SingleAPIDetails.json"));
		new MockServerClient("127.0.01", 1080)
		.when(request()
			.withPath("/api/portal/v1.3/proxies/2fc22ca8-cf55-49aa-ba0f-6a1dc413cee7"))
			.respond(response().withStatusCode(200).withBody(new JsonBody(apiDetails))
		);
		APIManagerAdapter adapter = APIManagerAdapter.getInstance("https://localhost:1080", "user", "password");
		
		API api = adapter.proxiesAdapter.getAPI("2fc22ca8-cf55-49aa-ba0f-6a1dc413cee7");
		Assert.assertNotNull(api);
		Assert.assertEquals(api.getName(), "Greeting API");
	}
	
	@SuppressWarnings("resource")
	@Test(expectedExceptions = IllegalArgumentException.class)
	public void testInvalidAPILookup() throws Exception {
		new MockServerClient("127.0.01", 1080)
		.when(request()
			.withPath("/api/portal/v1.3/proxies/2fc22ca8-cf55-49aa-ba0f-WRONG"))
			.respond(response().withStatusCode(403).withBody(new JsonBody("{ \"errors\": [ { \"code\": 403, \"message\": \"Forbidden\" } ] }"))
		);
		APIManagerAdapter adapter = APIManagerAdapter.getInstance("https://localhost:1080", "user", "password");
		
		adapter.proxiesAdapter.getAPI("2fc22ca8-cf55-49aa-ba0f-WRONG");
	}
}