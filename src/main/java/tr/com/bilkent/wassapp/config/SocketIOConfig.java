package tr.com.bilkent.wassapp.config;

import com.corundumstudio.socketio.Configuration;
import com.corundumstudio.socketio.SocketIOServer;
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

        return new SocketIOServer(config);
    }
}
