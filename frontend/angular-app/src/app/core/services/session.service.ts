import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { AUTH_TOKEN_KEY, REFRESH_TOKEN_KEY } from '../auth/token-storage.constants';
import { environment } from '../../../environments/environment';

@Injectable({ providedIn: 'root' })
export class SessionService {
  private timeoutId: ReturnType<typeof setTimeout> | null = null;
  private idleTimeoutId: ReturnType<typeof setTimeout> | null = null;
  private readonly inactivityEvents: Array<keyof WindowEventMap> = ['click', 'keydown', 'mousemove', 'scroll', 'touchstart'];

  constructor(private readonly router: Router) {}

  scheduleSessionExpiry(expiresAtEpochSeconds?: number): void {
    this.clearSessionTimer();
    if (!expiresAtEpochSeconds || typeof window === 'undefined') {
      return;
    }

    const remainingMs = expiresAtEpochSeconds * 1000 - Date.now();
    if (remainingMs <= 0) {
      this.expireSession();
      return;
    }

    this.timeoutId = setTimeout(() => this.expireSession(), remainingMs);
  }

  monitorInactivity(timeoutMs = environment.sessionIdleTimeoutMs): void {
    if (typeof window === 'undefined') {
      return;
    }

    this.clearInactivityMonitor();
    this.resetInactivityTimer(timeoutMs);

    this.inactivityEvents.forEach((eventName) => {
      window.addEventListener(eventName, this.handleUserActivity, { passive: true });
    });
  }

  clearSessionTimer(): void {
    if (this.timeoutId !== null && typeof window !== 'undefined') {
      clearTimeout(this.timeoutId);
    }
    this.timeoutId = null;
  }

  clearInactivityMonitor(): void {
    if (typeof window === 'undefined') {
      return;
    }

    if (this.idleTimeoutId !== null) {
      clearTimeout(this.idleTimeoutId);
      this.idleTimeoutId = null;
    }

    this.inactivityEvents.forEach((eventName) => {
      window.removeEventListener(eventName, this.handleUserActivity);
    });
  }

  expireSession(): void {
    if (typeof window !== 'undefined') {
      localStorage.removeItem(AUTH_TOKEN_KEY);
      localStorage.removeItem(REFRESH_TOKEN_KEY);
    }

    this.clearSessionTimer();
    this.clearInactivityMonitor();
    this.router.navigate(['/login']);
  }

  private readonly handleUserActivity = (): void => {
    this.resetInactivityTimer(environment.sessionIdleTimeoutMs);
  };

  private resetInactivityTimer(timeoutMs: number): void {
    if (typeof window === 'undefined') {
      return;
    }

    if (this.idleTimeoutId !== null) {
      clearTimeout(this.idleTimeoutId);
    }

    this.idleTimeoutId = setTimeout(() => this.expireSession(), timeoutMs);
  }
}
