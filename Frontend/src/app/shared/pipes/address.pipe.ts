import {Pipe, PipeTransform} from '@angular/core';
import { Address } from '../../core/models';

@Pipe({
  name: 'address'
})
export class AddressPipe implements PipeTransform {
  transform(address: Address): string {
    const {postcode, country, city} = address;
    return `${postcode} | ${country}, ${city}`;
  }
}
