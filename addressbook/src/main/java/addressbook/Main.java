package addressbook;

import static spark.Spark.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.UnknownHostException;

import static addressbook.UserService.*;

public class Main {
    public static void main(String[] args) throws UnknownHostException {
//        get("/hello", (req, res) -> "Hello World!");
    	ElasticSearchConnector es = null;
		String numberOfShards  	= "1";
		String numberOfReplicas	= "2";
		String clusterName 		= "elasticsearch";
		String indexName 		= "addressbook31";
		String indexType 		= "_doc";
		String ip 				= "localhost";
		int    port				= 9300;
		try {
		
			es = new ElasticSearchConnector( clusterName, ip, port );
			
			// check if elastic search cluster is healthy
//			es.isClusterHealthy();
			
			// check if index already existing
			if( !es.isIndexRegistered( indexName ) ) {
				// create index if not already existing
				es.createIndex( indexName, numberOfShards, numberOfReplicas );
				// manually insert some test data			
//				es.bulkInsert( indexName, indexType );
				// manually insert data one by one
//				es.myInsert( indexName, indexType, "ron", "76391472484");
//				es.myInsert( indexName, indexType, "amul", "342245569");
			}
			
//			es.delete( indexName, "name", "Peter Pan" );
		}
//		catch ( FileNotFoundException e ) {
//			e.printStackTrace();
//		}
		catch ( UnknownHostException e ) {
			e.printStackTrace();
		}
		catch ( IOException e ) {
			e.printStackTrace();
		} 
    	new UserController(new UserService(), es);
    	new ContactController(new ContactService(), es, indexName, indexType);
    }
}