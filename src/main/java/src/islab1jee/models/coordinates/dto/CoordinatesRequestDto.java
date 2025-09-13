package src.islab1jee.models.coordinates.dto;

import jakarta.validation.constraints.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class CoordinatesRequestDto {
    // getters/setters
    @NotNull(message = "Поле x не может быть null")
        @Min(value = -335, message = "Значение x должно быть больше -335")
        private Float x;

        @Max(value = 878, message = "Максимальное значение y: 878")
        private long y;
}