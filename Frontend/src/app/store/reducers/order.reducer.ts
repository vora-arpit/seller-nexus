import { createReducer, on } from "@ngrx/store";
import { OrderState, orderInitialState } from "../states/order.state";
import { addOrderSuccess, getOrderSuccess } from "../actions/order.action";

const _orderReducer=createReducer(
    orderInitialState,
    on(getOrderSuccess,(state,action)=>{
        console.log("reducers console",action.orders)
        return{
            ...state,
            orders:action.orders
        };
    }),
    on(addOrderSuccess,(state,action)=>{
        return{
            ...state,
            orders:[...state.orders,action.orders],
        };
    }),
)

export function orderReducer(state: any, action: any) {
    return _orderReducer(state, action);
  }