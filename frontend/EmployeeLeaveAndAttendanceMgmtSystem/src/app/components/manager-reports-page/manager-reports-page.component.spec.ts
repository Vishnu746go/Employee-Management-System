import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ManagerReportsPageComponent } from './manager-reports-page.component';

describe('ManagerReportsPageComponent', () => {
  let component: ManagerReportsPageComponent;
  let fixture: ComponentFixture<ManagerReportsPageComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ManagerReportsPageComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ManagerReportsPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
