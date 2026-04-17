import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-forgot-password',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="auth-layout"><div class="auth-card"><h1>Forgot password</h1><p class="muted">Reset flow scaffold with secure messaging.</p></div></div>
  `
})
export class ForgotPasswordComponent {}
