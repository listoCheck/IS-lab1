package src.islab1.models.classes.coordinates.dto;

import lombok.Data;
import jakarta.validation.constraints.*;
import org.openapitools.jackson.nullable.JsonNullable;

@Data
public class CoordinateUpdate {
    @NotNull
    @Min(-355)
    private JsonNullable<Integer> x;

    @Max(878)
    private JsonNullable<Float> y;
}