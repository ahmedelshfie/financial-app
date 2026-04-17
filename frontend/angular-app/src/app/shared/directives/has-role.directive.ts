import { Directive, Input, TemplateRef, ViewContainerRef, inject } from '@angular/core';
import { AuthService } from '../../features/auth/services/auth.service';
import { AuthorizationService } from '../../core/services/authorization.service';

@Directive({
  selector: '[appHasRole]',
  standalone: true
})
export class HasRoleDirective {
  private readonly templateRef = inject(TemplateRef<unknown>);
  private readonly viewContainer = inject(ViewContainerRef);
  private readonly authService = inject(AuthService);
  private readonly authorizationService = inject(AuthorizationService);

  @Input({ required: true })
  set appHasRole(requiredRoles: string[]) {
    this.viewContainer.clear();
    if (this.authorizationService.hasAnyRole(this.authService.currentUser()?.roles, requiredRoles)) {
      this.viewContainer.createEmbeddedView(this.templateRef);
    }
  }
}
