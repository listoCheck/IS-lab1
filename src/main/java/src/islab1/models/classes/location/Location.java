package src.islab1.models.classes.location;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
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