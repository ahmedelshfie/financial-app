import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Input, Output } from '@angular/core';

@Component({
  selector: 'app-confirm-modal',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="modal-backdrop" *ngIf="open" (click)="cancel.emit()">
      <div class="modal" (click)="$event.stopPropagation()">
        <h3>{{ title }}</h3>
        <p class="muted">{{ message }}</p>
        <div class="modal-actions">
          <button type="button" class="btn ghost" (click)="cancel.emit()">Cancel</button>
          <button type="button" class="btn primary" (click)="confirm.emit()">Confirm</button>
        </div>
      </div>
    </div>
  `
})
export class ConfirmModalComponent {
  @Input() open = false;
  @Input() title = 'Please confirm';
  @Input() message = 'Are you sure you want to continue?';

  @Output() readonly confirm = new EventEmitter<void>();
  @Output() readonly cancel = new EventEmitter<void>();
}
