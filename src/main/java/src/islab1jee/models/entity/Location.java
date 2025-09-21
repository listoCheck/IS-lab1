package src.islab1jee.models.entity;


import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;




@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "location")
public class Location {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "location_seq")
    @SequenceGenerator(name = "location_seq", sequenceName = "location_seq", allocationSize = 1)
    private Integer id;

    @NotNull
    @Column(name = "x", nullable = false)
    private Float x; // Поле не может быть null

    @Column(name = "y")
    private double y;

    @Column(name = "z")
    private long z;

    @NotBlank
    @Column(name = "name", nullable = false)
    private String name; // Строка не может быть пустой, Поле не может быть null
}