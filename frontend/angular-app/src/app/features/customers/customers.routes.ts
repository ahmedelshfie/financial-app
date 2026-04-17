import { Routes } from '@angular/router';

export const CUSTOMERS_ROUTES: Routes = [
  {
    path: '',
    title: 'Customers',
    loadComponent: () => import('./components/customers.component').then((m) => m.CustomersComponent)
  }
];
