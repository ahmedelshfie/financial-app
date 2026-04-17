import { ErrorHandler, Injectable } from '@angular/core';
import { HttpErrorResponse } from '@angular/common/http';
import { Observable, Subject, TimeoutError, throwError } from 'rxjs';
import { ApiErrorResponse } from '../../shared/models/api-response.model';
import { FRONTEND_ERROR_MESSAGES } from './error-messages';

export enum ErrorType {
  HTTP = 'HTTP_ERROR',
  VALIDATION = 'VALIDATION_ERROR',
  AUTHENTICATION = 'AUTHENTICATION_ERROR',
  AUTHORIZATION = 'AUTHORIZATION_ERROR',
  NETWORK = 'NETWORK_ERROR',
  BUSINESS = 'BUSINESS_ERROR',
  NOT_FOUND = 'NOT_FOUND_ERROR',
  TIMEOUT = 'TIMEOUT_ERROR',
  UNKNOWN = 'UNKNOWN_ERROR'
}

export interface AppError {
  type: ErrorType;
  message: string;
  action?: string;
  originalError?: unknown;
  apiError?: ApiErrorResponse;
  statusCode?: number;
  errorCode?: string;
}

export class ApplicationError extends Error {
  constructor(
    public readonly type: ErrorType,
    message: string,
    public readonly action?: string,
    public readonly originalError?: unknown,
    public readonly apiError?: ApiErrorResponse,
    public readonly statusCode?: number,
    public readonly errorCode?: string
  ) {
    super(message);
    this.name = 'ApplicationError';
  }
}

@Injectable({ providedIn: 'root' })
export class GlobalErrorHandlerService {
  private readonly errorLog: AppError[] = [];
  private readonly maxLogSize = 100;
  private readonly userErrorSubject = new Subject<string>();

  readonly userError$ = this.userErrorSubject.asObservable();

  handleHttpError(error: HttpErrorResponse): Observable<never> {
    const appError = this.createAppErrorFromHttp(error);
    this.publishError(appError);
    return throwError(() => appError);
  }

  handleAnyError(error: unknown): Observable<never> {
    if (error instanceof HttpErrorResponse) {
      return this.handleHttpError(error);
    }

    const appError = this.createAppErrorFromUnknown(error);
    this.publishError(appError);
    return throwError(() => appError);
  }

  handleRuntimeError(error: unknown): AppError {
    const appError = this.createAppErrorFromUnknown(error);
    this.publishError(appError);
    return appError;
  }

  toUserMessage(error: unknown, fallbackMessage: string): string {
    if (error instanceof ApplicationError && error.message) {
      return error.message;
    }

    if (error instanceof HttpErrorResponse) {
      const apiError = this.extractApiError(error);
      if (apiError?.message) {
        return apiError.message;
      }
    }

    return fallbackMessage;
  }

  getErrorLog(): ReadonlyArray<AppError> {
    return [...this.errorLog];
  }

  clearErrorLog(): void {
    this.errorLog.splice(0);
  }

  isAuthenticationError(error: AppError): boolean {
    return error.type === ErrorType.AUTHENTICATION || error.statusCode === 401;
  }

  isValidationError(error: AppError): boolean {
    return error.type === ErrorType.VALIDATION || error.statusCode === 400;
  }

