import { ComponentFixture, TestBed } from '@angular/core/testing';

import { JoomLoginComponent } from './joom-login.component';

describe('JoomLoginComponent', () => {
  let component: JoomLoginComponent;
  let fixture: ComponentFixture<JoomLoginComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [JoomLoginComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(JoomLoginComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
