package scalaz
package std

import scala.Either.{LeftProjection, RightProjection}
import scalaz.Isomorphism._
import scalaz.Tags.{First, Last}

sealed trait EitherInstances0 {
  implicit def eitherEqual[A, B](implicit A0: Equal[A], B0: Equal[B]): Equal[Either[A, B]] = new EitherEqual[A, B] {
    implicit def A = A0
    implicit def B = B0
  }

  implicit def eitherLeftEqual[A, X](implicit A0: Equal[A]): Equal[LeftProjection[A, X]] = new EitherLeftEqual[A, X] {
    implicit def A = A0
  }

  implicit def eitherRightEqual[X, A](implicit A0: Equal[A]): Equal[RightProjection[X, A]] = new EitherRightEqual[X, A] {
    implicit def A = A0
  }

  implicit def eitherFirstRightEqual[X, A](implicit A0: Equal[A]): Equal[RightProjection[X, A] @@ First] = First.subst(Equal[RightProjection[X, A]])

  implicit def eitherLastRightEqual[X, A](implicit A0: Equal[A]): Equal[RightProjection[X, A] @@ Last] = Last.subst(Equal[RightProjection[X, A]])

  implicit def eitherFirstLeftEqual[A, X](implicit A0: Equal[A]): Equal[LeftProjection[A, X] @@ First] = First.subst(Equal[LeftProjection[A, X]])

  implicit def eitherLastLeftEqual[A, X](implicit A0: Equal[A]): Equal[LeftProjection[A, X] @@ Last] = Last.subst(Equal[LeftProjection[A, X]])

  implicit def eitherFirstLeftSemigroup[A: Semigroup, X]: Semigroup[LeftProjection[A, X] @@ First] = new EitherFirstLeftSemigroup[A, X] {}

  implicit def eitherFirstRightSemigroup[X, A: Semigroup]: Semigroup[RightProjection[X, A] @@ First] = new EitherFirstRightSemigroup[X, A] {}

  implicit def eitherLastLeftSemigroup[A: Semigroup, X]: Semigroup[LeftProjection[A, X] @@ Last] = new EitherLastLeftSemigroup[A, X] {}

  implicit def eitherLastRightSemigroup[X, A: Semigroup]: Semigroup[RightProjection[X, A] @@ Last] = new EitherLastRightSemigroup[X, A] {}

  implicit def eitherLeftSemigroup[A, X](implicit SemigroupA: Semigroup[A], MonoidX: Monoid[X]): Semigroup[LeftProjection[A, X]] = new EitherLeftSemigroup[A, X] {
    implicit def A = SemigroupA
    implicit def X = MonoidX
  }

  implicit def eitherRightSemigroup[X, A](implicit MonoidX: Monoid[X], SemigroupA: Semigroup[A]): Semigroup[RightProjection[X, A]] = new EitherRightSemigroup[X, A] {
    implicit def X = MonoidX
    implicit def A = SemigroupA
  }

}

trait EitherInstances extends EitherInstances0 {
  implicit val eitherInstance = new Bitraverse[Either] {
    override def bimap[A, B, C, D](fab: Either[A, B])
                                  (f: A => C, g: B => D) = fab match {
      case Left(a)  => Left(f(a))
      case Right(b) => Right(g(b))
    }

    def bitraverseImpl[G[_] : Applicative, A, B, C, D](fab: Either[A, B])
                                                  (f: A => G[C], g: B => G[D]) = fab match {
      case Left(a)  => Applicative[G].map(f(a))(b => Left(b))
      case Right(b) => Applicative[G].map(g(b))(d => Right(d))
    }
  }

  /** Right biased monad */
  implicit def eitherMonad[L] = new Traverse[({type l[a] = Either[L, a]})#l] with Monad[({type l[a] = Either[L, a]})#l] with Cozip[({type l[a] = Either[L, a]})#l] {
    def bind[A, B](fa: Either[L, A])(f: A => Either[L, B]) = fa match {
      case Left(a)  => Left(a)
      case Right(b) => f(b)
    }

    def point[A](a: => A) = Right(a)

    def traverseImpl[G[_] : Applicative, A, B](fa: Either[L, A])(f: A => G[B]) = fa match {
      case Left(x)  => Applicative[G].point(Left(x))
      case Right(x) => Applicative[G].map(f(x))(Right(_))
    }

    override def foldRight[A, B](fa: Either[L, A], z: => B)(f: (A, => B) => B) = fa match {
      case Left(_)  => z
      case Right(a) => f(a, z)
    }

    def cozip[A, B](a: Either[L, A \/ B]) =
      a match {
        case Left(l) => -\/(Left(l))
        case Right(e)=> e match {
          case -\/(a) => -\/(Right(a))
          case \/-(b) => \/-(Right(b))
        }
      }
  }

