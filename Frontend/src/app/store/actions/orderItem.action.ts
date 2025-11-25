import { createAction, props } from "@ngrx/store";
import { OrderItemStateActionsTypes as Types } from "../enums/orderItem.action-types.enum";
import { OrderItem } from "../../core";

export const getOrderItem = createAction(Types.GET_ORDERITEM, props<{ orderId: number }>());
export const getOrderItemSuccess = createAction(Types.GET_ORDERITEM_SUCCESS, props<{ orderItem: OrderItem[] }>());
export const getOrderItemFailure = createAction(Types.GET_ORDERITEM_FAILURE, props<{ error: any }>());


export const addOrderItem=createAction(Types.ADD_ORDERITEM,props<{orderItem:any}>());
export const addOrderItemSuccess=createAction(Types.ADD_ORDERITEM_SUCCESS,props<{orderItem:OrderItem}>());
export const addOrderItemFailure=createAction(Types.ADD_ORDERITEM_FAILURE,props<{error:any}>());

export const updateOrderItem=createAction(Types.UPDATE_ORDERITEM,props<{orderItem:any}>());
export const updateOrderItemSuccess=createAction(Types.UPDATE_ORDERITEM_SUCCESS,props<{orderItem:OrderItem}>())
export const updateOrderItemFailure=createAction(Types.UPDATE_ORDERITEM_FAILURE,props<{error:any}>());

export const deleteOrderItem=createAction(Types.DELETE_ORDERITEM,props<{id:bigint}>());
export const deleteOrderItemSuccess=createAction(Types.DELETE_ORDERITEM_SUCCESS,props<{id:bigint}>());
export const deleteOrderItemFailure=createAction(Types.DELETE_ORDERITEM_FAILURE,props<{error:any}>());