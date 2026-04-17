import { Component, EventEmitter, Input, Output } from '@angular/core';

@Component({
  selector: 'app-header',
  standalone: true,
  template: `
    <header class="shell-header panel">
      <div>
        <h1 class="shell-title">Financial Platform</h1>
        <p class="muted">Welcome back, {{ username }}.</p>
      </div>
      <button type="button" class="btn ghost" (click)="logout.emit()">Logout</button>
    </header>
  `
})
export class HeaderComponent {
  @Input() username = 'Operator';
  @Output() readonly logout = new EventEmitter<void>();
}
