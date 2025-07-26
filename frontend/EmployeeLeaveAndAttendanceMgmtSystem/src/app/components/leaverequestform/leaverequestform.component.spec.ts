import { ComponentFixture, TestBed } from '@angular/core/testing';

import { LeaveRequestFormComponent } from './leaverequestform.component';

describe('LeaverequestformComponent', () => {
  let component: LeaveRequestFormComponent;
  let fixture: ComponentFixture<LeaveRequestFormComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [LeaveRequestFormComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(LeaveRequestFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
