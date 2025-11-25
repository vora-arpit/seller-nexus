import { createFeatureSelector, createSelector } from "@ngrx/store";
import { OrderState } from "../states/order.state";
import { StateNameEnum as StateNames} from "../enums/state-names.enum";

const getorderState = createFeatureSelector<OrderState>(StateNames.CUSTOMER_STATE_NAME);

export const getAllOrders = createSelector(
  getorderState,
  (state) => {
    console.log("Orders in selector:", state.orders); // Logging the orders array
    return state.orders
  });
 