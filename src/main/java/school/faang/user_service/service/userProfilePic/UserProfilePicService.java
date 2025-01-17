package school.faang.user_service.service.userProfilePic;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.dto.userProfile.UserProfileDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserProfilePic;
import school.faang.user_service.event.profilepic.ProfilePicEvent;
import school.faang.user_service.exception.ExceptionMessages;
import school.faang.user_service.mapper.userProfilePic.ProfilePicEventMapper;
import school.faang.user_service.mapper.userProfilePic.UserProfilePicMapper;
import school.faang.user_service.messaging.publisher.profilepic.ProfilePicEventPublisher;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.avatars.RandomAvatarService;
import school.faang.user_service.util.multipart.MultipartFileCopyUtil;
import school.faang.user_service.service.s3.S3Service;

import java.io.IOException;
import java.io.InputStream;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserProfilePicService {
    private static final int MAX_IMAGE_LARGE_PHOTO = 1080;
    private static final int MAX_IMAGE_SMALL_PHOTO = 170;

    private final S3Service s3Service;
    private final UserRepository userRepository;
    private final RandomAvatarService randomAvatarService;
    private final UserProfilePicMapper userProfilePicMapper;
    private final MultipartFileCopyUtil multipartFileCopyUtil;
    private final ProfilePicEventPublisher profilePicEventPublisher;
    private final ProfilePicEventMapper profilePicEventMapper;

    public UserProfileDto addImageInProfile(Long userId, MultipartFile multipartFile) throws IOException {
        User user = checkTheUserInTheDatabase(userId);

        if (multipartFile == null || multipartFile.isEmpty()) {
            randomAvatarService.generateAndStoreAvatar(user);
            return userProfilePicMapper.toDto(user);
        }

        String folder = user.getId() + user.getUsername();
        MultipartFile multipartFileUtilLarge = multipartFileCopyUtil
                .compressionMultipartFile(multipartFile, MAX_IMAGE_LARGE_PHOTO);
        MultipartFile multipartFileUtilSmall = multipartFileCopyUtil
                .compressionMultipartFile(multipartFile, MAX_IMAGE_SMALL_PHOTO);

        UserProfilePic userProfilePic = new UserProfilePic();
        userProfilePic.setFileId(s3Service.uploadFile(multipartFileUtilLarge, folder));
        userProfilePic.setSmallFileId(s3Service.uploadFile(multipartFileUtilSmall, folder));

        user.setUserProfilePic(userProfilePic);
        userRepository.save(user);

        ProfilePicEvent profilePicEvent = profilePicEventMapper.toProfilePicEvent(user);
        profilePicEventPublisher.publish(profilePicEvent);

        return userProfilePicMapper.toDto(user);
    }

    public InputStream getImageFromProfile(Long userId) {
        User user = checkTheUserInTheDatabase(userId);

        return s3Service.downloadingByteImage(user.getUserProfilePic().getFileId());
    }

    public UserProfileDto deleteImageFromProfile(Long userId) {
        User user = checkTheUserInTheDatabase(userId);

        s3Service.deleteImage(user.getUserProfilePic().getFileId());
        s3Service.deleteImage(user.getUserProfilePic().getSmallFileId());
        user.setUserProfilePic(null);
        userRepository.save(user);

        return userProfilePicMapper.toDto(user);
    }

    private User checkTheUserInTheDatabase(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> {
            String errorMessage = String.format(ExceptionMessages.USER_NOT_FOUND, userId);
            log.error(errorMessage);
            return new EntityNotFoundException(errorMessage);
        });
    }
}