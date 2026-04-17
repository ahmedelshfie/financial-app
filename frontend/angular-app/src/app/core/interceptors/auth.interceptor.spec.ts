import { TestBed } from '@angular/core/testing';
import { HTTP_INTERCEPTORS, HttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient, withInterceptorsFromDi } from '@angular/common/http';
import { Subject, throwError } from 'rxjs';

import { AuthInterceptor } from './auth.interceptor';
import { GlobalErrorHandlerService } from '../error-handling/global-error-handler.service';
import { AuthService } from '../../features/auth/services/auth.service';
import { ApiUrlService } from '../services/api-url.service';

describe('AuthInterceptor', () => {
  let http: HttpClient;
  let httpMock: HttpTestingController;
  let authService: jest.Mocked<AuthService>;
  let errorHandler: jest.Mocked<GlobalErrorHandlerService>;

  beforeEach(() => {
    authService = {
      getToken: jest.fn(),
      refreshAccessToken: jest.fn(),
      logout: jest.fn()
    } as unknown as jest.Mocked<AuthService>;

    errorHandler = {
      handleAnyError: jest.fn((error) => throwError(() => error))
    } as unknown as jest.Mocked<GlobalErrorHandlerService>;

    TestBed.configureTestingModule({
      providers: [
        provideHttpClient(withInterceptorsFromDi()),
        provideHttpClientTesting(),
        { provide: HTTP_INTERCEPTORS, useClass: AuthInterceptor, multi: true },
        { provide: AuthService, useValue: authService },
        { provide: GlobalErrorHandlerService, useValue: errorHandler },
        { provide: ApiUrlService, useValue: { endpoint: jest.fn(() => '/api/auth') } }
      ]
    });

    http = TestBed.inject(HttpClient);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('adds bearer token to protected requests', () => {
    authService.getToken.mockReturnValue('token-123');

    http.get('/api/accounts').subscribe();

    const req = httpMock.expectOne('/api/accounts');
    expect(req.request.headers.get('Authorization')).toBe('Bearer token-123');
    req.flush({});
  });

  it('shares one refresh request across concurrent 401 responses', () => {
    authService.getToken.mockReturnValue('expired-token');
    const refreshSubject = new Subject<string>();
    authService.refreshAccessToken.mockReturnValue(refreshSubject.asObservable());

    http.get('/api/transactions').subscribe();
    http.get('/api/accounts').subscribe();

    const txReq = httpMock.expectOne('/api/transactions');
    const accReq = httpMock.expectOne('/api/accounts');

    txReq.flush({}, { status: 401, statusText: 'Unauthorized' });
    accReq.flush({}, { status: 401, statusText: 'Unauthorized' });

    refreshSubject.next('new-token');
    refreshSubject.complete();

    const retriedOne = httpMock.expectOne('/api/transactions');
    const retriedTwo = httpMock.expectOne('/api/accounts');
    expect(retriedOne.request.headers.get('Authorization')).toBe('Bearer new-token');
    expect(retriedTwo.request.headers.get('Authorization')).toBe('Bearer new-token');
    retriedOne.flush({ ok: true });
    retriedTwo.flush({ ok: true });

    expect(authService.refreshAccessToken).toHaveBeenCalledTimes(1);
  });

  it('logs out when refresh fails', () => {
    authService.getToken.mockReturnValue('expired-token');
    authService.refreshAccessToken.mockReturnValue(throwError(() => new Error('refresh failed')));

    http.get('/api/transactions').subscribe({ error: () => undefined });

    const req = httpMock.expectOne('/api/transactions');
    req.flush({}, { status: 401, statusText: 'Unauthorized' });

    expect(authService.logout).toHaveBeenCalled();
    expect(errorHandler.handleAnyError).toHaveBeenCalled();
  });
});
