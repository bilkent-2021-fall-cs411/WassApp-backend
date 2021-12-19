package tr.com.bilkent.wassapp.model.payload;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class RegisterPayload {

    @NotNull
    private String displayName;

    @NotNull
    private String email;

    @NotNull
    private String password;
}
