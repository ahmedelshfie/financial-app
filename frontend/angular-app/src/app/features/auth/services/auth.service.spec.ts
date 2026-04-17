import { TestBed } from '@angular/core/testing';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';
import { AuthService } from './auth.service';
import { AUTH_TOKEN_KEY, REFRESH_TOKEN_KEY } from '../../../core/auth/token-storage.constants';
import { SessionService } from '../../../core/services/session.service';
import { environment } from '../../../../environments/environment';
import { GlobalErrorHandlerService } from '../../../core/error-handling/global-error-handler.service';
import { ApiUrlService } from '../../../core/services/api-url.service';

describe('AuthService', () => {
  let service: AuthService;
  let httpMock: HttpTestingController;

  const sessionService = {
    scheduleSessionExpiry: jest.fn(),
    clearSessionTimer: jest.fn()
  } as unknown as SessionService;

  beforeEach(() => {
    localStorage.clear();
    TestBed.configureTestingModule({
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        AuthService,
        { provide: SessionService, useValue: sessionService },
        { provide: GlobalErrorHandlerService, useValue: { toUserMessage: (_error: unknown, fallback: string) => fallback } },
        { provide: ApiUrlService, useValue: { endpoint: (_service: string, path = '') => `${environment.apiBaseUrl}/auth/${path}`.replace(/\/$/, '') } }
      ]
    });

    service = TestBed.inject(AuthService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
    localStorage.clear();
  });

  it('stores access and refresh token on login', () => {
    service.login({ username: 'demo', password: 'pass123' }).subscribe();

    const req = httpMock.expectOne(`${environment.apiBaseUrl}/auth/login`);
    req.flush({ accessToken: 'access-token', refreshToken: 'refresh-token', username: 'demo' });

    expect(localStorage.getItem(AUTH_TOKEN_KEY)).toBe('access-token');
    expect(localStorage.getItem(REFRESH_TOKEN_KEY)).toBe('refresh-token');
    expect(service.isAuthenticated()).toBe(true);
  });

  it('exposes error signal when login fails', () => {
    service.login({ username: 'demo', password: 'bad' }).subscribe({ error: () => undefined });

    const req = httpMock.expectOne(`${environment.apiBaseUrl}/auth/login`);
    req.flush({}, { status: 401, statusText: 'Unauthorized' });

    expect(service.error()).toContain('Unable to sign in');
  });

  it('clears all auth state on logout', () => {
    localStorage.setItem(AUTH_TOKEN_KEY, 'token');
    localStorage.setItem(REFRESH_TOKEN_KEY, 'refresh');

    service.logout();

    expect(localStorage.getItem(AUTH_TOKEN_KEY)).toBeNull();
    expect(localStorage.getItem(REFRESH_TOKEN_KEY)).toBeNull();
    expect(service.isAuthenticated()).toBe(false);
    expect(sessionService.clearSessionTimer).toHaveBeenCalled();
  });
});
