import { Component, OnInit } from '@angular/core';
import { ShiftService } from '../../services/shift.service';
import { ToastrService } from 'ngx-toastr';
import { CommonModule } from '@angular/common';
import { UserService } from '../../services/user.service';
import { ShiftResponseDTO } from '../../dtos/shift.dto';

@Component({
  selector: 'app-shift-request',
  templateUrl: './shift-request.component.html',
  styleUrls: ['./shift-request.component.css'],
  imports:[CommonModule]
})
export class ShiftRequestComponent implements OnInit {
  swapRequests: any[] = [];
  pageSize = 6;
  currentPage = 0;

  constructor(private shiftService: ShiftService, private toastr: ToastrService, private userService: UserService) {}

  ngOnInit() {
    this.getSwapRequestsForManager();
  }

  get paginatedSwapRequests(): any[] {
        const start = this.currentPage * this.pageSize;
        return this.swapRequests.slice(start, start + this.pageSize);
      }
  
      get totalPages(): number {
        return Math.ceil(this.swapRequests.length / this.pageSize);
      }
  
      
    nextPage(): void {
      if (this.currentPage < this.totalPages-1) {
        this.currentPage++;
      }
    }
  
    previousPage(): void {
      if (this.currentPage > 0) {
        this.currentPage--;
      }
    }
  

  getSwapRequestsForManager() {
    this.userService.getAssignedEmployeesForManager().subscribe({
      next: (employees) => {
        const employeeMap = new Map(employees.map(emp => [emp.id, emp.name]));
  
        this.shiftService.getShiftSwapRequestsForManager('PENDING').subscribe({
          next: (requests) => {
            this.swapRequests = requests.map(request => ({
              ...request,
              employeeName: employeeMap.get(request.userId) || 'Unknown'
            }));
          },
          error: (err) => {
            console.error('Failed to fetch swap requests.')
            const errorData = typeof err.error === 'string' ? JSON.parse(err.error) : err.error;
            this.toastr.error(errorData.message); 
          }
        });
      },
      error: (err) => {
        console.error('Failed to fetch assigned employees.')
        const errorData = typeof err.error === 'string' ? JSON.parse(err.error) : err.error;
        this.toastr.error(errorData.message); 
      }
    });
  }
  

  updateSwapRequestStatus(swapId: number, status: string) {
    const requestData = { swapId, status };
    console.log(requestData)
    this.shiftService.updateSwapRequestStatus(requestData).subscribe({
      next: () => {
        this.toastr.success(`Swap request ${status.toLowerCase()} successfully!`);
        this.ngOnInit();
      },
      error: (err) => {
        const errorData = typeof err.error === 'string' ? JSON.parse(err.error) : err.error;
        this.toastr.error(`Failed to ${status.toLowerCase()} swap request.`, errorData.message)
      }
    });
  }
}
