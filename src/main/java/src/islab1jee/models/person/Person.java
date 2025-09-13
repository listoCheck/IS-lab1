package src.islab1jee.models.person;

import lombok.Getter;
import lombok.Setter;
import src.islab1jee.models.location.Location;
import src.islab1jee.enums.Color;
import src.islab1jee.enums.Country;

import javax.persistence.*;
import javax.validation.constraints.*;

@Setter
@Getter
@Entity
@Table(name = "person")
public class Person {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "person_seq")
    @SequenceGenerator(name = "person_seq", sequenceName = "person_seq", allocationSize = 1)
    private Integer id;

    @NotBlank(message = "Поле name не может быть null или пустым")
    @Column(name = "name", nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "eye_color")
    private Color eyeColor; // Может быть null

    @NotNull(message = "Поле hairColor не может быть null")
    @Enumerated(EnumType.STRING)
    @Column(name = "hair_color", nullable = false)
    private Color hairColor;

    @NotNull(message = "Поле location не может быть null")
    @ManyToOne
    @JoinColumn(name = "location_id", nullable = false)
    private Location location;

    @Positive(message = "Значение weight должно быть больше 0")
    @Column(name = "weight")
    private double weight;

    @NotBlank(message = "Поле passportId не может быть null или пустым")
    @Column(name = "passport_id", nullable = false, unique = true)
    private String passportID;

    @Enumerated(EnumType.STRING)
    @Column(name = "nationality")
    private Country nationality; // Может быть null

}