package addressbook;



import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;

import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.support.ActiveShardCount;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

public class UserService {
	private Map<String, User> users = new HashMap<>();

	public List<User> getAllUsers(ElasticSearchConnector es) {
//		System.out.println("check");
		return new ArrayList<>(users.values());
	}

	public User getUser(String id, ElasticSearchConnector es) {
		return users.get(id);
	}

	public User createUser(String name, String email, ElasticSearchConnector es) {
//		System.out.println("check1");
		failIfInvalid(name, email, es);
		User user = new User(name, email);
		users.put(user.getId(), user);
		return user;
	}
	
	public String deleteUser(String id, ElasticSearchConnector es) {
//		System.out.println("check2");
//		failIfInvalid(id);
//		User user = new User(name);
		users.remove(id);
		return "Success";
	}

	public User updateUser(String id, String name, String email, ElasticSearchConnector es) {
		User user = users.get(id);
		if (user == null) {
			throw new IllegalArgumentException("No user with id '" + id + "' found");
		}
		failIfInvalid(name, email, es);
		user.setName(name);
		user.setEmail(email);
		return user;
	}

	private void failIfInvalid(String name, String email, ElasticSearchConnector es) {
		if (name == null || name.isEmpty()) {
			throw new IllegalArgumentException("Parameter 'name' cannot be empty");
		}
		if (email == null || email.isEmpty()) {
			throw new IllegalArgumentException("Parameter 'email' cannot be empty");
		}
	}
}
