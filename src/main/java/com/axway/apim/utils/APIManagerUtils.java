package com.axway.apim.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.axway.apim.utils.Utils.TraceLevel;
import com.axway.apim.utils.adapter.APIManagerAdapter;
import com.axway.apim.utils.model.API;
import com.axway.apim.utils.model.Application;
import com.axway.apim.utils.model.Subscriber;
import com.axway.apim.utils.model.User;
import com.fasterxml.jackson.databind.JsonNode;

public class APIManagerUtils {
	
	private static APIManagerUtils instance;
	
	APIManagerAdapter managerAdapter;

	public static synchronized APIManagerUtils getInstance(String username, String password) throws Exception  {
		String apiManagerURL = "https://localhost:8075";
		return getInstance(apiManagerURL, username, password);
	}
	
	public static synchronized APIManagerUtils getInstance(String apiManagerUrl, String username, String password) throws Exception  {
		if(instance!=null) return instance;
		instance = new APIManagerUtils(apiManagerUrl, username, password);
		return instance;
	}
	
	private APIManagerUtils(String apiManagerUrl, String username, String password) throws Exception {
		super();
		managerAdapter = APIManagerAdapter.getInstance(apiManagerUrl, username, password);
	}
	
	public List<Subscriber> getSubscribers(String apIId) throws Exception {
		return getSubscribers(apIId, null);
	}
	
	/**
	 * @param apIId the ID of the API
	 * @param notificationProperty a user custom-property (switch true/false) used to control if user has subscribed to notifications
	 * @return a list of subscribers
	 * @throws Exception
	 */
	public List<Subscriber> getSubscribers(String apIId, String notificationProperty) throws Exception {
		API api = managerAdapter.proxiesAdapter.getAPI(apIId);
		List<Subscriber> subscribers = new ArrayList<Subscriber>();
		Map<String, User> alreadyFound = new HashMap<String, User>();
		List<Application> apps = managerAdapter.proxiesAdapter.getSubscribedApps(apIId);
		User user;
		Subscriber subscriber;
		for(Application app : apps) {
			if(!alreadyFound.containsKey(app.getCreatedBy())) {
				user = managerAdapter.userAdapter.getUser(app.getCreatedBy());
				subscriber = addSubscriber(subscribers, user, notificationProperty);
				if(subscriber != null) {
					alreadyFound.put(user.getId(), user);
					Utils.traceMessage("Found: "+subscriber.toString()+" for: "+api.toString()+" based on: " + app.toString(), TraceLevel.INFO);
				}
			}
			if(app.getManagedBy()!=null) {
				for(String managedById : app.getManagedBy()) {
					if(alreadyFound.containsKey(managedById)) continue;
					user = managerAdapter.userAdapter.getUser(managedById);
					subscriber = addSubscriber(subscribers, user, notificationProperty);
					if(subscriber == null) continue;
					alreadyFound.put(user.getId(), user);
					Utils.traceMessage("Found: "+subscriber.toString()+" for: "+api.toString()+" based on: " + app.toString(), TraceLevel.INFO);
				}
			}
		}
		return subscribers;
	}
	
	private Subscriber addSubscriber(List<Subscriber> subscribers, User user, String notificationProperty) throws Exception {
		if(notificationProperty!=null) {
			JsonNode json = managerAdapter.userAdapter.getJsonUser(user.getId());
			JsonNode notify = json.get(notificationProperty);
			if(notify==null || notify.asText().equals("false")) {
				Utils.traceMessage("Ignore user: " + user.toString() + ". Notify flag: " + notificationProperty + " is false/null.", TraceLevel.DEBUG);
				return null;
			}
		}
		Subscriber subscriber = new Subscriber();
		subscriber.setEmail(user.getEmail());
		subscriber.setName(user.getName());
		subscriber.setLoginName(user.getLoginName());
		subscribers.add(subscriber);
		return subscriber;
	}
}
