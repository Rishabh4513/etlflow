package etlflow

import Schema._
import etlflow.etljobs.EtlJob
import etlflow.jobs.{Job3HttpSmtpSteps, Job4DBSteps}

sealed trait MyEtlJobName[+EJP <: EtlJobProps] extends EtlJobName[EJP]

object MyEtlJobName {
  case object EtlJob3 extends MyEtlJobName[EtlJob3Props] {
    def getActualProperties(job_properties: Map[String, String]): EtlJob3Props = EtlJob3Props()
    def etlJob(job_properties: Map[String, String]): EtlJob[EtlJob3Props] = Job3HttpSmtpSteps(getActualProperties(job_properties))
  }
  case object EtlJob4 extends MyEtlJobName[EtlJob4Props] {
    def getActualProperties(job_properties: Map[String, String]): EtlJob4Props = EtlJob4Props()
    def etlJob(job_properties: Map[String, String]): EtlJob[EtlJob4Props] = Job4DBSteps(getActualProperties(job_properties))
  }
}

