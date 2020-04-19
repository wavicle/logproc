package wavicle.logproc

import java.io.ByteArrayInputStream
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class LogprocTests {

    @Test
    fun `When the file is empty the line handler is not called`() {
        class Emitter: ParagraphEmitter() {
            override fun onParagraph(paragraph: String) {
                TODO("Not yet implemented")
            }

            override fun isNewParagraph(line: String): Boolean {
                TODO("Not yet implemented")
            }
        }

        val inputStream = ByteArrayInputStream("".toByteArray())
        Logproc.Raw.analyze(inputStream, Emitter())
        assertTrue { true }
    }

    @Test
    fun `With just one line that does not look like para start`() {
        class Emitter: ParagraphEmitter() {
            var calledOnce = false

            override fun onParagraph(paragraph: String) {
                if(calledOnce) {
                    throw AssertionError("Should be called only once.")
                }
                assertEquals("Clearly this is not a para", paragraph)
                calledOnce = true
            }

            override fun isNewParagraph(line: String): Boolean {
                return line.startsWith("LOG:")
            }
        }

        val inputStream = ByteArrayInputStream("Clearly this is not a para".toByteArray())
        Logproc.Raw.analyze(inputStream, Emitter())
        assertTrue { true }
    }

    @Test
    fun `With just one line that looks like a para start`() {
        class Emitter: ParagraphEmitter() {
            var calledOnce = false

            override fun onParagraph(paragraph: String) {
                if(calledOnce) {
                    throw AssertionError("Should be called only once.")
                }
                assertEquals("LOG: This is a para cos it begins with 'LOG'", paragraph)
                calledOnce = true
            }

            override fun isNewParagraph(line: String): Boolean {
                return line.startsWith("LOG:")
            }
        }

        val inputStream = ByteArrayInputStream("LOG: This is a para cos it begins with 'LOG'".toByteArray())
        Logproc.Raw.analyze(inputStream, Emitter())
        assertTrue { true }
    }

    @Test
    fun `With multiple lines forming a para`() {
        class Emitter: ParagraphEmitter() {
            val paras = mutableListOf<String>()

            override fun onParagraph(paragraph: String) {
                paras.add(paragraph)
            }

            override fun isNewParagraph(line: String): Boolean {
                return line.trim().startsWith("LOG:")
            }
        }

        val handler = Emitter()
        val inputStream = ByteArrayInputStream("""
            This is some random text. It does not start
            with the official 'para start', so in the end
            it will still be treated as a para of its own.
            
            And this is even if has blank lines in it.
            
            LOG: Log1. The story begins. it is a long
            one with many lines. these lines all belong
            to the first log.
            
            LOG: This is another log
            More lines belonging to the second log.
            Yup, this is for the second log too.
            
            LOG: For good times, a third log.
            And this line obviously belongs to the 3rd
            log. And it is multiline but that's okay.
        """.toByteArray())
        Logproc.Raw.analyze(inputStream, handler)
        assertEquals(handler.paras.size, 4)
    }
}
