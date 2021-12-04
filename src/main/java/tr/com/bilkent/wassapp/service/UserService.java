package tr.com.bilkent.wassapp.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tr.com.bilkent.wassapp.collection.User;
import tr.com.bilkent.wassapp.repository.UserRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;

    public User getUser(String email) {
        return userRepository.findByEmail(email);
    }

}
