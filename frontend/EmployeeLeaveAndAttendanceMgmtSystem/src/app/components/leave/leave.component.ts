import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { LeaveRequestFormComponent } from '../leaverequestform/leaverequestform.component';
import { GetLeaveRequestHistoryComponent } from '../getleaverequesthistory/getleaverequesthistory.component';

@Component({
  standalone: true,
  selector: 'app-leave',
  templateUrl: './leave.component.html',
  styleUrls: ['./leave.component.css'],
  imports: [CommonModule, LeaveRequestFormComponent, GetLeaveRequestHistoryComponent]
})
export class LeaveComponent {}
