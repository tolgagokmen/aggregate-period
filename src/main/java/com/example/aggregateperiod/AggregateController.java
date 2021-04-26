package com.example.aggregateperiod;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

@RestController
public class AggregateController {

	private static final String PERIOD = "period";
	private static final String TIME = "time";
	private static final String destUrl = "https://reference.intellisense.io/test.dataprovider";

	@PostMapping("/aggregate")
	public ResponseEntity<String> calculateVolume(@RequestBody AggregateRequest volumeRequest) throws IOException {
		String ress;
		HttpPost post = new HttpPost(destUrl);

		List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
		urlParameters.add(new BasicNameValuePair(PERIOD, volumeRequest.getPeriod().toString()));

		post.setEntity(new UrlEncodedFormEntity(urlParameters));

		try (CloseableHttpClient httpClient = HttpClients.createDefault();
				CloseableHttpResponse response = httpClient.execute(post)) {
			String res = EntityUtils.toString(response.getEntity());
			JsonObject outRes = buildResponse(volumeRequest.getPeriod(), res);
			ress = outRes.toString();
		}

		ResponseEntity<String> responseEntity = new ResponseEntity<String>(ress, HttpStatus.OK);
		return responseEntity;

	}

	private JsonObject buildResponse(Integer period, String responseString) throws IOException {
		JsonObject asJsonObject = new JsonParser().parse(responseString).getAsJsonObject();
		JsonObject outRes = new JsonObject();

		for (String keyRoot : asJsonObject.keySet()) {
			JsonObject inJo = asJsonObject.getAsJsonObject(keyRoot);

			JsonObject outJo = new JsonObject();
			for (String key : inJo.keySet()) {
				JsonArray ja1 = inJo.getAsJsonArray(key);
				JsonArray janew = new JsonArray();
				outJo.add(key, janew);
				Double avg = 0d;
				Double dd = (!ja1.get(0).isJsonNull() && !key.equals(TIME)) ? ja1.get(0).getAsDouble() : 0d;
				for (int i = 1; i < ja1.size(); i++) {
					if ((i) % period != 0 && !key.equals(TIME)) {
						try {
							dd = (!ja1.get(i).isJsonNull()) ? ja1.get(i).getAsDouble() : 0d;
						} catch (Exception e) {
							dd = (!ja1.get(i).isJsonNull()) ? ja1.get(i).getAsDouble() : 0d;
						}
						avg += dd;
					} else {
						if (key.equals(TIME)) {
							if ((i) % period == 0)
								janew.add(ja1.get(i).getAsString());
						} else {
							dd = (!ja1.get(i).isJsonNull()) ? ja1.get(i).getAsDouble() : 0d;
							avg += dd;
							avg = avg / period;
							janew.add(avg);
							avg = 0d;

						}

					}

				}

			}
			outRes.add(keyRoot, outJo);
		}
		return outRes;
	}
}
