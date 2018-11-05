# addressbookapi
This is a RESTful API for an address book with an Elasticsearch data store.

Instructions to set up ElasticSearch and Spark Java (Kibana for debugging and checking the elasticsearch):

1. Download elasticsearch from: https://www.elastic.co/downloads/elasticsearch
2. extract it -:$ tar -xvf <filename>
3. cd <filename>
4. configure the port in the config file located in: config/elasticsearch.yml
5. ./bin/elasticsearch

6. Download kibana from: https://www.elastic.co/downloads/kibana
7. extract it -:$ tar -xvf <filename>
8. cd <filename>
9. configure the port in the config file to query to elasticsearch port: config/kibana.yml
10. ./bin/kibana

11. Clone this locally: git clone <path>
12. To run the server locally, run the main java file.
13. To send the http rest API requests, i used a chrome extension "Advanced REST API".
14. When the server is running, we can send the http requests.
15. To run the test cases, i have used "addressbook" index. So, for the tests where i am creating the new Contact, if the same Junit test is run more than once, it will fail since that contact already exists after first run.
16. To run all the unit tests, run the MainTest file.

Note1: 
There is a bug in this current version- The created contact takes time to update in the elastic search. So when all the unit test file is run, in the initial run, only POST command (create contact) will be successfull whereas get, put and delete will fail since the newly created contact is not yet updated in the elasticsearch database. When we run Unit test for the second time, the POST will fail since thers is a contact with same name in the elasticsearch, but all others will succeed.
(This is happening only for Junit test cases, but when the requests are send from the browser, it works without any problem)


API Definition:

The structure of the data (data model) stored in the elasticsearch is:
{
	"name": "theActualName",
	"number": "theActualNumber"
}

The response from the server is in JSON format.

Functions and examples:

1. POST /contact
	example: POST /contact?name=vegeta&number=6312827654

2. GET /contact/{name}
	example: GET /contact/{name}

3. GET /contact
	example: GET /contact

4. PUT /contact/{name}
	example: PUT /contact/vegeta?number=7318739877

5. DELETE /contact/{name}
	example: DELETE /contact/vegeta

6. GET /contact?pageSize={}&page={}&query={}
	example: GET /contact?pageSize={4}&page={2}&query={...}

	NOTE: for the above query, i have not been able to consider the query in the search. So for now, the query will fetch all the records and paginate according to instructions passed above.
	So, the querystringquery is not functional yet.



