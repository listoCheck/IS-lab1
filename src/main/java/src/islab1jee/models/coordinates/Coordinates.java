package src.islab1jee.models.coordinates;


import lombok.*;


import javax.persistence.*;
import javax.validation.constraints.*;

@Setter
@Getter
@Entity
@Table(name = "coordinates")
public class Coordinates {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "coordinates_id_seq")
    @SequenceGenerator(name = "coordinates_id_seq", sequenceName = "coordinates_id_seq", allocationSize = 1)
    private Integer id;

    @NotNull(message = "Поле не может быть null")
    @Min(value = -335, message = "Значение поля должно быть больше -335")
    @Column(name = "x", nullable = false)
    private Float x; //Значение поля должно быть больше -335, Поле не может быть null

    @Max(value = 878, message = "Максимальное значение поля: 878")
    @Column(name = "y")
    private long y; //Максимальное значение поля: 878
}