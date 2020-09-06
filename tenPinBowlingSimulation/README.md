# Simple Ten Pins Bowling Simulator.
> 
>
> * This is a simple game simulation exercise to virtually generate scores for Ten-Pins bowling game.
>Simply create an object of class Game and invoke startGame function.
>There is another variant  to start the game by invoking startGameV2. startGameV2 gives more control in terms of simulating and generating random scenarios.

## Installation

### Build

In project directory
```sh
mvn clean
mvn install
```

### Test

To run the tests
```sh
mvn clean
mvn test
```

### Build Artifact
To build project jar
```sh
mvn clean install
```
Will produce jar in the target folder

## Contributing to the project
Please feel free to fork the repository. Any suggestions to further the simulation will be greatly appreciated.

### Run the simulation
More the main function create an object of class Game.
```scala
//Running in full automated mode - Version1
    val gameObj = new Game()
    gameObj.startGame()
    val finalScore = gameObj.score
    logger.info(s"****** FINAL SCORE:[$finalScore] ********")

//Running in pseudo mode using startGameV2 method to pass firstRoll and SecondRoll at runtime
    val gameObj2 = new Game()
    for (counter <- 1 to 10) {
      val firstRoll = gameObj.randomNumber(Constants.MAX_PINS)
      val secondRoll = gameObj.randomNumber(Constants.MAX_PINS - firstRoll)
      gameObj2.startGameV2(firstRoll, secondRoll)
    }
    logger.info(s"****** FINAL SCORE:[${gameObj2.score}] ********")
```
## Project Structure

```
src.main
        ten.pin.bowling
            └───exception
            └───helper
            └───simulator
    .test
        ten.pin.bowling
            └───exception
            └───helper
            └───simulator

```
src.main:
* exception: Includes custom exception handling class
* helper: Provides support for registering and using Constants and Logging 
* simulator: Includes the Game class used for running the game simulation 

src.test:
* Contains respective test classes.

## Code Coverage
Current coverage sitting around 80%.


## Contributing
Pull requests are welcome. For major changes, please open an issue first to discuss what you would like to change.

Please make sure to update tests as appropriate.

## License
GPL-Licensed.