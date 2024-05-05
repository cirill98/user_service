package school.faang.user_service.controller.user;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import school.faang.user_service.config.context.UserContext;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.filter.UserFilterDto;
import school.faang.user_service.service.user.UserService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UserService userService;
    private final UserContext userContext;

    @Operation(summary = "Create new user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Created new user",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserDto.class))})})
    @PostMapping("/create")
    public UserDto create(@Valid @RequestBody UserDto userDto) {
        return userService.create(userDto);
    }

    @GetMapping("/{userId}")
    @Operation(summary = "Get user by id")
    UserDto getUser(@PathVariable long userId) {
        return userService.getUserDtoById(userId);
    }

    @PostMapping("/list")
    @Operation(summary = "Get users by ids")
    List<UserDto> getUsersByIds(@RequestBody List<Long> ids) {
        return userService.getUsersDtoByIds(ids);
    }

    @DeleteMapping("/{userId}")
    public UserDto deactivationUserById(@PathVariable Long userId) {
        return userService.deactivationUserById(userId);
    }

    @PostMapping("/searchUsers")
    public List<UserDto> searchUsersByFilter(@RequestBody UserFilterDto userFilterDto) {
        Long requestUser = userContext.getUserId();
        return userService.searchUsersByFilter(userFilterDto, requestUser);
    }
}