import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ViewAllTasksComponent } from './view-all-tasks.component';

describe('ViewAllTasksComponent', () => {
  let component: ViewAllTasksComponent;
  let fixture: ComponentFixture<ViewAllTasksComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [ViewAllTasksComponent]
    });
    fixture = TestBed.createComponent(ViewAllTasksComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
