import { Component, OnInit } from '@angular/core';
import { FaceSnap } from '../../../core/models/face-snap.model';
import { FaceSnapsService } from '../../../core/services/face-snaps-service';
import { Router } from '@angular/router';
import { Observable } from 'rxjs';

@Component({
  selector: 'app-face-snap-list',
  templateUrl: './face-snap-list.component.html',
  styleUrl: './face-snap-list.component.scss'
})
export class FaceSnapListComponent implements OnInit {
  faceSnaps$! : Observable<FaceSnap[]>

  constructor(private faceSnapsService: FaceSnapsService) {}

  ngOnInit(): void {
    this.faceSnaps$ = this.faceSnapsService.getAllFaceSnap();
  }
}
