import javax.inject.Inject

import filter.LoggingFilter
import play.api.http.HttpFilters

class Filters @Inject()(
  log: LoggingFilter
) extends HttpFilters {

  val filters = Seq(log)
}
