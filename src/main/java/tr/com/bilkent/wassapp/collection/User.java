package tr.com.bilkent.wassapp.collection;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Slf4j
@Data
@Document("user")
public class User {

    @Id
    private String email;
    private String displayName;
    private String password;
}
