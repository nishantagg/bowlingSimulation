package ten.pin.bowling.helper

import org.apache.log4j.Logger

trait Logging {
  @transient lazy val logger: Logger = Logger.getLogger(getClass.getName)
}
