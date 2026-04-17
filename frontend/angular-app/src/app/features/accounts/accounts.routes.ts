import { Routes } from '@angular/router';

export const ACCOUNTS_ROUTES: Routes = [
  {
    path: '',
    title: 'Accounts',
    loadComponent: () => import('./components/accounts.component').then((m) => m.AccountsComponent)
  }
];
