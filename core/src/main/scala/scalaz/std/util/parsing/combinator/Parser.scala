package scalaz
package std
package util
package parsing
package combinator

import scala.util.parsing.combinator

trait Parsers {
  class ParsersW[P <: combinator.Parsers with Singleton] {
    type Parser[A] = P#Parser[A]
    object dummyParser extends combinator.Parsers
    def instance: Monad[Parser] = new Monad[Parser] {
      def pure[A](a: => A): Parser[A] = dummyParser.success(a).asInstanceOf[Parser[A]] // please look the other way!
      def bind[A, B](fa: Parser[A])(f: (A) => Parser[B]): Parser[B] = fa flatMap f
    }
  }

  // A few type gymnastics are required to target the path-dependent type
  //
  // The return type is Monad[p.type#Parser]
  //
  // This way seems to work without -Ydependent-method-types, yay!
  implicit def parserMonad[P <: combinator.Parsers with Singleton] = new ParsersW[P].instance
}

object Parser extends Parsers