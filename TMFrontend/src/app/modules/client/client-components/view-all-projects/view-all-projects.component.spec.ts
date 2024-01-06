import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ViewAllProjectsComponent } from './view-all-projects.component';

describe('ViewAllProjectsComponent', () => {
  let component: ViewAllProjectsComponent;
  let fixture: ComponentFixture<ViewAllProjectsComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [ViewAllProjectsComponent]
    });
    fixture = TestBed.createComponent(ViewAllProjectsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
