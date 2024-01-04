import { Component } from '@angular/core';
import {FormBuilder, FormGroup} from "@angular/forms";
import {NzButtonSize} from "ng-zorro-antd/button";
import {ClientService} from "../../client-service/client.service";

@Component({
  selector: 'app-view-all-projects',
  templateUrl: './view-all-projects.component.html',
  styleUrls: ['./view-all-projects.component.scss']
})
export class ViewAllProjectsComponent {
  projects: any = [];
  isSpinning: boolean;
  validateForm: FormGroup;
  size: NzButtonSize = 'large';

  constructor(private clientService: ClientService,
              private fb: FormBuilder) {}

  ngOnInit(): void {
    this.getAllProjects();
    console.log(this.projects);
  }

  getAllProjects(): void {
    this.projects = [];
    this.clientService.getAllProjects().subscribe((res) => {
      res.forEach(element => {
        this.projects.push(element);
      });
    });
  }
}
