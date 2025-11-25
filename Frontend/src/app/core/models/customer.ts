import { User } from './user';

export class Customer {
  id!: bigint;
  createdAt!: Date;
  birthdate!: Date;
  createdBy!: User;
  name!: string;
  description!: string;
  gender!: string;
  phone!: number;
  email!: string;
  address!: string;
  city!: string;
  state!: string;
  total?: number;
}
