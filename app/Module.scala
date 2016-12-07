import scala.collection.mutable

import com.google.inject.{AbstractModule, Provides}
import domain.Id
import domain.wordcount.Model.WordCount
import domain.wordcount.{WordCountRepository, WordCountRepositoryImpl}
import services.{WordCountService, WordCountServiceImpl}

class Module extends AbstractModule {

  lazy val dataStore = mutable.Map.empty[Id[WordCount], WordCount]

  @Provides
  def getDataStore = dataStore

  def configure() = {

    bind(classOf[WordCountService])
      .to(classOf[WordCountServiceImpl])

    bind(classOf[WordCountRepository])
      .to(classOf[WordCountRepositoryImpl])
  }
}
