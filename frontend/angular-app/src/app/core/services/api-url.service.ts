import { Injectable } from '@angular/core';
import { environment } from '../../../environments/environment';
import { API_ENDPOINTS, ApiEndpointKey } from '../config/api.config';

@Injectable({ providedIn: 'root' })
export class ApiUrlService {
  private readonly apiBaseUrl = environment.apiBaseUrl.replace(/\/+$/, '');

  url(path = ''): string {
    const normalizedPath = path.replace(/^\/+/, '');
    if (!normalizedPath) {
      return this.apiBaseUrl;
    }

    return `${this.apiBaseUrl}/${normalizedPath}`;
  }

  endpoint(key: ApiEndpointKey, path = ''): string {
    const endpointUrl = this.url(API_ENDPOINTS[key]);
    const normalizedPath = path.replace(/^\/+/, '');
    return normalizedPath ? `${endpointUrl}/${normalizedPath}` : endpointUrl;
  }
}
