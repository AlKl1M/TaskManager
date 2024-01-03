import { Injectable } from '@angular/core';

const TOKEN = 'token';
const USER = 'user';

@Injectable({
  providedIn: 'root'
})
export class StorageService {

  constructor() { }

  static getUserId(): String {
    const user = this.getUser();
    if (user == null) {
      return '';
    }
    return user.id;
  }

  static saveToken(token: string): void {
    window.localStorage.removeItem(TOKEN);
    window.localStorage.setItem(TOKEN, token);
  }

  static saveUser(user: any): void {
    window.localStorage.removeItem(USER);
    window.localStorage.setItem(USER, JSON.stringify(user));
  }

  static getToken() : string {
    return localStorage.getItem(TOKEN);
  }

  static getUser(): any {
    return JSON.parse(localStorage.getItem(USER));
  }

  static getUserRole(): string {
    const user = this.getUser();
    if (user == null) {
      return '';
    }
    return user.role;
  }

  static isAdminLoggedIn(): boolean {
    if (this.getToken() === null) {
      return false;
    }
    const role: string = this.getUserRole();
    return role == "ADMIN";
  }

  static isClientLoggedIn(): boolean {
    if (this.getToken() === null) {
      return false;
    }
    const role: string = this.getUserRole();
    return role == "CLIENT";
  }

  static signout(): void {
    window.localStorage.removeItem(USER);
    window.localStorage.removeItem(TOKEN);
  }
}
