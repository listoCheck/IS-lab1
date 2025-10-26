package src.islab1jee.model.importobjects;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import src.islab1jee.enums.ImportStatus;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
public class ImportOperation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private ImportStatus status;

    @Column
    private int addedCount;

    @Column
    private LocalDateTime timestamp;

    @Column(length = 2048)
    private String errorMessage;

}
