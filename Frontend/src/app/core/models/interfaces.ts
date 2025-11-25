
export interface Account {
    monthlySalesGoal: any,
    quarterlySalesGoal: any,
    monthlyCustomersGoal: any,
    quarterlyCustomersGoal: any,
    monthlySalesCurrent: any,
    quarterlySalesCurrent: any,
    monthlyCustomersCurrent: any,
    quarterlyCustomersCurrent: number;
  }
  
  export interface ApiResponse {
    message: string;
    success: boolean;
  }