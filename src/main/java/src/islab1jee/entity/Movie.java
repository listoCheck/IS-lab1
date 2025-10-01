package src.islab1jee.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import src.islab1jee.enums.MovieGenre;
import src.islab1jee.enums.MpaaRating;

import java.time.LocalDate;

@Setter
@Getter
@Entity
@Table
public class Movie {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "movie_seq")
    @SequenceGenerator(name = "movie_seq", sequenceName = "movie_seq", allocationSize = 1)
    private Integer id;

    @NotBlank
    @Column(nullable = false)
    private String name;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Coordinates coordinates;

    @Column(nullable = false, updatable = false)
    private LocalDate creationDate = LocalDate.now();

    @Positive
    @Column(nullable = false)
    private long oscarsCount;

    @Positive
    @Column
    private Long budget; // может быть null

    @Positive
    @Column(nullable = false)
    private long totalBoxOffice;

    @Enumerated(EnumType.STRING)
    @Column
    private MpaaRating mpaaRating;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(nullable = false)
    private Person director;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn
    private Person screenwriter;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(nullable = false)
    private Person operator;

    @Positive
    @Column
    private Long length;

    @Positive
    @Column(nullable = false)
    private int goldenPalmCount;

    @Positive
    @Column(nullable = false)
    private double usaBoxOffice;

    @NotBlank
    @Column(nullable = false)
    private String tagline;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MovieGenre genre;

}
