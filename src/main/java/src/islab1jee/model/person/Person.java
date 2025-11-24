package src.islab1jee.model.person;

import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.*;
import org.hibernate.annotations.Cache;
import src.islab1jee.enums.Color;
import src.islab1jee.enums.Country;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import src.islab1jee.model.location.Location;

@Setter
@Getter
@Entity
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "PersonRegion")
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
    @OneToOne
    @JoinColumn(name = "location_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
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