  /** [[scala.Either.LeftProjection]] is isomorphic to [[scala.Either]], when the type parameter `E` is partially applied. */
  implicit def LeftProjectionEIso2[E]: ({type λ[α] = LeftProjection[E, α]})#λ <~> ({type λ[α] = Either[E, α]})#λ =
    LeftProjectionIso2.unlift1[E]

  /** [[scala.Either.LeftProjection]] is isomorphic to [[scala.Either]], when the type parameter `E` is partially applied. */
  implicit def FirstLeftProjectionEIso2[E]: ({type λ[α] = LeftProjection[E, α] @@ First})#λ <~> ({type λ[α] = Either[E, α]})#λ =
    FirstLeftProjectionIso2.unlift1[E]

  /** [[scala.Either.LeftProjection]] is isomorphic to [[scala.Either]], when the type parameter `E` is partially applied. */
  implicit def LastLeftProjectionEIso2[E]: ({type λ[α] = LeftProjection[E, α] @@ Last})#λ <~> ({type λ[α] = Either[E, α]})#λ =
    LastLeftProjectionIso2.unlift1[E]

  /** [[scala.Either.LeftProjection]] is isomorphic to [[scala.Either]] */
  implicit val LeftProjectionIso2: LeftProjection <~~> Either = new IsoBifunctorTemplate[LeftProjection, Either] {
    def to[A, B](fa: LeftProjection[A, B]) = fa.e
    def from[A, B](ga: Either[A, B]) = ga.left
  }

