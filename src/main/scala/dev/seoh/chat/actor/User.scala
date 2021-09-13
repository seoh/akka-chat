package dev.seoh.chat.actor

import akka.actor.typed.Behavior
import akka.actor.typed.scaladsl.Behaviors

object User {
  sealed trait Command
  case class RoomCreated(id: Long) extends Command
  case class Joined(id: Long) extends Command
  case class Broadcasted(message: Room.Message) extends Command

  def apply(nickname: String): Behavior[User.Command] = Behaviors.receive { (ctx, msg) =>
    msg match {
      case RoomCreated(id) =>
        ctx.log.info(s"User($nickname) opens Room($id)")
        Behaviors.same
      case Joined(id) =>
        ctx.log.info(s"User($nickname) joined a Room($id)")
        Behaviors.same
      case Broadcasted(m) =>
        ctx.log.info(s"User(${m.from.path.name}): ${m.message}\tat${java.time.Instant.ofEpochMilli(m.createdAt)}")
        Behaviors.same
    }
  }
}
