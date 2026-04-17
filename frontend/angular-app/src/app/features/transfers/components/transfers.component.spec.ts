import { ComponentFixture, TestBed } from '@angular/core/testing';
import { of } from 'rxjs';
import { TransfersComponent } from './transfers.component';
import { TransfersService } from '../services/transfers.service';

describe('TransfersComponent', () => {
  let fixture: ComponentFixture<TransfersComponent>;
  let component: TransfersComponent;
  let transfersService: jest.Mocked<TransfersService>;

  beforeEach(async () => {
    transfersService = {
      listTransfers: jest.fn().mockReturnValue(of([])),
      createTransfer: jest.fn().mockReturnValue(of({ message: 'ok' }))
    } as unknown as jest.Mocked<TransfersService>;

    await TestBed.configureTestingModule({
      imports: [TransfersComponent],
      providers: [{ provide: TransfersService, useValue: transfersService }]
    }).compileComponents();

    fixture = TestBed.createComponent(TransfersComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('loads transfer history on init', () => {
    expect(transfersService.listTransfers).toHaveBeenCalled();
    expect(component.transfers()).toEqual([]);
  });

  it('submits transfer after confirmation', () => {
    const payload = {
      fromAccount: 'AC-1',
      toAccount: 'AC-2',
      amount: 10,
      currency: 'USD',
      narration: 'test'
    };
    component.request = payload;

    component.submitTransfer();

    expect(transfersService.createTransfer).toHaveBeenCalledWith(payload);
    expect(component.successMessage()).toContain('ok');
  });
});
