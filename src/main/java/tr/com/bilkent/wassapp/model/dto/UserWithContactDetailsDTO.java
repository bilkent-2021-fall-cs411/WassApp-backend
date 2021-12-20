package tr.com.bilkent.wassapp.model.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class UserWithContactDetailsDTO extends UserDTO {

    private Boolean isMessageRequestSent;
    private Boolean isInContacts;

}
