package me.alex_s168.sfl.parser

import me.alex_s168.multiplatform.collection.Node
import me.alex_s168.multiplatform.collection.Stream
import me.alex_s168.sfl.ast.ASTFunCall
import me.alex_s168.sfl.ast.ASTNode
import me.alex_s168.sfl.error.ErrorContext
import me.alex_s168.sfl.lexer.Token
import me.alex_s168.sfl.lexer.TokenType
import me.alex_s168.sfl.location.tTo

fun parseExprPart(
    stream: Stream<Token>,
    err: ErrorContext
): Node<ASTNode>? {
    val next = stream.peek()
        ?: return null

    return when (next.type) {
        TokenType.IDENTIFIER -> {
            val ref = parseRef(stream, err)
                ?: return null

            var next2 = stream.peek()
                ?: return ref as Node<ASTNode>

            if (next2.type == TokenType.ANGLE_OPEN) {
                val backup = stream.backup()
                stream.consume()
                val types = parseAngleBrackets(stream, err)
                    ?: return null
                if (stream.peek()?.type != TokenType.PAREN_OPEN) {
                    backup.restore()
                } else {
                    (ref.children as MutableList<Node<ASTNode>>).addAll(types as MutableList<Node<ASTNode>>)
                    next2 = stream.peek()
                        ?: return ref as Node<ASTNode>
                }
            }

            if (next2.type == TokenType.PAREN_OPEN) {
                stream.consume()
                val children = mutableListOf(ref as Node<ASTNode>)
                val args = parseParents(stream, err)
                    ?: return null
                children.addAll(args as MutableList<Node<ASTNode>>)
                
                val call = ASTFunCall(next.location tTo (args.lastOrNull()?.value?.loc ?: next2.location))
                Node(call, children)
            }
            else {
                ref as Node<ASTNode>
            }
        }
        // TODO: literals
        else -> null
    }
}