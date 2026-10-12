package com.scholarship.expression;

import com.scholarship.datastructures.LinkedStack;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Stack-based Expression Parser and Evaluator for dynamic scholarship merit formulas.
 * Implements:
 * 1. Balanced delimiter (parentheses/brackets) checking using LinkedStack.
 * 2. Infix to Postfix (Reverse Polish Notation) conversion using LinkedStack.
 * 3. Postfix Expression Evaluation with variable substitution using LinkedStack.
 */
public class ExpressionParser {

    private static class OperatorInfo {
        int precedence;
        char associativity; // 'L' or 'R'

        OperatorInfo(int precedence, char associativity) {
            this.precedence = precedence;
            this.associativity = associativity;
        }
    }

    private static final Map<String, OperatorInfo> OPERATORS = new HashMap<>();

    static {
        OPERATORS.put("+", new OperatorInfo(1, 'L'));
        OPERATORS.put("-", new OperatorInfo(1, 'L'));
        OPERATORS.put("*", new OperatorInfo(2, 'L'));
        OPERATORS.put("/", new OperatorInfo(2, 'L'));
        OPERATORS.put("^", new OperatorInfo(3, 'R'));
    }

    public static class EvaluationTraceStep {
        public String token;
        public String action;
        public List<Double> stackState;

        public EvaluationTraceStep(String token, String action, List<Double> stackState) {
            this.token = token;
            this.action = action;
            this.stackState = new ArrayList<>(stackState);
        }
    }

    public static class ParseResult {
        public final double result;
        public final List<String> postfixTokens;
        public final List<EvaluationTraceStep> trace;

        public ParseResult(double result, List<String> postfixTokens, List<EvaluationTraceStep> trace) {
            this.result = result;
            this.postfixTokens = postfixTokens;
            this.trace = trace;
        }
    }

    /**
     * Checks if all parentheses/brackets (), [], {} are balanced using LinkedStack.
     */
    public static boolean checkBalancedDelimiters(String expr) {
        LinkedStack<Character> stack = new LinkedStack<>();

        for (int i = 0; i < expr.length(); i++) {
            char ch = expr.charAt(i);
            if (ch == '(' || ch == '[' || ch == '{') {
                stack.push(ch);
            } else if (ch == ')' || ch == ']' || ch == '}') {
                if (stack.isEmpty()) {
                    return false;
                }
                char open = stack.pop();
                if ((ch == ')' && open != '(') ||
                    (ch == ']' && open != '[') ||
                    (ch == '}' && open != '{')) {
                    return false;
                }
            }
        }
        return stack.isEmpty();
    }

    /**
     * Tokenizes formula expression into operands, operators, and brackets.
     */
    public static List<String> tokenize(String expr) {
        List<String> tokens = new ArrayList<>();
        Pattern pattern = Pattern.compile("\\d+\\.\\d+|\\d+|[a-zA-Z_][a-zA-Z0-9_]*|[\\+\\-\\*/\\^\\(\\)\\[\\]\\{\\}]");
        Matcher matcher = pattern.matcher(expr);

        while (matcher.find()) {
            String token = matcher.group();
            if (token.equals("[") || token.equals("{")) {
                tokens.add("(");
            } else if (token.equals("]") || token.equals("}")) {
                tokens.add(")");
            } else {
                tokens.add(token);
            }
        }
        return tokens;
    }

