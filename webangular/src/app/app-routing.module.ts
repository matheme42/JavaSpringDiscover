import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { LandingPageComponent } from './landing/components/landing-page/landing-page.component';


const routes: Routes = [
  { path: 'facesnaps', loadChildren: () => import('./facesnaps/face-snaps.module').then(m => m.FacesnapsModule) },
  { path: '', component: LandingPageComponent},
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
