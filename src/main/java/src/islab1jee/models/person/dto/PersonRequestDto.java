package src.islab1jee.models.person.dto;

import lombok.Getter;
import lombok.Setter;
import src.islab1jee.enums.Color;
import src.islab1jee.enums.Country;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

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
