package etlflow.jobtests.jobs

import etlflow.jobtests.MyEtlJobProps.EtlJob5Props
import etlflow.etljobs.EtlJob
import etlflow.etlsteps._

case class Job4GenericSteps(job_properties: EtlJob5Props) extends EtlJob[EtlJob5Props] {

  private def processData(ip: String): Unit = {
    logger.info("Processing Data")
    throw new RuntimeException("Exception in Step")
    ip.foreach(jr => logger.info(jr.toString))
  }

  private def step1 = GenericETLStep(
    name               = "ProcessData",
    transform_function = processData,
  )

  val job =
    for {
      _     <- step1.execute("Sample")
    } yield ()
}
