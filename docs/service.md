Описание модели классов с se.ifmo
```mermaid
---
config:
  layout: dagre
---
classDiagram
    class Country {
        RUSSIA
        UNITED_KINGDOM
        VATICAN
        ITALY
    }
    class Location {
        private Float x
        private double y
        private long z
        private String name
    }
    class Person {
    private String name
    private Color eyeColor
    private Color hairColor
    private Location location
    private double weight
    private String passportID
    private Country nationality
    }
    class CoordinatesDTO {
        private Float x
        private long y
    }
    class Movie {
        private Integer id
        private String name
        private CoordinatesDTO coordinates
        private java.time.LocalDate creationDate
        private long oscarsCount
        private Long budget
        private long totalBoxOffice
        private MpaaRating mpaaRating
        private Person director
        private Person screenwriter
        private Person operator
        private Long length
        private int goldenPalmCount
        private double usaBoxOffice
        private String tagline
        private MovieGenre genre
    }
    class Color {
    GREEN
    BLACK
    YELLOW
    BROWN
    }
    class MovieGenre {
    WESTERN
    COMEDY
    MUSICAL
    ADVENTURE
    FANTASY
    }
    class MpaaRating {
    G
    PG
    PG_13
    R
    NC_17
    }
    Movie <|-- CoordinatesDTO
    Movie <|-- MovieGenre
    Movie <|-- Person
    Movie <|-- MpaaRating
    Person <|-- Location
    Person <|-- Country
    Person <|-- Color




```
//todo: удаление объекта при удалении дочернего
//todo: сделать синхронизацию на разных клиентах