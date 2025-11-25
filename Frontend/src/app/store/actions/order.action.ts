import { createAction, props } from "@ngrx/store";
import { OrderStateActionsTypes as Types} from "../enums/order.action-types.enum"
import { Order } from "../../core";

export const getOrder = createAction(
    Types.GET_ORDER// Include the filter text as a property
  );
  
  export const getOrderSuccess = createAction(
    Types.GET_ORDER_SUCCESS,
    props<{ orders: Order[] }>()
  );
  
  export const getOrderFailure = createAction(
    Types.GET_ORDER_FAILURE,
    props<{ error: any }>()
  );


export const addOrder=createAction(Types.ADD_ORDER,props<{order:Order}>()
);
export const addOrderSuccess=createAction(Types.ADD_ORDER_SUCCESS,props<{orders:Order}>()
);
export const addOrderFailure=createAction(Types.ADD_ORDER_FAILURE,props<{error:any}>()
);
