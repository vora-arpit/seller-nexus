import { createFeatureSelector, createSelector } from "@ngrx/store";
import { ProductState } from "../states/products.state";
import { StateNameEnum as StateNames } from "../enums/state-names.enum";

const getProductState= createFeatureSelector<ProductState>(StateNames.PRODUCT_STATE_NAME);

export const getAllProducts= createSelector(getProductState,(state)=>{
    return state.products
});