import { TestBed } from '@angular/core/testing';
import { ApiUrlService } from './api-url.service';

describe('ApiUrlService', () => {
  let service: ApiUrlService;

  beforeEach(() => {
    TestBed.configureTestingModule({ providers: [ApiUrlService] });
    service = TestBed.inject(ApiUrlService);
  });

  it('builds endpoint urls from the configured api base url', () => {
    expect(service.endpoint('auth', 'login')).toBe('/api/auth/login');
    expect(service.endpoint('accounts')).toBe('/api/accounts');
  });

  it('normalizes leading slashes in custom paths', () => {
    expect(service.url('/custom-path')).toBe('/api/custom-path');
  });
});
