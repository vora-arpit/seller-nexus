// import {Component, Input} from '@angular/core';
// import { DEFAULT_AVATAR_PATH } from '../../../../environments/environment.development';
// import { AuthService, EmployeeImageService, ImageService } from '../../../core';

// @Component({
//   selector: 'app-avatar-settings',
//   template: `
//     <app-generic-image-handler
//       [source]="avatarSrc"
//       [defaultSource]="defaultSource"
//     ></app-generic-image-handler>
//   `,
// // (successUploadEvent)="onSuccessUpload($event)"
// // (successRemoveEvent)="onSuccessRemove()"
//   viewProviders: [
//     {provide: ImageService, useClass: EmployeeImageService}
//   ]
// })
// export class AvatarSettingsComponent {
//   @Input() avatarSrc: string;
//   defaultSource = DEFAULT_AVATAR_PATH;

//   // constructor(private authService: AuthService) { }

//   // onSuccessRemove() {
//   //   this.authService.removeImage();
//   // }

//   // onSuccessUpload(file: File) {
//   //   this.authService.updateImage(file);
//   // }
// }
