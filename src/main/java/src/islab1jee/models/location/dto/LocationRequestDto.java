package src.islab1jee.models.location.dto;


import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Setter
@Getter
public class LocationRequestDto {
    // getters/setters
    @NotNull(message = "Поле x не может быть null")
    private Float x;

    private double y;

    private long z;

    @NotBlank(message = "Поле name не может быть пустым")
    private String name;

}
