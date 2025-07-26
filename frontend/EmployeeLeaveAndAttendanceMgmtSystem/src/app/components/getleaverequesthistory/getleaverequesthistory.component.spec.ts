import { ComponentFixture, TestBed } from '@angular/core/testing';

import { GetLeaveRequestHistoryComponent } from './getleaverequesthistory.component';

describe('GetleaverequesthistoryComponent', () => {
  let component: GetLeaveRequestHistoryComponent;
  let fixture: ComponentFixture<GetLeaveRequestHistoryComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [GetLeaveRequestHistoryComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(GetLeaveRequestHistoryComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
