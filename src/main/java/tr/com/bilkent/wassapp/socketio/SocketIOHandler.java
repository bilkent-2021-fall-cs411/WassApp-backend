package tr.com.bilkent.wassapp.socketio;

import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.listener.ConnectListener;
import com.corundumstudio.socketio.listener.DisconnectListener;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Component
@RequiredArgsConstructor
@Slf4j
public class SocketIOHandler {

    private final SocketIOServer server;

    private ConcurrentMap<String, Set<UUID>> clients;

    @PostConstruct
    public void start() {
        clients = new ConcurrentHashMap<>();

        server.addConnectListener(connectListener());
        server.addDisconnectListener(disconnectListener());

        server.start();
    }

    public boolean send(String receiver, String eventName, Object data) {
        Collection<UUID> receiverSessions = clients.get(receiver);
        if (CollectionUtils.isEmpty(receiverSessions)) {
            return false;
        }
        receiverSessions.forEach(uuid -> server.getClient(uuid).sendEvent(eventName, data));
        return true;
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
