package tn.esprit.services;


import tn.esprit.entities.Post;
import tn.esprit.utils.MyDataBase;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


public class ServicePost implements IService<Post> {
    private Connection connection;

    public ServicePost(){
        connection = MyDataBase.getInstance().getMyConnection();
    }
    @Override
    public void ajouter(Post post) throws SQLException {
        String sql = "INSERT INTO post (`title`, `content`, `image_url`) VALUES (?, ?, ?)";
        PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

        stmt.setString(1, post.getTitle());
        stmt.setString(2, post.getContent());
        stmt.setString(3, post.getImageUrl());

        stmt.executeUpdate();

    }
    @Override
    public void modifier(Post post) throws SQLException {

    }
    @Override
    public void supprimer(int id) throws SQLException {

    }
    @Override
    public List<Post> afficher() throws SQLException {
        List<Post> posts = new ArrayList<>();
        String sql = "SELECT * FROM post";

        Statement stmt = connection.createStatement();
        ResultSet rs = stmt.executeQuery(sql);

        while (rs.next()) {
            int id = rs.getInt("id");
            String title = rs.getString("title");
            String content = rs.getString("content");
            String imageUrl = rs.getString("image_url");
            LocalDateTime publishedAt = rs.getTimestamp("published_at").toLocalDateTime();
            int views = rs.getInt("views");

            Post post = new Post(id, title, content, imageUrl, publishedAt, views);
            posts.add(post);
        }

        return posts;
    }
}
