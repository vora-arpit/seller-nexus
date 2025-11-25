import { createFeatureSelector, createSelector } from "@ngrx/store";
import { CustomerState } from "../states/customer.state";
import { StateNameEnum as StateNames } from "../enums/state-names.enum";

const getcustomerState=createFeatureSelector<CustomerState>(StateNames.CUSTOMER_STATE_NAME);

export const getAllCustomers=createSelector(getcustomerState,(state)=>{
    return state.customers;
});