  /** [[scala.Either.LeftProjection]] is isomorphic to [[scala.Either]] */
  implicit val FirstLeftProjectionIso2: ({type λ[α, β]=LeftProjection[α, β] @@ First})#λ <~~> Either = new IsoBifunctorTemplate[({type λ[α, β]=LeftProjection[α, β] @@ First})#λ, Either] {
    def to[A, B](fa: LeftProjection[A, B] @@ First) = Tag.unwrap(fa).e
    def from[A, B](ga: Either[A, B]) = First(ga.left)
  }

  /** [[scala.Either.LeftProjection]] is isomorphic to [[scala.Either]] */
  implicit val LastLeftProjectionIso2:({type λ[α, β]=LeftProjection[α, β] @@ Last})#λ <~~> Either = new IsoBifunctorTemplate[({type λ[α, β]=LeftProjection[α, β] @@ Last})#λ, Either] {
    def to[A, B](fa: LeftProjection[A, B] @@ Last) = Tag.unwrap(fa).e
    def from[A, B](ga: Either[A, B]) = Last(ga.left)
  }

  /** [[scala.Either.RightProjection]] is isomorphic to [[scala.Either]], when the type parameter `A` is partially applied. */
  implicit def RightProjectionAIso2[A]: ({type λ[α] = RightProjection[α, A]})#λ <~> ({type λ[α] = Either[α, A]})#λ =
    RightProjectionIso2.unlift2[A]

  /** [[scala.Either.RightProjection]] is isomorphic to [[scala.Either]], when the type parameter `A` is partially applied. */
  implicit def FirstRightProjectionAIso2[A]: ({type λ[α] = RightProjection[α, A] @@ First})#λ <~> ({type λ[α] = Either[α, A]})#λ =
    FirstRightProjectionIso2.unlift2[A]

  /** [[scala.Either.RightProjection]] is isomorphic to [[scala.Either]], when the type parameter `A` is partially applied. */
  implicit def LastRightProjectionAIso2[A]: ({type λ[α] = RightProjection[α, A] @@ Last})#λ <~> ({type λ[α] = Either[α, A]})#λ =
    LastRightProjectionIso2.unlift2[A]

  /** [[scala.Either.RightProjection]] is isomorphic to [[scala.Either]] */
  implicit val RightProjectionIso2: RightProjection <~~> Either = new IsoBifunctorTemplate[RightProjection, Either] {
    def to[A, B](fa: RightProjection[A, B]) = fa.e
    def from[A, B](ga: Either[A, B]) = ga.right
  }

  /** [[scala.Either.RightProjection]] is isomorphic to [[scala.Either]] */
  implicit val FirstRightProjectionIso2: ({type λ[α, β]=RightProjection[α, β] @@ First})#λ <~~> Either = new IsoBifunctorTemplate[({type λ[α, β]=RightProjection[α, β] @@ First})#λ, Either] {
    def to[A, B](fa: RightProjection[A, B] @@ First) = Tag.unwrap(fa).e
    def from[A, B](ga: Either[A, B]) = First(ga.right)
  }

  /** [[scala.Either.RightProjection]] is isomorphic to [[scala.Either]] */
  implicit val LastRightProjectionIso2: ({type λ[α, β]=RightProjection[α, β] @@ Last})#λ <~~> Either = new IsoBifunctorTemplate[({type λ[α, β]=RightProjection[α, β] @@ Last})#λ, Either] {
    def to[A, B](fa: RightProjection[A, B] @@ Last) = Tag.unwrap(fa).e
    def from[A, B](ga: Either[A, B]) = Last(ga.right)
  }

  implicit val eitherLeftInstance = new IsomorphismBifunctor[LeftProjection, Either] {
    def iso = LeftProjectionIso2
    implicit def G: Bifunctor[Either] = eitherInstance
  }

  implicit val eitherFirstLeftInstance = new IsomorphismBifunctor[({type f[a, b]=LeftProjection[a,b] @@ First})#f, Either] {
    def iso = FirstLeftProjectionIso2
    implicit def G: Bifunctor[Either] = eitherInstance
  }

  implicit val eitherRightInstance = new IsomorphismBifunctor[RightProjection, Either] {
    def iso = RightProjectionIso2
    implicit def G: Bifunctor[Either] = eitherInstance
  }

  implicit def eitherRightLInstance[L] = new Monad[({type λ[α] = RightProjection[L, α]})#λ] {
    def point[A](a: => A) = Right(a).right
    def bind[A, B](fa: RightProjection[L, A])(f: A => RightProjection[L, B]) = fa.e match {
      case Left(a)  => Left(a).right
      case Right(b) => f(b)
    }
  }

  implicit def eitherFirstRightLInstance[L] = new Monad[({type λ[α] = RightProjection[L, α] @@ First})#λ] {
    def point[A](a: => A) = First(Right(a).right)
    def bind[A, B](fa: RightProjection[L, A] @@ First)(f: A => RightProjection[L, B] @@ First) = First(
      Tag.unwrap(fa).e match {
        case Left(a)  => Left(a).right
        case Right(b) => Tag.unwrap(f(b))
      }
    )
  }

  implicit def eitherLastRightLInstance[L] = new Monad[({type λ[α] = RightProjection[L, α] @@ Last})#λ] {
    def point[A](a: => A) = Last(Right(a).right)
    def bind[A, B](fa: RightProjection[L, A] @@ Last)(f: A => RightProjection[L, B] @@ Last) =
      Tag.unwrap(fa).e match {
        case Left(a)  => Last(Left(a).right)
        case Right(b) => f(b)
      }
  }

  implicit def eitherLeftRInstance[R] = new Monad[({type λ[α] = LeftProjection[α, R]})#λ] {
    def point[A](a: => A) = Left(a).left
    def bind[A, B](fa: LeftProjection[A, R])(f: A => LeftProjection[B, R]) = fa.e match {
      case Left(a)  => f(a)
      case Right(b) => Right(b).left
    }
  }

  implicit def eitherFirstLeftRInstance[R] = new Monad[({type λ[α] = LeftProjection[α, R] @@ First})#λ] {
    def point[A](a: => A) = First(Left(a).left)
    def bind[A, B](fa: LeftProjection[A, R] @@ First)(f: A => LeftProjection[B, R] @@ First) = First(
      Tag.unwrap(fa).e match {
        case Left(a)  => Tag.unwrap(f(a))
        case Right(b) => Right(b).left
      }
    )
  }

  implicit def eitherLastLeftRInstance[R] = new Monad[({type λ[α] = LeftProjection[α, R] @@ Last})#λ] {
    def point[A](a: => A) = Last(Left(a).left)
    def bind[A, B](fa: LeftProjection[A, R] @@ Last)(f: A => LeftProjection[B, R] @@ Last) = Last(
      Tag.unwrap(fa).e match {
        case Left(a)  => Tag.unwrap(f(a))
        case Right(b) => Right(b).left
      }
    )
  }

  implicit def eitherOrder[A, B](implicit OrderA: Order[A], OrderB: Order[B]): Order[Either[A, B]] = new EitherOrder[A, B] {
    implicit def A = OrderA
    implicit def B = OrderB
  }

  implicit def eitherLeftOrder[A, X](implicit OrderA: Order[A]): Order[LeftProjection[A, X]] = new EitherLeftOrder[A, X] {
    implicit def A = OrderA
  }

  implicit def eitherRightOrder[X, A](implicit OrderA: Order[A]): Order[RightProjection[X, A]] = new EitherRightOrder[X, A] {
    implicit def A = OrderA
  }

  implicit def eitherFirstLeftOrder[A, X](implicit OrderA: Order[A]): Order[LeftProjection[A, X] @@ First] = First.subst(Order[LeftProjection[A, X]])

  implicit def eitherFirstRightOrder[X, A](implicit OrderA: Order[A]): Order[RightProjection[X, A] @@ First] = First.subst(Order[RightProjection[X, A]])

  implicit def eitherLastLeftOrder[A, X](implicit OrderA: Order[A]): Order[LeftProjection[A, X] @@ Last] = Last.subst(Order[LeftProjection[A, X]])

  implicit def eitherLastRightOrder[X, A](implicit OrderA: Order[A]): Order[RightProjection[X, A] @@ Last] = Last.subst(Order[RightProjection[X, A]])

  implicit def eitherFirstLeftMonoid[A, X](implicit MonoidX: Monoid[X]): Monoid[LeftProjection[A, X] @@ First] = new EitherFirstLeftMonoid[A, X] {
    implicit def X = MonoidX
  }

  implicit def eitherFirstRightMonoid[X, A](implicit MonoidX: Monoid[X]): Monoid[RightProjection[X, A] @@ First] = new EitherFirstRightMonoid[X, A] {
    implicit def X = MonoidX
  }

  implicit def eitherLastLeftMonoid[A, X](implicit MonoidX: Monoid[X]): Monoid[LeftProjection[A, X] @@ Last] = new EitherLastLeftMonoid[A, X] {
    implicit def X = MonoidX
  }

  implicit def eitherLastRightMonoid[X, A](implicit MonoidX: Monoid[X]): Monoid[RightProjection[X, A] @@ Last] = new EitherLastRightMonoid[X, A] {
    implicit def X = MonoidX
  }

  implicit def eitherLeftMonoid[A, X](implicit MonoidA: Monoid[A], MonoidX: Monoid[X]): Monoid[LeftProjection[A, X]] = new EitherLeftMonoid[A, X] {
    implicit def A = MonoidA
    implicit def X = MonoidX
  }

  implicit def eitherRightMonoid[X, A](implicit MonoidX: Monoid[X], MonoidA: Monoid[A]): Monoid[RightProjection[X, A]] = new EitherRightMonoid[X, A] {
    implicit def X = MonoidX
    implicit def A = MonoidA
  }
}

