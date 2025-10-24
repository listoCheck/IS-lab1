package src.islab1jee.model.importobjects;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import src.islab1jee.enums.ImportStatus;

import java.time.LocalDateTime;

@Setter
@Getter
@Entity
@Table
public class ImportOperation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;

    @Enumerated(EnumType.STRING)
    private ImportStatus status;

    private int addedCount;

    private LocalDateTime timestamp;

    @Lob
    private String errorMessage;

}
