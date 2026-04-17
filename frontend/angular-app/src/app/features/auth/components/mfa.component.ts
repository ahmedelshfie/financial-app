import { Component } from '@angular/core';

@Component({
  selector: 'app-mfa',
  standalone: true,
  template: `<div class="auth-layout"><div class="auth-card"><h1>MFA verification</h1><p class="muted">Enter one-time passcode to continue.</p></div></div>`
})
export class MfaComponent {}
