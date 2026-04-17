import { Component, Input } from '@angular/core';

@Component({
  selector: 'app-loading-spinner',
  standalone: true,
  template: `
    <div class="loading-wrap" [attr.aria-label]="label">
      <span class="loading-spinner"></span>
      <span class="muted">{{ label }}</span>
    </div>
  `
})
export class LoadingSpinnerComponent {
  @Input() label = 'Loading...';
}
