package school.faang.user_service.dto;

import lombok.Data;

import java.util.List;

@Data
public class MentorDto {
    private Long id;
    private String name;
    private List<Long> menteesIds;
}