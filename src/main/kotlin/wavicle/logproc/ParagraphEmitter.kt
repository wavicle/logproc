package wavicle.logproc

abstract class ParagraphEmitter() : LineHandler {
    private var started: Boolean = false

    private val stringBuilder = StringBuilder()

    override fun handleLine(line: String) {
        if(isNewParagraph(line)) {
            if(started) {
                onParagraph(stringBuilder.toString())
                stringBuilder.setLength(0)
            }
        }
        stringBuilder.append(line)
        started = true
    }

    override fun finished() {
        if(started) {
            onParagraph(stringBuilder.toString())
        }
        reset()
    }

    private fun reset() {
        started = false
        stringBuilder.setLength(0)
    }

    abstract fun onParagraph(paragraph: String)

    abstract fun isNewParagraph(line: String): Boolean
}