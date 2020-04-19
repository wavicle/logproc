package wavicle.logproc

interface LineHandler {
    fun started() = Unit

    fun handleLine(line: String)

    fun finished()
}