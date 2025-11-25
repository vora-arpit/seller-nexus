import { createReducer, on } from "@ngrx/store";
import { OrderItemState, orderItemInitialState } from "../states/orderItem.state";
import { addOrderItemSuccess, deleteOrderItemSuccess, getOrderItemSuccess, updateOrderItemSuccess } from "../actions/orderItem.action";

const _orderItemReducer=createReducer(
    orderItemInitialState,
    on(getOrderItemSuccess, (state, action) => {
        // console.dir(action.orderItem, {depth : 10})
        return {
          ...state,
          orderItem: action.orderItem
        };
      }),
      
    on(addOrderItemSuccess,(state,action)=>{
        return{
            ...state,
            orderItems:[...state.orderItems,action.orderItem],
        };
    }),
    on(updateOrderItemSuccess,(state,action)=>{
        const allOrderItems=[...state.orderItems];
        const index=allOrderItems.findIndex(item=>item.id===action.orderItem.id);
        allOrderItems[index]=action.orderItem;
        return{
            ...state,
            orderItems:allOrderItems,
        };
    }),
    on(deleteOrderItemSuccess,(state,action)=>{
        const allOrderItems=[...state.orderItems];
        const index=allOrderItems.findIndex(item=>item.id===action.id);
        allOrderItems.splice(index,1);
        return{
            ...state,
            orderItems:allOrderItems,
        };
    })
)

export function orderItemReducer(state:any,action:any):OrderItemState{
    return _orderItemReducer(state,action);
}