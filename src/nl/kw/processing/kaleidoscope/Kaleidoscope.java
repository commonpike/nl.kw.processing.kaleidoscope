package nl.kw.processing.kaleidoscope;


import processing.core.*;
import nl.kw.processing.mirror.*;

import java.util.List;
import java.util.ArrayList; 

/**
* A Processing utility to apply several mirrors to a single PImage.
* Requires nl.kw.nl.processing.mirror.*
*
* `scope.setImage()` sets the input image and `scope.getOutput()`
* returns the output. 
* 
* You can add your own mirrors using `addMirror(x,y,alpha)`, but you can 
* also conveniently call `initMirrors(n)` where n is the amount of 
* mirrors; they will be evenly spaced rotated around the center 
* of the kaleidscope.
*
* A real kaleidoscope has an infinite number of iterations (of 
* mirages of mirages), but your PC has limited time. Depending 
* on the setup of your mirrors, 3 iterations is often enough.
* Set it using `setIterations()`.
*/

public class Kaleidoscope {
  
  public boolean debug=false;
  private boolean processing=false;
  private boolean processed=false;
  
  private int iterations=3;
  private double radius;
  private double width;
  private double height;
  private double outer;
  private int tint;
  private int bgcolor=0x00000000;
  
  private List<Mirror> mirrors= new ArrayList<Mirror>();
  
  private PImage original;
  private PImage image;
  private double imgzoom;
  private double orgzoom;
  private double imgangle;
  private PVector imgcenter=new PVector(0,0);
  
  private double scopezoom=1;
  private double scopeangle=0;
  private PVector scopecenter=new PVector(0,0);
  
  private PGraphics output;
  private PGraphics cropped;
  
  private static final int MIRIMG = 0;
  private static final int MIRPIX = 1;
  private static final int MIRARR = 2;
  private int method=MIRIMG;

  private boolean stepping=false;
  private boolean firststep=true;
  private boolean updateWhileStepping=true;
  
  private int step=0;
  private boolean ready=true; // able to receive image
  
  private PApplet applet;
  
  public Kaleidoscope(PApplet applet) {
    this(applet,3);
  }
  public Kaleidoscope(PApplet applet, int amount) {
    this(applet,amount,applet.width,applet.height);
  }
  public Kaleidoscope(PApplet applet, int amount, double width, double height) {
    this.applet = applet;
    this.setDimensions(width,height);
    this.initMirrors(amount);
  }
  
  public void setDimensions(double width,double height) {
    this.width=width;
    this.height=height;
    this.outer = Math.sqrt(Math.pow(width,2)+Math.pow(height,2));
    output = applet.createGraphics((int)Math.ceil(outer),(int)Math.ceil(outer));
    cropped = applet.createGraphics((int)Math.ceil(width),(int)Math.ceil(height));
  }
  
  public void initMirrors(int amount) {
    this.mirrors.clear();
    this.radius=Math.min(width,height)/12;
    for (int i=0; i<amount;i++) {
        double alpha = i*PConstants.TWO_PI/amount;
        this.addMirror(radius*Math.sin(alpha),radius*Math.cos(alpha),PConstants.TWO_PI-alpha);
    }
  }
  
  
  
  // ------------
  
  public void setTint(int tint) {
      this.tint=tint;
      for (Mirror mirror : mirrors) {
        mirror.setTint(tint);
      }
  }
  public int getTint() {
    return tint;
  }
  
  public void setBGColor(int bgcolor) {
      this.bgcolor=bgcolor;
      for (Mirror mirror : mirrors) {
        mirror.setBGColor(bgcolor);
      }
  }
  public int getBGColor() {
    return bgcolor;
  }
  
  public void setIterations(int i) {
    this.iterations=i;
  }
  public int getIterations() {
    return iterations;
  }
  public void setMethod(String m) {
    switch (m) {
       case "image":
       case "MIRIMG":
         setMethod(MIRIMG);
         break;
       case "pixel":
       case "MIRPIX":
         setMethod(MIRPIX);
         break;
       case "array":
       case "MIRARR":
         setMethod(MIRARR);
         break;
    }
  }
  
  public void setMethod(int m) {
    this.method=m;
  }
  public int getMethod() {
    return method;
  }
  
