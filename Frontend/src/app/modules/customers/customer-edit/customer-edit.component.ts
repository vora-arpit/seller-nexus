
import { ChangeDetectionStrategy, Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Customer, NotificationService } from '../../../core';
import { Store } from '@ngrx/store';
import { addCustomer, deleteCustomer, getCustomer, updateCustomer } from '../../../store/actions/customer.action';
import { ActivatedRoute, Router } from '@angular/router';
import { format } from 'date-fns';

@Component({
  selector: 'app-customer-edit',
  templateUrl: './customer-edit.component.html',
  styleUrls: ['./customer-edit.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class CustomerEditComponent implements OnInit {

  @Input() public customer: Customer = new Customer(); // Initialize customer property with a new instance of Customer
  // @Input() public error: string = ''; // Assign an initial value to error property
  // customer:Customer[]=[];
  @Output() public submitted: EventEmitter<Customer> = new EventEmitter();
  // @Output() public canceled: EventEmitter<string> = new EventEmitter();
  // @Output() public deleted: EventEmitter<string> = new EventEmitter();

  customerId:string |null= null;
  public angForm: FormGroup = this.fb.group({}); // Initialize angForm property

  public states = [
    { name: 'Gujarat', code: 'GJ' },
    { name: 'Mahrastra', code: 'MH' },
    { name: 'Andhra Pradesh', code: 'AP' },
    { name: 'Rajasthan', code: 'RJ' },
    { name: 'Utarpradesh', code: 'UP' },
    { name: 'Panjab', code: 'PB' }
    // Other state objects...
  ];

  public genders = [
    { name: 'Male', code: 'M' },
    { name: 'Female', code: 'F' }
  ];

  constructor(
    private fb: FormBuilder,private store:Store,private route:ActivatedRoute,private router:Router
    ,private notificationService:NotificationService  ) { }

  ngOnInit() {
    this.createForm();
    this.store.dispatch(getCustomer());

    this.customerId = this.route.snapshot.paramMap.get('id'); // Assign productId here

    const customer = this.route.snapshot.data['customer'];
    if (customer && customer.id) {
      this.populateForm(customer);
    }
  }
  createForm() {
    this.angForm = this.fb.group({
      name: [ '', [Validators.required]],//Validators.pattern('^[a-zA-Z]*$')
      state: [ '', [Validators.required]],
      description: [ ''],//[Validators.pattern('^[a-zA-Z0-9]*$')]
      gender: [ ''],
      phone: [ ''], //[Validators.pattern('[0-9]*')] // Corrected pattern validator
      email: [ '',[Validators.email]],
      city: [ ''],//Validators.pattern('^[a-zA-Z]*$')
      address: [ ''],//,[Validators.pattern('^[a-zA-Z0-9]*$')]
      birthdate: ['']
    });
  }

  


  onSubmit() {
    // console.log("Submit button clicked");
    if (this.angForm.invalid) {
      this.notificationService.showError("Form is invalid");
      return;
    }
    
    const formData = this.angForm.value;
    const birthdate = new Date(this.angForm.get('birthdate')?.value); // Access birthdate using get() method
  
    // Format the birthdate in the desired format
    const formattedBirthdate = format(birthdate, 'MM-dd-yyyy HH:mm:ss z');
  
    // Create a new property to hold the formatted birthdate
    const customerWithFormattedBirthdate = {
      ...formData,
      birthdate: formattedBirthdate
    };
  
    if (!this.customerId) {
      this.store.dispatch(addCustomer({ customer: customerWithFormattedBirthdate }));
      this.notificationService.showSuccess('Customer added successfully.');
    } else {
      this.store.dispatch(updateCustomer({ id: this.customerId ? this.customerId : null,customer: customerWithFormattedBirthdate }));
      this.notificationService.showSuccess('Customer updated successfully.');
    }
  
    this.submitted.next(formData);
  }
  
  confirmDelete(){
    if (!this.customerId) {
      this.notificationService.showError('Cannot delete customer without id.');
      return;
    }

    this.store.dispatch(deleteCustomer({ id: BigInt(this.customerId) }));
    this.notificationService.showSuccess('Customer deleted successfully.');

  }

  canceled() {
    this.router.navigate(['/customers']);
  }

  populateForm(customer: Customer) {
    this.angForm.patchValue({
      name: customer.name,
      state: customer.state,
      description: customer.description,
      gender:customer.gender,
      phone:customer.phone,
      email:customer.email,
      city:customer.city,
      address:customer.address,
      birthdate:customer.birthdate
    });
  }

  private formatDate(dateString: string): string {
    const date = new Date(dateString);
    // Format the date to match "MM-dd-yyyy hh:mm:ss Z"
    const formattedDate = `${(date.getMonth() + 1).toString().padStart(2, '0')}-${date.getDate().toString().padStart(2, '0')}-${date.getFullYear()} ${date.getHours().toString().padStart(2, '0')}:${date.getMinutes().toString().padStart(2, '0')}:${date.getSeconds().toString().padStart(2, '0')} Z`;
    return formattedDate;
  }
}










// import { ChangeDetectionStrategy, Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
// import { FormBuilder, FormGroup, Validators } from '@angular/forms';
// import { Customer } from '../../../core';

// @Component({
//   selector: 'app-customer-edit',
//   templateUrl: './customer-edit.component.html',
//   styleUrls: ['./customer-edit.component.scss'],
//   changeDetection: ChangeDetectionStrategy.OnPush
// })
// export class CustomerEditComponent implements OnInit {

//   @Input() public customer: Customer = new Customer(); // Initialize customer property with a new instance of Customer
//   @Input() public error: string = ''; // Assign an initial value to error property

//   @Output() public submitted: EventEmitter<Customer> = new EventEmitter();
//   @Output() public canceled: EventEmitter<string> = new EventEmitter();
//   @Output() public deleted: EventEmitter<string> = new EventEmitter();

//   public angForm: FormGroup = this.fb.group({}); // Initialize angForm property

//   public states = [
//     { name: 'Gujarat', code: 'GJ' },
//     { name: 'Mahrastra', code: 'MH' },
//     { name: 'Andhra Pradesh', code: 'AP' },
//     { name: 'Rajasthan', code: 'RJ' },
//     { name: 'Utarpradesh', code: 'UP' },
//     { name: 'Panjab', code: 'PB' }
//     // Other state objects...
//   ];

//   public genders = [
//     { name: 'Male', code: 'M' },
//     { name: 'Female', code: 'F' }
//   ];

//   constructor(
//     private fb: FormBuilder
//   ) { }

//   ngOnInit() {
//     this.createForm();
    
//   }

//   createForm() {
//     this.angForm = this.fb.group({
//       name: [this.customer?.name || '', [Validators.required]],//Validators.pattern('^[a-zA-Z]*$')
//       state: [this.customer?.state || '', [Validators.required]],
//       description: [this.customer?.description || ''],//[Validators.pattern('^[a-zA-Z0-9]*$')]
//       gender: [this.customer?.gender || ''],
//       phone: [this.customer?.phone || ''], //[Validators.pattern('[0-9]*')] // Corrected pattern validator
//       email: [this.customer?.email || '',[Validators.email]],
//       city: [this.customer?.city || ''],//Validators.pattern('^[a-zA-Z]*$')
//       address: [this.customer?.address || ''],//,[Validators.pattern('^[a-zA-Z0-9]*$')]
//       birthdate: [!this.customer?.id ? null : new Date(this.customer.birthdate)]
//     });
//   }

//   onSubmit() {
//     if (this.angForm.invalid)
//       return;
//     console.log(this.angForm.value);
//     this.submitted.next(this.angForm.value);
//   }

//   confirmDelete(){
//     if(confirm("Are you sure to delete?")) {
//       this.deleted.emit('');
//     }
//   }
// }