  private createAppErrorFromHttp(error: HttpErrorResponse): ApplicationError {
    const apiError = this.extractApiError(error);
    const detailsMessage = this.getValidationSummary(apiError);
    const status = apiError?.status ?? error.status;
    const errorCode = apiError?.errorCode;

    if (status === 0) {
      return new ApplicationError(
        ErrorType.NETWORK,
        FRONTEND_ERROR_MESSAGES.network,
        FRONTEND_ERROR_MESSAGES.networkAction,
        error,
        apiError,
        status,
        errorCode
      );
    }

    if (status === 400) {
      return new ApplicationError(
        ErrorType.VALIDATION,
        detailsMessage || apiError?.message || FRONTEND_ERROR_MESSAGES.validation,
        FRONTEND_ERROR_MESSAGES.validationAction,
        error,
        apiError,
        status,
        errorCode
      );
    }

    if (status === 401) {
      return new ApplicationError(
        ErrorType.AUTHENTICATION,
        apiError?.message || FRONTEND_ERROR_MESSAGES.authentication,
        FRONTEND_ERROR_MESSAGES.authenticationAction,
        error,
        apiError,
        status,
        errorCode
      );
    }

    if (status === 403) {
      return new ApplicationError(
        ErrorType.AUTHORIZATION,
        apiError?.message || FRONTEND_ERROR_MESSAGES.authorization,
        FRONTEND_ERROR_MESSAGES.authorizationAction,
        error,
        apiError,
        status,
        errorCode
      );
    }

    if (status === 404) {
      return new ApplicationError(
        ErrorType.NOT_FOUND,
        apiError?.message || FRONTEND_ERROR_MESSAGES.notFound,
        FRONTEND_ERROR_MESSAGES.notFoundAction,
        error,
        apiError,
        status,
        errorCode
      );
    }

    if (status === 409 || status === 422) {
      return new ApplicationError(
        ErrorType.BUSINESS,
        apiError?.message || FRONTEND_ERROR_MESSAGES.business,
        FRONTEND_ERROR_MESSAGES.businessAction,
        error,
        apiError,
        status,
        errorCode
      );
    }

    if (status === 502) {
      return new ApplicationError(
        ErrorType.HTTP,
        apiError?.message || FRONTEND_ERROR_MESSAGES.badGateway,
        FRONTEND_ERROR_MESSAGES.badGatewayAction,
        error,
        apiError,
        status,
        errorCode
      );
    }

    if (status === 503) {
      return new ApplicationError(
        ErrorType.HTTP,
        apiError?.message || FRONTEND_ERROR_MESSAGES.serviceUnavailable,
        FRONTEND_ERROR_MESSAGES.serviceUnavailableAction,
        error,
        apiError,
        status,
        errorCode
      );
    }

    if (status === 504) {
      return new ApplicationError(
        ErrorType.TIMEOUT,
        apiError?.message || FRONTEND_ERROR_MESSAGES.gatewayTimeout,
        FRONTEND_ERROR_MESSAGES.gatewayTimeoutAction,
        error,
        apiError,
        status,
        errorCode
      );
    }

    if (status >= 500) {
      return new ApplicationError(
        ErrorType.HTTP,
        FRONTEND_ERROR_MESSAGES.server,
        FRONTEND_ERROR_MESSAGES.serverAction,
        error,
        apiError,
        status,
        errorCode
      );
    }

    return new ApplicationError(
      ErrorType.HTTP,
      apiError?.message || `We could not process the request (HTTP ${status || 'unknown'}).`,
      FRONTEND_ERROR_MESSAGES.genericAction,
      error,
      apiError,
      status,
      errorCode
    );
  }

  private createAppErrorFromUnknown(error: unknown): ApplicationError {
    if (error instanceof ApplicationError) {
      return error;
    }

    if (error instanceof TimeoutError) {
      return new ApplicationError(
        ErrorType.TIMEOUT,
        FRONTEND_ERROR_MESSAGES.timeout,
        FRONTEND_ERROR_MESSAGES.timeoutAction,
        error
      );
    }

    if (error instanceof Error) {
      return new ApplicationError(
        ErrorType.UNKNOWN,
        FRONTEND_ERROR_MESSAGES.unknown,
        FRONTEND_ERROR_MESSAGES.unknownAction,
        error
      );
    }

    return new ApplicationError(
      ErrorType.UNKNOWN,
      FRONTEND_ERROR_MESSAGES.unknown,
      FRONTEND_ERROR_MESSAGES.unknownAction,
      error
    );
  }

  private extractApiError(error: HttpErrorResponse): ApiErrorResponse | undefined {
    if (typeof error.error !== 'object' || !error.error) {
      return undefined;
    }

    return error.error as ApiErrorResponse;
  }

  private getValidationSummary(apiError?: ApiErrorResponse): string | null {
    if (!apiError?.details) {
      return null;
    }

    const items = Object.entries(apiError.details)
      .map(([field, message]) => `${field}: ${message}`)
      .slice(0, 3);

    if (!items.length) {
      return null;
    }

    return `${FRONTEND_ERROR_MESSAGES.validationPrefix} ${items.join('; ')}`;
  }

  private publishError(appError: AppError): void {
    this.logError(appError);
    this.notifyUser(appError);
  }

  private logError(appError: AppError): void {
    console.error('[GlobalErrorHandler]', appError);
    this.errorLog.push(appError);

    if (this.errorLog.length > this.maxLogSize) {
      this.errorLog.shift();
    }
  }

  private notifyUser(appError: AppError): void {
    this.userErrorSubject.next(appError.message);
    console.warn('[User Notification]', appError.message);
  }
}

@Injectable({ providedIn: 'root' })
export class AppGlobalErrorHandler implements ErrorHandler {
  constructor(private readonly errorHandlerService: GlobalErrorHandlerService) {}

  handleError(error: unknown): void {
    this.errorHandlerService.handleRuntimeError(error);
  }
}
