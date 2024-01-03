import { Component } from '@angular/core';
import {FormBuilder, FormControl, FormGroup, Validators} from "@angular/forms";
import {AuthService} from "../../auth-services/auth-service/auth.service";
import {NzNotificationService} from "ng-zorro-antd/notification";

@Component({
  selector: 'app-signup',
  templateUrl: './signup.component.html',
  styleUrls: ['./signup.component.scss']
})
export class SignupComponent {
  isSpinning: boolean;
  validateForm: FormGroup;

  constructor(private service: AuthService, private fb: FormBuilder,
              private notification: NzNotificationService) {
  }

  register() {
    console.log(this.validateForm.value);
    this.service.signup(this.validateForm.value).subscribe((res) => {
      if (res.id != null) {
        console.log(res);
        this.notification.success("SUCCESS", "You're registered successfully", {nzDuration: 5000});
      } else {
        this.notification.success("ERROR", "Something went wrong", {nzDuration: 5000});
      }
    })
  }

  ngOnInit() {
    this.validateForm = this.fb.group({
      email: ["", Validators.required],
      password: ["", Validators.required],
      checkPassword: ["", [Validators.required, this.confirmationValidator]],
      name: ["", Validators.required]
    })
  }

  confirmationValidator = (control: FormControl): {[s:string]: boolean} => {
    if (!control.value) {
      return {required:true};
    } else if (control.value !== this.validateForm.controls['password'].value) {
      return {confirm: true, error: true};
    }
    return {};
  }
}
