import { UserRole, UserStatus } from '../../../core/constants/roles.constants';

export interface AdminUser {
  id: number;
  username: string;
  role: UserRole;
  status: UserStatus;
}

export interface AdminConfig {
  key: string;
  value: string;
}
