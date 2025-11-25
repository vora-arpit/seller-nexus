import { ComponentFixture, TestBed } from '@angular/core/testing';

import { OrderListOrderComponent } from './order-list-order.component';

describe('OrderListOrderComponent', () => {
  let component: OrderListOrderComponent;
  let fixture: ComponentFixture<OrderListOrderComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [OrderListOrderComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(OrderListOrderComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
