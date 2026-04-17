import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { AccountsService } from './accounts.service';
import { environment } from '../../../../environments/environment';

describe('AccountsService', () => {
  let service: AccountsService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting(), AccountsService]
    });

    service = TestBed.inject(AccountsService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('fetches accounts by customer id', () => {
    const response = [{ id: 1, accountNumber: 'AC-1', customerId: 5, accountTypeCode: 'CHK', currencyCode: 'USD', currentBalance: 10, availableBalance: 8, status: 'ACTIVE' }];

    service.getAccountsByCustomer(5).subscribe((accounts) => {
      expect(accounts).toEqual(response);
    });

    const req = httpMock.expectOne(`${environment.apiBaseUrl}/accounts/customer/5`);
    expect(req.request.method).toBe('GET');
    req.flush(response);
  });
});
