package wavicle.logproc

import java.io.InputStream

object Logproc {

    object Raw {
        fun analyze(inputStream: InputStream, lineHandler: LineHandler) {
            lineHandler.started()
            inputStream.bufferedReader().forEachLine(lineHandler::handleLine)
            lineHandler.finished()
        }
    }
}