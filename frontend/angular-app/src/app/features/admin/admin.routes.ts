import { Routes } from '@angular/router';
import { roleGuard } from '../../core/guards/auth.guard';
import { USER_ROLES } from '../../core/constants/roles.constants';

export const ADMIN_ROUTES: Routes = [
  {
    path: '',
    title: 'Admin',
    canActivate: [roleGuard([USER_ROLES.ADMIN])],
    loadComponent: () => import('./components/admin.component').then((m) => m.AdminComponent)
  }
];
