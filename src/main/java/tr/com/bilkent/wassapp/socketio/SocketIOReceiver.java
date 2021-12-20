package tr.com.bilkent.wassapp.socketio;

import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.listener.DataListener;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import tr.com.bilkent.wassapp.config.AuthContextHolder;
import tr.com.bilkent.wassapp.model.dto.WassAppResponse;
import tr.com.bilkent.wassapp.model.payload.*;
import tr.com.bilkent.wassapp.service.ContactService;
import tr.com.bilkent.wassapp.service.MessageService;
import tr.com.bilkent.wassapp.service.UserService;

import javax.annotation.PostConstruct;
import javax.validation.Validator;

@Component
@RequiredArgsConstructor
@Slf4j
public class SocketIOReceiver {

    private final SocketIOServer server;
    private final Validator validator;
    private final MessageService messageService;
    private final UserService userService;
    private final ContactService contactService;

    @PostConstruct
    public void start() {
        server.addEventListener("whoAmI", String.class, whoAmIListener());
        server.addEventListener("searchUsers", ContactPayload.class, searchUsersListener());
        server.addEventListener("sendMessageRequest", ContactPayload.class, sendMessageRequestListener());
        server.addEventListener("getMessageRequests", String.class, getMessageRequestsListener());
        server.addEventListener("answerMessageRequest", MessageRequestAnswerPayload.class, answerMessageRequestListener());
        server.addEventListener("getContacts", String.class, getContactsListener());
        server.addEventListener("getChats", String.class, getChatsListener());
        server.addEventListener("message", SendMessagePayload.class, messageListener());
        server.addEventListener("readMessage", MessageIdPayload.class, readMessageListener());
        server.addEventListener("getMessages", GetMessagesPayload.class, getMessagesListener());
        server.addEventListener("deleteMessage", MessageIdPayload.class, deleteMessageListener());
        server.addEventListener("deleteChatHistory", ContactPayload.class, deleteChatHistoryListener());
        server.addEventListener("deleteContact", ContactPayload.class, deleteContactListener());
    }

    private DataListener<String> whoAmIListener() {
        return new ValidatedDataListener<>(validator) {
            @Override
            public void onValidatedData(SocketIOClient client, String data, AckRequest ackSender) {
                String authenticatedUser = AuthContextHolder.getEmail();
                ackSender.sendAckData(new WassAppResponse<>(userService.getUserDTOByEmail(authenticatedUser)));
            }
        };
    }

    private ValidatedDataListener<ContactPayload> searchUsersListener() {
        return new ValidatedDataListener<>(validator) {
            @Override
            public void onValidatedData(SocketIOClient client, ContactPayload data, AckRequest ackSender) {
                ackSender.sendAckData(new WassAppResponse<>(userService.search(data)));
            }
        };
    }

    private ValidatedDataListener<ContactPayload> sendMessageRequestListener() {
        return new ValidatedDataListener<>(validator) {
            @Override
            public void onValidatedData(SocketIOClient client, ContactPayload data, AckRequest ackSender) {
                contactService.sendMessageRequest(data);
                ackSender.sendAckData(new WassAppResponse<>("OK"));
            }
        };
    }

    private ValidatedDataListener<MessageRequestAnswerPayload> answerMessageRequestListener() {
        return new ValidatedDataListener<>(validator) {
            @Override
            public void onValidatedData(SocketIOClient client, MessageRequestAnswerPayload data, AckRequest ackSender) {
                contactService.answerMessageRequest(data);
                ackSender.sendAckData(new WassAppResponse<>("OK"));
            }
        };
    }

    private DataListener<String> getMessageRequestsListener() {
        return new ValidatedDataListener<>(validator) {
            @Override
            public void onValidatedData(SocketIOClient client, String data, AckRequest ackSender) {
                ackSender.sendAckData(new WassAppResponse<>(contactService.getMessageRequests()));
            }
        };
    }

    private DataListener<String> getContactsListener() {
        return new ValidatedDataListener<>(validator) {
            @Override
            public void onValidatedData(SocketIOClient client, String data, AckRequest ackSender) {
                ackSender.sendAckData(new WassAppResponse<>(contactService.getContacts()));
            }
        };
    }

    private DataListener<String> getChatsListener() {
        return new ValidatedDataListener<>(validator) {
            @Override
            public void onValidatedData(SocketIOClient client, String data, AckRequest ackSender) {
                ackSender.sendAckData(new WassAppResponse<>(messageService.loadChats()));
            }
        };
    }

    private ValidatedDataListener<SendMessagePayload> messageListener() {
        return new ValidatedDataListener<>(validator) {
            @Override
            public void onValidatedData(SocketIOClient client, SendMessagePayload data, AckRequest ackSender) {
                ackSender.sendAckData(new WassAppResponse<>(messageService.sendMessage(data)));
            }
        };
    }

    private ValidatedDataListener<MessageIdPayload> readMessageListener() {
        return new ValidatedDataListener<>(validator) {
            @Override
            public void onValidatedData(SocketIOClient client, MessageIdPayload data, AckRequest ackSender) {
                messageService.readMessage(data);
                ackSender.sendAckData(new WassAppResponse<>("OK"));
            }
        };
    }

    private ValidatedDataListener<GetMessagesPayload> getMessagesListener() {
        return new ValidatedDataListener<>(validator) {
            @Override
            public void onValidatedData(SocketIOClient client, GetMessagesPayload data, AckRequest ackSender) {
                ackSender.sendAckData(new WassAppResponse<>(messageService.loadMessages(data)));
            }
        };
    }

    private ValidatedDataListener<MessageIdPayload> deleteMessageListener() {
        return new ValidatedDataListener<>(validator) {
            @Override
            public void onValidatedData(SocketIOClient client, MessageIdPayload data, AckRequest ackSender) {
                messageService.deleteMessage(data);
                ackSender.sendAckData(new WassAppResponse<>("OK"));
            }
        };
    }

    private ValidatedDataListener<ContactPayload> deleteChatHistoryListener() {
        return new ValidatedDataListener<>(validator) {
            @Override
            public void onValidatedData(SocketIOClient client, ContactPayload data, AckRequest ackSender) {
                messageService.deleteChatHistory(data);
                ackSender.sendAckData(new WassAppResponse<>("OK"));
            }
        };
    }

    private ValidatedDataListener<ContactPayload> deleteContactListener() {
        return new ValidatedDataListener<>(validator) {
            @Override
            public void onValidatedData(SocketIOClient client, ContactPayload data, AckRequest ackSender) {
                contactService.deleteContact(data);
                ackSender.sendAckData(new WassAppResponse<>("OK"));
            }
        };
    }

}
