import { ComponentFixture, TestBed } from '@angular/core/testing';

import { OrderitemListOrderComponent } from './orderitem-list-order.component';

describe('OrderitemListOrderComponent', () => {
  let component: OrderitemListOrderComponent;
  let fixture: ComponentFixture<OrderitemListOrderComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [OrderitemListOrderComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(OrderitemListOrderComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
