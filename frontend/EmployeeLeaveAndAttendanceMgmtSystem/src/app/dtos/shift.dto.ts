export interface ShiftAssignment{
    userId: number,
    date: string,
    shiftType: string
}

export interface ShiftResponseDTO {
    id: number;
    userId: number;
    shiftDate: string;
    shiftType: string;
  }