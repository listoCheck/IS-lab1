import type {ColorDTO} from "@/ts/dto/ColorDTO.ts";
import type {LocationDTO} from "@/ts/dto/LocationDTO.ts";

export interface PersonDTO {
    name: string,
    eyeColor: ColorDTO,
    hairColor: ColorDTO,
    location: LocationDTO,
    weight: number,
    passportID: string,
    nationality: string,

}