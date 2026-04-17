import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService, RegisterRequest } from '../services/auth.service';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  template: `
    <div class="auth-layout">
      <div class="auth-card">
        <h1>Create account</h1>
        <p class="muted">Set up access to the financial platform shell.</p>

        <form (ngSubmit)="onSubmit()" #registerForm="ngForm">
          <div class="form-group">
            <label for="username">Username</label>
            <input id="username" name="username" type="text" [(ngModel)]="userData.username" required minlength="3" #usernameField="ngModel" />
            <span class="error" *ngIf="usernameField.invalid && usernameField.touched">Username is required (min 3 characters).</span>
          </div>

          <div class="form-group">
            <label for="email">Email</label>
            <input id="email" name="email" type="email" [(ngModel)]="userData.email" required email #emailField="ngModel" />
            <span class="error" *ngIf="emailField.invalid && emailField.touched">Please enter a valid email.</span>
          </div>

          <div class="form-group">
            <label for="password">Password</label>
            <input id="password" name="password" type="password" [(ngModel)]="userData.password" required minlength="6" #passwordField="ngModel" />
            <span class="error" *ngIf="passwordField.invalid && passwordField.touched">Password is required (min 6 characters).</span>
          </div>

          <div class="alert-error" *ngIf="errorMessage()">{{ errorMessage() }}</div>

          <div class="form-group">
            <button type="submit" class="btn primary" [disabled]="registerForm.invalid || isLoading()">
              {{ isLoading() ? 'Creating account...' : 'Register' }}
            </button>
          </div>

          <p class="auth-link">Already registered? <a routerLink="/login">Login</a></p>
        </form>
      </div>
    </div>
  `
})
export class RegisterComponent {
  private authService = inject(AuthService);
  private router = inject(Router);

  userData: RegisterRequest = {
    username: '',
    email: '',
    password: '',
    firstName: '',
    lastName: ''
  };

  readonly isLoading = this.authService.isLoading;
  readonly errorMessage = this.authService.error;

  onSubmit(): void {
    if (this.userData.username && this.userData.email && this.userData.password) {
      this.authService.register(this.userData).subscribe({
        next: () => {
          this.router.navigate(['/dashboard']);
        }
      });
    }
  }
}
