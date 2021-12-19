package tr.com.bilkent.wassapp.repository;

public interface CustomizedMessageRepository {
    long markAllAsRead(String email, String otherEmail);
}
