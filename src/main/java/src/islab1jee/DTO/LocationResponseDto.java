package src.islab1jee.DTO;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class LocationResponseDto {
    private Integer id;
    private Float x;
    private double y;
    private long z;
    private String name;

}
