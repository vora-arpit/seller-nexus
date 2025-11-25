import { Order } from "./order";
import { Product } from "./product";

export class OrderItem {
    id: bigint;
    price: number;
    quantity: number;
    order: Order;
    product: Product;
  }
  