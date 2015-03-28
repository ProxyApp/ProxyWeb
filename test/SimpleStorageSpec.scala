import engine.io.{Filter, Search, Store}
import engine.io.memory.SimpleStorage
import org.scalacheck.Prop._
import org.scalacheck.Properties

object SimpleStorageSpec extends Properties("Simple Storage"){

  property("Can hold arbitrary objects") = forAll( (i: Int) =>{
    val s = SimpleStorage(List[Int]())
    val s1 = s.put(Store(i))
    s.get(Search(Filter(x => x == i))).isEmpty
  })

  property("Defaults to immutability") = forAll( (i: Int) =>{
    val s = SimpleStorage(List[Int]())
    val s1 = s.put(Store(i))
    val s2 = s.put(Store(i))

    val sm = SimpleStorage(scala.collection.mutable.Stack[Int]())
    val sm1 = sm.put(Store(i))
    val sm2 = sm.put(Store(i))

    s.c.isEmpty && sm.c.length == 0
  })

}
