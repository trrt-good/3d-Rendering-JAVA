# 3d-Rendering-JAVA

<div id="top"></div>

<!-- TABLE OF CONTENTS -->
<details>
  <summary>Table of Contents</summary>
  <ol>
    <li>
      <a href="#about-the-project">About The Project</a>
    </li>
    <li>
      <a href="#getting-started">Getting Started</a>
    <li><a href="#usage">Usage</a></li>
    <li><a href="#roadmap">Roadmap</a></li>
  </ol>
</details>

<!-- ABOUT THE PROJECT -->
## About The Project

A simple 3d rendering package in java using perspective projection. Written with built in java no opengl.   

<!-- GETTING STARTED -->
## Getting Started

Very simple to use with the rendering3d.jar which can be run with any obj file. 

### Usage

1. Clone or download the repo as zip
3. Run the rendering3d.jar with a desired `.obj` file, or with the sample files in `res` 

   ```
   java -jar rendering3d.jar
   ```
   for the demo cube or 
   ```
   java -jar rendering3d.jar <model.obj> <scale>
   ```
   for a specific model or 
   ```
   java -jar rendering3d.jar <model.obj> <texture.png> <scale>
   ```
   for a textured model 

<!-- ROADMAP -->
## Roadmap

- [ ] Barycentric coordinates
- [ ] Realistic textures
- [ ] Interpolation with vertex normals 
- [ ] Shaders 
- [ ] Materials 
