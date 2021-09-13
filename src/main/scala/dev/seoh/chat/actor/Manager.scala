package dev.seoh.chat.actor

import akka.actor.typed.scaladsl.{ActorContext, Behaviors}
import akka.actor.typed.{ActorRef, Behavior}

object Manager {
  sealed trait Command
  case class RoomCreate(reply: ActorRef[User.Command]) extends Command
  case class RoomEmpty() extends Command
  case class UserAdd(nick: String) extends Command

  def apply(): Behavior[Command] = Behaviors.setup(ctx => state(ctx, 0))

  def state(context: ActorContext[Command], seq: Long): Behavior[Command] =
    Behaviors.receive { (ctx, msg) =>
      msg match {
        case RoomCreate(host) =>
          val next = seq + 1
          val room = ctx.spawn(Room(next), s"Room-$next")
          room ! Room.Created()
          host ! User.RoomCreated(next)
          host ! User.Joined(next)
          state(ctx, next)


        case RoomEmpty() => Behaviors.same
        case UserAdd(nick) =>
          ctx.spawn(User(nick), s"User-$nick")
          Behaviors.same
      }
    }
}
