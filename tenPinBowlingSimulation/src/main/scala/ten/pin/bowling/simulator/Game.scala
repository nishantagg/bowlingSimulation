package ten.pin.bowling.simulator

import ten.pin.bowling.exception.GameException
import ten.pin.bowling.helper.{Constants, Logging}

/**
  * Game class is a simple Ten-Point Bowling game simulator.
  * Simply create a object of the class and call def startGame(): This will simulate real-life game scenario where
  * the game consists of 10 frames. In each frame the player has two opportunities to knock
  * down 10 pins. The score for the frame is the total number of pins knocked down, plus bonuses for strikes
  * and spares. Interim frame by frame score will be captured in the console as well in the log file.
  *
  * ******************   RULES and ASSUMPTIONS   *******************************
  * SPARE: A spare is when the player knocks down all 10 pins in two tries. The bonus for that frame is the number of
  * pins knocked down by the next roll.
  *
  * STRIKE: A strike is when the player knocks down all 10 pins on their first try. The bonus for that frame is the value of
  * the next two balls rolled.
  *
  * Special case: Frame10\n
  * In the tenth frame a player who rolls a spare or strike is allowed to roll the extra balls to complete the frame.
  * However no more than three balls can be rolled in the tenth frame.
  * Max points a player can collect in frame10 is 30
  */
class Game extends Logging {

  /** Class specific variables */
  private[simulator] var scoreCount = 0
  private[simulator] var strikeCount = 0
  private[simulator] var spareCount = 0
  private[simulator] var currentFrame = 1

  /**
    * FrameStats Map will be dynamically updated with the scores during the runtime.
    * This will enable the display of player's performance Frame by Frame.()
    * Typical values will be (1 -> ("STRIKE","")), (2 ->("SPARE","7|/")), (3,("none","7|2")
    */
  private[simulator] var framesStats: Map[Int, (String, String)] = Map.empty

  /**
    * This function returns the score for the game.
    * Should be called at the end of Frame[10] else will return interim score.
    *
    * @return the final score for the game.
    */
  def score: Int = scoreCount

  /**
    * @param boundary : Max number/ Boundary value for the random number
    * @return : The random number
    */
  def randomNumber(boundary: Int): Int = {
    scala.util.Random.nextInt(boundary)
  }

  /**
    * This function called every time the score needs to be updated.
    *
    * @param pins :Knocked down pins in that role.
    *             Return Type: Unit
    */
  private[simulator] def rolls(pins: Int): Unit = scoreCount += pins

  /**
    * def startGame() holds the logic for simulating the rolls.
    * This is a tail-recursive function which will iterate until 10th frame is reached.
    * This function is responsible for:
    * Simulating the knocked down pins for each roll. Used the random number generation technique.
    * Storing the statistics for each frame in the FramesStats Map based on the roll-type i.e[STRIKE, SPARE, NONE]
    * calling def scoring(): after each frame to update the scores.
    */
  def startGame(): Unit = {
    if (currentFrame == 1) {
      logger.info(s"Game starting. All the best :)")
    }
    logger.info(s"Starting frame[$currentFrame].")
    val firstRoll = randomNumber(Constants.MAX_PINS) // Simulating real scenario by some random pin number
    logger.info(s"Roll-1: Pins knocked down[$firstRoll]")
    if (!updateFrame(firstRoll, 0, Constants.STRIKE)) {
      val secondRoll = randomNumber(Constants.MAX_PINS - firstRoll) //This will keep the random pin boundary within limit of 10
      logger.info(s"Roll-2: Pins knocked down[$secondRoll]")
      if (!updateFrame(firstRoll, secondRoll, Constants.SPARE)) { // Spare can only hit at 2nd roll
        framesStats = framesStats + (currentFrame -> (Constants.NONE, s"$firstRoll|$secondRoll")) //Updating FrameStats Map with score R1|R2
      }
    }
    scoring()
    currentFrame match {
      case 10 =>
        logger.info("Game completed. Calculating Statistics......")
        logger.info(s"Total Strikes[$strikeCount]")
        logger.info(s"Total Spares[$spareCount]")
      case _ =>
        logger.info(s"Calling startGame() for next frame. Current frame[$currentFrame]")
        currentFrame += 1
        startGame()
    }
  }

  /**
    * def startGameV2() is invariably same to startGame() with only difference that in this case the firstRoll and
    * secondRoll arguments will be provided by the calling function. Instead of tail recursive startGameV2 needs to be called
    * explicitly by the calling function until it reaches Frame10.
    * This gives more control to the calling thread in terms of simulation and generating random scenarios.
    * @param firstRoll : Knocked pins on first roll
    * @param secondRoll : Knocked pins on second roll
    */
  def startGameV2(firstRoll: Int, secondRoll: Int): Unit = {
    if (currentFrame == 1) {
      logger.info(s"Game starting. All the best :)")
    }
    logger.info(s"Starting frame[$currentFrame].")
    logger.info(s"Roll-1: Pins knocked down[$firstRoll]")
    if (!updateFrame(firstRoll, 0, Constants.STRIKE)) {
      logger.info(s"Roll-2: Pins knocked down[$secondRoll]")
      if (!updateFrame(firstRoll, secondRoll, Constants.SPARE)) { // Spare can only hit at 2nd roll
        framesStats = framesStats + (currentFrame -> (Constants.NONE, s"$firstRoll|$secondRoll")) //Updating FrameStats Map with score R1|R2
      }
    }
    scoring()
    currentFrame match {
      case 10 =>
        logger.info("Game completed. Calculating Statistics......")
        logger.info(s"Total Strikes[$strikeCount]")
        logger.info(s"Total Spares[$spareCount]")
      case _ =>
        currentFrame += 1
    }
  }

