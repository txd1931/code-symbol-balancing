package com.example.app.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.EmptyStackException;
import java.util.Random;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public class StackTest {

  @Test
  void printEmptyUsesProvidedStream() {
    Stack stack = new Stack();
    ByteArrayOutputStream buffer = new ByteArrayOutputStream();
    PrintStream out = new PrintStream(buffer);
    stack.print(out);
    assertEquals("Empty stack" + System.lineSeparator(), buffer.toString(StandardCharsets.UTF_8));
  }

  @Test
  void newStackIsEmpty() {
    Stack stack = new Stack();
    assertTrue(stack.empty());
  }

  @ParameterizedTest
  @ValueSource(longs = {1L, 21L, 28392113L})
  void peekReturnsLastPushedElement(long seed) {
    Random random = new Random(seed);
    Stack stack = new Stack();
    int iterations = random.nextInt(10, 100);
    Object lastPushed = null;
    for(int i = 0; i < iterations; i++){
      int value = random.nextInt();
      lastPushed = value;
      stack.push(value);
    }
    assertEquals(lastPushed, stack.peek());
  }

  @Test
  void pushMakesStackNonEmpty() {
    Stack stack = new Stack();
    stack.push("A");
    assertFalse(stack.empty());
  }

  @Test
  void peekReturnsLastPushedElement() {
    Stack stack = new Stack();
    stack.push("A");
    stack.push("B");
    assertEquals("B", stack.peek());
  }

  @Test
  void peekDoesNotRemoveElement() {
    Stack stack = new Stack();
    stack.push("A");
    assertEquals("A", stack.peek());
    assertEquals("A", stack.peek());
    assertFalse(stack.empty());
  }

  @Test
  void popReturnsElementsInLifoOrder() {
    Stack stack = new Stack();
    stack.push("A");
    stack.push("B");
    stack.push("C");

    assertEquals("C", stack.pop());
    assertEquals("B", stack.pop());
    assertEquals("A", stack.pop());
    assertTrue(stack.empty());
  }

  @Test
  void popOnEmptyThrows() {
    Stack stack = new Stack();
    assertThrows(EmptyStackException.class, stack::pop);
  }

  @Test
  void peekOnEmptyThrows() {
    Stack stack = new Stack();
    assertThrows(EmptyStackException.class, stack::peek);
  }

  @Test
  void printWritesTopToBottomWithIndexes() {
    Stack stack = new Stack();
    stack.push("A");
    stack.push("B");

    ByteArrayOutputStream buffer = new ByteArrayOutputStream();
    PrintStream out = new PrintStream(buffer);

    stack.print(out);

    String expected =
        "0. B" + System.lineSeparator()
            + "1. A" + System.lineSeparator();

    assertEquals(expected, buffer.toString(StandardCharsets.UTF_8));
  }
}