    /**
     * Converts Infix expression to Postfix (RPN) using custom LinkedStack.
     */
    public static List<String> infixToPostfix(String expr) {
        if (!checkBalancedDelimiters(expr)) {
            throw new IllegalArgumentException("Unbalanced delimiters in expression: " + expr);
        }

        List<String> tokens = tokenize(expr);
        if (tokens.isEmpty()) {
            throw new IllegalArgumentException("Empty expression");
        }

        LinkedStack<String> opStack = new LinkedStack<>();
        List<String> output = new ArrayList<>();

        for (String token : tokens) {
            // Case 1: Number or Variable identifier
            if (isNumber(token) || isIdentifier(token)) {
                output.add(token);
            }
            // Case 2: Open parenthesis
            else if (token.equals("(")) {
                opStack.push(token);
            }
            // Case 3: Close parenthesis
            else if (token.equals(")")) {
                while (!opStack.isEmpty() && !opStack.peek().equals("(")) {
                    output.add(opStack.pop());
                }
                if (opStack.isEmpty()) {
                    throw new IllegalArgumentException("Mismatched parentheses");
                }
                opStack.pop(); // Remove '('
            }
            // Case 4: Operator
            else if (OPERATORS.containsKey(token)) {
                OperatorInfo currOp = OPERATORS.get(token);
                while (!opStack.isEmpty() && OPERATORS.containsKey(opStack.peek())) {
                    OperatorInfo topOp = OPERATORS.get(opStack.peek());
                    if ((currOp.associativity == 'L' && currOp.precedence <= topOp.precedence) ||
                        (currOp.associativity == 'R' && currOp.precedence < topOp.precedence)) {
                        output.add(opStack.pop());
                    } else {
                        break;
                    }
                }
                opStack.push(token);
            } else {
                throw new IllegalArgumentException("Unrecognized token: " + token);
            }
        }

        while (!opStack.isEmpty()) {
            String top = opStack.pop();
            if (top.equals("(") || top.equals(")")) {
                throw new IllegalArgumentException("Mismatched parentheses remaining in stack");
            }
            output.add(top);
        }

        return output;
    }

    /**
     * Evaluates a Postfix (RPN) list using LinkedStack with variable bindings.
     */
    public static ParseResult evaluatePostfix(List<String> postfixTokens, Map<String, Double> variables) {
        LinkedStack<Double> valStack = new LinkedStack<>();
        List<EvaluationTraceStep> trace = new ArrayList<>();

        // Case-insensitive lookup map
        Map<String, Double> lookup = new HashMap<>();
        if (variables != null) {
            for (Map.Entry<String, Double> entry : variables.entrySet()) {
                lookup.put(entry.getKey().toLowerCase(), entry.getValue());
            }
        }

        for (String token : postfixTokens) {
            String action;
            if (isNumber(token)) {
                double val = Double.parseDouble(token);
                valStack.push(val);
                action = "Push constant " + val;
            } else if (lookup.containsKey(token.toLowerCase())) {
                double val = lookup.get(token.toLowerCase());
                valStack.push(val);
                action = String.format("Substitute '%s' = %.2f & push", token, val);
            } else if (OPERATORS.containsKey(token)) {
                if (valStack.size() < 2) {
                    throw new IllegalArgumentException("Insufficient operands for operator " + token);
                }
                double b = valStack.pop();
                double a = valStack.pop();
                double res;

                switch (token) {
                    case "+": res = a + b; break;
                    case "-": res = a - b; break;
                    case "*": res = a * b; break;
                    case "/":
                        if (b == 0.0) throw new ArithmeticException("Division by zero");
                        res = a / b;
                        break;
                    case "^": res = Math.pow(a, b); break;
                    default: throw new IllegalArgumentException("Unknown operator: " + token);
                }
                valStack.push(res);
                action = String.format("Evaluate: %.2f %s %.2f = %.4f", a, token, b, res);
            } else {
                throw new IllegalArgumentException("Undefined variable: " + token);
            }

            trace.add(new EvaluationTraceStep(token, action, valStack.toList()));
        }

        if (valStack.size() != 1) {
            throw new IllegalArgumentException("Malformed expression. Stack size at end: " + valStack.size());
        }

        double finalResult = Math.round(valStack.pop() * 10000.0) / 10000.0;
        return new ParseResult(finalResult, postfixTokens, trace);
    }

    /**
     * Evaluates formula string directly.
     */
    public static ParseResult evaluateFormula(String formula, Map<String, Double> variables) {
        List<String> postfix = infixToPostfix(formula);
        return evaluatePostfix(postfix, variables);
    }

    private static boolean isNumber(String str) {
        return str.matches("^\\d+(\\.\\d+)?$");
    }

    private static boolean isIdentifier(String str) {
        return str.matches("^[a-zA-Z_][a-zA-Z0-9_]*$");
    }
}
