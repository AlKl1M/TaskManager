import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import {ViewAllProjectsComponent} from "./client-components/view-all-projects/view-all-projects.component";
import {ViewAllTasksComponent} from "./client-components/view-all-tasks/view-all-tasks.component";

const routes: Routes = [
  {path: "projects", component: ViewAllProjectsComponent},
  {path: "projects/:projectId/tasks", component: ViewAllTasksComponent}
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class ClientRoutingModule { }
