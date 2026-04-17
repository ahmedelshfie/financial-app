import { HttpErrorResponse } from '@angular/common/http';
import { GlobalErrorHandlerService, ErrorType } from './global-error-handler.service';
import { FRONTEND_ERROR_MESSAGES } from './error-messages';

describe('GlobalErrorHandlerService', () => {
  let service: GlobalErrorHandlerService;

  beforeEach(() => {
    service = new GlobalErrorHandlerService();
    jest.spyOn(console, 'error').mockImplementation(() => {});
    jest.spyOn(console, 'warn').mockImplementation(() => {});
  });

  afterEach(() => {
    jest.restoreAllMocks();
  });

  it('maps 400 responses to validation errors', (done) => {
    const error = new HttpErrorResponse({
      status: 400,
      statusText: 'Bad Request',
      error: { message: 'Validation failed', details: { username: 'required' } }
    });

    service.handleHttpError(error).subscribe({
      error: (appError) => {
        expect(appError.type).toBe(ErrorType.VALIDATION);
        expect(appError.message).toContain('Please fix:');
        done();
      }
    });
  });

  it('records runtime errors in error log', () => {
    service.handleRuntimeError(new Error('boom'));

    expect(service.getErrorLog()).toHaveLength(1);
    expect(service.getErrorLog()[0].message).toContain('unexpected error occurred');
  });

  it('maps 502 responses to a gateway-specific server message', (done) => {
    const error = new HttpErrorResponse({
      status: 502,
      statusText: 'Bad Gateway'
    });

    service.handleHttpError(error).subscribe({
      error: (appError) => {
        expect(appError.type).toBe(ErrorType.HTTP);
        expect(appError.message).toBe(FRONTEND_ERROR_MESSAGES.badGateway);
        expect(appError.action).toBe(FRONTEND_ERROR_MESSAGES.badGatewayAction);
        done();
      }
    });
  });

  it('maps 504 responses to timeout errors', (done) => {
    const error = new HttpErrorResponse({
      status: 504,
      statusText: 'Gateway Timeout'
    });

    service.handleHttpError(error).subscribe({
      error: (appError) => {
        expect(appError.type).toBe(ErrorType.TIMEOUT);
        expect(appError.message).toBe(FRONTEND_ERROR_MESSAGES.gatewayTimeout);
        expect(appError.action).toBe(FRONTEND_ERROR_MESSAGES.gatewayTimeoutAction);
        done();
      }
    });
  });
});
