package tr.com.bilkent.wassapp.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tr.com.bilkent.wassapp.collection.User;
import tr.com.bilkent.wassapp.config.AuthContextHolder;
import tr.com.bilkent.wassapp.model.dto.UserDTO;
import tr.com.bilkent.wassapp.model.enums.MessageRequestAnswer;
import tr.com.bilkent.wassapp.model.payload.ContactPayload;
import tr.com.bilkent.wassapp.model.payload.MessageRequestAnswerPayload;
import tr.com.bilkent.wassapp.repository.UserRepository;
import tr.com.bilkent.wassapp.socketio.SocketIOHandler;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ContactService {

    private final UserService userService;
    private final MessageService messageService;
    private final UserRepository userRepository;
    private final SocketIOHandler socketIOHandler;

    public void sendMessageRequest(ContactPayload data) {
        String authenticatedUser = AuthContextHolder.getEmail();
        User contact = userService.getUserByEmail(data.getContact());
        if (contact.getContacts().contains(authenticatedUser)) {
            throw new RuntimeException("Already in contacts");
        }

        if (contact.getMessageRequests().add(authenticatedUser)) {
            userRepository.save(contact);
            socketIOHandler.send(data.getContact(), "messageRequest", userService.getUserDTOByEmail(authenticatedUser));
        } else {
            throw new RuntimeException("Message request already sent");
        }
    }

    public List<UserDTO> getMessageRequests() {
        String authenticatedUser = AuthContextHolder.getEmail();
        User user = userService.getUserByEmail(authenticatedUser);
        Set<String> messageRequests = user.getMessageRequests();

        return messageRequests.stream().map(userService::getUserDTOByEmail).collect(Collectors.toList());
    }

    public void answerMessageRequest(MessageRequestAnswerPayload data) {
        String authenticatedUser = AuthContextHolder.getEmail();
        User user = userService.getUserByEmail(authenticatedUser);
        User contact = userService.getUserByEmail(data.getContact());
        if (!user.getMessageRequests().contains(data.getContact())) {
            throw new RuntimeException("No such message request");
        }

        if (data.getAnswer() == MessageRequestAnswer.ACCEPT) {
            user.getContacts().add(data.getContact());
            contact.getContacts().add(authenticatedUser);
            userRepository.save(contact);
        }
        user.getMessageRequests().remove(data.getContact());
        userRepository.save(user);

        socketIOHandler.send(data.getContact(), "messageRequestAnswer", new MessageRequestAnswerPayload(authenticatedUser, data.getAnswer()));
    }

    public List<UserDTO> getContacts() {
        String authenticatedUser = AuthContextHolder.getEmail();
        User user = userService.getUserByEmail(authenticatedUser);
        Set<String> contacts = user.getContacts();

        return contacts.stream().map(userService::getUserDTOByEmail).collect(Collectors.toList());
    }

    public void deleteContact(ContactPayload data) {
        String authenticatedUser = AuthContextHolder.getEmail();
        User user = userService.getUserByEmail(authenticatedUser);
        User contact = userService.getUserByEmail(data.getContact());
        if (!user.getContacts().remove(data.getContact())) {
            throw new RuntimeException("No such contact");
        }

        contact.getContacts().remove(authenticatedUser);
        userRepository.save(user);
        userRepository.save(contact);

        messageService.deleteChatHistory(data);
        socketIOHandler.send(data.getContact(), "deleteContact", new ContactPayload(authenticatedUser));
    }
}
