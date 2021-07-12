
import nl.kw.processing.mirror.*;
import nl.kw.processing.kaleidoscope.*;


Kaleidoscope scope;

float rotation=PI;
float speed=0.01;
  
PImage output;

void settings() {
  //size(900,900,P2D);
  //size(900,900,P3D); // bit faster
  size(300,300,FX2D); // bit faster
  //size(900,900,JAVA2D); // same as fx2d
  //fullScreen(FX2D);

}

void setup() {
  
  float ratio=(float)width/(float)height;
  scope = new Kaleidoscope(this);
  scope.setDimensions(300,300/ratio);
  scope.setMethod("image"); // "image","pixel","array"
  scope.initMirrors(intrnd(1,4)); 
  scope.setIterations(3); 
    
  PImage input = loadImage("input.png");
  scope.setImage(input);
  
}

void draw() {
  rotation=rotation+speed%TWO_PI;
  scope.rotateScope(rotation);
  output = scope.getOutput();
  image(output,0,0,width,height); 
}



int intrnd(int min, int max) { // inclusive
  int range = max - min + 1;
  // Math.random() function will return a random no between [0.0,1.0).
  int res = (int)(Math.random()*range)+min;
  return res;
}
