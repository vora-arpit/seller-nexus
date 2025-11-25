import { createReducer, on } from "@ngrx/store";
import { productinitialState } from "../states/products.state";
import {addProductsSuccess, deleteProductsSuccess, getProductsSuccess, updateProductsSuccess,} from '../actions/products.action';

const _productReducer=createReducer(
    productinitialState,
    on(getProductsSuccess,(state,action)=>{
        return{
            ...state,
            products:action.products
        };
    }),

    on(addProductsSuccess,(state,action)=>{
        return{
            ...state,
            products:[...state.products,action.product],
        };
    }),
    on(updateProductsSuccess,(state,action)=>{
        const allProducts=[...state.products];
        const index=allProducts.findIndex(item=>item.id===action.product.id);
        allProducts[index]={...action.product};
        return{
            ...state,
            products:allProducts,
        };
    }),

    on(deleteProductsSuccess,(state,action)=>{
        const allProducts=[...state.products];
        const index=allProducts.findIndex(item=>item.id===action.id);
        allProducts.splice(index,1);
        return{
            ...state,
            products:allProducts,
        };

    })
)

export function productReducer(state:any,action:any){
    return _productReducer(state,action);
}