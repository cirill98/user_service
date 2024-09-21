package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.UserRegistrationDto;
import school.faang.user_service.service.UserLifeCycleService;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user")
public class UserController {

    private final UserContext userContext;
    private final UserLifeCycleService userLifeCycleService;

    @PutMapping("/deactivate")
    public void deactivateUser() {
        long id = userContext.getUserId();
        userLifeCycleService.deactivateUser(id);
    }

    @PostMapping
    public void registrationUser(@RequestBody @Validated UserRegistrationDto userRegistrationDto) {
        log.info("Register user: {}", userRegistrationDto);
        userLifeCycleService.registrationUser(userRegistrationDto);
        log.info("User registration successful");
    }
}
