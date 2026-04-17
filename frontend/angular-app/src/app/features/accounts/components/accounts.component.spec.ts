import { ComponentFixture, TestBed } from '@angular/core/testing';
import { of, throwError } from 'rxjs';
import { AccountsComponent } from './accounts.component';
import { AccountsService } from '../services/accounts.service';

describe('AccountsComponent', () => {
  let fixture: ComponentFixture<AccountsComponent>;
  let component: AccountsComponent;
  let accountsService: jest.Mocked<AccountsService>;

  beforeEach(async () => {
    accountsService = {
      getAccountsByCustomer: jest.fn()
    } as unknown as jest.Mocked<AccountsService>;

    await TestBed.configureTestingModule({
      imports: [AccountsComponent],
      providers: [{ provide: AccountsService, useValue: accountsService }]
    }).compileComponents();
  });

  it('loads accounts on init', () => {
    accountsService.getAccountsByCustomer.mockReturnValue(of([{ id: 1, accountNumber: 'A1', customerId: 1, accountTypeCode: 'CHK', currencyCode: 'USD', currentBalance: 12, availableBalance: 10, status: 'ACTIVE' }]));

    fixture = TestBed.createComponent(AccountsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();

    expect(accountsService.getAccountsByCustomer).toHaveBeenCalledWith(1);
    expect(component.accounts()).toHaveLength(1);
    expect(component.errorMessage()).toBeNull();
  });

  it('shows error message when loading fails', () => {
    accountsService.getAccountsByCustomer.mockReturnValue(throwError(() => new Error('fail')));

    fixture = TestBed.createComponent(AccountsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();

    expect(component.accounts()).toHaveLength(0);
    expect(component.errorMessage()).toContain('could not load account data');
  });
});
