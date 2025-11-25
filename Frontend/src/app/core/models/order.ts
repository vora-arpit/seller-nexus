import { Customer } from "./customer";
import { OrderItem } from "./orderitem";
import { User } from "./user";

export interface Order {
    id: number;
    createdAt: Date;
    createdBy :User;
    customer: Customer;
    status: string;
    total: number;
    itemcount:number;
    orderItem:OrderItem;
}