  public void setStepping(boolean stepping) {
    setStepping(stepping,false);
  }
  
  public void setStepping(boolean stepping, boolean updateWhileStepping) {
    this.stepping=stepping;
    this.updateWhileStepping=updateWhileStepping;
    this.firststep=true;
    this.ready=true;
  }
  
  public boolean getStepping() {
    return stepping;
  }
  public int getStep() {
    return step;
  }
  public boolean isReady() {
    return ready;
  }
  
  // ------------
  
  public Mirror addMirror(double x, double y, double alpha) {
     if (debug) applet.println("addmirror",x,y,Math.toDegrees(alpha));
     if (this.stepping && !this.ready) {
       if (!this.updateWhileStepping) {
        if (debug) applet.println("addmirror","not updating while stepping");
         return null;  
       }
     }
     Mirror mirror = new Mirror(this.applet,(float)x,(float)y,(float)alpha);
     if (this.bgcolor!=0) mirror.setBGColor(this.bgcolor);
     if (this.tint!=0) mirror.setTint(this.tint);
     this.mirrors.add(mirror);
     return mirror;
  }
  public List<Mirror> getMirrors() {
    return mirrors;
  }
  public Mirror getMirror(int i) {
    return mirrors.get(i);
  }
  
  // ------------

  public void setImage(PImage image) {
      //println("setimage "+image);
      if (this.stepping && !this.ready) {
         if (!this.updateWhileStepping) {
           return; 
         }
      }
      this.original=image;
      this.orgzoom=outer/Math.min(original.width,original.height);
      zoomImage(1);
      rotateImage(0);
  }
  
  public void processImage() {
     
    // just a check in the case you'd
    // want to run this threaded. 
    // but you cant.
    
    if (!processing) {
      
      processing=true;
      processed=false;
      
      if (!stepping || step==0) {
        // rotate the image from the center
        output.beginDraw();
        output.clear();
        output.translate(output.width/2,output.height/2);
        output.rotate((float)imgangle);
        //println(imgangle);
        output.translate(-output.width/2,-output.height/2);
        output.copy(image,(image.width-output.width)/2,(image.height-output.height)/2,output.width,output.height,0,0,output.width,output.height);
        //output.image(image,(output.width-image.width)/2,(output.height-image.height)/2);
        //output.set((output.width-image.width)/2,(output.height-image.height)/2,image);
        output.endDraw();
      }
      
      if (!stepping || firststep) {
        
        switch (method) {
          
          case MIRIMG:
            // apply mirrors (uses output.image(mirage))
            
            
            for (int i=0; i<iterations; i++) {
              for (Mirror mirror : mirrors) {
                  mirror.drawMirage(output);
              }
            }
            
            break;
            
          case MIRPIX:
            // loop pixels, pass vector (uses get() and set())
            
            for (int i=0; i<iterations; i++) {
              for (Mirror mirror : mirrors) {
                for (int x=0; x < output.width; x++) {
                  for (int y=0; y < output.height; y++) {
                    mirror.drawMirage(output,x-output.width/2,y-output.height/2);
                  }
                }
              }
            }

            break;
            
          case MIRARR:
            // loop pixels, pass index (uses output.pixels[])
            
            for (int i=0; i<iterations; i++) {
              output.loadPixels();
              for (Mirror mirror : mirrors) {
                for (int pc=0; pc < output.pixels.length; pc++) {
                  mirror.drawMirage(output,pc,true);
                }
              }
              output.updatePixels();
            }
            
            
            break;
          default:
            throw new RuntimeException("Kaleidoscope: unknown method "+method);
        }
        
        ready=true;
        
      } else {
        
        if (mirrors.size()==0) {
          // makes no sense
          for (Mirror mirror : mirrors) {
             applet.println("Err?",mirror);
          }
          applet.println("Err?",stepping,firststep); // true, true
          throw new RuntimeException("Kaleidoscope: no mirrors ?");
        }
        
        int midx = step%mirrors.size();
        //println("step",step,"mirr",midx);
        Mirror mirror = mirrors.get(midx);
        
        switch (method) {
          
          case MIRIMG:
          
            // apply mirrors (uses output.image(mirage))
            mirror.drawMirage(output);
             
            break;
            
          case MIRPIX:
          
            // loop pixels, pass vector (uses get() and set())
            for (int x=0; x < output.width; x++) {
              for (int y=0; y < output.height; y++) {
                mirror.drawMirage(output,x-output.width/2,y-output.height/2);
              }
            }

            break;
            
          case MIRARR:
            // loop pixels, pass index (uses output.pixels[])
            for (int pc=0; pc < output.pixels.length; pc++) {
              mirror.drawMirage(output,pc,true);
            }
            break;
          default:
            throw new RuntimeException("Kaleidoscope: unknown method "+method);
        }
        
        step++;
        if (step==iterations*mirrors.size()) {
          //println("ready");
          step=0;
          ready=true;
        } else {
          ready=false; 
        }
      
      } 
      
      if (ready) {
        
        // crop result
        cropped.beginDraw();
        //cropped.copy(output.get(),(output.width-cropped.width)/2,(output.height-cropped.height)/2,cropped.width,cropped.height,0,0,cropped.width,cropped.height);
        //cropped.image(output,(cropped.width-output.width)/2,(cropped.height-output.height)/2,output.width,output.height);
        cropped.set((cropped.width-output.width)/2,(cropped.height-output.height)/2,output);
        cropped.endDraw();
        firststep=false;
      
      }
      
      processing=false;
      processed=true;
      
    } else {
      throw new RuntimeException("Kaleidoscope: already processing");
    }
  }
  
