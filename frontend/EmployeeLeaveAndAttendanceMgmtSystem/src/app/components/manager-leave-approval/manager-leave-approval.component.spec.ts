import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ManagerLeaveApprovalComponent } from './manager-leave-approval.component';

describe('ManagerLeaveApprovalComponent', () => {
  let component: ManagerLeaveApprovalComponent;
  let fixture: ComponentFixture<ManagerLeaveApprovalComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ManagerLeaveApprovalComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ManagerLeaveApprovalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
