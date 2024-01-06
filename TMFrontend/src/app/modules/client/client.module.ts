import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { ClientRoutingModule } from './client-routing.module';
import { ViewAllProjectsComponent } from './client-components/view-all-projects/view-all-projects.component';
import {NzFormModule} from "ng-zorro-antd/form";
import {NzCardModule} from "ng-zorro-antd/card";
import { ViewAllTasksComponent } from './client-components/view-all-tasks/view-all-tasks.component';
import {NzButtonModule} from "ng-zorro-antd/button";


@NgModule({
  declarations: [
    ViewAllProjectsComponent,
    ViewAllTasksComponent
  ],
  imports: [
    CommonModule,
    ClientRoutingModule,
    NzFormModule,
    NzCardModule,
    NzButtonModule
  ]
})
export class ClientModule { }
