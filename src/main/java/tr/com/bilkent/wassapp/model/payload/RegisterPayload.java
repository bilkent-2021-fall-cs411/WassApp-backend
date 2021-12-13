package tr.com.bilkent.wassapp.model.payload;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;

@Data
public class RegisterPayload {
    @Length(max = 30)
    @NotNull
    private String displayName;

    @NotNull
    private String email;

    @NotNull
    private String password;
}
