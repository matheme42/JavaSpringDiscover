import { Injectable } from "@angular/core"

@Injectable({
    providedIn: 'root'
})

export class AuthService {
    private tokenName = "X-API-KEY"
    private token = "Baeldung"

    getToken() : string {
        return this.token;
    }

    getTokenName() : string {
        return this.tokenName;
    }
}