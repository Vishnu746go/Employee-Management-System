import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ManagerHistoryComponent } from './manager-history.component';

describe('ManagerHistoryComponent', () => {
  let component: ManagerHistoryComponent;
  let fixture: ComponentFixture<ManagerHistoryComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ManagerHistoryComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ManagerHistoryComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
