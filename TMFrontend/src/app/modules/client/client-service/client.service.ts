import { Injectable } from '@angular/core';
import {HttpClient, HttpHeaders} from "@angular/common/http";
import {Observable} from "rxjs";
import {StorageService} from "../../../auth-services/storage-service/storage.service";

const BASIC_URL = ["http://localhost:8080/"]

@Injectable({
  providedIn: 'root'
})
export class ClientService {

  constructor(private http: HttpClient) { }

  getAllProjects(): Observable<any> {
    return this.http.get<[]>(BASIC_URL + 'api/client/projects', {
      headers: this.createAuthorizationHeader()
    });
  }

  getAllTasks(projectId: number): Observable<any> {
    return this.http.get<[]>(BASIC_URL + `api/client/projects/${projectId}/tasks`, {
      headers: this.createAuthorizationHeader()
    });
  }

  createAuthorizationHeader(): HttpHeaders {
    let authHeaders: HttpHeaders = new HttpHeaders();
    return authHeaders.set(
      "Authorization", "Bearer " + StorageService.getToken()
    );
  }
}
