import { Component, OnInit } from '@angular/core';
import { ShiftService } from '../../services/shift.service';
import { ToastrService } from 'ngx-toastr';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-shift',
  templateUrl: './shift.component.html',
  styleUrls: ['./shift.component.css'],
  imports: [CommonModule]
})
export class ShiftComponent implements OnInit {
  shiftData: any = null;
  shiftSwapBalance: number = 0;
  swapHistory: any[] = [];
  selectedDate: string = '';

  constructor(private shiftService: ShiftService, private toastr: ToastrService) {}

  ngOnInit() {
    const today = new Date().toISOString().split('T')[0]; 
    this.selectedDate = today;
    this.getShiftForDate(today);
    this.getShiftSwapInfo();
  }

  getShiftForDate(date: string) {
    this.shiftService.getShiftByDate(date).subscribe({
      next: (response) => this.shiftData = response,
      error: () => this.shiftData = null 
    });
  }

  getShiftSwapInfo() {
    this.shiftService.getShiftSwapBalance().subscribe({
      next: (balance) => this.shiftSwapBalance = balance,
      error: (err) => {
        this.shiftSwapBalance = 0;
        const errorData = typeof err.error === 'string' ? JSON.parse(err.error) : err.error;
        this.toastr.error(errorData.message); 
      }
    });

    this.shiftService.getRecentSwapHistory().subscribe({
      next: (history) =>{ 
        this.swapHistory = history.slice(-4);
        this.swapHistory = this.swapHistory.reverse();
        console.log(history)
      },
      error: (err) => {
        this.swapHistory = [];
        const errorData = typeof err.error === 'string' ? JSON.parse(err.error) : err.error;
        this.toastr.error(errorData.message);
      }
    });
  }

  onDateChange(event: any) {
    this.selectedDate = event.target.value;
    this.getShiftForDate(this.selectedDate);
  }

  submitSwapRequest() {
    if (!this.shiftData) {
      this.toastr.error('No shift data available to swap.');
      return;
    }

    const requestData = {
      shiftId: this.shiftData.id,
      shiftDate: this.shiftData.shiftDate,
      shiftType: this.shiftData.shiftType === "DAY" ? "NIGHT" : "DAY"
    };

    this.shiftService.createSwapRequest(requestData).subscribe({
      next: () => this.toastr.success('Shift swap request submitted!'),
      error: (err) => {
        const errorData = typeof err.error === 'string' ? JSON.parse(err.error) : err.error;
        this.toastr.error(errorData.message);
        console.log(errorData);
      }
    });
  }
}
