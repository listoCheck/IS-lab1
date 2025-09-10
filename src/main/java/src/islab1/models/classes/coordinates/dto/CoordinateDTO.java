package src.islab1.models.classes.coordinates.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class CoordinateDTO {
    private int id;
    private Integer x;
    private Float y;
}