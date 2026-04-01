package com.example.app;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.InvalidParameterException;
import java.util.EmptyStackException;

import com.example.app.util.Stack;

public class Main {
  public static final PrintStream out = System.out;
  public static final String lineSeparator = System.lineSeparator();
  private static final String OPENING_SYMBOLS = "{[(";
  private static final String CLOSING_SYMBOLS = "}])";

  public static void main(String[] args) {
    validateArgs(args);
    String[] lines;
    int[] errorLocation;
    lines = readFile(args[0]);
    errorLocation = findErrorLocation(lines);
    outputResult(errorLocation);
  }

  private static void validateArgs(String[] args){
    if (args.length == 0) 
      throw new InvalidParameterException("No file was found");
    if (args.length > 1) {
      throw new InvalidParameterException("No extra parameters are accepted");
    }
  }

  private static String[] readFile(String path) {
    try {
      return Files.readString(Path.of(path)).lines().toArray(String[]::new);
    } catch (IOException e) {
      e.printStackTrace();
      return null;
    }
  }

  private static int[] findErrorLocation(String[] lines) {
    char character;
    Stack symbols = new Stack();
    for (int i = 0; i < lines.length; i++) {
      String line = lines[i];
      for (int j = 0; j < line.length(); j++) {
        character = line.charAt(j);
        if (OPENING_SYMBOLS.indexOf(character) != -1) {
          symbols.push(character);
          continue;
        }
        if (CLOSING_SYMBOLS.indexOf(character) != -1) {
          try {
            if (CLOSING_SYMBOLS.indexOf(character) == OPENING_SYMBOLS.indexOf((char) symbols.peek()))
              symbols.pop();
            else
              return new int[] { i+1, j+1 };
          } catch (EmptyStackException e) {
            return new int[] { i+1, j+1 };
          }
        }
      }
    }
    if (!symbols.empty())
      return new int[] { -1, -1 };
    return null;
  }

  private static void outputResult(int[] errorLocation) {
    if (errorLocation == null) {
      out.println("No unbalanced symbols were found on the given file");
      return;
    }
    if (errorLocation[0] < -1 || errorLocation[1] < -1)
      throw new IllegalArgumentException("Invalid errorLocation values");
    if (errorLocation[0] == -1 || errorLocation[1] == -1) {
      out.println("Found unclosed symbols");
      return;
    }
    out.println("Error found on line " + errorLocation[0] + " and column " + errorLocation[1]);
  }

}
