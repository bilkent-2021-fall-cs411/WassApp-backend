package tr.com.bilkent.wassapp.config;

import com.corundumstudio.socketio.Configuration;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.protocol.JacksonJsonSupport;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import tr.com.bilkent.wassapp.collection.User;
import tr.com.bilkent.wassapp.service.UserService;

@org.springframework.context.annotation.Configuration
@RequiredArgsConstructor
@Slf4j
public class SocketIOConfig {

    @Value("${socketio.port}")
    private int port;

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @Bean
    public SocketIOServer socketIOServer() {
        Configuration config = new Configuration();
        config.setPort(port);
        config.setAuthorizationListener(data -> {
            String email = data.getSingleUrlParam("email");
            String password = data.getSingleUrlParam("password");

            try {
                User user = userService.getUserByEmail(email);
                return passwordEncoder.matches(password, user.getPassword());
            } catch (UsernameNotFoundException e) {
                return false;
            }
        });
        config.setJsonSupport(new JacksonJsonSupport(new JavaTimeModule()) {
            @Override
            protected void init(ObjectMapper objectMapper) {
                super.init(objectMapper);
                super.objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
            }
        });
        config.setRandomSession(true);

        return new SocketIOServer(config);
    }
}
