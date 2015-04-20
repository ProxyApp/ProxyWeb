package proxy.controllers

import org.scalacheck.Properties
import org.scalacheck.Prop._


object UsersContextSpec extends Properties("User context"){

  property("can return all users") = secure( )

}
