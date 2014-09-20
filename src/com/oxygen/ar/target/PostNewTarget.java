package com.oxygen.ar.target;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Date;

import my.org.apache.commons.codec.binary.Base64;
import my.org.apache.commons.io.FileUtils;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.cookie.DateUtils;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;



// See the Vuforia Web Services Developer API Specification - https://developer.vuforia.com/resources/dev-guide/adding-target-cloud-database-api

public class PostNewTarget implements TargetStatusListener, Runnable {

	//Server Keys
	private String accessKey = "aae934c1ca32a39ec28b4334e3743e4ef0fef78a";
	private String secretKey = "ba471eefd828429a14ef80220662a0742dc7bfd5";
	
	private String url = "https://vws.vuforia.com";
	private String targetName = "chips";
	private String imageLocation = "/storage/emulated/0/VoforiaSample/chips.jpg";

	private TargetStatusPoller targetStatusPoller;
	private final float pollingIntervalMinutes = 60;//poll at 1-hour interval
	
	public PostNewTarget(String name,String location){
			targetName=name;
			imageLocation=location;
	}
	
	private String postTarget() throws URISyntaxException, ClientProtocolException, IOException, JSONException {
		
		//System.out.println(Base64.class.getProtectionDomain().getCodeSource().getLocation());
		
		HttpPost postRequest = new HttpPost();
		HttpClient client = new DefaultHttpClient();
		postRequest.setURI(new URI(url + "/targets"));
		JSONObject requestBody = new JSONObject();
		
		setRequestBody(requestBody);
		postRequest.setEntity(new StringEntity(requestBody.toString()));
		setHeaders(postRequest); // Must be done after setting the body
		
		System.out.println("before execute");
		HttpResponse response = client.execute(postRequest);
		System.out.println("behind execute");
		String responseBody = EntityUtils.toString(response.getEntity());
		System.out.println(responseBody);
		
		JSONObject jobj = new JSONObject(responseBody);
		
		String uniqueTargetId = jobj.has("target_id") ? jobj.getString("target_id") : "";
		System.out.println("\nCreated target with id: " + uniqueTargetId);
		
		return uniqueTargetId;
	}
	
	private void setRequestBody(JSONObject requestBody) throws IOException, JSONException {
		File imageFile = new File(imageLocation);
		if(!imageFile.exists()) {
			//imageFile.createNewFile();
			System.out.println("File location does not exist!");
			System.exit(1);
		}
		byte[] image = FileUtils.readFileToByteArray(imageFile);
		requestBody.put("name", targetName); // Mandatory
		requestBody.put("width", 320.0); // Mandatory
		requestBody.put("image", Base64.encodeBase64String(image)); // Mandatory
		//requestBody.put("active_flag", 1); // Optional
		//requestBody.put("application_metadata", Base64.encodeBase64String("Vuforia test metadata".getBytes())); // Optional
		System.out.println("name="+requestBody.get("name"));
		System.out.println("width="+requestBody.get("width"));
		System.out.println("image="+requestBody.get("image"));
	}
	
	private void setHeaders(HttpUriRequest request) {
		SignatureBuilder sb = new SignatureBuilder();
		request.setHeader(new BasicHeader("Date", DateUtils.formatDate(new Date()).replaceFirst("[+]00:00$", "")));
		request.setHeader(new BasicHeader("Content-Type", "application/json"));
		request.setHeader("Authorization", "VWS " + accessKey + ":" + sb.tmsSignature(request, secretKey));
		System.out.println("header");
		Header[] hs=request.getAllHeaders();
		for(Header h: hs){
			System.out.println(h.getName()+"  "+h.getValue());
		}
	}
	
	/**
	 * Posts a new target to the Cloud database; 
	 * then starts a periodic polling until 'status' of created target is reported as 'success'.
	 */
	public void postTargetThenPollStatus() {
		String createdTargetId = "";
		try {
			createdTargetId = postTarget();
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
	
		// Poll the target status until the 'status' is 'success'
		// The TargetState will be passed to the OnTargetStatusUpdate callback 
		if (createdTargetId != null && !createdTargetId.equals("")) {
			targetStatusPoller = new TargetStatusPoller(pollingIntervalMinutes, createdTargetId, accessKey, secretKey, this );
			targetStatusPoller.startPolling();
		}
	}
	
	// Called with each update of the target status received by the TargetStatusPoller
	@Override
	public void OnTargetStatusUpdate(TargetState target_state) {
		if (target_state.hasState) {
			
			String status = target_state.getStatus();
			
			System.out.println("Target status is: " + (status != null ? status : "unknown"));
			
			if (target_state.getActiveFlag() == true && "success".equalsIgnoreCase(status)) {
				targetStatusPoller.stopPolling();
				System.out.println("Target is now in 'success' status");
			}
		}
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		Log.v("publish", "beginRun");
		postTargetThenPollStatus();
		Log.v("publish", "endRun");
	}
	
	
	/*public static void main(String[] args) throws URISyntaxException, ClientProtocolException, IOException, JSONException {
		System.out.println("main");
		
		PostNewTarget p = new PostNewTarget();
		p.postTargetThenPollStatus();
	}*/
	
}
