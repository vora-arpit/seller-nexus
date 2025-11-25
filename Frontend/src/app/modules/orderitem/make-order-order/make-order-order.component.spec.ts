import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MakeOrderOrderComponent } from './make-order-order.component';

describe('MakeOrderOrderComponent', () => {
  let component: MakeOrderOrderComponent;
  let fixture: ComponentFixture<MakeOrderOrderComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [MakeOrderOrderComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(MakeOrderOrderComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
