package tr.com.bilkent.wassapp.socketio;

import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.listener.ConnectListener;
import com.corundumstudio.socketio.listener.DataListener;
import com.corundumstudio.socketio.listener.DisconnectListener;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import tr.com.bilkent.wassapp.model.MessageTest;

import javax.annotation.PostConstruct;
import javax.validation.Validator;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Component
@RequiredArgsConstructor
@Slf4j
public class SocketIOHandler {

    private final SocketIOServer server;
    private final Validator validator;

    private ConcurrentMap<String, Set<UUID>> clients;

    @PostConstruct
    public void start() {
        clients = new ConcurrentHashMap<>();

        server.addConnectListener(connectListener());
        server.addDisconnectListener(disconnectListener());

        server.addEventListener("getChats", String.class, getChatsListener());
        server.addEventListener("message", MessageTest.class, messageListener());

        server.start();
    }

    private DataListener<String> getChatsListener() {
        return new ValidatedDataListener<>(validator) {
            @Override
            public void onValidatedData(SocketIOClient client, String data, AckRequest ackSender) {
                String email = getClientEmail(client);
                // TODO: Get chats here
                List<String> chats = List.of("Chats of " + email);
                ackSender.sendAckData(chats);
            }
        };
    }

    private ValidatedDataListener<MessageTest> messageListener() {
        return new ValidatedDataListener<>(validator) {
            @Override
            public void onValidatedData(SocketIOClient client, MessageTest data, AckRequest ackSender) {
                String email = getClientEmail(client);
                // TODO: Store msg in db

                Collection<UUID> receiverSessions = clients.get(data.getReceiver());
                if (!CollectionUtils.isEmpty(receiverSessions)) {
                    receiverSessions.forEach(uuid -> server.getClient(uuid)
                            .sendEvent("message", "[" + email + "]: " + data.getMessage()));
                }
            }
        };
    }

    private ConnectListener connectListener() {
        return client -> {
            String email = getClientEmail(client);
            if (!clients.containsKey(email)) {
                clients.put(email, Collections.synchronizedSet(new HashSet<>()));
            }
            clients.get(getClientEmail(client)).add(client.getSessionId());
        };
    }

    private DisconnectListener disconnectListener() {
        return client -> {
            String email = getClientEmail(client);
            clients.get(email).remove(client.getSessionId());
            if (clients.get(email).isEmpty()) {
                clients.remove(email);
            }
        };
    }

    private String getClientEmail(SocketIOClient client) {
        return client.getHandshakeData().getSingleUrlParam("email");
    }

}
