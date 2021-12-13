package tr.com.bilkent.wassapp.socketio;

import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.listener.DataListener;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import tr.com.bilkent.wassapp.model.dto.MessageDTO;
import tr.com.bilkent.wassapp.model.dto.WassAppResponse;
import tr.com.bilkent.wassapp.model.payload.GetMessagesPayload;
import tr.com.bilkent.wassapp.model.payload.SendMessagePayload;
import tr.com.bilkent.wassapp.service.MessageService;

import javax.annotation.PostConstruct;
import javax.validation.Validator;

@Component
@RequiredArgsConstructor
@Slf4j
public class SocketIOReceiver {

    private final SocketIOServer server;
    private final Validator validator;
    private final MessageService messageService;

    @PostConstruct
    public void start() {
        server.addEventListener("getChats", String.class, getChatsListener());
        server.addEventListener("message", SendMessagePayload.class, messageListener());
        server.addEventListener("getMessages", GetMessagesPayload.class, getMessagesListener());
    }

    private DataListener<String> getChatsListener() {
        return new ValidatedDataListener<>(validator) {
            @Override
            public void onValidatedData(SocketIOClient client, String data, AckRequest ackSender) {
                ackSender.sendAckData(messageService.loadChats());
            }
        };
    }

    private ValidatedDataListener<SendMessagePayload> messageListener() {
        return new ValidatedDataListener<>(validator) {
            @Override
            public void onValidatedData(SocketIOClient client, SendMessagePayload data, AckRequest ackSender) {
                MessageDTO messageDTO = messageService.sendMessage(data);
                ackSender.sendAckData(new WassAppResponse<>(messageDTO));
            }
        };
    }

    private ValidatedDataListener<GetMessagesPayload> getMessagesListener() {
        return new ValidatedDataListener<>(validator) {
            @Override
            public void onValidatedData(SocketIOClient client, GetMessagesPayload data, AckRequest ackSender) {
                ackSender.sendAckData(messageService.loadMessages(data));
            }
        };
    }

}
