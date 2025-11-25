import { OrderItem } from "../../core";

export interface OrderItemState{
    orderItems: OrderItem[];
}

export const orderItemInitialState: OrderItemState = {
    orderItems: []
}