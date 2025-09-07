package src.islab1.models.classes.coordinates.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class CoordinateCreate{
    @NotNull
    @Min(-355)
    private Integer x;

    @Max(878)
    private Float y;
}
