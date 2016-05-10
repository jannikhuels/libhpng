package de.wwu.criticalsystems.libhpng.formulaparsing;
/* Generated By:JJTree&JavaCC: Do not edit this line. SMCParserConstants.java */

/**
 * Token literal values and constants.
 * Generated by org.javacc.parser.OtherFilesGen#start()
 */
public interface SMCParserConstants {

  /** End of File. */
  int EOF = 0;
  /** RegularExpression Id. */
  int LBRACE = 3;
  /** RegularExpression Id. */
  int RBRACE = 4;
  /** RegularExpression Id. */
  int NUMBER = 5;
  /** RegularExpression Id. */
  int SEP = 6;
  /** RegularExpression Id. */
  int ATOMIC = 7;
  /** RegularExpression Id. */
  int NOT = 8;
  /** RegularExpression Id. */
  int OR = 9;
  /** RegularExpression Id. */
  int AND = 10;
  /** RegularExpression Id. */
  int UNTIL = 11;
  /** RegularExpression Id. */
  int PROB = 12;
  /** RegularExpression Id. */
  int COMPARE = 13;
  /** RegularExpression Id. */
  int EOL = 14;

  /** Lexical state. */
  int DEFAULT = 0;

  /** Literal token values. */
  String[] tokenImage = {
    "<EOF>",
    "\" \"",
    "\"\\t\"",
    "\"(\"",
    "\")\"",
    "<NUMBER>",
    "\",\"",
    "\"a\"",
    "\"!\"",
    "\"OR\"",
    "\"AND\"",
    "\"U\"",
    "\"P\"",
    "<COMPARE>",
    "<EOL>",
    "\"_\"",
  };

}
