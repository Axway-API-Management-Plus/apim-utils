package com.axway.apim.utils.adapter;

import java.util.HashMap;
import java.util.Map;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.util.EntityUtils;

import com.axway.apim.utils.Utils;
import com.axway.apim.utils.Utils.TraceLevel;
import com.axway.apim.utils.model.User;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class APIManagerUserAdapter {
	
	Map<String, String> userDetails = new HashMap<String, String>();
	
	ObjectMapper mapper = new ObjectMapper();
	
	APIManagerHttpClient apiManager; 
	
	public APIManagerUserAdapter(APIManagerHttpClient apiManager) throws Exception {
		super();
		this.apiManager = apiManager;
	}
	
	private String readUserFromManager(String userId) throws Exception {
		if(userDetails.containsKey(userId)) return userDetails.get(userId);
		CloseableHttpResponse httpResponse = apiManager.get("/users/"+userId);
		
		try {
			int statusCode = httpResponse.getStatusLine().getStatusCode();
			String response = EntityUtils.toString(httpResponse.getEntity());
			if(statusCode!=200) {
				if(statusCode==404) {
					userDetails.put(userId, null);
					return null; // For this use-case we accept if a user-Id could not be found
				}
				Utils.traceMessage("Error getting user from API-Manager. Received Status-Code: " +statusCode+ ", Response: '" + response + "'", TraceLevel.ERROR);
				throw new IllegalArgumentException("Error getting user from API-Manager.");
			}
			userDetails.put(userId, response);
			return response;
		} finally {
			httpResponse.close();
		}
	}
	
	public User getUser(String userId) throws Exception {
		String userDetails = readUserFromManager(userId);
		if(userDetails==null) {
			Utils.traceMessage("No user found with ID: " + userId, TraceLevel.ERROR);
			return null;
		}
		User user = mapper.readValue(userDetails, User.class);
		return user;
	}
	
	public JsonNode getJsonUser(String userId) throws Exception {
		String userDetails = readUserFromManager(userId);
		if(userDetails==null) {
			Utils.traceMessage("No user found with ID: " + userId, TraceLevel.ERROR);
			return null;
		}
		return mapper.readTree(userDetails);
	}
}
