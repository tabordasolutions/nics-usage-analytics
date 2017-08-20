import java.net.{URI, URISyntaxException}

case class LogEntry(
                     hostname: String,
                     logname: String,
                     username: String,
                     requestedAt: String,
                     firstLine: String,
                     status: String,
                     bytesSent: String,
                     referer: String,
                     userAgent: String,
                     userOrgId: String
                   ) {
  def toCommaSeparatedValues: String = {
    s""""$hostname","$logname","$username","$requestedAt","$firstLine","$status","$bytesSent","$referer","$userAgent","$userOrgId""""
  }

  def validate(): Unit = {
    validateFirstLine()
    validateReferrer()
  }

  private def validateFirstLine(): Unit = {
    if(!(firstLine equals "-") && !firstLine.matches("""^(GET|POST|PUT|PATCH|DELETE|OPTIONS|HEAD) \S+ HTTP/1\.(0|1)$""")) {
      throw new ValidationException(s"First line failed validation; firstLine=$firstLine")
    }
  }

  private def validateReferrer(): Unit = {
    try {
      if (!(referer equals "-")) {
        new URI(referer)
      }
    } catch {
      case e: URISyntaxException => throw new ValidationException(s"Referrer URI failed validation; referrer=$referer")
    }
  }
}
