# 3d-Rendering-JAVA

![alt text](https://cdn.discordapp.com/attachments/903515599097954354/1008136588947095572/unknown.png)

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

A simple 3d rendering program in java and integrates smoothly with the swing api.    

<!-- GETTING STARTED -->
## Getting Started

Very simple to use with the rendering3d.jar which can be run with any .obj file. 

### Usage

1. Clone or download the repo as zip
3. Run the rendering3d.jar with a desired `.obj` file, or with the sample files in `res` 

  for the demo cube: 
   ```
   java -jar rendering3d.jar
   ```
  for a specific model:
   ```
   java -jar rendering3d.jar <model.obj> <scale>
   ```
  for a textured model: 
   ```
   java -jar rendering3d.jar <model.obj> <texture.png> <scale>
   ```
    

<!-- ROADMAP -->
## Roadmap

- [ ] Barycentric coordinates
- [ ] Realistic textures
- [ ] Interpolation with vertex normals 
- [ ] Shaders 
- [ ] Materials 
