export const USER_ROLES = {
  ADMIN: 'ROLE_ADMIN',
  USER: 'ROLE_USER'
} as const;

export type UserRole = (typeof USER_ROLES)[keyof typeof USER_ROLES];

export const USER_STATUSES = {
  ACTIVE: 'Active',
  SUSPENDED: 'Suspended'
} as const;

export type UserStatus = (typeof USER_STATUSES)[keyof typeof USER_STATUSES];
