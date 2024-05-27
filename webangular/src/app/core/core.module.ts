import { LOCALE_ID, NgModule } from '@angular/core';
import { CommonModule, registerLocaleData } from '@angular/common';
import { httpInterceptorProvider } from './interceptors';
import { HeaderComponent } from '../home/components/header/header.component';
import { RouterModule } from '@angular/router';
import * as fr from '@angular/common/locales/fr';
import { HttpClientModule } from '@angular/common/http';


@NgModule({
  declarations: [],
  exports: [],
  imports: [
    RouterModule,
    CommonModule,
    HttpClientModule,
  ],
  providers: [
    { provide: LOCALE_ID, useValue: 'fr-FR'},
    httpInterceptorProvider
  ],
})
export class CoreModule {
  constructor() {
    registerLocaleData(fr.default);
  }
}
