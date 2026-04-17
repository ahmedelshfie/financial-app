import { TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { authGuard, guestGuard, roleGuard } from './auth.guard';
import { AuthService } from '../../features/auth/services/auth.service';
import { AuthorizationService } from '../services/authorization.service';

describe('auth guards', () => {
  const createUrlTree = jest.fn((commands: string[]) => ({ commands }));

  const authService = {
    isAuthenticated: jest.fn(),
    hasValidToken: jest.fn(),
    storeRedirectUrl: jest.fn(),
    currentUser: jest.fn()
  } as unknown as AuthService;

  const authorizationService = {
    hasAnyRole: jest.fn()
  } as unknown as AuthorizationService;

  const router = {
    url: '/accounts',
    createUrlTree
  } as unknown as Router;

  beforeEach(() => {
    jest.clearAllMocks();
    TestBed.configureTestingModule({
      providers: [
        { provide: AuthService, useValue: authService },
        { provide: AuthorizationService, useValue: authorizationService },
        { provide: Router, useValue: router }
      ]
    });
  });

  it('allows access for authenticated users with valid token', () => {
    (authService.isAuthenticated as unknown as jest.Mock).mockReturnValue(true);
    (authService.hasValidToken as jest.Mock).mockReturnValue(true);

    const result = TestBed.runInInjectionContext(() => authGuard({} as never, {} as never));

    expect(result).toBe(true);
  });

  it('redirects unauthenticated users to login and stores current url', () => {
    (authService.isAuthenticated as unknown as jest.Mock).mockReturnValue(false);
    (authService.hasValidToken as jest.Mock).mockReturnValue(false);

    const result = TestBed.runInInjectionContext(() => authGuard({} as never, {} as never));

    expect(authService.storeRedirectUrl).toHaveBeenCalledWith('/accounts');
    expect(createUrlTree).toHaveBeenCalledWith(['/login']);
    expect(result).toEqual({ commands: ['/login'] });
  });

  it('redirects authenticated users away from guest pages', () => {
    (authService.isAuthenticated as unknown as jest.Mock).mockReturnValue(true);
    (authService.hasValidToken as jest.Mock).mockReturnValue(true);

    const result = TestBed.runInInjectionContext(() => guestGuard({} as never, {} as never));

    expect(createUrlTree).toHaveBeenCalledWith(['/dashboard']);
    expect(result).toEqual({ commands: ['/dashboard'] });
  });

  it('blocks route when user does not have required roles', () => {
    (authService.currentUser as unknown as jest.Mock).mockReturnValue({ roles: ['ROLE_USER'] });
    (authorizationService.hasAnyRole as unknown as jest.Mock).mockReturnValue(false);

    const result = TestBed.runInInjectionContext(() => roleGuard(['ROLE_ADMIN'])({} as never, {} as never));

    expect(result).toEqual({ commands: ['/dashboard'] });
  });
});
