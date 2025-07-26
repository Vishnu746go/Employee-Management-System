import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EmpAssignmentComponent } from './emp-assignment.component';

describe('EmpAssignmentComponent', () => {
  let component: EmpAssignmentComponent;
  let fixture: ComponentFixture<EmpAssignmentComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [EmpAssignmentComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(EmpAssignmentComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
