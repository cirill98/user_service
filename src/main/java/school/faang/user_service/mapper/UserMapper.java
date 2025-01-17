package school.faang.user_service.mapper;

import static school.faang.user_service.constants.Constants.EMPTY_VALUE;
import static school.faang.user_service.constants.Constants.PATTERN_OF_DATE;
import static school.faang.user_service.constants.Constants.PREMIUM_STATUS_ACTION;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import org.mapstruct.AfterMapping;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.userSubscriptionDto.UserSubscriptionDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.premium.Premium;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, builder = @Builder(disableBuilder = true))
public abstract class UserMapper {

  public abstract List<UserDto> toDtoList(List<User> users);

  public abstract UserSubscriptionDto toDto(User user);

  public abstract User toEntity(UserSubscriptionDto userDto);

  @Mapping(target = "premium", ignore = true)
  @Mapping(source = "contactPreference.preference", target = "preference")
  public abstract UserDto toUserDto(User user);

  @Mapping(source = "preference", target = "contactPreference.preference")
  @Mapping(target = "premium", ignore = true)
  public abstract User toEntity(UserDto userDto);

  @AfterMapping
  protected void updateFields(User user, @MappingTarget UserDto target) {
    target.setPremium(checkPremium(user));
  }

  /**
   * Метод для проверки наличия активного Премиум статуса.
   *
   * @param user пользователь.
   * @return результат об активном премиум статусе и его сроке действия при его наличии.
   */
  private String checkPremium(User user) {
    return Optional.ofNullable(user.getPremium())
        .map(Premium::getEndDate)
        .filter(endDate -> endDate.isAfter(LocalDateTime.now()))
        .map(date -> String.format(PREMIUM_STATUS_ACTION,
            date.format(DateTimeFormatter.ofPattern(PATTERN_OF_DATE))))
        .orElse(EMPTY_VALUE);
  }
}