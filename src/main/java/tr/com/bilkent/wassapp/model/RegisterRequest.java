package tr.com.bilkent.wassapp.model;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;

@Data
public class RegisterRequest {
    @Length(max = 30)
    @NotNull
    private String displayName;

    @NotNull
    private String email;

    @NotNull
    private String password;
}
