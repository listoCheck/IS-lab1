package src.islab1.models.classes;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Location {
    private Float x; //Поле не может быть null
    private double y;
    private long z;
    private String name; //Строка не может быть пустой, Поле не может быть null

    public Location(Float x, double y, long z, String name) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.name = name;
    }

    public Location() {
    }

    public boolean isValid() {
        return x != null &&
                name != null && !name.trim().isEmpty();
    }

    @Override
    public String toString() {
        return "Location{" +
                "x=" + x +
                ", y=" + y +
                ", z=" + z +
                ", name='" + name + '\'' +
                '}';
    }

}