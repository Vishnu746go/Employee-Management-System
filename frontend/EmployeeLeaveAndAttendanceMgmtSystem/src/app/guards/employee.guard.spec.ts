import { TestBed } from '@angular/core/testing';

import { EmployeeGuard } from './employee.guard';

describe('EmployeeGuard', () => {
  let service: EmployeeGuard;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(EmployeeGuard);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
