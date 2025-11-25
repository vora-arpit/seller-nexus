    import { createAction,props } from "@ngrx/store";
    import { CustomerStateActionsTypes as Types } from "../enums/customer.action-types.enum";
    import { Customer } from "../../core";


    export const getCustomer=createAction(Types.GET_CUSTOMER);
    export const getCustomerSuccess=createAction(
        Types.GET_CUSTOMER_SUCCESS,props<{customers:Customer[]}>()
    );
    export const getCustomerFailure=createAction(
        Types.GET_CUSTOMER_FAILURE,props<{error:any}>()
    );

    export const addCustomer=createAction(
        Types.ADD_CUSTOMER,props<{customer:Customer}>()
    );
    export const addCustomerSuccess=createAction(
        Types.ADD_CUSTOMER_SUCCESS,props<{customer:Customer}>()
    );
    export const addCustomerFailure=createAction(
        Types.ADD_CUSTOMER_FAILURE,props<{error:any}>()
    );

    export const updateCustomer=createAction(
        Types.UPDATE_CUSTOMER,props<{id:string,customer:Customer}>()
    );
    export const updateCustomerSuccess=createAction(
        Types.UPDATE_CUSTOMER_SUCCESS,props<{customer:Customer}>()
    );
    export const updateCustomerFailure=createAction(
        Types.UPDATE_CUSTOMER_FAILURE,props<{error:any}>()
    );

    export const deleteCustomer=createAction(
        Types.DELETE_CUSTOMER,props<{id:bigint}>()
    );  
    export const deleteCustomerSuccess=createAction(
        Types.DELETE_CUSTOMER_SUCCESS,props<{id:bigint}>()
    );
    export const deleteCustomerFailure=createAction(
        Types.DELETE_CUSTOMER_FAILURE,props<{error:any}>()
    );