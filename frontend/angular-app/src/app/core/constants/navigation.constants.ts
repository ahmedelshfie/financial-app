import { MenuItem } from '../models/auth-session.model';
import { USER_ROLES } from './roles.constants';

const STANDARD_USER_ROLES = [USER_ROLES.USER, USER_ROLES.ADMIN];

export const MAIN_MENU_ITEMS: MenuItem[] = [
  { label: 'Dashboard', route: '/dashboard', roles: STANDARD_USER_ROLES },
  { label: 'Accounts', route: '/accounts', roles: STANDARD_USER_ROLES },
  { label: 'Customers', route: '/customers', roles: STANDARD_USER_ROLES },
  { label: 'Transactions', route: '/transactions', roles: STANDARD_USER_ROLES },
  { label: 'Transfers', route: '/transfers', roles: STANDARD_USER_ROLES },
  { label: 'Payments', route: '/payments', roles: STANDARD_USER_ROLES },
  { label: 'Reports', route: '/reports', roles: STANDARD_USER_ROLES },
  { label: 'Admin', route: '/admin', roles: [USER_ROLES.ADMIN] }
];
