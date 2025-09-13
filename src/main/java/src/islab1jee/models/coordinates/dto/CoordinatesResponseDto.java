package src.islab1jee.models.coordinates.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CoordinatesResponseDto {
    // getters/setters
    private Integer id;
    private Float x;
    private long y;
}
