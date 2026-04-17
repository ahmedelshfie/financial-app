import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ApiUrlService } from '../../../core/services/api-url.service';
import { AdminConfig, AdminUser } from '../models/admin.model';

@Injectable({ providedIn: 'root' })
export class AdminService {
  constructor(
    private readonly http: HttpClient,
    private readonly apiUrlService: ApiUrlService
  ) {}

  listUsers(): Observable<AdminUser[]> {
    return this.http.get<AdminUser[]>(this.apiUrlService.endpoint('admin', 'users'));
  }

  listConfigs(): Observable<AdminConfig[]> {
    return this.http.get<AdminConfig[]>(this.apiUrlService.endpoint('admin', 'configs'));
  }
}
