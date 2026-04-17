import { Injectable } from '@angular/core';

@Injectable({ providedIn: 'root' })
export class AuthorizationService {
  hasAnyRole(userRoles: string[] | null | undefined, requiredRoles: string[] | null | undefined): boolean {
    if (!requiredRoles?.length) {
      return true;
    }

    if (!userRoles?.length) {
      return false;
    }

    return requiredRoles.some((requiredRole) => userRoles.includes(requiredRole));
  }
}
