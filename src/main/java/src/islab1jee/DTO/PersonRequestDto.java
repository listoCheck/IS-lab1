package src.islab1jee.DTO;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import src.islab1jee.enums.Color;
import src.islab1jee.enums.Country;


@Setter
@Getter
public class PersonRequestDto {
    @NotBlank
    private String name;

    private Color eyeColor;

    @NotNull
    private Color hairColor;

    @NotNull
    private Integer locationId;

    @Positive
    private double weight;

    @NotBlank
    private String passportID;

    private Country nationality;

}
