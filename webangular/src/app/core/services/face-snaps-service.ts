import { Injectable } from "@angular/core";
import { FaceSnap } from "../models/face-snap.model";
import { HttpClient } from "@angular/common/http";
import { Observable, map, switchMap } from "rxjs";

@Injectable({
    providedIn: 'root'
})

export class FaceSnapsService {

  constructor(private http: HttpClient) {}
      getAllFaceSnap() : Observable<FaceSnap[]> {
        return this.http.get<FaceSnap[]>("http://localhost:9000/snaps");
      }

      getFaceSnapById(id: number) : Observable<FaceSnap> {
        const faceSnap = this.http.get<FaceSnap>(`http://localhost:9000/snap/${id}`);
        if (!faceSnap) throw new Error("FaceSnap not found")
        return faceSnap;
      }

      snapFaceSnapById(id: number, snapType : 'snap' | 'unsnap') : Observable<FaceSnap> {
        return this.getFaceSnapById(id).pipe(
          map(faceSnap => ({
            ...faceSnap,
            snap : faceSnap.snap + (snapType == 'snap' ? 1 : -1)
          })),
          switchMap(updatedFaceSnap => this.http.put<FaceSnap>(`http://localhost:9000/snap/${id}`, updatedFaceSnap))
        )
      }

      addFaceSnap(formValue: { title: string, description: string, imageUrl: string, location?: string }) : Observable<FaceSnap> {
        const faceSnap: FaceSnap = {
            ...formValue,
            snap: 0,
            createdDate: new Date(),
            id: 0
        };
        return this.http.post<FaceSnap>('http://localhost:9000/snap', faceSnap)
    }
}