import { Injectable } from "@angular/core";
import { FaceSnap } from "../models/face-snap.model";
import { HttpClient } from "@angular/common/http";
import { Observable, map, switchMap } from "rxjs";

@Injectable({
    providedIn: 'root'
})

export class FaceSnapsService {

  constructor(private http: HttpClient) {}

    private faceSnaps: FaceSnap[] = [
        {
          id: 1,  
          title: 'Archibald',
          description: 'Mon meilleur ami depuis tout petit !',
          imageUrl: 'https://cdn.pixabay.com/photo/2015/05/31/16/03/teddy-bear-792273_1280.jpg',
          createdDate: new Date(),
          snap: 0,
          location: "dubay"
        },
        {
          id: 2,  
          title: 'Three Rock Mountain',
          description: 'Un endroit magnifique pour les randonnées.',
          imageUrl: 'https://upload.wikimedia.org/wikipedia/commons/thumb/0/08/Three_Rock_Mountain_Southern_Tor.jpg/2880px-Three_Rock_Mountain_Southern_Tor.jpg',
          createdDate: new Date(),
          snap: 0
        },
        {
          id: 3, 
          title: 'Un bon repas',
          description: 'Mmmh que c\'est bon !',
          imageUrl: 'https://wtop.com/wp-content/uploads/2020/06/HEALTHYFRESH.jpg',
          createdDate: new Date(),
          snap: 200
        }
      ];

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