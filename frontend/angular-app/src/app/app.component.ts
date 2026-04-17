import { Component, OnDestroy, OnInit, inject } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { SessionService } from './core/services/session.service';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet],
  template: '<router-outlet></router-outlet>'
})
export class AppComponent implements OnInit, OnDestroy {
  private readonly sessionService = inject(SessionService);

  ngOnInit(): void {
    this.sessionService.monitorInactivity();
  }

  ngOnDestroy(): void {
    this.sessionService.clearInactivityMonitor();
  }
}
