import { Component, OnInit, Input, input } from '@angular/core';
import { FaceSnap } from '../../../core/models/face-snap.model';
import { FaceSnapsService } from '../../../core/services/face-snaps-service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-face-snap',
  templateUrl: './face-snap.component.html',
  styleUrl: './face-snap.component.scss'
})
export class FaceSnapComponent implements OnInit {
  
  textButton! : String;
  @Input() faceSnap!: FaceSnap;

  constructor(private faceSnapsService: FaceSnapsService, private route : Router) {}

  ngOnInit(): void {
    this.textButton = "Oh Snap";
  }

  onViewFaceSnap() : void {
    this.route.navigateByUrl("facesnaps/" + this.faceSnap.id.toString())
  }
}
