package etlflow.etlsteps

import etlflow.log.LoggerApi
import etlflow.schema.LoggingLevel
import etlflow.utils.ApplicationLogger
import etlflow.core.{CoreEnv, StepEnv}
import zio.{RIO, Task, ZIO}

trait EtlStep[IPSTATE,OPSTATE] extends ApplicationLogger { self =>

  val name: String
  val step_type: String = this.getClass.getSimpleName

  def process(input_state: =>IPSTATE): RIO[CoreEnv, OPSTATE]
  def getExecutionMetrics: Map[String,Map[String,String]] = Map()
  def getStepProperties(level:LoggingLevel = LoggingLevel.INFO): Map[String,String] = Map()

  final def execute(input_state: =>IPSTATE): ZIO[StepEnv, Throwable, OPSTATE] = {
    for {
      step_start_time <- Task.succeed(System.currentTimeMillis())
      _   <- LoggerApi.stepLogStart(step_start_time, self)
      op  <- process(input_state).tapError{ex =>
        LoggerApi.stepLogEnd(step_start_time,self,Some(ex))
      }
      _   <- LoggerApi.stepLogEnd(step_start_time,self)
    } yield op
  }
}