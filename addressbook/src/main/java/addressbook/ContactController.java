package addressbook;

import static addressbook.JsonUtil.json;
import static addressbook.JsonUtil.toJson;
import static spark.Spark.after;
import static spark.Spark.delete;
import static spark.Spark.exception;
import static spark.Spark.get;
import static spark.Spark.post;
import static spark.Spark.put;

public class ContactController {
	public ContactController(final ContactService contactService, ElasticSearchConnector es, String indexName, String indexType) {

		get("/contact", (req, res) -> contactService.getOptions(
				req.queryParams("pageSize"),
				req.queryParams("page"),
				req.queryParams("query"),
				es,
				indexName,
				indexType
				), json());
		
		get("/contact", (req, res) -> contactService.getAllContacts(es, indexName, indexType), json());

		get("/contact/:id", (req, res) -> {
			String name = req.params(":id");
			Contact contact = contactService.getContact(name, es, indexName, indexType);
			if (contact != null) {
				return contact;
			}
			res.status(400);
			return new ResponseError("No user with name '%s' found", name);
		}, json());

		post("/contact", (req, res) -> contactService.createContact(
				req.queryParams("name"),
				req.queryParams("number"),
				es,
				indexName,
				indexType
		), json());

		put("/contact/:id", (req, res) -> contactService.updateContact(
				req.params(":id"),
				req.queryParams("number"),
				es,
				indexName,
				indexType
		), json());

		after((req, res) -> {
			res.type("application/json");
		});
		
		delete("/contact/:id", (req, res) -> contactService.deleteContact(
				req.params(":id"),
				es,
				indexName,
				indexType
		), json());

		exception(IllegalArgumentException.class, (e, req, res) -> {
			res.status(400);
			res.body(toJson(new ResponseError(e)));
		});
	}
}
