package src.islab1jee.models.person.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import src.islab1jee.enums.Color;
import src.islab1jee.enums.Country;
import src.islab1jee.models.location.dto.LocationResponseDto;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PersonResponseDto {
    // getters/setters
    private Integer id;
    private String name;
    private Color eyeColor;
    private Color hairColor;
    private double weight;
    private String passportID;
    private Country nationality;
    private LocationResponseDto location;


}

