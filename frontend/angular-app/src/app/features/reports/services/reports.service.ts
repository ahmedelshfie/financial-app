import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ApiUrlService } from '../../../core/services/api-url.service';
import { ReportItem } from '../models/report.model';

@Injectable({ providedIn: 'root' })
export class ReportsService {
  constructor(
    private readonly http: HttpClient,
    private readonly apiUrlService: ApiUrlService
  ) {}

  listReports(): Observable<ReportItem[]> {
    return this.http.get<ReportItem[]>(this.apiUrlService.endpoint('reports'));
  }
}
