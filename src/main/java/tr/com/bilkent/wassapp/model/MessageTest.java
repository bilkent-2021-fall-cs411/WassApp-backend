package tr.com.bilkent.wassapp.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
public class MessageTest {

    @NotNull
    private String receiver;

    @NotNull
    private String message;

}
