import com.google.gson.Gson;
import dao.Sql2oCommentDao;
import exceptions.ApiException;
import models.Comment;
import org.sql2o.Connection;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static spark.Spark.*;

public class App {
    static int getHerokuAssignedPort() {
        ProcessBuilder processBuilder = new ProcessBuilder();
        if (processBuilder.environment().get("PORT") != null) {
            return Integer.parseInt(processBuilder.environment().get("PORT"));
        }
        return 4567; //return default port if heroku-port isn't set (i.e. on localhost)
    }

    public static void main(String[] args) {
        port(getHerokuAssignedPort());
        Sql2oCommentDao commentDao = new Sql2oCommentDao(DB.sql2o);
        Connection conn = DB.sql2o.open();
        Gson gson = new Gson();


        post("/comments/new", "application/json", (req, res) -> {
            Comment comment = gson.fromJson(req.body(), Comment.class);
            commentDao.add(comment);
            res.status(400);
            res.type("application/json");
            return gson.toJson(comment);
        });

        get("/comments", "application/json", (req, res) -> { //accept a request in format JSON from an app
            res.type("application/json");
            if (commentDao.getAll().size() == 0){
                return "{\"message\":\"I'm sorry, but no comments yet added.\"}";
            } else{
                return gson.toJson(commentDao.getAll());//send it back to be displayed
            }
        });

        get("/comments/:id", "application/json", (req, res) -> {
            int commentId = Integer.parseInt(req.params("id"));
            Comment commentToFind = commentDao.findById(commentId);
            if (commentToFind == null){
                throw new ApiException(404, String.format("No user with the id: \"%s\" exists", req.params("id")));
            }
            return gson.toJson(commentToFind);
        });
        //FILTERS
        exception(ApiException.class, (exc, req, res) -> {
            ApiException err = (ApiException) exc;
            Map<String, Object> jsonMap = new HashMap<>();
            jsonMap.put("status", err.getStatusCode());
            jsonMap.put("errorMessage", err.getMessage());
            res.type("application/json"); //after does not run in case of an exception.
            res.status(err.getStatusCode()); //set the status
            res.body(gson.toJson(jsonMap));  //set the output.
        });
        after((req, res) -> {
            res.type("application/json");
        });

    }


}
