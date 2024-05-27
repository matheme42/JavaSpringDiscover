export enum UserRole {
    USER = "USER",
    ADMIN = "ADMIN",
    REGISTER = "REGISTER"
}

export interface User {
    username : string;
    email? : string;
    password? : string;
    image? : string;
    role? : UserRole;
    logged?: boolean;
    last_connection?: Date;
}

export class PendingRequest {
    username! : string;
    image? : string;
    role? : UserRole;
}

export class InvitationRequest {
    username! : string;
    image? : string;
    role? : UserRole;
}
