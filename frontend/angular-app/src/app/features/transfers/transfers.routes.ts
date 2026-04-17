import { Routes } from '@angular/router';

export const TRANSFERS_ROUTES: Routes = [
  {
    path: '',
    title: 'Transfers',
    loadComponent: () => import('./components/transfers.component').then((m) => m.TransfersComponent)
  }
];
