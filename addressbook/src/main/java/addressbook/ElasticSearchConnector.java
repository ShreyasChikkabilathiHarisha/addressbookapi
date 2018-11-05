package addressbook;

import java.io.IOException;
//import java.io.ObjectInputStream.GetField;
//import org.elasticsearch.index.get.GetField;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.elasticsearch.action.admin.cluster.health.ClusterHealthResponse;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsResponse;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.action.support.WriteRequest.RefreshPolicy;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.document.DocumentField;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.common.xcontent.XContentFactory.*;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.reindex.BulkByScrollResponse;
import org.elasticsearch.index.reindex.DeleteByQueryAction;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class ElasticSearchConnector {
	private TransportClient client = null;

	public ElasticSearchConnector(String clusterName, String clusterIp, int clusterPort) throws UnknownHostException {

		Settings settings = Settings.builder()
				 .put( "cluster.name", clusterName )
				 .put( "client.transport.ignore_cluster_name", true )
				 .put( "client.transport.sniff", true )
				 .build();

		// create connection
		 client = new PreBuiltTransportClient( settings );
		client.addTransportAddress(new TransportAddress(InetAddress.getByName(clusterIp), clusterPort));

		System.out.println("Connection " + clusterName + "@" + clusterIp + ":" + clusterPort + " established!");
	}

	public boolean isClusterHealthy() {

		final ClusterHealthResponse response = client.admin().cluster().prepareHealth().setWaitForGreenStatus()
				.setTimeout(TimeValue.timeValueSeconds(2)).execute().actionGet();

		if (response.isTimedOut()) {
			System.out.println("The cluster is unhealthy: " + response.getStatus());
			return false;
		}

		System.out.println("The cluster is healthy: " + response.getStatus());
		return true;
	}

	public boolean isIndexRegistered(String indexName) {
		// check if index already exists
		final IndicesExistsResponse ieResponse = client.admin().indices().prepareExists(indexName)
				.get(TimeValue.timeValueSeconds(1));

		// index not there
		if (!ieResponse.isExists()) {
			return false;
		}

		System.out.println("Index already created!");
		return true;
	}

	public boolean createIndex(String indexName, String numberOfShards, String numberOfReplicas) {
		CreateIndexResponse createIndexResponse = client.admin().indices().prepareCreate(indexName)
				.setSettings(Settings.builder()
				 .put("index.number_of_shards", numberOfShards )
				 .put("index.number_of_replicas", numberOfReplicas )
				).get();

		if (createIndexResponse.isAcknowledged()) {
			System.out.println(
					"Created Index with " + numberOfShards + " Shard(s) and " + numberOfReplicas + " Replica(s)!");
			return true;
		}

		return false;
	}
	
	public List<Contact> myGetAll(String indexName, String indexType) throws IOException {
		SearchResponse response = client.prepareSearch(indexName)
				   .setTypes(indexType)
				   .setFetchSource(new String[]{"name", "number"}, null)
				   .setSize(1000)
				   .execute()
				   .actionGet();

		List<Contact> get_all = new ArrayList<>();
		for (SearchHit hit : response.getHits())
		{
				   Map map = hit.getSourceAsMap();
				   get_all.add(new Contact((String)map.get("name"), (String)map.get("number")));
				   System.out.println("Get success: name= " + map.get("name") +", number= " + map.get("number"));
		}
		
		return get_all;
	}
	
	public List<Contact> myGetQuery(String indexName, String indexType, String query) throws IOException {
//		JsonParser parser = new JsonParser();
//		JsonObject json = (JsonObject) parser.parse(query);
//		JsonArray sourceList = json.getAsJsonArray("query").getAsJsonArray();
//		String field = sourceList.get(0).getAsString();
//		String query_exec = sourceList.get(1).getAsString();
		SearchResponse response = client.prepareSearch(indexName)
				   .setTypes(indexType)
				   .setFetchSource(new String[]{"name", "number"}, null)
//				   .setQuery(QueryBuilders.termsQuery(field, query_exec))
				   .setSize(1000)
				   .execute()
				   .actionGet();

		List<Contact> get_all = new ArrayList<>();
		for (SearchHit hit : response.getHits())
		{
				   Map map = hit.getSourceAsMap();
				   get_all.add(new Contact((String)map.get("name"), (String)map.get("number")));
				   System.out.println("Get success: name= " + map.get("name") +", number= " + map.get("number"));
		}
		
		return get_all;
	}
	
	public String myGet(String indexName, String indexType, String name_) throws IOException {
		SearchResponse response = client.prepareSearch(indexName)
				   .setTypes(indexType)
				   .setFetchSource(new String[]{"number"}, null)
				   .setQuery(QueryBuilders.termsQuery("name", name_))
				   .execute()
				   .actionGet();

				for (SearchHit hit : response.getHits()){
				   Map map = hit.getSourceAsMap();
				   System.out.println("Get success: name= " + name_ +", number= " + map.get("number"));
				   return (String) map.get("number");
				}
		
		return null;
	}
	
	public boolean myInsert(String indexName, String indexType, String name_, String number_) throws IOException {
		IndexResponse response = client.prepareIndex(indexName, indexType, name_)
		        .setSource(XContentFactory.jsonBuilder()
		                    .startObject()
		                        .field("name", name_)
		                        .field("number", number_)
		                    .endObject()
		                  )
		        .setRefreshPolicy("")
		        .get();

		return true;
	}
	
	public boolean myUpdate(String indexName, String indexType, String name_, String number_) throws IOException {
		System.out.println("Inside myUpdate!");
		UpdateRequest updateRequest = new UpdateRequest();
		updateRequest.index(indexName);
		updateRequest.type(indexType);
		updateRequest.id(name_);
		updateRequest.doc(XContentFactory.jsonBuilder()
		        .startObject()
		            .field("name", name_)
		            .field("number", number_)
		        .endObject());
		try {
			client.update(updateRequest).get();
		}catch(ExecutionException e)
		{
			System.out.println("Update failed!");
			return false;
		}catch( InterruptedException e)
		{
			System.out.println("Update failed!");
			return false;
		}

		return true;
	}

	public long delete(String indexName, String key, String value) {
		BulkByScrollResponse response = DeleteByQueryAction.INSTANCE.newRequestBuilder(client)
				.filter(QueryBuilders.matchQuery(key, value))
				.source(indexName)
				.refresh(true)
				.get();
		
		System.out.println("Deleted " + response.getDeleted() + " element(s)!");

		return response.getDeleted();
	}
}
