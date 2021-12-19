package tr.com.bilkent.wassapp.model.payload;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.OffsetDateTime;


@Data
public class GetMessagesPayload {

    @NotNull
    private String contact;
    private OffsetDateTime beforeDate = OffsetDateTime.now();
    private int count = 100;
}
