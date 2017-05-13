package diction;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Properties;
import java.util.Set;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;


public class ConceptNet {

	static String basePath = System.getProperty("user.dir") + "/resources/diction/conceptnet/";
	HashMap<String, String> stopWords = new HashMap<String, String>();
	Properties prop = new Properties();
	CloseableHttpClient httpClient;

	public static void main(String[] args) {
		ConceptNet obj = new ConceptNet();
		try{
			//obj.getRequestConceptNet("reality");
			long start1 = System.currentTimeMillis();
			String out = obj.getProp("reality");
			long end1 = System.currentTimeMillis();

			long start2 = System.currentTimeMillis();
			String out2 = obj.getProp("chair");
			long end2 = System.currentTimeMillis();

			System.out.println("First call: " + (end1-start1));
			System.out.println("Second call: " + (end2-start2));

			/*for(int i=0;i<50;i++){
				obj.getRequestConceptNet("pigs");
			}*/

			//System.out.println(out);
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	ConceptNet(){
		//httpClient = HttpClientBuilder.create().build();
		initStopWords();
		initProp();
	}

	@Override
	public void finalize() {
		/*		System.out.println("ConceptNet obj is getting destroyed");
		try {
			httpClient.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		httpClient = null;*/
	}

	private void initProp() {
		try {
			prop.load(new FileInputStream(basePath + "conceptnet.properties"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void saveProp() {
		try {
			prop.store(new FileOutputStream(basePath + "conceptnet.properties"), null);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public String getProp(String key){
		String value = prop.getProperty(key);
		if(value == null){
			try{
				value = getRequestConceptNet(key);
				prop.setProperty(key, value);
				saveProp();
			}catch(Exception e){
				value = "";
				e.printStackTrace();
			}
		}
		return key + "," + value;
	}

	private void initStopWords() {
		stopWords.put("a", "");
		stopWords.put("the", "");
		stopWords.put("from", "");
		stopWords.put("in", "");
		stopWords.put("n't", "");
		stopWords.put("of", "");
		stopWords.put("an", "");
		stopWords.put("this", "");
		stopWords.put("at", "");
		stopWords.put("on", "");
		stopWords.put("to", "");
		stopWords.put("it", "");
		stopWords.put("be", "");
		stopWords.put("very", "");
		stopWords.put("your", "");
		stopWords.put("you", "");
		stopWords.put("some", "");
		stopWords.put("up", "");
		stopWords.put("for", "");
		stopWords.put("them", "");
		stopWords.put("into", "");
		stopWords.put("about", "");
		stopWords.put("form", "");
		stopWords.put("many", "");
		stopWords.put("around", "");
		stopWords.put("with", "");
		stopWords.put("is", "");
		stopWords.put("his", "");
		stopWords.put("and", "");
	}


	private String getRequestConceptNet(String str) throws Exception {
		StringBuilder sb = new StringBuilder();
		try{
			CloseableHttpClient httpClient = HttpClientBuilder.create().build();
			HttpGet getRequest = new HttpGet("http://api.conceptnet.io/c/en/" + str);
			getRequest.addHeader("accept", "application/json");

			HttpResponse response = httpClient.execute(getRequest);

			if (response.getStatusLine().getStatusCode() != 200) {
				throw new RuntimeException("Failed : HTTP error code : " + response.getStatusLine().getStatusCode());
			}

			BufferedReader br = new BufferedReader(new InputStreamReader((response.getEntity().getContent())));

			String output = "";
			String finalOut = "";
			//System.out.println("Output from Server .... \n");
			while ((output = br.readLine()) != null) {
				finalOut = finalOut + output;
				//System.out.println(output);
			}

			JSONParser parser = new JSONParser(); 
			Object obj = parser.parse(finalOut);
			JSONObject jsonObj = (JSONObject) obj;

			JSONArray msg = (JSONArray) jsonObj.get("edges");
			Iterator iterator = msg.iterator();

			Set<String> wordSet = new LinkedHashSet<String>();
			while (iterator.hasNext()) {
				JSONObject jsonObjElem = (JSONObject) iterator.next();
				String surfaceText = (String)jsonObjElem.get("surfaceText");
				//System.out.println(surfaceText);
				if(surfaceText == null){
					//System.out.println("Skipping due to NULL");
					continue;
				}
				System.out.println(surfaceText);
				if(surfaceText.contains("not") || surfaceText.contains("translation")){
					System.out.println("Skipping due to not/translation");
					continue;
				}
				while(surfaceText.indexOf("[") != -1){
					int indexOpen = surfaceText.indexOf("[");
					int indexClose = surfaceText.indexOf("]");
					String subStr = surfaceText.substring(indexOpen+2, indexClose);
					//System.out.println(subStr);
					subStr = subStr.trim().toLowerCase();
					String[] strArr = subStr.split(" ");
					for(String word : strArr){
						if(!word.equals(str) && !stopWords.containsKey(word)){
							wordSet.add(word);
						}
					}
					surfaceText = surfaceText.substring(indexClose+2, surfaceText.length());
				}
				//System.out.println();
			}
			httpClient.close();
			httpClient = null;

			System.out.println(str + " : " + wordSet.toString());
			if(wordSet.size() != 0){
				for(String word : wordSet){
					sb.append(word + ",");
				}
				sb.deleteCharAt(sb.length()-1);
			}

		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return sb.toString();
	}


}
