import { Component, OnInit } from '@angular/core';
import { AuthService } from '../../../core/services/auth.service';
import { Router } from '@angular/router';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { BehaviorSubject, Observable, Subject, Subscriber, map, tap } from 'rxjs';
import { User, UserRole } from '../../models/user.model';

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrl: './register.component.scss'
})
export class RegisterComponent implements OnInit {

  registerForm!: FormGroup;

  registerForm$!: Observable<User>;

  errorMessage?: string;

  user? : User;
  
  constructor(private formBuilder: FormBuilder, private auth: AuthService, private router: Router) { }

  ngOnInit(): void {
    this.registerForm = this.formBuilder.group({
      username: [null, Validators.required],
      password: [null, Validators.required],
      image: [null],
      email: [null, [Validators.email, Validators.required]]
    }, {
      updateOn: 'blur'
    });

    this.registerForm$ = this.registerForm.valueChanges.pipe(
      map(formValue => ({
        ...formValue,
        role : UserRole.USER
      })),
      tap((user) => this.user = user)
    );
}

  onRegister() : void {
    if (this.user == null) return ;
    const result = this.auth.register({...this.registerForm.value, role : UserRole.USER})
    result.pipe(tap(this.parseRegisterMessage.bind(this))).subscribe();
  }

  parseRegisterMessage(map : any) : void {
    this.errorMessage = map['error'];
    if (map['token']) {
      this.auth.extractToken(map);
      this.router.navigateByUrl('/')
    }
  }
}
