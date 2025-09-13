package src.islab1jee.models.location.dto;


import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class LocationResponseDto {
    // getters/setters
    private Integer id;
    private Float x;
    private double y;
    private long z;
    private String name;

    public LocationResponseDto() {}

    public LocationResponseDto(Integer id, Float x, double y, long z, String name) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.z = z;
        this.name = name;
    }

}
