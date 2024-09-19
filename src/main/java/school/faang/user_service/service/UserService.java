package school.faang.user_service.service;

import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserFilterDto;

import java.util.List;

public interface UserService {
    List<UserDto> getPremiumUsers(UserFilterDto userFilterDto);
}
