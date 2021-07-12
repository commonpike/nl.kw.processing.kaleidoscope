
import nl.kw.processing.kaleidoscope.*;
import nl.kw.processing.mirror.*;

Kaleidoscope scope;

boolean debug=false;
int scopeSteps=300;

float scopeImageRotateSpeed = 2;
float scopeImageZoomSpeed   = 4;
float scopeImageZoomAmount  = 1;
float scopeRotateSpeed      = -0.75;
float scopeZoomSpeed        = 4;
float scopeZoomAmount       = 2;
  
int scopeDuration   = 1000; // msecs
long scopeStarttime = 0;   // msecs
int scopeStep=0;
float scopeImageAngle;
float scopeImageZoom;
float scopeAngle;
float scopeZoom;
  
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
  scope.setStepping(true,true);
  scope.initMirrors(intrnd(1,4)); // 3 
  scope.rotateScopeRandom();
  scope.setIterations(1); // 1
    
  PImage input = loadImage("input.png");
  scope.setImage(input);
  
}

void draw() {
  stepScope();
  output = scope.getOutput();
  image(output,0,0,width,height); 
}

void stepScope() {

        
    
    // slowly rotate the image
    scopeImageAngle+=scopeImageRotateSpeed*TWO_PI/scopeSteps;
    scope.rotateImage(scopeImageAngle);
      
    // slowly zoom the image in and out
    scopeImageZoom=1+scopeImageZoomAmount*(1+sin(scopeImageZoomSpeed*scopeStep*TWO_PI/scopeSteps));
    scope.zoomImage(scopeImageZoom);
      
    // slowly rotate the scope
    scopeAngle+=scopeRotateSpeed*TWO_PI/scopeSteps;
    scope.rotateScope(scopeAngle);
      
    // slowly zoom the scope in and out
    scopeZoom=1+scopeZoomAmount*(1+sin(scopeZoomSpeed*scopeStep*TWO_PI/scopeSteps));
    scope.zoomScope(scopeZoom);
    
    scope.processImage();
    
    scopeStep++;
    if (scopeStep>scopeSteps) {
      scopeStep=0;
    }
    if (debug) println("scopeStep",scopeStep,":",scopeSteps);

 }

int intrnd(int min, int max) { // inclusive
  int range = max - min + 1;
  // Math.random() function will return a random no between [0.0,1.0).
  int res = (int)(Math.random()*range)+min;
  return res;
}