  public PImage getOutput() {
    if (!processed) {
      processImage();
    }
    processed=false;
    return cropped;
  }
  
  // ------------
  
  public double getImageZoom() {
    return this.imgzoom;
  }
  
  public void zoomImage(double zoom) {
    zoomImage(zoom,false);
  }
  
  public void zoomImage(double zoom, boolean rel) {
    if (this.stepping && !this.ready) {
      if (!this.updateWhileStepping) {
        return;
      }
    }
    if (zoom>0) {
      if (rel) zoom = zoom*this.imgzoom;
      this.imgzoom = zoom;
      image=original.get();
      image.resize((int)Math.round(orgzoom*imgzoom*original.width),(int)Math.round(orgzoom*imgzoom*original.height));
    } else {
       // fail silent 
    }
  }
  
  public double getImageAngle() {
    return this.imgangle;
  }
  
  public void rotateImage(double angle) {
    rotateImage(angle,false);
  }
  
  public void rotateImage(double angle, boolean rel) {
    if (this.stepping && !this.ready) {
      if (!this.updateWhileStepping) {
        return;
      }
    }
    if (rel) angle = angle+this.imgangle;
    this.imgangle = angle % PConstants.TWO_PI;
  }
  
  // ------------
  
  public double getScopeZoom() {
    return this.scopezoom;
  }
  
  public void zoomScope(double zoom) {
    zoomScope(zoom,false);
  }
  
  public void zoomScope(double zoom, boolean rel) {
    if (this.stepping && !this.ready) {
      if (!this.updateWhileStepping) {
        return;
      }
    }
    if (rel) zoom = zoom*this.scopezoom;
    for (Mirror mirror: mirrors) {
      double mx = zoom*mirror.getX()/this.scopezoom;
      double my = zoom*mirror.getY()/this.scopezoom;
      mirror.position((float)mx,(float)my);   
    }
    this.scopezoom=zoom;
  }
  
  public double getScopeAngle() {
    return this.imgangle;
  }
  
  
  
  public void rotateScope(double angle) {
    rotateScope(angle,false);
  }
  
  public void rotateScope(double angle, boolean rel) {
    if (this.stepping && !this.ready) {
      if (!this.updateWhileStepping) {
        return;
      }
    }
    if (!rel) {
      angle = angle-this.scopeangle;
    }
    //println(angle);
    for (Mirror mirror: mirrors) {
      mirror.rotate((float)angle);   
    }
    this.scopeangle += angle;
    this.scopeangle %= PConstants.TWO_PI;
  }
  
  public void rotateScopeRandom() {
    int refidx = (int)Math.floor(Math.random()*mirrors.size());
    Mirror refmir = this.mirrors.get(refidx);
    double refang = refmir.getAlpha();
    if (2*Math.random()>1) {
      refang+=PConstants.HALF_PI;
    } else {
      refang-=PConstants.HALF_PI;
    }
    rotateScope(refang);
  }

}
  
