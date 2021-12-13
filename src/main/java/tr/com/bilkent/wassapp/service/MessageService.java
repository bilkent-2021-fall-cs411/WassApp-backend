package tr.com.bilkent.wassapp.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import tr.com.bilkent.wassapp.collection.Message;
import tr.com.bilkent.wassapp.collection.enums.Status;
import tr.com.bilkent.wassapp.config.AuthContextHolder;
import tr.com.bilkent.wassapp.model.dto.MessageDTO;
import tr.com.bilkent.wassapp.model.dto.MessagePageDTO;
import tr.com.bilkent.wassapp.model.payload.GetMessagesPayload;
import tr.com.bilkent.wassapp.model.payload.SendMessagePayload;
import tr.com.bilkent.wassapp.repository.MessageRepository;
import tr.com.bilkent.wassapp.socketio.SocketIOHandler;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class MessageService {
    private final MessageRepository messageRepository;
    private final ModelMapper modelMapper;
    private final SocketIOHandler socketIOHandler;

    public MessagePageDTO loadChats() {
        String authenticatedUser = AuthContextHolder.getEmail();
        List<Message> chats = messageRepository.findChatsOfUser(authenticatedUser).getMappedResults();

        List<MessageDTO> messages = modelMapper.map(chats, new TypeToken<>() {
        }.getType());
        return new MessagePageDTO((long) messages.size(), messages);
    }

    public MessageDTO sendMessage(SendMessagePayload sendMessagePayload) {
        Message message = modelMapper.map(sendMessagePayload, Message.class);
        message.setSender(AuthContextHolder.getEmail());
        message.setStatus(Status.SENT);
        message = messageRepository.save(message);

        MessageDTO messageDTO = modelMapper.map(message, MessageDTO.class);
        socketIOHandler.send(sendMessagePayload.getReceiver(), "message", messageDTO);
        return messageDTO;
    }

    public MessagePageDTO loadMessages(GetMessagesPayload data) {
        String authenticatedUser = AuthContextHolder.getEmail();
        Page<Message> messagePage = messageRepository.getChatMessages(
                authenticatedUser, data.getChat(), data.getBeforeDate(), PageRequest.ofSize(data.getCount()));

        List<MessageDTO> messages = modelMapper.map(messagePage.getContent(), new TypeToken<>() {
        }.getType());
        return new MessagePageDTO(messagePage.getTotalElements(), messages);
    }
}
