import { Order } from "../../core";

export interface OrderState{
    orders:Order[];
}

export const orderInitialState:OrderState={
    orders:[]
}