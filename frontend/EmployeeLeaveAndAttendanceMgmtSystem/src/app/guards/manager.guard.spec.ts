import { TestBed } from '@angular/core/testing';
import { CanActivateFn } from '@angular/router';

import { ManagerGuard } from './manager.guard';

describe('ManagerGuard', () => {
   let service: ManagerGuard;
  
    beforeEach(() => {
      TestBed.configureTestingModule({});
      service = TestBed.inject(ManagerGuard);
    });
  
    it('should be created', () => {
      expect(service).toBeTruthy();
    });
});
