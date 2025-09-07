package src.islab1.models.classes.person;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnTransformer;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import src.islab1.models.classes.location.Location;
import src.islab1.models.enums.Color;
import src.islab1.models.enums.Country;
import jakarta.validation.constraints.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@Entity
@Table(name = "person")
public class Person {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "person_seq")
    @SequenceGenerator(name = "person_seq", sequenceName = "person_seq", allocationSize = 1)
    private Integer id;

    @NotBlank(message = "Поле не может быть null, Строка не может быть пустой")
    @Column(name = "name", nullable = false)
    private String name; //Поле не может быть null, Строка не может быть пустой

    @Enumerated(EnumType.STRING)
    @ColumnTransformer(write = "?::color")
    @Column(name = "eye_color", nullable = false)
    private Color eyeColor; // Поле может быть null

    @NotNull(message = "Поле не может быть null")
    @Enumerated(EnumType.STRING)
    @ColumnTransformer(write = "?::color")
    @Column(name = "hairColor", nullable = false)
    private Color hairColor; //Поле не может быть null

    @NotNull(message = "Поле не может быть null")
    @ManyToOne
    @JoinColumn(name = "location_id", nullable = false)
    private Location location; //Поле не может быть null

    @Positive(message = "Значение поля должно быть больше 0")
    @Column(name = "weight")
    private double weight; //Значение поля должно быть больше 0

    @NotBlank(message = "Поле не может быть null")
    @Column(name = "passportId", nullable = false, unique = true)
    private String passportID; //Поле не может быть null

    @Enumerated(EnumType.STRING)
    @ColumnTransformer(write = "?::country")
    @Column(name = "nationality", nullable = false)
    private Country nationality; // Поле может быть null

}