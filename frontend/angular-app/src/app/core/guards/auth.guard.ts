import { CanActivateFn, Router, UrlTree } from '@angular/router';
import { inject } from '@angular/core';
import { AuthService } from '../../features/auth/services/auth.service';
import { AuthorizationService } from '../services/authorization.service';

export const authGuard: CanActivateFn = (): boolean | UrlTree => {
  const authService = inject(AuthService);
  const router = inject(Router);

  if (authService.isAuthenticated() && authService.hasValidToken()) {
    return true;
  }

  authService.storeRedirectUrl(router.url);
  return router.createUrlTree(['/login']);
};

export const guestGuard: CanActivateFn = (): boolean | UrlTree => {
  const authService = inject(AuthService);
  const router = inject(Router);

  if (authService.isAuthenticated() && authService.hasValidToken()) {
    return router.createUrlTree(['/dashboard']);
  }

  return true;
};

export const roleGuard = (requiredRoles: string[]): CanActivateFn => (): boolean | UrlTree => {
  const authService = inject(AuthService);
  const authorizationService = inject(AuthorizationService);
  const router = inject(Router);

  if (authorizationService.hasAnyRole(authService.currentUser()?.roles, requiredRoles)) {
    return true;
  }

  return router.createUrlTree(['/dashboard']);
};
