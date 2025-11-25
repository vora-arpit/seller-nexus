import { Described } from "./described";

export interface Position extends Described {
    readonly id?: string;
    permissions?: string[];
  }