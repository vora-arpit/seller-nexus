import { createFeatureSelector, createSelector } from "@ngrx/store";
import { OrderItemState } from "../states/orderItem.state";
import { StateNameEnum as StateNames } from "../enums/state-names.enum";

const getorderItemState=createFeatureSelector<OrderItemState>(StateNames.ORDERITEM_STATE_NAME);

export const getAllOrderItems=createSelector(getorderItemState,(state)=>{
   return state.orderItems;
});