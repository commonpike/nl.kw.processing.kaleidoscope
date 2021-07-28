202107*pike

# Processing3 Kaleidoscope

A Processing utility to apply several mirrors to a single PImage,
thus creating a kaleidoscope. Requires nl.kw.nl.processing.mirror.*

`scope.setImage()` sets the input image and `scope.getOutput()`
returns the output. 

You can add your own mirrors using `addMirror(x,y,alpha)`, but you can 
also conveniently call `initMirrors(n)` where n is the amount of 
mirrors; they will be evenly spaced rotated around the center 
of the kaleidscope.

A real kaleidoscope has an infinite number of iterations (of 
mirages of mirages), but your PC has limited time. Depending 
on the setup of your mirrors, 3 iterations is often enough.
Set it using `setIterations()`.

To see what you can do with this, watch some of the animations at
https://www.instagram.com/studio.pike

## Installation

You may be able to install this through the Processing IDE.
It should end up your "sketchbook location",
also sometimes called the "processing library directory".
You can find its location in the Processing app, in the menu,
under preferences. It's usually in your homedir somewhere.

Or you can download it from GIT, and put it in that folder
yourself: 
<https://github.com/commonpike/nl.kw.processing.kaleidoscope>
locally rename the repo folder to 'Kaleidoscope'.

Once it's there, choose 'sketch > import library'
from the menu bar.

## Folder structure

```
- library
    The only folder you need, containing the jar file
- library.properties
		The properties file for P3
- README.md 
    This file
- docs
    Documentation; an HTML summary
- examples
    Example PDEs
- reference
    HTML Javadocs
- src
    Java source files
    
The following files are only available on github:

- dist
    Distribution files; the ZIP    
- build
    Compiled java classes
- bin
    Some goodies I use for maintenance
    
```

## Feedback & Problems 

If you have problems, questions, suggestions or
additions, contact me.


pike-processing@kw.nl