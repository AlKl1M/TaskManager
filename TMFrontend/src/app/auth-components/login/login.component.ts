import { Component } from '@angular/core';
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {AuthService} from "../../auth-services/auth-service/auth.service";
import {Router} from "@angular/router";
import {StorageService} from "../../auth-services/storage-service/storage.service";

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent {
  loginForm: FormGroup;
  isSpinning: boolean;

  constructor(private service: AuthService, private fb: FormBuilder,
              private router: Router) {}

  submitForm() {
    this.service.login(this.loginForm.value).subscribe((res) => {
      console.log(res);
      if (res.userId != null) {
        const user = {
          id: res.userId,
          role: res.userRole
        }
        console.log(user);
        StorageService.saveToken(res.jwt);
        StorageService.saveUser(user);
        if (StorageService.isClientLoggedIn()) {
          this.router.navigateByUrl("/")
        }
      } else {
        console.log("Wrong credentials")
      }
    })
  }

  ngOnInit() {
    this.loginForm = this.fb.group({
      email: [null, Validators.required],
      password: [null, Validators.required]
    })
  }
}
