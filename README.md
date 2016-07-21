This project is a collections of playable LibGDX [Screens](https://libgdx.badlogicgames.com/nightlies/docs/api/com/badlogic/gdx/Screen.html), each demonstrating a feature that can be built with the framework.

[Runnable HTML5 build](http://dev.harpoonandhatchet.com/combat-demo/)
### Screens

* [CelShaderScreen](core/src/com/hh/gdxtutorial/screens/CelShaderScreen.java)

	A screen featuring a number of spinning reddish cubes drawn with a cel rendering process.

* [GaussianBlurShaderScreen](core/src/com/hh/gdxtutorial/screens/GaussianBlurShaderScreen.java)

	A screen featuring a number of spinning reddish cubes with a Gaussian blur post process applied to the finished product.

* [TiltShiftShaderScreen](core/src/com/hh/gdxtutorial/screens/TiltShiftShaderScreen.java)

	The red blocks, again, this time with a fake tilt shift post process applied. It blurs a configurable percentage of the top and bottom viewport pixels. The blur is strongest at the very top and bottom of the viewport and fades as it moves towards the center. It looks move convincing viewed from some angles than others.

* [TurnEngineScreen](core/src/com/hh/gdxtutorial/screens/TurnEngineScreen.java)

	A screen featuring 5 spheres that take turns moving on top of a plane. The player controls the green sphere and goes first. Left click sets the green sphere's target during player control.

* [TurnSystemScreen](core/src/com/hh/gdxtutorial/screens/TurnSystemScreen.java)

	5 spheres again. This is another implementation of a turn-based screen, this time using the [Ashley](https://github.com/libgdx/ashley) entity system to manage passing of turn control between the player and computer controlled spheres.
