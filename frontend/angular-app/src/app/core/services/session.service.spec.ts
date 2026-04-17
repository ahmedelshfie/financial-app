import { TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { SessionService } from './session.service';
import { AUTH_TOKEN_KEY, REFRESH_TOKEN_KEY } from '../auth/token-storage.constants';

describe('SessionService', () => {
  let service: SessionService;
  const navigate = jest.fn();

  beforeEach(() => {
    localStorage.clear();
    jest.useFakeTimers();
    TestBed.configureTestingModule({
      providers: [SessionService, { provide: Router, useValue: { navigate } }]
    });

    service = TestBed.inject(SessionService);
  });

  afterEach(() => {
    jest.useRealTimers();
    jest.clearAllMocks();
  });

  it('expires the session after inactivity timeout', () => {
    localStorage.setItem(AUTH_TOKEN_KEY, 'token');
    localStorage.setItem(REFRESH_TOKEN_KEY, 'refresh');

    service.monitorInactivity(10);
    jest.advanceTimersByTime(11);

    expect(localStorage.getItem(AUTH_TOKEN_KEY)).toBeNull();
    expect(localStorage.getItem(REFRESH_TOKEN_KEY)).toBeNull();
    expect(navigate).toHaveBeenCalledWith(['/login']);
  });
});
