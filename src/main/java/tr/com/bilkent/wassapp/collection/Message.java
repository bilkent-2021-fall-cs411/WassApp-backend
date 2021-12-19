package tr.com.bilkent.wassapp.collection;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import tr.com.bilkent.wassapp.collection.enums.MessageStatus;

import java.time.OffsetDateTime;

@Slf4j
@Data
@Document("message")
public class Message {
    @Id
    private String id;

    private String body;
    private String sender;
    private String receiver;

    @CreatedDate
    private OffsetDateTime sendDate;
    private MessageStatus status;

}
