package dao;

import models.Comment;

import java.util.List;

public interface CommentDao {
    void add(Comment comment);
    List<Comment> getAll();
    Comment findById(int id);
//    void deleteById(int id);
//    void clearAll();
}
