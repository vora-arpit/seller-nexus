import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EditOrderitemOrderComponent } from './edit-orderitem-order.component';

describe('EditOrderitemOrderComponent', () => {
  let component: EditOrderitemOrderComponent;
  let fixture: ComponentFixture<EditOrderitemOrderComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [EditOrderitemOrderComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(EditOrderitemOrderComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