  /**
    * def scoring() holds the core logic of score calculations.
    * For current frame, calculation starts by iterating over previous frame to check if there was any Strike or Spare.
    * This function is also responsible for triggering Frame10 bonus round calculations.
    */
  private[simulator] def scoring(): Unit = {
    logger.info(s"Current Frame[$currentFrame]")
    val (rollType, currentFrameStats) = framesStats.getOrElse(currentFrame, ("", ""))
    rollType match {
      case Constants.STRIKE =>
        if (currentFrame != 1) {
          val (previousRollType, _) = framesStats.getOrElse(currentFrame - 1, ("", ""))
          previousRollType match {
            case Constants.STRIKE | Constants.SPARE => rolls(10 + 10)
            case _ => logger.info(s"Strike or Spare not found for previous frame[${currentFrame - 1}]. No score updates required.")
          }
        }
        logger.info(s"Total Score[$scoreCount]")
      case Constants.SPARE =>
        if (currentFrame != 1) {
          val (previousRollType, _) = framesStats.getOrElse(currentFrame - 1, ("", ""))
          previousRollType match {
            case Constants.STRIKE => rolls(10 + 10)
            case Constants.SPARE =>
              val currentFirstRollPins = currentFrameStats.split("\\|")(0).toInt
              rolls(10 + currentFirstRollPins)
            case _ => logger.info(s"Strike or Spare not found for previous frame[${currentFrame - 1}]. No score updates required.")
          }
        }
        logger.info(s"Total Score[$scoreCount]")
      case Constants.NONE =>
        val frameScore = currentFrameStats.toString.split("\\|")
        val firstRoll = frameScore(0).toInt
        val secondRoll = frameScore(1).toInt
        logger.info(s"FrameScore[${firstRoll + secondRoll}] for frame[$currentFrame]")
        val (previousRollType, _) = framesStats.getOrElse(currentFrame - 1, ("", ""))
        previousRollType match {
          case Constants.STRIKE => rolls(10 + 2 * (firstRoll + secondRoll))
          case Constants.SPARE => rolls(10 + firstRoll + (firstRoll + secondRoll))
          case _ => rolls(firstRoll + secondRoll)
        }
        logger.info(s"Total Score[$scoreCount]")
      case _ => throw GameException(Constants.uninitializedError, null)
    }
    currentFrame match {
      case 10 =>
        logger.info("Reached Frame[10]. Validating bonus rolls criteria.")
        frame10Extras(randomNumber(10), randomNumber(10), randomNumber(10))
      case _ =>
    }
  }

  /**
    * def frame10Extras() will handle the special scenario of Strike or Spare in the 10th frame
    * However no more than three balls can be rolled in the tenth frame.
    * Max points a player can collect in frame10 is 30
    *
    * @param secondRoll Knocked pins on second roll if it was a Strike.
    * @param thirdRoll  Knocked pins on third roll.
    * @param lastRoll   Knocked pins on last roll if it was a Spare initially.
    */
  private[simulator] def frame10Extras(secondRoll: Int = 0, thirdRoll: Int = 0, lastRoll: Int = 0): Unit = {
    logger.info("Frame[10].Checking for Strike or Spare")
    val (rollType, _) = framesStats.getOrElse(currentFrame, ("", ""))
    rollType match {
      case Constants.STRIKE =>
        logger.info("Bonus roll for Strike in 10th Frame")
        logger.info("Simulating second roll")
        if (secondRoll == 10) {
          logger.info("!!! STRIKE AGAIN !!!")
          strikeCount += 1
        }
        rolls(10 + secondRoll)
        logger.info("Simulating third roll")
        if (thirdRoll == 10) {
          logger.info("!!! Third Strike in a row. You are on a roll :) !!!")
          strikeCount += 1
        }
        rolls(thirdRoll)
      case Constants.SPARE =>
        logger.info("Bonus roll for Spare in 10th Frame")
        logger.info("Simulating last roll")
        logger.info(s"Last attempt roll score[$lastRoll]")
        logger.info("Updating scores...")
        spareCount += 1
        rolls(10 + lastRoll + lastRoll)
      case _ => logger.info("No Strike or Spare found. No score updates required.")
    }

  }

  /**
    * def updateFrame() will handle the framesStats Map update.
    *
    * @param firstRoll  Knocked pins on first roll
    * @param secondRoll Knocked pins on second roll
    * @param flag       Strike or Spare flag
    * @return true if its Strike or Spare else false
    */
  private[simulator] def updateFrame(firstRoll: Int = 0, secondRoll: Int = 0, flag: String): Boolean = {
    if (firstRoll + secondRoll == 10) {
      flag match {
        case Constants.STRIKE => {
          logger.info(s"!!!Bingo - Strike Roll !!!. Frame[$currentFrame]")
          framesStats = framesStats + (currentFrame -> (Constants.STRIKE, ""))
          strikeCount += 1
        }
        case Constants.SPARE => {
          logger.info(s"!!!Spare Roll !!!. Frame[$currentFrame]")
          framesStats = framesStats + (currentFrame -> (Constants.SPARE, s"$firstRoll|/"))
          spareCount += 1
        }
      }
      true
    } else {
      false
    }
  }
}

