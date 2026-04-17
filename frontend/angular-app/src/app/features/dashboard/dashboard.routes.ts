import { Routes } from '@angular/router';

export const DASHBOARD_ROUTES: Routes = [
  {
    path: '',
    title: 'Dashboard',
    loadComponent: () => import('./components/dashboard.component').then((m) => m.DashboardComponent)
  }
];
