package src.islab1.models.classes;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Coordinates {
    private Float x; //Значение поля должно быть больше -335, Поле не может быть null
    private long y; //Максимальное значение поля: 878

    public Coordinates(Float x, long y) {
        this.x = x;
        this.y = y;
    }

    public Coordinates() {
    }

    public boolean isValid() {
        return x >= -335;
    }
}