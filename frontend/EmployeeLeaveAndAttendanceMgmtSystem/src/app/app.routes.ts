import { Routes } from '@angular/router';
import { LoginComponent } from './pages/login/login.component';
import { AdminComponent } from './pages/admin/admin.component';
import { AdminGuard } from './guards/admin.guard';
import { ManagerComponent } from './pages/manager/manager.component';
import { EmployeeComponent } from './pages/employee/employee.component';
import { EmployeeGuard } from './guards/employee.guard';
import { ManagerGuard } from './guards/manager.guard';
import { ManagerLeaveApprovalComponent } from './components/manager-leave-approval/manager-leave-approval.component';
import { UserComponent } from './components/user/user.component';
import { EmpAssignmentComponent } from './components/emp-assignment/emp-assignment.component';
import { LeaveComponent } from './components/leave/leave.component';
import { ShiftAssignmentComponent } from './components/shift-assignment/shift-assignment.component';
import { ShiftRequestComponent } from './components/shift-request/shift-request.component';
import { ManagerDashboardComponent } from './components/manager-dashboard/manager-dashboard.component';
import { TeamAttendanceComponent } from './components/team-attendance/team-attendance.component';
import { ShiftComponent } from './components/shift/shift.component';
import { EmpDashboardComponent } from './components/emp-dashboard/emp-dashboard.component';
import { AttendanceComponent } from './components/attendance/attendance.component';
import { ManagerReportsPageComponent } from './components/manager-reports-page/manager-reports-page.component';
import { AdminDashboardComponent } from './components/admin-dashboard/admin-dashboard.component';
import { UserDeletionComponent } from './components/user-deletion/user-deletion.component';

export const routes: Routes = [
  { path: '', redirectTo: 'login', pathMatch: 'full' },
  { path: 'login', component: LoginComponent },
  { 
    path: 'manager', 
    component: ManagerComponent, 
    canActivate: [ManagerGuard], 
    children: [
      { path: 'leave-approval', component: ManagerLeaveApprovalComponent },
      { path: 'assign-shift' , component: ShiftAssignmentComponent },
      { path: 'shift-swap-request', component: ShiftRequestComponent },
      { path: 'team-attendance', component: TeamAttendanceComponent },
      { path: 'reports', component: ManagerReportsPageComponent },
      { path: '', component: ManagerDashboardComponent },
    ]
  },  
  { 
    path: 'employee', 
    component: EmployeeComponent, 
    canActivate: [EmployeeGuard], 
    children: [
      { path: 'leave', component: LeaveComponent },
      { path: 'shift', component: ShiftComponent },
      { path: 'attendance', component: AttendanceComponent },
      { path: '', component: EmpDashboardComponent }
    ]
  },
  { 
    path: 'admin', 
    component: AdminComponent,
    canActivate: [AdminGuard],
    children: [
      { path: '', component: AdminDashboardComponent },
      { path: 'create-user', component: UserComponent },
      { path: 'assign-emp', component: EmpAssignmentComponent },
      { path: 'delete-user', component: UserDeletionComponent}
    ]
  }
];
