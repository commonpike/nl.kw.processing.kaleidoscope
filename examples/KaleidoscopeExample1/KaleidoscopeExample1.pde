
import nl.kw.processing.mirror.*;
import nl.kw.processing.kaleidoscope.*;

Kaleidoscope scope;

void settings() {
  size(900,900,FX2D); 
}

void setup() {
  scope = new Kaleidoscope(this);
  scope.setImage(loadImage("input.png"));
}

void draw() {
  PImage output = scope.getOutput();
  image(output,0,0,width,height); 
  noLoop();
}
