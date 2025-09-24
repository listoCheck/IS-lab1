import type {Color} from "@/ts/interface/Color.ts";
import type {LocationDTO} from "@/ts/dto/LocationDTO.ts";

export interface PersonDTO {
    name: string,
    eyeColor: Color,
    hairColor: Color,
    location: LocationDTO,
    weight: number,
    passportID: string,
    nationality: string,

}