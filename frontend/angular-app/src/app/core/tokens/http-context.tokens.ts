import { HttpContextToken } from '@angular/common/http';

/**
 * Set to true for requests that should not include auth headers.
 */
export const SKIP_AUTH = new HttpContextToken<boolean>(() => false);
