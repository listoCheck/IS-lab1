import type {CoordinatesDTO} from "@/ts/dto/CoordinatesDTO.ts";
import type {MpaaRating} from "@/ts/interface/MpaaRaating.ts";
import type {PersonDTO} from "@/ts/dto/PersonDTO.ts";
import type {Genre} from "@/ts/interface/Genre.ts";

export interface MovieDTO {
    name: string;
    coordinates: CoordinatesDTO;
    oscarsCount: number;
    budget: number;
    totalBoxOffice: number;
    mpaaRating: MpaaRating;
    director: PersonDTO;
    screenwriter: PersonDTO;
    operator: PersonDTO;
    length: number;
    goldenPalmCount: number;
    usaBoxOffice: number;
    tagline: string;
    genre: Genre;
}