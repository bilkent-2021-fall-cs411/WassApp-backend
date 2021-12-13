package tr.com.bilkent.wassapp.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class MessagePageDTO {
    private Long total;
    private List<MessageDTO> messages;
}
