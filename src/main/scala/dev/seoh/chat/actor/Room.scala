package dev.seoh.chat.actor

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, Behavior}

object Room {
  sealed trait Command

  case class Created() extends Command
  case class Join(user: ActorRef[User.Command]) extends Command

  case class PostMessage(msg: Message) extends Command

  case class Message(from: ActorRef[User.Command], message: String, createdAt: Long)

  def apply(number: Long): Behavior[Command] =
    state(number, List.empty, List.empty)

  private def state(number: Long, users: List[ActorRef[User.Command]], messages: List[Message]): Behavior[Command] =
    Behaviors.receive { (ctx, msg) =>
      msg match {
        case Created() =>
          ctx.log.info(s"Room($number) is created")
          Behaviors.same
        case Join(user) =>
          ctx.log.info(s"User($user) requests to join")
          user ! User.Joined(number)
          state(number, user :: users, messages)
        case PostMessage(m) =>
          ctx.log.debug(m.toString)
          ctx.log.debug(users.toString)
          val others = users.filter(_ != m.from)
          others.foreach(u => u ! User.Broadcasted(m))
          state(number, users, m :: messages)
      }
    }
}