package tr.com.bilkent.wassapp.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ChatDTO {
    private UserDTO otherUser;
    private MessageDTO lastMessage;
}
