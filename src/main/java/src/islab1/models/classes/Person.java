package src.islab1.models.classes;

import lombok.Getter;
import lombok.Setter;
import src.islab1.models.enums.Color;
import src.islab1.models.enums.Country;

@Setter
@Getter
public class Person {
    private String name; //Поле не может быть null, Строка не может быть пустой
    private Color eyeColor; //Поле может быть null
    private Color hairColor; //Поле не может быть null
    private Location location; //Поле не может быть null
    private double weight; //Значение поля должно быть больше 0
    private String passportID; //Поле не может быть null
    private Country nationality; //Поле может быть null

    public Person(String name, Color eyeColor, Color hairColor, Location location, double weight, String passportID, Country nationality) {
        this.name = name;
        this.eyeColor = eyeColor;
        this.hairColor = hairColor;
        this.location = location;
        this.weight = weight;
        this.passportID = passportID;
        this.nationality = nationality;
    }

    public Person() {
    }

    public boolean isValid() {
        return name != null && !name.trim().isEmpty() &&
                hairColor != null &&
                location != null &&
                weight > 0 &&
                passportID != null && !passportID.trim().isEmpty();
    }

    @Override
    public String toString() {
        return "Person{" +
                "name='" + name + '\'' +
                ", eyeColor=" + eyeColor +
                ", hairColor=" + hairColor +
                ", weight=" + weight +
                ", passportID='" + passportID + '\'' +
                ", nationality=" + nationality +
                '}';
    }
}