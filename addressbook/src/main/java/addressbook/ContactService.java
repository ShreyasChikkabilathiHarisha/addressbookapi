package addressbook;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ContactService {
	private Map<String, Contact> contacts = new HashMap<>();
	
	public List<Contact> getOptions(String pageSize, String page, String query, ElasticSearchConnector es, String indexName, String indexType) {
		if(pageSize == null || pageSize.charAt(1) == '}')
			return getAllContacts(es, indexName, indexType);
		return getPageWiseContacts(pageSize, page, query, es, indexName, indexType);
	}

	public List<Contact> getAllContacts(ElasticSearchConnector es, String indexName, String indexType) {
		try {
			return es.myGetAll( indexName, indexType);
		}catch ( Exception e ) {
			e.printStackTrace();
		}
		return null;
	}
	
	public List<Contact> getPageWiseContacts(String pageSize, String page, String query, ElasticSearchConnector es, String indexName, String indexType) {
		List<Contact> result;
		if(pageSize.length() >0 && pageSize.charAt(0) == '{')
			pageSize = pageSize.substring(1, pageSize.length()-1);
		if(page.length() >0 && page.charAt(0) == '{')
		{
			page = page.substring(1, page.length()-1);
			if(page.length() == 0)
				page = "1";
		}
		try {
			result = es.myGetQuery( indexName, indexType, query);
		}catch ( Exception e ) {
			e.printStackTrace();
			return null;
		}
		int cur_page = 1;
		for(int i = 0; i < result.size(); i += Integer.parseInt(pageSize))
		{
			if(cur_page == Integer.parseInt(page))
			{
				if(i+Integer.parseInt(pageSize) < result.size())
					return result.subList(i, i+Integer.parseInt(pageSize));
				else
					return result.subList(i, result.size());
			}
			cur_page++;
		}
		return null;
	}

	public Contact getContact(String name, ElasticSearchConnector es, String indexName, String indexType) {
		String result = null;
		try {
			result = es.myGet( indexName, indexType, name);
		}catch ( IOException e ) {
			e.printStackTrace();
		}
		if(result == null)
			return null;
		return new Contact(name, result);
	}

	public Contact createContact(String name, String number, ElasticSearchConnector es, String indexName, String indexType) {
		failIfInvalid(name, number, es);
		String result = null;
		try {
			result = es.myGet( indexName, indexType, name);
		}catch ( IOException e ) {
			e.printStackTrace();
		}
		if(result != null)
			throw new IllegalArgumentException("Contact with same name already exists");
		
		Contact contact = new Contact(name, number);
		try {
			es.myInsert( indexName, indexType, name, number);
		}catch ( IOException e ) {
			e.printStackTrace();
		}
		
		return contact;
	}
	
	public String deleteContact(String name, ElasticSearchConnector es, String indexName, String indexType) {
//		failIfInvalid(name);
		String result = null;
		try {
			result = es.myGet( indexName, indexType, name);
		}catch ( IOException e ) {
			e.printStackTrace();
		}
		if(result == null)
			throw new IllegalArgumentException("Contact doesnot exists");
		
		es.delete( indexName, "name", name);
		return "Deleted successfully";
	}

	public Contact updateContact(String name, String number, ElasticSearchConnector es, String indexName, String indexType) {
		Contact contact = new Contact(name, number);
		
		String result = null;
		try {
			result = es.myGet( indexName, indexType, name);
		}catch ( IOException e ) {
			e.printStackTrace();
		}
		if(result == null)
			throw new IllegalArgumentException("Contact doesnot exists");
		
		failIfInvalid(name, number, es);
		try {
			es.myUpdate( indexName, indexType, name, number);
		}catch ( IOException e ) {
			e.printStackTrace();
		}
		System.out.println("Contact updated");
		return contact;
	}

	private void failIfInvalid(String name, String number, ElasticSearchConnector es) {
		if (name == null || name.isEmpty()) {
			throw new IllegalArgumentException("Parameter name cannot be empty");
		}
		if (number == null || number.length() <= 8 || number.length() > 19) // to support number of form : +(121) 639-238 7361 or 6392387361
		{ 
			throw new IllegalArgumentException("Parameter number is not valid");
		}
	}
}
