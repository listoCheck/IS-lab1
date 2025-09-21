package src.islab1jee.DTO;


import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;


@Setter
@Getter
public class LocationRequestDto {
    @NotNull(message = "Поле x не может быть null")
    private Float x;

    private double y;

    private long z;

    @NotBlank(message = "Поле name не может быть пустым")
    private String name;

}
