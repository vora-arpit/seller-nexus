import { Product } from "../../core";

export interface ProductState{
    products:Product[];
}

export const productinitialState:ProductState={
    products:[]
};