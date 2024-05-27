import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { LoginComponent } from './components/login/login.component';
import { RegisterComponent } from './components/register/register.component';
import { AuthGuard2 } from '../core/guards/auth.guard2';

const routes: Routes = [
  { path: 'login', component: LoginComponent, canActivate: [AuthGuard2] },
  { path: 'register', component: RegisterComponent, canActivate: [AuthGuard2] }
];

@NgModule({
  imports: [
    RouterModule.forChild(routes)
  ],
  exports: [
    RouterModule
  ]
})
export class AuthRoutingModule {}