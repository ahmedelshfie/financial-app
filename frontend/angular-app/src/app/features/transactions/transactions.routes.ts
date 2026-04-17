import { Routes } from '@angular/router';

export const TRANSACTIONS_ROUTES: Routes = [
  {
    path: '',
    title: 'Transactions',
    loadComponent: () => import('./components/transactions.component').then((m) => m.TransactionsComponent)
  }
];
