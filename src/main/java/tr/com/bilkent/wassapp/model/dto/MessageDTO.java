package tr.com.bilkent.wassapp.model.dto;

import lombok.Data;
import tr.com.bilkent.wassapp.collection.enums.MessageStatus;

import java.time.OffsetDateTime;

@Data
public class MessageDTO {
    private String id;
    private String body;
    private String sender;
    private String receiver;
    private OffsetDateTime sendDate;
    private MessageStatus status;
}
