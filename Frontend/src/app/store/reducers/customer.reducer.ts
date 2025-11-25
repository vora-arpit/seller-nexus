import { createReducer ,on} from "@ngrx/store";
import { CustomerState, customerInitialState } from "../states/customer.state";
import { addCustomerSuccess, deleteCustomerSuccess, getCustomerSuccess, updateCustomerSuccess } from "../actions/customer.action";

const _customerReducer = createReducer(
    customerInitialState,
    on(getCustomerSuccess, (state, action) => {
        console.dir(action.customers, {depth : 10})
        return {
            ...state,
            customers: action.customers // Update the state with action.customers directly
        };
    }),
    on(addCustomerSuccess,(state,action)=>{
        return{
            ...state,
            customers:[...state.customers,action.customer],
        };
    }),
    on(updateCustomerSuccess,(state,action)=>{
        const allCustomers=[...state.customers];
        const index=allCustomers.findIndex(item=>item.id===action.customer.id);
        allCustomers[index]={...action.customer};
        return{
            ...state,
            customers: [...allCustomers],
        };
    }),
    on(deleteCustomerSuccess,(state,action)=>{
        const allCustomers=[...state.customers];
        const index=allCustomers.findIndex(item=>item.id===action.id);

        allCustomers.splice(index,1);
        return{
            ...state,
            customers:allCustomers,
        };

    })

)

export function customerReducer(state: any, action: any): CustomerState {
    return _customerReducer(state, action);
}