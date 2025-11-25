import { createAction, props } from "@ngrx/store";
import { ProductStateActionsTypes as Types } from "../enums/products.action-types.enum";
import { Product } from "../../core";

export const getProducts=createAction(Types.GET_PRODUCTS);
export const getProductsSuccess=createAction(
    Types.GET_PRODUCTS_SUCCESS,props<{products:Product[]}>()
);
export const getProductsFailure=createAction(
    Types.GET_PRODUCTS_FAILURE,props<{ error:any}>()
);

export const addProducts=createAction(
    Types.ADD_PRODUCTS,props<{product:Product}>()
);
export const addProductsSuccess=createAction(
    Types.ADD_PRODUCTS_SUCCESS,props<{product:Product}>()
);
export const addProductsFailure=createAction(
    Types.ADD_PRODUCTS_FAILURE,props<{ error:any}>()
);

export const updateProducts=createAction(
    Types.UPDATE_PRODUCTS,props<{id: string;product:Product}>()
);
export const updateProductsSuccess=createAction(
    Types.UPDATE_PRODUCTS_SUCCESS,props<{product:Product}>()
);
export const updateProductsFailure=createAction(
    Types.UPDATE_PRODUCTS_FAILURE,props<{ error:any}>()
);      

export const deleteProducts=createAction(
    Types.DELETE_PRODUCTS,props<{id:bigint}>()
);  
export const deleteProductsSuccess=createAction(
    Types.DELETE_PRODUCTS_SUCCESS,props<{id:bigint}>()
);
export const deleteProductsFailure=createAction(
    Types.DELETE_PRODUCTS_FAILURE,props<{ error:any}>()
);