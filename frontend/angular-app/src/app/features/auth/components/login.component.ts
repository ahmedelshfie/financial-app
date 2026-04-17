import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService, LoginRequest } from '../services/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  template: `
    <div class="auth-layout">
      <div class="auth-card">
        <h1>Welcome back</h1>
        <p class="muted">Sign in to access your finance operations shell.</p>

        <form (ngSubmit)="onSubmit()" #loginForm="ngForm">
          <div class="form-group">
            <label for="username">Username</label>
            <input id="username" name="username" type="text" [(ngModel)]="credentials.username" required minlength="3" #usernameField="ngModel" />
            <span class="error" *ngIf="usernameField.invalid && usernameField.touched">Username is required (min 3 characters).</span>
          </div>

          <div class="form-group">
            <label for="password">Password</label>
            <input id="password" name="password" type="password" [(ngModel)]="credentials.password" required minlength="6" #passwordField="ngModel" />
            <span class="error" *ngIf="passwordField.invalid && passwordField.touched">Password is required (min 6 characters).</span>
          </div>

          <div class="alert-error" *ngIf="errorMessage()">{{ errorMessage() }}</div>

          <div class="form-group">
            <button type="submit" class="btn primary" [disabled]="loginForm.invalid || isLoading()">
              {{ isLoading() ? 'Signing in...' : 'Login' }}
            </button>
          </div>

          <p class="auth-link">Need an account? <a routerLink="/register">Register</a> • <a routerLink="/forgot-password">Forgot password?</a></p>
        </form>
      </div>
    </div>
  `
})
export class LoginComponent {
  private authService = inject(AuthService);
  private router = inject(Router);

  credentials: LoginRequest = { username: '', password: '' };

  readonly isLoading = this.authService.isLoading;
  readonly errorMessage = this.authService.error;

  onSubmit(): void {
    if (this.credentials.username && this.credentials.password) {
      this.authService.login(this.credentials).subscribe({
        next: () => {
          const redirectUrl = this.authService.consumeRedirectUrl();
          if (redirectUrl) {
            this.router.navigateByUrl(redirectUrl);
          } else {
            this.router.navigate(['/dashboard']);
          }
        }
      });
    }
  }
}
