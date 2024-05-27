import { Injectable } from '@angular/core';
import {
  Scene,
  PerspectiveCamera,
  WebGLRenderer,
  Mesh,
  Group,
  OrthographicCamera,
} from 'three';
import { OrbitControls } from 'three/examples/jsm/controls/OrbitControls';

@Injectable({
  providedIn: 'root',
})
export class RendererService {
  scene!: Scene;
  camera!: PerspectiveCamera;
  camera2!: OrthographicCamera;
  controls!: OrbitControls;
  renderer!: WebGLRenderer;
  groundMesh!: Mesh;

  renderGroup: Group = new Group();

  constructor() {}

  import: any;
  backgroundImage: any;

  width = window.innerWidth;
  height = window.innerHeight;

  resizeRenderer(width?: number, height?: number) {
    // Mettez à jour la taille du renderer
    if (!width) {
      width = this.width;
    }
    if (!height) {
      height = this.height;
    }

    this.renderer.setSize(width, height);

    // Mettez à jour l'aspect ratio de la caméra
    this.camera.aspect = width / height;
    this.camera.updateProjectionMatrix();
  }

  get stats(): Map<string, string> {
    const stats = new Map();

    stats.set('FOV', this.camera?.fov);
    stats.set(
      'Camera position',
      `${this.camera?.position.x}, ${this.camera?.position.y}, ${this.camera?.position.z}`
    );

    return stats;
  }
}
