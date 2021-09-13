package dev.seoh.chat

import akka.actor.testkit.typed.Effect.Spawned
import akka.actor.testkit.typed.scaladsl.{BehaviorTestKit, ScalaTestWithActorTestKit}
import dev.seoh.chat.actor.Manager._
import dev.seoh.chat.actor._
import org.scalatest.wordspec.AnyWordSpecLike


class ManagerSpec extends ScalaTestWithActorTestKit with AnyWordSpecLike {

  "Manager actor" must {
    val mgr = BehaviorTestKit(Manager())

    "spawn a child user" in {
      val name = "nick"
      mgr.run(UserAdd(name))
      val expected = mgr.expectEffectType[Spawned[User.Command]]
      expected.childName should equal (s"User-$name")
    }

    "spawn a room with number" in {
      def join(name: String, seq: Int) = {

        mgr.run(UserAdd(name))
        mgr.expectEffectType[Spawned[User.Command]]

        val user = createTestProbe[User.Command](name)
        mgr.run(RoomCreate(user.ref))
        mgr.expectEffectType[Spawned[Room.Command]]

        user.expectMessage(User.RoomCreated(seq))
      }

      (1 to 10) map { i =>
        join(s"nick$i", i)
      }
    }

    "first user should be joined after created room" in {
      val name = "nick10"
      val user = createTestProbe[User.Command](name)

      mgr.run(RoomCreate(user.ref))
      mgr.expectEffectType[Spawned[Room.Command]]
      user.receiveMessages(2) should contain theSameElementsAs
        (Vector(User.RoomCreated(11), User.Joined(11)))
    }

    "user can send message" in {
      val sent = createTestProbe[User.Command]( "nick11")
      val recv = createTestProbe[User.Command]( "nick12")

      mgr.run(RoomCreate(sent.ref))
      sent.receiveMessages(2) should contain theSameElementsAs
        (Vector(User.RoomCreated(12), User.Joined(12)))

      val room = mgr.expectEffectType[Spawned[Room.Command]]
      room.ref ! Room.Join(recv.ref)
      recv.expectMessage(User.Joined(12))

      val m = Room.Message(sent.ref, "abc", System.currentTimeMillis())
      room.ref ! Room.PostMessage(m)
      recv.expectMessage(User.Broadcasted(m))
    }

    "other users received message" in {
      pending
    }

    "user leaves a room" in {
      pending
    }

    "room is fired when empty" in {
      pending
    }

  }
}
