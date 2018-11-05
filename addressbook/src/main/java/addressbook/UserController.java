package addressbook;

import static spark.Spark.*;

import com.google.gson.Gson;

import spark.ResponseTransformer;
import spark.TemplateEngine;

import static addressbook.JsonUtil.*;

public class UserController {
	public UserController(final UserService userService, ElasticSearchConnector es) {

		get("/users", (req, res) -> userService.getAllUsers(es), json());

		get("/users/:id", (req, res) -> {
			String id = req.params(":id");
			User user = userService.getUser(id, es);
			if (user != null) {
				return user;
			}
			res.status(400);
			return new ResponseError("No user with id '%s' found", id);
		}, json());

		post("/users", (req, res) -> userService.createUser(
				req.queryParams("name"),
				req.queryParams("email"),
				es
		), json());

		put("/users/:id", (req, res) -> userService.updateUser(
				req.params(":id"),
				req.queryParams("name"),
				req.queryParams("email"),
				es
		), json());

		after((req, res) -> {
			res.type("application/json");
		});
		
		delete("/users/:id", (req, res) -> userService.deleteUser(
				req.params(":id"),
				es
		), json());

		exception(IllegalArgumentException.class, (e, req, res) -> {
			res.status(400);
			res.body(toJson(new ResponseError(e)));
		});
	}
}
