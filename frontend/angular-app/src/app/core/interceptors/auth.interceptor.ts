import { Injectable } from '@angular/core';
import {
  HttpErrorResponse,
  HttpEvent,
  HttpHandler,
  HttpInterceptor,
  HttpRequest
} from '@angular/common/http';
import { Observable, catchError, finalize, shareReplay, switchMap, timeout } from 'rxjs';
import { GlobalErrorHandlerService } from '../error-handling/global-error-handler.service';
import { environment } from '../../../environments/environment';
import { ApiUrlService } from '../services/api-url.service';
import { AuthService } from '../../features/auth/services/auth.service';
import { SKIP_AUTH } from '../tokens/http-context.tokens';

@Injectable({ providedIn: 'root' })
export class AuthInterceptor implements HttpInterceptor {
  private refreshRequest$: Observable<string> | null = null;

  constructor(
    private readonly errorHandler: GlobalErrorHandlerService,
    private readonly authService: AuthService,
    private readonly apiUrlService: ApiUrlService
  ) {}

  intercept(request: HttpRequest<unknown>, next: HttpHandler): Observable<HttpEvent<unknown>> {
    if (request.context.get(SKIP_AUTH)) {
      return next.handle(request).pipe(
        timeout(environment.timeoutMs),
        catchError((error: unknown) => this.handleError(error))
      );
    }

    const token = this.authService.getToken();
    const authRequest = token && !this.isAuthEndpoint(request.url)
      ? this.withAuthorizationHeader(request, token)
      : request;

    return next.handle(authRequest).pipe(
      timeout(environment.timeoutMs),
      catchError((error: unknown) => {
        if (error instanceof HttpErrorResponse && error.status === 401 && !this.isAuthEndpoint(request.url)) {
          return this.handleUnauthorized(request, next, error);
        }

        return this.handleError(error);
      })
    );
  }

  private handleUnauthorized(
    request: HttpRequest<unknown>,
    next: HttpHandler,
    originalError: HttpErrorResponse
  ): Observable<HttpEvent<unknown>> {
    return this.refreshToken().pipe(
      switchMap((token) => next.handle(this.withAuthorizationHeader(request, token))),
      catchError(() => {
        this.authService.logout();
        return this.handleError(originalError);
      })
    );
  }

  private refreshToken(): Observable<string> {
    if (!this.refreshRequest$) {
      this.refreshRequest$ = this.authService.refreshAccessToken().pipe(
        finalize(() => {
          this.refreshRequest$ = null;
        }),
        shareReplay({ bufferSize: 1, refCount: true })
      );
    }

    return this.refreshRequest$;
  }

  private withAuthorizationHeader(request: HttpRequest<unknown>, token: string): HttpRequest<unknown> {
    return request.clone({ setHeaders: { Authorization: `Bearer ${token}` } });
  }

  private handleError(error: unknown): Observable<never> {
    return this.errorHandler.handleAnyError(error);
  }

  private isAuthEndpoint(url: string): boolean {
    const authBaseUrl = this.apiUrlService.endpoint('auth');
    return url.startsWith(authBaseUrl) || url.includes('/auth/');
  }
}

@Injectable({ providedIn: 'root' })
export class HeadersInterceptor implements HttpInterceptor {
  intercept(request: HttpRequest<unknown>, next: HttpHandler): Observable<HttpEvent<unknown>> {
    const modifiedRequest = request.clone({
      setHeaders: {
        Accept: 'application/json',
        'X-Requested-With': 'XMLHttpRequest'
      }
    });

    return next.handle(modifiedRequest);
  }
}
