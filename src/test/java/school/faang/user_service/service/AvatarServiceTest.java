package school.faang.user_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;
import school.faang.user_service.entity.User;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AvatarServiceTest {

    @Mock
    private S3Service s3Service;

    @Mock
    private UtilsService utilsService;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private AvatarService avatarService;

    private User user;
    private byte[] avatarBytes;
    private byte[] smallAvatarBytes;
    private String generationUrl;
    private int smallAvatarWidth;
    private int smallAvatarHeight;
    private String extension;

    @BeforeEach
    void setUp() {
        generationUrl = "https://api.dicebear.com/9.x/style/jpeg?seed=0";
        smallAvatarWidth = 200;
        smallAvatarHeight = 200;
        extension = "jpeg";

        avatarService.setSTYLES(new String[]{"style"});
        avatarService.setGENERATION_URL_PATTERN(generationUrl);
        avatarService.setSEED_RANGE(1);
        avatarService.setEXTENSION(extension);
        avatarService.setBUCKET_NAME("bucket-name");
        avatarService.setAVATAR_ID_PATTERN("avatar_%d.jpeg");
        avatarService.setSMALL_AVATAR_ID_PATTERN("small_avatar_%d.jpeg");
        avatarService.setSMALL_FILE_WIDTH(smallAvatarWidth);
        avatarService.setSMALL_FILE_HEIGHT(smallAvatarHeight);
        avatarService.setCONTENT_TYPE("image/jpeg");


        user = User.builder()
                .id(1L)
                .build();

        avatarBytes = new byte[1000];
        smallAvatarBytes = new byte[1000];
    }

    @Test
    @DisplayName("testing updateAvatarToRandom method")
    void testUpdateAvatarToRandom() {
        when(restTemplate.getForObject(generationUrl, byte[].class)).thenReturn(avatarBytes);
        when(utilsService.resizeImage(avatarBytes, smallAvatarWidth, smallAvatarHeight, extension))
                .thenReturn(smallAvatarBytes);

        avatarService.setRandomAvatar(user);

        verify(restTemplate, times(1)).getForObject(anyString(), eq(byte[].class));
        verify(utilsService, times(1)).resizeImage(avatarBytes, smallAvatarWidth,
                smallAvatarHeight, extension);
        verify(s3Service, times(2))
                .uploadToS3(anyString(), eq(avatarBytes), anyString(), anyString());
    }
}