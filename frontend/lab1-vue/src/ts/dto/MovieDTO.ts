import type {CoordinatesDTO} from "./CoordinatesDTO.ts";
import type {MpaRating} from "../enum/MpaRating.ts";
import type {PersonDTO} from "./PersonDTO.ts";
import type {Genre} from "../enum/Genre.ts";

export interface MovieDTO {
    id: number;
    name: string;
    coordinates: CoordinatesDTO;
    oscarsCount: number;
    budget: number;
    totalBoxOffice: number;
    mpaaRating: MpaRating;
    director: PersonDTO;
    screenwriter: PersonDTO;
    operator: PersonDTO;
    length: number;
    goldenPalmCount: number;
    usaBoxOffice: number;
    tagline: string;
    genre: Genre;
}