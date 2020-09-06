package ten.pin.bowling

import ten.pin.bowling.helper.{Constants, Logging}
import ten.pin.bowling.simulator.Game

object GameMain extends Logging {

  def main(args: Array[String]): Unit = {
    val gameObj = new Game()
    gameObj.startGame()
    val finalScore = gameObj.score
    logger.info(s"****** FINAL SCORE:[$finalScore] ********")

    //Using startGameV2 method
    val gameObj2 = new Game()
    for (counter <- 1 to 10) {
      val firstRoll = gameObj.randomNumber(Constants.MAX_PINS)
      val secondRoll = gameObj.randomNumber(Constants.MAX_PINS - firstRoll)
      gameObj2.startGameV2(firstRoll, secondRoll)
    }
    logger.info(s"****** FINAL SCORE:[${gameObj2.score}] ********")

  }

}
