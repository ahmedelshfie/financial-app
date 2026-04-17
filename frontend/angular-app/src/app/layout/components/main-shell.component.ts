import { CommonModule } from '@angular/common';
import { Component, inject } from '@angular/core';
import { Router, RouterOutlet } from '@angular/router';
import { AuthService } from '../../features/auth/services/auth.service';
import { HeaderComponent } from '../header/header.component';
import { SidebarComponent } from '../sidebar/sidebar.component';
import { FooterComponent } from '../footer/footer.component';
import { MAIN_MENU_ITEMS } from '../../core/constants/navigation.constants';

@Component({
  selector: 'app-main-shell',
  standalone: true,
  imports: [CommonModule, RouterOutlet, HeaderComponent, SidebarComponent, FooterComponent],
  template: `
    <div class="shell">
      <app-sidebar [menuItems]="menuItems"></app-sidebar>

      <div class="shell-content">
        <app-header [username]="authService.currentUser()?.username ?? 'Operator'" (logout)="logout()"></app-header>

        <main class="shell-main">
          <router-outlet></router-outlet>
        </main>

        <app-footer></app-footer>
      </div>
    </div>
  `
})
export class MainShellComponent {
  readonly authService = inject(AuthService);
  private readonly router = inject(Router);

  readonly menuItems = MAIN_MENU_ITEMS;

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
  }
}
