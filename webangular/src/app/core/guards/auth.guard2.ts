import { Injectable } from "@angular/core";
import { AuthService } from "../services/auth.service";
import { ActivatedRouteSnapshot, CanActivate, Router, RouterStateSnapshot } from "@angular/router";

@Injectable({
    providedIn: 'root'
})
export class AuthGuard2 implements CanActivate {
   
    constructor(private auth: AuthService, private router: Router) {}
   
    canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): boolean {
       if (!this.auth.getUserToken()) return true;
        this.router.navigateByUrl('/');
        return false;
    }
}