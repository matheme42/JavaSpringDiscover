export enum UserRole {
    USER = "USER",
    ADMIN = "ADMIN",
    REGISTER = "REGISTER"
}

export class User {
    username! : string;
    email! : string;
    password! : string;
    image! : string;
    role! : UserRole;
}