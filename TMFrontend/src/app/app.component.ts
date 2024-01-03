import { Component } from '@angular/core';
import {StorageService} from "./auth-services/storage-service/storage.service";
import {Router} from "@angular/router";

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent {
  title = 'TMFrontend';

  isAdminLoggedIn: boolean = StorageService.isAdminLoggedIn();
  isClientLoggedIn: boolean = StorageService.isClientLoggedIn();

  constructor(private router: Router) {

  }


  ngOnInit() {
    this.router.events.subscribe(event => {
      if (event.constructor.name === "NavigationEnd") {
        this.isAdminLoggedIn = StorageService.isAdminLoggedIn();
        this.isClientLoggedIn = StorageService.isClientLoggedIn();
      }
    })
  }

  logout() {
    StorageService.signout();
    this.router.navigateByUrl("/login")
  }
}
