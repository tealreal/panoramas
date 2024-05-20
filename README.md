# Panoramas

## What is this mod?
A shitass port of [SWDTeam's Panorama mod](https://swdteam.com/p/panorama) to Fabric with tiny improvements. The usage of this mod is the same as the original.

This mod functions the same but aims to patch the following issues to make panorama screenshots more consistent:
- Sky changes colors at different rotations
- Falling or high-velocity movement when taking a panorama
- Moving entities and other things affected by ticking

Some enhancements are provided...
- Removal of copied Minecraft code when making the cube with panorama textures displayed on the title screen
- Resolutions higher than 4096 (cuz why not)
- Cuss words in code comments

## Notes for shaders

- Turn off bloom
- Turn off vignette
- Multicolored blocklight may need to be off
- Turn off lens flare 

#### Shaders operate independently of the game, and go by frame rather than tick speed, therefore:
- Animations may need to be turned off
- Set water speed and cloud speed to 0

## Why did you just reupload SWDTeam's mod?!1/

Because they do not have a repository I can submit pull requests to and fork from. Originally, their mod was for Forge only and I needed a Fabric port, so I ported it and noticed some bugs when I brought all the code from Forge to Fabric. 

This repository will become obsolete when SWDTeam decides to adopt my changes into their mod, which is unlikely so 🤷 this fork will remain.