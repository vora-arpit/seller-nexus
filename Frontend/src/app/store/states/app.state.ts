import { ActionReducerMap } from "@ngrx/store";
import { StateNameEnum as StateNames } from "../enums/state-names.enum"
import { ProductState } from "./products.state";
import { productReducer } from "../reducers/products.reducer";
import { ProductEffects } from "../effects/products.effect";
import { CustomerState } from "./customer.state";
import { customerReducer } from "../reducers/customer.reducer";
import { CustomerEffects } from "../effects/customer.effect";
import { OrderState } from "./order.state";
import { OrderItemState } from "./orderItem.state";
import { orderReducer } from "../reducers/order.reducer";
import { orderItemReducer } from "../reducers/orderItem.reducer";
import { OrderEffects } from "../effects/order.effects";
import { OrderItemEffects } from "../effects/orderItem.effects";


export interface AppState{
    [StateNames.PRODUCT_STATE_NAME]:ProductState;
    [StateNames.CUSTOMER_STATE_NAME]:CustomerState;
    [StateNames.ORDER_STATE_NAME]:OrderState;
    [StateNames.ORDERITEM_STATE_NAME]:OrderItemState;

}

export const allReducers: ActionReducerMap<AppState> ={
    [StateNames.PRODUCT_STATE_NAME]:productReducer,
    [StateNames.CUSTOMER_STATE_NAME]:customerReducer,
    [StateNames.ORDER_STATE_NAME]:orderReducer,
    [StateNames.ORDERITEM_STATE_NAME]:orderItemReducer
};

export const allEffects=[
    ProductEffects,CustomerEffects,OrderEffects,OrderItemEffects
];


