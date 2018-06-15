package biodiv.auth.register;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.HttpClientUtils;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import net.minidev.json.parser.JSONParser;
import net.minidev.json.parser.ParseException;

public class GoogleRecaptchaCheck {
	private static final String GOOGLE_SECRET_KEY = "6LelEl8UAAAAAG9wxnPRTRYFtAJ0lOzuIpOM4Kxb";
	private static final String GOOGLE_API_ENDPOINT = "https://www.google.com/recaptcha/api/siteverify";
	private String userResponse;

	public GoogleRecaptchaCheck(String userResponse) {
		this.userResponse = userResponse;
	}

	public boolean isRobot() throws IOException {
		CloseableHttpClient httpClient = HttpClientBuilder.create().build();
		HttpPost postRequest = new HttpPost(GOOGLE_API_ENDPOINT);
		List<NameValuePair> postParameters = new ArrayList<NameValuePair>();
		postParameters.add(new BasicNameValuePair("secret", GOOGLE_SECRET_KEY));
		postParameters.add(new BasicNameValuePair("response", this.userResponse));
		postRequest.setEntity(new UrlEncodedFormEntity(postParameters));
		CloseableHttpResponse response = null;
		try {
			response = httpClient.execute(postRequest);
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				InputStream body = response.getEntity().getContent();
				JSONParser jsonParser = new JSONParser();
				JSONObject json = null;
				try {
					json = (JSONObject)jsonParser.parse(
					      new InputStreamReader(body, "UTF-8"));
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				//JSONObject json = Json.createReader(body).readObject();
				if (json != null && json.getBoolean("success")) {
					return false;
				}
				return true;
			}
			// here you might want to log the error-codes element of the json
			// response and/or the status code.
			return true;
		} finally {
			HttpClientUtils.closeQuietly(httpClient);
			HttpClientUtils.closeQuietly(response);
		}
	}
}
