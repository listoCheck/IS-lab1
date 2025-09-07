package src.islab1.models.classes.movie;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import src.islab1.models.classes.coordinates.Coordinates;
import src.islab1.models.classes.person.Person;
import src.islab1.models.enums.MovieGenre;
import src.islab1.models.enums.MpaaRating;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "movies")
public class Movie {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "movie_seq")
    @SequenceGenerator(name = "movie_seq", sequenceName = "movie_seq", allocationSize = 1)
    @Column(name = "id")
    private Integer id;

    @NotBlank
    @Column(name = "name", nullable = false)
    private String name;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "coordinates_id", nullable = false)
    private Coordinates coordinates;

    @Column(name = "creation_date", nullable = false, updatable = false)
    private LocalDate creationDate = LocalDate.now();

    @Positive
    @Column(name = "oscars_count", nullable = false)
    private long oscarsCount;

    @Positive
    @Column(name = "budget")
    private Long budget; // может быть null

    @Positive
    @Column(name = "total_box_office", nullable = false)
    private long totalBoxOffice;

    @Enumerated(EnumType.STRING)
    @Column(name = "mpaa_rating")
    private MpaaRating mpaaRating;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "director_id", nullable = false)
    private Person director;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "screenwriter_id")
    private Person screenwriter;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "operator_id", nullable = false)
    private Person operator;

    @Positive
    @Column(name = "length")
    private Long length;

    @Positive
    @Column(name = "golden_palm_count", nullable = false)
    private int goldenPalmCount;

    @Positive
    @Column(name = "usa_box_office", nullable = false)
    private double usaBoxOffice;

    @NotBlank
    @Column(name = "tagline", nullable = false)
    private String tagline;

    @Enumerated(EnumType.STRING)
    @Column(name = "genre", nullable = false)
    private MovieGenre genre;
}