object either extends EitherInstances


private trait EitherRightEqual[X, A] extends Equal[RightProjection[X, A]] {
  implicit def A: Equal[A]

  final override def equal(a1: RightProjection[X, A], a2: RightProjection[X, A]) = (a1.toOption, a2.toOption) match {
    case (Some(x), Some(y)) => A.equal(x, y)
    case (None, None)       => true
    case _                  => false
  }
}

private trait EitherLeftEqual[A, X] extends Equal[LeftProjection[A, X]] {
  implicit def A: Equal[A]

  final override def equal(a1: LeftProjection[A, X], a2: LeftProjection[A, X]) = (a1.toOption, a2.toOption) match {
    case (Some(x), Some(y)) => A.equal(x, y)
    case (None, None)       => true
    case _                  => false
  }
}

private trait EitherEqual[A, B] extends Equal[Either[A, B]] {
  implicit def A: Equal[A]
  implicit def B: Equal[B]

  final override def equal(f1: Either[A, B], f2: Either[A, B]) = (f1, f2) match {
    case (Left(a1), Left(a2))                      => A.equal(a1, a2)
    case (Right(b1), Right(b2))                    => B.equal(b1, b2)
    case (Right(_), Left(_)) | (Left(_), Right(_)) => false
  }
  override val equalIsNatural: Boolean = A.equalIsNatural && B.equalIsNatural
}

private trait EitherFirstLeftSemigroup[A, X] extends Semigroup[LeftProjection[A, X] @@ First] {
  def append(f1: LeftProjection[A, X] @@ First, f2: => LeftProjection[A, X] @@ First) = if (Tag.unwrap(f1).e.isLeft) f1 else f2
}

private trait EitherFirstRightSemigroup[X, A] extends Semigroup[RightProjection[X, A] @@ First] {
  def append(f1: RightProjection[X, A] @@ First, f2: => RightProjection[X, A] @@ First) = if (Tag.unwrap(f1).e.isRight) f1 else f2
}

private trait EitherLastLeftSemigroup[A, X] extends Semigroup[LeftProjection[A, X] @@ Last] {
  def append(f1: LeftProjection[A, X] @@ Last, f2: => LeftProjection[A, X] @@ Last) = if (Tag.unwrap(f1).e.isLeft) f1 else f2
}

