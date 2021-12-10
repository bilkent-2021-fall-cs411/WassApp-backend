package tr.com.bilkent.wassapp.socketio;

import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.listener.DataListener;
import lombok.AllArgsConstructor;
import org.springframework.util.CollectionUtils;
import tr.com.bilkent.wassapp.model.WassAppResponse;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@AllArgsConstructor
public abstract class ValidatedDataListener<T> implements DataListener<T> {

    private final Validator validator;

    @Override
    public void onData(SocketIOClient client, T data, AckRequest ackSender) {
        Set<ConstraintViolation<T>> errors = validator.validate(data);
        if (CollectionUtils.isEmpty(errors)) {
            try {
                onValidatedData(client, data, ackSender);
            } catch (Exception e) {
                ackSender.sendAckData(new WassAppResponse<>(400, e.getMessage()));
            }
            return;
        }

        Map<String, String> errorMessages = new HashMap<>();
        errors.forEach(error -> errorMessages.put(error.getPropertyPath().toString(), error.getMessage()));
        ackSender.sendAckData(errorMessages);
    }

    public abstract void onValidatedData(SocketIOClient client, T data, AckRequest ackSender);

}
