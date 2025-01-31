package etlflow.task

import com.coralogix.zio.k8s.client.model.K8sNamespace
import com.coralogix.zio.k8s.model.batch.v1.{Job, JobSpec}
import com.coralogix.zio.k8s.model.core.v1.{Container, PodSpec, PodTemplateSpec}
import com.coralogix.zio.k8s.model.pkg.apis.meta.v1.ObjectMeta
import etlflow.k8s._
import zio.RIO

/** Submit a job to kubernetes cluster using give job configuration
  * @param name
  *   kubernetes job name
  * @param image
  *   docker image name
  * @param imagePullPolicy
  *   docker image pull policy, default to 'IfNotPresent'
  * @param podRestartPolicy
  *   pod restart policy, default to 'OnFailure'
  * @param command
  *   entrypoint array, default to docker image's ENTRYPOINT
  * @param namespace
  *   kubernetes cluster namespace, defaults to default namespace
  */
case class CreateKubeJobTask(
    name: String,
    image: String,
    imagePullPolicy: String = "IfNotPresent",
    podRestartPolicy: String = "OnFailure",
    command: Option[Vector[String]] = None,
    namespace: K8sNamespace = K8sNamespace.default
) extends EtlTask[K8sEnv, Job] {

  override protected def process: RIO[K8sEnv, Job] = {
    logger.info("#" * 50)
    logger.info(s"Creating K8S Job: $name")

    val metadata        = ObjectMeta(name = Some(name))
    val container       = Container(name = name, image = Some(image), imagePullPolicy = Some(imagePullPolicy), command = command)
    val podSpec         = PodSpec(containers = Some(Vector(container)), restartPolicy = Some(podRestartPolicy))
    val podTemplateSpec = PodTemplateSpec(metadata = Some(metadata), spec = Some(podSpec))

    val job = K8SApi.createJob(metadata, JobSpec(template = podTemplateSpec), namespace)
    logger.info(s"K8S Job submitted successfully: $name")
    logger.info("#" * 50)
    job
  }

  @SuppressWarnings(Array("org.wartremover.warts.ToString"))
  override def getTaskProperties: Map[String, String] = Map(
    "name"             -> name,
    "image"            -> image,
    "imagePullPolicy"  -> imagePullPolicy,
    "podRestartPolicy" -> podRestartPolicy,
    "command"          -> command.toString
  )
}
