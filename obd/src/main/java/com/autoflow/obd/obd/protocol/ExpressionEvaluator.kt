package com.autoflow.obd.obd.protocol

import java.util.ArrayDeque

object ExpressionEvaluator {
    private val operators = setOf('+', '-', '*', '/')

    fun evaluate(expression: String, bytes: ByteArray): Double {
        val tokens = tokenize(expression)
        val rpn = toRpn(tokens)
        return evalRpn(rpn) { variable ->
            val index = (variable.code - 'A'.code)
            if (index in bytes.indices) bytes[index].toInt() and 0xFF else 0
        }
    }

    private fun tokenize(expression: String): List<String> {
        val tokens = mutableListOf<String>()
        val builder = StringBuilder()
        expression.filterNot { it.isWhitespace() }.forEach { char ->
            when {
                char.isDigit() || char == '.' -> builder.append(char)
                char in operators || char == '(' || char == ')' -> {
                    if (builder.isNotEmpty()) {
                        tokens += builder.toString()
                        builder.clear()
                    }
                    tokens += char.toString()
                }
                char.isLetter() -> {
                    if (builder.isNotEmpty()) {
                        tokens += builder.toString()
                        builder.clear()
                    }
                    tokens += char.toString().uppercase()
                }
                else -> error("Unsupported token: $char")
            }
        }
        if (builder.isNotEmpty()) tokens += builder.toString()
        return tokens
    }

    private fun precedence(op: String): Int = when (op) {
        "*", "/" -> 2
        "+", "-" -> 1
        else -> 0
    }

    private fun toRpn(tokens: List<String>): List<String> {
        val output = mutableListOf<String>()
        val stack = ArrayDeque<String>()
        tokens.forEach { token ->
            when {
                token.matches(NUMBER_REGEX) || token.matches(VARIABLE_REGEX) -> output += token
                operators.contains(token.single()) -> {
                    while (stack.isNotEmpty() && operators.contains(stack.peek().single()) &&
                        precedence(stack.peek()) >= precedence(token)
                    ) {
                        output += stack.pop()
                    }
                    stack.push(token)
                }
                token == "(" -> stack.push(token)
                token == ")" -> {
                    while (stack.isNotEmpty() && stack.peek() != "(") {
                        output += stack.pop()
                    }
                    if (stack.isNotEmpty() && stack.peek() == "(") stack.pop()
                }
            }
        }
        while (stack.isNotEmpty()) output += stack.pop()
        return output
    }

    private fun evalRpn(rpn: List<String>, resolveVariable: (Char) -> Int): Double {
        val stack = ArrayDeque<Double>()
        rpn.forEach { token ->
            when {
                token.matches(NUMBER_REGEX) -> stack.push(token.toDouble())
                token.matches(VARIABLE_REGEX) -> stack.push(resolveVariable(token.single()).toDouble())
                token in listOf("+", "-", "*", "/") -> {
                    val b = stack.pop()
                    val a = stack.pop()
                    val result = when (token) {
                        "+" -> a + b
                        "-" -> a - b
                        "*" -> a * b
                        "/" -> if (b == 0.0) 0.0 else a / b
                        else -> 0.0
                    }
                    stack.push(result)
                }
            }
        }
        return stack.pop()
    }

    private val NUMBER_REGEX = "^-?\\d+(?:\\.\\d+)?$".toRegex()
    private val VARIABLE_REGEX = "^[A-D]$".toRegex()
}
