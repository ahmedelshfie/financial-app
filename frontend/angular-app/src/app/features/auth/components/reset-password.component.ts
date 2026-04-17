import { Component } from '@angular/core';

@Component({
  selector: 'app-reset-password',
  standalone: true,
  template: `
    <div class="auth-layout"><div class="auth-card"><h1>Reset password</h1><p class="muted">Token-based password reset screen.</p></div></div>
  `
})
export class ResetPasswordComponent {}
