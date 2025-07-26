import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ShiftRequestComponent } from './shift-request.component';

describe('ShiftRequestComponent', () => {
  let component: ShiftRequestComponent;
  let fixture: ComponentFixture<ShiftRequestComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ShiftRequestComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ShiftRequestComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
