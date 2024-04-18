import { Component, OnInit } from '@angular/core';
import { FaceSnapsService } from '../../../core/services/face-snaps-service';
import { FaceSnap } from '../../../core/models/face-snap.model';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs/internal/Observable';
import { tap } from 'rxjs/operators';

@Component({
  selector: 'app-single-face-snap',
  templateUrl: './single-face-snap.component.html',
  styleUrl: './single-face-snap.component.scss'
})
export class SingleFaceSnapComponent implements OnInit {
  textButton! : String;
  faceSnap$!: Observable<FaceSnap>;

  constructor(private faceSnapsService: FaceSnapsService, private route: ActivatedRoute) {}

  ngOnInit(): void {
    this.textButton = "Oh Snap";
    const faceSnapId = +this.route.snapshot.params['id'];
    this.faceSnap$ = this.faceSnapsService.getFaceSnapById(faceSnapId);
  }

  onAddSnap(FaceSnapId : number) : void {
    if (this.textButton == "Oh Snap") {
      this.faceSnap$ = this.faceSnapsService.snapFaceSnapById(FaceSnapId, "snap").pipe(
        tap(() => this.textButton = "unSnap"));
      return ;
    }
    this.faceSnap$ = this.faceSnapsService.snapFaceSnapById(FaceSnapId, "unsnap").pipe(
      tap(() =>  this.textButton = "Oh Snap"));
  }
}
