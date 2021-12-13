package tr.com.bilkent.wassapp.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tr.com.bilkent.wassapp.model.dto.WassAppResponse;
import tr.com.bilkent.wassapp.model.payload.RegisterPayload;
import tr.com.bilkent.wassapp.service.UserService;

import javax.validation.Valid;

@RestController
@RequestMapping
@RequiredArgsConstructor
@Slf4j
public class RegisterController {

    private final UserService userService;

    @PostMapping("/register")
    public WassAppResponse<String> register(@Valid @RequestBody RegisterPayload registerPayload) {
        userService.register(registerPayload);
        return new WassAppResponse<>("Ok");
    }
}
