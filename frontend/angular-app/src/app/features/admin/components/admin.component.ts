import { CommonModule } from '@angular/common';
import { Component, DestroyRef, inject, signal } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { finalize, forkJoin } from 'rxjs';
import { AdminService } from '../services/admin.service';
import { AdminConfig, AdminUser } from '../models/admin.model';
import { USER_STATUSES } from '../../../core/constants/roles.constants';
import { LoadingSpinnerComponent } from '../../../shared/components/loading-spinner.component';

@Component({
  selector: 'app-admin',
  standalone: true,
  imports: [CommonModule, LoadingSpinnerComponent],
  template: `
    <section class="page-header">
      <h2>Admin</h2>
      <p class="muted">Admin workspace for users, roles, and platform configuration.</p>
    </section>

    <app-loading-spinner *ngIf="isLoading()" label="Loading admin data..."></app-loading-spinner>

    <section class="two-column" *ngIf="!isLoading()">
      <article class="panel table-panel">
        <h3>User & Role Management</h3>
        <table>
          <thead><tr><th>User</th><th>Role</th><th>Status</th></tr></thead>
          <tbody>
            <tr *ngFor="let user of users()">
              <td>{{ user.username }}</td>
              <td>{{ user.role }}</td>
              <td><span class="chip" [class.success]="user.status === userStatuses.ACTIVE">{{ user.status }}</span></td>
            </tr>
          </tbody>
        </table>
      </article>

      <article class="panel table-panel">
        <h3>Configuration</h3>
        <table>
          <thead><tr><th>Key</th><th>Value</th></tr></thead>
          <tbody>
            <tr *ngFor="let config of configs()">
              <td>{{ config.key }}</td>
              <td>{{ config.value }}</td>
            </tr>
          </tbody>
        </table>
      </article>
    </section>
  `
})
export class AdminComponent {
  private readonly adminService = inject(AdminService);
  private readonly destroyRef = inject(DestroyRef);

  readonly isLoading = signal(false);
  readonly users = signal<AdminUser[]>([]);
  readonly configs = signal<AdminConfig[]>([]);
  protected readonly userStatuses = USER_STATUSES;

  constructor() {
    this.loadAdminData();
  }

  private loadAdminData(): void {
    this.isLoading.set(true);
    forkJoin({ users: this.adminService.listUsers(), configs: this.adminService.listConfigs() })
      .pipe(
        takeUntilDestroyed(this.destroyRef),
        finalize(() => this.isLoading.set(false))
      )
      .subscribe(({ users, configs }) => {
        this.users.set(users);
        this.configs.set(configs);
      });
  }
}
