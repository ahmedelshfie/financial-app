import { Routes } from '@angular/router';
import { guestGuard } from '../../core/guards/auth.guard';

export const AUTH_ROUTES: Routes = [
  {
    path: 'login',
    title: 'Login',
    canActivate: [guestGuard],
    loadComponent: () => import('./components/login.component').then((m) => m.LoginComponent)
  },
  {
    path: 'register',
    title: 'Register',
    canActivate: [guestGuard],
    loadComponent: () => import('./components/register.component').then((m) => m.RegisterComponent)
  },
  {
    path: 'forgot-password',
    title: 'Forgot Password',
    canActivate: [guestGuard],
    loadComponent: () => import('./components/forgot-password.component').then((m) => m.ForgotPasswordComponent)
  },
  {
    path: 'reset-password',
    title: 'Reset Password',
    canActivate: [guestGuard],
    loadComponent: () => import('./components/reset-password.component').then((m) => m.ResetPasswordComponent)
  },
  {
    path: 'mfa',
    title: 'MFA Verification',
    canActivate: [guestGuard],
    loadComponent: () => import('./components/mfa.component').then((m) => m.MfaComponent)
  }
];
