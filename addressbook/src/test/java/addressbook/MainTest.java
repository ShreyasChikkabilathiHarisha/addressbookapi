package addressbook;

import static org.junit.Assert.*;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.gson.Gson;

import junit.framework.TestResult;
import spark.Spark;
import spark.utils.IOUtils;

public class MainTest {

	private static ElasticSearchConnector es = null;

	private static String numberOfShards  	= "1";
	private static String numberOfReplicas	= "2";
	private static String clusterName 		= "elasticsearch";
	private static String indexName 		= "addressbook86";
	private static String indexType 		= "_doc";
	private static String ip 				= "localhost";
	private static int    port				= 9300;
	List<Contact> test_contact_list_expected = new  ArrayList<Contact>();
	
	@BeforeClass
	public static void beforeClass() {
		try {
			es = new ElasticSearchConnector( clusterName, ip, port );
			System.out.println(es);
			if( !es.isIndexRegistered( indexName ) )
			{
				// create index if not already existing
				es.createIndex( indexName, numberOfShards, numberOfReplicas );
			}
		}
		catch ( IOException e ) {
			e.printStackTrace();
		}
//		new MainTest().aNewContactShouldBeCreated();
	}

	@AfterClass
	public static void afterClass() {
		Spark.stop();
	}
	@Test
	public void createContactTest() {
		// ContactService object to run Test cases
    	ContactService test_contactService = new ContactService();
    	
    	// Test createContact
    	Contact test_createContact_result = test_contactService.createContact("ash3", "6317364829", es, indexName, indexType);
    	Contact test_createContact_expected = new Contact("ash3", "6317364829");
		assertEquals(test_createContact_expected , test_createContact_result);
		
//		new MainTest().aNewUserShouldBeCreated();
	}
	@Test
	public void getContactTest() {
		// ContactService object to run Test cases
    	ContactService test_contactService = new ContactService();
    	
    	// Test createContact
    	Contact test_getContact_result = test_contactService.getContact("ash2", es, indexName, indexType);
    	Contact test_getContact_expected = new Contact("ash2", "6317364829");
		assertEquals(test_getContact_expected , test_getContact_result);
	}
	@Test
	public void updateContactTest() {
		// ContactService object to run Test cases
    	ContactService test_contactService = new ContactService();
    	
    	// Test createContact
    	Contact test_updateContact_result = test_contactService.updateContact("ash2", "5555555555", es, indexName, indexType);
    	Contact test_updateContact_expected = new Contact("ash2", "5555555555");
		assertEquals(test_updateContact_expected , test_updateContact_result);
	}
	@Test
	public void deleteContactTest() {
		// ContactService object to run Test cases
    	ContactService test_contactService = new ContactService();
    	
    	// Test createContact
    	String test_deleteContact_result = test_contactService.deleteContact("ash2", es, indexName, indexType);
		assertEquals("Deleted successfully" , test_deleteContact_result);
	}
	
	/*
	private static class TestResponse {

		public final String body;
		public final int status;

		public TestResponse(int status, String body) {
			this.status = status;
			this.body = body;
		}

		public Map<String,String> json() {
			return new Gson().fromJson(body, HashMap.class);
		}
	}
	
	private TestResponse request(String method, String path) {
		try {
			URL url = new URL("http://localhost:4567" + path);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod(method);
			connection.setDoOutput(true);
			connection.connect();
			String body = IOUtils.toString(connection.getInputStream());
			TestResponse res = new TestResponse(connection.getResponseCode(), body);
			connection.disconnect();
			return res;
		} catch (IOException e) {
			e.printStackTrace();
			fail("Sending request failed: " + e.getMessage());
			return null;
		}
	}
	*/
	/*
	@Test
	public void aNewContactShouldBeCreated() {
		TestResponse res = request("POST", "/contact?name=ash2&number=6317364829");
		Map<String, String> json = res.json();
		assertEquals(200, res.status);
		assertEquals("ash2", json.get("name"));
		assertEquals("6317364829", json.get("number"));
		assertNotNull(json.get("name"));
	}
	*/

}
