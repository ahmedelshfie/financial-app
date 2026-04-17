import { Routes } from '@angular/router';

export const REPORTS_ROUTES: Routes = [
  {
    path: '',
    title: 'Reports',
    loadComponent: () => import('./components/reports.component').then((m) => m.ReportsComponent)
  }
];
