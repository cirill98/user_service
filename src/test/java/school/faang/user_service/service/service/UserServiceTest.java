package school.faang.user_service.service.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserProfilePic;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.s3.FileDownloadException;
import school.faang.user_service.exception.s3.FileUploadException;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.randomAvatar.RandomAvatarService;
import school.faang.user_service.service.s3Service.S3Service;
import school.faang.user_service.service.userService.UserService;
import school.faang.user_service.validation.user.UserValidator;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private S3Service s3Service;

    @Mock
    private RandomAvatarService randomAvatarService;

    @Mock
    private UserValidator userValidator;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private User user;
    private File randomPhoto;

    private UserProfilePic userProfilePic;

    @BeforeEach
    public void setUp() {
        user = new User();
        user.setId(1L);
        user.setUsername("name");

        randomPhoto = new File("randomPhoto.svg");

        userProfilePic = new UserProfilePic();
        userProfilePic.setFileId("avatarId");

        user.setUserProfilePic(userProfilePic);
    }

    @Test
    public void generateRandomAvatarUserNotCurrent() {
        when(userValidator.isCurrentUser(anyLong())).thenReturn(false);

        Assertions.assertThrows(DataValidationException.class, () -> userService.generateRandomAvatar(1L));
    }

    @Test
    public void generateRandomAvatarUserNotFound() {
        when(userValidator.isCurrentUser(anyLong())).thenReturn(true);
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(DataValidationException.class, () -> userService.generateRandomAvatar(1L));
    }

    @Test
    public void generateRandomAvatarFileUploadFailed() {
        when(userValidator.isCurrentUser(anyLong())).thenReturn(true);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(randomAvatarService.getRandomPhoto()).thenReturn(randomPhoto);
        when(s3Service.uploadFile(any(File.class), any(String.class))).thenReturn(null);

        assertThrows(FileUploadException.class, () -> userService.generateRandomAvatar(1L));
    }

    @Test
    public void generateRandomAvatarSuccess() {
        when(userValidator.isCurrentUser(anyLong())).thenReturn(true);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(randomAvatarService.getRandomPhoto()).thenReturn(randomPhoto);
        when(s3Service.uploadFile(any(File.class), any(String.class))).thenReturn("avatarId");

        String avatarId = userService.generateRandomAvatar(1L);

        assertEquals("avatarId", avatarId);
    }

    @Test
    public void getUserRandomAvatarUserNotFound() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(DataValidationException.class, () -> userService.getUserRandomAvatar(1L));
    }

    @Test
    public void getUserRandomAvatarAvatarIdNull() {
        userProfilePic.setFileId(null);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));

        assertThrows(FileDownloadException.class, () -> userService.getUserRandomAvatar(1L));
    }

    @Test
    public void getUserRandomAvatarSuccess() {
        byte[] avatarBytes = new byte[]{1, 2, 3, 4};
        InputStream avatarStream = new ByteArrayInputStream(avatarBytes);

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(s3Service.getFile("avatarId")).thenReturn(avatarStream);

        byte[] result = userService.getUserRandomAvatar(1L);

        assertArrayEquals(avatarBytes, result);
    }
}