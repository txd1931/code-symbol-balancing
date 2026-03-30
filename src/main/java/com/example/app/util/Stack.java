package com.example.app.util;

import java.io.PrintStream;
import java.util.EmptyStackException;

public final class Stack {

  private final class StackNode {
    private Object element;
    private StackNode next;

    public StackNode(Object element, StackNode next) {
      this.element = element;
      this.next = next;
    }
  }

  private StackNode top;

  public boolean empty() {
    return top == null;
  }

  public void push(Object element) {
    if (empty()) {
      top = new StackNode(element, null);
      return;
    }
    top = new StackNode(element, top);
  }

  public Object pop() {
    if (empty())
      throw new EmptyStackException();
    Object o = top.element;
    top = top.next;
    return o;
  }

  public Object peek() {
    if(empty())
      throw new EmptyStackException();
    return top.element;
  }

  public void print(PrintStream out) {
    if (empty()) {
      out.println("Empty stack");
      return;
    }
    StackNode itr = top;
    for (int i = 0; itr != null; i++) {
      out.println(i + ". " + itr.element);
      itr = itr.next;
    }
  }
}
