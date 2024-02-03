package dao;

import models.Comment;
import org.sql2o.Connection;
import org.sql2o.Sql2o;
import org.sql2o.Sql2oException;

import java.util.List;

public class Sql2oCommentDao implements CommentDao {
    private final Sql2o sql2o;
    public Sql2oCommentDao(Sql2o sql2o) {
        this.sql2o=sql2o;
    }
    @Override
    public void add(Comment comment) {
        String sql = "INSERT INTO comments (comment) VALUES (:comment)";
        try (Connection con = sql2o.open()) {
            int id = (int) con.createQuery(sql, true)
                    .bind(comment)
                    .executeUpdate()
                    .getKey();
            comment.setId(id);
        } catch (Sql2oException ex) {
            System.out.println(ex);
        }
    }

    @Override
    public List<Comment> getAll() {
        String sql = "SELECT * FROM comments";
        try(Connection con = sql2o.open()) {
            return con.createQuery(sql).executeAndFetch(Comment.class);
        }
    }

    @Override
    public Comment findById(int id) {
        try (Connection con = sql2o.open()) {
            return con.createQuery("SELECT * FROM comments WHERE id = :id")
                    .addParameter("id", id)
                    .executeAndFetchFirst(Comment.class);
        }
    }
//
//    @Override
//    public void deleteById(int id) {
//        String sql = "DELETE from comments WHERE id = :id";
//        try (Connection con = sql2o.open()) {
//            con.createQuery(sql)
//                    .addParameter("id", id)
//                    .executeUpdate();
//        } catch (Sql2oException ex){
//            System.out.println(ex);
//        }
//    }
//
//    @Override
//    public void clearAll() {
//        String sql = "DELETE from comments";
//        try (Connection con = sql2o.open()) {
//            con.createQuery(sql).executeUpdate();
//        } catch (Sql2oException ex) {
//            System.out.println(ex);
//        }
//    }
}
