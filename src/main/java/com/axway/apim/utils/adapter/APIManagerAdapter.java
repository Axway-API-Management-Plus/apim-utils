package com.axway.apim.utils.adapter;

public class APIManagerAdapter {
	
	private static APIManagerAdapter instance;
	
	protected APIManagerHttpClient apiManager;
	
	public APIManagerProxiesAdapter proxiesAdapter;
	public APIManagerUserAdapter userAdapter;
	
	public synchronized static APIManagerAdapter getInstance(String apiManagerUrl, String username, String password) throws Exception {
		if(instance!=null) return instance;
		instance = new APIManagerAdapter(apiManagerUrl, username, password);
		return instance;
	}

	public APIManagerAdapter(String apiManagerUrl, String username, String password) throws Exception {
		super();
		apiManager = new APIManagerHttpClient(apiManagerUrl, username, password);
		this.proxiesAdapter = new APIManagerProxiesAdapter(apiManager);
		this.userAdapter = new APIManagerUserAdapter(apiManager);
	}
	
	
		
}
