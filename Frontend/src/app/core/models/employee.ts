import { Address } from "./address";
import { Described } from "./described";
import { Position } from "./position";

export interface Employee extends Described {
    readonly id?: number;
    email: string;
    positionName?: string;
    position?: Position;
    readonly organizationId?: number;
    authorities?: string[];
    password?: string;
    enabled?: boolean;
    phone?: string;
    address?: Address;
    imgSrc?: string;
  }
  