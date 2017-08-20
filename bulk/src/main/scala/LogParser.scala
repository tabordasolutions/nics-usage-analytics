
import java.io._
import java.util.zip.{GZIPInputStream, GZIPOutputStream}

import scala.io.Source

object LogParser extends App {
  val pattern = """\S+ ([\d.:]+) (\S+) (\S+) \[([\w:/]+\s[\+\-]\d{4})\] "(.+?)" (\d{3}) (\d+|-) "(\S+)?" "(.+)?" ?(\S+)?""".r

  def parseOne(line: String): String = {
    line match {
      case pattern(hostname, logname, username, requestedAt, firstLine, status, bytesSent, referer, userAgent, userOrgId) =>
        val entry = LogEntry(hostname, logname, username, requestedAt, firstLine.take(2000), status, bytesSent, referer, userAgent, userOrgId)
        entry.validate()
        entry.toCommaSeparatedValues
      case _ =>
        throw new ParseException
    }
  }

  def printOne(stream: PrintStream, line: String): Unit = {
    stream.println(line)
  }

  def processFile(inbox: File, outbox: File, inFile: File): Unit = {
    val outFile = new File(inFile.toString.replace(inbox.getName, outbox.getName).replace(".gz", ".csv.gz"))
    outFile.getParentFile.mkdirs()
    val outStream = new PrintStream(new GZIPOutputStream(new FileOutputStream(outFile)))
    try {
      val inStream = new GZIPInputStream(new FileInputStream(inFile))
      try {
        Source.fromInputStream(inStream).getLines().zipWithIndex.foreach {
          case (line, index) =>
            try {
              printOne(outStream, parseOne(line))
            } catch {
              case e: ParseException =>
                System.err.println(s"ERROR: Failed to parse index $index of $inFile; line=$line")
              case e: ValidationException =>
                if(!e.getMessage.contains(".ogg")) {
                  System.err.println(s"ERROR: Failed to validate index $index of $inFile; message=${e.getMessage}")
                }
            }
        }
      } finally {
        inStream.close()
      }
    } finally {
      outStream.close()
    }
  }

  def findFiles(root: File): Array[File] = {
    val entries = root.listFiles.filter(!_.isHidden).filter(!_.getName.endsWith(".csv"))
    val files = entries.filter(_.isFile)
    val directories = entries.filter(_.isDirectory)
    files ++ directories.flatMap(findFiles)
  }

  val inbox = new File("inbox")
  val outbox = new File("outbox")

  findFiles(inbox).foreach { f =>
    println(s"Processing $f")
    processFile(inbox, outbox, f)
  }
}
