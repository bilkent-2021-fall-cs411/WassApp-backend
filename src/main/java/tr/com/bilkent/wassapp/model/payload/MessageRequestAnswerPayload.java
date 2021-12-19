package tr.com.bilkent.wassapp.model.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import tr.com.bilkent.wassapp.model.enums.MessageRequestAnswer;

import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MessageRequestAnswerPayload {

    @NotNull
    private String contact;

    @NotNull
    private MessageRequestAnswer answer;

}
