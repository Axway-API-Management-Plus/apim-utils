package com.axway.apim.utils.adapter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.util.EntityUtils;

import com.axway.apim.utils.Utils;
import com.axway.apim.utils.Utils.TraceLevel;
import com.axway.apim.utils.model.API;
import com.axway.apim.utils.model.Application;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class APIManagerProxiesAdapter {
	
	ObjectMapper mapper = new ObjectMapper();
	
	Map<String, API> apiDetails = new HashMap<String, API>();
	
	APIManagerHttpClient apiManager;
	
	public APIManagerProxiesAdapter(APIManagerHttpClient apiManager) throws Exception {
		super();
		this.apiManager = apiManager;
	}
	
	public API getAPI(String apiId) throws Exception {
		if(apiDetails.containsKey(apiId)) return apiDetails.get(apiId);
		CloseableHttpResponse httpResponse = apiManager.get("/proxies/"+apiId+"");
		try {
			int statusCode = httpResponse.getStatusLine().getStatusCode();
			String response = EntityUtils.toString(httpResponse.getEntity());
			if(statusCode!=200) {
				Utils.traceMessage("Error getting API-Details for API with ID: "+apiId+" from API-Manager. Received Status-Code: " +statusCode+ ", Response: '" + response + "'", TraceLevel.ERROR);
				throw new IllegalArgumentException("Error getting API-Details for API with ID: "+apiId+" from API-Manager.");
			}
			API api = mapper.readValue(response, API.class);
			apiDetails.put(apiId, api);
			return api;
		} finally {
			httpResponse.close();
		}
	}

	public List<Application> getSubscribedApps(String apiId) throws Exception {
		CloseableHttpResponse httpResponse = apiManager.get("/proxies/"+apiId+"/applications");
		
		try {
			int statusCode = httpResponse.getStatusLine().getStatusCode();
			String response = EntityUtils.toString(httpResponse.getEntity());
			if(statusCode!=200) {
				Utils.traceMessage("Error getting Application-Subscriptions for API with ID: "+apiId+" from API-Manager. Received Status-Code: " +statusCode+ ", Response: '" + response + "'", TraceLevel.ERROR);
				throw new IllegalArgumentException("Error getting Application-Subscriptions for API with ID: "+apiId+" from API-Manager.");
			}
			// Parse the received list of applications
			List<Application> apps = mapper.readValue(response, new TypeReference<List<Application>>(){});
			return apps;
		} finally {
			httpResponse.close();
		}
	}

}
