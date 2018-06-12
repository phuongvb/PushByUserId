package com.alticast;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SendPushNotificationSample {
	public static final String PUSHWOOSH_SERVICE_BASE_URL = "https://cp.pushwoosh.com/json/1.3/";
	public static final String INPUT_FILE = "input.json";
	public static final String SEND_DATE = "send_date";
	public static final String CONTENT = "content";
	public static final String TITLE = "title";
	public static final String CUSTOM_DATA = "custom_data";
	public static final String USERS = "users";
	public static final String ANDROID_HEADER = "android_header";
	public static final String IOS_ROOT_PARAMS = "ios_root_params";
	public static final String ANDROID_ROOT_PARAMS = "android_root_params";
	public static final String IOS_TITLE = "ios_title";
	public static final String APPS = "apps";
	public static final String AUTH = "auth";
	public static final String APPLICATION = "application";
	public static final String NOTIFICATIONS = "notifications";

	public static void main(String[] args)
			throws JSONException, MalformedURLException, FileNotFoundException, IOException {
		String method = "createMessage";
		URL url = new URL(PUSHWOOSH_SERVICE_BASE_URL + method);

		String input = SendPushNotificationSample.dataFromFile(INPUT_FILE);
		JSONObject jsonObject = new JSONObject(input);

		String sendDate = jsonObject.getString(SEND_DATE);
		String content = jsonObject.getString(CONTENT);
		String title = jsonObject.getString(TITLE);

		String dataFile = jsonObject.getString(CUSTOM_DATA);
		String data = SendPushNotificationSample.dataFromFile(dataFile);

		String userFile = jsonObject.getString(USERS);
		String[] users = SendPushNotificationSample.dataFromFile(userFile).split(",");
		for (int i = 0, length = users.length; i < length; i++) {
			users[i] = users[i].trim();
		}
		JSONArray notificationsArray;
		if (users.length == 1 && users[0].equals("")) {
			notificationsArray = new JSONArray().put(new JSONObject().put(SEND_DATE, sendDate)
					.put(CONTENT, content).put(IOS_TITLE, title).put(ANDROID_HEADER, title).put(IOS_ROOT_PARAMS, data)
					.put(ANDROID_ROOT_PARAMS, data));
		} else {
			notificationsArray = new JSONArray().put(new JSONObject().put(SEND_DATE, sendDate)
					.put(CONTENT, content).put(IOS_TITLE, title).put(ANDROID_HEADER, title).put(IOS_ROOT_PARAMS, data)
					.put(ANDROID_ROOT_PARAMS, data).put(USERS, users));
		}
		System.out.println("notification array: " + notificationsArray.toString());
		JSONArray apps = jsonObject.getJSONArray(APPS);
		for (int i=0; i < apps.length(); i++) {
			JSONObject appObject = (JSONObject) apps.get(i);
			String authToken = appObject.getString(AUTH);
			JSONArray applicationCodes = appObject.getJSONArray(APPLICATION);
			for (int j=0; j < applicationCodes.length(); j++) {
				String code = applicationCodes.getString(j);
				SendPushNotificationSample.sendRequestToApplication(code, authToken, url, notificationsArray);
			}
		}
	}

	static String dataFromFile(String filename) throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(filename));
		StringBuilder input = new StringBuilder();

		String line;
		while ((line = reader.readLine()) != null) {
			line = line.trim();
			input.append(line).append('\r');
		}
		if (input.length() > 0) {
			input.deleteCharAt(input.length() - 1);
		}
		reader.close();
		return input.toString();
	}

	static void sendRequestToApplication(String applicationCode, String authToken, URL url,
			JSONArray notificationsArray) {
		try {
			JSONObject requestObject = new JSONObject().put(APPLICATION, applicationCode).put(AUTH, authToken)
					.put(NOTIFICATIONS, notificationsArray);
			JSONObject mainRequest = new JSONObject().put("request", requestObject);
			JSONObject response = SendServerRequest.sendJSONRequest(url, mainRequest.toString());
			System.out.println("Response is: " + response);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

}

class SendServerRequest {
	static JSONObject sendJSONRequest(URL url, String request) {
		HttpURLConnection connection = null;
		try {
			connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Content-Type", "application/json");
			connection.setDoInput(true);
			connection.setDoOutput(true);

			DataOutputStream writer = new DataOutputStream(connection.getOutputStream());
			writer.write(request.getBytes("UTF-8"));
			writer.flush();
			writer.close();

			return parseResponse(connection);
		} catch (Exception e) {
			System.out.println("An error occurred: " + e.getMessage());
			return null;
		} finally {
			if (connection != null) {
				connection.disconnect();
			}
		}
	}

	static JSONObject parseResponse(HttpURLConnection connection) throws IOException, JSONException {
		String line;
		BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		StringBuilder response = new StringBuilder();

		while ((line = reader.readLine()) != null) {
			response.append(line).append('\r');
		}
		reader.close();

		return new JSONObject(response.toString());
	}
}

// "send_date":"now", // Optional: YYYY-MM-DD HH:mm OR 'now'

//ViettelTV-iPhone-QA
//{
//	"auth":"q9mT9jczTO3a8CMgqO9PFWoI2LYY4QTxFd2JFf6oc9ChUZB2pL1fAleSo5jAEeNhlJfrsnHkQlOuaViFRo5v",
//	"application":["384FF-FA7C3"],
//}

