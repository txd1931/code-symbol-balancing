package com.example.app;

import java.io.PrintStream;

import com.example.app.util.Stack;

public class Main {
  public static PrintStream out = System.out;
  
  public static void main(String[] args) {
    Stack stack = new Stack();
    stack.print(out);
    stack.push("a");
    stack.push("b");
    stack.push("c");
    stack.print(out);
    out.println(stack.pop());
    stack.print(out);
  }
}
