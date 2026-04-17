import { CommonModule, DatePipe } from '@angular/common';
import { Component, DestroyRef, computed, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { finalize } from 'rxjs';
import { ReportsService } from '../services/reports.service';
import { ReportItem } from '../models/report.model';
import { LoadingSpinnerComponent } from '../../../shared/components/loading-spinner.component';

@Component({
  selector: 'app-reports',
  standalone: true,
  imports: [CommonModule, FormsModule, DatePipe, LoadingSpinnerComponent],
  template: `
    <section class="page-header">
      <h2>Reports</h2>
      <p class="muted">Explore analytics reports with category and status filtering.</p>
    </section>

    <article class="panel transaction-filters">
      <div>
        <label for="report-search">Search reports</label>
        <input id="report-search" [(ngModel)]="searchTerm" placeholder="Report name or id" />
      </div>
      <div>
        <label for="report-category">Category</label>
        <select id="report-category" [(ngModel)]="categoryFilter">
          <option value="ALL">All</option>
          <option value="Compliance">Compliance</option>
          <option value="Finance">Finance</option>
          <option value="Operations">Operations</option>
        </select>
      </div>
    </article>

    <app-loading-spinner *ngIf="isLoading()" label="Loading reports..."></app-loading-spinner>

    <article class="panel table-panel" *ngIf="!isLoading()">
      <table>
        <thead>
          <tr><th>Report</th><th>Category</th><th>Last generated</th><th>Status</th><th>Action</th></tr>
        </thead>
        <tbody>
          <tr *ngFor="let report of filteredReports()">
            <td>{{ report.name }} <p class="muted">{{ report.id }}</p></td>
            <td>{{ report.category }}</td>
            <td>{{ report.lastGeneratedAt | date: 'short' }}</td>
            <td><span class="chip" [class.success]="report.status === 'Ready'">{{ report.status }}</span></td>
            <td><button type="button" class="btn ghost">Open</button></td>
          </tr>
          <tr *ngIf="!filteredReports().length"><td colspan="5" class="muted">No reports available for this filter.</td></tr>
        </tbody>
      </table>
    </article>
  `
})
export class ReportsComponent {
  private readonly reportsService = inject(ReportsService);
  private readonly destroyRef = inject(DestroyRef);

  readonly isLoading = signal(false);
  readonly reports = signal<ReportItem[]>([]);

  searchTerm = '';
  categoryFilter: 'ALL' | ReportItem['category'] = 'ALL';

  readonly filteredReports = computed(() => {
    const query = this.searchTerm.toLowerCase().trim();

    return this.reports().filter((report) => {
      const textMatch = !query || [report.id, report.name].some((value) => value.toLowerCase().includes(query));
      const categoryMatch = this.categoryFilter === 'ALL' || report.category === this.categoryFilter;
      return textMatch && categoryMatch;
    });
  });

  constructor() {
    this.loadReports();
  }

  private loadReports(): void {
    this.isLoading.set(true);
    this.reportsService
      .listReports()
      .pipe(
        takeUntilDestroyed(this.destroyRef),
        finalize(() => this.isLoading.set(false))
      )
      .subscribe((reports) => this.reports.set(reports));
  }
}
