package school.faang.user_service.controller.user;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.controller.ApiPath;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.service.csv.CsvUserService;
import school.faang.user_service.service.user.DeactivateUserFacade;
import school.faang.user_service.service.user.UserService;

/**
 * Контроллер отвечающий за обработку запросов пользователя для управления пользователями.
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(ApiPath.USERS)
public class UserController {

    private final DeactivateUserFacade deactivateUserFacade;
    private final CsvUserService csvUserService;
    private final UserService userService;

    @GetMapping("/user/{userId}/deactivate")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User deactivation success"),
            @ApiResponse(responseCode = "404", description = "User deactivation failed")
    })
    public ResponseEntity<UserDto> deactivateUser(@PathVariable("userId") long userId) {
        return ResponseEntity.status(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON)
                .body(deactivateUserFacade.deactivateUser(userId));
    }

    @GetMapping("/{id}")
    public UserDto getUser(@PathVariable long id) {
        return userService.getUser(id);
    }

    @PutMapping("/{id}")
    void putChatIdInUser(@PathVariable long id, @RequestParam("chatId") long chatId){
        userService.putChatIdInUser(id, chatId);
    }

    @PostMapping(value = "/parseUser")
    public void getStudentsParsing(@RequestBody MultipartFile multipartFile) {
        if (multipartFile.isEmpty()) {
            log.error("multipartFile isEmpty");
            throw new IllegalArgumentException("multipartFile isEmpty");
        } else {
            csvUserService.getStudentsParsing(multipartFile);
        }
    }
}