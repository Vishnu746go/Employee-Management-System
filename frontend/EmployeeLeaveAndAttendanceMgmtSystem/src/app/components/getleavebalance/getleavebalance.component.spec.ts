import { ComponentFixture, TestBed } from '@angular/core/testing';

import { GetLeaveBalanceComponent } from './getleavebalance.component';
describe('GetleavebalanceComponent', () => {
  let component: GetLeaveBalanceComponent;
  let fixture: ComponentFixture<GetLeaveBalanceComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [GetLeaveBalanceComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(GetLeaveBalanceComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
