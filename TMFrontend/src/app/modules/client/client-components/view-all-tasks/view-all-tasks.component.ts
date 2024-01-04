import { Component } from '@angular/core';
import {FormBuilder, FormGroup} from "@angular/forms";
import {NzButtonSize} from "ng-zorro-antd/button";
import {ClientService} from "../../client-service/client.service";
import {ActivatedRoute} from "@angular/router";

@Component({
  selector: 'app-view-all-tasks',
  templateUrl: './view-all-tasks.component.html',
  styleUrls: ['./view-all-tasks.component.scss']
})
export class ViewAllTasksComponent {
  tasks: any = [];
  projectId: number = this.activatedRoute.snapshot.params["projectId"];
  isSpinning: boolean;
  validateForm: FormGroup;
  size: NzButtonSize = 'large';

  constructor(private activatedRoute: ActivatedRoute,
              private clientService: ClientService,
              private fb: FormBuilder) {
  }

  ngOnInit() {
    this.getAllProjects();
  }

  getAllProjects(): void {
    this.tasks = [];
    this.clientService.getAllTasks(this.projectId).subscribe((res) => {
      res.forEach(element => {
        this.tasks.push(element);
      });
    });
    console.log(this.tasks);
  }
}
