import { Component, OnInit, inject } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Observable, map, tap } from 'rxjs';
import { User, UserRole } from '../../../auth/models/user.model';
import { AuthService } from '../../../core/services/auth.service';
import { UserService } from '../../../core/services/user.service';

@Component({
  selector: 'app-profil',
  templateUrl: './profil.component.html',
  styleUrl: './profil.component.scss',
})
export class ProfilComponent implements OnInit {
  userForm!: FormGroup;

  userForm$!: Observable<User>;

  user?: User;

  errorMessage?: string;

  /* *************************************** */

  userFormPassword!: FormGroup;

  userFormPassword$!: Observable<User>;

  errorMessagePassword?: string;

  userService: UserService = inject(UserService);

  constructor(
    private formBuilder: FormBuilder,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    let currentUser: User | undefined = this.authService.getCurrentUser();
    this.userForm = this.formBuilder.group(
      {
        username: [currentUser?.username, Validators.required],
        image: [currentUser?.image == 'null' ? '' : currentUser?.image],
        email: [currentUser?.email, [Validators.email, Validators.required]],
      },
      {
        updateOn: 'blur',
      }
    );

    this.userFormPassword = this.formBuilder.group(
      {
        oldPassword: [, Validators.required],
        newPassword: [, Validators.required],
      },
      {
        updateOn: 'blur',
      }
    );

    this.userForm$ = this.userForm.valueChanges.pipe(
      map((formValue) => ({
        ...formValue,
        role: this.authService.getCurrentUser()?.role,
      })),
      tap((user) => (this.user = user))
    );
  }

  onSubmitUserForm(): void {}

  onSubmituserFormPassword(): void {
    this.userService
      .editPassword({
        newPassword: this.userFormPassword.value['newPassword'],
        oldPassword: this.userFormPassword.value['oldPassword'],
      })
      .pipe(tap((data) => this.analyseUserFormPassword(data)))
      .subscribe();
  }

  analyseUserFormPassword(data: any): void {
    if (data['error']) {
      this.errorMessagePassword = data['error'];
    } else {
      this.errorMessagePassword = undefined;
      this.userFormPassword.reset();
    }
  }
}
