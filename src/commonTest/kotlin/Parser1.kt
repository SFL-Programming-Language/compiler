import me.alex_s168.sfl.error.ErrorContext
import me.alex_s168.sfl.error.hasErrors
import me.alex_s168.sfl.lexer.lex
import me.alex_s168.sfl.location.SourceLocation
import me.alex_s168.sfl.parser.parseType
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@Test
fun testParser1() {
    val source = """
        mut std::collections::Vec<Aa, Bb<Cc>>
    """.trimIndent()
    val errors = ErrorContext()
    val loc = SourceLocation(
        source = source,
        fileAbsPath = "testParser0"
    )
    val tokens = lex(loc, errors)
    tokens.setDone()
    println("Tokens:")
    for (token in tokens.reversed()) {
        println(token)
    }
    println("===")
    assertEquals(false, errors.hasErrors(), errors.toString())
    val ast = parseType(tokens, errors)
    assertEquals(false, errors.hasErrors(), errors.toString())
    assertNotNull(ast)
    println("AST:")
    println(ast)
    println("===")
    assertTrue(tokens.isEmpty(), "Expected all tokens to be consumed. Left: ${tokens.joinToString { it.toString() }}")
}