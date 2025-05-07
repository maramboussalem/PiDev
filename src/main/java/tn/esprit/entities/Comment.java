package tn.esprit.entities;

import java.time.LocalDateTime;

public class Comment {

    private int id;
    private String content;
    private LocalDateTime commentDate;
    private Utilisateur user;
    private Post post;
    private String audioPath;

    public Comment() {
        this.commentDate = LocalDateTime.now();
    }

    public Comment(String content, Utilisateur user, Post post) {
        this.content = content;
        this.user = user;
        this.post = post;
        this.commentDate = LocalDateTime.now();
    }

    public Comment(int id, String content, LocalDateTime commentDate, Utilisateur user, Post post) {
        this.id = id;
        this.content = content;
        this.commentDate = commentDate;
        this.user = user;
        this.post = post;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDateTime getCommentDate() {
        return commentDate;
    }

    public void setCommentDate(LocalDateTime commentDate) {
        this.commentDate = commentDate;
    }

    public Utilisateur getUser() {
        return user;
    }

    public void setUser(Utilisateur user) {
        this.user = user;
    }

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }
    public String getAudioPath() {
        return audioPath;
    }

    public void setAudioPath(String audioPath) {
        this.audioPath = audioPath;
    }


    // toString
    @Override
    public String toString() {
        return "Comment{" +
                "id=" + id +
                ", content='" + content + '\'' +
                ", commentDate=" + commentDate +
                ", user=" + (user != null ? user.getId() : "null") +
                ", post=" + (post != null ? post.getId() : "null") +
                '}';
    }
}