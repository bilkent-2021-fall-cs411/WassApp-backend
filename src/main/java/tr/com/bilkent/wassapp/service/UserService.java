package tr.com.bilkent.wassapp.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import tr.com.bilkent.wassapp.collection.User;
import tr.com.bilkent.wassapp.model.dto.UserDTO;
import tr.com.bilkent.wassapp.model.payload.RegisterPayload;
import tr.com.bilkent.wassapp.repository.UserRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;

    public User getUserByEmail(String email) {
        Optional<User> userOptional = userRepository.findByEmail(email);
        return userOptional.orElseThrow(() -> new UsernameNotFoundException("No such user"));
    }

    public UserDTO getUserDTOByEmail(String email) {
        User user = getUserByEmail(email);
        return modelMapper.map(user, UserDTO.class);
    }

    public void register(RegisterPayload registerPayload) {
        if (userRepository.findByEmail(registerPayload.getEmail()).isPresent()) {
            throw new RuntimeException("User already exists");
        }
        User user = new User();
        user.setDisplayName(registerPayload.getDisplayName());
        user.setEmail(registerPayload.getEmail());
        user.setPassword(passwordEncoder.encode(registerPayload.getPassword()));

        userRepository.save(user);
    }
}
