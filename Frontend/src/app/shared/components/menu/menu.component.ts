import { OnInit, Component, Input } from '@angular/core';
import { User } from '../../../core';


@Component({
  selector: 'app-menu',
  templateUrl: './menu.component.html',
  styleUrls: ['./menu.component.scss']
})
export class MenuComponent implements OnInit {
[x: string]: any;

  @Input() public user?: User;

  ngOnInit() {

  }

}