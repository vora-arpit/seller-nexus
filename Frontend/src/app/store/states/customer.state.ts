import { Customer } from "../../core";

export interface CustomerState {
    customers: Customer[]; // Change this to match the expected payload type
}

export const customerInitialState: CustomerState = {
    customers: []
}
