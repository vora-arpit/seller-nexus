// This file can be replaced during build by using the `fileReplacements` array.
// `ng build --prod` replaces `environment.ts` with `environment.prod.ts`.
// The list of file replacements can be found in `angular.json`.

export const environment = {
  production: false,
  API_BASE_URL: 'http://localhost:8080',
  OAUTH2_REDIRECT_URI: 'http://localhost:4200/auth/oauth2/redirect',
  ACCESS_TOKEN: 'accessToken',
  stripe:'pk_test_51P8zDOSH1kxQcZnuJCdVuOqsLVhyp8WG7bKJVzQAXfXu5D2211QXYTupo5ZOc0cbgQFZR53NwKCdXxNun59KDIuO00aQW1rIws'
};


export const DEFAULT_PRODUCT_IMAGE_PATH = '/assets/images/product.png';
export const DEFAULT_AVATAR_PATH = '/assets/images/avatar.png';

export const APP_LOGO="../logo1.png"


/*
 * For easier debugging in development mode, you can import the following file
 * to ignore zone related error stack frames such as `zone.run`, `zoneDelegate.invokeTask`.
 *
 * This import should be commented out in production mode because it will have a negative impact
 * on performance if an error is thrown.
 */
// import 'zone.js/dist/zone-error';  // Included with Angular CLI.
