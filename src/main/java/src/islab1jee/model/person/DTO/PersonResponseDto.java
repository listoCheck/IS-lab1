package src.islab1jee.model.person.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import src.islab1jee.enums.Color;
import src.islab1jee.enums.Country;
import src.islab1jee.model.location.DTO.LocationResponseDto;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PersonResponseDto {
    private Integer id;
    private String name;
    private Color eyeColor;
    private Color hairColor;
    private LocationResponseDto location;
    private double weight;
    private String passportID;
    private Country nationality;
}

