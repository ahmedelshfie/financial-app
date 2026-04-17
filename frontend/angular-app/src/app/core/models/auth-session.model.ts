export interface AuthSession {
  username: string;
  roles: string[];
  expiresAtEpochSeconds?: number;
}

export interface MenuItem {
  label: string;
  route: string;
  roles?: string[];
}
