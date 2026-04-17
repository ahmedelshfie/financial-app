import { CommonModule } from '@angular/common';
import { Component, Input } from '@angular/core';
import { RouterLink, RouterLinkActive } from '@angular/router';
import { MenuItem } from '../../core/models/auth-session.model';
import { HasRoleDirective } from '../../shared/directives/has-role.directive';

@Component({
  selector: 'app-sidebar',
  standalone: true,
  imports: [CommonModule, RouterLink, RouterLinkActive, HasRoleDirective],
  template: `
    <aside class="shell-sidebar">
      <div class="brand">FinanceFlow</div>
      <p class="brand-subtitle">Operations Console</p>

      <nav class="nav-list">
        <ng-container *ngFor="let item of menuItems">
          <a *appHasRole="item.roles ?? []" [routerLink]="item.route" routerLinkActive="active" class="nav-item">{{ item.label }}</a>
        </ng-container>
      </nav>
    </aside>
  `
})
export class SidebarComponent {
  @Input() menuItems: MenuItem[] = [];
}
