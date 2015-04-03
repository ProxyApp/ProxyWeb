package engine.domain

import com.sksamuel.scrimage.Image


case class Contact(id: UserId,
                   label: String,
                   image: Image,
                   channels: List[Channel]) extends Identifiable[UserId]
