package tn.esprit.services;

import tn.esprit.entities.Comment;
import tn.esprit.entities.Post;
import tn.esprit.entities.Utilisateur;
import tn.esprit.utils.MyDataBase;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ServiceComment implements IService<Comment> {
    private Connection connection;
    public ServiceComment(){
        connection = MyDataBase.getInstance().getMyConnection();
    }
    @Override
    public void ajouter(Comment comment) throws SQLException {
        String query = "INSERT INTO comment (content, comment_date, user_id, post_id) VALUES (?, ?, ?, ?)";
        PreparedStatement stmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
        stmt.setString(1, comment.getContent());
        stmt.setTimestamp(2, Timestamp.valueOf(comment.getCommentDate())); // Convert LocalDateTime to Timestamp
        stmt.setInt(3, comment.getUser() != null ? comment.getUser().getId() : 0); // Assuming getUser() returns an object with getId() method
        stmt.setInt(4, comment.getPost() != null ? comment.getPost().getId() : 0); // Assuming getPost() returns an object with getId() method
        stmt.executeUpdate();
    }

    @Override
    public void modifier(Comment comment) throws SQLException {
        String query = "UPDATE comment SET content = ?, comment_date = ?, user_id = ?, post_id = ? WHERE id = ?";
        PreparedStatement stmt = connection.prepareStatement(query);
        stmt.setString(1, comment.getContent());
        stmt.setTimestamp(2, Timestamp.valueOf(comment.getCommentDate())); // Updating the comment date too (if needed)
        stmt.setInt(3, comment.getUser() != null ? comment.getUser().getId() : 0);
        stmt.setInt(4, comment.getPost() != null ? comment.getPost().getId() : 0);
        stmt.setInt(5, comment.getId());
        stmt.executeUpdate();
    }


    @Override
    public void supprimer(int id) throws SQLException {
        String req = "DELETE FROM comment WHERE id = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(req);
        preparedStatement.setInt(1, id);
        preparedStatement.executeUpdate();
    }

    @Override
    public List<Comment> afficher() throws SQLException {
        List<Comment> comments = new ArrayList<>();
        String req = "SELECT * FROM comment"; // or your correct table
        PreparedStatement preparedStatement = connection.prepareStatement(req);
        ResultSet resultSet = preparedStatement.executeQuery();
        ServiceUtilisateur serviceUtilisateur = new ServiceUtilisateur();
        while (resultSet.next()) {
            Comment comment = new Comment();
            comment.setId(resultSet.getInt("id"));
            comment.setContent(resultSet.getString("content"));
            int userId = resultSet.getInt("user_id");
            Utilisateur user = serviceUtilisateur.findById(userId);
            comment.setUser(user);

            Post post = new Post();
            post.setId(resultSet.getInt("post_id"));
            comment.setPost(post);
            comment.setCommentDate(resultSet.getTimestamp("comment_date").toLocalDateTime());


            comments.add(comment);
        }
        return comments;
    }


}
