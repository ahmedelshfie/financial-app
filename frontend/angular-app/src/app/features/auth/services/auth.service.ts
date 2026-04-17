import { Injectable, computed, signal } from '@angular/core';
import { HttpClient, HttpContext } from '@angular/common/http';
import { Observable, catchError, finalize, map, tap, throwError } from 'rxjs';
import { AUTH_TOKEN_KEY, REDIRECT_AFTER_LOGIN_KEY, REFRESH_TOKEN_KEY } from '../../../core/auth/token-storage.constants';
import { SKIP_AUTH } from '../../../core/tokens/http-context.tokens';
import { isJwtExpired, parseJwt } from '../../../shared/utils/jwt.utils';
import { AuthSession } from '../../../core/models/auth-session.model';
import { SessionService } from '../../../core/services/session.service';
import { ApiUrlService } from '../../../core/services/api-url.service';
import { GlobalErrorHandlerService } from '../../../core/error-handling/global-error-handler.service';

export interface AuthResponse {
  accessToken: string;
  refreshToken?: string;
  username: string;
  roles?: string[];
}

export interface LoginRequest {
  username: string;
  password: string;
}

export interface RegisterRequest {
  username: string;
  email: string;
  password: string;
  firstName?: string;
  lastName?: string;
}

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly skipAuthContext = new HttpContext().set(SKIP_AUTH, true);

  private readonly currentUserSignal = signal<AuthSession | null>(null);
  private readonly isLoadingSignal = signal(false);
  private readonly errorSignal = signal<string | null>(null);

  readonly isAuthenticated = computed(() => this.currentUserSignal() !== null);
  readonly currentUser = computed(() => this.currentUserSignal());
  readonly isLoading = computed(() => this.isLoadingSignal());
  readonly error = computed(() => this.errorSignal());

  constructor(
    private readonly http: HttpClient,
    private readonly sessionService: SessionService,
    private readonly apiUrlService: ApiUrlService,
    private readonly globalErrorHandler: GlobalErrorHandlerService
  ) {
    this.initializeAuthState();
  }

  login(credentials: LoginRequest): Observable<AuthResponse> {
    this.isLoadingSignal.set(true);
    this.errorSignal.set(null);

    return this.http.post<AuthResponse>(this.apiUrlService.endpoint('auth', 'login'), credentials, { context: this.skipAuthContext }).pipe(
      tap((response) => this.storeAuthState(response)),
      catchError((error: unknown) => {
        this.errorSignal.set(this.globalErrorHandler.toUserMessage(error, 'Unable to sign in. Please check your credentials and try again.'));
        return throwError(() => error);
      }),
      finalize(() => this.isLoadingSignal.set(false))
    );
  }

  register(userData: RegisterRequest): Observable<AuthResponse> {
    this.isLoadingSignal.set(true);
    this.errorSignal.set(null);

    return this.http.post<AuthResponse>(this.apiUrlService.endpoint('auth', 'register'), userData, { context: this.skipAuthContext }).pipe(
      tap((response) => this.storeAuthState(response)),
      catchError((error: unknown) => {
        this.errorSignal.set(this.globalErrorHandler.toUserMessage(error, 'Unable to complete registration right now. Please try again.'));
        return throwError(() => error);
      }),
      finalize(() => this.isLoadingSignal.set(false))
    );
  }

  refreshAccessToken(): Observable<string> {
    const refreshToken = this.getRefreshToken();
    if (!refreshToken) {
      return throwError(() => new Error('No refresh token available'));
    }

    return this.http
      .post<AuthResponse>(this.apiUrlService.endpoint('auth', 'refresh'), { refreshToken }, { context: this.skipAuthContext })
      .pipe(
        tap((response) => this.storeAuthState(response)),
        map((response) => response.accessToken)
      );
  }

  logout(): void {
    this.clearAuthState();
  }

  getToken(): string | null {
    return this.storageGet(AUTH_TOKEN_KEY);
  }

  getRefreshToken(): string | null {
    return this.storageGet(REFRESH_TOKEN_KEY);
  }

  hasValidToken(): boolean {
    const token = this.getToken();
    if (!token) {
      return false;
    }

    return !isJwtExpired(token);
  }

  clearError(): void {
    this.errorSignal.set(null);
  }

  storeRedirectUrl(url: string): void {
    if (typeof window !== 'undefined') {
      localStorage.setItem(REDIRECT_AFTER_LOGIN_KEY, url);
    }
  }

  consumeRedirectUrl(): string | null {
    if (typeof window === 'undefined') {
      return null;
    }

    const redirectUrl = localStorage.getItem(REDIRECT_AFTER_LOGIN_KEY);
    if (redirectUrl) {
      localStorage.removeItem(REDIRECT_AFTER_LOGIN_KEY);
    }

    return redirectUrl;
  }

  private initializeAuthState(): void {
    const token = this.getToken();
    if (!token || isJwtExpired(token)) {
      this.clearAuthState();
      return;
    }

    const payload = parseJwt(token);
    const username = payload?.sub ?? payload?.username;
    const roles = Array.isArray(payload?.['roles']) ? payload['roles'] : [];
    const expiresAtEpochSeconds = typeof payload?.exp === 'number' ? payload.exp : undefined;

    if (typeof username === 'string' && username.trim()) {
      this.currentUserSignal.set({ username, roles, expiresAtEpochSeconds });
      this.sessionService.scheduleSessionExpiry(expiresAtEpochSeconds);
      return;
    }

    this.clearAuthState();
  }

  private storeAuthState(response: AuthResponse): void {
    if (typeof window !== 'undefined') {
      localStorage.setItem(AUTH_TOKEN_KEY, response.accessToken);

      if (response.refreshToken) {
        localStorage.setItem(REFRESH_TOKEN_KEY, response.refreshToken);
      }
    }

    const payload = parseJwt(response.accessToken);
    const expiresAtEpochSeconds = typeof payload?.exp === 'number' ? payload.exp : undefined;
    const roles = response.roles ?? (Array.isArray(payload?.['roles']) ? payload['roles'] : []);

    this.currentUserSignal.set({
      username: response.username,
      roles,
      expiresAtEpochSeconds
    });
    this.sessionService.scheduleSessionExpiry(expiresAtEpochSeconds);
  }

  private clearAuthState(): void {
    if (typeof window !== 'undefined') {
      localStorage.removeItem(AUTH_TOKEN_KEY);
      localStorage.removeItem(REFRESH_TOKEN_KEY);
      localStorage.removeItem(REDIRECT_AFTER_LOGIN_KEY);
    }

    this.sessionService.clearSessionTimer();
    this.currentUserSignal.set(null);
    this.errorSignal.set(null);
  }

  private storageGet(key: string): string | null {
    if (typeof window === 'undefined') {
      return null;
    }

    return localStorage.getItem(key);
  }
}
