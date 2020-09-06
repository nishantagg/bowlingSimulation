package ten.pin.bowling.simulator

import ten.pin.bowling.UnitSpec
import ten.pin.bowling.exception.GameException
import ten.pin.bowling.helper.Constants

class GameTest extends UnitSpec {

  "startGame" should {
    val objGame = new Game
    objGame.currentFrame = 10
    objGame.framesStats = Map(
      1 -> (Constants.STRIKE, ""),
      2 -> (Constants.STRIKE, "")
    )
    "update the 10th frame" in {
      //When
      objGame.startGame()
      val (currentRollType, _) = objGame.framesStats.getOrElse(objGame.currentFrame, ("", ""))
      //Then
      List(Constants.STRIKE, Constants.SPARE, Constants.NONE) should contain(currentRollType)
    }

  }

  "startGameV2" should {
    "update the 10th frame" in {
      //Given
      val objGame = new Game
      val firstRoll = 5
      val secondRoll = 2
      val dummyScore = 50
      objGame.rolls(dummyScore)
      objGame.currentFrame = 10
      objGame.framesStats = Map(
        1 -> (Constants.STRIKE, ""),
        2 -> (Constants.STRIKE, "")
      )
      //When
      objGame.startGameV2(firstRoll, secondRoll)
      val (currentRollType, _) = objGame.framesStats.getOrElse(objGame.currentFrame, ("", ""))
      val getScore = objGame.score
      //Then
      currentRollType shouldEqual (Constants.NONE)
      getScore shouldEqual (dummyScore + firstRoll + secondRoll)
    }
  }

  "scoring" should {
    val objGame = new Game
    val dummyScore = 50
    objGame.currentFrame = 2
    "add 20 points to the score if current roll is Strike and previous roll was Strike" in {
      //Given
      objGame.scoreCount = 0
      objGame.rolls(dummyScore)
      objGame.framesStats = Map(
        1 -> (Constants.STRIKE, ""),
        2 -> (Constants.STRIKE, "")
      )
      //When
      objGame.scoring()
      val getScore = objGame.score
      //Then
      getScore shouldEqual (dummyScore + 10 + 10)
    }
    "add 20 points to the score if previous roll was Strike and current roll is Spare" in {
      //Given
      objGame.scoreCount = 0
      objGame.rolls(dummyScore)
      objGame.framesStats = Map(
        1 -> (Constants.STRIKE, ""),
        2 -> (Constants.SPARE, "")
      )
      //When
      objGame.scoring()
      val getScore = objGame.score
      //Then
      getScore shouldEqual (dummyScore + 10 + 10)
    }
    "add (10 + currentRollPins) points to the score if previous roll was Spare" in {
      //Given
      objGame.scoreCount = 0
      objGame.rolls(dummyScore)
      val firstRoll = 7
      objGame.framesStats = Map(
        1 -> (Constants.SPARE, ""),
        2 -> (Constants.SPARE, s"$firstRoll|/")
      )
      //When
      objGame.scoring()
      val getScore = objGame.score
      //Then
      getScore shouldEqual (dummyScore + 10 + firstRoll)
    }
    "add (10 + 2*(FirstRoll + SecondRoll)) points to the score if previous roll was Strike and current was neither Strike nor Spare" in {
      //Given
      objGame.scoreCount = 0
      objGame.rolls(dummyScore)
      val firstRoll = 7
      val secondRoll = 2
      objGame.framesStats = Map(
        1 -> (Constants.STRIKE, ""),
        2 -> (Constants.NONE, s"$firstRoll|$secondRoll")
      )
      //When
      objGame.scoring()
      val getScore = objGame.score
      //Then
      getScore shouldEqual (dummyScore + 10 + 2 * (firstRoll + secondRoll))
    }
    "add (10 + FirstRoll + FirstRoll + SecondRoll)) points to the score if previous roll was SPARE and current was neither Strike nor Spare" in {
      //Given
      objGame.scoreCount = 0
      objGame.rolls(dummyScore)
      val firstRoll = 7
      val secondRoll = 2
      objGame.framesStats = Map(
        1 -> (Constants.SPARE, ""),
        2 -> (Constants.NONE, s"$firstRoll|$secondRoll")
      )
      //When
      objGame.scoring()
      val getScore = objGame.score
      //Then
      getScore shouldEqual (dummyScore + 10 + firstRoll + firstRoll + secondRoll)
    }
    "add (FirstRoll + SecondRoll)) points to the score if neither previous nor current is Strike nor Spare" in {
      //Given
      objGame.scoreCount = 0
      objGame.rolls(dummyScore)
      val firstRoll = 5
      val secondRoll = 2
      objGame.framesStats = Map(
        1 -> (Constants.NONE, ""),
        2 -> (Constants.NONE, s"$firstRoll|$secondRoll")
      )
      //When
      objGame.scoring()
      val getScore = objGame.score
      //Then
      getScore shouldEqual (dummyScore + firstRoll + secondRoll)
    }

    "Fail with uninitialised error exception if current frame type not found" in {
      //Given
      objGame.framesStats = Map(
        1 -> ("EXCEPTION", "")
      )
      //Then
      intercept[GameException](objGame.scoring()).getMessage shouldEqual (Constants.uninitializedError)
    }

  }

  "frame10Extras" should {
    val objGame = new Game
    val dummyScore = 50
    objGame.currentFrame = 10
    "add 30 points to the final score based on three consecutive Strikes in 10th frame" in {
      //Given
      objGame.scoreCount = 0
      objGame.rolls(dummyScore)
      objGame.framesStats = Map(objGame.currentFrame -> (Constants.STRIKE, ""))
      val secondRoll = 10
      val thirdRoll = 10
      //When
      objGame.frame10Extras(secondRoll, thirdRoll)
      val getScore = objGame.score
      //Then
      getScore shouldEqual (dummyScore + 10 + 10 + 10)
    }
    "add last roll points to the final score based on Spare in 10th frame" in {
      //Given
      objGame.scoreCount = 0
      objGame.rolls(dummyScore)
      objGame.framesStats = Map(objGame.currentFrame -> (Constants.SPARE, ""))
      val roll = 7
      //When
      objGame.frame10Extras(0, 0, roll)
      val getScore = objGame.score
      //Then
      getScore shouldEqual (dummyScore + 10 + roll + roll)
    }
    "do nothing for everything else" in {
      //Given
      objGame.scoreCount = 0
      objGame.rolls(dummyScore)
      objGame.framesStats = Map(objGame.currentFrame -> (Constants.NONE, s"7|1"))
      //When
      objGame.frame10Extras()
      val getScore = objGame.score
      //Then
      getScore shouldEqual (dummyScore)
    }
  }

  "updateFrame" should {
    "return true for Strike or Spare" in {
      //Given
      val objGame = new Game
      val firstRoll = 10
      val secondRoll = 0
      objGame.currentFrame = 1
      //When
      val checkStrike = objGame.updateFrame(firstRoll, secondRoll, Constants.STRIKE)
      objGame.currentFrame = 2
      val checkSpare = objGame.updateFrame(secondRoll, firstRoll, Constants.SPARE)
      //Then
      checkStrike shouldBe true
      checkSpare shouldBe true
    }
  }

}
