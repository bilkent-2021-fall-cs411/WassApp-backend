package tr.com.bilkent.wassapp.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import tr.com.bilkent.wassapp.collection.Message;
import tr.com.bilkent.wassapp.collection.enums.MessageStatus;
import tr.com.bilkent.wassapp.config.AuthContextHolder;
import tr.com.bilkent.wassapp.model.dto.ChatDTO;
import tr.com.bilkent.wassapp.model.dto.MessageDTO;
import tr.com.bilkent.wassapp.model.dto.MessagePageDTO;
import tr.com.bilkent.wassapp.model.dto.UserDTO;
import tr.com.bilkent.wassapp.model.payload.ContactPayload;
import tr.com.bilkent.wassapp.model.payload.GetMessagesPayload;
import tr.com.bilkent.wassapp.model.payload.MessageIdPayload;
import tr.com.bilkent.wassapp.model.payload.SendMessagePayload;
import tr.com.bilkent.wassapp.repository.MessageRepository;
import tr.com.bilkent.wassapp.socketio.SocketIOHandler;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class MessageService {
    private final MessageRepository messageRepository;
    private final ModelMapper modelMapper;
    private final SocketIOHandler socketIOHandler;
    private final UserService userService;

    public List<ChatDTO> loadChats() {
        String authenticatedUser = AuthContextHolder.getEmail();
        List<Message> chatLastMessages = messageRepository.findChatsOfUser(authenticatedUser).getMappedResults();

        List<MessageDTO> lastMessages = modelMapper.map(chatLastMessages, new TypeToken<List<MessageDTO>>() {}.getType());
        return lastMessages.stream().map(message -> {
            String otherUserEmail = authenticatedUser.equals(message.getSender()) ? message.getReceiver() : message.getSender();
            UserDTO otherUser = userService.getUserDTOByEmail(otherUserEmail);
            long unreadMessages = messageRepository.countBySenderAndReceiverAndStatusIn(otherUserEmail, authenticatedUser, List.of(MessageStatus.SENT, MessageStatus.DELIVERED));
            return new ChatDTO(otherUser, message, unreadMessages);
        }).collect(Collectors.toList());
    }

    public MessageDTO sendMessage(SendMessagePayload sendMessagePayload) {
        if (!userService.checkContact(sendMessagePayload.getReceiver())) {
            throw new RuntimeException("Contact not found");
        }

        Message message = modelMapper.map(sendMessagePayload, Message.class);
        message.setSender(AuthContextHolder.getEmail());
        message.setStatus(MessageStatus.SENT);
        message = messageRepository.save(message);

        MessageDTO messageDTO = modelMapper.map(message, MessageDTO.class);
        boolean isDelivered = socketIOHandler.send(sendMessagePayload.getReceiver(), "message", messageDTO);
        if (isDelivered) {
            message.setStatus(MessageStatus.DELIVERED);
            messageDTO.setStatus(MessageStatus.DELIVERED);
            messageRepository.save(message);
        }
        return messageDTO;
    }

    public MessagePageDTO loadMessages(GetMessagesPayload data) {
        if (!userService.checkContact(data.getContact())) {
            throw new RuntimeException("Contact not found");
        }

        String authenticatedUser = AuthContextHolder.getEmail();
        Page<Message> messagePage = messageRepository.getChatMessages(
                authenticatedUser, data.getContact(), data.getBeforeDate(), PageRequest.ofSize(data.getCount()));
        messageRepository.markAllAsRead(authenticatedUser, data.getContact());

        List<MessageDTO> messages = modelMapper.map(messagePage.getContent(), new TypeToken<>() {}.getType());
        return new MessagePageDTO(messagePage.getTotalElements(), messages);
    }

    public void deleteChatHistory(ContactPayload data) {
        if (!userService.checkContact(data.getContact())) {
            throw new RuntimeException("Contact not found");
        }

        String authenticatedUser = AuthContextHolder.getEmail();
        messageRepository.deleteChatHistory(authenticatedUser, data.getContact());

        socketIOHandler.send(data.getContact(), "deleteChatHistory", new ContactPayload(authenticatedUser));
    }

    public void deleteMessage(MessageIdPayload data) {
        String authenticatedUser = AuthContextHolder.getEmail();
        Message message = messageRepository.findById(data.getMessageId())
                .orElseThrow(() -> new RuntimeException("Message not found"));
        if (!message.getSender().equals(authenticatedUser)) {
            throw new RuntimeException("You are not allowed to delete this message");
        }
        messageRepository.deleteById(data.getMessageId());

        MessageDTO messageDTO = modelMapper.map(message, MessageDTO.class);
        socketIOHandler.send(message.getReceiver(), "deleteMessage", messageDTO);
    }

}