private trait EitherLastRightSemigroup[X, A] extends Semigroup[RightProjection[X, A] @@ Last] {
  def append(f1: RightProjection[X, A] @@ Last, f2: => RightProjection[X, A] @@ Last) = if (Tag.unwrap(f1).e.isRight) f1 else f2
}

private trait EitherLeftSemigroup[A, X] extends Semigroup[LeftProjection[A, X]] {
  implicit def A: Semigroup[A]
  implicit def X: Monoid[X]

  def append(f1: LeftProjection[A, X], f2: => LeftProjection[A, X]) = (f1.toOption, f2.toOption) match {
    case (Some(x), Some(y)) => Left(Semigroup[A].append(x, y)).left
    case (None, Some(_))    => f2
    case (Some(_), None)    => f1
    case (None, None)       => Right(Monoid[X].zero).left
  }
}

private trait EitherRightSemigroup[X, A] extends Semigroup[RightProjection[X, A]] {
  implicit def X: Monoid[X]
  implicit def A: Semigroup[A]

  def append(f1: RightProjection[X, A], f2: => RightProjection[X, A]) = (f1.toOption, f2.toOption) match {
    case (Some(x), Some(y)) => Right(Semigroup[A].append(x, y)).right
    case (None, Some(_))    => f2
    case (Some(_), None)    => f1
    case (None, None)       => Left(Monoid[X].zero).right
  }
}


private trait EitherFirstLeftMonoid[A, X] extends Monoid[LeftProjection[A, X] @@ First] with EitherFirstLeftSemigroup[A, X] {
  implicit def X: Monoid[X]

  def zero = First(Right(Monoid[X].zero).left)
}

private trait EitherLastLeftMonoid[A, X] extends Monoid[LeftProjection[A, X] @@ Last] with EitherLastLeftSemigroup[A, X] {
  implicit def X: Monoid[X]

  def zero = Last(Right(Monoid[X].zero).left)
}

private trait EitherLeftMonoid[A, X] extends Monoid[LeftProjection[A, X]] with EitherLeftSemigroup[A, X] {
  implicit def X: Monoid[X]

  def zero: LeftProjection[A, X] = Right(Monoid[X].zero).left
}

private trait EitherFirstRightMonoid[X, A] extends Monoid[RightProjection[X, A] @@ First] with EitherFirstRightSemigroup[X, A] {
  implicit def X: Monoid[X]

  def zero = First(Left(Monoid[X].zero).right)
}

private trait EitherLastRightMonoid[X, A] extends Monoid[RightProjection[X, A] @@ Last] with EitherLastRightSemigroup[X, A] {
  implicit def X: Monoid[X]

  def zero = Last(Left(Monoid[X].zero).right)
}

private trait EitherRightMonoid[X, A] extends Monoid[RightProjection[X, A]] with EitherRightSemigroup[X, A] {
  implicit def X: Monoid[X]

  def zero: RightProjection[X, A] = Left(Monoid[X].zero).right
}

private trait EitherOrder[A, B] extends Order[Either[A, B]] with EitherEqual[A, B]{
  implicit def A: Order[A]
  implicit def B: Order[B]

  import Ordering._

  def order(f1: Either[A, B], f2: Either[A, B]) = (f1, f2) match {
    case (Left(x), Left(y))   => A.order(x, y)
    case (Right(x), Right(y)) => B.order(x, y)
    case (Left(_), Right(_))  => LT
    case (Right(_), Left(_))  => GT
  }
}

private trait EitherLeftOrder[A, X] extends Order[LeftProjection[A, X]] with EitherLeftEqual[A, X]{
  implicit def A: Order[A]

  import Ordering._

  def order(f1: LeftProjection[A, X], f2: LeftProjection[A, X]) = (f1.toOption, f2.toOption) match {
    case (Some(x), Some(y)) => A.order(x, y)
    case (None, Some(_))    => LT
    case (Some(_), None)    => GT
    case (None, None)       => EQ
  }
}

private trait EitherRightOrder[X, A] extends Order[RightProjection[X, A]] with EitherRightEqual[X, A]{
  implicit def A: Order[A]

  import Ordering._

  def order(f1: RightProjection[X, A], f2: RightProjection[X, A]) = (f1.toOption, f2.toOption) match {
    case (Some(x), Some(y)) => A.order(x, y)
    case (None, Some(_))    => LT
    case (Some(_), None)    => GT
    case (None, None)       => EQ
  }
}

