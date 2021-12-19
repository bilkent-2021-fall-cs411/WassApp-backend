package tr.com.bilkent.wassapp.repository;

public interface CustomizedMessageRepository {

    long markAllAsRead(String email, String otherEmail);

    long deleteChatHistory(String email, String otherEmail);

}
