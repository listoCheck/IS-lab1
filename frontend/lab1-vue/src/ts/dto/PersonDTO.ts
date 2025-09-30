import type {Color} from "../enum/Color.ts";
import type {LocationDTO} from "./LocationDTO.ts";

export interface PersonDTO {
    id: number;
    name: string,
    eyeColor: Color,
    hairColor: Color,
    location: LocationDTO,
    weight: number,
    passportID: string,
    nationality: string,

}