(function() {
  /*
  Copyright (c) 2002-2013 "Neo Technology,"
  Network Engine for Objects in Lund AB [http://neotechnology.com]
  
  This file is part of Neo4j.
  
  Neo4j is free software: you can redistribute it and/or modify
  it under the terms of the GNU General Public License as published by
  the Free Software Foundation, either version 3 of the License, or
  (at your option) any later version.
  
  This program is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU General Public License for more details.
  
  You should have received a copy of the GNU General Public License
  along with this program.  If not, see <http://www.gnu.org/licenses/>.
  */  define(['lib/amd/CodeMirror'], function(CodeMirror) {
    CodeMirror.defineMode("cypher", function(config) {
      var curPunc, indentUnit, keywords, operatorChars, ops, popContext, pushContext, tokenBase, tokenLiteral, tokenOpLiteral, wordRegexp;
      indentUnit = config.indentUnit;
      curPunc = null;
      wordRegexp = function(words) {
        return new RegExp("^(?:" + words.join("|") + ")$", "i");
      };
      ops = wordRegexp(['node', 'nodes', 'and', 'or', 'in', 'not', 'all', 'any', 'none', 'single', 'length', 'id', 'type', 'coalesce', 'head', 'last', 'extract', 'filter', 'tail', 'abs', 'round', 'sqrt', 'sign']);
      keywords = wordRegexp(['START', 'MATCH', 'RELATE', 'WHERE', 'CREATE', 'RETURN', 'MATCH', 'LIMIT', 'ORDER BY', 'SKIP', 'COUND', 'SUM', 'AVG', 'MAX', 'MIN', 'COLLECT', 'DISCINCT', 'WITH']);
      operatorChars = /[*+\-<>=&|]/;
      tokenBase = function(stream, state) {
        var ch, ch2, word;
        ch = stream.next();
        curPunc = null;
        if (ch === "$" || ch === "?") {
          stream.match(/^[\w\d]*/);
          return "variable-2";
        } else if (ch === "<" && !stream.match(/^[\s\u00a0=]/, false)) {
          stream.match(/^[^\s\u00a0>]*>?/);
          return "atom";
        } else if (ch === "\"" || ch === "'") {
          state.tokenize = tokenLiteral(ch);
          return state.tokenize(stream, state);
        } else if (ch === "`") {
          state.tokenize = tokenOpLiteral(ch);
          return state.tokenize(stream, state);
        } else if (/[{}\(\),\.;\[\]]/.test(ch)) {
          curPunc = ch;
          return null;
        } else if (ch === "/") {
          ch2 = stream.next();
          if (ch2 === "/") {
            stream.skipToEnd();
            return "comment";
          }
        } else if (operatorChars.test(ch)) {
          stream.eatWhile(operatorChars);
          return null;
        } else if (ch === ":") {
          stream.eatWhile(/[\w\d\._\-]/);
          return "atom";
        } else {
          stream.eatWhile(/[_\w\d]/);
          if (stream.eat(":")) {
            stream.eatWhile(/[\w\d_\-]/);
            return "atom";
          }
          word = stream.current();
          if (ops.test(word)) {
            return null;
          } else if (keywords.test(word)) {
            return "keyword";
          } else {
            return "variable";
          }
        }
      };
      tokenLiteral = function(quote) {
        return function(stream, state) {
          var ch, escaped;
          escaped = false;
          while ((ch = stream.next()) !== null) {
            if (ch === quote && !escaped) {
              state.tokenize = tokenBase;
              break;
            }
            escaped = !escaped && ch === "\\";
          }
          return "string";
        };
      };
      tokenOpLiteral = function(quote) {
        return function(stream, state) {
          var ch, escaped;
          escaped = false;
          while ((ch = stream.next()) !== null) {
            if (ch === quote && !escaped) {
              state.tokenize = tokenBase;
              break;
            }
            escaped = !escaped && ch === "\\";
          }
          return "variable-2";
        };
      };
      pushContext = function(state, type, col) {
        return state.context = {
          prev: state.context,
          indent: state.indent,
          col: col,
          type: type
        };
      };
      popContext = function(state) {
        state.indent = state.context.indent;
        return state.context = state.context.prev;
      };
      return {
        startState: function(base) {
          return {
            tokenize: tokenBase,
            context: null,
            indent: 0,
            col: 0
          };
        },
        token: function(stream, state) {
          var style;
          if (stream.sol()) {
            if (state.context && state.context.align === null) {
              state.context.align = false;
            }
            state.indent = stream.indentation();
          }
          if (stream.eatSpace()) {
            return null;
          }
          style = state.tokenize(stream, state);
          if (style !== "comment" && state.context && state.context.align === null && state.context.type !== "pattern") {
            state.context.align = true;
          }
          if (curPunc === "(") {
            pushContext(state, ")", stream.column());
          } else if (curPunc === "[") {
            pushContext(state, "]", stream.column());
          } else if (curPunc === "{") {
            pushContext(state, "}", stream.column());
          } else if (/[\]\}\)]/.test(curPunc)) {
            while (state.context && state.context.type === "pattern") {
              popContext(state);
            }
            if (state.context && curPunc === state.context.type) {
              popContext(state);
            }
          } else if (curPunc === "." && state.context && state.context.type === "pattern") {
            popContext(state);
          } else if (/atom|string|variable/.test(style) && state.context) {
            if (/[\}\]]/.test(state.context.type)) {
              pushContext(state, "pattern", stream.column());
            } else if (state.context.type === "pattern" && !state.context.align) {
              state.context.align = true;
              state.context.col = stream.column();
            }
          }
          return style;
        },
        indent: function(state, textAfter) {
          var closing, context, firstChar;
          firstChar = textAfter && textAfter.charAt(0);
          context = state.context;
          if (/[\]\}]/.test(firstChar)) {
            while (context && context.type === "pattern") {
              context = context.prev;
            }
          }
          closing = context && firstChar === context.type;
          if (!context) {
            return 0;
          } else if (context.type === "pattern") {
            return context.col;
          } else if (context.align) {
            return context.col + (closing != null ? closing : {
              0: 1
            });
          } else {
            return context.indent + (closing != null ? closing : {
              0: indentUnit
            });
          }
        }
      };
    });
    return CodeMirror.defineMIME("text/x-cypher", "cypher");
  });
}).call(this);
