package ten.pin.bowling.exception

final case class GameException(
    private val message: String = "",
    private val cause: Throwable = None.orNull
)
  extends Exception(message, cause)
