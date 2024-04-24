import { Injectable } from "@angular/core"
import { User } from "../../auth/models/user.model";
import { Observable, catchError, map, of, pipe, tap, throwError } from "rxjs";
import { HttpClient, HttpErrorResponse } from "@angular/common/http";
import { Login } from "../../auth/models/login.model";
import { Router } from "@angular/router";

@Injectable({
    providedIn: 'root'
})

export class AuthService {

    /// token needed to authenticate the backend
    private tokenName = "X-API-KEY"
    private token = "Baeldung"

    private userToken : string | null | undefined;


    getToken() : string {
        return this.token;
    }

    getTokenName() : string {
        return this.tokenName;
    }

    getUserToken() : string | undefined | null {
        if (this.userToken == null || this.userToken == undefined) {
           this.userToken = localStorage.getItem('currentUser');
        }
        return this.userToken;
    }

    constructor(private http: HttpClient) {}


    register(user : User) : Observable<Map<string, string>> {
        return this.http.post<Map<string, string>>("http://localhost:9000/register", user).pipe(
            catchError((error : HttpErrorResponse) => of({...error.error, status: error.status} as Map<string, string>))
        )
    }

    login(login : Login) : Observable<Map<string, string>> {
        return this.http.post<Map<string, string>>("http://localhost:9000/login", login).pipe(
            catchError((error : HttpErrorResponse) => of({...error.error, status: error.status} as Map<string, string>)),
            tap(this.extractToken)
        )
    }

    logout() : Observable<Map<string, string>> {
        return this.http.get<Map<string, string>>("http://localhost:9000/logout").pipe(
            catchError((error : HttpErrorResponse) => of({...error.error, status: error.status} as Map<string, string>)),
        )
    }

    cleanUserToken() : void {
        this.userToken = null;
        localStorage.removeItem('currentUser');
    }

    extractToken(map: any) : void {
        if (map['token'] != null) {
            localStorage.setItem('currentUser', map['token']);
        }
    }
}