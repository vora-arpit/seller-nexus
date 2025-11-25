import {Pipe, PipeTransform} from '@angular/core';
import {DatePipe} from '@angular/common';

@Pipe({
  name: 'dateTime',
})
export class DateTimePipe implements PipeTransform {
  constructor(
    private date: DatePipe,
  ) { }

  transform(value: Date): string {
    const date = this.date.transform(value, 'dd.MM');
    const time = this.date.transform(value, 'hh:mm');
    return [date, time].join(' ');
  }
}
