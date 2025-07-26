import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ManagerReportComponent } from '../managerreport/managerreport.component';
import { ManagerHistoryComponent } from '../manager-history/manager-history.component';

@Component({
  selector: 'app-manager-reports-page',
  standalone: true,
  imports: [CommonModule, ManagerReportComponent, ManagerHistoryComponent],
  templateUrl: './manager-reports-page.component.html',
  styleUrls: ['./manager-reports-page.component.css']
})
export class ManagerReportsPageComponent {}
