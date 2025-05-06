package tn.esprit.services;

import tn.esprit.entities.Message;
import tn.esprit.utils.MyDataBase;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ConversationService {

    private Connection connection;

    public ConversationService() {
        this.connection = MyDataBase.getInstance().getMyConnection(); // Use MyDataBase instead of MyConnection
        if (connection == null) {
            System.err.println("Erreur: Connexion à la base de données échouée dans ConversationService !");
        }
    }

    // Save a message to the database
    public void saveMessage(Message message) throws SQLException {
        String query = "INSERT INTO conversation_messages (conversation_id, sender, message) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, message.getConversationId());
            stmt.setString(2, message.getSender());
            stmt.setString(3, message.getMessage());
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erreur SQL lors de l'ajout du message: " + e.getMessage());
            throw e;
        }
    }

    // Retrieve all messages for a given conversation_id
    public List<Message> getMessagesByConversationId(int conversationId) throws SQLException {
        List<Message> messages = new ArrayList<>();
        String query = "SELECT * FROM conversation_messages WHERE conversation_id = ? ORDER BY timestamp ASC";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, conversationId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Message message = new Message();
                message.setId(rs.getInt("id"));
                message.setConversationId(rs.getInt("conversation_id"));
                message.setSender(rs.getString("sender"));
                message.setMessage(rs.getString("message"));
                message.setTimestamp(rs.getTimestamp("timestamp"));
                messages.add(message);
            }
        } catch (SQLException e) {
            System.err.println("Erreur SQL lors de la récupération des messages: " + e.getMessage());
            throw e;
        }
        return messages;
    }
}