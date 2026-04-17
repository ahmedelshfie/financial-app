import { NgModule, Optional, SkipSelf } from '@angular/core';
import { FRONTEND_ERROR_MESSAGES } from './error-handling/error-messages';

@NgModule()
export class CoreModule {
  constructor(@Optional() @SkipSelf() parentModule: CoreModule | null) {
    if (parentModule) {
      throw new Error(FRONTEND_ERROR_MESSAGES.coreModuleSingleton);
    }
  }
}
